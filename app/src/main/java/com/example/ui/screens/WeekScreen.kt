package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Appointment
import com.example.data.model.WorkingHours
import com.example.util.DateTimeUtils

@Composable
fun WeekScreen(
    selectedDate: String,
    allAppointments: List<Appointment>,
    workingHours: WorkingHours,
    onSelectDate: (String) -> Unit,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onToday: () -> Unit,
    onAppointmentClick: (Appointment) -> Unit,
    onNewAppointmentForSlot: (date: String, time: String) -> Unit
) {
    val weekDays = remember(selectedDate) {
        DateTimeUtils.getDaysOfWeek(selectedDate)
    }

    val hoursList = remember(workingHours) {
        (workingHours.startHour..workingHours.endHour).map { h ->
            String.format("%02d:00", h)
        }
    }

    val hourSlotHeight = 84.dp
    val timeColumnWidth = 60.dp
    val dayColumnWidth = 136.dp

    // Shared horizontal scroll state so header and content scroll together
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Week Title & Navigation Toolbar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val startDay = weekDays.firstOrNull()?.dayOfMonth ?: 1
                val endDay = weekDays.lastOrNull()?.dayOfMonth ?: 7
                val monthName = DateTimeUtils.formatMonthYear(selectedDate)

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Semana: $startDay a $endDay",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = monthName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledTonalButton(
                        onClick = onToday,
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("week_today_button"),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = ButtonDefaults.TextButtonContentPadding
                    ) {
                        Icon(Icons.Default.Today, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Hoje", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(onClick = onPreviousWeek, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Semana anterior", modifier = Modifier.size(18.dp))
                    }

                    IconButton(onClick = onNextWeek, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Próxima semana", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // Header Row: Static corner on left + horizontally scrollable day selector headers
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                // Fixed top-left corner box (aligned with hours column)
                Box(
                    modifier = Modifier
                        .width(timeColumnWidth)
                        .height(64.dp)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Hora",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Horizontally scrollable day headers
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(horizontalScrollState)
                ) {
                    weekDays.forEach { dayInfo ->
                        val isSelected = dayInfo.isSelected
                        val isToday = dayInfo.isToday

                        Box(
                            modifier = Modifier
                                .width(dayColumnWidth)
                                .height(64.dp)
                                .padding(horizontal = 4.dp, vertical = 6.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    when {
                                        isSelected -> MaterialTheme.colorScheme.primaryContainer
                                        isToday -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                                        else -> Color.Transparent
                                    }
                                )
                                .border(
                                    width = if (isToday) 1.5.dp else 0.dp,
                                    color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectDate(dayInfo.dateString) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val isDayOff = !DateTimeUtils.isWorkingDay(dayInfo.dateString, workingHours.workingDays)
                                Text(
                                    text = dayInfo.dayName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${dayInfo.dayOfMonth}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                if (isDayOff) {
                                    Text(
                                        text = "Folga",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Main Grid Body: Fixed Left Time Column + Horizontally scrollable day columns
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScrollState)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                // FIXED STATIC LEFT TIME COLUMN (Mandatory Requirement)
                Column(
                    modifier = Modifier
                        .width(timeColumnWidth)
                        .background(MaterialTheme.colorScheme.surface)
                        .testTag("static_time_column")
                ) {
                    hoursList.forEach { hour ->
                        Box(
                            modifier = Modifier
                                .width(timeColumnWidth)
                                .height(hourSlotHeight)
                                .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Text(
                                text = hour,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(80.dp))
                }

                // HORIZONTALLY SCROLLABLE 7-DAY COLUMNS (Underneath/Next to static time column)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(horizontalScrollState)
                        .testTag("scrollable_week_grid")
                ) {
                    weekDays.forEach { dayInfo ->
                        Column(
                            modifier = Modifier
                                .width(dayColumnWidth)
                                .background(
                                    if (dayInfo.isToday) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f)
                                    else Color.Transparent
                                )
                        ) {
                            hoursList.forEach { hour ->
                                val hourPrefix = hour.substring(0, 2)
                                val appInSlot = allAppointments.firstOrNull {
                                    it.date == dayInfo.dateString && it.startTime.startsWith(hourPrefix)
                                }

                                Box(
                                    modifier = Modifier
                                        .width(dayColumnWidth)
                                        .height(hourSlotHeight)
                                        .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                                        .padding(2.dp)
                                ) {
                                    if (appInSlot != null) {
                                        // Blue card with client name, start time, duration, and price
                                        val isCompleted = appInSlot.status == "completed"
                                        val isCancelled = appInSlot.status == "cancelled"

                                        val cardBg = when {
                                            isCompleted -> Color(0xFFD1FAE5)
                                            isCancelled -> Color(0xFFFFE4E6)
                                            else -> Color(0xFFDBEAFE) // Blue container
                                        }
                                        val cardTextColor = when {
                                            isCompleted -> Color(0xFF064E3B)
                                            isCancelled -> Color(0xFF881337)
                                            else -> Color(0xFF1E3A8A) // Dark Blue
                                        }

                                        Card(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clickable { onAppointmentClick(appInSlot) }
                                                .testTag("week_card_${appInSlot.id}"),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(containerColor = cardBg),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                                verticalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column {
                                                    Text(
                                                        text = appInSlot.clientName,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp,
                                                        color = cardTextColor,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = appInSlot.serviceName,
                                                        fontSize = 9.sp,
                                                        color = cardTextColor.copy(alpha = 0.85f),
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.Bottom
                                                ) {
                                                    Text(
                                                        text = "${appInSlot.startTime} (${appInSlot.durationMinutes}m)",
                                                        fontSize = 8.5.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = cardTextColor
                                                    )
                                                    Text(
                                                        text = "R$${appInSlot.price.toInt()}",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = cardTextColor
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        // Empty slot: tap to add appointment for this day & hour
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clickable {
                                                    onNewAppointmentForSlot(dayInfo.dateString, hour)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Add,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}
