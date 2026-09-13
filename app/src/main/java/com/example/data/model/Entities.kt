package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String = "",
    val email: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "services")
data class ServiceItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val durationMinutes: Int = 30,
    val price: Double = 0.0,
    val category: String = "Geral",
    val color: String = "#4F46E5"
)

@Entity(
    tableName = "appointments",
    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ServiceItem::class,
            parentColumns = ["id"],
            childColumns = ["serviceId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("clientId"),
        Index("serviceId"),
        Index("date")
    ]
)
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientId: Long? = null,
    val clientName: String,
    val clientPhone: String = "",
    val serviceId: Long? = null,
    val serviceName: String,
    val price: Double,
    val currency: String = "R$",
    val date: String,          // Format: YYYY-MM-DD
    val startTime: String,     // Format: HH:MM
    val endTime: String,       // Format: HH:MM
    val durationMinutes: Int,
    val status: String = "scheduled", // 'scheduled', 'completed', 'cancelled'
    val reminder: String = "none",    // 'none', '15m', '30m', '1h', '1d'
    val notes: String = "",
    val recurrence: String = "none",  // 'none', 'daily', 'weekly', 'biweekly', 'monthly'
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "working_hours")
data class WorkingHours(
    @PrimaryKey
    val id: Long = 1,
    val startHour: Int = 8,
    val endHour: Int = 18,
    val intervalMinutes: Int = 60,
    val workingDays: String = "1,2,3,4,5,6" // 1=Mon, 7=Sun
)
