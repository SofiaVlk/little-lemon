package com.example.littlelemon

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlelemon.ui.theme.LittleLemonColor

@Preview
@Composable
    fun Profile (
    onButtonClick: (Destinations) -> Unit = {}
){
    // Recordar estados para los campos de texto
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }


    val context = LocalContext.current

    // Recuperar datos de SharedPreferences
    LaunchedEffect(Unit) {
        val userData = getUserData(context)
        firstName = userData.first
        lastName = userData.second
        email = userData.third
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Encabezado con la imagen
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        // Espacio entre los campos de texto
        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "Personal information",
            textAlign = TextAlign.Left,
            color = LittleLemonColor.charcoal,
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 48.dp)
        )
        // Espacio entre los campos de texto
        Spacer(modifier = Modifier.height(24.dp))

        // Campo de texto para el nombre
        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First name") },
            modifier = Modifier.fillMaxWidth(),
        )

        // Espacio entre los campos de texto
        Spacer(modifier = Modifier.height(24.dp))

        // Campo de texto para el apellido
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last name") },
            modifier = Modifier.fillMaxWidth(),
        )

        // Espacio entre los campos de texto
        Spacer(modifier = Modifier.height(24.dp))
        // Campo de texto para el correo electrónico
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
        )

        Column (
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom
        ) {
            val hostState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()


            SnackbarHost(hostState = hostState) { snackbarData ->
                Snackbar {
                    //Text(text = snackbarData)
                }
            }


            val context = LocalContext.current
            // Botón de registrarse
            Button(

                onClick = {
                    clearUserData(context)
                    onButtonClick(Onboarding)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LittleLemonColor.yellow,
                    contentColor = LittleLemonColor.charcoal
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Log out")
            }

        }

    }
}

fun clearUserData(context: Context) {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        clear()
        apply()
    }
}

fun getUserData(context: Context): Triple<String, String, String> {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val firstName = sharedPreferences.getString("first_name", "") ?: ""
    val lastName = sharedPreferences.getString("last_name", "") ?: ""
    val email = sharedPreferences.getString("email", "") ?: ""
    return Triple(firstName, lastName, email)
}