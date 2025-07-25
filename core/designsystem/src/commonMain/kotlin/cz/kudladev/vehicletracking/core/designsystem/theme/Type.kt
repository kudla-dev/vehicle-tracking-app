package cz.kudladev.vehicletracking.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import vehicletracking.core.designsystem.generated.resources.*


@Composable
fun displayFontFamily() = FontFamily(
    Font(resource = Res.font.Exo_Light,weight = FontWeight.Light),
    Font(resource = Res.font.Exo_Regular, weight = FontWeight.Normal),
    Font(resource = Res.font.Exo_Medium, weight = FontWeight.Medium),
    Font(resource = Res.font.Exo_SemiBold, weight = FontWeight.SemiBold),
    Font(resource = Res.font.Exo_Bold, weight = FontWeight.Bold),
)

@Composable
fun bodyFontFamily() = FontFamily(
    Font(resource = Res.font.NunitoSans)
)

// Default Material 3 typography values
val baseline = Typography()

@Composable
fun AppTypography() = Typography().run {

    val bodyFontFamily = bodyFontFamily()
    val displayFontFamily = displayFontFamily()
    copy(
        displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
        displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
        displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
        headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
        titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily),
        titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
        titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
        bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
        bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
        bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
        labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
        labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
        labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
    )
}

