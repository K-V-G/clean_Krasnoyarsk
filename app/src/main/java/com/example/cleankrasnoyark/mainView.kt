package com.example.cleankrasnoyark

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import com.example.cleankrasnoyark.airVisualService.AirVisualService
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun mainViewFunc(navHostController: NavHostController) {
    val robotoBold = FontFamily(
        Font(R.font.roboto_bold)
    )
    val (aqi, setAqi) = remember { mutableStateOf("") }
    val (statusState, setStatusState) = remember { mutableStateOf("Определяем...") }
    val (colorBox, setColorBox) = remember { mutableStateOf(Color.White) }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var location by remember { mutableStateOf<Location?>(null) }

    val (isSideMenuOpen, setSideMenuOpen) = remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Разрешение получено, запрашиваем местоположение
            fusedLocationClient.lastLocation.addOnSuccessListener { locationResult ->
                location = locationResult
                Log.d("Location", "Received location: $locationResult")
            }
        } else {
            Toast.makeText(context, "Пожалуйста, включите определение местоположения", Toast.LENGTH_LONG).show()

        }
    }

    val locationRequest = LocationRequest.create().apply {
        interval = 10000 // интервал обновления местоположения в миллисекундах
        fastestInterval = 5000 // наименьший интервал обновления местоположения в миллисекундах
        priority = 100 // приоритет обновления местоположения
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location1 in locationResult.locations) {
                // обработка полученного местоположения
                Log.d("Location", "Received location: $location1")
                // сохраняем полученное местоположение в переменную location
                location = location1
            }
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Разрешение уже предоставлено, запрашиваем местоположение
            fusedLocationClient.lastLocation.addOnSuccessListener { locationResult ->
                location = locationResult
                Log.d("Location", "Received location: $locationResult")
            }

            // Запрашиваем обновление местоположения
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } else {
            // Разрешение еще не предоставлено, запрашиваем его
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    if (location != null) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.airvisual.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(AirVisualService::class.java)

        LaunchedEffect(Unit) {
            val currentLocation = location
            if (currentLocation != null) {
                try {
                    val response = service.getNearestCity(
                        currentLocation.latitude ?: 0.0,
                        currentLocation.longitude ?: 0.0,
                        "ce4c0569-45ef-46d8-a5b2-abdbfb4d3164"
                    )
                    Log.d("Response", response.toString())
                    val aqius = response.data.current.pollution.aqius
                    setAqi(aqius.toString())
                    val status = searchStatus(aqius)
                    val color = searchColor(aqius)
                    setStatusState(status)
                    setColorBox(color)

                } catch (e: Exception) {
                    Log.e("ERROR", "Network error1: ${e.message}")
                }
            }
        }
    } else {
        if (!isLocationEnabled(context)) {
            Toast.makeText(context, "Пожалуйста, включите определение местоположения", Toast.LENGTH_LONG).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Box(
                modifier = Modifier
                    .weight(2f)
                    .background(
                        color = Color(0xffAF543F),
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row {
                    Box(modifier = Modifier
                        .weight(0.5f)
                        .fillMaxSize())
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(6f)
                            .padding(start = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        GreetingBasedOnTime()
                    }
                    Box(modifier = Modifier
                        .weight(0.5f)
                        .fillMaxSize())
                }
            }
            Box(modifier = Modifier
                .weight(0.5f)
                .fillMaxSize()) {
            }
            Row(
                modifier = Modifier
                    .weight(4f)
                    .fillMaxSize()
            ) {
                Box(modifier = Modifier
                    .weight(0.5f)
                    .fillMaxSize())
                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xffEAE9E9).copy(alpha = 0.57f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .weight(7f)
                        .fillMaxSize()
                ) {
                    Column {
                        Box(modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.dp),
                            contentAlignment = Alignment.Center) {
                            Column {
                                Text(
                                    text = "Индекс качества воздуха в реальном времени (AQI)",
                                    color = Color.Black,
                                    fontSize = 20.sp,
                                    fontFamily = robotoBold
                                )
                            }
                        }
                        Box(modifier = Modifier.weight(2f),
                            contentAlignment = Alignment.Center) {
                            Row  {
                                Box(modifier = Modifier
                                    .weight(0.3f)
                                    .fillMaxSize())
                                Box(modifier = Modifier
                                    .weight(7f)
                                    .fillMaxSize()) {
                                        Box(modifier = Modifier
                                            .fillMaxSize()
                                            .border(
                                                width = 2.dp,
                                                color = Color.Black,
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                            .background(
                                                color = colorBox,
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                            .clickable {
                                                setSideMenuOpen(true)
                                            }) {
                                            Row {
                                                Box(modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxSize(),
                                                    contentAlignment = Alignment.Center) {
                                                    Column{
                                                        Box(modifier = Modifier.weight(1f),
                                                            contentAlignment = Alignment.Center) {
                                                            Text(text = "AQI",
                                                                color = Color.Black,
                                                                fontSize = 17.sp,
                                                                fontFamily = robotoBold)
                                                        }
                                                        Box(modifier = Modifier.weight(1f),
                                                            contentAlignment = Alignment.TopCenter) {
                                                            Text(text = aqi,
                                                                color = Color.Black,
                                                                fontSize = 17.sp,
                                                                fontFamily = robotoBold)
                                                        }
                                                    }
                                                }
                                                Divider(
                                                    color = Color.Black,
                                                    modifier = Modifier
                                                        .padding(horizontal = 8.dp)
                                                        .fillMaxHeight()
                                                        .width(2.dp)
                                                )
                                                Box(modifier = Modifier
                                                    .weight(2f)
                                                    .fillMaxSize()) {
                                                        Column {
                                                            Box(modifier = Modifier.weight(1f),
                                                                contentAlignment = Alignment.Center) {
                                                                Text(text = "Статус",
                                                                    color = Color.Black,
                                                                    fontSize = 17.sp,
                                                                    fontFamily = robotoBold)
                                                            }
                                                            Box(modifier = Modifier.weight(1f),
                                                                contentAlignment = Alignment.TopCenter) {
                                                                Text(text = statusState,
                                                                    color = Color.Black,
                                                                    fontSize = 17.sp,
                                                                    fontFamily = robotoBold)
                                                            }
                                                        }
                                                }
                                            }

                                        }
                                }
                                Box(modifier = Modifier
                                    .weight(0.3f)
                                    .fillMaxSize())
                            }
                        }
                        Box(modifier = Modifier.weight(0.5f))
                        }
                }
                Box(modifier = Modifier
                    .weight(0.5f)
                    .fillMaxSize())
            }
                Box(modifier = Modifier
                    .weight(4f)
                    .fillMaxSize()) {
                }
        }
    }

    if (isSideMenuOpen) {
        SideMenu(isOpen = isSideMenuOpen,
            onClose = { setSideMenuOpen(false) }
        )
    }
}

private fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GreetingBasedOnTime() {
    val robotoRegular = FontFamily(
        Font(R.font.roboto_regular)
    )
    val currentTime = LocalTime.now()
    val greeting = when {
        currentTime.isAfter(LocalTime.of(0, 0)) && currentTime.isBefore(LocalTime.of(12, 0)) -> "Доброе утро"
        currentTime.isAfter(LocalTime.of(12, 0)) && currentTime.isBefore(LocalTime.of(18, 0)) -> "Добрый день"
        else -> "Добрый вечер"
    }

    Text(text = greeting,
        color = Color.White,
        fontSize = 25.sp,
        fontFamily = robotoRegular)
}

fun searchStatus(aqi: Int): String {
    var status = "Нууу, такое"

    when (aqi) {
        in 1..50 ->status = "Хорошо"
        in 51..100 ->status = "Удовлетворительно"
        in 101..150 -> status = "Опасно"
        in 151..200 -> status = "Нездоровый"
        in 201..300 -> status = "Очень нездоровый"
        in 301..500 -> status = "Очень опасный"
    }

    return status
}

fun searchColor(aqi: Int): Color {
    var colorBox = Color.White

    when (aqi) {
        in 1..50 -> colorBox = Color(0xFF67AF08)
        in 51..100 -> colorBox = Color(0xFFE8FC00)
        in 101..150 -> colorBox = Color(0xFFFFA800)
        in 151..200 -> colorBox = Color(0xFFFF0000)
        in 201..300 -> colorBox = Color(0xFF99004C)
        in 301..500 -> colorBox = Color(0xFF8C0000)
    }

    return colorBox
}

