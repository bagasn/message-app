package com.bagas.messagingapp.livedata

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserLiveData: ViewModel() {

    val name: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val email: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val phone: MutableLiveData<String> by lazy { MutableLiveData<String>() }

}