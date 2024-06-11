package led.server.ledcontroller.ui

import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.ui.graphics.Color
import led.server.ledcontroller.R

data class LightingType(
    val name: String,
    val iconID: Int
)

val Functions = listOf(
    LightingType("Sine wave" , R.drawable.outline_function_24),
    LightingType("Solid color", R.drawable.outline_lightbulb_24),
    LightingType("Pulse", R.drawable.outline_vital_signs_24),
    LightingType("Fade", R.drawable.round_gradient_24),
    LightingType("Remote Control", R.drawable.outline_settings_remote_24),
    LightingType("Lightning", R.drawable.rounded_electric_bolt_24),
    LightingType("Sparkle", R.drawable.outline_auto_awesome_24),
    LightingType("Rainbow", R.drawable.outline_looks_24)
)

val ColorSaver = run {
    val redKey = "Red"
    val greenKey = "Green"
    val blueKey = "Blue"
    mapSaver(
        save = { mapOf(redKey to it.red, greenKey to it.green, blueKey to it.blue) },
        restore = {
            Color(
                red = it[redKey] as Float,
                green = it[greenKey] as Float,
                blue = it[blueKey] as Float
            )
        }
    )
}