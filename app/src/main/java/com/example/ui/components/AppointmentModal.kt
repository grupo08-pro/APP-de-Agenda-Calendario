package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Appointment
import com.example.data.model.Client
import com.example.data.model.ServiceItem
import com.example.data.model.WorkingHours
import com.example.util.DateTimeUtils

@Composable
fun AppointmentModal(
    editingAppointment: Appointment?,
    prefillDate: String,
    prefillStartTime: String,
    availableServices: List<ServiceItem>,
    availableClients: List<Client>,
    workingHours: WorkingHours? = null,
    collisionError: String?,
    onDismiss: () -> Unit,
    onSave: (Appointment, Int) -> Unit
) {
    var date by remember { mutableStateOf(editingAppointment?.date ?: prefillDate) }
    var startTime by remember { mutableStateOf(editingAppointment?.startTime ?: prefillStartTime) }
    var durationMinutes by remember {
        mutableIntStateOf(editingAppointment?.durationMinutes ?: (availableServices.firstOrNull()?.durationMinutes ?: 45))
    }
    var endTime by remember {
        mutableStateOf(editingAppointment?.endTime ?: DateTimeUtils.calculateEndTime(startTime, durationMinutes))
    }

    var selectedClientId by remember { mutableStateOf<Long?>(editingAppointment?.clientId) }
    var clientName by remember { mutableStateOf(editingAppointment?.clientName ?: "") }
    var clientPhone by remember { mutableStateOf(editingAppointment?.clientPhone ?: "") }

    var selectedServiceId by remember {
        mutableStateOf<Long?>(editingAppointment?.serviceId ?: availableServices.firstOrNull()?.id)
    }
    var serviceName by remember {
        mutableStateOf(editingAppointment?.serviceName ?: (availableServices.firstOrNull()?.name ?: "Atendimento"))
    }
    var price by remember {
        mutableDoubleStateOf(editingAppointment?.price ?: (availableServices.firstOrNull()?.price ?: 50.0))
    }
    var recurrence by remember { mutableStateOf(editingAppointment?.recurrence ?: "none") }
    var repeatDays by remember { mutableIntStateOf(7) }
    var notes by remember { mutableStateOf(editingAppointment?.notes ?: "") }

    // Dropdowns visibility
    var serviceDropdownExpanded by remember { mutableStateOf(false) }
    var clientSearchExpanded by remember { mutableStateOf(false) }
    var startTimeDropdownExpanded by remember { mutableStateOf(false) }
    var recurrenceDropdownExpanded by remember { mutableStateOf(false) }

    // Filtered clients for autocomplete
    val filteredClients = remember(clientName, availableClients) {
        if (clientName.isBlank()) emptyList()
        else availableClients.filter { it.name.contains(clientName, ignoreCase = true) }
    }

    val timeSlots = remember {
        listOf(
            "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
            "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",
            "14:00", "14:30", "15:00", "15:30", "16:00", "16:30",
            "17:00", "17:30", "18:00", "18:30", "19:00"
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .testTag("appointment_modal_surface"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (editingAppointment != null) "Editar Agendamento" else "Novo Agendamento",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Preencha os detalhes do atendimento",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_modal_button")) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error banner for time collision
                if (!collisionError.isNullOrBlank()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = collisionError,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Service Dropdown Selector (Auto-fills duration, end time and price)
                Text(
                    text = "Serviço / Procedimento *",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = "$serviceName • ${durationMinutes}min • R$ ${String.format("%.2f", price)}",
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = {
                            Icon(Icons.Default.Work, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { serviceDropdownExpanded = !serviceDropdownExpanded }) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Selecionar serviço")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { serviceDropdownExpanded = true }
                            .testTag("service_selector_field"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors()
                    )

                    DropdownMenu(
                        expanded = serviceDropdownExpanded,
                        onDismissRequest = { serviceDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        availableServices.forEach { service ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(service.name, fontWeight = FontWeight.SemiBold)
                                            Text(
                                                "${service.durationMinutes} min • ${service.category}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            "R$ ${String.format("%.2f", service.price)}",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                },
                                onClick = {
                                    selectedServiceId = service.id
                                    serviceName = service.name
                                    durationMinutes = service.durationMinutes
                                    price = service.price
                                    endTime = DateTimeUtils.calculateEndTime(startTime, service.durationMinutes)
                                    serviceDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Date & Time Row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Date input
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Data (AAAA-MM-DD)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            leadingIcon = {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("appointment_date_input"),
                            singleLine = true
                        )
                    }

                    // Start Time dropdown
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Horário de Início",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box {
                            OutlinedTextField(
                                value = startTime,
                                onValueChange = {
                                    startTime = it
                                    endTime = DateTimeUtils.calculateEndTime(it, durationMinutes)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { startTimeDropdownExpanded = !startTimeDropdownExpanded }) {
                                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("appointment_start_time_input"),
                                singleLine = true
                            )

                            DropdownMenu(
                                expanded = startTimeDropdownExpanded,
                                onDismissRequest = { startTimeDropdownExpanded = false }
                            ) {
                                timeSlots.forEach { slot ->
                                    DropdownMenuItem(
                                        text = { Text(slot) },
                                        onClick = {
                                            startTime = slot
                                            endTime = DateTimeUtils.calculateEndTime(slot, durationMinutes)
                                            startTimeDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Calculated Duration & End Time info card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Término previsto: $endTime (${durationMinutes} min)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "R$ ${String.format("%.2f", price)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Check if the selected date is a day off
                val isDateOff = remember(date, workingHours) {
                    workingHours?.let { !DateTimeUtils.isWorkingDay(date, it.workingDays) } ?: false
                }
                if (isDateOff) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.65f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Atenção: $date está configurado como dia de folga no expediente.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Client Name (with Autocomplete suggestion popup)
                Text(
                    text = "Nome do Cliente *",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = {
                            clientName = it
                            clientSearchExpanded = it.isNotBlank()
                        },
                        placeholder = { Text("Digite para buscar ou adicionar") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("client_name_input"),
                        singleLine = true
                    )

                    if (clientSearchExpanded && filteredClients.isNotEmpty()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 60.dp)
                                .heightIn(max = 160.dp),
                            shape = RoundedCornerShape(12.dp),
                            tonalElevation = 8.dp,
                            shadowElevation = 6.dp,
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            LazyColumn {
                                items(filteredClients) { client ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedClientId = client.id
                                                clientName = client.name
                                                clientPhone = client.phone
                                                clientSearchExpanded = false
                                            }
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(client.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                            if (client.phone.isNotBlank()) {
                                                Text(
                                                    client.phone,
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        Text(
                                            "Selecionar",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Phone Input (auto-filled if client was selected)
                Text(
                    text = "Telefone / WhatsApp (com DDD)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = clientPhone,
                    onValueChange = { clientPhone = it },
                    placeholder = { Text("Ex: 11987654321") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("client_phone_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Recurrence Selector
                Text(
                    text = "Recorrência",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    val recurrenceLabel = when (recurrence) {
                        "daily" -> "Diariamente (dias úteis de trabalho)"
                        "weekly" -> "Semanal (repete toda semana)"
                        "biweekly" -> "Quinzenal (a cada 2 semanas)"
                        "monthly" -> "Mensal (todo mês)"
                        else -> "Nenhuma (atendimento único)"
                    }

                    OutlinedTextField(
                        value = recurrenceLabel,
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = {
                            Icon(Icons.Default.Repeat, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { recurrenceDropdownExpanded = !recurrenceDropdownExpanded }) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { recurrenceDropdownExpanded = true }
                    )

                    DropdownMenu(
                        expanded = recurrenceDropdownExpanded,
                        onDismissRequest = { recurrenceDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Nenhuma (atendimento único)") },
                            onClick = { recurrence = "none"; recurrenceDropdownExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Diariamente (dias de trabalho)") },
                            onClick = { recurrence = "daily"; recurrenceDropdownExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Semanal (repete toda semana)") },
                            onClick = { recurrence = "weekly"; recurrenceDropdownExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Quinzenal (a cada 2 semanas)") },
                            onClick = { recurrence = "biweekly"; recurrenceDropdownExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Mensal (todo mês)") },
                            onClick = { recurrence = "monthly"; recurrenceDropdownExpanded = false }
                        )
                    }
                }

                // If daily recurrence is selected, show repeat days configuration
                AnimatedVisibility(visible = recurrence == "daily") {
                    val offDaysList = remember(workingHours) {
                        workingHours?.let { DateTimeUtils.getOffDaysNames(it.workingDays) } ?: emptyList()
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Repetir por quantos dias?",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Máximo: 60 dias",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = { if (repeatDays > 1) repeatDays-- },
                                    enabled = repeatDays > 1,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Remove,
                                        contentDescription = "Diminuir dias",
                                        tint = if (repeatDays > 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                    )
                                }

                                OutlinedTextField(
                                    value = repeatDays.toString(),
                                    onValueChange = { str ->
                                        val v = str.filter { it.isDigit() }.toIntOrNull()
                                        if (v != null) {
                                            repeatDays = v.coerceIn(1, 60)
                                        } else if (str.isEmpty()) {
                                            repeatDays = 1
                                        }
                                    },
                                    textStyle = MaterialTheme.typography.titleSmall.copy(
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.width(62.dp),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                IconButton(
                                    onClick = { if (repeatDays < 60) repeatDays++ },
                                    enabled = repeatDays < 60,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Aumentar dias",
                                        tint = if (repeatDays < 60) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(5, 7, 15, 30, 60).forEach { preset ->
                                FilterChip(
                                    selected = repeatDays == preset,
                                    onClick = { repeatDays = preset },
                                    label = { Text("${preset}d", fontSize = 12.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val offDaysText = if (offDaysList.isNotEmpty()) {
                                "Dias bloqueados de folga (${offDaysList.joinToString(", ")}) não terão atendimento gerado."
                            } else {
                                "Não gerará atendimento nos dias bloqueados de folga."
                            }
                            Text(
                                text = offDaysText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Observações
                Text(
                    text = "Observações / Detalhes",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Ex: Cliente solicitou corte com tesoura...") },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 70.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            if (clientName.isNotBlank() && serviceName.isNotBlank()) {
                                onSave(
                                    Appointment(
                                        id = editingAppointment?.id ?: 0,
                                        clientId = selectedClientId,
                                        clientName = clientName.trim(),
                                        clientPhone = clientPhone.trim(),
                                        serviceId = selectedServiceId,
                                        serviceName = serviceName,
                                        price = price,
                                        currency = "R$",
                                        date = date,
                                        startTime = startTime,
                                        endTime = endTime,
                                        durationMinutes = durationMinutes,
                                        status = editingAppointment?.status ?: "scheduled",
                                        recurrence = recurrence,
                                        notes = notes.trim()
                                    ),
                                    repeatDays
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("save_appointment_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        enabled = clientName.isNotBlank() && serviceName.isNotBlank()
                    ) {
                        Text(if (editingAppointment != null) "Atualizar" else "Salvar")
                    }
                }
            }
        }
    }
}
