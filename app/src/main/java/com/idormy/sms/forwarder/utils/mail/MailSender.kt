package com.idormy.sms.forwarder.utils.mail

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.mail.Transport

/**
 * 邮件发送器
 */
object MailSender {

    /**
     * 获取单例
     */
    @JvmStatic
    fun getInstance() = this

    /**
     * 发送邮件
     */
    fun sendMail(mail: Mail, onMailSendListener: OnMailSendListener? = null) {
        val send = GlobalScope.async(Dispatchers.IO) {
            Transport.send(MailUtil.createMailMessage(mail))
        }

        GlobalScope.launch(Dispatchers.Main) {
            runCatching {
                send.await()
                onMailSendListener?.onSuccess()
            }.onFailure {
                Log.e("MailSender", it.message.toString())
                onMailSendListener?.onError(it)
            }
        }
    }

    /**
     * 发送回调
     */
    interface OnMailSendListener {
        fun onSuccess()
        fun onError(e: Throwable)
    }
}