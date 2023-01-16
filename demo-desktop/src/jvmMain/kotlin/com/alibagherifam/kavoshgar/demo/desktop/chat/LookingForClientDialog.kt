package com.alibagherifam.kavoshgar.demo.desktop.chat

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alibagherifam.kavoshgar.demo.desktop.StringResources

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LookingForClientDialog(
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        text = {
            Column(
                modifier = Modifier.widthIn(min = 280.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = StringResources.MESSAGE_WAIT_FOR_OTHER_CLIENT,
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.h5
                )
                Spacer(Modifier.size(24.dp))
                CircularProgressIndicator()
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    modifier = Modifier.widthIn(min = 180.dp),
                    onClick = { onDismissRequest() }
                ) {
                    Text(StringResources.LABEL_DISMISS)
                }
            }
        }
    )
}
