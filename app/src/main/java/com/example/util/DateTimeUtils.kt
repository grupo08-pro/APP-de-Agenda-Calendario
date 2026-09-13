package com.example.util

import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtils {
    private val standardDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val friendlyDateFormat = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("pt", "BR"))
    private val shortDayFormat = SimpleDateFormat("EEE", Locale("pt", "BR"))
    private val monthYearFormat = SimpleDateFormat("MMMM 'de' yyyy", Locale("pt", "BR"))

    fun todayString(): String {
        return standardDateFormat.format(Date())
    }

    fun formatDate(date: Date): String {
        return standardDateFormat.format(date)
    }

    fun parseDate(dateStr: String): Date {
        return try {
            standardDateFormat.parse(dateStr) ?: Date()
        } catch (_: Exception) {
            Date()
        }
    }

    fun formatFriendly(dateStr: String): String {
        val date = parseDate(dateStr)
        val today = todayString()
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = standardDateFormat.format(cal.time)
        cal.add(Calendar.DAY_OF_YEAR, -2)
        val yesterday = standardDateFormat.format(cal.time)

        val base = friendlyDateFormat.format(date).replaceFirstChar { it.uppercase() }
        return when (dateStr) {
            today -> "Hoje • $base"
            tomorrow -> "Amanhã • $base"
            yesterday -> "Ontem • $base"
            else -> base
        }
    }

    fun formatMonthYear(dateStr: String): String {
        val date = parseDate(dateStr)
        return monthYearFormat.format(date).replaceFirstChar { it.uppercase() }
    }

    fun addDays(dateStr: String, days: Int): String {
        val cal = Calendar.getInstance()
        cal.time = parseDate(dateStr)
        cal.add(Calendar.DAY_OF_YEAR, days)
        return standardDateFormat.format(cal.time)
    }

    fun addMonths(dateStr: String, months: Int): String {
        val cal = Calendar.getInstance()
        cal.time = parseDate(dateStr)
        cal.add(Calendar.MONTH, months)
        return standardDateFormat.format(cal.time)
    }

    /**
     * Returns day of week code: "1" for Monday, "2" for Tuesday, ..., "6" for Saturday, "7" for Sunday
     */
    fun getDayOfWeekCode(dateStr: String): String {
        val cal = Calendar.getInstance()
        cal.time = parseDate(dateStr)
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // SUNDAY = 1, MONDAY = 2, ..., SATURDAY = 7
        val code = if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1
        return code.toString()
    }

    /**
     * Checks whether dateStr falls on a configured working day.
     * workingDaysStr is comma-separated day codes like "1,2,3,4,5,6" (1=Mon..7=Sun).
     */
    fun isWorkingDay(dateStr: String, workingDaysStr: String): Boolean {
        if (workingDaysStr.isBlank()) return true
        val allowedDays = workingDaysStr.split(",").map { it.trim() }
        val code = getDayOfWeekCode(dateStr)
        return allowedDays.contains(code)
    }

    fun getDayName(dayCode: String): String {
        return when (dayCode) {
            "1" -> "Segunda-feira"
            "2" -> "Terça-feira"
            "3" -> "Quarta-feira"
            "4" -> "Quinta-feira"
            "5" -> "Sexta-feira"
            "6" -> "Sábado"
            "7" -> "Domingo"
            else -> ""
        }
    }

    /**
     * Returns the human-readable names of days that are marked as off / blocked.
     */
    fun getOffDaysNames(workingDaysStr: String): List<String> {
        val workingSet = workingDaysStr.split(",").map { it.trim() }.toSet()
        val allDays = listOf("1", "2", "3", "4", "5", "6", "7")
        return allDays.filter { !workingSet.contains(it) }.map { getDayName(it) }
    }

    fun getStartOfWeek(dateStr: String): String {
        val cal = Calendar.getInstance()
        cal.time = parseDate(dateStr)
        cal.firstDayOfWeek = Calendar.MONDAY
        var dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        // In Java Calendar, SUNDAY=1, MONDAY=2, ..., SATURDAY=7
        val diff = if (dayOfWeek == Calendar.SUNDAY) -6 else Calendar.MONDAY - dayOfWeek
        cal.add(Calendar.DAY_OF_MONTH, diff)
        return standardDateFormat.format(cal.time)
    }

    fun getDaysOfWeek(selectedDateStr: String): List<WeekDayInfo> {
        val startOfWeekStr = getStartOfWeek(selectedDateStr)
        val cal = Calendar.getInstance()
        cal.time = parseDate(startOfWeekStr)

        val result = mutableListOf<WeekDayInfo>()
        val dayNames = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")

        for (i in 0..6) {
            val dateStr = standardDateFormat.format(cal.time)
            val dayNum = cal.get(Calendar.DAY_OF_MONTH)
            result.add(
                WeekDayInfo(
                    dateString = dateStr,
                    dayOfWeekNumber = i + 1, // 1 to 7
                    dayName = dayNames[i],
                    dayOfMonth = dayNum,
                    isToday = dateStr == todayString(),
                    isSelected = dateStr == selectedDateStr
                )
            )
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return result
    }

    data class WeekDayInfo(
        val dateString: String,
        val dayOfWeekNumber: Int,
        val dayName: String,
        val dayOfMonth: Int,
        val isToday: Boolean,
        val isSelected: Boolean
    )

    data class MonthDayInfo(
        val dateString: String,
        val dayOfMonth: Int,
        val isCurrentMonth: Boolean,
        val isToday: Boolean
    )

    fun getMonthDays(year: Int, monthIndex0: Int): List<MonthDayInfo> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthIndex0)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.firstDayOfWeek = Calendar.MONDAY

        // Get day of week of 1st day (where Monday is index 0)
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val offset = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY

        // Move to start of calendar matrix
        cal.add(Calendar.DAY_OF_MONTH, -offset)

        val result = mutableListOf<MonthDayInfo>()
        val totalCells = 35 // 7x5 or 42 (7x6)
        val today = todayString()

        for (i in 0 until 42) {
            val dateStr = standardDateFormat.format(cal.time)
            val isCurrentMonth = cal.get(Calendar.MONTH) == monthIndex0
            result.add(
                MonthDayInfo(
                    dateString = dateStr,
                    dayOfMonth = cal.get(Calendar.DAY_OF_MONTH),
                    isCurrentMonth = isCurrentMonth,
                    isToday = dateStr == today
                )
            )
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return result
    }

    fun calculateEndTime(startTime: String, durationMinutes: Int): String {
        val parts = startTime.split(":")
        if (parts.size != 2) return startTime
        val h = parts[0].toIntOrNull() ?: 0
        val m = parts[1].toIntOrNull() ?: 0
        val totalMinutes = h * 60 + m + durationMinutes
        val endH = (totalMinutes / 60) % 24
        val endM = totalMinutes % 60
        return String.format(Locale.getDefault(), "%02d:%02d", endH, endM)
    }

    fun formatCurrency(amount: Double, symbol: String = "R$"): String {
        return String.format(Locale("pt", "BR"), "%s %.2f", symbol, amount)
    }

    fun buildWhatsAppUrl(rawPhone: String, clientName: String, date: String, time: String, service: String): String {
        var cleanPhone = rawPhone.replace(Regex("[^0-9]"), "")
        if (!cleanPhone.startsWith("55") && cleanPhone.length in 10..11) {
            cleanPhone = "55$cleanPhone"
        }
        val message = "Olá $clientName! Confirmando seu agendamento de $service para o dia $date às $time. Qualquer dúvida estamos à disposição!"
        val encodedMessage = try {
            URLEncoder.encode(message, "UTF-8")
        } catch (_: Exception) {
            message
        }
        return "https://wa.me/$cleanPhone?text=$encodedMessage"
    }
}
