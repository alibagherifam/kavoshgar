package dev.alibagherifam.kavoshgar.demo.lobby.ui

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.alibagherifam.kavoshgar.demo.lobby.model.isValidLobbyName
import dev.alibagherifam.kavoshgar.demo.theme.AppTheme
import kavoshgar_project.demo.generated.resources.Res
import kavoshgar_project.demo.generated.resources.create_lobby
import kavoshgar_project.demo.generated.resources.dismiss
import kavoshgar_project.demo.generated.resources.lobby_name
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun LobbyNamePromptDialog(
    onCreateLobbyClick: (String) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    val isValid by remember {
        derivedStateOf {
            isValidLobbyName(name)
        }
    }

    val onCreateClick = remember(onCreateLobbyClick) {
        { onCreateLobbyClick(name) }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        title = {
            Text(
                text = stringResource(Res.string.lobby_name),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.widthIn(min = 280.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = if (isValid) ImeAction.Done else ImeAction.None
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onCreateClick() }
                ),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = onCreateClick,
                modifier = Modifier.widthIn(min = 100.dp),
                enabled = isValid
            ) {
                Text(text = stringResource(Res.string.create_lobby))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.width(100.dp)
            ) {
                Text(text = stringResource(Res.string.dismiss))
            }
        }
    )
}

@Preview
@Composable
private fun LobbyNamePromptDialogPreview() {
    AppTheme {
        LobbyNamePromptDialog(
            onCreateLobbyClick = {},
            onDismissRequest = {}
        )
    }
}
