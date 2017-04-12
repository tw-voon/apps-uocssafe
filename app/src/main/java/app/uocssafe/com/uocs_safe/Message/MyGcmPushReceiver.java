package app.uocssafe.com.uocs_safe.Message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import app.uocssafe.com.uocs_safe.Helper.AppConfig;
import app.uocssafe.com.uocs_safe.Helper.NotificationUtils;
import app.uocssafe.com.uocs_safe.Helper.Session;
import app.uocssafe.com.uocs_safe.Message.Models.Messages;
import app.uocssafe.com.uocs_safe.Message.Models.User;
import app.uocssafe.com.uocs_safe.uocs_safe;

public class MyGcmPushReceiver extends FirebaseMessagingService {

    private NotificationUtils notificationUtils;
    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();
    Session session;
    String title, message, imageUrl, timestamp, flag;
    Boolean isBackground;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){

        JSONObject data;
        JSONObject payload = null;

//        Log.d(TAG, "datass: " + remoteMessage.getData());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
//            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
//            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().get("data"));
//                data = json.getJSONObject("data");

                title = remoteMessage.getData().get("title");
                message = json.getString("message");
                isBackground = Boolean.valueOf(remoteMessage.getData().get("is_background"));
//                imageUrl = data.getString("image");
                timestamp = json.getString("created_at");
                flag = remoteMessage.getData().get("flag");
//                payload = data.getJSONObject("payload");

//                Log.e(TAG, "title: " + title);
//                Log.e(TAG, "message: " + message);
//                Log.e(TAG, "isBackground: " + isBackground);
//                Log.e(TAG, "payload: " + payload.toString());
//                Log.e(TAG, "imageUrl: " + imageUrl);
//                Log.e(TAG, "timestamp: " + timestamp);
//                Log.e(TAG, "Flag: " + flag);

                switch (Integer.parseInt(flag)) {
                    case AppConfig.PUSH_TYPE_CHATROOM:
                        // push notification belongs to a chat room
                        processChatRoomPush(title, isBackground, json.toString());
                        break;
                    case AppConfig.PUSH_TYPE_USER:
                        // push notification is specific to user
                        processUserMessage(title, isBackground, json.toString());
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
//        Log.d(TAG, "From: " + from);
//        Log.d(TAG, "title: " + title);
//        Log.d(TAG, "isBackground: " + isBackground);
//        Log.d(TAG, "flag: " + flag);
//        Log.d(TAG, "data: " + data);
//        Toast.makeText(getApplicationContext(), "Message received", Toast.LENGTH_SHORT).show();
//
//        session = new Session(getApplicationContext());
//
//        if (flag == null)
//            return;
//
//        if(session.loggedin()){
//            // user is not logged in, skipping push notification
//            Log.e(TAG, "user is not logged in, skipping push notification");
//            return;
//        }
//
//        if (from.startsWith("/topics/")) {
//            // message received from some topic.
//        } else {
//            // normal downstream message.
//        }
//
//        switch (Integer.parseInt(flag)) {
//            case AppConfig.PUSH_TYPE_CHATROOM:
//                // push notification belongs to a chat room
//                processChatRoomPush(title, isBackground, payload.toString());
//                break;
//            case AppConfig.PUSH_TYPE_USER:
//                // push notification is specific to user
//                processUserMessage(title, isBackground, payload.toString());
//                break;
//        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(AppConfig.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }
    }

    /*Processing chat room push message,
    this message will be broadcast to all activities registered
    **/

    private void processChatRoomPush(String title, boolean isBackground, String data){
        session = new Session(getApplicationContext());
        Log.d(TAG, data);
        if(!isBackground){
            try{
                JSONObject datObj = new JSONObject(data);

                String chatRoomId = datObj.getString("chat_room_id");

                JSONObject mObj = datObj.getJSONObject("message");
                Messages message = new Messages();
                message.setMessage(mObj.getString("message"));
                message.setId(mObj.getString("message_id"));
                message.setCreatedAt(mObj.getString("created_at"));

                JSONObject uObj = datObj.getJSONObject("user");

                if(uObj.getString("id").equals(session.getUserID())){
                    Log.e(TAG, "Skipping the push message as it belongs to same user");
                    return;
                }

                User user = new User();
                user.setId(uObj.getString("id"));
                user.setName(uObj.getString("name"));
                message.setUser(user);

                if(!NotificationUtils.isAppIsInBackground(getApplicationContext())){
                    Intent pushNotification = new Intent(AppConfig.PUSH_NOTIFICATION);
                    pushNotification.putExtra("type", AppConfig.PUSH_TYPE_CHATROOM);
                    pushNotification.putExtra("message", message);
                    pushNotification.putExtra("chat_room_id", chatRoomId);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    // play notification sound
                    NotificationUtils notificationUtils = new NotificationUtils();
                    notificationUtils.playNotificationSound();
                } else {
                    // app is in background. show the message in notification try
                    Intent resultIntent = new Intent(getApplicationContext(), MessageActivity.class);
                    resultIntent.putExtra("chat_room_id", chatRoomId);
                    showNotificationMessage(getApplicationContext(), title, user.getName() + " : " + message.getMessage(), message.getCreated_at(), resultIntent);
                }
            } catch (JSONException e){

                Log.e(TAG, "json parsing error: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();

            }
        }
    }

    private void processUserMessage(String title, boolean isBackground, String data){
        if(!isBackground){
            try{
                JSONObject dataObj = new JSONObject(data);
                String imageUrl = dataObj.getString("image");

                JSONObject mObj = dataObj.getJSONObject("message");
                Messages message = new Messages();
                message.setMessage(mObj.getString("message"));
                message.setId(mObj.getString("message_id"));
                message.setCreatedAt(mObj.getString("created_at"));

                JSONObject uObj = dataObj.getJSONObject("user");
                User user = new User();
                user.setId(uObj.getString("user_id"));
                user.setName(uObj.getString("name"));
                message.setUser(user);

                // verifying whether the app is in background or foreground
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(AppConfig.PUSH_NOTIFICATION);
                    pushNotification.putExtra("type", AppConfig.PUSH_TYPE_USER);
                    pushNotification.putExtra("message", message);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    // play notification sound
                    NotificationUtils notificationUtils = new NotificationUtils();
                    notificationUtils.playNotificationSound();
                } else {

                    // app is in background. show the message in notification try
                    Intent resultIntent = new Intent(getApplicationContext(), MessageActivity.class);

                    // check for push notification image attachment
                    if (TextUtils.isEmpty(imageUrl)) {
                        showNotificationMessage(getApplicationContext(), title, user.getName() + " : " + message.getMessage(), message.getCreated_at(), resultIntent);
                    } else {
                        // push notification contains image
                        // show it with the image
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message.getMessage(), message.getCreated_at(), resultIntent, imageUrl);
                    }
                }
            } catch (JSONException e){
                Log.e(TAG, "json parsing error: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
