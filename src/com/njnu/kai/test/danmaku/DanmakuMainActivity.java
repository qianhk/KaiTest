package com.njnu.kai.test.danmaku;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;
import com.njnu.kai.test.R;
import com.njnu.kai.test.danmaku.controller.DanmakuFilters;
import com.njnu.kai.test.danmaku.controller.DrawHandler;
import com.njnu.kai.test.danmaku.controller.IDanmakuView;
import com.njnu.kai.test.danmaku.danmaku.loader.ILoader;
import com.njnu.kai.test.danmaku.danmaku.loader.IllegalDataException;
import com.njnu.kai.test.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import com.njnu.kai.test.danmaku.danmaku.model.BaseDanmaku;
import com.njnu.kai.test.danmaku.danmaku.model.DanmakuTimer;
import com.njnu.kai.test.danmaku.danmaku.model.android.DanmakuGlobalConfig;
import com.njnu.kai.test.danmaku.danmaku.model.android.Danmakus;
import com.njnu.kai.test.danmaku.danmaku.parser.BaseDanmakuParser;
import com.njnu.kai.test.danmaku.danmaku.parser.DanmakuFactory;
import com.njnu.kai.test.danmaku.danmaku.parser.IDataSource;
import com.njnu.kai.test.danmaku.danmaku.parser.android.BiliDanmukuParser;
import com.njnu.kai.test.danmaku.widget.DanmakuView;
import com.njnu.kai.test.support.FileUtils;
import com.njnu.kai.test.support.LogUtils;
import com.njnu.kai.test.support.ToastUtils;

import java.io.InputStream;

public class DanmakuMainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "DanmakuMainActivity";
    private IDanmakuView mDanmakuView;

    private View mMediaController;

    public PopupWindow mPopupWindow;

    private Button mBtnRotate;

    private Button mBtnHideDanmaku;

    private Button mBtnShowDanmaku;

    private BaseDanmakuParser mParser;

    private Button mBtnPauseDanmaku;

    private Button mBtnResumeDanmaku;

    private Button mBtnSendDanmaku;

    private long mPausedPosition;

    private Button mBtnSendDanmakus;

    private VideoView mVideoView;

    private TextView mMvPostion;
    private TextView mMvDuration;
    private SeekBar mProgressSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danmaku_main);
        DanmakuFilters filters = DanmakuFilters.getDefault();
        filters.registerFilter(DanmakuFilters.TAG_QUANTITY_DANMAKU_FILTER);
//        filters.registerFilter(DanmakuFilters.TAG_QUANTITY_FIX_DANMAKU_FILTER);

