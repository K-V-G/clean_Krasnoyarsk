package com.example.cleankrasnoyark

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import com.example.cleankrasnoyark.Navigations.NavigationsActions
import com.example.cleankrasnoyark.ui.theme.CleanKrasnoyarkTheme


@Composable
fun initialViewsFunc(navController: NavHostController) {
    val robotoRegular = FontFamily(
        Font(R.font.roboto_regular)
    )
    val bebasFontFamily = FontFamily(
        Font(R.font.bebas_neue_regular)
    )
    val ubuntuFontFamily = FontFamily(
        Font(R.font.ubuntu_regular)
    )
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            add(SvgDecoder.Factory())
        }
        .build()
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier
            .weight(4f)
            .fillMaxSize(), contentAlignment = Alignment.Center) {
            Image(
                painter = rememberAsyncImagePainter(R.raw.medved, imageLoader),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
        Box(
            modifier = Modifier
                .weight(6f)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .fillMaxSize()
                .background(Color(0xffAF543F)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Привет!",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontFamily = ubuntuFontFamily
                    )
                }
                Box(modifier = Modifier.weight(2f)) {
                    Text(
                        text = "Это приложение позволит вам узнать индекс качества воздуха в г. Красноярск",
                        color = Color.White,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = ubuntuFontFamily
                    )
                }
                Box(modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterHorizontally)) {
                        Button(onClick =
                        {
                            navController.navigate(route = NavigationsActions.Main.route)
                        },
                            modifier = Modifier
                                .width(205.dp)
                                .height(55.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .align(Alignment.Center),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF373D4C)
                            )
                            ){
                            Text(
                                text = "Начать",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontFamily = robotoRegular,
                                textAlign = TextAlign.Center
                            )
                        }
                }
                Box(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "POWERED BY KWG",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = bebasFontFamily,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 20.dp))
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CleanKrasnoyarkTheme {
        initialViewsFunc(navController = rememberNavController())
    }
}