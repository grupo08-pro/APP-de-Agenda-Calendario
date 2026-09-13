package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import com.example.data.model.Appointment
import com.example.data.model.ServiceItem
import com.example.data.model.WorkingHours
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.MoreSubTab

@Composable
fun MoreScreen(
    currentSubTab: MoreSubTab,
    onSelectSubTab: (MoreSubTab) -> Unit,
    allAppointments: List<Appointment>,
    services: List<ServiceItem>,
    workingHours: WorkingHours,
    year: Int,
    month0: Int,
    viewModel: AppViewModel,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddService: () -> Unit,
    onEditService: (ServiceItem) -> Unit,
    onSaveWorkingHours: (WorkingHours) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = when (currentSubTab) {
                MoreSubTab.REPORTS -> 0
                MoreSubTab.SERVICES -> 1
                MoreSubTab.HOURS -> 2
            },
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("more_tab_row")
        ) {
            Tab(
                selected = currentSubTab == MoreSubTab.REPORTS,
                onClick = { onSelectSubTab(MoreSubTab.REPORTS) },
                text = { Text("Relatórios", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Default.Assessment, contentDescription = null) }
            )
            Tab(
                selected = currentSubTab == MoreSubTab.SERVICES,
                onClick = { onSelectSubTab(MoreSubTab.SERVICES) },
                text = { Text("Serviços", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Default.Work, contentDescription = null) }
            )
            Tab(
                selected = currentSubTab == MoreSubTab.HOURS,
                onClick = { onSelectSubTab(MoreSubTab.HOURS) },
                text = { Text("Expediente", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Default.Schedule, contentDescription = null) }
            )
        }

        when (currentSubTab) {
            MoreSubTab.REPORTS -> {
                ReportsView(
                    allAppointments = allAppointments,
                    year = year,
                    month0 = month0,
                    viewModel = viewModel,
                    onPreviousMonth = onPreviousMonth,
                    onNextMonth = onNextMonth
                )
            }
            MoreSubTab.SERVICES -> {
                ServicesView(
                    services = services,
                    onAddService = onAddService,
                    onEditService = onEditService
                )
            }
            MoreSubTab.HOURS -> {
                WorkingHoursView(
                    workingHours = workingHours,
                    onSaveWorkingHours = onSaveWorkingHours
                )
            }
        }
    }
}
