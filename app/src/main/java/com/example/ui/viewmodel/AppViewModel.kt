package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.Appointment
import com.example.data.model.Client
import com.example.data.model.ServiceItem
import com.example.data.model.WorkingHours
import com.example.data.repository.AppointmentRepository
import com.example.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class AppTab(val title: String) {
    DAY("Dia"),
    WEEK("Semana"),
    MONTH("Mês"),
    CLIENTS("Clientes"),
    MORE("Mais")
}

enum class MoreSubTab {
    REPORTS,
    SERVICES,
    HOURS
}

data class ServiceRevenueStat(
    val serviceName: String,
    val count: Int,
    val totalRevenue: Double,
    val color: String
)

data class MonthReportMetrics(
    val completedRevenue: Double = 0.0,
    val scheduledRevenue: Double = 0.0,
    val totalAppointments: Int = 0,
    val completedAppointments: Int = 0,
    val averageTicket: Double = 0.0,
    val topServices: List<ServiceRevenueStat> = emptyList()
)

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppointmentRepository

    init {
        val db = AppDatabase.getInstance(application)
        repository = AppointmentRepository(db)
        viewModelScope.launch {
            repository.checkAndSeedIfNeeded()
        }
    }

    private val _currentTab = MutableStateFlow(AppTab.DAY)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _moreSubTab = MutableStateFlow(MoreSubTab.REPORTS)
    val moreSubTab: StateFlow<MoreSubTab> = _moreSubTab.asStateFlow()

    private val _selectedDate = MutableStateFlow(DateTimeUtils.todayString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val cal = Calendar.getInstance()
    private val _selectedYear = MutableStateFlow(cal.get(Calendar.YEAR))
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    private val _selectedMonth = MutableStateFlow(cal.get(Calendar.MONTH)) // 0 to 11
    val selectedMonth: StateFlow<Int> = _selectedMonth.asStateFlow()

    val clients: StateFlow<List<Client>> = repository.clients.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val services: StateFlow<List<ServiceItem>> = repository.services.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val workingHours: StateFlow<WorkingHours> = repository.workingHours
        .combine(MutableStateFlow(Unit)) { wh, _ ->
            wh ?: WorkingHours(id = 1, startHour = 8, endHour = 18, intervalMinutes = 60, workingDays = "1,2,3,4,5,6")
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WorkingHours(id = 1, startHour = 8, endHour = 18, intervalMinutes = 60, workingDays = "1,2,3,4,5,6")
        )

    val allAppointments: StateFlow<List<Appointment>> = repository.allAppointments.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val appointmentsForSelectedDate: StateFlow<List<Appointment>> = _selectedDate.flatMapLatest { date ->
        repository.getAppointmentsForDate(date)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Modals state
    private val _isAppointmentModalOpen = MutableStateFlow(false)
    val isAppointmentModalOpen: StateFlow<Boolean> = _isAppointmentModalOpen.asStateFlow()

    private val _editingAppointment = MutableStateFlow<Appointment?>(null)
    val editingAppointment: StateFlow<Appointment?> = _editingAppointment.asStateFlow()

    private val _modalPrefilledDate = MutableStateFlow(DateTimeUtils.todayString())
    val modalPrefilledDate: StateFlow<String> = _modalPrefilledDate.asStateFlow()

    private val _modalPrefilledStartTime = MutableStateFlow("09:00")
    val modalPrefilledStartTime: StateFlow<String> = _modalPrefilledStartTime.asStateFlow()

    private val _collisionError = MutableStateFlow<String?>(null)
    val collisionError: StateFlow<String?> = _collisionError.asStateFlow()

    // Appointment detail modal
    private val _selectedAppointmentForDetail = MutableStateFlow<Appointment?>(null)
    val selectedAppointmentForDetail: StateFlow<Appointment?> = _selectedAppointmentForDetail.asStateFlow()

    // Client modal
    private val _isClientModalOpen = MutableStateFlow(false)
    val isClientModalOpen: StateFlow<Boolean> = _isClientModalOpen.asStateFlow()

    private val _editingClient = MutableStateFlow<Client?>(null)
    val editingClient: StateFlow<Client?> = _editingClient.asStateFlow()

    // Service modal
    private val _isServiceModalOpen = MutableStateFlow(false)
    val isServiceModalOpen: StateFlow<Boolean> = _isServiceModalOpen.asStateFlow()

    private val _editingService = MutableStateFlow<ServiceItem?>(null)
    val editingService: StateFlow<ServiceItem?> = _editingService.asStateFlow()

    // Navigation methods
    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setMoreSubTab(subTab: MoreSubTab) {
        _moreSubTab.value = subTab
    }

    fun selectDate(dateStr: String) {
        _selectedDate.value = dateStr
        val d = DateTimeUtils.parseDate(dateStr)
        val c = Calendar.getInstance()
        c.time = d
        _selectedYear.value = c.get(Calendar.YEAR)
        _selectedMonth.value = c.get(Calendar.MONTH)
    }

    fun goToToday() {
        selectDate(DateTimeUtils.todayString())
    }

    fun previousDay() {
        _selectedDate.value = DateTimeUtils.addDays(_selectedDate.value, -1)
    }

    fun nextDay() {
        _selectedDate.value = DateTimeUtils.addDays(_selectedDate.value, 1)
    }

    fun previousMonth() {
        if (_selectedMonth.value == 0) {
            _selectedMonth.value = 11
            _selectedYear.value -= 1
        } else {
            _selectedMonth.value -= 1
        }
    }

    fun nextMonth() {
        if (_selectedMonth.value == 11) {
            _selectedMonth.value = 0
            _selectedYear.value += 1
        } else {
            _selectedMonth.value += 1
        }
    }

    // Appointment Modal controls
    fun openNewAppointmentModal(prefillDate: String? = null, prefillStartTime: String? = null) {
        _editingAppointment.value = null
        _modalPrefilledDate.value = prefillDate ?: _selectedDate.value
        _modalPrefilledStartTime.value = prefillStartTime ?: "09:00"
        _collisionError.value = null
        _isAppointmentModalOpen.value = true
    }

    fun openEditAppointmentModal(appointment: Appointment) {
        _editingAppointment.value = appointment
        _modalPrefilledDate.value = appointment.date
        _modalPrefilledStartTime.value = appointment.startTime
        _collisionError.value = null
        _selectedAppointmentForDetail.value = null
        _isAppointmentModalOpen.value = true
    }

    fun closeAppointmentModal() {
        _isAppointmentModalOpen.value = false
        _editingAppointment.value = null
        _collisionError.value = null
    }

    fun openAppointmentDetail(appointment: Appointment) {
        _selectedAppointmentForDetail.value = appointment
    }

    fun closeAppointmentDetail() {
        _selectedAppointmentForDetail.value = null
    }

    fun saveAppointment(
        appointment: Appointment,
        recurrenceDays: Int = 7,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val currentWorkingDays = workingHours.value.workingDays

            // Block scheduling on days of folga
            if (!DateTimeUtils.isWorkingDay(appointment.date, currentWorkingDays)) {
                _collisionError.value = "Dia bloqueado! Não é permitido agendar em dias de folga configurados no expediente (${appointment.date})."
                return@launch
            }

            // Check collision
            val hasCollision = repository.checkTimeCollision(
                date = appointment.date,
                startTime = appointment.startTime,
                endTime = appointment.endTime,
                excludeId = if (appointment.id > 0) appointment.id else null
            )

            if (hasCollision) {
                _collisionError.value = "Choque de horários! Já existe um atendimento agendado para o horário informado em ${appointment.date}."
                return@launch
            }

            _collisionError.value = null

            if (appointment.id > 0) {
                repository.updateAppointment(appointment)
            } else {
                repository.insertAppointment(appointment)

                // Handle recurrence: do not generate on blocked off days (folga)
                when (appointment.recurrence) {
                    "daily" -> {
                        val maxDays = recurrenceDays.coerceIn(1, 60)
                        for (i in 1..maxDays) {
                            val nextDate = DateTimeUtils.addDays(appointment.date, i)
                            // Não gerar atendimento nos dias bloqueados que são de folga
                            if (!DateTimeUtils.isWorkingDay(nextDate, currentWorkingDays)) {
                                continue
                            }
                            val coll = repository.checkTimeCollision(nextDate, appointment.startTime, appointment.endTime)
                            if (!coll) {
                                repository.insertAppointment(appointment.copy(id = 0, date = nextDate))
                            }
                        }
                    }
                    "weekly" -> {
                        // Create 3 additional weekly occurrences
                        for (i in 1..3) {
                            val nextDate = DateTimeUtils.addDays(appointment.date, i * 7)
                            if (!DateTimeUtils.isWorkingDay(nextDate, currentWorkingDays)) {
                                continue
                            }
                            val coll = repository.checkTimeCollision(nextDate, appointment.startTime, appointment.endTime)
                            if (!coll) {
                                repository.insertAppointment(appointment.copy(id = 0, date = nextDate))
                            }
                        }
                    }
                    "biweekly" -> {
                        // Create 2 additional bi-weekly occurrences
                        for (i in 1..2) {
                            val nextDate = DateTimeUtils.addDays(appointment.date, i * 14)
                            if (!DateTimeUtils.isWorkingDay(nextDate, currentWorkingDays)) {
                                continue
                            }
                            val coll = repository.checkTimeCollision(nextDate, appointment.startTime, appointment.endTime)
                            if (!coll) {
                                repository.insertAppointment(appointment.copy(id = 0, date = nextDate))
                            }
                        }
                    }
                    "monthly" -> {
                        // Create 2 additional monthly occurrences
                        for (i in 1..2) {
                            val nextDate = DateTimeUtils.addMonths(appointment.date, i)
                            if (!DateTimeUtils.isWorkingDay(nextDate, currentWorkingDays)) {
                                continue
                            }
                            val coll = repository.checkTimeCollision(nextDate, appointment.startTime, appointment.endTime)
                            if (!coll) {
                                repository.insertAppointment(appointment.copy(id = 0, date = nextDate))
                            }
                        }
                    }
                }
            }
            closeAppointmentModal()
            onSuccess()
        }
    }

    fun updateAppointmentStatus(appointment: Appointment, newStatus: String) {
        viewModelScope.launch {
            val updated = appointment.copy(status = newStatus)
            repository.updateAppointment(updated)
            if (_selectedAppointmentForDetail.value?.id == appointment.id) {
                _selectedAppointmentForDetail.value = updated
            }
        }
    }

    fun deleteAppointment(appointment: Appointment) {
        viewModelScope.launch {
            repository.deleteAppointment(appointment)
            closeAppointmentDetail()
        }
    }

    // Client modal controls
    fun openNewClientModal() {
        _editingClient.value = null
        _isClientModalOpen.value = true
    }

    fun openEditClientModal(client: Client) {
        _editingClient.value = client
        _isClientModalOpen.value = true
    }

    fun closeClientModal() {
        _isClientModalOpen.value = false
        _editingClient.value = null
    }

    fun saveClient(client: Client) {
        viewModelScope.launch {
            if (client.id > 0) {
                repository.updateClient(client)
            } else {
                repository.insertClient(client)
            }
            closeClientModal()
        }
    }

    fun deleteClient(client: Client) {
        viewModelScope.launch {
            repository.deleteClient(client)
            closeClientModal()
        }
    }

    // Service modal controls
    fun openNewServiceModal() {
        _editingService.value = null
        _isServiceModalOpen.value = true
    }

    fun openEditServiceModal(service: ServiceItem) {
        _editingService.value = service
        _isServiceModalOpen.value = true
    }

    fun closeServiceModal() {
        _isServiceModalOpen.value = false
        _editingService.value = null
    }

    fun saveService(service: ServiceItem) {
        viewModelScope.launch {
            if (service.id > 0) {
                repository.updateService(service)
            } else {
                repository.insertService(service)
            }
            closeServiceModal()
        }
    }

    fun deleteService(service: ServiceItem) {
        viewModelScope.launch {
            repository.deleteService(service)
            closeServiceModal()
        }
    }

    fun updateWorkingHours(workingHours: WorkingHours) {
        viewModelScope.launch {
            repository.setWorkingHours(workingHours)
        }
    }

    // Financial calculations
    fun calculateMonthMetrics(all: List<Appointment>, year: Int, month0: Int): MonthReportMetrics {
        val targetMonthPrefix = String.format("%04d-%02d", year, month0 + 1)
        val monthAppointments = all.filter { it.date.startsWith(targetMonthPrefix) }

        var completedRev = 0.0
        var scheduledRev = 0.0
        var completedCount = 0

        val serviceMap = mutableMapOf<String, Pair<Int, Double>>()

        for (app in monthAppointments) {
            when (app.status) {
                "completed" -> {
                    completedRev += app.price
                    completedCount++
                    val current = serviceMap.getOrDefault(app.serviceName, Pair(0, 0.0))
                    serviceMap[app.serviceName] = Pair(current.first + 1, current.second + app.price)
                }
                "scheduled" -> {
                    scheduledRev += app.price
                }
            }
        }

        val avgTicket = if (completedCount > 0) completedRev / completedCount else 0.0

        val topServices = serviceMap.map { (name, pair) ->
            ServiceRevenueStat(
                serviceName = name,
                count = pair.first,
                totalRevenue = pair.second,
                color = "#4F46E5"
            )
        }.sortedByDescending { it.totalRevenue }

        return MonthReportMetrics(
            completedRevenue = completedRev,
            scheduledRevenue = scheduledRev,
            totalAppointments = monthAppointments.size,
            completedAppointments = completedCount,
            averageTicket = avgTicket,
            topServices = topServices
        )
    }
}
