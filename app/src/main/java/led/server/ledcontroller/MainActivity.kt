package led.server.ledcontroller

import android.os.Bundle
import android.os.Debug
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.launch
import led.server.ledcontroller.ui.theme.LedControllerTheme
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LedControllerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    //https://github.com/skydoves/colorpicker-compose
    val controller = rememberColorPickerController()

    controller.setWheelColor(Color.Black)
    controller.setWheelAlpha(0.5f)
    controller.setDebounceDuration(100)

    var c by remember {mutableStateOf("black xd")}
    var res by remember {mutableStateOf("black xd")}
    var color by remember { mutableStateOf(Color.Black) }


    val coroutineScope = rememberCoroutineScope()


    Column {

        Text(
            text = "Debug xd " + res,
            modifier = modifier
        )
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HsvColorPicker(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .padding(10.dp),
            controller = controller,
            onColorChanged = { colorEnvelope: ColorEnvelope ->
                color = colorEnvelope.color // ARGB color value.
                val hexCode: String = colorEnvelope.hexCode // Color hex code, which represents color value.
                //val fromUser: Boolean = colorEnvelope.fromUser // Represents this event is triggered by user or not.
                c = colorEnvelope.hexCode.removePrefix("FF")
                coroutineScope.launch {
                    res = Update(c)
                }
            }
        )
        BrightnessSlider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(35.dp),
            controller = controller,
            //wheelImageBitmap = ImageBitmap.imageResource(R.drawable.baseline_fingerprint_24)
        )

        Text(
            text = "# " + c,
            modifier = modifier,
            color = color
        )

        AlphaTile(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(6.dp)),
            controller = controller
        )
    }

}

private val client = OkHttpClient()

fun Update(rgb: String) : String {

    var res = ""
    val request: Request = Request.Builder()
        .url("http://192.168.50.74/?value=$rgb&")
        .build()

    client.newCall(request).enqueue(object: Callback {
        override fun onFailure(call: Call, e: java.io.IOException) {
            res = e.toString()
            Log.d("FASZ", "FASZ")
        }

        override fun onResponse(call: Call, response: Response) {
            res = response.body!!.string()
            Log.d("JÓ", "JÓ")
        }

    })
    return res
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LedControllerTheme {
        Greeting("Android")
    }
}