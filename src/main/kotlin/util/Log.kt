package util

import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger


object Log {
    val logger: Logger = Logger.getLogger("com.develogica.heelel_desk")
    var IsEnabled = true
    var MinLevel: Level = Level.INFO


    inline fun info(tag: String? = null, msg: () -> String) = log(Level.INFO, tag, msg)
    inline fun warn(tag: String? = null, msg: () -> String) = log(Level.WARNING, tag, msg)
    inline fun debug(tag: String? = null, msg: () -> String) = log(Level.FINE, tag, msg)
    inline fun error(tag: String? = null, msg: () -> String) = log(Level.SEVERE, tag, msg)

    inline fun log(level: Level, tag: String?, msg: () -> String) {
        if (IsEnabled.not() || level.intValue() < MinLevel.intValue()) return

        val tagStr = tag?.let { "[$it] " } ?: ""
        val message = tagStr + msg()

        logger.log(level, message)
    }
}
