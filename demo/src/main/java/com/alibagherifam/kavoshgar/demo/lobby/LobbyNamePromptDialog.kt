package com.alibagherifam.kavoshgar.demo.lobby

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alibagherifam.kavoshgar.Constants
import com.alibagherifam.kavoshgar.demo.R

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
                        when {
                            // TODO: Check here
                            inputValue.isBlank() -> {}
                            inputValue.toByteArray().size > Constants.LOBBY_NAME_MAX_SIZE -> {
                            }
                            else -> {
                                onCreateButtonClick(inputValue)
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.label_create_lobby))
                }
                Spacer(Modifier.size(8.dp))
                TextButton(
                    modifier = Modifier.width(100.dp),
                    onClick = onDismissRequest
                ) {
                    Text(stringResource(R.string.label_dismiss))
                }
            }
        }
    )
}
