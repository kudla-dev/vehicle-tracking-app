package cz.kudladev.vehicletracking.feature.onboarding.loading

data class LoadingScreenState(
    val loadingProcess: LoadingProcess = LoadingProcess.Loading,
)


sealed class LoadingProcess {
    object Loading : LoadingProcess()
    object Success : LoadingProcess()
    data class Error(val message: String) : LoadingProcess()
}