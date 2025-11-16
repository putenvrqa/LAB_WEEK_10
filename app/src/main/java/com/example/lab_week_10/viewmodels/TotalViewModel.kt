package com.example.lab_week_10.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TotalViewModel: ViewModel() {
    //Declare the LiveData object
    private val _total = MutableLiveData<Int>()
    val total: LiveData<Int> = _total
    //Initialize the LiveData object
    init {
        _total.postValue(0)
    }
    //Increment the total value
    fun incrementTotal() {
        _total.postValue(_total.value?.plus(1))
    }
}