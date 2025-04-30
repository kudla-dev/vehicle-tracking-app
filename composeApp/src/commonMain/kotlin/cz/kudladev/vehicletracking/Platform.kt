package cz.kudladev.vehicletracking

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform