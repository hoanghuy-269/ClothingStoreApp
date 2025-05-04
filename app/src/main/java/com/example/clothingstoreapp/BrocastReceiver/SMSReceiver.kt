package com.example.clothingstoreapp.BroadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage

class SMSReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras ?: return
        val pdus = bundle["pdus"] as? Array<*> ?: return
        val messages = pdus.mapNotNull {
            SmsMessage.createFromPdu(it as ByteArray)
        }

        for (message in messages) {
            val messageBody = message.messageBody
            val otp = extractOTP(messageBody)
            if (otp != null) {
                // Gửi Broadcast với OTP
                val otpIntent = Intent("otp_received")
                otpIntent.putExtra("otp", otp)
                context?.sendBroadcast(otpIntent)
            }
        }
    }

    private fun extractOTP(message: String): String? {
        val regex = Regex("\\d{6}")
        return regex.find(message)?.value
    }
}
