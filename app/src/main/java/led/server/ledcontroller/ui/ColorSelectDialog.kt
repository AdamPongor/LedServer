package led.server.ledcontroller.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import led.server.ledcontroller.controls.ColorPicker

val colorDialogState by lazy { mutableStateOf(false) }

@Composable
fun ColorSelectDialog(
    initialColor: Color = Color.White,
    submitButtonText: String = "OK",
    cancelButtonText: String = "Cancel",
    onSubmitButtonClick: (Color) -> Unit = {},
    onDismissRequest: () -> Unit = { colorDialogState.value = false }
) {
    val controller = rememberColorPickerController()

    controller.setWheelColor(Color.Black)
    controller.setWheelAlpha(0.5f)
    controller.setDebounceDuration(100)

    var c by remember {mutableStateOf("black xd")}
    var color by remember { mutableStateOf(Color.Black) }

    Dialog(onDismissRequest = { onDismissRequest.invoke()}) {
        Surface(modifier = Modifier.width(300.dp),
            shape = RoundedCornerShape(5.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ColorPicker(
                    initialColor = initialColor,
                    onColorChanged = {
                            colorEnvelope: ColorEnvelope ->
                        color = colorEnvelope.color // ARGB color value.
                        //val hexCode: String = colorEnvelope.hexCode
                        c = colorEnvelope.hexCode.removePrefix("FF")
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row (modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 5.dp), horizontalArrangement = Arrangement.spacedBy(50.dp)){
                    FilledTonalButton(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = {
                            onDismissRequest.invoke()
                        },
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(text = cancelButtonText)
                    }

                    FilledTonalButton(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = {
                            onSubmitButtonClick.invoke(color)
                            onDismissRequest.invoke()
                        },
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(text = submitButtonText)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ColorPickerPreview(){
    ColorSelectDialog()
}