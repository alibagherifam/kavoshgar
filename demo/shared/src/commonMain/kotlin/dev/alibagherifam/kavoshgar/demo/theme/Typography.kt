package dev.alibagherifam.kavoshgar.demo.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kavoshgar_project.demo.shared.generated.resources.Res
import kavoshgar_project.demo.shared.generated.resources.iran_sans_bold
import kavoshgar_project.demo.shared.generated.resources.iran_sans_regular
import org.jetbrains.compose.resources.Font

@Composable
private fun getIranSansFamily() = FontFamily(
    Font(Res.font.iran_sans_regular),
    Font(Res.font.iran_sans_bold, FontWeight.Bold)
)

@Composable
internal fun getAppTypography() = Typography(
    displayLarge = TextStyle(
        fontFamily = getIranSansFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = getIranSansFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = getIranSansFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = getIranSansFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = getIranSansFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = getIranSansFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = getIranSansFamily(),
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = getIranSansFamily(),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = getIranSansFamily(),
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = getIranSansFamily(),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily = getIranSansFamily(),
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontFamily = getIranSansFamily(),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.sp
    ),
    labelMedium = TextStyle(
        fontFamily = getIranSansFamily(),
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = getIranSansFamily(),
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        letterSpacing = 0.sp
    )
)
