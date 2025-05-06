package cz.kudladev.vehicletracking.app

actual object AppContext {
    private var value: Any? = null

    actual fun set(context: Any?) {
        value = context
    }

    actual fun get(): Any? {
        return value ?: throw RuntimeException("Context Error")
    }
}