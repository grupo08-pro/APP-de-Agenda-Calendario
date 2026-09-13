package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Appointment
import com.example.data.model.Client
import com.example.data.model.ServiceItem
import com.example.data.model.WorkingHours
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAllClients(): Flow<List<Client>>

    @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
    fun getClientById(id: Long): Flow<Client?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: Client): Long

    @Update
    suspend fun updateClient(client: Client)

    @Delete
    suspend fun deleteClient(client: Client)
}

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services ORDER BY name ASC")
    fun getAllServices(): Flow<List<ServiceItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceItem): Long

    @Update
    suspend fun updateService(service: ServiceItem)

    @Delete
    suspend fun deleteService(service: ServiceItem)
}

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY date ASC, startTime ASC")
    fun getAllAppointments(): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE date = :date ORDER BY startTime ASC")
    fun getAppointmentsByDate(date: String): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE date = :date ORDER BY startTime ASC")
    suspend fun getAppointmentsByDateSync(date: String): List<Appointment>

    @Query("SELECT * FROM appointments WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC, startTime ASC")
    fun getAppointmentsBetweenDates(startDate: String, endDate: String): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE clientId = :clientId ORDER BY date DESC, startTime DESC")
    fun getAppointmentsByClient(clientId: Long): Flow<List<Appointment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: Appointment): Long

    @Update
    suspend fun updateAppointment(appointment: Appointment)

    @Delete
    suspend fun deleteAppointment(appointment: Appointment)

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteAppointmentById(id: Long)
}

@Dao
interface WorkingHoursDao {
    @Query("SELECT * FROM working_hours WHERE id = 1 LIMIT 1")
    fun getWorkingHours(): Flow<WorkingHours?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setWorkingHours(workingHours: WorkingHours)
}
