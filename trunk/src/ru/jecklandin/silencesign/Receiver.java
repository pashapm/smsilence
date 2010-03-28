package ru.jecklandin.silencesign;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;
import android.util.Log;

public class Receiver extends BroadcastReceiver {
	final static String TAG = "SilenceSign:Receiver";
	
	public static boolean s_receive = true;

	@Override
	public void onReceive(Context context, Intent intent) {

		String s = (String) intent.getCharSequenceExtra("smsilence");
		if (s != null) {
			if (s.equals("stop")) {
				Log.d(TAG, "receiver stop");
				Receiver.s_receive = false;
				return;
			} else if (s.equals("start")) {
				Log.d(TAG, "receiver start");
				Receiver.s_receive = true;
				return;
			}
		}

		if (!s_receive)
			return;

		// ======== sms working ============//
		Bundle bundle = intent.getExtras();
		Object messages[] = (Object[]) bundle.get("pdus");
		SmsMessage smsMessage[] = new SmsMessage[messages.length];
		for (int n = 0; n < messages.length; n++) {
			smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
		}

		AudioManager amanager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		Log.d(TAG, "NOTIF SOUND: "+amanager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));

		String subj = smsMessage[0].getDisplayMessageBody();
		Log.d(TAG, "SMS body:" + subj);
		if (subj.contains("#s")) {
			Log.d(TAG, "SMS silence!");

			boolean was_vibro = false;
			//if #s, mute both vibro & sound 
			if (amanager.getVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION) == 
				AudioManager.VIBRATE_SETTING_ON) {
				amanager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, 
						AudioManager.VIBRATE_SETTING_OFF);
				was_vibro = true;
			}
			
			amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
			boolean was_muted = true;
			
			new Thread(new SilencingThread(amanager, was_vibro, was_muted)).start();
		} else if (subj.contains("#v")) {
			Log.d(TAG, "SMS vibro!");

			boolean was_muted = true;
			amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
		
			new Thread(new SilencingThread(amanager, false, was_muted)).start();
		}

	}
}

class SilencingThread implements Runnable {

	AudioManager m_man;
	private int WAIT_SEC = 10;
	
	private boolean was_vibro;
	private boolean was_muted; 
	
	SilencingThread(AudioManager manager, boolean was_vibro, boolean was_muted) {
		m_man = manager;
		this.was_muted = was_muted;
		this.was_vibro = was_vibro;
	} 

	@Override
	public void run() {
		try {
			Log.d(Receiver.TAG, "Paused for "+WAIT_SEC+" sec");
			Thread.sleep(WAIT_SEC * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Log.d(Receiver.TAG, "RESTORING!");
		
		//restore sound 
		if (was_muted) {
			Log.d(Receiver.TAG, "Restoring sound");
			m_man.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
		}
		
		
		//restore vibro
		if (was_vibro) 
		{
			Log.d(Receiver.TAG, "Restoring vibro");
			m_man.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);
        } 
	}

}