package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Appointment
import com.example.data.model.WorkingHours
import com.example.ui.theme.EmeraldTertiary
import com.example.util.DateTimeUtils

@Composable
fun DayScreen(
    selectedDate: String,
    appointments: List<Appointment>,
    workingHours: WorkingHours,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onToday: () -> Unit,
    onAppointmentClick: (Appointment) -> Unit,
    onNewAppointmentForSlot: (time: String) -> Unit
) {
    val context = LocalContext.current
    val hoursList = (workingHours.startHour..workingHours.endHour).map { h ->
        String.format("%02d:00", h)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Date Header Navigation
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = DateTimeUtils.formatFriendly(selectedDate),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = "${appointments.size} agendamento(s) hoje",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledTonalButton(
                        onClick = onToday,
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("today_button"),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = ButtonDefaults.TextButtonContentPadding
                    ) {
                        Text("Hoje", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(onClick = onPreviousDay, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Dia anterior", modifier = Modifier.size(20.dp))
                    }

                    IconButton(onClick = onNextDay, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Próximo dia", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        val isDayOff = !DateTimeUtils.isWorkingDay(selectedDate, workingHours.workingDays)

        if (isDayOff) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Dia de Folga • Expediente Bloqueado",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "Configurado como dia de folga. Agendamentos recorrentes não são gerados neste dia.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Timeline List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(hoursList.size) { index ->
                val hourStr = hoursList[index]
                val hourPrefix = hourStr.substring(0, 2)
                val appsInSlot = appointments.filter { it.startTime.startsWith(hourPrefix) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Time label on the left
                    Column(
                        modifier = Modifier.width(55.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = hourStr,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(50.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Slot content
                    Column(modifier = Modifier.weight(1f)) {
                        if (appsInSlot.isNotEmpty()) {
                            appsInSlot.forEach { app ->
                                val statusBg = when (app.status) {
                                    "completed" -> Color(0xFFD1FAE5)
                                    "cancelled" -> Color(0xFFFFE4E6)
                                    else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                }
                                val statusTextColor = when (app.status) {
                                    "completed" -> EmeraldTertiary
                                    "cancelled" -> Color(0xFFE11D48)
                                    else -> MaterialTheme.colorScheme.primary
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                        .clickable { onAppointmentClick(app) }
                                        .testTag("appointment_card_${app.id}"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(10.dp)
                                                        .clip(CircleShape)
                                                        .background(statusTextColor)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = app.clientName,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = statusBg
                                            ) {
                                                Text(
                                                    text = when (app.status) {
                                                        "completed" -> "Concluído"
                                                        "cancelled" -> "Cancelado"
                                                        else -> "Agendado"
                                                    },
                                                    color = statusTextColor,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${app.serviceName} • ${app.durationMinutes} min",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "R$ ${String.format("%.2f", app.price)}",
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${app.startTime} às ${app.endTime}",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            // WhatsApp shortcut button directly on the card
                                            if (app.clientPhone.isNotBlank()) {
                                                FilledTonalButton(
                                                    onClick = {
                                                        val url = DateTimeUtils.buildWhatsAppUrl(
                                                            rawPhone = app.clientPhone,
                                                            clientName = app.clientName,
                                                            date = app.date,
                                                            time = app.startTime,
                                                            service = app.serviceName
                                                        )
                                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                        try {
                                                            context.startActivity(intent)
                                                        } catch (_: Exception) {}
                                                    },
                                                    modifier = Modifier
                                                        .height(32.dp)
                                                        .testTag("card_whatsapp_btn_${app.id}"),
                                                    shape = RoundedCornerShape(8.dp),
                                                    colors = ButtonDefaults.filledTonalButtonColors(
                                                        containerColor = Color(0xFFD1FAE5),
                                                        contentColor = EmeraldTertiary
                                                    ),
                                                    contentPadding = ButtonDefaults.TextButtonContentPadding
                                                ) {
                                                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // Free Slot Button: "+ Disponível (HH:MM)"
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onNewAppointmentForSlot(hourStr) }
                                    .testTag("free_slot_$hourStr"),
                                color = if (isDayOff) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        if (isDayOff) Icons.Default.Lock else Icons.Default.Add,
                                        contentDescription = null,
                                        tint = if (isDayOff) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isDayOff) "Dia de Folga • Bloqueado ($hourStr)" else "+ Disponível ($hourStr)",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isDayOff) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
