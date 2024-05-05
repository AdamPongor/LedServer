package led.server.ledcontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.launch
import led.server.ledcontroller.controls.ParamRangeSlider
import led.server.ledcontroller.controls.ParamSlider
import led.server.ledcontroller.ui.SingleSelectDialog
import led.server.ledcontroller.ui.dialogState
import led.server.ledcontroller.ui.theme.LedControllerTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LedControllerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScaffold()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(modifier: Modifier = Modifier) {
    var lightMode by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(stringResource(id = R.string.app_name))
                },
                actions = {
                    IconButton(modifier = Modifier.fillMaxHeight(), onClick = { dialogState.value = true},) {
                        Icon(
                            modifier = Modifier.fillMaxHeight(),
                            imageVector = Icons.Outlined.List,
                            contentDescription = "lighting type"
                        )
                    }
                }
            )
        },
        /*bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Bottom app bar",
                )
            }
        },*/
        floatingActionButton = {
            FloatingActionButton(onClick = {  }) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(text = Functions[lightMode].name, modifier = Modifier
                .padding(10.dp)
                .align(Alignment.CenterHorizontally), fontWeight = FontWeight.Medium, fontSize = 25.sp)
            Content(modifier.padding(innerPadding), lightMode)
        }
        SingleSelectDialog("Lighting mode", Functions, lightMode, onSubmitButtonClick = {value -> lightMode = value; Update(param("program", lightMode)) })
    }
}

@Composable
fun Content(modifier: Modifier = Modifier, lightMode: Int){
    //https://github.com/skydoves/colorpicker-compose
    val controller = rememberColorPickerController()

    controller.setWheelColor(Color.Black)
    controller.setWheelAlpha(0.5f)
    controller.setDebounceDuration(100)

    var c by remember {mutableStateOf("black xd")}
    var res by remember {mutableStateOf("black xd")}
    var color by remember { mutableStateOf(Color.Black) }

    val coroutineScope = rememberCoroutineScope()

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
            onColorChanged = { colorEnvelope: ColorEnvelope ->
                color = colorEnvelope.color // ARGB color value.
                //val hexCode: String = colorEnvelope.hexCode
                c = colorEnvelope.hexCode.removePrefix("FF")
                if (colorEnvelope.fromUser){
                    coroutineScope.launch {
                        res = Update(param("color", c))
                    }
                }
            }
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
            text = "# $c",
            modifier = Modifier
                .padding(10.dp)
                .padding(top = 0.dp),
            color = color
        )

        AlphaTile(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(6.dp)),
            controller = controller
        )

        val speedValue = remember { mutableFloatStateOf(0f) }
        val amp0Value = remember { mutableFloatStateOf(0f) }
        val amp1Value = remember { mutableFloatStateOf(255f) }
        val waveValue = remember { mutableFloatStateOf(0f) }

        when(lightMode){
            0 -> WaveFunction(speedValue, amp0Value, amp1Value, waveValue)
            1 -> {}
            2 -> PulseFunction(speedValue, amp0Value, amp1Value)
            else -> {}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LedControllerTheme {
        MainScaffold()
    }
}

@Composable
fun WaveFunction(speed: MutableFloatState, amp0: MutableFloatState, amp1: MutableFloatState, wave: MutableFloatState){
    Column {
        ParamSlider(speed, 0f, 300f, "speed")
        ParamRangeSlider(amp0, amp1, 0f, 255f, "amp0", "amp1")
        ParamSlider(wave, 0f, 2000f, "wave")
    }
}

@Composable
fun PulseFunction(speed: MutableFloatState, amp0: MutableFloatState, amp1: MutableFloatState){
    Column {
        ParamSlider(speed, 0f, 300f, "speed")
        ParamRangeSlider(amp0, amp1, 0f, 255f, "amp0", "amp1")
    }
}