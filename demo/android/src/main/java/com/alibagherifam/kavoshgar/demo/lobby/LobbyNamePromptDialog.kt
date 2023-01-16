package com.alibagherifam.kavoshgar.demo.lobby

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alibagherifam.kavoshgar.Constants
import com.alibagherifam.kavoshgar.demo.R
import com.alibagherifam.kavoshgar.demo.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
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
                    text = stringResource(R.string.message_lobby_name_selection),
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
                    // TODO: Check here
                    if (inputValue.count() in 1..Constants.LOBBY_NAME_MAX_SIZE) {
                        onCreateButtonClick(inputValue)
                    }
                }
            ) {
                Text(stringResource(R.string.label_create_lobby))
            }
        },
        dismissButton = {
            TextButton(
                modifier = Modifier.width(100.dp),
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.label_dismiss))
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
