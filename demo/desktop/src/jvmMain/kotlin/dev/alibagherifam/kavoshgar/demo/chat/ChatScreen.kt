package dev.alibagherifam.kavoshgar.demo.chat

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.alibagherifam.kavoshgar.demo.StringResources
import dev.alibagherifam.kavoshgar.demo.theme.AppTheme

@Composable
fun ChatScreen(
    lobbyName: String,
    viewModel: MessengerViewModel,
    onCloseRequest: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    ChatContent(
        lobbyName,
        uiState,
        onSendMessageClick = viewModel::sendMessage,
        onMessageInputValueChange = viewModel::changeMessageInputValue,
        onBackPressed = onCloseRequest
    )
    if (uiState.isConnectionLost) {
        onCloseRequest()
    }
    if (uiState.isLookingForClient) {
        LookingForClientDialog(
            onDismissRequest = onCloseRequest
        )
    }
}

@Composable
fun ChatContent(
    lobbyName: String,
    uiState: ChatUiState,
    onSendMessageClick: () -> Unit,
    onMessageInputValueChange: (String) -> Unit,
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                title = lobbyName,
                onBackPressed = onBackPressed
            )
        },
        bottomBar = {
            MessageInputBar(
                inputValue = uiState.messageInputValue,
                enabled = !uiState.isLookingForClient,
                onSendMessageClick = onSendMessageClick,
                onMessageInputValueChange = onMessageInputValueChange
            )
        }
    ) { innerPadding ->
        MessageList(
            messages = uiState.messages,
            contentPadding = innerPadding
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    onBackPressed: () -> Unit
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = StringResources.CONTENT_DESC_BACK_BUTTON
                )
            }
        }
    )
}

@Composable
fun MessageList(
    messages: List<Message>,
    contentPadding: PaddingValues
) {
    LazyColumn(contentPadding = contentPadding) {
        items(messages) {
            MessageItem(it)
        }
    }
}

@Composable
fun MessageItem(message: Message) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp),
        horizontalAlignment = if (message.isMine) Alignment.End else Alignment.Start
    ) {
        Card(
            modifier = Modifier.widthIn(min = 80.dp, max = 300.dp),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    message.isMine -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.tertiary
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(
                    vertical = 4.dp,
                    horizontal = 8.dp
                )
            ) {
                Text(
                    modifier = Modifier.align(Alignment.End),
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.End
                )
                Text(
                    text = message.timestamp,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun MessageInputBar(
    inputValue: String,
    enabled: Boolean,
    onSendMessageClick: () -> Unit,
    onMessageInputValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MessageInputField(
            Modifier.weight(1f),
            inputValue,
            enabled,
            onSendMessageClick,
            onMessageInputValueChange
        )
        SendButton(
            enabled = enabled && inputValue.isNotBlank(),
            onClick = onSendMessageClick
        )
    }
}

@Composable
fun MessageInputField(
    modifier: Modifier,
    inputValue: String,
    enabled: Boolean,
    onSendMessageClick: () -> Unit,
    onMessageInputValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier.onPreviewKeyEvent { event ->
            if (event.isEnterClick()) {
                onSendMessageClick()
                true
            } else false
        },
        value = inputValue,
        enabled = enabled,
        onValueChange = onMessageInputValueChange,
        textStyle = MaterialTheme.typography.bodyLarge,
        placeholder = {
            Text(StringResources.PLACEHOLDER_MESSAGE_INPUT)
        },
        singleLine = true,
        shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
        keyboardActions = KeyboardActions { onSendMessageClick() }
    )
}

fun KeyEvent.isEnterClick() = (key == Key.Enter) && (type == KeyEventType.KeyDown)

@Composable
fun SendButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val contentColor = when {
        enabled -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    }
    val containerColor = when {
        enabled -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    }
    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
    val shape: Shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50))
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier.size(50.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.Send,
                contentDescription = StringResources.CONTENT_DESC_SEND_BUTTON
            )
        }
    }
}

@Preview
@Composable
fun ChatContentPreview() {
    val messages = listOf(
        Message(
            isMine = true,
            content = "سلام! برای امتحان فردا چیزی خوندی؟"
        ),
        Message(
            isMine = false,
            content = "سلام. هنوز شروع نکردم 😃 احتمالا راحت باشه."
        ),
        Message(
            isMine = true,
            content = "منم چیزی نخوندم هنوز"
        )
    )
    AppTheme {
        ChatContent(
            lobbyName = "محمد عطایی",
            uiState = ChatUiState(
                messages = messages,
                messageInputValue = "فردا سر کلاس میبی"
            ),
            onMessageInputValueChange = {},
            onSendMessageClick = {},
            onBackPressed = {}
        )
    }
}
