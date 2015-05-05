package com.cjcornell.cyrano;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.cjcornell.cyrano.data.AppSettings;
import com.cjcornell.cyrano.data.DataStore;
import com.cjcornell.cyrano.task.TaskSearchForDevices;
import com.cjcornell.cyrano.task.TaskSearchForTriggers;
import com.cjcornell.cyrano.task.TaskTriggerScript;

public class TextToSpeachService extends Service implements
		OnCompletionListener {
	private static TextToSpeachService instance = new TextToSpeachService(1);
	private final String TAG = "TextToSpeachService";
	public static AudioManager am;
	String path = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/wakeUp.wav";
	MediaPlayer mp = new MediaPlayer();
	private Command whichItem;
	String destFileName = " ";
	Context context;
	int i = 1, count = 1, friend = 0;
	double Delay;
	int result;
	boolean isPAUSEmp, isplay = true;
	public boolean commandcall, triggercall, cell, friendannounc;
	public boolean Triggerscript;
	private boolean mppause;
	private int identifier;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public TextToSpeachService(int identifier) {
		this.identifier = identifier;
	};

	public static TextToSpeachService getInstance() {
		return instance;
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// String s = intent.getStringExtra("speach");
		// String s = intent.getStringExtra("friends");

		// Friend friends[] = (Friend[])intent.getSerializableExtra("friends");
		ArrayList<Friend> friends = (ArrayList<Friend>) intent.getSerializableExtra("friends");

		// Log.v(TAG, "sas" + s); if (s != null) {
		/*
		 * textToSpeech(TextToSpeachService.this, s, new
		 * AudioCompletionNotifiable() {
		 * 
		 * 
		 * @Override public void audioCompleted() { stopSelf(); } }); Log.v(TAG,
		 * "Call tts"); //playtts(); } else
		 */
		if (friends != null) {
			announceMultipleFriends(friends, new AudioCompletionNotifiable() {

				@Override
				public void audioCompleted() {
					stopSelf();
				}
			});
		}

		super.onStart(intent, startId);
	}

	@Override
	public void onCreate() {
		Log.v(TAG, "Srvice");

	}

	// For text-to-speech
	private TextToSpeech tts;
	protected boolean commandcallPush;

	public interface AudioCompletionNotifiable {
		public void audioCompleted();
	}

	private interface TTSAction {
		public void run(TextToSpeech tts);
	}

	@SuppressLint("NewApi")
	private void performTextToSpeech(Context context, final TTSAction callback,
			final AudioCompletionNotifiable n) {
		tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onInit(int status) {
				if (tts == null)
					return;
				/*
				 * We need to deal with the tts object once it is done speaking
				 * - namely, we should free the resources it is using. There is
				 * a deprecated way to do this, so we must include both if we
				 * are to be flexible with device versions.
				 */
				if (Build.VERSION.SDK_INT >= 15) {
					// The newer way
					tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
						@Override
						public void onDone(String utteranceId) {
							Log.e(TAG, "Completed successfully");
							instance.playtts();
							stopTextToSpeech();
							if (n != null) {
								n.audioCompleted();

							}
						}

						@Override
						public void onError(String utteranceId) {
							Log.v(TAG, "Error id: " + utteranceId);
							stopTextToSpeech();
							if (n != null) {
								n.audioCompleted();
							}
						}

						@Override
						public void onStart(String utteranceId) {
							Log.v(TAG, "TTS Started");

						}
					});
				} else {
					// This covers the deprecated way to handle the object upon
					// completion
					tts.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
						@Override
						public void onUtteranceCompleted(String utteranceId) {
							Log.v(TAG, "Completed successfully");
							stopTextToSpeech();
							if (n != null) {
								n.audioCompleted();
							}
						}
					});
				}

				if (status == TextToSpeech.SUCCESS) {
					// Do nothing if no TTS object was created
					if (tts != null) {
						// Set the language appropriately
						// TODO: Make options for different languages
						int result = tts.setLanguage(Locale.US);

						// Ensure the language is supported
						if (result == TextToSpeech.LANG_MISSING_DATA
								|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
							Log.v(TAG, "Language not supported");

							// Actually read out the message
						} else {

							callback.run(tts);
						}
					} else {
						Log.v(TAG, "The TextToSpeech variable is null");
					}
				}
			}
		});
	}

	/**
	 * Text-to-speech (TTS) Use the built in text-to-speech engine to speak
	 * text.
	 */

	public void textToSpeech(Context context, final String text,
			final AudioCompletionNotifiable n) {
		am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		performTextToSpeech(context, new TTSAction() {
			@SuppressWarnings("deprecation")
			@Override
			public void run(TextToSpeech tts) {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, text);

				String d = Environment.getExternalStorageDirectory()
						.getAbsolutePath();
				destFileName = d + "/wakeUp.wav";

				tts.synthesizeToFile(text, params, destFileName);
				Log.e(TAG, "Text of speach" + text);

				// tts.speak(text, TextToSpeech.QUEUE_FLUSH, params);

			}

		}, n);

	}

	public void textToSpeech(Context context, final String text) {
		textToSpeech(context, text, null);

	}

	/**
	 * Text-to-speech (TTS) Stop the current TTS utterance.
	 */
	public void stopTextToSpeech() {
		if (tts != null && tts.isSpeaking()) {
			tts.stop();
			tts.shutdown();

		}
		tts = null;
	}

	/**
	 * playInstructions Plays a sequence of instructions with delays specified
	 * in between
	 */
	public void playInstructions(Context context,
			final List<String> instructions, final List<Integer> delays,
			final AudioCompletionNotifiable n) {
		performTextToSpeech(context, new TTSAction() {
			@Override
			public void run(TextToSpeech tts) {
				for (int i = 0; i < instructions.size(); i++) {
					if (i == instructions.size() - 1) {
						HashMap<String, String> params = new HashMap<String, String>();
						params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
								"stringId");
						tts.speak(instructions.get(i), TextToSpeech.QUEUE_ADD,
								params);
						tts.playSilence(1000 * delays.get(i),
								TextToSpeech.QUEUE_ADD, null);
					} else {
						tts.speak(instructions.get(i), TextToSpeech.QUEUE_ADD,
								null);
						tts.playSilence(1000 * delays.get(i),
								TextToSpeech.QUEUE_ADD, null);
					}
				}
				Log.v(TAG, "Instructions read out successfully!");
			}
		}, n);
	}

	public void playInstructions(Context context,
			final List<String> instructions, final List<Integer> delays) {
		playInstructions(context, instructions, delays, null);
	}

	/**
	 * announceMultipleFriends Announce multiple friends using textToSpeech
	 * 
	 * @param friends
	 *            : The friends to announce
	 */
	private void announceMultipleFriends(ArrayList<Friend> friends,
			AudioCompletionNotifiable n) {
		int numFriends = Math.min(AppSettings.maxFriends, friends.size());
		List<String> phrases = new ArrayList<String>();
		List<Integer> pauses = new ArrayList<Integer>();
		if (friends.size() == 1) {
			phrases.add(this.getString(R.string.singleFriendsMessage));
		} else {
			phrases.add(this.getString(R.string.multipleFriendsMessage,
					friends.size()));
		}
		pauses.add(AppSettings.pauseLength);
		for (int i = 0; i < numFriends; i++) {
			phrases.add(this.getString(R.string.singleFriendMessage, friends
					.get(i).getName()));
			pauses.add(1);
		}

		playInstructions(this, phrases, pauses, n);
	}

	public void playtts() {
		Uri uri = Uri.parse(path);
		Log.e(TAG, "playtts strats");
		try {
			mp.setDataSource(this, uri);
			mp.setAudioStreamType(AudioManager.STREAM_ALARM);
			mp.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mp.start();
		Log.d(TAG, "media start" + isPAUSEmp + "......");
		if (isPAUSEmp)
			mp.pause();
		mp.setOnCompletionListener(this);
		new Thread(new Task()).start();

	}

	public void pauseMP() {
		// TODO Auto-generated method stub

		mp.pause();
		isPAUSEmp = true;
		Log.d(TAG, "mp Pause" + isPAUSEmp + "............");
	}

	public void scriptpaly(Command currentItem, ActivityCyrano activityCyrano) {
		// TODO Auto-generated method stub
		whichItem = currentItem;
		this.context = activityCyrano;
	}

	public void resumeMP() {
		// TODO Auto-generated method stub
		try {
			if (isPAUSEmp) {
				mp.start();
				isPAUSEmp = false;
				Log.v(TAG, "mp Resume");
			}
		} catch (Exception e) {
			Log.d(TAG, "Mp stopped");
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		int delay = (int) Delay;
		isPAUSEmp = false;
		mp.reset();
		Log.e(TAG, "finsih mp" + delay);
		if (instance.commandcall) {
			try {
				Thread.sleep(1000 * delay);
				((ActivityCyrano) context).nextscript(whichItem);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Log.e("", e.toString() + "");
			}

		} else {

			if (AppSettings.friendAudio
					&& DataStore.getInstance().setsearchval
					&& friend < DataStore.getInstance().NumberofbuletoothFriends) {
				String BTaddress = "";
				if (!DataStore.getInstance().getFrientList().isEmpty()) {
					BTaddress = DataStore.getInstance().getFrientList()
							.get(friend).getAddress();
				} else {
					BTaddress = null;
				}

				boolean announce = true;
				if (DataStore.getInstance().getIDSofBTIDS().get(BTaddress) != null
						&& DataStore.getInstance().getIDSofBTIDS()
								.get(BTaddress) > 1) {
					DataStore.getInstance().setsearchval = false;
					announce = false;
				} else
					announce = true;

				if (!friendannounc) {
					if (DataStore.getInstance().NumberofbuletoothFriends <= AppSettings.maxFriends) {
						if (announce)
							friendnamespeak();
					} else {
						DataStore.getInstance().setfriendsearchval = false;
						DataStore.getInstance().setsearchval = false;
					}

				} else {
					if (DataStore.getInstance().NumberofbuletoothFriends <= AppSettings.maxFriends) {
						if (announce)
							friendreminderspeak();
						friend++;
					} else {
						DataStore.getInstance().setfriendsearchval = false;
						DataStore.getInstance().setsearchval = false;
					}

				}
			} else {
				friend = 0;
				DataStore.getInstance().setsearchval = false;

			}
			Log.i(TAG, DataStore.getInstance().setsearchval + "   "
					+ "check for executions");
			if (!DataStore.getInstance().setsearchval) {
				if (!instance.commandcall) {
					DataStore.getInstance().setfriendsearchval = false;
					new TaskSearchForDevices(DataStore.getInstance()
							.getActivity()).execute(DataStore.getInstance()
							.getBluetoothDeviceList());

					new TaskSearchForTriggers(DataStore.getInstance()
							.getActivity()).execute(DataStore.getInstance()
							.getBluetoothDeviceList());

				}
			}
		}

		if (instance.commandcall) {
		} else {
			if (Triggerscript) {

				if (count < DataStore.getInstance().getTriggerIDS().size()) {
					new TaskTriggerScript(
							DataStore.getInstance().getActivity(), DataStore
									.getInstance().getTriggerIDS().get(count))
							.execute(1, context);
					count++;
				} else {
					DataStore.getInstance().setfriendsearchval = true;
					count = 1;
					Triggerscript = false;
					cell = true;

				}

			}
		}

	}

	private void friendreminderspeak() {
		// TODO Auto-generated method stub
		try {
			textToSpeech(
					DataStore.getInstance().getActivity(),
					DataStore
							.getInstance()
							.get_BluetoothFriendReminder()
							.get(DataStore.getInstance().getFrientList()
									.get(friend).getAddress()));
		} catch (Exception e) {

			Log.e(TAG, e.toString());
		}
		// speak
		// friend
		// personal
		// reminder
		friendannounc = false;

	}

	private void friendnamespeak() {
		// TODO Auto-generated method stub
		try {

			textToSpeech(DataStore.getInstance().getActivity(), DataStore
					.getInstance().getFrientList().get(friend).getName());// speak//
																			// friend//
																			// name
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		friendannounc = true;

	}

	private void initData() {
		if (am.isMusicActive()) {
			if (mp.isPlaying())
				mp.pause();
			mppause = true;

		} else {
			if (!am.isMusicActive() && mppause)
				mp.start();
			mppause = false;
		}

	}

	class Task implements Runnable {

		@Override
		public void run() {

			while (i > 0) {

				try {

					Thread.sleep(1000);
					initData();

				} catch (InterruptedException e) {

					e.printStackTrace();

				}

			}

		}

	}

	public void triggerscript(ActivityCyrano activityCyrano) {
		// TODO Auto-generated method stub
		context = activityCyrano;
		Triggerscript = true;

	}

}