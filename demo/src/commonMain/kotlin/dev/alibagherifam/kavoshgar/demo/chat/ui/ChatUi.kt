package dev.alibagherifam.kavoshgar.demo.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import dev.alibagherifam.kavoshgar.demo.chat.model.FakeMessageFactory
import dev.alibagherifam.kavoshgar.demo.chat.model.Message
import dev.alibagherifam.kavoshgar.demo.chat.presenter.ChatUiEvent
import dev.alibagherifam.kavoshgar.demo.chat.presenter.ChatUiEvent.MessageSend
import dev.alibagherifam.kavoshgar.demo.chat.presenter.ChatUiState
import dev.alibagherifam.kavoshgar.demo.theme.AppTheme
import kavoshgar_project.demo.generated.resources.Res
import kavoshgar_project.demo.generated.resources.back
import kavoshgar_project.demo.generated.resources.placeholder_message_input
import kavoshgar_project.demo.generated.resources.send
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ChatUi(
    lobbyName: String,
    uiState: ChatUiState,
    eventSink: (ChatUiEvent) -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.isConnectionLost) {
        LaunchedEffect(Unit) {
            onBackPress()
        }
    }

    if (uiState.isLookingForClient) {
        LookingForClientDialog(
            onDismissRequest = onBackPress
        )
    }

    var messageInputValue by remember {
        mutableStateOf("")
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            ChatTopBar(
                title = lobbyName,
                onBackPress = onBackPress
            )
        },
        bottomBar = {
            MessageInputBar(
                inputValue = messageInputValue,
                enabled = !uiState.isLookingForClient,
                onSendMessageClick = {
                    eventSink(MessageSend(messageInputValue))
                },
                onMessageInputValueChange = {
                    messageInputValue = it
                }
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
private fun ChatTopBar(
    title: String,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(text = title)
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackPress) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(Res.string.back)
                )
            }
        }
    )
}

@Composable
private fun MessageList(
    messages: List<Message>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(messages) {
            MessageItem(it)
        }
    }
}

@Composable
private fun MessageItem(
    message: Message,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
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
                    text = message.content,
                    modifier = Modifier.align(Alignment.End),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.End
                )

                val timestamp: String = message
                    .receiveInstant
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .time
                    .format(
                        LocalTime.Format {
                            hour(Padding.NONE)
                            char(':')
                            minute()
                        }
                    )

                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun MessageInputBar(
    inputValue: String,
    enabled: Boolean,
    onSendMessageClick: () -> Unit,
    onMessageInputValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MessageInputField(
            inputValue = inputValue,
            enabled = enabled,
            onSendMessageClick = onSendMessageClick,
            onMessageInputValueChange = onMessageInputValueChange,
            modifier = Modifier.weight(1f)
        )
        SendButton(
            enabled = enabled && inputValue.isNotBlank(),
            onClick = onSendMessageClick
        )
    }
}

@Composable
private fun MessageInputField(
    inputValue: String,
    enabled: Boolean,
    onSendMessageClick: () -> Unit,
    onMessageInputValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
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
            Text(text = stringResource(Res.string.placeholder_message_input))
        },
        singleLine = true,
        shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
        keyboardActions = KeyboardActions { onSendMessageClick() }
    )
}

private fun KeyEvent.isEnterClick() = (key == Key.Enter) && (type == KeyEventType.KeyDown)

@Composable
private fun SendButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
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
        modifier = modifier,
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
                contentDescription = stringResource(Res.string.send)
            )
        }
    }
}

@Preview
@Composable
private fun ChatUiPreview() {
    val fakeMessages = FakeMessageFactory.createList()

    AppTheme {
        ChatUi(
            lobbyName = "محمد عطایی",
            uiState = ChatUiState(messages = fakeMessages),
            eventSink = {},
            onBackPress = {}
        )
    }
}
