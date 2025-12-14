package app.jot2.ui.jot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import app.jot2.JotApplication
import app.jot2.theme.Jot2Theme
import app.jot2.ui.settings.SettingsScreen

class JotActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as JotApplication
        val dao = app.database.jotDao()
        val settingsManager = app.settingsManager

        setContent {
            val colorIndex by settingsManager.colorIndex.collectAsState()
//            val pinColorIndex by settingsManager.pinColorIndex.collectAsState()
            var showSettings by remember { mutableStateOf(false) }

            Jot2Theme(colorIndex = colorIndex) {
                if (showSettings) {
                    SettingsScreen(
                        onBack = { showSettings = false }
                    )
                } else {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        val viewModel: JotViewModel = viewModel(
                            factory = JotViewModelFactory(dao)
                        )
                        val jots by viewModel.jots.collectAsState()

                        JotScreen(
                            jots = jots,
                            onAddJot = viewModel::addJot,
                            onDeleteJot = viewModel::deleteJot,
                            onPinJot = viewModel::pinJot,
                            onUnpinJot = viewModel::unpinJot,
                            onUpdateJot = viewModel::updateJot,
                            onSettingsClick = { showSettings = true },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}