package led.server.ledcontroller.backend

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

private val client = OkHttpClient()

//var URL = "192.168.50.74";

data class param (
    val name: String,
    val value: Any
)

fun Update(vararg p: param, URL: String) : String {

    //val URL = settings.getAccessToken.stateIn(CoroutineScope(Dispatchers.IO)).value

    val builder = StringBuilder()

    p.forEach { arg ->
        builder.append("${arg.name}=${arg.value}&")
    }

    var res = ""
    val request: Request = Request.Builder()
        .url("http://$URL/?${builder}")
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