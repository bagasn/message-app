package com.bagas.messagingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bagas.messagingapp.livedata.UserLiveData
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    lateinit var liveModel: UserLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        liveModel = ViewModelProvider(
            viewModelStore,
            ViewModelProvider.NewInstanceFactory()
        ).get(UserLiveData::class.java)

        val observerName = Observer<String> {
            findViewById<TextView>(R.id.text_name).text = it
        }

        val observerEmail = Observer<String> {
            findViewById<TextView>(R.id.text_email).text = it
        }

        val observerPhone = Observer<String> {
            findViewById<TextView>(R.id.text_phone).text = it
        }

        liveModel.name.observe(this, observerName)
        liveModel.email.observe(this, observerEmail)
        liveModel.phone.observe(this, observerPhone)

        Log.w("Firebase", "before request token " + System.currentTimeMillis())
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                val sp = getSharedPreferences("app_session", MODE_PRIVATE)
                if (task.isSuccessful) {
                    sp.edit()
                        .putString("firebase_token", task.result)
                        .apply()
                }

                Log.i(
                    "Firebase", "Firebase Token: " +
                            sp.getString("firebase_token", "none")
                )
                Log.w("Firebase", "after request token " + System.currentTimeMillis())
            }


    }
}