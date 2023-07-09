package ru.lanik.kedditor.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.lanik.kedditor.R
import ru.lanik.kedditor.model.DropdownMenuModel
import ru.lanik.kedditor.model.MenuItemModel
import ru.lanik.kedditor.ui.helper.MenuItem
import ru.lanik.kedditor.ui.helper.StyledTopScreenBar
import ru.lanik.kedditor.ui.theme.KedditorCorners
import ru.lanik.kedditor.ui.theme.KedditorSize
import ru.lanik.kedditor.ui.theme.KedditorTheme
import ru.lanik.network.constants.DefaultPostSort
import ru.lanik.network.constants.DefaultPostSource
import ru.lanik.network.constants.DefaultSubredditSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
) {
    val viewState by viewModel.settingsViewState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .background(KedditorTheme.colors.primaryBackground),
    ) {
        StyledTopScreenBar(
            scrollBehavior = scrollBehavior,
            isLoading = false,
            showActionButton = false,
            navIcon = {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = KedditorTheme.colors.tintColor,
                )
            },
            onNavClick = { viewModel.onNavigateBack() },
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = R.string.settings_fragment_name).uppercase(),
                    color = KedditorTheme.colors.primaryText,
                    style = KedditorTheme.typography.body,
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(KedditorTheme.shapes.generalPadding),
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.settings_action_enable_dart_theme),
                color = KedditorTheme.colors.primaryText,
                style = KedditorTheme.typography.body,
            )

            Switch(
                checked = viewState.isDark,
                onCheckedChange = {
                    viewModel.onSettingsChange(
                        viewState.copy(
                            isDark = it,
                        ),
                    )
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = KedditorTheme.colors.tintColor,
                    uncheckedThumbColor = KedditorTheme.colors.secondaryText,
                ),
            )
        }

        Divider(
            modifier = Modifier.padding(start = KedditorTheme.shapes.generalPadding),
            thickness = 0.5.dp,
            color = KedditorTheme.colors.secondaryText.copy(
                alpha = 0.3f,
            ),
        )

        MenuItem(
            model = MenuItemModel(
                title = stringResource(id = R.string.settings_action_text_size),
                currentIndex = when (viewState.textSize) {
                    KedditorSize.Small -> 0
                    KedditorSize.Medium -> 1
                    KedditorSize.Big -> 2
                },
                dropdownMenuModel = DropdownMenuModel(
                    values = listOf(
                        stringResource(id = R.string.settings_option_size_small),
                        stringResource(id = R.string.settings_option_size_medium),
                        stringResource(id = R.string.settings_option_size_big),
                    ),
                ),
            ),
            onItemSelected = {
                val settingsNew = viewState.copy(
                    textSize = when (it) {
                        0 -> KedditorSize.Small
                        1 -> KedditorSize.Medium
                        2 -> KedditorSize.Big
                        else -> throw NotImplementedError("No valid value for this $it")
                    },
                )
                viewModel.onSettingsChange(settingsNew)
            },
        )

        MenuItem(
            model = MenuItemModel(
                title = stringResource(id = R.string.settings_action_padding_size),
                currentIndex = when (viewState.paddingSize) {
                    KedditorSize.Small -> 0
                    KedditorSize.Medium -> 1
                    KedditorSize.Big -> 2
                },
                dropdownMenuModel = DropdownMenuModel(
                    values = listOf(
                        stringResource(id = R.string.settings_option_size_small),
                        stringResource(id = R.string.settings_option_size_medium),
                        stringResource(id = R.string.settings_option_size_big),
                    ),
                ),
            ),
            onItemSelected = {
                val settingsNew = viewState.copy(
                    paddingSize = when (it) {
                        0 -> KedditorSize.Small
                        1 -> KedditorSize.Medium
                        2 -> KedditorSize.Big
                        else -> throw NotImplementedError("No valid value for this $it")
                    },
                )
                viewModel.onSettingsChange(settingsNew)
            },
        )

        MenuItem(
            model = MenuItemModel(
                title = stringResource(id = R.string.settings_action_cornel_style),
                currentIndex = when (viewState.cornerStyle) {
                    KedditorCorners.Rounded -> 0
                    KedditorCorners.Flat -> 1
                },
                dropdownMenuModel = DropdownMenuModel(
                    values = listOf(
                        stringResource(id = R.string.settings_option_shape_rounded),
                        stringResource(id = R.string.settings_option_shape_flat),
                    ),
                ),
            ),
            onItemSelected = {
                val settingsNew = viewState.copy(
                    cornerStyle = when (it) {
                        0 -> KedditorCorners.Rounded
                        1 -> KedditorCorners.Flat
                        else -> throw NotImplementedError("No valid value for this $it")
                    },
                )
                viewModel.onSettingsChange(settingsNew)
            },
        )

        MenuItem(
            model = MenuItemModel(
                title = stringResource(id = R.string.settings_action_default_post_source),
                currentIndex = when (viewState.defaultPostSource) {
                    DefaultPostSource.POPULAR -> 0
                    DefaultPostSource.ALL -> 1
                },
                dropdownMenuModel = DropdownMenuModel(
                    values = listOf(
                        stringResource(id = R.string.settings_option_post_source_popular),
                        stringResource(id = R.string.settings_option_post_source_all),
                    ),
                ),
            ),
            onItemSelected = {
                val settingsNew = viewState.copy(
                    defaultPostSource = when (it) {
                        0 -> DefaultPostSource.POPULAR
                        1 -> DefaultPostSource.ALL
                        else -> throw NotImplementedError("No valid value for this $it")
                    },
                )
                viewModel.onSettingsChange(settingsNew)
            },
        )

        MenuItem(
            model = MenuItemModel(
                title = stringResource(id = R.string.settings_action_default_post_sort),
                currentIndex = when (viewState.defaultPostSort) {
                    DefaultPostSort.HOT -> 0
                    DefaultPostSort.NEW -> 1
                    DefaultPostSort.TOP -> 2
                    DefaultPostSort.RISING -> 3
                },
                dropdownMenuModel = DropdownMenuModel(
                    values = listOf(
                        stringResource(id = R.string.settings_option_post_sort_hot),
                        stringResource(id = R.string.settings_option_post_sort_new),
                        stringResource(id = R.string.settings_option_post_sort_top),
                        stringResource(id = R.string.settings_option_post_sort_rising),
                    ),
                ),
            ),
            onItemSelected = {
                val settingsNew = viewState.copy(
                    defaultPostSort = when (it) {
                        0 -> DefaultPostSort.HOT
                        1 -> DefaultPostSort.NEW
                        2 -> DefaultPostSort.TOP
                        3 -> DefaultPostSort.RISING
                        else -> throw NotImplementedError("No valid value for this $it")
                    },
                )
                viewModel.onSettingsChange(settingsNew)
            },
        )

        MenuItem(
            model = MenuItemModel(
                title = stringResource(id = R.string.settings_action_default_subreddit_source),
                currentIndex = when (viewState.defaultSubredditSource) {
                    DefaultSubredditSource.DEFAULT -> 0
                    DefaultSubredditSource.POPULAR -> 1
                    DefaultSubredditSource.NEW -> 2
                },
                dropdownMenuModel = DropdownMenuModel(
                    values = listOf(
                        stringResource(id = R.string.settings_option_subreddit_source_default),
                        stringResource(id = R.string.settings_option_subreddit_source_popular),
                        stringResource(id = R.string.settings_option_subreddit_source_new),
                    ),
                ),
            ),
            onItemSelected = {
                val settingsNew = viewState.copy(
                    defaultSubredditSource = when (it) {
                        0 -> DefaultSubredditSource.DEFAULT
                        1 -> DefaultSubredditSource.POPULAR
                        2 -> DefaultSubredditSource.NEW
                        else -> throw NotImplementedError("No valid value for this $it")
                    },
                )
                viewModel.onSettingsChange(settingsNew)
            },
        )
    }
}