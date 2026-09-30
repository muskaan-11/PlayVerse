package com.droids.playverse.data.repository

import com.droids.playverse.data.local.db.dao.CoinTransactionDao
import com.droids.playverse.data.local.db.dao.CoinWalletDao
import com.droids.playverse.data.local.db.entities.CoinTransactionEntity
import com.droids.playverse.data.local.db.entities.CoinWalletEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

interface CoinRepository {
    val balance: StateFlow<Int>
    suspend fun earn(amount: Int, source: String)
    suspend fun spend(amount: Int, source: String): Boolean

    /**
     * Cost in coins for a continue, given which continue attempt this is
     * within the current run (1 = first continue this session, 2 = second, ...).
     * Costs escalate: 30 -> 60 -> 100, then stay at 100 for any further continues.
     */
    fun getContinueCost(continueNumberInSession: Int): Int

    /** Small automatic coin trickle awarded whenever a game session ends, regardless of score. */
    suspend fun earnFromSession(gameId: String)

    /** Additional score-scaled bonus, on top of the session trickle, for games with a numeric score. */
    suspend fun earnFromScore(gameId: String, score: Int)
}

class CoinRepositoryImpl(
    private val walletDao: CoinWalletDao,
    private val transactionDao: CoinTransactionDao,
    externalScope: CoroutineScope
) : CoinRepository {

    private val mutex = Mutex()

    override fun getContinueCost(continueNumberInSession: Int): Int {
        val index = (continueNumberInSession - 1).coerceAtLeast(0)
        return if (index < CONTINUE_COST_SCHEDULE.size) {
            CONTINUE_COST_SCHEDULE[index]
        } else {
            CONTINUE_COST_SCHEDULE.last()
        }
    }

    override val balance: StateFlow<Int> = walletDao.getWallet()
        .map { it?.balance ?: 0 }
        .stateIn(externalScope, SharingStarted.Eagerly, 0)

    override suspend fun earn(amount: Int, source: String) {
        require(amount > 0) { "earn amount must be positive" }

        mutex.withLock {
            val current = walletDao.getWalletOnce()?.balance ?: 0
            val newBalance = current + amount
            val now = System.currentTimeMillis()

            walletDao.insertWallet(CoinWalletEntity(id = 0, balance = newBalance, lastUpdated = now))
            transactionDao.insertTransaction(
                CoinTransactionEntity(
                    amount = amount,
                    type = "EARN",
                    source = source,
                    balanceAfter = newBalance,
                    timestamp = now
                )
            )
        }
    }

    override suspend fun spend(amount: Int, source: String): Boolean {
        require(amount > 0) { "spend amount must be positive" }

        return mutex.withLock {
            val current = walletDao.getWalletOnce()?.balance ?: 0
            if (current < amount) return@withLock false

            val newBalance = current - amount
            val now = System.currentTimeMillis()

            walletDao.insertWallet(CoinWalletEntity(id = 0, balance = newBalance, lastUpdated = now))
            transactionDao.insertTransaction(
                CoinTransactionEntity(
                    amount = amount,
                    type = "SPEND",
                    source = source,
                    balanceAfter = newBalance,
                    timestamp = now
                )
            )
            true
        }
    }

    override suspend fun earnFromSession(gameId: String) {
        earn(Random.nextInt(SESSION_REWARD_MIN, SESSION_REWARD_MAX + 1), "session_$gameId")
    }

    override suspend fun earnFromScore(gameId: String, score: Int) {
        if (score <= 0) return

        val bonus = (score / SCORE_BONUS_DIVISOR).coerceIn(1, SCORE_BONUS_MAX)
        earn(bonus, "score_$gameId")
    }

    companion object {
        // 1st continue = 30, 2nd = 60, 3rd+ = 100
        private val CONTINUE_COST_SCHEDULE = listOf(30, 60, 100)

        private const val SESSION_REWARD_MIN = 2
        private const val SESSION_REWARD_MAX = 5
        private const val SCORE_BONUS_DIVISOR = 10
        private const val SCORE_BONUS_MAX = 25
    }
}