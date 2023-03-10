package dev.alibagherifam.kavoshgar.demo.lobby

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alibagherifam.kavoshgar.demo.StringResources
import dev.alibagherifam.kavoshgar.demo.theme.AppTheme
import dev.alibagherifam.kavoshgar.demo.lobby.isValidLobbyName

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun LobbyNamePromptDialog(
    onCreateButtonClick: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var inputValue by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column(
                modifier = Modifier.widthIn(min = 280.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.align(Alignment.End),
                    text = StringResources.MESSAGE_LOBBY_NAME_SELECTION,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.size(16.dp))
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { inputValue = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                modifier = Modifier.widthIn(min = 100.dp),
                onClick = {
                    if (isValidLobbyName(inputValue)) {
                        onCreateButtonClick(inputValue)
                    }
                }
            ) {
                Text(StringResources.LABEL_CREATE_LOBBY)
            }
        },
        dismissButton = {
            TextButton(
                modifier = Modifier.width(100.dp),
                onClick = onDismissRequest
            ) {
                Text(StringResources.LABEL_DISMISS)
            }
        }
    )
}

@Preview
@Composable
fun LobbyNamePromptDialogPreview() {
    AppTheme {
        LobbyNamePromptDialog({}, {})
    }
}
