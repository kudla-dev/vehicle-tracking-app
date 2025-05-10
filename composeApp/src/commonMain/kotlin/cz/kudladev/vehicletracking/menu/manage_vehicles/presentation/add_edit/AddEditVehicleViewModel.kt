package cz.kudladev.vehicletracking.menu.manage_vehicles.presentation.add_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.kudladev.vehicletracking.core.data.models.image.Image
import cz.kudladev.vehicletracking.core.data.models.image.ImageWithBytes
import cz.kudladev.vehicletracking.core.data.models.image.ImageWithUrl
import cz.kudladev.vehicletracking.core.domain.BrandRepository
import cz.kudladev.vehicletracking.core.domain.VehicleRepository
import cz.kudladev.vehicletracking.menu.manage_vehicles.domain.BrandValidator
import cz.kudladev.vehicletracking.menu.manage_vehicles.domain.ColorValidator
import cz.kudladev.vehicletracking.menu.manage_vehicles.domain.FullNameValidator
import cz.kudladev.vehicletracking.menu.manage_vehicles.domain.ModelValidator
import cz.kudladev.vehicletracking.menu.manage_vehicles.domain.NameValidator
import cz.kudladev.vehicletracking.menu.manage_vehicles.domain.PlaceValidator
import cz.kudladev.vehicletracking.menu.manage_vehicles.domain.SpzValidator
import cz.kudladev.vehicletracking.menu.manage_vehicles.domain.YearValidator
import cz.kudladev.vehicletracking.network.onError
import cz.kudladev.vehicletracking.network.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddEditVehicleViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val brandRepository: BrandRepository,
    private val vehicleRepository: VehicleRepository,
    private val fullNameValidator: FullNameValidator,
    private val nameValidator: NameValidator,
    private val modelValidator: ModelValidator,
    private val spzValidator: SpzValidator,
    private val yearValidator: YearValidator,
    private val colorValidator: ColorValidator,
    private val placeValidator: PlaceValidator,
    private val brandValidator: BrandValidator
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(AddEditVehicleState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                getBrands()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = AddEditVehicleState()
        )

    fun onAction(action: AddEditVehicleAction) {
        when (action) {
            is AddEditVehicleAction.OnColorChange -> {
                _state.update { it.copy(
                    color = action.color
                ) }
            }
            is AddEditVehicleAction.OnDriverLicenseChange -> {
                _state.update { it.copy(
                    driverLicense = action.driverLicense
                ) }
            }
            is AddEditVehicleAction.OnFullNameChange -> {
                _state.update { it.copy(
                    fullName = action.fullName
                ) }
            }
            is AddEditVehicleAction.OnMaximumDistanceChange -> {
                _state.update { it.copy(
                    maximumDistance = action.maximumDistance
                ) }
            }
            is AddEditVehicleAction.OnModelChange -> {
                _state.update { it.copy(
                    model = action.model
                ) }
            }
            is AddEditVehicleAction.OnNameChange -> {
                _state.update { it.copy(
                    name = action.name
                ) }
            }
            is AddEditVehicleAction.OnPlaceChange -> {
                _state.update { it.copy(
                    place = action.place
                ) }
            }
            AddEditVehicleAction.OnSaveClick -> {
                val fullName = _state.value.fullName
                val name = _state.value.name
                val model = _state.value.model
                val spz = _state.value.spz
                val year = _state.value.year
                val color = _state.value.color
                val place = _state.value.place
                val brand = _state.value.brand?.name ?: ""
                val images = _state.value.images

                if (validate(
                        fullName,
                        name,
                        model,
                        spz,
                        year,
                        color,
                        place,
                        brand,
                        images
                    )
                ) {
                    println("Validation successful")
                }
            }
            is AddEditVehicleAction.OnSpzChange -> {
                _state.update { it.copy(
                    spz = action.spz
                ) }
            }
            is AddEditVehicleAction.OnTotalDistanceChange -> {
                _state.update { it.copy(
                    totalDistance = action.totalDistance
                ) }
            }
            AddEditVehicleAction.OnTransferableSpzChange -> {
                _state.update { it.copy(
                    transferableSpz = !_state.value.transferableSpz
                ) }
            }
            is AddEditVehicleAction.OnYearChange -> {
                _state.update { it.copy(
                    year = action.year
                ) }
            }

            is AddEditVehicleAction.OnVehicleURLChange -> {
                _state.update { it.copy(
                    vehicleURL = action.motorcycleURL
                ) }
            }

            AddEditVehicleAction.ToggleImageDialog -> {
                _state.update { it.copy(
                    imageDialog = !_state.value.imageDialog
                ) }
            }

            is AddEditVehicleAction.OnNewImageAdd -> {
                val newImages = _state.value.images.toMutableList()
                newImages.add(ImageWithBytes(bytes = action.byteArray))
                _state.update { it.copy(
                    images = newImages
                ) }
            }

            is AddEditVehicleAction.OnImageClick -> {

            }
            is AddEditVehicleAction.OnImageRemove -> {
                val newImages = _state.value.images.toMutableList()
                newImages.removeAt(action.index)
                _state.update { it.copy(
                    images = newImages
                ) }
            }

            AddEditVehicleAction.OnVehicleURLConfirm -> {
                getVehicleImageFromUrl(_state.value.vehicleURL)
            }

            is AddEditVehicleAction.OnBrandChange -> {
                _state.update { it.copy(
                    brand = action.brand,
                ) }
            }
            AddEditVehicleAction.ToggleBrandDialog -> {
                _state.update { it.copy(
                    selectingBrand = !_state.value.selectingBrand
                ) }
            }

            AddEditVehicleAction.TogglePlaceDialog -> {
                _state.update { it.copy(
                    selectingPlace = !_state.value.selectingPlace
                ) }
            }
        }
    }

    private fun validate(
        fullName: String,
        name: String,
        model: String,
        spz: String,
        year: String,
        color: String,
        place: String,
        brand: String,
        images: List<Image>,
    ): Boolean {
        val isFullNameValid = fullNameValidator.execute(fullName)
        val isNameValid = nameValidator.execute(name)
        val isModelValid = modelValidator.execute(model)
        val isSpzValid = spzValidator.execute(spz)
        val isYearValid = yearValidator.execute(year)
        val isColorValid = colorValidator.execute(color)
        val isPlaceValid = placeValidator.execute(place)
        val isBrandValid = brandValidator.execute(brand)

        val result = listOf(
            isFullNameValid,
            isNameValid,
            isModelValid,
            isSpzValid,
            isYearValid,
            isColorValid,
            isPlaceValid,
            isBrandValid
        ).any { it.isSuccessful }

        _state.update { it.copy(
            fullNameError = if (isFullNameValid.isSuccessful) null else isFullNameValid.errorMessage,
            nameError = if (isNameValid.isSuccessful) null else isNameValid.errorMessage,
            modelError = if (isModelValid.isSuccessful) null else isModelValid.errorMessage,
            spzError = if (isSpzValid.isSuccessful) null else isSpzValid.errorMessage,
            yearError = if (isYearValid.isSuccessful) null else isYearValid.errorMessage,
            colorError = if (isColorValid.isSuccessful) null else isColorValid.errorMessage,
        ) }

        if (images.isEmpty()){
            _state.update { it.copy(
                imagesError = "Please select at least one image"
            ) }
            return false
        } else {
            _state.update { it.copy(
                imagesError = null
            ) }
        }

        return result
    }

    private fun getVehicleImageFromUrl(url: String) = viewModelScope.launch {
        vehicleRepository
            .scrape(url)
            .onSuccess { response ->
                _state.update { it.copy(
                    images = response.imageUrls.map { url ->
                        ImageWithUrl(url = url)
                    },
                    fullName = response.fullName,
                    driverLicense = response.drivingLicenses.joinToString(", ")
                ) }
            }
            .onError { error ->
                _state.update { it.copy(
                    vehicleURLError = error
                ) }
            }
    }

    private fun getBrands() = viewModelScope.launch {
        brandRepository
            .getBrands()
            .onSuccess { brands ->
                _state.update { it.copy(
                    brands = brands,
                    brand = brands.firstOrNull(),
                ) }
            }
            .onError { error ->
                _state.update { it.copy(
                    brandError = error
                ) }
            }
    }

}