package app.jot2.ui.jot

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.jot2.data.Jot
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun JotScreen(
    jots: List<Jot>,
    onAddJot: (String) -> Unit,
    onDeleteJot: (Jot) -> Unit,
    onPinJot: (Jot) -> Unit,
    onUnpinJot: (Jot) -> Unit,
    onUpdateJot: (Jot, String) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var selectedJot by remember { mutableStateOf<Jot?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var editingJot by remember { mutableStateOf<Jot?>(null) }
    var editText by remember { mutableStateOf("") }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                focusManager.clearFocus()
            }
            .imePadding()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "2Jot",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onSettingsClick) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(jots, key = { it.id }) { jot ->
                key(jot.id, jot.isPinned) {
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            when (dismissValue) {
                                SwipeToDismissBoxValue.StartToEnd -> {
                                    if (jot.isPinned) onUnpinJot(jot) else onPinJot(jot)
                                    false
                                }
                                SwipeToDismissBoxValue.EndToStart -> {
                                    editingJot = jot
                                    editText = jot.text
                                    false
                                }
                                SwipeToDismissBoxValue.Settled -> false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val direction = dismissState.dismissDirection
                            val color = when (direction) {
                                SwipeToDismissBoxValue.StartToEnd -> Color(0xFF4CAF50)
                                SwipeToDismissBoxValue.EndToStart -> Color(0xFF2196F3)
                                else -> Color.Transparent
                            }
                            val icon = when (direction) {
                                SwipeToDismissBoxValue.StartToEnd -> if (jot.isPinned) "Unpin" else "Pin"
                                SwipeToDismissBoxValue.EndToStart -> "Edit"
                                else -> ""
                            }
                            val alignment = when (direction) {
                                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                else -> Alignment.CenterEnd
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color, RoundedCornerShape(16.dp))
                                    .padding(horizontal = 20.dp),
                                contentAlignment = alignment
                            ) {
                                Text(
                                    text = icon,
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        },
                        content = {
                            JotItem(
                                jot = jot,
                                onLongClick = {
                                    selectedJot = jot
                                    showMenu = true
                                },
                                onDoubleClick = {
                                    if (jot.isPinned) onUnpinJot(jot) else onPinJot(jot)
                                },
                                onClick = {
                                    focusManager.clearFocus()
                                }
                            )
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Jot something...") },
                shape = RoundedCornerShape(24.dp),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrect = true
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            FilledIconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onAddJot(inputText)
                        inputText = ""
                        focusManager.clearFocus()
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    }
                }
            ) {
                Text("+", fontSize = 24.sp)
            }
        }
    }

    if (showMenu && selectedJot != null) {
        AlertDialog(
            onDismissRequest = {
                showMenu = false
                selectedJot = null
            },
            title = { Text("Actions") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            selectedJot?.let { jot ->
                                if (jot.isPinned) onUnpinJot(jot) else onPinJot(jot)
                            }
                            showMenu = false
                            selectedJot = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (selectedJot?.isPinned == true) "Unpin" else "Pin",
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    TextButton(
                        onClick = {
                            editingJot = selectedJot
                            editText = selectedJot?.text ?: ""
                            showMenu = false
                            selectedJot = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Edit",
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    TextButton(
                        onClick = {
                            selectedJot?.let { jot ->
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, jot.text)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, null))
                            }
                            showMenu = false
                            selectedJot = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Share",
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    TextButton(
                        onClick = {
                            selectedJot?.let { onDeleteJot(it) }
                            showMenu = false
                            selectedJot = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Delete",
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = {
                    showMenu = false
                    selectedJot = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (editingJot != null) {
        AlertDialog(
            onDismissRequest = {
                editingJot = null
                editText = ""
            },
            title = { Text("Edit") },
            text = {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 6
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        editingJot?.let { jot ->
                            if (editText.isNotBlank()) {
                                onUpdateJot(jot, editText)
                            }
                        }
                        editingJot = null
                        editText = ""
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    editingJot = null
                    editText = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun JotItem(
    jot: Jot,
    onLongClick: () -> Unit,
    onDoubleClick: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                onDoubleClick = onDoubleClick
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = jot.text,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )
            if (jot.isPinned) {
                Text(
                    text = "📌",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}