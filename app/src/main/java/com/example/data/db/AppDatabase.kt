package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Appointment
import com.example.data.model.Client
import com.example.data.model.ServiceItem
import com.example.data.model.WorkingHours
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        Client::class,
        ServiceItem::class,
        Appointment::class,
        WorkingHours::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun serviceDao(): ServiceDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun workingHoursDao(): WorkingHoursDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agendamentos.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Seed default data
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                seedInitialData(database)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun seedInitialData(database: AppDatabase) {
            val workingHoursDao = database.workingHoursDao()
            val serviceDao = database.serviceDao()
            val clientDao = database.clientDao()
            val appointmentDao = database.appointmentDao()

            workingHoursDao.setWorkingHours(
                WorkingHours(
                    id = 1,
                    startHour = 8,
                    endHour = 18,
                    intervalMinutes = 60,
                    workingDays = "1,2,3,4,5,6"
                )
            )

            // Seed Services
            val s1 = serviceDao.insertService(
                ServiceItem(
                    name = "Corte & Barba Completo",
                    durationMinutes = 60,
                    price = 85.0,
                    category = "Barbearia",
                    color = "#4F46E5"
                )
            )
            val s2 = serviceDao.insertService(
                ServiceItem(
                    name = "Corte Masculino Degradê",
                    durationMinutes = 45,
                    price = 50.0,
                    category = "Cabelo",
                    color = "#2563EB"
                )
            )
            val s3 = serviceDao.insertService(
                ServiceItem(
                    name = "Barboterapia / Toalha Quente",
                    durationMinutes = 30,
                    price = 45.0,
                    category = "Barbearia",
                    color = "#059669"
                )
            )
            val s4 = serviceDao.insertService(
                ServiceItem(
                    name = "Design de Sobrancelha",
                    durationMinutes = 20,
                    price = 35.0,
                    category = "Estética",
                    color = "#D97706"
                )
            )
            val s5 = serviceDao.insertService(
                ServiceItem(
                    name = "Limpeza de Pele Express",
                    durationMinutes = 45,
                    price = 120.0,
                    category = "Estética",
                    color = "#7C3AED"
                )
            )

            // Seed Clients
            val c1 = clientDao.insertClient(
                Client(
                    name = "Carlos Eduardo Lima",
                    phone = "5511987654321",
                    email = "carlos.lima@email.com",
                    notes = "Prefere corte com tesoura no topo e degradê navalhado."
                )
            )
            val c2 = clientDao.insertClient(
                Client(
                    name = "Mariana Silva Rocha",
                    phone = "5511976543210",
                    email = "mariana.silva@email.com",
                    notes = "Cliente pontual, gosta de café expresso."
                )
            )
            val c3 = clientDao.insertClient(
                Client(
                    name = "Rafael Santos Mendes",
                    phone = "5521998877665",
                    email = "rafael.santos@email.com",
                    notes = "Agendamento frequente às terças-feiras."
                )
            )
            val c4 = clientDao.insertClient(
                Client(
                    name = "Beatriz Costa Alencar",
                    phone = "5531988776655",
                    email = "beatriz.costa@email.com",
                    notes = "Pele sensível."
                )
            )

            // Dates: Today, Tomorrow, Day after
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val cal = Calendar.getInstance()

            val todayStr = dateFormat.format(cal.time)

            cal.add(Calendar.DAY_OF_YEAR, 1)
            val tomorrowStr = dateFormat.format(cal.time)

            cal.add(Calendar.DAY_OF_YEAR, 1)
            val dayAfterStr = dateFormat.format(cal.time)

            cal.add(Calendar.DAY_OF_YEAR, -3)
            val yesterdayStr = dateFormat.format(cal.time)

            // Appointments for Today
            appointmentDao.insertAppointment(
                Appointment(
                    clientId = c1,
                    clientName = "Carlos Eduardo Lima",
                    clientPhone = "5511987654321",
                    serviceId = s1,
                    serviceName = "Corte & Barba Completo",
                    price = 85.0,
                    date = todayStr,
                    startTime = "09:00",
                    endTime = "10:00",
                    durationMinutes = 60,
                    status = "completed",
                    notes = "Pagamento via PIX"
                )
            )

            appointmentDao.insertAppointment(
                Appointment(
                    clientId = c2,
                    clientName = "Mariana Silva Rocha",
                    clientPhone = "5511976543210",
                    serviceId = s5,
                    serviceName = "Limpeza de Pele Express",
                    price = 120.0,
                    date = todayStr,
                    startTime = "11:00",
                    endTime = "11:45",
                    durationMinutes = 45,
                    status = "scheduled",
                    notes = "Primeira sessão"
                )
            )

            appointmentDao.insertAppointment(
                Appointment(
                    clientId = c3,
                    clientName = "Rafael Santos Mendes",
                    clientPhone = "5521998877665",
                    serviceId = s2,
                    serviceName = "Corte Masculino Degradê",
                    price = 50.0,
                    date = todayStr,
                    startTime = "14:00",
                    endTime = "14:45",
                    durationMinutes = 45,
                    status = "scheduled",
                    notes = ""
                )
            )

            appointmentDao.insertAppointment(
                Appointment(
                    clientId = c4,
                    clientName = "Beatriz Costa Alencar",
                    clientPhone = "5531988776655",
                    serviceId = s4,
                    serviceName = "Design de Sobrancelha",
                    price = 35.0,
                    date = todayStr,
                    startTime = "16:00",
                    endTime = "16:20",
                    durationMinutes = 20,
                    status = "scheduled",
                    notes = ""
                )
            )

            // Tomorrow appointments
            appointmentDao.insertAppointment(
                Appointment(
                    clientId = c1,
                    clientName = "Carlos Eduardo Lima",
                    clientPhone = "5511987654321",
                    serviceId = s3,
                    serviceName = "Barboterapia / Toalha Quente",
                    price = 45.0,
                    date = tomorrowStr,
                    startTime = "10:00",
                    endTime = "10:30",
                    durationMinutes = 30,
                    status = "scheduled"
                )
            )

            appointmentDao.insertAppointment(
                Appointment(
                    clientId = c3,
                    clientName = "Rafael Santos Mendes",
                    clientPhone = "5521998877665",
                    serviceId = s1,
                    serviceName = "Corte & Barba Completo",
                    price = 85.0,
                    date = tomorrowStr,
                    startTime = "15:00",
                    endTime = "16:00",
                    durationMinutes = 60,
                    status = "scheduled"
                )
            )

            // Yesterday appointment
            appointmentDao.insertAppointment(
                Appointment(
                    clientId = c2,
                    clientName = "Mariana Silva Rocha",
                    clientPhone = "5511976543210",
                    serviceId = s4,
                    serviceName = "Design de Sobrancelha",
                    price = 35.0,
                    date = yesterdayStr,
                    startTime = "14:00",
                    endTime = "14:20",
                    durationMinutes = 20,
                    status = "completed"
                )
            )
        }
    }
}
