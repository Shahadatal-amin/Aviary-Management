package com.aviary.management.util

import android.content.Context
import android.net.Uri
import com.aviary.management.data.Aviary
import com.aviary.management.data.Bird
import com.aviary.management.data.DatabaseProvider
import kotlinx.coroutines.flow.firstOrNull

object CsvImporter {

    // Very small CSV parser supporting quoted fields
    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var i = 0
        val sb = StringBuilder()
        var inQuotes = false
        while (i < line.length) {
            val c = line[i]
            when {
                c == '"' -> {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                        // escaped quote
                        sb.append('"')
                        i++
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                c == ',' && !inQuotes -> {
                    result.add(sb.toString())
                    sb.clear()
                }
                else -> sb.append(c)
            }
            i++
        }
        result.add(sb.toString())
        return result
    }

    suspend fun importBirdsFromCsv(context: Context, uri: Uri): Int {
        val db = DatabaseProvider.get(context)
        val resolver = context.contentResolver
        val stream = resolver.openInputStream(uri) ?: return 0
        stream.bufferedReader().useLines { lines ->
            val iterator = lines.iterator()
            if (!iterator.hasNext()) return 0
            // assume header present
            iterator.next()

            // Ensure at least one aviary exists to assign imported birds
            var defaultAviaryId = db.aviaryDao().observeAll().firstOrNull()?.firstOrNull()?.id
                ?: db.aviaryDao().insert(Aviary(name = "Imported Aviary"))

            var count = 0
            while (iterator.hasNext()) {
                val raw = iterator.next()
                if (raw.isBlank()) continue
                val fields = parseCsvLine(raw)
                if (fields.size < 10) continue
                try {
                    val name = fields[1]
                    val species = fields[2]
                    val mutation = fields[3].ifBlank { "Normal" }
                    val aviaryId = fields[4].toLongOrNull() ?: defaultAviaryId
                    val bandId = fields[5]
                    val dob = fields[6].toLongOrNull()
                    val notes = fields[7]
                    val photoUri = fields[8].ifBlank { null }
                    val createdAt = fields[9].toLongOrNull() ?: System.currentTimeMillis()

                    // If aviaryId doesn't exist locally, use defaultAviaryId
                    // (we avoid creating aviaries with specific IDs because Room auto-generates keys)

                    val bird = Bird(
                        name = name,
                        species = species,
                        mutation = mutation,
                        aviaryId = aviaryId,
                        bandId = bandId,
                        dob = dob,
                        notes = notes,
                        photoUri = photoUri,
                        createdAt = createdAt
                    )
                    db.birdDao().insert(bird)
                    count++
                } catch (_: Exception) {
                    // skip malformed rows
                }
            }
            return count
        }
    }
}
