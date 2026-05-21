package com.masjid.jemaah.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    private val isoParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    
    private val simpleDateParser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val indonesianFormatter = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))

    /**
     * Formats a raw API date string (ISO or YYYY-MM-DD) into a human-readable Indonesian format.
     */
    fun formatToHumanReadable(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""
        try {
            // Extract core part to avoid fractional second parsing issues
            val cleanString = if (dateString.contains("T")) {
                dateString.substringBefore(".")
            } else {
                dateString
            }
            
            val parsedDate = if (cleanString.contains("T")) {
                isoParser.parse(cleanString)
            } else {
                simpleDateParser.parse(cleanString)
            }
            
            return parsedDate?.let { indonesianFormatter.format(it) } ?: dateString
        } catch (e: Exception) {
            return dateString
        }
    }
}
