package cz.kudladev.vehicletracking.app

import android.content.Context
import java.lang.ref.WeakReference

actual object AppContext {
    private var value: WeakReference<Context?>? = null

    actual fun set(context: Any?) {
        value = WeakReference(context as? Context)
    }

    actual fun get(): Any? {
        return value?.get() ?: throw RuntimeException("Context Error")
    }
}