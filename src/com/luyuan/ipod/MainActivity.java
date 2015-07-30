
package com.luyuan.ipod;

import com.luyuan.mcu.IIpod;
import com.luyuan.mcu.IIpodCallback;
import com.luyuan.mcu.constance.IpodConstance;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends Activity {
    private final static String TAG = "LuyuanIpod.MainActivity";

    private final static boolean LOGD = true;

    private String mIpodActionName = "com.luyuan.mcu.service.IpodService";

    private IIpod mIpodService = null;

    boolean mIsIpodServiceBind = false;

    private static final byte SHUFFLE_CLOSE = 0;

    private static final byte SHUFFLE_SONG = 1;

    private static final byte SHUFFLE_ALBUM = 2;

    private byte shuffle_mode = SHUFFLE_CLOSE;

    private byte shuffle_mode_temp = SHUFFLE_CLOSE;

    private static final byte REPEATE_CLOSE = 0;

    private static final byte REPEATE_ONE = 1;

    private static final byte REPEATE_ALL = 2;

    private byte repeate_mode = REPEATE_CLOSE;

    private byte repeate_mode_temp = REPEATE_CLOSE;

    private int volume = 0;

    private int volume_temp = 0;

    private int current_time = 0;

    private int current_time_temp = 0;

    private int total_time = 0;

    private int total_time_temp = 0;

    private String song_name = null;

    private String song_name_temp = null;

    private String singer_name = null;

    private String singer_name_temp = null;

    private String album_name = null;

    private String album_name_temp = null;

    private boolean ipod_exist = false;

    private boolean ipod_exist_temp = false;

    public final static String IPOD_PLAY_PAUSE = "com.luyuan.mcu.ipod.playpause";

    public final static String IPOD_REPEATE_MODE = "com.luyuan.mcu.ipod.repeatemode";

    public final static String IPOD_SHUFFLE_MODE = "com.luyuan.mcu.ipod.shufflemode";

    public final static String IPOD_SONG_NAME = "com.luyuan.mcu.ipod.songname";

    public final static String IPOD_SINGER_NAME = "com.luyuan.mcu.ipod.singername";

    public final static String IPOD_ALBUM_NAME = "com.luyuan.mcu.ipod.albumname";

    public final static String IPOD_VOLUME = "com.luyuan.mcu.ipod.volume";

    public final static String IPOD_CURRENT_TIME = "com.luyuan.mcu.ipod.current.time";

    public final static String IPOD_TOTAL_TIME = "com.luyuan.mcu.ipod.total.time";

    private static final String IPOD_CONNECTION_STATUS = "com.luyuan.ipod_connection_status";

    public static final String LUYUAN_KEY_UP = "com.luyuan.mcu.key_up";

    public static final String DO_START_IPOD_ACITON = "com.luyuan.mcu.ipod_music_start";

    public static final String DO_END_IPOD_ACITON = "com.luyuan.mcu.ipod_music_end";
    private static final String BROADCAST_ACC_STATUS = "com.luyuan.mcuservice.accstatus";

    public final static String IPOD_CLOSE_BY_STEERING_WHEEL = "com.luyuan.mcu.ipod.close.streeringwhmIpodServiceeel";

    IpodServiceConnection mIpodServiceConnection = new IpodServiceConnection();

    private ImageView mButtonPlayPauseView;

    private ImageView mButtonNextView;

    private ImageView mButtonRetreatQuicklyView;

    private ImageView mButtonFastForwardView;

    private ImageView mButtonPreviousView;

    private Button mButtonPlayPause;

    private Button mButtonNext;

    private Button mButtonRetreatQuickly;

    private Button mButtonFastForward;

    private Button mButtonPrevious;

    private Button mButtonShuffle;

    private Button mButtonRepeat;

    private TextView mTextSongName;

    private TextView mTextSingerName;

    private TextView mTextAlbumName;

    private TextView mTextTimeName;

    private int mResendTime = 3;

    private IIpodCallback mCallback = new IIpodCallback.Stub() {

        @Override
        public void onPlayStatusChange(byte status) throws RemoteException {
            Log.d(TAG, "IIpodCallback--onPlayStatusChange " + status);
            // mButtonPlayPause.setBackgroundResource(R.drawable.ic_pause);

        }

        @Override
        public void onSongNameChange(String name) throws RemoteException {
            Log.d(TAG, "IIpodCallback--onSongNameChange " + name);
            mTextSongName.setText(name);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.ipod_play);

        Intent intent = new Intent(mIpodActionName);
        bindService(intent, mIpodServiceConnection, Context.BIND_AUTO_CREATE);

        mButtonPlayPauseView = (ImageView) findViewById(R.id.operation_play_pause_view);
        mButtonPlayPause = (Button) findViewById(R.id.operation_play_pause);
        // mButtonPlayPause.setOnClickListener(mPlayPauseClick);
        mButtonPlayPause.setOnTouchListener(mPlayPauseTouchListener);
        mButtonNextView = (ImageView) findViewById(R.id.operation_next_view);
        mButtonNext = (Button) findViewById(R.id.operation_next);
        // mButtonNext.setOnClickListener(mNextClick);
        mButtonNext.setOnTouchListener(mNextTouchListener);
        mButtonRetreatQuicklyView = (ImageView) findViewById(R.id.retreat_quickly_view);
        mButtonRetreatQuickly = (Button) findViewById(R.id.retreat_quickly);
        mButtonRetreatQuickly.setOnTouchListener(mRetreatQuicklyListener);
        mButtonFastForwardView = (ImageView) findViewById(R.id.fast_forward_view);
        mButtonFastForward = (Button) findViewById(R.id.fast_forward);
        mButtonFastForward.setOnTouchListener(mFastForwardListener);
        mButtonPreviousView = (ImageView) findViewById(R.id.operation_previous_view);
        mButtonPrevious = (Button) findViewById(R.id.operation_previous);
        // mButtonPrevious.setOnClickListener(mPreviousClick);
        mButtonPrevious.setOnTouchListener(mPreviousTouchListener);
        mButtonShuffle = (Button) findViewById(R.id.operation_shuffle);
        // mButtonShuffle.setOnClickListener(mShuffleClick);
        mButtonShuffle.setOnTouchListener(mShuffleTouchListener);
        mButtonRepeat = (Button) findViewById(R.id.operation_recycle);
        // mButtonRepeat.setOnClickListener(mReapeatClick);
        mButtonRepeat.setOnTouchListener(mRepeatTouchListener);
        mTextSongName = (TextView) findViewById(R.id.title);
        mTextSingerName = (TextView) findViewById(R.id.artist);
        mTextAlbumName = (TextView) findViewById(R.id.album);
        mTextTimeName = (TextView) findViewById(R.id.type);

        setTimeText();
        registerBroadcast();
        sendBroadcast(new Intent("com.luyuan.try_cancel_mute"));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent");
		if (mIsIpodServiceBind) {
		    try {
                if(mIpodService.getPlayStatus() != IpodConstance.PlayStatus.PLAY) {
                    playFunction();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
		}
		sendBroadcast(new Intent("com.luyuan.try_cancel_mute"));
        super.onNewIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (null != mIpodService) {
            try {
                mIpodService.unregisterCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        stopFunction();
        unregisterReceiver(ipodBroadCastReceiver);
        unbindService(mIpodServiceConnection);
        removeRadioPlayNotification();
    }

    OnTouchListener mPlayPauseTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mIsIpodServiceBind) {
                        try {
                            if (mIpodService.getPlayStatus() == IpodConstance.PlayStatus.PLAY) {
                                mButtonPlayPauseView.setBackgroundResource(R.drawable.operation_pause_focus);
                            } else {
                                mButtonPlayPauseView.setBackgroundResource(R.drawable.operation_play_focus);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (mIsIpodServiceBind) {
                    try {
                        if (mIpodService.getFastForwardRewindMode() != IpodConstance.FastForwardRewindMode.NORMAL) {
                            quitFunction();
                        } else {
                            playPauseFunction();
                        }
                        if (mIpodService.getPlayStatus() == IpodConstance.PlayStatus.PLAY) {
                            mButtonPlayPauseView.setBackgroundResource(R.drawable.operation_pause_normal);
                        } else {
                            mButtonPlayPauseView.setBackgroundResource(R.drawable.operation_play_normal);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }
    };

    OnClickListener mPlayPauseClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            playPauseFunction();
        }

    };

    OnTouchListener mNextTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mButtonNextView.setBackgroundResource(R.drawable.operation_next_focus);
                    quitFunction();
                    nextFunction();
                }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                mButtonNextView.setBackgroundResource(R.drawable.operation_next_normal);
            }
            return true;
        }

    };

    OnTouchListener mRetreatQuicklyListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            Log.d(TAG, "mRetreatQuicklyListener : ");
                 if (event.getAction() == MotionEvent.ACTION_DOWN) {
                     mButtonRetreatQuicklyView.setBackgroundResource(R.drawable.but_fase_forward_sel);
                }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                    mButtonRetreatQuicklyView.setBackgroundResource(R.drawable.but_fase_forward_nor);
                    if (mIsIpodServiceBind) {
                        try {
                            if (mIpodService.getFastForwardRewindMode() != IpodConstance.FastForwardRewindMode.NORMAL) {
                                quitFunction();
                            } else {
                                retreatQuicklyFunction();
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
            }
            return true;
        }
    };

    OnTouchListener mFastForwardListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            Log.d(TAG, "mFastForwardListener : ");
                 if (event.getAction() == MotionEvent.ACTION_DOWN) {
                     mButtonFastForwardView.setBackgroundResource(R.drawable.but_rewind_sel);
                }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                mButtonFastForwardView.setBackgroundResource(R.drawable.but_rewind_nor);
                if (mIsIpodServiceBind) {
                    try {
                        if (mIpodService.getFastForwardRewindMode() != IpodConstance.FastForwardRewindMode.NORMAL) {
                            quitFunction();
                        } else {
                            fastForwardFunction();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }
    };

    OnClickListener mNextClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            nextFunction();
        }

    };

    OnTouchListener mPreviousTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mButtonPreviousView.setBackgroundResource(R.drawable.operation_previous_focus);
                    quitFunction();
                    previousFunction();
                }


            if (event.getAction() == MotionEvent.ACTION_UP) {
                mButtonPreviousView.setBackgroundResource(R.drawable.operation_previous_normal);
            }
            return true;
        }

    };

    OnClickListener mPreviousClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            previousFunction();
        }

    };

    OnTouchListener mRepeatTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            float x = event.getRawX();
            float y = event.getRawY();
            Log.d(TAG, "prev -- x:" + x + ",y:" + y);

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (repeate_mode == REPEATE_CLOSE)
                        mButtonRepeat.setBackgroundResource(R.drawable.operation_recycle_focus);
                    else if (repeate_mode == REPEATE_ONE) {
                        mButtonRepeat.setBackgroundResource(R.drawable.operation_recycle_one_focus);
                    } else if (repeate_mode == REPEATE_ALL) {
                        mButtonRepeat.setBackgroundResource(R.drawable.operation_recycle_all_focus);
                    }
                    repeatFunction();
                }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (repeate_mode == REPEATE_CLOSE)
                    mButtonRepeat.setBackgroundResource(R.drawable.operation_recycle_normal);
                else if (repeate_mode == REPEATE_ONE) {
                    mButtonRepeat.setBackgroundResource(R.drawable.operation_recycle_one_normal);
                } else if (repeate_mode == REPEATE_ALL) {
                    mButtonRepeat.setBackgroundResource(R.drawable.operation_recycle_all_normal);
                }

            }
            return true;
        }

    };

    OnClickListener mReapeatClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            repeatFunction();
        }

    };

    OnTouchListener mShuffleTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            float x = event.getRawX();
            float y = event.getRawY();
            Log.d(TAG, "prev -- x:" + x + ",y:" + y);

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (shuffle_mode == SHUFFLE_CLOSE)
                        mButtonShuffle.setBackgroundResource(R.drawable.operation_shuffle_focus);
                else {
                        mButtonShuffle.setBackgroundResource(R.drawable.operation_shuffle_normal);
                    }
                    shuffleFunction();
                }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (shuffle_mode == SHUFFLE_CLOSE)
                    mButtonShuffle.setBackgroundResource(R.drawable.operation_shuffle_normal);
                else {
                    mButtonShuffle.setBackgroundResource(R.drawable.operation_shuffle_focus);
                }

            }
            return true;
        }

    };

    OnClickListener mShuffleClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            shuffleFunction();
        }

    };

    private class IpodServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "connection success ...");
            mIpodService = IIpod.Stub.asInterface(service);
            if (mIpodService == null) {
                if (LOGD) {
                    Log.d(TAG, "IpodService connect failed");
                }
                return;
            }
            mIsIpodServiceBind = true;
            doIpodInit();
			try {
				boolean status = mIpodService.getConnectionMode();
				if (status == false) {
					Toast.makeText(MainActivity.this, R.string.ipod_connect,
							Toast.LENGTH_SHORT).show();
					finish();
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected...");
            mIsIpodServiceBind = false;
            mIpodService = null;
        }
    }

    private void update() {
        // Log.d(TAG, "update:" + ipod_exist);
        if (ipod_exist_temp != ipod_exist && ipod_exist_temp == false) {
            ipod_exist = ipod_exist_temp;
            mTextSongName.setText(R.string.download_info);
            mTextSingerName.setText(R.string.unknow);
            mTextAlbumName.setText(R.string.unknow);
            return;
        } else if (ipod_exist_temp != ipod_exist && ipod_exist_temp == true) {
            ipod_exist = ipod_exist_temp;
            mTextSongName.setText(R.string.unknow);
        }
        
        if (mIsIpodServiceBind) {
            try {
                if (mIpodService.getPlayStatus() == IpodConstance.PlayStatus.PLAY) {
                    showRadioPlayNotification();
                    mButtonPlayPauseView.setBackgroundResource(R.drawable.operation_pause);
                } else {
                    removeRadioPlayNotification();
                    mButtonPlayPauseView.setBackgroundResource(R.drawable.operation_play);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if (repeate_mode != repeate_mode_temp) {
            repeate_mode = repeate_mode_temp;
            if (repeate_mode == REPEATE_CLOSE) {
                mButtonRepeat.setBackgroundResource(R.drawable.operation_recycle);
            } else if (repeate_mode == REPEATE_ALL) {
                mButtonRepeat.setBackgroundResource(R.drawable.operation_recycle_all);
            } else if (repeate_mode == REPEATE_ONE) {
                mButtonRepeat.setBackgroundResource(R.drawable.operation_recycle_one);
            }
        }

        if (shuffle_mode != shuffle_mode_temp) {
            shuffle_mode = shuffle_mode_temp;
            if (shuffle_mode == SHUFFLE_CLOSE) {
                mButtonShuffle.setBackgroundResource(R.drawable.operation_shuffle);
            } else {
                mButtonShuffle.setBackgroundResource(R.drawable.operation_shuffle_selected);
            }
        }

        if (null != song_name_temp && !song_name_temp.equals(song_name)) {
            song_name = song_name_temp;
            mTextSongName.setText(song_name);
        }

        if (null != singer_name_temp && !singer_name_temp.equals(singer_name)) {
            singer_name = singer_name_temp;
            mTextSingerName.setText(singer_name);
        }

        if (null != album_name_temp && !album_name_temp.equals(album_name)) {
            album_name = album_name_temp;
            mTextAlbumName.setText(album_name);
        }

        if (current_time_temp != current_time || total_time_temp != total_time) {
            current_time = current_time_temp;
            total_time = total_time_temp;
            setTimeText();
        }

    }

    public void registerBroadcast() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(IPOD_PLAY_PAUSE);
        mIntentFilter.addAction(IPOD_REPEATE_MODE);
        mIntentFilter.addAction(IPOD_SHUFFLE_MODE);
        mIntentFilter.addAction(IPOD_SONG_NAME);
        mIntentFilter.addAction(IPOD_SINGER_NAME);
        mIntentFilter.addAction(IPOD_ALBUM_NAME);
        mIntentFilter.addAction(IPOD_VOLUME);
        mIntentFilter.addAction(IPOD_CURRENT_TIME);
        mIntentFilter.addAction(IPOD_TOTAL_TIME);
        mIntentFilter.addAction(IPOD_CONNECTION_STATUS);
        mIntentFilter.addAction(LUYUAN_KEY_UP);
        mIntentFilter.addAction(DO_START_IPOD_ACITON);
        mIntentFilter.addAction(DO_END_IPOD_ACITON);
        mIntentFilter.addAction(IPOD_CLOSE_BY_STEERING_WHEEL);
        //mIntentFilter.addAction(BROADCAST_ACC_STATUS);
        registerReceiver(ipodBroadCastReceiver, mIntentFilter);
    }
    
    Boolean accDisconnect =false;
    Boolean isInPlaying =false;
    BroadcastReceiver ipodBroadCastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Log.d(TAG, "IPOD--broadcast:" + intent.getAction());
            if (intent.getAction().equals(IPOD_CLOSE_BY_STEERING_WHEEL)) {
                Log.d(TAG, "IPOD--broadcast--IPOD_CLOSE_BY_STEERING_WHEEL");
                finish();
            } else if (intent.getAction().equals(BROADCAST_ACC_STATUS)){
            	int status = intent.getIntExtra("accstatus", 0);                
                if (status == 0) {                    
                    accDisconnect = true;
                    
                    if (mIsIpodServiceBind) {
                        try {
                            if (mIpodService.getPlayStatus() == IpodConstance.PlayStatus.PLAY) {
                                isInPlaying = true;
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    if(isInPlaying){
                        pauseFunction();
                        //play_status = PAUSE;
                    }
                    Log.d(TAG,"BROADCAST_ACC_STATUS--0:"+accDisconnect);
                } else {
                    Log.d(TAG,"BROADCAST_ACC_STATUS--1:"+accDisconnect+","+isInPlaying+","+","+mIsIpodServiceBind);
                    if (accDisconnect) {
                        accDisconnect = false;
//                        if(isInPlaying)
//                            playFunction();
                    }
                }
            } else if (intent.getAction().equals(DO_START_IPOD_ACITON)) {
                Log.d(TAG, "IPOD--broadcast--DO_START_IPOD_ACITON:" + DO_START_IPOD_ACITON);
                quitFunction();
                playFunction();
            } else if (intent.getAction().equals(DO_END_IPOD_ACITON)) {
                Log.d(TAG, "IPOD--broadcast--DO_END_IPOD_ACITON:" + DO_END_IPOD_ACITON);
                quitFunction();
                pauseFunction();
            } else if (intent.getAction().equals(IPOD_PLAY_PAUSE)) {
                update();
            } else if (intent.getAction().equals(IPOD_REPEATE_MODE)) {
                repeate_mode_temp = intent.getByteExtra("IPOD_REPEATE_MODE", REPEATE_CLOSE);
                Log.d(TAG, "IPOD_REPEATE_MODE:" + repeate_mode_temp);
                if (repeate_mode_temp != repeate_mode)
                    update();
            } else if (intent.getAction().equals(IPOD_SHUFFLE_MODE)) {
                shuffle_mode_temp = intent.getByteExtra("IPOD_SHUFFLE_MODE", SHUFFLE_CLOSE);
                Log.d(TAG, "IPOD_SHUFFLE_MODE:" + shuffle_mode_temp);
                if (shuffle_mode_temp != shuffle_mode)
                    update();
            } else if (intent.getAction().equals(IPOD_SONG_NAME)) {
                song_name_temp = intent.getStringExtra("IPOD_SONG_NAME");
                Log.d(TAG, " song_name_temp:" + song_name_temp);
                if (null != song_name_temp && !song_name_temp.equals(song_name))
                    update();
            } else if (intent.getAction().equals(IPOD_SINGER_NAME)) {
                singer_name_temp = intent.getStringExtra("IPOD_SINGER_NAME");
                Log.d(TAG, " singer_name_temp:" + singer_name_temp);
                if (null != singer_name_temp && !singer_name_temp.equals(singer_name))
                    update();
            } else if (intent.getAction().equals(IPOD_ALBUM_NAME)) {
                album_name_temp = intent.getStringExtra("IPOD_ALBUM_NAME");
                Log.d(TAG, " album_name_temp:" + album_name_temp);
                if (null != album_name_temp && !album_name_temp.equals(album_name))
                    update();
            } else if (intent.getAction().equals(IPOD_VOLUME)) {
                volume_temp = intent.getIntExtra("IPOD_VOLUME", 0);
                if (volume_temp != volume) {
                    volume = volume_temp;
                }
            } else if (intent.getAction().equals(IPOD_CURRENT_TIME)) {
                short a = intent.getShortExtra("IPOD_CURRENT_TIME", (short) 0);
                resendCommand();
                current_time_temp = a;
                if (current_time_temp != current_time)
                    update();
            } else if (intent.getAction().equals(IPOD_TOTAL_TIME)) {
                short c = intent.getShortExtra("IPOD_TOTAL_TIME", (short) 0);
                total_time_temp = c;
                if (total_time_temp != total_time)
                    update();
            } else if (intent.getAction().equals(IPOD_CONNECTION_STATUS)) {
                ipod_exist_temp = intent.getBooleanExtra("connected", false);
                if (ipod_exist_temp == false) {
                    stopFunction();
                    finish();
                } else if (ipod_exist_temp != ipod_exist)
                    update();
            } else if (intent.getAction().equals(LUYUAN_KEY_UP)) {
                String key = intent.getStringExtra("KEY_NAME");
                Log.d(TAG, "KEY_NAME:" + key);
                if (key.equals("PLAY_PAUS_KEY_IPOD")) {
                    if (mIsIpodServiceBind) {
                        try {
                            if (mIpodService.getFastForwardRewindMode() != IpodConstance.FastForwardRewindMode.NORMAL) {
                                quitFunction();
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    playPauseFunction();
                } else if (key.equals("UPKEY")) {
                    previousFunction();
                } else if (key.equals("DOWNKEY")) {
                    nextFunction();
                }
            }
        }

    };

    private void previousFunction() {
        if (mIsIpodServiceBind) {
            try {
            	sendBroadcast(new Intent("com.luyuan.try_cancel_mute"));
                mIpodService.previous();

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void nextFunction() {
        if (mIsIpodServiceBind) {
            try {
            	sendBroadcast(new Intent("com.luyuan.try_cancel_mute"));
                mIpodService.next();

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void removeRadioPlayNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(5);
    }

    private void retreatQuicklyFunction() {
        if (mIsIpodServiceBind) {
            Log.d(TAG, "retreatQuicklyFunction" + "Retreat quickly");

            if (mIsIpodServiceBind) {
                try {
                    sendBroadcast(new Intent("com.luyuan.try_cancel_mute"));
                    mIpodService.retreatQuickly();

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void fastForwardFunction() {
        if (mIsIpodServiceBind) {
            Log.d(TAG, "fastForwardFunction" + "Fast forward");

            if (mIsIpodServiceBind) {
                try {
                    sendBroadcast(new Intent("com.luyuan.try_cancel_mute"));
                    mIpodService.fastForward();

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void quitFunction() {
        Exception ee = new Exception();
        ee.printStackTrace();
        if (mIsIpodServiceBind) {
            Log.d(TAG, "fastForwardFunction" + "Fast forward");

            if (mIsIpodServiceBind) {
                try {
                    mIpodService.quit();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void playFunction() {
        if (mIsIpodServiceBind) {
            try {
                mIpodService.play();

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void pauseFunction() {
        if (mIsIpodServiceBind) {
            try {
                mIpodService.pause();
                //finish();

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void playPauseFunction() {
        if (mIsIpodServiceBind) {
            try {
                if (mIpodService.getPlayStatus() != IpodConstance.PlayStatus.PLAY) {
                    sendBroadcast(new Intent("com.luyuan.try_cancel_mute"));
                    mIpodService.play();
                } else {
                    try {
                        mIpodService.pause();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

    private void stopFunction() {
        if (mIsIpodServiceBind) {
            try {
                Log.d(TAG, "close--ipod:");
                mIpodService.closeIpod();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void shuffleFunction() {
        Log.d(TAG, "shuffle: " + shuffle_mode);
        byte temp = 0;
        if (mIsIpodServiceBind) {
            if (shuffle_mode == SHUFFLE_CLOSE) {
                temp = SHUFFLE_SONG;
            } else {
                temp = SHUFFLE_CLOSE;
            }
            if (temp == SHUFFLE_CLOSE || temp == SHUFFLE_SONG) {
                try {
                    mIpodService.setShuffleMode(temp);
                } catch (RemoteException e) {
                    // TODO: handle exception
                }
            }

        }
    }

    private void repeatFunction() {
        Log.d(TAG, "repeat: " + repeate_mode);
        byte temp = 0;
        if (mIsIpodServiceBind) {
            switch (repeate_mode) {
                case REPEATE_CLOSE:
                    temp = REPEATE_ONE;
                    break;
                case REPEATE_ONE:
                    temp = REPEATE_ALL;
                    break;
                case REPEATE_ALL:
                    temp = REPEATE_CLOSE;
                    break;
                default:
                    break;
            }

            if (temp == REPEATE_CLOSE || temp == REPEATE_ONE || temp == REPEATE_ALL) {
                try {
                    mIpodService.setRepeatMode(temp);
                } catch (RemoteException e) {
                    // TODO: handle exception
                }
            }

        }
    }

    private void doIpodInit() {
        if (mIsIpodServiceBind) {
            try {
                mIpodService.registerCallback(mCallback);
                boolean exist = mIpodService.getConnectionMode();
                Log.d(TAG, "connect:" + exist);
                mIpodService.openIpod();
                mIpodService.play();
                mIpodService.lookupTotalTime();
                mIpodService.lookupIpodStatus();
                mIpodService.lookupSongnameSingerAlbum();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

            }
        }

    };

    public static String secondToString(int second) {
        boolean negative = second < 0;
        second = java.lang.Math.abs(second);

        int sec = (int) (second % 60);
        second /= 60;
        int min = (int) (second % 60);
        second /= 60;
        int hours = (int) second;

        String time;
        DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        format.applyPattern("00");
        if (second > 0) {
            time = (negative ? "-" : "") + hours + ":" + format.format(min) + ":"
                    + format.format(sec);
        } else {
            time = (negative ? "-" : "") + min + ":" + format.format(sec);
        }
        return time;
    }

    private void setTimeText() {
        StringBuilder stringBuilder = new StringBuilder(secondToString(current_time));
        stringBuilder.append("  --  ").append(secondToString(total_time));
        mTextTimeName.setText(stringBuilder.toString());
    }

    private void resendCommand() {
        if (current_time > total_time || null == song_name || "".equals(song_name)) {
            if (mResendTime <= 0)
                return;
            mResendTime--;
            if (mIsIpodServiceBind) {
                try {
                    mIpodService.lookupTotalTime();
                    mIpodService.lookupIpodStatus();
                    mIpodService.lookupSongnameSingerAlbum();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showRadioPlayNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

        int icon = R.drawable.ipod;

        long when = System.currentTimeMillis();
        Notification mNotification = new Notification(icon, null, when);

        Context context = getApplicationContext();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        CharSequence contentTitle = getText(R.string.ipod_play);
        CharSequence contentText = getText(R.string.ipod_enter);
        mNotification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        mNotification.flags = mNotification.flags | Notification.FLAG_ONGOING_EVENT;

        mNotificationManager.notify(5, mNotification);
    }

}
