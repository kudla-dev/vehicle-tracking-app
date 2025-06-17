package cz.kudladev.vehicletracking.core.datastore

expect object AppContext {
    fun set(context: Any?)
    fun get(): Any?
}