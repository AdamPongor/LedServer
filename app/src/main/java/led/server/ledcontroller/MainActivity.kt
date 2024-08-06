package led.server.ledcontroller

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import led.server.ledcontroller.backend.Settings
import led.server.ledcontroller.backend.Settings.Companion.NUM_LEDS
import led.server.ledcontroller.backend.Settings.Companion.URL_KEY
import led.server.ledcontroller.backend.Update
import led.server.ledcontroller.backend.param
import led.server.ledcontroller.backend.parseGradient
import led.server.ledcontroller.controls.ColorPicker
import led.server.ledcontroller.controls.ParamRangeSlider
import led.server.ledcontroller.controls.ParamSlider
import led.server.ledcontroller.ui.ColorSelectDialog
import led.server.ledcontroller.ui.Functions
import led.server.ledcontroller.ui.SettingsScreen
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
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "mainscaffold",
                        enterTransition = { slideInHorizontally(initialOffsetX = {it}) },
                        exitTransition = { slideOutHorizontally() + fadeOut() },
                        popEnterTransition = { slideInHorizontally() + fadeIn(animationSpec = spring(stiffness = -Spring.StiffnessMediumLow)) },
                        popExitTransition = { slideOutHorizontally(targetOffsetX = {it}) }

                    ) {
                        composable("mainscaffold") { MainScaffold({ navController.navigate("settings") }) }
                        composable("settings") { SettingsScreen { navController.popBackStack() } }
                    }
                    //MainScaffold()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(onSettings: () -> Unit, modifier: Modifier = Modifier) {
    var lightMode by rememberSaveable { mutableIntStateOf(0) }

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
                navigationIcon = {
                    IconButton(modifier = Modifier.fillMaxHeight(), onClick = { dialogState.value = true}) {
                        Icon(
                            modifier = Modifier.fillMaxHeight(),
                            imageVector = Icons.Outlined.Menu,
                            contentDescription = "lighting type"
                        )
                    }
                },
                actions = {

                    IconButton(modifier = Modifier.fillMaxHeight(), onClick = onSettings) {
                        Icon(
                            modifier = Modifier.fillMaxHeight(),
                            imageVector = Icons.Default.Settings,
                            contentDescription = "lighting type"
                        )
                    }
                }
            )
        },
        bottomBar = {
            val coroutineScope = rememberCoroutineScope()
            val settings = Settings(LocalContext.current)
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                )
                {

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        IconButton(onClick = { dialogState.value = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Menu,
                                contentDescription = "lighting type"
                            )
                        }
                        Text("Effects")
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        IconButton(onClick = {

                            coroutineScope.launch {
                                val URL = settings.getAccessToken(URL_KEY).stateIn(CoroutineScope(Dispatchers.IO)).value ?: ""
                                Update(param("program", -1), URL = URL)
                            }
                        }){
                            Icon(painter = painterResource(id = R.drawable.baseline_power_settings_new_24), contentDescription = "Power")
                        }
                        Text("Power")
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.FavoriteBorder, contentDescription = "Add")
                        }
                        Text("Favorite")
                    }
                }
            }
        },
        /*floatingActionButton = {
            FloatingActionButton(onClick = {  }) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Add")
            }
        }*/
    ) { innerPadding ->

        val coroutineScope = rememberCoroutineScope()
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
        val settings = Settings(LocalContext.current)
        SingleSelectDialog("Lighting mode", Functions, lightMode, onSubmitButtonClick = { value ->
            lightMode = value
            coroutineScope.launch {
                val URL = settings.getAccessToken(URL_KEY).stateIn(CoroutineScope(Dispatchers.IO)).value ?: ""
                Update(param("program", lightMode), URL = URL)
            }

         })
    }
}
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

