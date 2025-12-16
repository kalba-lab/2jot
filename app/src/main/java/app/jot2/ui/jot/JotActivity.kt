package app.jot2.ui.jot

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import app.jot2.JotApplication
import app.jot2.theme.Jot2Theme
import app.jot2.ui.settings.SettingsScreen
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.LaunchedEffect

class JotActivity : ComponentActivity() {

    private var viewModelRef: JotViewModel? = null

    private val exportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            viewModelRef?.let { vm ->
                val json = vm.exportToJson()
                contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(json.toByteArray())
                }
                Toast.makeText(this, "Exported successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val importLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            viewModelRef?.let { vm ->
                contentResolver.openInputStream(uri)?.use { stream ->
                    val json = stream.bufferedReader().readText()
                    val success = vm.importFromJson(json)
                    if (success) {
                        Toast.makeText(this, "Imported successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Import failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

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
                val viewModel: JotViewModel = viewModel(
                    factory = JotViewModelFactory(dao)
                )

                LaunchedEffect(viewModel) {
                    viewModelRef = viewModel
                }

                if (showSettings) {
                    SettingsScreen(
                        onBack = { showSettings = false },
                        onExport = {
                            val timestamp = LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                            exportLauncher.launch("2jot_backup_$timestamp.json")
                        },
                        onImport = {
                            importLauncher.launch(arrayOf("application/json"))
                        }
                    )
                } else {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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