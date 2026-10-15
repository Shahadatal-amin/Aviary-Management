package com.aviary.management.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Database
import kotlinx.coroutines.flow.Flow

@Dao
interface AviaryDao {
    @Query("SELECT * FROM aviaries ORDER BY name ASC")
    fun observeAll(): Flow<List<Aviary>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(aviary: Aviary): Long

    @Delete
    suspend fun delete(aviary: Aviary)
}

@Dao
interface BirdDao {
    @Query("SELECT * FROM birds ORDER BY name ASC")
    fun observeAll(): Flow<List<Bird>>

    @Query("SELECT * FROM birds WHERE aviaryId = :aviaryId ORDER BY name ASC")
    fun observeByAviary(aviaryId: Long): Flow<List<Bird>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bird: Bird): Long

    @Delete
    suspend fun delete(bird: Bird)
}

@Dao
interface HealthEntryDao {
    @Query("SELECT * FROM health_entries ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<HealthEntry>>

    @Query("SELECT * FROM health_entries WHERE birdId = :birdId ORDER BY timestamp DESC")
    fun observeByBird(birdId: Long): Flow<List<HealthEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: HealthEntry): Long

    @Delete
    suspend fun delete(entry: HealthEntry)
}

@Dao
interface FeedingEntryDao {
    @Query("SELECT * FROM feeding_entries ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<FeedingEntry>>

    @Query("SELECT * FROM feeding_entries WHERE birdId = :birdId ORDER BY timestamp DESC")
    fun observeByBird(birdId: Long): Flow<List<FeedingEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: FeedingEntry): Long

    @Delete
    suspend fun delete(entry: FeedingEntry)
}

@Database(
    entities = [Aviary::class, Bird::class, HealthEntry::class, FeedingEntry::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun aviaryDao(): AviaryDao
    abstract fun birdDao(): BirdDao
    abstract fun healthEntryDao(): HealthEntryDao
    abstract fun feedingEntryDao(): FeedingEntryDao
}
