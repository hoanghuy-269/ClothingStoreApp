package com.example.clothingstoreapp.cloudinary

import android.content.Context
import com.cloudinary.android.MediaManager

object CloudinaryConfig {
    private var isInitialized = false

    fun init(context: Context) {
        if (!isInitialized) {
            val config: HashMap<String, String> = HashMap()
            config["cloud_name"] = "dwgzmn51c"
            config["api_key"] = "733465226788873"
            config["api_secret"] = "3YZpFyQ8M7DlQNiK3vhS18CKl5g"

            MediaManager.init(context, config)
            isInitialized = true
        }
    }
}
