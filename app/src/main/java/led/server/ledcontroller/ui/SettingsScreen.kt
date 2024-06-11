package led.server.ledcontroller.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import led.server.ledcontroller.backend.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit){
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Settings")
                },
                navigationIcon = {
                    IconButton(modifier = Modifier.fillMaxHeight(), onClick = onBack) {
                        Icon(
                            modifier = Modifier.fillMaxHeight(),
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "nav back"
                        )
                    }
                },
            )
        }
    ){ innerPadding ->

        val settings = Settings(LocalContext.current)
        val tokenText = settings.getAccessToken(Settings.URL_KEY).collectAsState(initial = "")
        val ledNumValue = remember { mutableStateOf(TextFieldValue(text = "0", selection = TextRange(1))) }
        val ipTokenValue = remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            ipTokenValue.value = settings.getAccessToken(Settings.URL_KEY).stateIn(CoroutineScope(Dispatchers.IO)).value ?: ""
            val intText = settings.getAccessToken(Settings.NUM_LEDS).stateIn(CoroutineScope(Dispatchers.IO)).value ?: 0
            ledNumValue.value = TextFieldValue(text = intText.toString(), TextRange(intText.toString().length))
        }

        Column(modifier = Modifier
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
        ){
            Card(
                modifier = Modifier.padding(1.dp),
                shape = RoundedCornerShape(2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ){
                Text(
                    text = "Networking",
                    modifier = Modifier
                        .padding(start = 16.dp, top = 10.dp),
                    textAlign = TextAlign.Center,
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    value = ipTokenValue.value,
                    onValueChange = {
                        ipTokenValue.value = it
                        CoroutineScope(Dispatchers.IO).launch {
                            settings.saveToken(Settings.URL_KEY, it)
                        }

                    },
                    label = { Text("Server IP address") },
                    visualTransformation = VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                )
            }
            Card(
                modifier = Modifier.padding(1.dp),
                shape = RoundedCornerShape(2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(
                    text = "LEDs",
                    modifier = Modifier
                        .padding(start = 16.dp, top = 10.dp),
                    textAlign = TextAlign.Center,
                )

                LaunchedEffect(ledNumValue.value){

                }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    value = ledNumValue.value,
                    onValueChange = {
                        try {
                            val intValue = if (it.text.isEmpty()){
                                0
                            } else {
                                it.text.toInt()
                            }

                            if (intValue >= 0){
                                ledNumValue.value = TextFieldValue(text = intValue.toString(), TextRange(intValue.toString().length))
                                CoroutineScope(Dispatchers.IO).launch {
                                    settings.saveToken(Settings.NUM_LEDS, intValue)
                                }
                            }

                        }
                        catch(e: Exception){

                        }
                    },
                    label = { Text("Number of LEDs") },
                    visualTransformation = VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    }
}


@Preview
@Composable
fun xdPreview(){
    SettingsScreen({})
}
