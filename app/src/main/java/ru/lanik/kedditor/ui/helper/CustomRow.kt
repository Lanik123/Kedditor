package ru.lanik.kedditor.ui.helper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.lanik.kedditor.ui.theme.KedditorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyledTopScreenBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    containerColor: Color = KedditorTheme.colors.primaryBackground,
    isLoading: Boolean = false,
    showActionButton: Boolean = true,
    navIcon: @Composable () -> Unit = {},
    onNavClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
    titleContent: @Composable () -> Unit = {},
) {
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = { onNavClick() },
            ) {
                navIcon()
            }
        },
        title = {
            titleContent()
        },
        actions = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.size(48.dp),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = KedditorTheme.colors.tintColor,
                        modifier = Modifier.size(24.dp),
                    )
                } else if (showActionButton) {
                    IconButton(onClick = { onActionClick() }) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = null,
                            tint = KedditorTheme.colors.tintColor,
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = containerColor,
        ),
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}