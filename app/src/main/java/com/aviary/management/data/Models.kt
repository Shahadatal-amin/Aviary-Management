package com.aviary.management.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "aviaries",
    indices = [Index(value = ["name"], unique = true)]
)
data class Aviary(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val location: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "birds",
    foreignKeys = [
        ForeignKey(
            entity = Aviary::class,
            parentColumns = ["id"],
            childColumns = ["aviaryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["aviaryId"]) ]
)
data class Bird(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val species: String,
    val mutation: String = "Normal",
    val aviaryId: Long,
    val bandId: String = "",
    val dob: Long? = null,
    val notes: String = "",
    val photoUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "health_entries")
data class HealthEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val birdId: Long,
    val title: String,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "feeding_entries")
data class FeedingEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val birdId: Long,
    val title: String,
    val amount: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
