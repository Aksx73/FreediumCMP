package com.absut.freediummobile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data class WebView(val url: String) : Screen
}



@Composable
@Preview
fun App() {
    val navController = rememberNavController()

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home
            ) {
                composable<Screen.Home> {
                    HomeScreen { url ->
                        navController.navigate(Screen.WebView(url))
                    }
                }
                composable<Screen.WebView> { backStackEntry ->
                    val route = backStackEntry.toRoute<Screen.WebView>()
                    WebViewScreen(url = route.url)
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onOpenButtonClick: (String) -> Unit) {
    var url by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Freedium",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.size(16.dp))

        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("Enter Medium URL") },
            placeholder = { Text("https://medium.com/...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            maxLines = 5,
            /*trailingIcon = {
                if (url.isNotEmpty()){
                    IconButton(onClick = { url = ""}) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear text"
                        )
                    }
                }
            }*/
        )

        Spacer(Modifier.size(24.dp))

        Button(
            onClick = {
                onOpenButtonClick(url)
            },
            shape = MaterialTheme.shapes.extraLarge,
            enabled = url.isNotBlank()
        ) {
            Text("Open in Freedium")
        }
    }
}

@Composable
fun WebViewScreen(url: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "WebView Screen",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.size(16.dp))

        Text(
            text = "URL: $url",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.size(24.dp))

        Text(
            text = "WebView implementation coming soon...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}