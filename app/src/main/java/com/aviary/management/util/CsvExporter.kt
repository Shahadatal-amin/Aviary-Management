package com.aviary.management.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object CsvExporter {
    fun exportBirdsCsv(context: Context, birdsCsvRows: List<String>): File {
        val dir = File(context.cacheDir, "exports")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "birds_${System.currentTimeMillis()}.csv")
        FileOutputStream(file).use { out ->
            out.write("id,name,species,mutation,aviaryId,bandId,dob,notes,photoUri,createdAt\n".toByteArray())
            birdsCsvRows.forEach { row ->
                out.write((row + "\n").toByteArray())
            }
        }
        return file
    }

    fun toCsvRow(id: Long, name: String, species: String, mutation: String, aviaryId: Long, bandId: String, dob: Long?, notes: String, photoUri: String?, createdAt: Long): String {
        val dobVal = dob?.toString() ?: ""
        val photo = photoUri ?: ""
        // escape commas by wrapping fields with quotes where needed
        fun q(s: String) = if (s.contains(",") || s.contains("\n")) "\"${s.replace("\"","\'\'")}\"" else s
        return listOf(id.toString(), q(name), q(species), q(mutation), aviaryId.toString(), q(bandId), dobVal, q(notes), q(photo), createdAt.toString()).joinToString(",")
    }
}
