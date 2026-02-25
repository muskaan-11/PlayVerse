package com.droids.playverse

import android.content.Intent
import androidx.annotation.ColorRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droids.playverse.model.GameItem


@Composable
fun GameCard(
    game: GameItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(175.dp)
            .height(215.dp)
            .border(
                width = 2.dp,
                color = Color(0xFF56B7FF),
                shape = RoundedCornerShape(40.dp)
            )
            .clip(RoundedCornerShape(40.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🔹 IMAGE (takes remaining space)
            Image(
                painter = painterResource(game.image),
                contentDescription = "img",
                modifier = Modifier
                    .weight(1f)              // ⭐ KEY FIX
                    .fillMaxWidth(),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (game.id == "tic_tac_toe") {
                ImageBackgroundButton(
                    text = "Tic Tac Toe",// your downloaded image
                    onClick = onClick
                )

            }
            else if(game.id == "coming"){
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(game.color),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = game.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else {
                // 🔹 BUTTON (auto height for full text)
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(game.color),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = game.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
@Composable
fun ImageBackgroundButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(280.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(50))
            .clickable { onClick() }
    ) {

        // 🔹 Background Image
        Image(
            painter = painterResource(R.drawable.tic_tac_btn),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 🔹 Text on top
        Text(
            text = text,
            modifier = Modifier.align(Alignment.Center),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
