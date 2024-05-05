package led.server.ledcontroller.controls

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.setValue
import led.server.ledcontroller.Update
import led.server.ledcontroller.param

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParamRangeSlider(initialMinValue: MutableFloatState, initialMaxValue: MutableFloatState, min: Float, max: Float, paramName1: String, paramName2: String) {
    var sliderPosition by remember { mutableStateOf(initialMinValue.floatValue..initialMaxValue.floatValue) }
    Column {
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
                Update(param(paramName1, sliderPosition.start.toInt()), param(paramName2, sliderPosition.endInclusive.toInt()))
            },
            track = {
                SliderPositions -> SliderDefaults.Track(sliderPositions = SliderPositions)
            }
        )
        Text(text = sliderPosition.start.toInt().toString())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParamSlider(initialValue: MutableFloatState, min: Float, max: Float, paramName: String) {
    var sliderPosition by remember { mutableFloatStateOf(initialValue.floatValue) }
    Column {
        Slider(
            value = sliderPosition,
            valueRange = min..max,
            onValueChange = {
                sliderPosition = it
                initialValue.floatValue = sliderPosition
            },
            onValueChangeFinished = {
                Update(param(paramName, sliderPosition.toInt()))
            },
            track = {
                SliderPositions -> SliderDefaults.Track(sliderPositions = SliderPositions)
            }
        )
        Text(text = sliderPosition.toInt().toString())
    }
}