package led.server.ledcontroller.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun ColorPicker(
    initialColor: Color = Color.White,
    onColorChanged: (ColorEnvelope) -> Unit,
    controller: ColorPickerController = rememberColorPickerController()
){
    controller.setWheelColor(Color.Black)
    controller.setWheelAlpha(0.5f)
    controller.setDebounceDuration(100)

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HsvColorPicker(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .padding(10.dp),
            controller = controller,
            onColorChanged = onColorChanged,
            initialColor = initialColor
        )
        BrightnessSlider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .padding(bottom = 0.dp)
                .height(30.dp),
            controller = controller,
            //wheelImageBitmap = ImageBitmap.imageResource(R.drawable.baseline_fingerprint_24)
        )

        Text(
            text = "#${controller.selectedColor.value.toArgb().toHexString(HexFormat.UpperCase).removePrefix("FF")}",
            modifier = Modifier
                .padding(10.dp)
                .padding(top = 0.dp),
            color = controller.selectedColor.value
        )

        AlphaTile(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(6.dp)),
            controller = controller
        )
    }
}