//        filters.registerFilter(DanmakuFilters.TAG_TYPE_DANMAKU_FILTER);
//        DanmakuFilters.TypeDanmakuFilter danmakuFilter = (DanmakuFilters.TypeDanmakuFilter)filters.get(DanmakuFilters.TAG_TYPE_DANMAKU_FILTER);
//        ArrayList<Integer> typeList = new ArrayList<Integer>();
//        typeList.add(BaseDanmaku.TYPE_FIX_BOTTOM);
//        typeList.add(BaseDanmaku.TYPE_FIX_TOP);
//        danmakuFilter.setData(typeList);

        findViews();

        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        LogUtils.w(TAG, "lookDanmaku onCreate isLandscape_%b", isLandscape);
        if (isLandscape) {
            onOrientationLandscape();
        } else {
            onOrientationPortrait();
        }
    }

    private BaseDanmakuParser createParser(InputStream stream) {

        if (stream == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }


        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;

    }

    private boolean mSeeking;

    private void findViews() {

        mMediaController = findViewById(R.id.media_controller);
        mBtnRotate = (Button)findViewById(R.id.rotate);
        mBtnHideDanmaku = (Button)findViewById(R.id.btn_hide);
        mBtnShowDanmaku = (Button)findViewById(R.id.btn_show);
        mBtnPauseDanmaku = (Button)findViewById(R.id.btn_pause);
        mBtnResumeDanmaku = (Button)findViewById(R.id.btn_resume);
        mBtnSendDanmaku = (Button)findViewById(R.id.btn_send);
        mBtnSendDanmakus = (Button)findViewById(R.id.btn_send_danmakus);
        mBtnRotate.setOnClickListener(this);
        mBtnHideDanmaku.setOnClickListener(this);
        mMediaController.setOnClickListener(this);
        mBtnShowDanmaku.setOnClickListener(this);
        mBtnPauseDanmaku.setOnClickListener(this);
        mBtnResumeDanmaku.setOnClickListener(this);
        mBtnSendDanmaku.setOnClickListener(this);
        mBtnSendDanmakus.setOnClickListener(this);
        mMvDuration = (TextView)findViewById(R.id.mv_duration);
        mMvPostion = (TextView)findViewById(R.id.mv_position);
        mProgressSeekBar = (SeekBar)findViewById(R.id.mv_progress_seek_bar);
        mProgressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mSeeking = true;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMvPostion.setText(formatLongToTimeStr(progress));
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSeeking = false;
                int progress = seekBar.getProgress();
                mVideoView.seekTo(progress);
                mDanmakuView.seekTo((long)progress);
            }
        });
        mVideoView = (VideoView)findViewById(R.id.videoview);
        mDanmakuView = (DanmakuView)findViewById(R.id.sv_danmaku);
        DanmakuGlobalConfig.DEFAULT.setDanmakuStyle(DanmakuGlobalConfig.DANMAKU_STYLE_STROKEN, 3).setDuplicateMergingEnabled(true);
        if (mDanmakuView != null) {
            mParser = createParser(this.getResources().openRawResource(R.raw.danmaku_comments));
            mDanmakuView.setCallback(new DrawHandler.Callback() {

                @Override
                public void updateTimer(DanmakuTimer timer) {
                    LogUtils.d(TAG, "updateTimer " + timer);
                }

                @Override
                public void prepared() {
                    mDanmakuView.start();
                }

                @Override
                public void drawingFinished() {
                    mDanmakuView.seekTo(0l);
                }
            });
            mDanmakuView.prepare(mParser);

            mDanmakuView.showFPS(true);
            mDanmakuView.enableDanmakuDrawingCache(true);
            ((View)mDanmakuView).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mMediaController.setVisibility(View.VISIBLE);
                }
            });
        }

        if (mVideoView != null) {
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    flushProgressView();
                    mHandler.sendEmptyMessageDelayed(WHAT_FLUSH_PROGRESS, FLUSH_PROGRESS_INTERVAL);
                }
            });
            String fileName = "test.mp4";
            String videoPath = Environment.getExternalStorageDirectory() + "/" + fileName;
            if (FileUtils.fileExists(videoPath)) {
                mVideoView.setVideoPath(videoPath);
            } else {
                ToastUtils.showToast(this, "请在sd卡根目录放置一个" + fileName);
            }
        }
