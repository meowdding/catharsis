package me.owdding.catharsis.utils

import net.fabricmc.loader.api.FabricLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface CatharsisLogger {

    companion object Companion {
        internal val STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
        fun named(name: String): CatharsisLogger = LoggerImpl(LoggerFactory.getLogger(name))
        fun autoResolve(): CatharsisLogger = LoggerImpl(LoggerFactory.getLogger(STACK_WALKER.callerClass.simpleName))

        fun CatharsisLogger.featureLogger(): CatharsisLogger {
            val name = STACK_WALKER.callerClass.let { it.annotations.filterIsInstance<FeatureName>().firstOrNull()?.name ?: it.simpleName }

            return featureLogger(name)
        }
    }

    fun featureLogger(name: String): CatharsisLogger

    fun trace(message: String) {
        trace(message, null, null)
    }

    fun trace(message: String, throwable: Throwable) {
        trace(message, throwable, null)
    }

    fun trace(message: String, prefix: String) {
        trace(message, null, prefix)
    }

    fun trace(message: String, throwable: Throwable? = null, prefix: String? = null)

    fun debug(message: String) {
        debug(message, null, null)
    }

    fun debug(message: String, throwable: Throwable) {
        debug(message, throwable, null)
    }

    fun debug(message: String, prefix: String) {
        debug(message, null, prefix)
    }

    fun debug(message: String, throwable: Throwable? = null, prefix: String? = null)

    fun info(message: String) {
        info(message, null, null)
    }

    fun info(message: String, throwable: Throwable) {
        info(message, throwable, null)
    }

    fun info(message: String, prefix: String) {
        info(message, null, prefix)
    }

    fun info(message: String, throwable: Throwable? = null, prefix: String? = null)

    fun warn(message: String) {
        warn(message, null, null)
    }

    fun warn(message: String, throwable: Throwable) {
        warn(message, throwable, null)
    }

    fun warn(message: String, prefix: String) {
        warn(message, null, prefix)
    }

    fun warn(message: String, throwable: Throwable? = null, prefix: String? = null)

    fun error(message: String) {
        error(message, null, null)
    }

    fun error(message: String, throwable: Throwable) {
        error(message, throwable, null)
    }

    fun error(message: String, prefix: String) {
        error(message, null, prefix)
    }

    fun error(message: String, throwable: Throwable? = null, prefix: String? = null)

}

annotation class FeatureName(val name: String)

internal data class LoggerImpl(val logger: Logger, val defaultPrefix: String? = null) : CatharsisLogger {
    fun mergedPrefix(prefix: String?) = listOfNotNull(defaultPrefix, prefix).joinToString("/")

    private fun wrapMessage(message: String, prefix: String?): String {
        val prefix = mergedPrefix(prefix).takeUnless { it.isBlank() }
        if (FabricLoader.getInstance().isDevelopmentEnvironment) {
            return "${prefix?.let { "<$it> " } ?: ""}$message"
        } else {
            return "[${logger.name}${prefix?.let { "/$it" } ?: ""}] $message"
        }
    }

    private fun log(message: String, throwable: Throwable?, prefix: String?, noError: (String) -> Unit, error: (String, Throwable) -> Unit) {
        val message = wrapMessage(message, prefix)
        if (throwable != null) {
            error(message, throwable)
        } else {
            noError(message)
        }
    }

    override fun featureLogger(name: String): CatharsisLogger {
        return LoggerImpl(logger, mergedPrefix(name))
    }

    override fun trace(message: String, throwable: Throwable?, prefix: String?) = log(message, throwable, prefix, logger::trace, logger::trace)

    override fun debug(message: String, throwable: Throwable?, prefix: String?) = log(message, throwable, prefix, logger::debug, logger::debug)

    override fun info(message: String, throwable: Throwable?, prefix: String?) = log(message, throwable, prefix, logger::info, logger::info)

    override fun warn(message: String, throwable: Throwable?, prefix: String?) = log(message, throwable, prefix, logger::warn, logger::warn)

    override fun error(message: String, throwable: Throwable?, prefix: String?) = log(message, throwable, prefix, logger::error, logger::error)

}
