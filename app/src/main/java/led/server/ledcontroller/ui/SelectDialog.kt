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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import led.server.ledcontroller.Functions
import led.server.ledcontroller.LightingType

val dialogState by lazy { mutableStateOf(false) }

@Composable
fun SingleSelectDialog(title: String,
                       optionsList: List<LightingType>,
                       defaultSelected: Int,
                       submitButtonText: String = "OK",
                       cancelButtonText: String = "Cancel",
                       onSubmitButtonClick: (Int) -> Unit = {},
                       onDismissRequest: () -> Unit = { dialogState.value = false }) {

    val (selectedOption, onOptionSelected) = remember { mutableStateOf(optionsList[defaultSelected]) }

    if (dialogState.value){
        Dialog(onDismissRequest = { onDismissRequest.invoke()}) {
            Surface(modifier = Modifier.width(300.dp),
                shape = RoundedCornerShape(5.dp)
            ) {

                Column(modifier = Modifier.padding(10.dp)) {

                    Text(modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Medium, text = title, fontSize = 25.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(modifier = Modifier.fillMaxHeight(0.6f)) {
                        items(optionsList.size) { index ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (optionsList[index] == selectedOption),
                                        onClick = {
                                            onOptionSelected(optionsList[index])
                                        }),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (optionsList[index] == selectedOption),
                                    onClick = { onOptionSelected(optionsList[index]) }
                                )
                                Icon(
                                    painter = painterResource(id = optionsList[index].iconID),
                                    contentDescription = optionsList[index].name
                                )
                                Text(
                                    text = optionsList[index].name,
                                    style = MaterialTheme.typography.bodySmall.merge(),
                                    modifier = Modifier.padding(start = 16.dp)
                                )

                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row (modifier = Modifier.fillMaxWidth().height(50.dp).padding(horizontal = 10.dp).padding(bottom = 5.dp), horizontalArrangement = Arrangement.spacedBy(50.dp)){
                        FilledTonalButton(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onClick = {
                                onDismissRequest.invoke()
                            },
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            Text(text = cancelButtonText)
                        }

                        FilledTonalButton(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onClick = {
                                onSubmitButtonClick.invoke(optionsList.indexOf(selectedOption))
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
}

@Preview
@Composable
fun DialogPreview(){
    SingleSelectDialog("Lighting mode", Functions, 0, )
}