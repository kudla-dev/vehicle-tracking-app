package cz.kudladev.vehicletracking.app

expect object AppContext {
    fun set(context: Any?)
    fun get(): Any?
}