//        mVideoView.setMediaController(new MediaController(this));
    }

    private void flushProgressView() {
        if (!mSeeking) {
            int position = mVideoView.getCurrentPosition();
            int duration = mVideoView.getDuration();
            mMvPostion.setText(formatLongToTimeStr(position));
            mMvDuration.setText(formatLongToTimeStr(duration));
            mProgressSeekBar.setProgress(position);
            mProgressSeekBar.setMax(duration);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v == mMediaController) {
            mMediaController.setVisibility(View.GONE);
        }
        if (mDanmakuView == null || !mDanmakuView.isPrepared())
            return;
        if (v == mBtnRotate) {
            setRequestedOrientation(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (v == mBtnHideDanmaku) {
            mDanmakuView.hide();
            //mPausedPosition = mDanmakuView.hideAndPauseDrawTask();
        } else if (v == mBtnShowDanmaku) {
            mDanmakuView.show();
            //mDanmakuView.showAndResumeDrawTask(mPausedPosition); // sync to the video time in your practice
        } else if (v == mBtnPauseDanmaku) {
            mDanmakuView.pause();
            mVideoView.suspend();
            mHandler.removeMessages(WHAT_FLUSH_PROGRESS);
        } else if (v == mBtnResumeDanmaku) {
            mDanmakuView.resume();
            mVideoView.resume();
            mHandler.sendEmptyMessageDelayed(WHAT_FLUSH_PROGRESS, FLUSH_PROGRESS_INTERVAL);
        } else if (v == mBtnSendDanmaku) {
            addDanmaku(true, 1000);
        } else if (v == mBtnSendDanmakus) {
            addDanmaku(false, 5000);
//            Boolean b = (Boolean)mBtnSendDanmakus.getTag();
//            timer.cancel();
//            if (b == null || !b) {
//                mBtnSendDanmakus.setText(R.string.cancel_sending_danmakus);
//                timer = new Timer();
//                timer.schedule(new AsyncAddTask(), 0, 1000);
//                mBtnSendDanmakus.setTag(true);
//            } else {
//                mBtnSendDanmakus.setText(R.string.send_danmakus);
//                mBtnSendDanmakus.setTag(false);
//            }
//            mDanmakuView.clear();
//            mDanmakuView.stop();
//            mDanmakuView.removeAllDanmakus();
//            mVideoView.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mParser = createParser(getResources().openRawResource(R.raw.danmaku_comments));
//                    mDanmakuView.prepare(mParser);
//                }
//            }, 2000);

        }
    }

//    Timer timer = new Timer();
//
//    class AsyncAddTask extends TimerTask {
//
//        @Override
//        public void run() {
//            for (int i = 0; i < 5; i++) {
//                addDanmaku(true);
//                SystemClock.sleep(20);
//            }
//        }
//    };


    private void addDanmaku(boolean islive, int duration) {
        BaseDanmaku danmaku = DanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        //for(int i=0;i<100;i++){
        //}
        String text = "凯凯测试弹幕" + System.nanoTime() + (islive ? "1/n测试弹幕第二行2" : "");
        DanmakuFactory.fillText(danmaku, text);
        danmaku.padding = 5;
        danmaku.priority = 1;
        danmaku.isLive = true;
        danmaku.time = mDanmakuView.getCurrentTime() + 1200;
//        danmaku.duration = new Duration(duration);
        danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = Color.WHITE;
        //danmaku.underlineColor = Color.GREEN;
        danmaku.borderColor = Color.GREEN;
        mDanmakuView.addDanmaku(danmaku);
    }

    private static final int WHAT_FLUSH_PROGRESS = 1;

    private static final int FLUSH_PROGRESS_INTERVAL = 500;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_FLUSH_PROGRESS:
                    flushProgressView();
                    sendEmptyMessageDelayed(WHAT_FLUSH_PROGRESS, FLUSH_PROGRESS_INTERVAL);
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * HH:MM:SS的格式
     *
     * @param time 毫秒
     * @return 字符串
     */
    public static String formatLongToTimeStr(long time) {
        final int millisFactor = 1000;
        final int timeFactor = 60;
        final int secondsOneHour = 3600;

        int totalSeconds = (int)(time / millisFactor);
        int seconds = totalSeconds % timeFactor;
        int minutes = (totalSeconds / timeFactor) % timeFactor;
        int hours = totalSeconds / secondsOneHour;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes,
                seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        boolean isLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (isLandscape) {
            onOrientationLandscape();
        } else {
            onOrientationPortrait();
        }
    }

    private void onOrientationPortrait() {
        DanmakuGlobalConfig.DEFAULT.setScaleTextSize(0.6f);
    }

    private void onOrientationLandscape() {
        DanmakuGlobalConfig.DEFAULT.setScaleTextSize(1.0f);
    }
}
