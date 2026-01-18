package com.absut.freediummobile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.multiplatform.webview.util.KLogSeverity
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
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
                        val cleanUrl = url
                            .removePrefix("https://")
                            .removePrefix("http://")
                        val freediumUrl = "https://freedium-mirror.cfd/$cleanUrl"
                        navController.navigate(Screen.WebView(freediumUrl))
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeScreen(onOpenButtonClick: (String) -> Unit = { }) {
    var url by rememberSaveable { mutableStateOf("b701c3716547") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Freedium",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Clear text"
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Clear text"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .safeContentPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Freedium: Your paywall breakthrough for Medium!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
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
                trailingIcon = {
                    if (url.isNotEmpty()) {
                        IconButton(onClick = { url = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear text"
                            )
                        }
                    }
                }
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
}

@Preview
@Composable
fun WebViewScreen(url: String = "") {
    //val initialUrl = "https://google.com"
    println("WebView is loading: $url")
    val webViewState = rememberWebViewState(url)
    webViewState.webSettings.apply {
        isJavaScriptEnabled = true
        logSeverity = KLogSeverity.Debug
        customUserAgentString =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1) AppleWebKit/625.20 (KHTML, like Gecko) Version/14.3.43 Safari/625.20"
        androidWebSettings.apply {
            isAlgorithmicDarkeningAllowed = true
            safeBrowsingEnabled = true
        }
    }
    val navigator = rememberWebViewNavigator()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
        ) {
            val loadingState = webViewState.loadingState
            if (loadingState is LoadingState.Loading) {
                LinearProgressIndicator(
                    progress = loadingState.progress,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            WebView(
                state = webViewState,
                modifier = Modifier.fillMaxSize(),
                navigator = navigator,
            )
        }
    }
}