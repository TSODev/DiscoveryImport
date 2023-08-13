package utils.logging

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal
import java.time.Duration.between
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


enum class Level(val level:Int, val color: TextColors) {
    ALL(0,TextColors.white),
    TRACE(1,TextColors.white),
    DEBUG(2,TextColors.brightCyan),
    INFO(3, TextColors.green),
    WARN(4, TextColors.green),
    ERROR(5, TextColors.magenta),
    FATAL(6, TextColors.red),
    OFF(10, TextColors.black)}

enum class Tag(val value:String) {
    DATE("%Date%"),
    TIME("%Time%"),
    DATETIME("%DateTime%"),
    DURATION("%Duration%")
}
object TLogger {
    private val t = Terminal()
    private var runLevel: Level = Level.ALL
    private var prefix = mutableListOf<String>()
    private var currentDateTime = LocalDateTime.now()

    fun getRunLevel(): Int = this.runLevel.level

    fun setRunLevel(level: Level) {
        this.runLevel = level
    }

    fun getPrefix(): List<String> = prefix

    fun addToPrefix(prefixSupplement: String) {
        prefix.add("$prefixSupplement ")
    }

    fun addDateTimeToPrefix() {
        prefix.add(Tag.DATETIME.value)
    }

    fun addDateToPrefix() {
        prefix.add(Tag.DATE.value)
    }

    fun addTimeToPrefix() {
        prefix.add(Tag.TIME.value)
    }

    fun addDurationToPrefix(){
        prefix.add(Tag.DURATION.value)
    }

    fun removeToPrefix(prefixSupplement: String) {
        prefix.remove("$prefixSupplement ")
    }

    private fun log(level:Level,  message: String, addCRLF: Boolean) {
        val toPrint = mutableListOf<String>()

        val    duration = between(/* startInclusive = */ currentDateTime, /* endExclusive = */ LocalDateTime.now())

        val dt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy hh:mm:ss.SSS"))
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
        val time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss.SSS"))

        prefix.forEach { p ->
            when (p) {
                Tag.DATETIME.value -> toPrint.add("[$dt] ")
                Tag.DATE.value -> toPrint.add("[$date] ")
                Tag.TIME.value -> toPrint.add("[$time] ")
                Tag.DURATION.value -> toPrint.add("[${duration.toMillis()}ms] ")
                else -> {
                    toPrint.add(p)
                }
            }
        }
        val data = toPrint.joinToString(" ") + message
        if (runLevel <= level)
            if (addCRLF)
                t.println(level.color(data))
            else
                t.print(level.color(message))
    }
    fun all(message: String, addCRLF: Boolean = true) {
        log(Level.ALL, message, addCRLF)
    }

    fun trace(message: String, addCRLF: Boolean = true) {
        log(Level.TRACE, message, addCRLF)
    }

    fun info(message: String, addCRLF: Boolean = true) {
        log(Level.INFO, message, addCRLF)
    }

    fun warn(message: String, addCRLF: Boolean = true) {
        log(Level.WARN, message, addCRLF)
    }

    fun debug(message: String,addCRLF: Boolean = true) {
        log(Level.DEBUG, message, addCRLF)
    }
    fun error(message: String,addCRLF: Boolean = true) {
        log(Level.ERROR, message, addCRLF)
    }

    fun fatal(message: String, addCRLF: Boolean = true) {
        log(Level.FATAL, message, addCRLF)
    }



}

