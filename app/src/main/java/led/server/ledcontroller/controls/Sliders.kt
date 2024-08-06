package led.server.ledcontroller.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import led.server.ledcontroller.backend.Settings
import led.server.ledcontroller.backend.Update
import led.server.ledcontroller.backend.param

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParamRangeSlider(initialMinValue: MutableFloatState, initialMaxValue: MutableFloatState, min: Float, max: Float, paramName1: String, paramName2: String, iconID: Int, unit : String = "") {

    var sliderPosition by remember { mutableStateOf(initialMinValue.floatValue..initialMaxValue.floatValue) }
    val settings = Settings(LocalContext.current)
    val coroutineScope = rememberCoroutineScope()

    Row( modifier = Modifier.padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            modifier = Modifier.size(40.dp),
            painter = painterResource(id = iconID),
            contentDescription = "paramIcon"
        )

        Spacer(Modifier.padding(horizontal = 10.dp))
        Column () {
            RangeSlider(
                value = sliderPosition,
                steps = 256,
                onValueChange = { range ->
                    sliderPosition = range;
                    initialMinValue.floatValue = range.start
                    initialMaxValue.floatValue = range.endInclusive
                },
                valueRange = min..max,
                onValueChangeFinished = {
                    coroutineScope.launch {
                        val URL = settings.getAccessToken(Settings.URL_KEY).stateIn(CoroutineScope(Dispatchers.IO)).value ?: ""
                        Update(param(paramName1, sliderPosition.start.toInt()), param(paramName2, sliderPosition.endInclusive.toInt()), URL = URL)
                    }

                },
                track = {
                        SliderPositions -> SliderDefaults.Track(sliderPositions = SliderPositions)
                }
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                Text(text = sliderPosition.start.toInt().toString() + unit)
                Text(text = sliderPosition.endInclusive.toInt().toString() + unit)
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParamSlider(initialValue: MutableFloatState, min: Float, max: Float, paramName: String, iconID: Int, unit : String = "") {
    var sliderPosition by remember { mutableFloatStateOf(initialValue.floatValue) }
    val settings = Settings(LocalContext.current)
    val coroutineScope = rememberCoroutineScope()
    Row(Modifier.padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(40.dp),
            painter = painterResource(id = iconID),
            contentDescription = "paramIcon"
        )
        Spacer(Modifier.padding(horizontal = 10.dp))
        Column(verticalArrangement = Arrangement.Center){
            Slider(
                value = sliderPosition,
                valueRange = min..max,
                onValueChange = {
                    sliderPosition = it
                    initialValue.floatValue = sliderPosition
                },
                onValueChangeFinished = {
                    coroutineScope.launch{
                        val URL = settings.getAccessToken(Settings.URL_KEY).stateIn(CoroutineScope(Dispatchers.IO)).value ?: ""
                        Update(param(paramName, sliderPosition.toInt()), URL = URL)
                    }

                },
                track = {
                        SliderPositions -> SliderDefaults.Track(sliderPositions = SliderPositions)
                }
            )
            Text(text = sliderPosition.toInt().toString() + unit)
        }
    }
}