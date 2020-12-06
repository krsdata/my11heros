package com.my11heros.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.my11heros.utils.BindingUtils;
import com.my11heros.R;

/**
 * The type Notification utils.
 */
public class NotificationUtils {

	private static String TAG = NotificationUtils.class.getSimpleName();
	private static final String APPUPDATE_REMINDER_NOTIFICATION_CHANNEL_ID = "reminder_notification_channel";
	private static final int APPUPDATE_REMINDER_PENDING_INTENT_ID = 3000;
	private Context mContext;

	/**
	 * Instantiates a new Notification utils.
	 *
	 * @param mContext the m context
	 */
	public NotificationUtils(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 * Show notification message.
	 *
	 * @param title     the title
	 * @param message   the message
	 * @param timeStamp the time stamp
	 * @param intent    the intent
	 */
	public void showNotificationMessage(String title, String message, String timeStamp, Intent intent) {
		showNotificationMessage(title, message, timeStamp, intent, null);
	}

	/**
	 * Show notification message.
	 *
	 * @param title     the title
	 * @param message   the message
	 * @param timeStamp the time stamp
	 * @param intent    the intent
	 * @param imageUrl  the image url
	 */
	public void showNotificationMessage(final String title, final String message, final String timeStamp, Intent intent, String imageUrl) {
		// Check for empty push message
		if (TextUtils.isEmpty(message))
			return;


		// notification icon
		final int icon = R.mipmap.ic_launcher;

		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		final PendingIntent resultPendingIntent =
				PendingIntent.getActivity(
						mContext,
						0,
						intent,
						PendingIntent.FLAG_CANCEL_CURRENT
				);

		final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				mContext);

		final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
				+ "://" + mContext.getPackageName() + "/raw/notification");

		if (!TextUtils.isEmpty(imageUrl)) {

			if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

				Bitmap bitmap = getBitmapFromURL(imageUrl);

				if (bitmap != null) {
					showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
				} else {
					showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
				}
			}
		} else {
			showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
			//playNotificationSound();
		}
	}


	private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {

		//NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		//inboxStyle.addLine(message);
		// If the build version is greater than JELLY_BEAN and lower than OREO,
		// set the notification's priority to PRIORITY_HIGH.
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
			mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
		}
		NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel notificationChannel = new NotificationChannel(
					APPUPDATE_REMINDER_NOTIFICATION_CHANNEL_ID,
					mContext.getString(R.string.notifications_admin_channel_name),
					NotificationManager.IMPORTANCE_HIGH);

			notificationManager.createNotificationChannel(notificationChannel);
		}


		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(mContext, APPUPDATE_REMINDER_NOTIFICATION_CHANNEL_ID)
						.setColorized(true)
						.setColor(ContextCompat.getColor(mContext, R.color.black))
						.setSmallIcon(R.mipmap.ic_launcher)
						.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
						.setContentTitle(title)
						.setContentText(message)
						//.setStyle(inboxStyle)
						/*.setStyle(new NotificationCompat.BigTextStyle()
								.bigText(message))*/
						.setDefaults(Notification.DEFAULT_VIBRATE)
						.setContentIntent(resultPendingIntent)
						.setAutoCancel(true);


		// If the build version is greater than JELLY_BEAN and lower than OREO,
		// set the notification's priority to PRIORITY_HIGH.
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
			notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
		}

		//Trigger the notification by calling notify on the NotificationManager.
		notificationManager.notify(APPUPDATE_REMINDER_PENDING_INTENT_ID, notificationBuilder.build());

	}

	private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {
		NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
		bigPictureStyle.setBigContentTitle(title);
		bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
		bigPictureStyle.bigPicture(bitmap);
		Notification notification;
		notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
				.setAutoCancel(true)
				.setContentTitle(title)
				.setContentIntent(resultPendingIntent)
				.setSound(alarmSound)
				.setStyle(bigPictureStyle)
				.setWhen(getTimeMilliSec(timeStamp))
				.setSmallIcon(R.mipmap.ic_launcher)
				.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
				.setContentText(message)
				.build();

		NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(BindingUtils.NOTIFICATION_ID_BIG_IMAGE, notification);
	}

	/**
	 * Downloading push notification image before displaying it in
	 * the notification tray
	 *
	 * @param strURL the str url
	 * @return the bitmap from url
	 */
	public Bitmap getBitmapFromURL(String strURL) {
		try {
			URL url = new URL(strURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			return BitmapFactory.decodeStream(input);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Play notification sound.
	 */
// Playing notification sound
	public void playNotificationSound() {
		try {
			Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
					+ "://" + mContext.getPackageName() + "/raw/notification");
			Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
			r.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method checks if the app is in background or not
	 *
	 * @param context the context
	 * @return the boolean
	 */
//	public static boolean isAppIsInBackground(Context context) {
//		boolean isInBackground = true;
//		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
//			List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
//			for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
//				if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//					for (String activeProcess : processInfo.pkgList) {
//						if (activeProcess.equals(context.getPackageName())) {
//							isInBackground = false;
//						}
//					}
//				}
//			}
//		} else {
//			List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//			ComponentName componentInfo = null;
//			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//				componentInfo = taskInfo.get(0).topActivity;
//				if (componentInfo.getPackageName().equals(context.getPackageName())) {
//					isInBackground = false;
//				}
//			}
//
//		}
//
//		return isInBackground;
//	}

	/**
	 * Clear notifications.
	 *
	 * @param context the context
	 */
// Clears notification tray messages
	public static void clearNotifications(Context context) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
	}

	/**
	 * Gets time milli sec.
	 *
	 * @param timeStamp the time stamp
	 * @return the time milli sec
	 */
	public static long getTimeMilliSec(String timeStamp) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		try {
			Date date = format.parse(timeStamp);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
}