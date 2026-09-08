package com.novastats.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novastats.app.core.navigation.NovaTab
import com.novastats.app.core.theme.LocalNovaTheme

@Composable
fun NovaTabBar(
    tabs: List<NovaTab>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val theme = LocalNovaTheme.current

    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        modifier = Modifier.fillMaxWidth(),
        containerColor = theme.surface,
        contentColor = theme.primary,
        edgePadding = 0.dp,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(
                    tabPositions[selectedTab]
                ),
                color = theme.primary
            )
        }
    ) {
        tabs.forEach { tab ->
            Tab(
                selected = selectedTab == tab.index,
                onClick = { onTabSelected(tab.index) },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tab.icon,
                            fontSize = 16.sp
                        )
                        Text(
                            text = tab.label,
                            fontSize = 10.sp,
                            fontWeight = if (selectedTab == tab.index)
                                FontWeight.Bold
                            else
                                FontWeight.Normal,
                            color = if (selectedTab == tab.index)
                                theme.primary
                            else
                                theme.textSecondary
                        )
                    }
                }
            )
        }
    }
}