package led.server.ledcontroller

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import led.server.ledcontroller.ui.ColorSelectDialog
import led.server.ledcontroller.ui.SingleSelectDialog
import led.server.ledcontroller.ui.colorDialogState
import led.server.ledcontroller.ui.dialogState
import led.server.ledcontroller.ui.theme.LedControllerTheme
import okhttp3.internal.toHexString
import okhttp3.internal.trimSubstring


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
                    IconButton(modifier = Modifier.fillMaxHeight(), onClick = { dialogState.value = true}) {
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
            },

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
        val stepsValue = remember { mutableFloatStateOf(20f) }
        val fadeIndex = remember { mutableIntStateOf(0) }

        when(lightMode){
            0 -> WaveFunction(speedValue, amp0Value, amp1Value, waveValue)
            1 -> {}
            2 -> PulseFunction(speedValue, amp0Value, amp1Value)
            3 -> FadeFunction(stepsValue, fadeIndex)
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
        ParamSlider(speed, 0f, 300f, "speed", R.drawable.time)
        ParamRangeSlider(amp0, amp1, 0f, 255f, "amp0", "amp1", R.drawable.amplitude)
        ParamSlider(wave, 0f, 2000f, "wave", R.drawable.wavelength)
    }
}

@Composable
fun PulseFunction(speed: MutableFloatState, amp0: MutableFloatState, amp1: MutableFloatState){
    Column {
        ParamSlider(speed, 0f, 300f, "speed", R.drawable.time)
        ParamRangeSlider(amp0, amp1, 0f, 255f, "amp0", "amp1", R.drawable.amplitude)
    }
}

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun FadeFunction(steps: MutableFloatState, fadeIndex: MutableIntState){

    var colors = remember { mutableStateListOf( Color.Red, Color.Black) }
    val rainbow = mutableListOf(Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta)
    var selectedIndex = remember { mutableIntStateOf(0) }

    val coroutineScope = rememberCoroutineScope()

    Column(){
        ParamSlider(initialValue = steps, min = 1f, max = 200f, paramName = "steps", iconID = R.drawable.time)
        Row(
            Modifier
                .fillMaxWidth()
                .selectable(
                    selected = (fadeIndex.intValue == 0),
                    onClick = {
                        fadeIndex.intValue = 0
                        coroutineScope.launch {
                            Update(param("gradient", parseGradient(rainbow)))
                        }
                    }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = (fadeIndex.intValue == 0),
                onClick = {
                    fadeIndex.intValue = 0
                    coroutineScope.launch {
                        Update(param("gradient", parseGradient(rainbow)))
                    }
                }
            )
            Spacer(Modifier.padding(horizontal = 10.dp))
            Text(
                text = "Rainbow"
            )
            Image(
                modifier = Modifier
                    .height(40.dp)
                    .padding(horizontal = 10.dp)
                    .clip(shape = RoundedCornerShape(5.dp)),
                painter = painterResource(id = R.drawable.rainbow_gradient_fully_saturated),
                contentScale = ContentScale.FillWidth,
                contentDescription = "rainbow"
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .selectable(
                    selected = (fadeIndex.intValue == 1),
                    onClick = {
                        fadeIndex.intValue = 1
                        coroutineScope.launch {
                            Update(param("gradient", parseGradient(colors)))
                        }
                    }),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = (fadeIndex.intValue == 1),
                onClick = {
                    fadeIndex.intValue = 1
                    coroutineScope.launch {
                        Update(param("gradient", parseGradient(colors)))
                    }
                },
            )
            Spacer(Modifier.padding(horizontal = 10.dp))
            Text(
                text = "Custom"
            )
        }
        LazyColumn(modifier = Modifier.fillMaxHeight()) {
            items(colors.size) {index ->
                Row(Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Text(
                        text = "#" + colors[index].toArgb().toHexString().trimSubstring(2, 8).uppercase(),
                        color = colors[index]
                        )
                    Spacer(Modifier.padding(horizontal = 10.dp))
                    Box(modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(40.dp)
                        .clip(shape = RoundedCornerShape(5.dp))
                        .background(colors[index])
                        .clickable {
                            selectedIndex.intValue = index
                            colorDialogState.value = true
                        }
                    )
                    Spacer(Modifier.padding(horizontal = 10.dp))
                    FilledIconButton(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(5.dp),
                        enabled = (colors.size > 2),
                        onClick = {
                            if (colors.size > 2){
                                fadeIndex.intValue = 1
                                colors.removeAt(index)
                                coroutineScope.launch {
                                    Update(param("gradient", parseGradient(colors)))
                                }
                            }
                    }){
                        Icon(painter = painterResource(id = R.drawable.twotone_delete_outline_24), contentDescription = "Delete")
                    }
                }
                if(index == colors.size-1 && colors.size < 10){
                    Button(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        shape = RoundedCornerShape(5.dp),
                        onClick = {
                            fadeIndex.intValue = 1
                            if(colors.size < 10){
                                colors.add(Color.White)
                                coroutineScope.launch {
                                    Update(param("gradient", parseGradient(colors)))
                                }
                            }
                        }
                    ){
                        Icon(painter = painterResource(id = R.drawable.baseline_add_24), contentDescription = "Add")
                    }
                }
            }

        }

        if (colorDialogState.value){
            ColorSelectDialog(
                initialColor = colors[selectedIndex.intValue],
                onSubmitButtonClick = {
                    color ->
                    fadeIndex.intValue = 1
                    colors[selectedIndex.intValue] = color
                    coroutineScope.launch {
                        Update(param("gradient", parseGradient(colors)))
                    }
                }
            )

        }
    }
}

fun parseGradient(colors: MutableList<Color>): String{
    val builder = StringBuilder()

    builder.append("${colors.size}:")

    colors.forEach { c ->
        builder.append("${c.toArgb().toHexString().trimSubstring(2, 8).uppercase()},")
    }

    Log.d("xdd", builder.trimEnd(',').toString() )
    return builder.trimEnd(',').toString()
}