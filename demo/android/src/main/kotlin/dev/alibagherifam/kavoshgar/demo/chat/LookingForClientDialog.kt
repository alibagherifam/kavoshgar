package dev.alibagherifam.kavoshgar.demo.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alibagherifam.kavoshgar.demo.R
import dev.alibagherifam.kavoshgar.demo.theme.AppTheme

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
                    text = stringResource(R.string.message_wait_for_other_client),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.size(24.dp))
                CircularProgressIndicator()
            }
        },
        confirmButton = {
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
                    Text(stringResource(R.string.label_dismiss))
                }
            }
        }
    )
}

@Preview
@Composable
fun LookingForClientDialogPreview() {
    AppTheme {
        LookingForClientDialog {}
    }
}
