package com.njnu.kai.test;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.CursorWindow;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.PopupWindow;
import com.njnu.kai.test.danmaku.danmaku.util.AndroidUtils;
import com.njnu.kai.test.support.*;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-1-27
 */
public class HtcT328WActivity extends BaseActivity {

    private static final String TAG = "HtcT328WActivity";
    private static final int EVENT_SUCCESS = 0;

    private EditText mEditText;
    private StringBuilder mStrBuilder;
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            finish();
//        }
//    };

    private Random mRandom = new Random(System.currentTimeMillis());

    private static UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static int sThemeResId;

    public void flushBackgroundColor(int darkColor, int lightColor) {
//        LinearGradient linearGradient = new LinearGradient(0, DisplayUtils.getHeightPixels(), DisplayUtils.getWidthPixels(), 0
//                , darkColor, lightColor, Shader.TileMode.CLAMP);
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{darkColor, lightColor});
        findViewById(R.id.layout_entry).setBackgroundDrawable(drawable);
        findViewById(R.id.view_left).setBackgroundColor(darkColor);
        findViewById(R.id.view_right).setBackgroundColor(lightColor);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public void onClick(View v) {
            final int viewId = v.getId();
            if (viewId == R.id.view_left) {
                sThemeResId = R.style.WhiteTheme;
                recreate();
            } else if (viewId == R.id.view_right) {
                sThemeResId = R.style.OrangeTheme;
                recreate();
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PopupWindow popupWindow = new PopupWindow(this);
        popupWindow.dismiss();

        if (sThemeResId != 0) {
            setTheme(sThemeResId);
        }

        setContentView(R.layout.htct328);
        mEditText = (EditText) findViewById(R.id.edt_text);
        mEditText.setGravity(Gravity.TOP | Gravity.LEFT);

        mStrBuilder = new StringBuilder();
        hasSWindowToPidMapField();

        mStrBuilder.append(String.format("\n\nwidth=%d height=%d\n", DisplayUtils.getWidthPixels(), DisplayUtils.getHeightPixels()));

        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        mStrBuilder.append("\n" + externalStorageDirectory.getAbsolutePath() + "\n" + Environment.getExternalStorageState());

        String path = "sdcard";
        File pathFile = new File(path);
        if (!pathFile.exists() || !pathFile.canWrite()) {
            pathFile = android.os.Environment.getExternalStorageDirectory();
            if (!pathFile.canWrite()) {
                File parentFile = pathFile.getParentFile();
                File[] listFiles = parentFile.listFiles();
                for (int i = 0; listFiles != null && i < listFiles.length; i++) {
                    if (listFiles[i].isDirectory() && listFiles[i].canWrite()) {
                        pathFile = listFiles[i];
                        break;
                    }
                }
            }
        }
        try {
            path = pathFile.getCanonicalPath();
        } catch (Exception ignored) {
            path = pathFile.getAbsolutePath();
        }
        mStrBuilder.append("\npath=" + path);
        mStrBuilder.append("\ngetExternalStoragePublicDirectory(kai)=" + Environment.getExternalStoragePublicDirectory("kai").getAbsolutePath());
        mStrBuilder.append("\ngetExternalStoragePublicDirectory(MUSIC)=" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath());

        mStrBuilder.append("\ngetExternalCacheDir=" + (getExternalCacheDir() != null ? getExternalCacheDir().getAbsolutePath() : "null"));
        mStrBuilder.append("\ngetCacheDir=" + getCacheDir().getAbsolutePath());
        mStrBuilder.append("\ngetFilesDir=" + getFilesDir().getAbsolutePath());
        mStrBuilder.append("\nsourceDir=" + getApplicationInfo().sourceDir);


        IdListResult idListResult = JSONUtils.fromJsonString("{\"code\":1,\"msg\":\"ok\"}"
                , IdListResult.class);
        idListResult = JSONUtils.fromJsonString("{\"code\":1,\"msg\":\"ok\",\"data\":[\"96158474\",\"96158472\"]}"
                , IdListResult.class);


        idListResult = null;

        doGetFastScroller();

        ColorDrawable colorDrawable = new ColorDrawable(0x23456789);
        int colorDrawableColor = getColorDrawableColor(colorDrawable);
        mStrBuilder.append(String.format("\ncolorDrawable %08X", colorDrawableColor));
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            mStrBuilder.append("\nversionName=" + info.versionName); // 版本名
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String testStr = "\ntest_";
        testStr += ('b' + "_kai");
        mStrBuilder.append(testStr);
        mStrBuilder.append("\nmemoryClass=" + AndroidUtils.getMemoryClass(this) + "MB");
        Runtime runtime = Runtime.getRuntime();
        mStrBuilder.append(String.format("\nprocess=%d freeM=%s maxM=%s totalM=%s", runtime.availableProcessors()
                , StringUtils.readableByte(runtime.freeMemory()), StringUtils.readableByte(runtime.maxMemory())
                , StringUtils.readableByte(runtime.totalMemory())));
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String umengAppkey = appInfo.metaData.getString("UMENG_APPKEY");
            mStrBuilder.append("\numengAppkey=" + umengAppkey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        resetAsyncTaskDefaultExecutor();

        flushBackgroundColor(mRandom.nextInt() | 0xFF000000, mRandom.nextInt() | 0xFF000000);
//        new KaiAsyncTask().execute();

        final String dataString = "appshare?gid=1234&pid=5678";
        if (!StringUtils.isEmpty(dataString)) {
            Pattern pattern = Pattern.compile("gid=(\\d+)&pid=(\\d+)");
            final Matcher matcher = pattern.matcher(dataString);
            final boolean find = matcher.find();
            LogUtils.i(TAG, "onMatch find_%b", find);
            if (find) {
                final int start = matcher.start();
                final int end = matcher.end();
                final int groupCount = matcher.groupCount();
                final String group0 = matcher.group(0);

                LogUtils.i(TAG, "onMatch %d %d %d %s", start, end, groupCount, group0);

                final int start1 = matcher.start(1);
                final int end1 = matcher.end(1);
                final String group1 = matcher.group(1);
                LogUtils.i(TAG, "onMatch %d %d %s", start1, end1, group1);
            }
        }

        sURIMatcher.addURI(getClass().getName(), "abc/def/*", EVENT_SUCCESS);
        final Uri uri1 = Uri.parse("content://" + getClass().getName() + "/abc/def/-123");
        final Uri uri2 = Uri.parse("content://" + getClass().getName() + "/abc/def/123");
        final Uri uri3 = Uri.parse("content://" + getClass().getName() + "/abc/def/ghi");
        final Uri uri4 = Uri.parse("content://" + getClass().getName() + "/abc/def/");
        mStrBuilder.append(String.format("\nmatch1=%d match2=%d match3=%d match4=%d\n"
                , sURIMatcher.match(uri1), sURIMatcher.match(uri2), sURIMatcher.match(uri3), sURIMatcher.match(uri4)));

        mEditText.setText(mStrBuilder.toString());

        findViewById(R.id.view_left).setOnClickListener(mOnClickListener);
        findViewById(R.id.view_right).setOnClickListener(mOnClickListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mHandler.sendEmptyMessageDelayed(0, 3000);
        lookTopActivityPkgName();
    }

    @Override
    protected void onPause() {
        super.onPause();
        lookTopActivityPkgName();
    }

    private void lookTopActivityPkgName() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> runningTaskInfoList = activityManager.getRunningTasks(1);
        final ActivityManager.RunningTaskInfo runningTaskInfo = runningTaskInfoList != null && !runningTaskInfoList.isEmpty()
                ? runningTaskInfoList.get(0) : null;
        LogUtils.d(TAG, "lookTopActivityPkgName pkg name %s", runningTaskInfo != null ? runningTaskInfo.topActivity.getPackageName() : null);
        final List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

        try {
            final int PROCESS_STATE_TOP = 2;
            ActivityManager.RunningAppProcessInfo topApp = null;
            Field field = ActivityManager.RunningAppProcessInfo.class.getField("processState");
            for (ActivityManager.RunningAppProcessInfo app : runningAppProcesses) {
                if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                        app.importanceReasonCode == 0) {
                    Integer state = null;
                    state = field.getInt(app);
                    if (state != null && state == PROCESS_STATE_TOP) {
                        topApp = app;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        needPermissionForBlocking(this);

        if (SDKVersionUtils.hasLollipop()) {
            try {
                getProcessNew();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //API 21 and above
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private String getProcessNew() {
        String topPackageName = null;
        UsageStatsManager usage = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_BEST, time - 10 * 1000, time);
        if (stats != null) {
            SortedMap<Long, UsageStats> runningTask = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {
                runningTask.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (runningTask.isEmpty()) {
                return null;
            }
            topPackageName = runningTask.get(runningTask.lastKey()).getPackageName();
        }
        return topPackageName;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean needPermissionForBlocking(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode != AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public class KaiAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finish();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            finish();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
//            finish();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public int getColorDrawableColor(ColorDrawable colorDrawable) {
        if (SDKVersionUtils.hasHoneycomb()) {
            return colorDrawable.getColor();
        } else {
            try {
                Field stateField = ColorDrawable.class.getDeclaredField("mState");
                stateField.setAccessible(true);
                Object object = stateField.get(colorDrawable);
                Field colorField = object.getClass().getDeclaredField("mUseColor");
                colorField.setAccessible(true);
                Number colorObj = (Number) colorField.get(object);
                return colorObj.intValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Color.TRANSPARENT;
        }
    }

    private void hasSWindowToPidMapField() {
        try {
            Field[] declaredFields = CursorWindow.class.getDeclaredFields();
            boolean hassWindowToPidMapField = false;
            for (Field declaredField : declaredFields) {
                if (declaredField.toString().contains("sWindowToPidMap")) {
                    mStrBuilder.append("\nfield:" + declaredField.toString() + "\n");
                    hassWindowToPidMapField = true;
                    break;
                }
            }
            mStrBuilder.append(String.format("\nhasSWindowToPidMapField=%b", hassWindowToPidMapField));
        } catch (Throwable e) {
            mStrBuilder.append("hasSWindowToPidMapField has Exception: \n");
            mStrBuilder.append(e.toString());
        }
    }

    private void doGetFastScroller() {
        try {
            final Field defaultHandler = AbsListView.class.getDeclaredField("mFastScroller");
            defaultHandler.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
//        if (Looper.getMainLooper() != Looper.myLooper()) {
//            throw new AssertionError("KAI_FINISH_NOT_IN_MAIN_THREAD activity=" + this.getClass().getName());
//        }
        super.finish();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void resetAsyncTaskDefaultExecutor() {
        try {
            ThreadPoolExecutor.DiscardOldestPolicy discardOldestPolicy = new ThreadPoolExecutor.DiscardOldestPolicy();
            final Field defaultHandler = ThreadPoolExecutor.class.getDeclaredField("defaultHandler");
            defaultHandler.setAccessible(true);
            defaultHandler.set(null, discardOldestPolicy);
            if (SDKVersionUtils.hasHoneycomb()) {
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) AsyncTask.THREAD_POOL_EXECUTOR;
                threadPoolExecutor.setRejectedExecutionHandler(discardOldestPolicy);
                Method setDefaultExecutorMethod = AsyncTask.class.getMethod("setDefaultExecutor", Executor.class);
                setDefaultExecutorMethod.invoke(null, threadPoolExecutor);
            } else {
                Field sExecutor = AsyncTask.class.getDeclaredField("sExecutor");
                sExecutor.setAccessible(true);
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) sExecutor.get(null);
                threadPoolExecutor.setRejectedExecutionHandler(discardOldestPolicy);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
