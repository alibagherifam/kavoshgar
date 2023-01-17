package com.alibagherifam.kavoshgar.demo.lobby

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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alibagherifam.kavoshgar.demo.R
import com.alibagherifam.kavoshgar.demo.chat.ChatNavigationArgs
import com.alibagherifam.kavoshgar.demo.theme.AppTheme

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
            val selectedLobby = uiState.selectedLobby
            if (selectedLobby != null) {
                val args = ChatNavigationArgs(
                    isLobbyOwner = false,
                    lobbyName = selectedLobby.name,
                    lobbyAddress = selectedLobby.address
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyListContent(
    uiState: LobbyListUiState,
    onLobbySelectionChange: (Lobby) -> Unit,
    onCreateLobbyClick: () -> Unit,
    onJoinLobbyClick: () -> Unit
) {
    Scaffold(
        bottomBar = {
            LobbyNavigationBar(
                onCreateLobbyClick = onCreateLobbyClick,
                onJoinLobbyClick = onJoinLobbyClick
            )
        }
    ) { innerPadding ->
        LobbyTable(
            contentPadding = innerPadding,
            lobbies = uiState.lobbies,
            selectedLobby = uiState.selectedLobby,
            onLobbySelectionChange
        )
    }
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
            Text(stringResource(R.string.label_create_lobby))
        }
        Button(
            modifier = Modifier.widthIn(min = 150.dp),
            onClick = onJoinLobbyClick
        ) {
            Text(stringResource(R.string.label_join_lobby))
        }
    }
}

@Composable
fun LobbyTable(
    contentPadding: PaddingValues,
    lobbies: List<Lobby>,
    selectedLobby: Lobby?,
    onLobbySelectionChange: (Lobby) -> Unit
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
                    .background(MaterialTheme.colorScheme.onBackground)
            )
        }
    }
}

@Composable
fun TableHeader() {
    val backgroundColor = MaterialTheme.colorScheme.primary
    val contentColor = contentColorFor(backgroundColor)
    val textStyle = MaterialTheme.typography.titleLarge
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        TableCell(
            text = stringResource(R.string.label_lobby_name),
            weight = 4f,
            textColor = contentColor,
            textStyle = textStyle
        )
        TableCell(
            text = stringResource(R.string.label_lobby_address),
            weight = 3f,
            textColor = contentColor,
            textStyle = textStyle
        )
        TableCell(
            text = stringResource(R.string.label_lobby_latency),
            weight = 1.6f,
            textColor = contentColor,
            textStyle = textStyle
        )
    }
}

@Composable
fun TableRow(
    lobby: Lobby,
    isSelected: Boolean,
    onLobbySelectionChange: (Lobby) -> Unit
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }
    val contentColor = contentColorFor(backgroundColor)
    val textStyle = MaterialTheme.typography.titleMedium
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
    val lobbies = List(size = 5) { getRandomLobbies() }
    AppTheme {
        LobbyListContent(
            LobbyListUiState(lobbies, selectedLobby = lobbies[1]),
            onLobbySelectionChange = {},
            onCreateLobbyClick = {},
            onJoinLobbyClick = {}
        )
    }
}
