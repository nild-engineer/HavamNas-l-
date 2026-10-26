package com.example.hava1.ui.city

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hava1.data.repository.CityRepository
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CitySearchViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val repository = CityRepository()

    val query = savedStateHandle.getStateFlow("searchQuery", "")

    private val _state = MutableStateFlow<CitySearchState>(CitySearchState.Idle)

    val state = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        startSearch(query.value)
    }

    fun updateQuery(value: String) {
        if (value == query.value) return

        savedStateHandle["searchQuery"] = value
        startSearch(value)
    }

    fun retry() {
        startSearch(query.value, immediately = true)
    }

    private fun startSearch(
        value: String,
        immediately: Boolean = false,
    ) {
        searchJob?.cancel()

        val searchText = value.trim()

        if (searchText.length < 2) {
            _state.value = CitySearchState.Idle
            return
        }

        _state.value = CitySearchState.Loading

        searchJob = viewModelScope.launch {
            try {
                // Her tuşta istek göndermek yerine yazmanın durmasını bekler.
                if (!immediately) {
                    delay(400)
                }

                val cities = repository.search(searchText)

                _state.value =
                    if (cities.isEmpty()) {
                        CitySearchState.Empty
                    } else {
                        CitySearchState.Success(cities)
                    }
            } catch (error: CancellationException) {
                // Yeni arama için iptal edilen istek hata değildir.
                throw error
            } catch (error: SocketTimeoutException) {
                _state.value =
                    CitySearchState.Error(
                        "Bağlantı zaman aşımına uğradı. Tekrar Dene.",
                    )
            } catch (error: IOException) {
                _state.value =
                    CitySearchState.Error(
                        "Sunucuya ulaşılamadı. İnternet bağlantınızı " +
                            "kontrol edip tekrar dene.",
                    )
            } catch (error: HttpException) {
                val message =
                    if (error.code() == 429) {
                        "Çok fazla istek gönderildi. " + "Biraz bekleyip tekrar dene."
                    } else {
                        "Şehir servisi şu anda yanıt veremiyor. " + "Daha sonra tekrar dene."
                    }

                _state.value = CitySearchState.Error(message)
            } catch (error: Exception) {
                _state.value =
                    CitySearchState.Error(
                        "Arama sonucu işlenemedi. Tekrar Dene.",
                    )
            }
        }
    }
}
