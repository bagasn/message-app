package com.bagas.messagingapp.util

import android.content.Context
import android.content.SharedPreferences

class SPManager(context: Context) {

    companion object {
        private const val sp_master = "messaging_app"

        private const val sp_firebase_token = "sp_firebase_token";
        private const val sp_order_counter = "sp_order_counter";

        fun with(context: Context) = SPManager(context)
    }

    private val mSession : SharedPreferences = context.getSharedPreferences(sp_master, Context.MODE_PRIVATE)

    var firebaseToken: String?
    get() = mSession.getString(sp_firebase_token, "")
    set(newToken) {
        mSession.edit()
            .putString(sp_firebase_token, newToken)
            .apply()
    }

    var orderCounter: Int
    get() = mSession.getInt(sp_order_counter, 0);
    set(value) {
        mSession.edit()
            .putInt(sp_order_counter, value)
            .apply()
    }

}