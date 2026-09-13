package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AppointmentDetailModal
import com.example.ui.components.AppointmentModal
import com.example.ui.components.ClientModal
import com.example.ui.components.ServiceModal
import com.example.ui.screens.ClientsScreen
import com.example.ui.screens.DayScreen
import com.example.ui.screens.MonthScreen
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.WeekScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: AppViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val moreSubTab by viewModel.moreSubTab.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()

    val clients by viewModel.clients.collectAsStateWithLifecycle()
    val services by viewModel.services.collectAsStateWithLifecycle()
    val workingHours by viewModel.workingHours.collectAsStateWithLifecycle()
    val allAppointments by viewModel.allAppointments.collectAsStateWithLifecycle()
    val dayAppointments by viewModel.appointmentsForSelectedDate.collectAsStateWithLifecycle()

    val isAppointmentModalOpen by viewModel.isAppointmentModalOpen.collectAsStateWithLifecycle()
    val editingAppointment by viewModel.editingAppointment.collectAsStateWithLifecycle()
    val modalPrefilledDate by viewModel.modalPrefilledDate.collectAsStateWithLifecycle()
    val modalPrefilledStartTime by viewModel.modalPrefilledStartTime.collectAsStateWithLifecycle()
    val collisionError by viewModel.collisionError.collectAsStateWithLifecycle()

    val selectedAppointmentForDetail by viewModel.selectedAppointmentForDetail.collectAsStateWithLifecycle()
    val isClientModalOpen by viewModel.isClientModalOpen.collectAsStateWithLifecycle()
    val editingClient by viewModel.editingClient.collectAsStateWithLifecycle()

    val isServiceModalOpen by viewModel.isServiceModalOpen.collectAsStateWithLifecycle()
    val editingService by viewModel.editingService.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openNewAppointmentModal() },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .size(56.dp)
                    .testTag("fab_new_appointment")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Novo Agendamento", modifier = Modifier.size(28.dp))
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == AppTab.DAY,
                    onClick = { viewModel.setTab(AppTab.DAY) },
                    icon = { Icon(Icons.Default.Schedule, contentDescription = "Dia") },
                    label = { Text("Dia", fontSize = 11.sp, fontWeight = if (currentTab == AppTab.DAY) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.primary
                    )
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.WEEK,
                    onClick = { viewModel.setTab(AppTab.WEEK) },
                    icon = { Icon(Icons.Default.ViewWeek, contentDescription = "Semana") },
                    label = { Text("Semana", fontSize = 11.sp, fontWeight = if (currentTab == AppTab.WEEK) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.primary
                    )
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.MONTH,
                    onClick = { viewModel.setTab(AppTab.MONTH) },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Mês") },
                    label = { Text("Mês", fontSize = 11.sp, fontWeight = if (currentTab == AppTab.MONTH) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.primary
                    )
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.CLIENTS,
                    onClick = { viewModel.setTab(AppTab.CLIENTS) },
                    icon = { Icon(Icons.Default.People, contentDescription = "Clientes") },
                    label = { Text("Clientes", fontSize = 11.sp, fontWeight = if (currentTab == AppTab.CLIENTS) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.primary
                    )
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.MORE,
                    onClick = { viewModel.setTab(AppTab.MORE) },
                    icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "Mais") },
                    label = { Text("Mais", fontSize = 11.sp, fontWeight = if (currentTab == AppTab.MORE) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.DAY -> {
                    DayScreen(
                        selectedDate = selectedDate,
                        appointments = dayAppointments,
                        workingHours = workingHours,
                        onPreviousDay = { viewModel.previousDay() },
                        onNextDay = { viewModel.nextDay() },
                        onToday = { viewModel.goToToday() },
                        onAppointmentClick = { viewModel.openAppointmentDetail(it) },
                        onNewAppointmentForSlot = { time ->
                            viewModel.openNewAppointmentModal(prefillStartTime = time)
                        }
                    )
                }
                AppTab.WEEK -> {
                    WeekScreen(
                        selectedDate = selectedDate,
                        allAppointments = allAppointments,
                        workingHours = workingHours,
                        onSelectDate = { viewModel.selectDate(it) },
                        onPreviousWeek = {
                            val newDate = com.example.util.DateTimeUtils.addDays(selectedDate, -7)
                            viewModel.selectDate(newDate)
                        },
                        onNextWeek = {
                            val newDate = com.example.util.DateTimeUtils.addDays(selectedDate, 7)
                            viewModel.selectDate(newDate)
                        },
                        onToday = { viewModel.goToToday() },
                        onAppointmentClick = { viewModel.openAppointmentDetail(it) },
                        onNewAppointmentForSlot = { date, time ->
                            viewModel.openNewAppointmentModal(prefillDate = date, prefillStartTime = time)
                        }
                    )
                }
                AppTab.MONTH -> {
                    MonthScreen(
                        year = selectedYear,
                        month0 = selectedMonth,
                        allAppointments = allAppointments,
                        onPreviousMonth = { viewModel.previousMonth() },
                        onNextMonth = { viewModel.nextMonth() },
                        onToday = { viewModel.goToToday() },
                        onDayClick = { date ->
                            viewModel.selectDate(date)
                            viewModel.setTab(AppTab.DAY)
                        }
                    )
                }
                AppTab.CLIENTS -> {
                    ClientsScreen(
                        clients = clients,
                        allAppointments = allAppointments,
                        onAddClient = { viewModel.openNewClientModal() },
                        onEditClient = { viewModel.openEditClientModal(it) }
                    )
                }
                AppTab.MORE -> {
                    MoreScreen(
                        currentSubTab = moreSubTab,
                        onSelectSubTab = { viewModel.setMoreSubTab(it) },
                        allAppointments = allAppointments,
                        services = services,
                        workingHours = workingHours,
                        year = selectedYear,
                        month0 = selectedMonth,
                        viewModel = viewModel,
                        onPreviousMonth = { viewModel.previousMonth() },
                        onNextMonth = { viewModel.nextMonth() },
                        onAddService = { viewModel.openNewServiceModal() },
                        onEditService = { viewModel.openEditServiceModal(it) },
                        onSaveWorkingHours = { viewModel.updateWorkingHours(it) }
                    )
                }
            }
        }
    }

    // Appointment Creation/Edition Modal
    if (isAppointmentModalOpen) {
        AppointmentModal(
            editingAppointment = editingAppointment,
            prefillDate = modalPrefilledDate,
            prefillStartTime = modalPrefilledStartTime,
            availableServices = services,
            availableClients = clients,
            workingHours = workingHours,
            collisionError = collisionError,
            onDismiss = { viewModel.closeAppointmentModal() },
            onSave = { app, repeatDays ->
                viewModel.saveAppointment(app, recurrenceDays = repeatDays, onSuccess = {})
            }
        )
    }

    // Appointment Detail Modal
    selectedAppointmentForDetail?.let { app ->
        AppointmentDetailModal(
            appointment = app,
            onDismiss = { viewModel.closeAppointmentDetail() },
            onStatusChange = { newStatus ->
                viewModel.updateAppointmentStatus(app, newStatus)
            },
            onEdit = {
                viewModel.openEditAppointmentModal(app)
            },
            onDelete = {
                viewModel.deleteAppointment(app)
            }
        )
    }

    // Client Creation/Edition Modal
    if (isClientModalOpen) {
        ClientModal(
            editingClient = editingClient,
            onDismiss = { viewModel.closeClientModal() },
            onSave = { client ->
                viewModel.saveClient(client)
            },
            onDelete = { client ->
                viewModel.deleteClient(client)
            }
        )
    }

    // Service Creation/Edition Modal
    if (isServiceModalOpen) {
        ServiceModal(
            editingService = editingService,
            onDismiss = { viewModel.closeServiceModal() },
            onSave = { service ->
                viewModel.saveService(service)
            },
            onDelete = { service ->
                viewModel.deleteService(service)
            }
        )
    }
}

