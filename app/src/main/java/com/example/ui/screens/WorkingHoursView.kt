package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WorkingHours

@Composable
fun WorkingHoursView(
    workingHours: WorkingHours,
    onSaveWorkingHours: (WorkingHours) -> Unit
) {
    var startHourText by remember(workingHours) { mutableStateOf(workingHours.startHour.toString()) }
    var endHourText by remember(workingHours) { mutableStateOf(workingHours.endHour.toString()) }
    var intervalMinutesText by remember(workingHours) { mutableStateOf(workingHours.intervalMinutes.toString()) }
    var savedSuccess by remember { mutableStateOf(false) }

    val daysList = listOf(
        Pair("1", "Segunda-feira"),
        Pair("2", "Terça-feira"),
        Pair("3", "Quarta-feira"),
        Pair("4", "Quinta-feira"),
        Pair("5", "Sexta-feira"),
        Pair("6", "Sábado"),
        Pair("7", "Domingo")
    )

    val currentWorkingDays = remember(workingHours) {
        workingHours.workingDays.split(",").map { it.trim() }.toMutableSet()
    }
    var workingDaysSet by remember(workingHours) { mutableStateOf(currentWorkingDays) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("working_hours_view")
    ) {
        Text(
            text = "Configuração de Expediente",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Defina os horários de início e término exibidos na agenda",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = startHourText,
                        onValueChange = { startHourText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Hora de Início") },
                        placeholder = { Text("8") },
                        leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("start_hour_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = endHourText,
                        onValueChange = { endHourText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Hora de Fim") },
                        placeholder = { Text("18") },
                        leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("end_hour_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = intervalMinutesText,
                    onValueChange = { intervalMinutesText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Intervalo dos Slots (minutos)") },
                    placeholder = { Text("60") },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("interval_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Dias de Atendimento:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                daysList.forEach { (code, name) ->
                    val isChecked = workingDaysSet.contains(code)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                val newSet = workingDaysSet.toMutableSet()
                                if (checked) newSet.add(code) else newSet.remove(code)
                                workingDaysSet = newSet
                            }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = name, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val sH = (startHourText.toIntOrNull() ?: 8).coerceIn(0, 23)
                        val eH = (endHourText.toIntOrNull() ?: 18).coerceIn(sH + 1, 24)
                        val intv = (intervalMinutesText.toIntOrNull() ?: 60).coerceIn(15, 120)
                        val daysStr = workingDaysSet.sorted().joinToString(",")

                        onSaveWorkingHours(
                            workingHours.copy(
                                startHour = sH,
                                endHour = eH,
                                intervalMinutes = intv,
                                workingDays = daysStr
                            )
                        )
                        savedSuccess = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_working_hours_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Salvar Horários", fontWeight = FontWeight.Bold)
                }

                if (savedSuccess) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Horários atualizados com sucesso!",
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
