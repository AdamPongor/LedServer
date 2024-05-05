package led.server.ledcontroller

data class LightingType(
    val name: String,
    val iconID: Int
)

val Functions = listOf(
    LightingType("Sine wave" , R.drawable.outline_function_24),
    LightingType("Solid color", R.drawable.outline_lightbulb_24),
    LightingType("Pulse", R.drawable.outline_vital_signs_24),
    LightingType("Remote Control", R.drawable.outline_settings_remote_24),
    LightingType("Lightning", R.drawable.rounded_electric_bolt_24),
    LightingType("Sparkle", R.drawable.outline_auto_awesome_24),
    LightingType("Rainbow", R.drawable.outline_looks_24)
)