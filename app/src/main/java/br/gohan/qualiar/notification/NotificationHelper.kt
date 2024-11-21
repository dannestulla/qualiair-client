package br.gohan.qualiar.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import br.gohan.qualiar.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationHelper : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // Enviar para APi novo token
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body

        showNotification3(title, body)
    }

        private fun showNotification3(title: String?, body: String?) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Criação do canal de notificação (necessário apenas no Android 8.0+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "default_notification_channel_id"
                val channelName = "Notification name"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val notificationChannel = NotificationChannel(channelId, channelName, importance)

                // Registra o canal de notificação no sistema
                if (notificationManager.getNotificationChannel(channelId) == null) {
                    notificationManager.createNotificationChannel(notificationChannel)
                }
            }

            // Configuração da notificação
            val notificationBuilder = NotificationCompat.Builder(this, "default_notification_channel_id")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            // Exibe a notificação
            notificationManager.notify(1, notificationBuilder.build())
        }
}
