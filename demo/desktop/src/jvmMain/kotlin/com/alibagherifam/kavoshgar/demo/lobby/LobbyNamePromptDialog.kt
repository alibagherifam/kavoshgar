package com.alibagherifam.kavoshgar.demo.lobby

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alibagherifam.kavoshgar.demo.StringResources

@OptIn(ExperimentalMaterialApi::class)
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
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.h5
                )
                Spacer(Modifier.size(16.dp))
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { inputValue = it }
                )
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp, start = 16.dp),
            ) {
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
                Spacer(Modifier.size(8.dp))
                TextButton(
                    modifier = Modifier.width(100.dp),
                    onClick = onDismissRequest
                ) {
                    Text(StringResources.LABEL_DISMISS)
                }
            }
        }
    )
}
