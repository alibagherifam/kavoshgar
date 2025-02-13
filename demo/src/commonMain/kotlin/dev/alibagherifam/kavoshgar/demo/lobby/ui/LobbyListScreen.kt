package dev.alibagherifam.kavoshgar.demo.lobby.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import dev.alibagherifam.kavoshgar.demo.chat.ui.ChatNavigationArgs
import dev.alibagherifam.kavoshgar.demo.lobby.model.FakeLobbyFactory
import dev.alibagherifam.kavoshgar.demo.lobby.model.Lobby
import dev.alibagherifam.kavoshgar.demo.lobby.presenter.LobbyListUiEvent
import dev.alibagherifam.kavoshgar.demo.lobby.presenter.LobbyListUiEvent.LobbySelection
import dev.alibagherifam.kavoshgar.demo.lobby.presenter.LobbyListUiState
import dev.alibagherifam.kavoshgar.demo.theme.AppTheme
import kavoshgar_project.demo.generated.resources.Res
import kavoshgar_project.demo.generated.resources.create_lobby
import kavoshgar_project.demo.generated.resources.ip_address
import kavoshgar_project.demo.generated.resources.join_lobby
import kavoshgar_project.demo.generated.resources.latency
import kavoshgar_project.demo.generated.resources.lobby_name
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun LobbyListUi(
    uiState: LobbyListUiState,
    eventSink: (LobbyListUiEvent) -> Unit,
    onChatPageRequest: (ChatNavigationArgs) -> Unit,
    modifier: Modifier = Modifier
) {
    var isLobbyPromptVisible by remember { mutableStateOf(false) }
    Scaffold(
        modifier = modifier,
        bottomBar = {
            LobbyListBottomBar(
                onCreateLobbyClick = {
                    isLobbyPromptVisible = true
                },
                onJoinLobbyClick = {
                    val selectedLobby = uiState.selectedLobby
                    if (selectedLobby != null) {
                        val args = ChatNavigationArgs(
                            isLobbyOwner = false,
                            lobbyName = selectedLobby.name,
                            lobbyAddress = selectedLobby.address
                        )
                        onChatPageRequest(args)
                    }
                }
            )
        }
    ) { innerPadding ->
        LobbyTable(
            lobbies = uiState.lobbies,
            selectedLobby = uiState.selectedLobby,
            onLobbySelectionChange = {
                eventSink(LobbySelection(it))
            },
            modifier = Modifier.padding(innerPadding)
        )
    }

    if (isLobbyPromptVisible) {
        LobbyNamePromptDialog(
            onCreateLobbyClick = { lobbyName ->
                val args = ChatNavigationArgs(
                    isLobbyOwner = true,
                    lobbyName = lobbyName
                )
                onChatPageRequest(args)
            },
            onDismissRequest = {
                isLobbyPromptVisible = false
            }
        )
    }
}

@Composable
private fun LobbyListBottomBar(
    onCreateLobbyClick: () -> Unit,
    onJoinLobbyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            modifier = Modifier.widthIn(min = 150.dp),
            onClick = onCreateLobbyClick
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )

            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            Text(text = stringResource(Res.string.create_lobby))
        }

        Button(
            modifier = Modifier.widthIn(min = 150.dp),
            onClick = onJoinLobbyClick
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )

            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            Text(text = stringResource(Res.string.join_lobby))
        }
    }
}

@Composable
private fun LobbyTable(
    lobbies: List<Lobby>,
    selectedLobby: Lobby?,
    onLobbySelectionChange: (Lobby) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        stickyHeader {
            LobbyListHeader()
        }

        items(lobbies) {
            LobbyRow(
                lobby = it,
                isSelected = selectedLobby?.name == it.name,
                onLobbySelectionChange = onLobbySelectionChange
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.onBackground)
            )
        }
    }
}

@Composable
private fun LobbyListHeader(
    modifier: Modifier = Modifier
) {
    val backgroundColor = MaterialTheme.colorScheme.primary
    val contentColor = contentColorFor(backgroundColor)
    val textStyle = MaterialTheme.typography.titleLarge
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        TableCell(
            text = stringResource(Res.string.lobby_name),
            weight = 4f,
            textColor = contentColor,
            textStyle = textStyle
        )
        TableCell(
            text = stringResource(Res.string.ip_address),
            weight = 3f,
            textColor = contentColor,
            textStyle = textStyle
        )
        TableCell(
            text = stringResource(Res.string.latency),
            weight = 1.6f,
            textColor = contentColor,
            textStyle = textStyle
        )
    }
}

@Composable
private fun LobbyRow(
    lobby: Lobby,
    isSelected: Boolean,
    onLobbySelectionChange: (Lobby) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }
    val contentColor = contentColorFor(backgroundColor)
    val textStyle = MaterialTheme.typography.titleMedium
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onLobbySelectionChange(lobby)
            }.background(backgroundColor)
    ) {
        TableCell(
            text = lobby.name,
            weight = 4f,
            textColor = contentColor,
            textStyle = textStyle
        )
        TableCell(
            text = lobby.addressName,
            weight = 3f,
            textColor = contentColor,
            textStyle = textStyle
        )
        TableCell(
            text = lobby.latency.toString(),
            weight = 1.6f,
            textColor = contentColor,
            textStyle = textStyle
        )
    }
}

@Composable
private fun RowScope.TableCell(
    text: String,
    weight: Float,
    textColor: Color,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier
            .weight(weight)
            .padding(12.dp),
        color = textColor,
        style = textStyle
    )
}

@Preview
@Composable
private fun LobbyListUiPreview() {
    val fakeLobbies = FakeLobbyFactory.createList()

    AppTheme {
        LobbyListUi(
            uiState = LobbyListUiState(
                lobbies = fakeLobbies,
                selectedLobby = fakeLobbies[1]
            ),
            eventSink = {},
            onChatPageRequest = {}
        )
    }
}
