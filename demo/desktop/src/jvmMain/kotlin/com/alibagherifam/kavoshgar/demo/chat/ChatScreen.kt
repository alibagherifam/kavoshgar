package com.alibagherifam.kavoshgar.demo.chat

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alibagherifam.kavoshgar.demo.StringResources
import com.alibagherifam.kavoshgar.demo.theme.AppTheme

@Composable
fun ChatScreen(
    lobbyName: String,
    viewModel: ChatViewModel,
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
        content = { innerPadding ->
            MessageList(
                messages = uiState.messages,
                contentPadding = innerPadding
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
    )
}

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
                    imageVector = Icons.Default.ArrowBack,
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
            backgroundColor = when {
                message.isMine -> MaterialTheme.colors.primary
                else -> MaterialTheme.colors.secondary
            }
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
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.End
                )
                Text(
                    text = message.timestamp,
                    style = MaterialTheme.typography.overline
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
            .background(MaterialTheme.colors.background)
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
        textStyle = MaterialTheme.typography.body1,
        placeholder = {
            Text(StringResources.PLACEHOLDER_MESSAGE_INPUT)
        },
        singleLine = true,
        shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
        keyboardActions = KeyboardActions { onSendMessageClick() }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
fun KeyEvent.isEnterClick() = (key == Key.Enter) && (type == KeyEventType.KeyDown)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SendButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val contentColor = when {
        enabled -> MaterialTheme.colors.onPrimary
        else -> MaterialTheme.colors.onSurface.copy(alpha = 0.38f)
    }
    val containerColor = when {
        enabled -> MaterialTheme.colors.primary
        else -> MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
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
                imageVector = Icons.Default.Send,
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
            content = "سلام! برای امتحان فردا چیزی خوندی؟",
            timestamp = "11:23"
        ),
        Message(
            isMine = false,
            content = "سلام. هنوز شروع نکردم 😃 احتمالا راحت باشه.",
            timestamp = "11:23"
        ),
        Message(
            isMine = true,
            content = "منم چیزی نخوندم هنوز",
            timestamp = "11:24"
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
