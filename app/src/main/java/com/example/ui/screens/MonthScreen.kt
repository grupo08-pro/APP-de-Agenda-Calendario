package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Appointment
import com.example.ui.theme.EmeraldTertiary
import com.example.util.DateTimeUtils

@Composable
fun MonthScreen(
    year: Int,
    month0: Int,
    allAppointments: List<Appointment>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToday: () -> Unit,
    onDayClick: (String) -> Unit
) {
    val monthDays = remember(year, month0) {
        DateTimeUtils.getMonthDays(year, month0)
    }

    val monthName = remember(year, month0) {
        val sampleDateStr = String.format("%04d-%02d-01", year, month0 + 1)
        DateTimeUtils.formatMonthYear(sampleDateStr)
    }

    val dayNames = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Month Header
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
                Column {
                    Text(
                        text = monthName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Toque em um dia para ver os horários",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledTonalButton(
                        onClick = onToday,
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("month_today_button"),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = ButtonDefaults.TextButtonContentPadding
                    ) {
                        Icon(Icons.Default.Today, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Hoje", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(onClick = onPreviousMonth, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Mês anterior", modifier = Modifier.size(18.dp))
                    }

                    IconButton(onClick = onNextMonth, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Próximo mês", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Days of week header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            dayNames.forEach { dayName ->
                Text(
                    text = dayName,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 7x5 or 7x6 Calendar Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .testTag("month_calendar_grid")
        ) {
            items(monthDays) { dayInfo ->
                val dayAppointments = allAppointments.filter { it.date == dayInfo.dateString && it.status != "cancelled" }
                val count = dayAppointments.size

                val isToday = dayInfo.isToday
                val isCurrentMonth = dayInfo.isCurrentMonth

                val cellBg = when {
                    isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    else -> MaterialTheme.colorScheme.surface
                }

                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .aspectRatio(0.85f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(cellBg)
                        .border(
                            width = if (isToday) 1.5.dp else 0.5.dp,
                            color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onDayClick(dayInfo.dateString) }
                        .testTag("month_day_${dayInfo.dateString}"),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Day Number
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${dayInfo.dayOfMonth}",
                                fontSize = 12.sp,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                                color = when {
                                    isToday -> Color.White
                                    isCurrentMonth -> MaterialTheme.colorScheme.onSurface
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                }
                            )
                        }

                        // Badge with appointment count and status
                        if (count > 0 && isCurrentMonth) {
                            val badgeBg = when {
                                count >= 4 -> Color(0xFF312E81) // Heavy load: Indigo
                                count in 2..3 -> Color(0xFF2563EB) // Moderate: Blue
                                else -> EmeraldTertiary // Light: Emerald Green
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = badgeBg
                            ) {
                                Text(
                                    text = "$count",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }
                }
            }
        }
    }
}