@Composable
fun SideMenu(isOpen: Boolean, onClose: () -> Unit) {
    val robotoRegular = FontFamily(
        Font(R.font.roboto_regular)
    )
    val robotoBold = FontFamily(
        Font(R.font.roboto_bold)
    )
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val menuWidth = (screenWidth * 0.85f).coerceAtMost(300.dp)

    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            add(SvgDecoder.Factory())
        }
        .build()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Row {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(menuWidth)
                    .background(Color(0xffF8F8F8))
            ) {
                Column (modifier = Modifier
                    .fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .padding(16.dp)
                            .clickable(onClick = onClose),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Button(
                            onClick = onClose,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF373D4C)
                            )
                        ) {
                            Text(text = "Закрыть", color = Color.White)
                        }
                    }
                    Box(modifier = Modifier
                        .weight(2f)
                        .padding(16.dp)) {
                        Text(text = "Индекс качества воздуха",
                        color = Color.Black,
                        fontSize = 25.sp,
                        fontFamily = robotoBold)
                    }
                    Image(
                        painter = rememberAsyncImagePainter(R.raw.tabl, imageLoader),
                        contentDescription = null,
                        modifier = Modifier.padding(16.dp)
                    )
                    Box(modifier = Modifier.weight(6f)) {
                        Column(
                            Modifier
                                .padding(16.dp)
                                .fillMaxSize()
                                .verticalScroll(
                                    rememberScrollState()
                                )) {
                            Text(
                                text = "Индекс качества воздуха (AQI) — это " +
                                        "числовой показатель регионального " +
                                        "качества наружного воздуха в вашем " +
                                        "районе.",
                                color = Color.Black,
                                fontSize = 15.sp,
                                fontFamily = robotoRegular
                            )
                            Text(
                                text = "AQI делится на шесть категорий, каждая из " +
                                        "которых имеет свой цвет. " +
                                        "AQI выше 100 означает, что качество воздуха опасно " +
                                        "для уязвимых групп.",
                                color = Color.Black,
                                fontSize = 15.sp,
                                fontFamily = robotoRegular
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = Color.Black, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            var text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color(0xff8BDD20))) {
                                    append("Зеленый ")
                                }
                                withStyle(style = SpanStyle(color = Color.Black)) {
                                    append("уровень опасносности характеризуется удовлетворительным " +
                                            "качеством воздуха, загрязнение воздуха практически не " +
                                            "представляет угрозы. Категория не имеет последствий " +
                                            "для здоровья." +
                                            "\n" +
                                            "Каждый человек может продолжать свои занятия.")
                                }
                            }
                            Text(text = text,
                                fontSize = 15.sp,
                                fontFamily = robotoRegular)

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = Color.Black, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color(0xFF8D9904))) {
                                    append("Желтый ")
                                }
                                withStyle(style = SpanStyle(color = Color.Black)) {
                                    append("уровень опасности характеризуется приемлемым " +
                                            "качеством воздуха. Тем не менее, некоторые " +
                                            "загрязнители могут вызвать умеренное беспокойство " +
                                            "по поводу здоровья ограниченного числа " +
                                            "гиперчувствительных людей. Например, люди, " +
                                            "чрезвычайно чувствительные к озону, могут " +
                                            "испытывать респираторные симптомы, такие как " +
                                            "незначительные затруднения дыхания." +
                                            "\n\n" +
                                            "Лишь очень немногим сверхчувствительным людям " +
                                            "рекомендуется сокращать времяпрепровождения на" +
                                            " свежем воздухе.")
                                }
                            }
                            Text(text = text,
                                fontSize = 15.sp,
                                fontFamily = robotoRegular)

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = Color.Black, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color(0xffFFA800))) {
                                    append("Оранжевый ")
                                }
                                withStyle(style = SpanStyle(color = Color.Black)) {
                                    append("уровень опасности является нездоровым для " +
                                            "чувствительных групп. Особо чувствительные люди к " +
                                            "загрязнениям могут чувствовать" +
                                            " негативное воздействие веществ в воздухе. " +
                                            "Для основной группы населения, загрязнение не " +
                                            "способно оказать значительного вреда на здоровье." +
                                            "\n\n" +
                                        "Детям, беременным женщинам и пожилым людям не " +
                                            "рекомендуется долго пребывать на свежем воздухе. " +
                                            "Также следует ограничить прогулки индивидам, которые " +
                                            "страдают сердечнососудистыми заболеваниями, астмой и " +
                                            "другими респираторными болезнями."
                                    )
                                }
                            }
                            Text(text = text,
                                fontSize = 15.sp,
                                fontFamily = robotoRegular)

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = Color.Black, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color(0xffFF0000))) {
                                    append("Красный ")
                                }
                                withStyle(style = SpanStyle(color = Color.Black)) {
                                    append("уровень опасности является нездоровым. Каждый человек " +
                                            "способен прочувствовать негативное влияние " +
                                            "загрязнения. Чувствительные слои общества могут" +
                                            " переживать различные осложнения." +
                                            "\n\n" +
                                        "Меры предосторожности аналогичны III уровню"
                                    )
                                }
                            }
                            Text(text = text,
                                fontSize = 15.sp,
                                fontFamily = robotoRegular)

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = Color.Black, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color(0xff99004C))) {
                                    append("Пурпурный ")
                                }
                                withStyle(style = SpanStyle(color = Color.Black)) {
                                    append("уровень опасности является очень нездоровым." +
                                            " Все население подвержено высокому риску развития " +
                                            "осложнений. Вводится чрезвычайное положение." +
                                            "\n\n" +
                                            "Всему населению рекомендуется проводить немного " +
                                            "времени на свежем воздухе, при наличии в специальных " +
                                            "респираторных масок."
                                    )
                                }
                            }
                            Text(text = text,
                                fontSize = 15.sp,
                                fontFamily = robotoRegular)

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = Color.Black, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color(0xff8C0000))) {
                                    append("Бордовый ")
                                }
                                withStyle(style = SpanStyle(color = Color.Black)) {
                                    append("уровень опасности является опасным." +
                                            " ВХарактеризуется катастрофическими рисками для " +
                                            "здоровья человека. Вследствие несоблюдения правил, " +
                                            "могут проявляться серьезные осложнения, благодаря " +
                                            "которым повышаются риски летального исхода." +
                                            "\n\n" +
                                            "Все населения должно избегать свежего воздуха " +
                                            "даже при кратковременном пребывании."
                                    )
                                }
                            }
                            Text(text = text,
                                fontSize = 15.sp,
                                fontFamily = robotoRegular)
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(onClick = onClose)
            )
        }
    }
}

