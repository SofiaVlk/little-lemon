package com.example.littlelemon

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.littlelemon.ui.theme.LittleLemonTheme
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private val database by lazy {
        MenuDatabase.getDatabase(this)
    }


    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    database.menuDao().existMenuItems().value?.let {existMenuItems ->
                        if (!existMenuItems) {
                            MenuRepository.getMenuNetwork().forEach {
                                database.menuDao().saveMenuItem(
                                    MenuItem(
                                        id = it.id.toString(),
                                        title = it.title,
                                        description = it.description,
                                        price = it.price.toDouble(),
                                        image = it.image,
                                        category = it.category
                                    )
                                )
                            }
                        }
                    }
            }
        }

        setContent {
            val snackbarHostState = remember { SnackbarHostState() }

            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
            ) {
                LittleLemonTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MyApp()
                    }
                }
            }
        }
    }
}


@Composable
fun MyApp() {
    val navController = rememberNavController()
    val startDestination = if (userIsLoggedIn()) Home.route else Onboarding.route
    MyNavigation(navController = navController, startDestination = startDestination)

}

fun userIsLoggedIn(): Boolean {
    return false
}

object MenuRepository {

    private val client = HttpClient(Android) {
        expectSuccess = true
        engine {
            connectTimeout = 5000
            socketTimeout = 5000
        }
        install(ContentNegotiation) {
            json(contentType = ContentType.Any)
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("KTOR_CLIENT::", message)
                }
            }
        }
        defaultRequest {
            header("Content-Type", "application/json")

            // Content Type
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
        }

    }

    suspend fun getMenuNetwork(): List<MenuItemNetwork> {
        val response: MenuNetwork = client
            .get("https://raw.githubusercontent.com/Meta-Mobile-Developer-PC/Working-With-Data-API/main/menu.json")
            .body()
        return response.menu
    }
}