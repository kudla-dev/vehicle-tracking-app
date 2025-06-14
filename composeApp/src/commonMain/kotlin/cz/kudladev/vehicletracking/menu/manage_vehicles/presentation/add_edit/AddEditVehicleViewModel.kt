package cz.kudladev.vehicletracking.menu.manage_vehicles.presentation.add_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.kudladev.vehicletracking.core.data.image.ImageService
import cz.kudladev.vehicletracking.core.data.models.image.Image
import cz.kudladev.vehicletracking.core.data.models.image.ImageWithBytes
import cz.kudladev.vehicletracking.core.data.models.image.ImageWithUrl
import cz.kudladev.vehicletracking.core.domain.BrandRepository
import cz.kudladev.vehicletracking.core.domain.VehicleRepository
import cz.kudladev.vehicletracking.core.domain.models.Brand
import cz.kudladev.vehicletracking.menu.manage_vehicles.domain.*
import cz.kudladev.vehicletracking.network.onError
import cz.kudladev.vehicletracking.network.onSuccess
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddEditVehicleViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val brandRepository: BrandRepository,
    private val vehicleRepository: VehicleRepository,
    private val imageService: ImageService,
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

    val uploadStatus = imageService.getUploadStatus()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                getBrands()
                imageService.clearUploadStatus()
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
                val driverLicense = _state.value.driverLicense
                val place = _state.value.place
                val brand = _state.value.brand?.name ?: ""
                val images = _state.value.images
                val type = _state.value.type
                val subType = _state.value.subType

                if (validate(
                        fullName,
                        name,
                        model,
                        spz,
                        year,
                        color,
                        driverLicense,
                        place,
                        brand,
                        images,
                    )
                ) {
                    createVehicle(
                        fullName = fullName,
                        model = model,
                        spz = spz,
                        transferableSpz = _state.value.transferableSpz,
                        year = year,
                        color = color,
                        place = place,
                        brand = _state.value.brand ?: Brand(0, "", ""),
                        driverLicense = _state.value.driverLicense,
                        totalDistance = _state.value.totalDistance.toIntOrNull() ?: 0,
                        maximumDistance = _state.value.maximumDistance.toIntOrNull() ?: 0,
                        images = images,
                    )
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
                newImages.add(ImageWithBytes(bytes = action.byteArray, position = newImages.size))
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
                    images = newImages.mapIndexed { index, image ->
                        when (image) {
                            is ImageWithBytes -> image.copy(position = index)
                            is ImageWithUrl -> image.copy(position = index)
                            else -> image
                        }
                    }
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
            AddEditVehicleAction.ClearRecentUploads -> {
                imageService.clearUploadStatus()
            }

            is AddEditVehicleAction.OnImagesReordered -> {
                _state.update { it.copy(
                    images = action.images.mapIndexed { index, image ->
                        when (image) {
                            is ImageWithBytes -> image.copy(position = index)
                            is ImageWithUrl -> image.copy(position = index)
                            else -> image
                        }
                    }
                ) }
            }

            AddEditVehicleAction.ToggleDriverLicenseDialog -> {
                _state.update { it.copy(
                    selectingDriverLicense = !_state.value.selectingDriverLicense
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
        driverLicense: String,
        place: String,
        brand: String,
        images: List<Image>,
    ): Boolean {
        val isFullNameValid = fullNameValidator.execute(fullName)
        val isModelValid = modelValidator.execute(model)
        val isSpzValid = spzValidator.execute(spz)
        val isYearValid = yearValidator.execute(year)
        val isColorValid = colorValidator.execute(color)
        val isPlaceValid = placeValidator.execute(place)
        val isBrandValid = brandValidator.execute(brand)

        val result = listOf(
            isFullNameValid,
            isModelValid,
            isSpzValid,
            isYearValid,
            isColorValid,
            isPlaceValid,
            isBrandValid
        ).any { it.isSuccessful }

        _state.update { it.copy(
            fullNameError = if (isFullNameValid.isSuccessful) null else isFullNameValid.errorMessage,
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
        } else if(driverLicense.isBlank()){
            _state.update { it.copy(
                driverLicenseError = "Please enter a valid driving license"
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
                    images = response.imageUrls.mapIndexed { index, url ->
                        ImageWithUrl(url = url, position = index)
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

    private fun createVehicle(
        fullName: String,
        model: String,
        spz: String,
        transferableSpz: Boolean,
        year: String,
        color: String,
        place: String,
        brand: Brand,
        driverLicense: String,
        totalDistance: Int,
        maximumDistance: Int,
        images: List<Image>,
    ) = viewModelScope.launch {
        vehicleRepository
            .createVehicle(
                fullName = fullName,
                brandId = brand.id,
                model = model,
                year = year,
                color = color,
                spz = spz,
                transferableSpz = transferableSpz,
                totalDistance = totalDistance,
                maximumDistance = maximumDistance,
                driverLicense = driverLicense,
                place = place,
            )
            .onSuccess { response ->
                images.forEach { image ->
                    when (image) {
                        is ImageWithBytes -> {
                            imageService
                                .enqueueBackgroundUpload(
                                    image.bytes ?: ByteArray(0),
                                    response.id?.toLong() ?: 0L,
                                    position = image.position
                                )
                        }
                        is ImageWithUrl -> {
                            imageService
                                .enqueueBackgroundUpload(
                                    image.url ?: "",
                                    response.id?.toLong() ?: 0L,
                                    position = image.position
                                )
                        }
                    }
                }
            }
            .onError { error ->
                _state.update { it.copy(
                    resultState = AddEditResultState.Error(error),
                ) }
            }
    }


}