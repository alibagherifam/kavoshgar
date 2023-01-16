package com.alibagherifam.kavoshgar.demo.lobby

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.alibagherifam.kavoshgar.demo.StringResources
import com.alibagherifam.kavoshgar.demo.chat.ChatNavigationArgs
import com.alibagherifam.kavoshgar.demo.theme.AppTheme
import com.alibagherifam.kavoshgar.lobby.ServerInformation
import com.alibagherifam.kavoshgar.lobby.getRandomServerInformation

@Composable
fun LobbyListScreen(
    viewModel: LobbyListViewModel,
    onChatPageRequest: (ChatNavigationArgs) -> Unit
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    LobbyListContent(
        uiState,
        onLobbySelectionChange = viewModel::selectLobby,
        onJoinLobbyClick = {
            val selectedLobby = uiState.selectedServer
            if (selectedLobby != null) {
                val args = ChatNavigationArgs(
                    isLobbyOwner = false,
                    lobbyName = selectedLobby.name,
                    serverAddress = selectedLobby.address
                )
                onChatPageRequest(args)
            }
        },
        onCreateLobbyClick = { isDialogOpen = true }
    )
    if (isDialogOpen) {
        LobbyNamePromptDialog(
            onCreateButtonClick = { lobbyName ->
                val args = ChatNavigationArgs(
                    isLobbyOwner = true,
                    lobbyName = lobbyName
                )
                onChatPageRequest(args)
            },
            onDismissRequest = { isDialogOpen = false }
        )
    }
}

@Composable
fun LobbyListContent(
    uiState: LobbyListUiState,
    onLobbySelectionChange: (ServerInformation) -> Unit,
    onCreateLobbyClick: () -> Unit,
    onJoinLobbyClick: () -> Unit
) {
    Scaffold(
        content = { innerPadding ->
            LobbyTable(
                contentPadding = innerPadding,
                lobbies = uiState.servers,
                selectedLobby = uiState.selectedServer,
                onLobbySelectionChange
            )
        },
        bottomBar = {
            LobbyNavigationBar(
                onCreateLobbyClick = onCreateLobbyClick,
                onJoinLobbyClick = onJoinLobbyClick
            )
        }
    )
}

@Composable
fun LobbyNavigationBar(
    onCreateLobbyClick: () -> Unit,
    onJoinLobbyClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            modifier = Modifier.widthIn(min = 150.dp),
            onClick = onCreateLobbyClick
        ) {
            Text(StringResources.LABEL_CREATE_LOBBY)
        }
        Button(
            modifier = Modifier.widthIn(min = 150.dp),
            onClick = onJoinLobbyClick
        ) {
            Text(StringResources.LABEL_JOIN_LOBBY)
        }
    }
}

@Composable
fun LobbyTable(
    contentPadding: PaddingValues,
    lobbies: List<ServerInformation>,
    selectedLobby: ServerInformation?,
    onLobbySelectionChange: (ServerInformation) -> Unit
) {
    LazyColumn(contentPadding = contentPadding) {
        item { TableHeader() }
        items(lobbies) {
            TableRow(
                lobby = it,
                isSelected = selectedLobby?.name == it.name,
                onLobbySelectionChange
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colors.onBackground)
            )
        }
    }
}

@Composable
fun TableHeader() {
    val backgroundColor = MaterialTheme.colors.primary
    val contentColor = contentColorFor(backgroundColor)
    val textStyle = MaterialTheme.typography.h6
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        TableCell(
            text = StringResources.LABEL_LOBBY_NAME,
            weight = 4f,
            textColor = contentColor,
            textStyle = textStyle
        )
        TableCell(
            text = StringResources.LABEL_LOBBY_ADDRESS,
            weight = 3f,
            textColor = contentColor,
            textStyle = textStyle
        )
        TableCell(
            text = StringResources.LABEL_LOBBY_LATENCY,
            weight = 1.6f,
            textColor = contentColor,
            textStyle = textStyle
        )
    }
}

@Composable
fun TableRow(
    lobby: ServerInformation,
    isSelected: Boolean,
    onLobbySelectionChange: (ServerInformation) -> Unit
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colors.primaryVariant
        else -> Color.Transparent
    }
    val contentColor = contentColorFor(backgroundColor)
    val textStyle = MaterialTheme.typography.subtitle1
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLobbySelectionChange(lobby) }
            .background(backgroundColor)
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
fun RowScope.TableCell(
    text: String,
    weight: Float,
    textColor: Color,
    textStyle: TextStyle
) {
    Text(
        text = text,
        modifier = Modifier
            .weight(weight)
            .padding(12.dp),
        color = textColor,
        style = textStyle
    )
}

@Preview
@Composable
fun LobbyListContentPreview() {
    val servers = List(size = 5) { getRandomServerInformation() }
    AppTheme {
        LobbyListContent(
            LobbyListUiState(servers, selectedServer = servers[1]),
            onLobbySelectionChange = {},
            onCreateLobbyClick = {},
            onJoinLobbyClick = {}
        )
    }
}
