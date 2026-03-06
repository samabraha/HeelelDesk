package util

import java.util.logging.Logger

class Log {
    companion object {
        val logger = Logger.getLogger("Heelel_Show")

        fun error(message: String) {
            showLog("ERROR: $message")
        }

        fun info(message: String) {
            showLog("INFO: $message")
        }

        fun debug(message: String) {
            showLog("DEBUG: $message")
        }

        fun fine(message: String) {
            showLog("FINE: $message")
        }

        fun warning(message: String) {
            showLog("WARNING: $message")
        }

        private fun showLog(message: String) {
            logger.info(message)
        }
    }


}