@Composable
fun Content(modifier: Modifier = Modifier, lightMode: Int){
    //https://github.com/skydoves/colorpicker-compose

    var color = Color.Black
    val controller = rememberColorPickerController()

    val settings = Settings(LocalContext.current)
    val res = rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when (lightMode){

            0, 1, 2 ->{
                SideEffect{
                    Log.d("xddd", color.toArgb().toHexString())
                }
                ColorPicker (
                    initialColor = color,
                    controller = controller,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        color = colorEnvelope.color // ARGB color value.
                        //val hexCode: String = colorEnvelope.hexCode
                        val c = colorEnvelope.hexCode.removePrefix("FF")
                        if (colorEnvelope.fromUser) {
                            coroutineScope.launch {
                                val URL = settings.getAccessToken(URL_KEY).stateIn(CoroutineScope(Dispatchers.IO)).value ?: ""
                                res.value = Update(param("color", c),URL = URL)
                            }
                        }
                    }
                )
            }
        }

        val amp0Value = rememberSaveable { mutableFloatStateOf(0f) }
        val amp1Value = rememberSaveable { mutableFloatStateOf(255f) }
        val waveValue = rememberSaveable { mutableFloatStateOf(5f) }
        val stepsValue = rememberSaveable { mutableFloatStateOf(20f) }
        val fadeIndex = rememberSaveable { mutableIntStateOf(0) }
        val chance = rememberSaveable { mutableFloatStateOf(20f) }
        val maxRands = rememberSaveable { mutableFloatStateOf(20f) }
        val randStepsMin = rememberSaveable { mutableFloatStateOf(20f) }
        val randStepsMax = rememberSaveable { mutableFloatStateOf(50f) }

        when(lightMode){
            0 -> WaveFunction(stepsValue, amp0Value, amp1Value, waveValue)
            1 -> SolidFunction(controller)
            2 -> PulseFunction(stepsValue, amp0Value, amp1Value)
            3 -> FadeFunction(stepsValue, fadeIndex, settings)
            6 -> SparkleFunction(chance, maxRands, randStepsMin, randStepsMax)
            else -> {}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LedControllerTheme {
        MainScaffold({})
    }
}

@Composable
fun WaveFunction(steps: MutableFloatState, amp0: MutableFloatState, amp1: MutableFloatState, wave: MutableFloatState){
    val settings = Settings(LocalContext.current)
    val coroutineScope = rememberCoroutineScope()
    val maxNum = remember { mutableStateOf(0) }
    LaunchedEffect(Unit){
            maxNum.value = settings.getAccessToken(NUM_LEDS).stateIn(CoroutineScope(Dispatchers.IO)).value ?: 0

    }
    Column {
        ParamSlider(steps, 0f, 300f, "steps", R.drawable.time)
        ParamRangeSlider(amp0, amp1, 0f, 255f, "amp0", "amp1", R.drawable.amplitude)
        ParamSlider(wave, 0f, (maxNum.value *2 ).toFloat(), "wave", R.drawable.wavelength)
    }
}

@Composable
fun PulseFunction(steps: MutableFloatState, amp0: MutableFloatState, amp1: MutableFloatState){
    Column {
        ParamSlider(steps, 0f, 300f, "steps", R.drawable.time)
        ParamRangeSlider(amp0, amp1, 0f, 255f, "amp0", "amp1", R.drawable.amplitude)
    }
}

@Composable
fun SolidFunction(controller: ColorPickerController){

    val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.White,
                        Color.Magenta, Color.Yellow, Color.Cyan, Color.Black,
                        Color(255, 100, 0), Color(80, 255, 0), Color(0, 255, 80), Color(128, 0, 255)
    )

    LazyVerticalGrid(
        modifier = Modifier.padding(10.dp),
        columns = GridCells.Adaptive(minSize = 80.dp),

    ){
        items(colors.size) { index ->
            Box(
                modifier = Modifier
                    .size(95.dp)
                    .padding(10.dp)
                    .background(color = colors[index], RoundedCornerShape(6.dp))
                    .clickable {
                        controller.selectByColor(colors[index], true)
                    },
            )
        }
    }
}

