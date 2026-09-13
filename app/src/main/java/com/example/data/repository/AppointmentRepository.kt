package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.Appointment
import com.example.data.model.Client
import com.example.data.model.ServiceItem
import com.example.data.model.WorkingHours
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class AppointmentRepository(private val database: AppDatabase) {
    private val clientDao = database.clientDao()
    private val serviceDao = database.serviceDao()
    private val appointmentDao = database.appointmentDao()
    private val workingHoursDao = database.workingHoursDao()

    val clients: Flow<List<Client>> = clientDao.getAllClients()
    val services: Flow<List<ServiceItem>> = serviceDao.getAllServices()
    val workingHours: Flow<WorkingHours?> = workingHoursDao.getWorkingHours()
    val allAppointments: Flow<List<Appointment>> = appointmentDao.getAllAppointments()

    fun getAppointmentsForDate(date: String): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsByDate(date)
    }

    fun getAppointmentsBetweenDates(startDate: String, endDate: String): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsBetweenDates(startDate, endDate)
    }

    fun getAppointmentsForClient(clientId: Long): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsByClient(clientId)
    }

    suspend fun insertAppointment(appointment: Appointment): Long = withContext(Dispatchers.IO) {
        appointmentDao.insertAppointment(appointment)
    }

    suspend fun updateAppointment(appointment: Appointment) = withContext(Dispatchers.IO) {
        appointmentDao.updateAppointment(appointment)
    }

    suspend fun deleteAppointment(appointment: Appointment) = withContext(Dispatchers.IO) {
        appointmentDao.deleteAppointment(appointment)
    }

    suspend fun deleteAppointmentById(id: Long) = withContext(Dispatchers.IO) {
        appointmentDao.deleteAppointmentById(id)
    }

    suspend fun insertClient(client: Client): Long = withContext(Dispatchers.IO) {
        clientDao.insertClient(client)
    }

    suspend fun updateClient(client: Client) = withContext(Dispatchers.IO) {
        clientDao.updateClient(client)
    }

    suspend fun deleteClient(client: Client) = withContext(Dispatchers.IO) {
        clientDao.deleteClient(client)
    }

    suspend fun insertService(service: ServiceItem): Long = withContext(Dispatchers.IO) {
        serviceDao.insertService(service)
    }

    suspend fun updateService(service: ServiceItem) = withContext(Dispatchers.IO) {
        serviceDao.updateService(service)
    }

    suspend fun deleteService(service: ServiceItem) = withContext(Dispatchers.IO) {
        serviceDao.deleteService(service)
    }

    suspend fun setWorkingHours(workingHours: WorkingHours) = withContext(Dispatchers.IO) {
        workingHoursDao.setWorkingHours(workingHours)
    }

    suspend fun checkTimeCollision(
        date: String,
        startTime: String,
        endTime: String,
        excludeId: Long? = null
    ): Boolean = withContext(Dispatchers.IO) {
        val existing = appointmentDao.getAppointmentsByDateSync(date)
        val startMins = timeToMinutes(startTime)
        val endMins = timeToMinutes(endTime)

        for (app in existing) {
            if (excludeId != null && app.id == excludeId) continue
            if (app.status == "cancelled") continue

            val appStart = timeToMinutes(app.startTime)
            val appEnd = timeToMinutes(app.endTime)

            // Two intervals [startA, endA) and [startB, endB) overlap if startA < endB and startB < endA
            if (startMins < appEnd && appStart < endMins) {
                return@withContext true
            }
        }
        return@withContext false
    }

    private fun timeToMinutes(time: String): Int {
        val parts = time.split(":")
        if (parts.size != 2) return 0
        return (parts[0].toIntOrNull() ?: 0) * 60 + (parts[1].toIntOrNull() ?: 0)
    }

    suspend fun checkAndSeedIfNeeded() = withContext(Dispatchers.IO) {
        try {
            val existingServices = serviceDao.getAllServices().first()
            if (existingServices.isEmpty()) {
                AppDatabase.seedInitialData(database)
            }
        } catch (_: Exception) {
            // Seeding fallback
        }
    }
}
