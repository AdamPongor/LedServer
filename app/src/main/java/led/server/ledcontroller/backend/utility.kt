package led.server.ledcontroller.backend

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import okhttp3.internal.toHexString
import okhttp3.internal.trimSubstring

fun parseGradient(colors: MutableList<Color>): String{
    val builder = StringBuilder()

    builder.append("${colors.size}:")

    colors.forEach { c ->
        builder.append("${c.toArgb().toHexString().trimSubstring(2, 8).uppercase()},")
    }

    Log.d("xdd", builder.trimEnd(',').toString() )
    return builder.trimEnd(',').toString()
}