@Composable
fun SparkleFunction(chance: MutableFloatState, maxRands: MutableFloatState, randStepsMin: MutableFloatState, randStepsMax: MutableFloatState){

    var color = Color.Black
    val controller = rememberColorPickerController()

    val settings = Settings(LocalContext.current)
    val res = rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    ColorPicker (
        initialColor = color,
        controller = controller,
        onColorChanged = { colorEnvelope: ColorEnvelope ->
            color = colorEnvelope.color // ARGB color value.
            //val hexCode: String = colorEnvelope.hexCode
            val c = colorEnvelope.hexCode.removePrefix("FF")
            if (colorEnvelope.fromUser) {
                coroutineScope.launch {
                    val URL = settings.getAccessToken(URL_KEY).stateIn(CoroutineScope(Dispatchers.IO)).value ?: ""
                    res.value = Update(param("color", c),URL = URL)
                }
            }
        }
    )

    ParamSlider(chance, 0f, 100f, "chance", R.drawable.outline_auto_awesome_24, "%")
    ParamSlider(maxRands, 0f, 100f, "maxRands", R.drawable.dice_24, "%")
    ParamRangeSlider(randStepsMin, randStepsMax, 0f, 300f, "randStepsMin", "randStepsMax", R.drawable.time)

}

@Composable
fun FadeFunction(steps: MutableFloatState, fadeIndex: MutableIntState, settings: Settings){

    val colors = remember { mutableStateListOf( Color.Red, Color.Black) }
    val rainbow = mutableListOf(Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta)
    val selectedIndex = remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    Column{
        ParamSlider(initialValue = steps, min = 1f, max = 200f, paramName = "steps", iconID = R.drawable.time)
        Row(
            Modifier
                .fillMaxWidth()
                .selectable(
                    selected = (fadeIndex.intValue == 0),
                    onClick = {
                        fadeIndex.intValue = 0
                        coroutineScope.launch {
                            val URL =
                                settings
                                    .getAccessToken(URL_KEY)
                                    .stateIn(CoroutineScope(Dispatchers.IO)).value ?: ""
                            Update(param("gradient", parseGradient(rainbow)), URL = URL)
                        }
                    }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = (fadeIndex.intValue == 0),
                onClick = {
                    fadeIndex.intValue = 0
                    coroutineScope.launch {
                        val URL = settings.getAccessToken(URL_KEY).stateIn(CoroutineScope(Dispatchers.IO)).value ?: ""
                        Update(param("gradient", parseGradient(rainbow)), URL = URL)
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
                            val URL =
                                settings
                                    .getAccessToken(URL_KEY)
                                    .stateIn(CoroutineScope(Dispatchers.IO)).value ?: ""
                            Update(param("gradient", parseGradient(colors)), URL = URL)
                        }
                    }),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = (fadeIndex.intValue == 1),
                onClick = {
                    fadeIndex.intValue = 1
                    coroutineScope.launch {
                        val URL = settings.getAccessToken(URL_KEY).stateIn(CoroutineScope(Dispatchers.IO)).value ?: ""
                        Update(param("gradient", parseGradient(colors)), URL = URL)
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
                                    val URL = settings.getAccessToken(URL_KEY).stateIn(CoroutineScope(Dispatchers.IO)).value ?: ""
                                    Update(param("gradient", parseGradient(colors)), URL = URL)
                                }
                            }
                    }){
                        Icon(painter = painterResource(id = R.drawable.twotone_delete_outline_24), contentDescription = "Delete")
                    }
                }
                if(index == colors.size-1 && colors.size < 10){
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        shape = RoundedCornerShape(5.dp),
                        onClick = {
                            fadeIndex.intValue = 1
                            if(colors.size < 10){
                                colors.add(Color.White)
                                coroutineScope.launch {
                                    val URL = settings.getAccessToken(URL_KEY).stateIn(CoroutineScope(Dispatchers.IO)).value ?: ""
                                    Update(param("gradient", parseGradient(colors)), URL = URL)
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
                        val URL = settings.getAccessToken(URL_KEY).stateIn(CoroutineScope(Dispatchers.IO)).value ?: ""
                        Update(param("gradient", parseGradient(colors)), URL = URL)
                    }
                }
            )

        }
    }
}