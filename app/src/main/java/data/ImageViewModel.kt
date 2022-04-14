package data

import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 *
 */
class ImageViewModel(private val repository: ImageRepository) : ViewModel() {

    /**
     *
     */
    val allImage: LiveData<List<ImageData>> = repository.allImage.asLiveData()


    /**
     *
     */
    fun insert(imageData: ImageData): Job = viewModelScope.launch {
        repository.insert(imageData)
    }
}
/**
 *
 */
class ImageViewModelFactory(private val repository: ImageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ImageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}