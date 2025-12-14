package app.jot2.ui.pin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.jot2.JotApplication
import app.jot2.theme.Jot2Theme
import app.jot2.ui.settings.SettingsScreen

class PinActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as JotApplication
        val dao = app.database.jotDao()

        setContent {
            var showSettings by remember { mutableStateOf(false) }

            Jot2Theme {
                if (showSettings) {
                    SettingsScreen(
                        onBack = { showSettings = false }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.White
                    ) { innerPadding ->
                        val pinnedJot by dao.getPinnedJot().collectAsState(initial = null)

                        PinScreen(
                            pinnedJot = pinnedJot,
                            onSettingsClick = { showSettings = true },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}