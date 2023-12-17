package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.db.local.entity.Story
import com.example.storyapp.db.remote.StoryRepository
import com.example.storyapp.response.AddStory
import com.example.storyapp.response.Login
import com.example.storyapp.response.Signup
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class DetailStoryViewModel @Inject constructor (private val repository: StoryRepository): ViewModel() {

    val login: LiveData<Login> = repository.login

    val signup: LiveData<Signup> = repository.signup

    val isLoading: LiveData<Boolean> = repository.isLoading

    val addStory: LiveData<AddStory> = repository.addStory

    val listStory: LiveData<List<Story>> = repository.listStory

    val message: MutableLiveData<String> = repository.message

    fun getLogin(email: String, password: String) {
        repository.getLogin(email, password)
    }
    fun getSignup(name: String, email: String, password: String) {
        repository.getSignup(name, email, password)
    }

    fun getListStory(token: String) {
        repository.getListStory(token)
    }

    fun addStory(token: String, image: MultipartBody.Part, description: RequestBody, lat: Double? = null, lon: Double? = null) {
        repository.addStory(token, image, description, lat, lon)
    }

    @ExperimentalPagingApi
    fun getPagingStory(token: String): LiveData<PagingData<Story>> {
        return repository.getPagingStory(token).cachedIn(viewModelScope)
    }
}