package com.mfreimueller.frooty.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

fun LocalDate.isSameWeek(otherDate: LocalDate): Boolean {
    val weekFields = WeekFields.of(Locale.getDefault())
    return get(weekFields.weekOfWeekBasedYear()) == otherDate.get(weekFields.weekOfWeekBasedYear()) &&
            year == otherDate.year
}

val LocalDate.weekAndYearString: String
    get() {
        val weekFields = WeekFields.of(Locale.getDefault())
        val weekOfYear = get(weekFields.weekOfWeekBasedYear())

        return "KW $weekOfYear - $year" // TODO translation
    }

val LocalDate.weekday: String
    get() {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> return "Montag"
            DayOfWeek.TUESDAY -> return "Dienstag"
            DayOfWeek.WEDNESDAY -> return "Mittwoch"
            DayOfWeek.THURSDAY -> return "Donnerstag"
            DayOfWeek.FRIDAY -> return "Freitag"
            DayOfWeek.SATURDAY -> return "Samstag"
            DayOfWeek.SUNDAY -> return "Sonntag"
        }
    }

fun LocalDate.compareWeek(otherDate: LocalDate): Int {
    if (year - otherDate.year != 0) {
        return year - otherDate.year
    }

    val weekFields = WeekFields.of(Locale.getDefault())
    val thisWeek = get(weekFields.weekOfWeekBasedYear())
    val otherWeek = otherDate.get(weekFields.weekOfWeekBasedYear())
    return thisWeek - otherWeek
}

/**
 * Returns the last start of the week, for example, if today
 * is Saturday and the week starts at Monday, the LocalDate
 * for last Monday is returned.
 */
val LocalDate.lastStartOfWeek: LocalDate
    get() {
        val weekFields = WeekFields.of(Locale.getDefault())
        return with(TemporalAdjusters.previousOrSame(weekFields.firstDayOfWeek))
    }

/**
 * Returns the next start of the week, for example, if today
 * is Saturday and the week starts at Monday, the LocalDate
 * for next Monday is returned.
 */
val LocalDate.nextStartOfWeek: LocalDate
    get() {
        val weekFields = WeekFields.of(Locale.getDefault())
        return with(TemporalAdjusters.next(weekFields.firstDayOfWeek))
    }

val LocalDate.formattedString: String
    get() = format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));