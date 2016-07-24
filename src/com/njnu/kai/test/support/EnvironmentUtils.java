package com.njnu.kai.test.support;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Environment utility class
 *
 */
public class EnvironmentUtils {
    private static final String TAG = "EnvironmentUtils";

    private static String mPackageName;
    private static String mSDCardPath;
    private static String mSecondSDCardPath;
    private static String mSecondSDCardValidFolder;
    private static final String HIDE_PATH_KEY_WORD = File.separator + ".";

    static {
        String path = "sdcard";
        File pathFile = new File(path);
        if (!pathFile.exists() || !pathFile.canWrite()) {
            pathFile = Environment.getExternalStorageDirectory();
            if (!pathFile.canWrite()) {
                File parentFile = pathFile.getParentFile();
                File[] listFiles = parentFile.listFiles();
                for (int i = 0; i < listFiles.length; i++) {
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
        mSDCardPath = path;
    }

    /**
     * 初始化系统环境
     *
     * @param context 系统环境上下文
     */
    public static void init(Context context) {
        Config.init(context);
        Network.init(context);

        GeneralParameters.init(context);
        mPackageName = context.getPackageName();
        resetAsyncTaskDefaultExecutor();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void resetAsyncTaskDefaultExecutor() {
        try {
            ThreadPoolExecutor.DiscardOldestPolicy discardOldestPolicy = new ThreadPoolExecutor.DiscardOldestPolicy();
            final Field defaultHandler = ThreadPoolExecutor.class.getDeclaredField("defaultHandler");
            defaultHandler.setAccessible(true);
            defaultHandler.set(null, discardOldestPolicy);
            if (SDKVersionUtils.hasHoneycomb()) {
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)AsyncTask.THREAD_POOL_EXECUTOR;
                threadPoolExecutor.setRejectedExecutionHandler(discardOldestPolicy);
                Method setDefaultExecutorMethod = AsyncTask.class.getMethod("setDefaultExecutor", Executor.class);
                setDefaultExecutorMethod.invoke(null, threadPoolExecutor);
            } else {
                Field sExecutor = AsyncTask.class.getDeclaredField("sExecutor");
                sExecutor.setAccessible(true);
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)sExecutor.get(null);
                threadPoolExecutor.setRejectedExecutionHandler(discardOldestPolicy);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取包名
     *
     * @return 包名
     */
    public static String getPackageName() {
        return mPackageName;
    }

    /**
     * 存储信息
     */
    public static class Storage {

        private static final String APP_NAME_IN_LOWER_CASE = "app";

        /**
         * 外部存储是否可读写
         *
         * @return 可读写返回true, 否则返回false
         */
        public static boolean isExternalStorageWritable() {
            String state = Environment.getExternalStorageState();
            return Environment.MEDIA_MOUNTED.equals(state);
        }

        /**
         * 外部存储是否可读
         *
         * @return 可读返回true, 否则返回false
         */
        public static boolean isExternalStorageReadable() {
            String state = Environment.getExternalStorageState();
            return Environment.MEDIA_MOUNTED.equals(state)
                    || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
        }

        /**
         * 获取目录可用字节数，目录不存在返回0
         *
         * @param path 目录文件
         * @return 字节数
         */
        public static long getUsableSpace(File path) {
            if (SDKVersionUtils.hasGingerbread()) {
                return path.getUsableSpace();
            }

            try {
                final StatFs stats = new StatFs(path.getPath());
                return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }

        /**
         * 获取外部目录缓存路径
         *
         * @param context context
         * @return 外部存储换成路径
         */
        public static File getExternalCacheDir(Context context) {
            File file = null;
            if (SDKVersionUtils.hasFroyo()) {
                file = context.getExternalCacheDir();
            }

            if (file == null) {
                final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
                file = new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
            }

            file.mkdirs();

            if (file.isDirectory()) {
                return file;
            }

            return null;
        }

        /**
         * 获取SDCard路径
         *
         * @return sdcard路径，不为空
         */
        public static String getSDCardPath() {
            return mSDCardPath;
        }

        /**
         * 获取缓存路径
         *
         * @param context context
         * @return 存储路径
         */
        public static String getCachePath(Context context) {
            File file = null;
            if (isExternalStorageWritable()) {
                file = getExternalCacheDir(context);
            }

            return (file != null) ? file.getAbsolutePath() : context.getCacheDir().getAbsolutePath();
        }

        /**
         * 判断是否路径是否可写
         *
         * @param path path
         * @return writable
         */
        public static boolean isWritablePath(String path) {
            if (!new File(path).canWrite()) {
                return false;
            }
            String filePathPrefix = path + File.separator;
            int i = 0;
            while (FileUtils.fileExists(filePathPrefix + i)) {
                i++;
            }
            File testFile = FileUtils.createFile(filePathPrefix + i);

            if (testFile != null) {
                testFile.delete();
                return true;
            }

            return false;
        }

        /**
         * 得到可用路径
         *
         * @param context context
         * @return writablePath
         */
        public static String getWritablePath(Context context) {
            String secondSDCardPath = getSecondSDCardPath(context);
            if (!StringUtils.isEmpty(secondSDCardPath) && FileUtils.exists(secondSDCardPath)) {
                return SDKVersionUtils.hasKitKat() ? mSecondSDCardValidFolder : secondSDCardPath + File.separator + APP_NAME_IN_LOWER_CASE;
            } else {
                return mSDCardPath + File.separator + APP_NAME_IN_LOWER_CASE;
            }
        }

        /**
         * 获取第二张SD卡路径
         *
         * @param context context
         * @return 第二张SD卡路径
         */
        public static String getSecondSDCardPath(Context context) {
            if (mSecondSDCardPath != null) {
                return mSecondSDCardPath;
            }
            try {
                mSecondSDCardPath = searchSecondSdCardPathV1(context);
                if (!StringUtils.isEmpty(mSecondSDCardPath)) {
                    return mSecondSDCardPath;
                }
                mSecondSDCardPath = searchSecondSdCardPathV2(context);
                if (!StringUtils.isEmpty(mSecondSDCardPath)) {
                    return mSecondSDCardPath;
                }
                mSecondSDCardPath = searchSecondSdCardPathV3();
                if (!StringUtils.isEmpty(mSecondSDCardPath)) {
                    return mSecondSDCardPath;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return mSecondSDCardPath;
        }

        /**
         * SD card 指示器
         */
        public enum SDCardIndicator {
            /**
             * 第一个sd card
             */
            FIRST_SD_CARD,
            /**
             * 第二个sd card
             */
            SECOND_SD_CARD
        }


        /**
         * 获取SD卡的应用私有的files文件路径
         *
         * @param sdCardIndicator sd卡指示器
         * @param context         context
         * @return $SDCardPath$/Android/data/packagename
         */
        public static String getAppFolderPath(Context context, SDCardIndicator sdCardIndicator) {
            String sdCardPath = null;
            if (SDCardIndicator.FIRST_SD_CARD == sdCardIndicator) {
                sdCardPath = getSDCardPath();
            } else if (SDCardIndicator.SECOND_SD_CARD == sdCardIndicator) {
                sdCardPath = getSecondSDCardPath(context);
            }

            sdCardPath = StringUtils.isEmpty(sdCardPath) ? ConstantUtils.BLANK_STRING : sdCardPath;
            return new StringBuffer(sdCardPath)
                    .append(File.separator)
                    .append("Android")
                    .append(File.separator)
                    .append("data")
                    .append(File.separator)
                    .append(EnvironmentUtils.getPackageName()).toString();
        }


        /**
         * 此方法主要针对 4.4以上
         *
         * @param context context
         * @return SDCard path
         * @throws Exception exception
         */
        private static String searchSecondSdCardPathV1(Context context) {
            if (mSecondSDCardValidFolder == null) {
                getSecondSdCardAppFolder(context);
            }

            if (StringUtils.isEmpty(mSecondSDCardValidFolder)) {
                return ConstantUtils.BLANK_STRING;
            }
            StringBuffer sb = new StringBuffer(File.separator);
            sb.append("Android");
            sb.append(File.separator);
            sb.append("data");
            sb.append(File.separator);
            sb.append(mPackageName);
            return mSecondSDCardValidFolder.replaceAll(sb.toString(), "");
        }

        private static String getSecondSdCardAppFolder(Context context) {
            if (mSecondSDCardValidFolder != null) {
                return mSecondSDCardValidFolder;
            }
            File[] externalDataDirectories = ContextCompat.getExternalFilesDirs(context, null);
            if (externalDataDirectories == null || externalDataDirectories.length < 2 || externalDataDirectories[1] == null) {
                mSecondSDCardValidFolder = ConstantUtils.BLANK_STRING;
            } else {
                try {
                    mSecondSDCardValidFolder = externalDataDirectories[1].getCanonicalPath().replaceAll(File.separator + "files", "");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (mSecondSDCardValidFolder == null) {
                        mSecondSDCardValidFolder = ConstantUtils.BLANK_STRING;
                    }
                }
            }
            return mSecondSDCardValidFolder;
        }

        /**
         * 此方法主要针对 3.0以上 4.4以下和部分2.3
         *
         * @param context context
         * @return SDCard path
         * @throws Exception exception
         */
        private static String searchSecondSdCardPathV2(Context context) throws Exception {
            String tmpPath = ConstantUtils.BLANK_STRING;
            if (SDKVersionUtils.hasHoneycomb()) {
                File file = null;
                StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
                String[] paths = (String[]) sm.getClass().getMethod("getVolumePaths", null).invoke(sm, null);
                for (String path : paths) {
                    file = new File(path);
                    if (file.canWrite() && !FileUtils.isSamePhysicalPath(path, mSDCardPath)) {
                        tmpPath = file.getCanonicalPath();
                        break;
                    }
                }
            }
            return tmpPath;
        }


        private static ArrayList<String> mExtraSDPaths = new ArrayList<String>();
        private static final int MIN_SD_REMAIN_SIZE = 3 * ConstantUtils.KILO * ConstantUtils.KILO;

        /**
         * 此方法主要针对 方法1和方法2以外以上的情况
         *
         * @return SDCard path
         */
        private static String searchSecondSdCardPathV3() {
            if (!mExtraSDPaths.isEmpty()) {
                return mExtraSDPaths.get(0);
            }
            String extraSDPath = "";
            File root = File.listRoots()[0];
            File[] files = root.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (pathname.getName().contains("dev")) {
                        return false;
                    }
                    return true;
                }
            });
            for (int i = 0, len = files.length; i < len; i++) {
                File file = files[i];
                if (file.isDirectory() && file.canRead() && file.canWrite()) {
                    checkSDPath(file);
                } else {
                    File[] subFile = file.listFiles();
                    if (subFile == null) {
                        continue;
                    }
                    for (int j = 0, length = subFile.length; j < length; j++) {
                        checkSDPath(subFile[j]);
                    }
                }

            }
            if (!mExtraSDPaths.isEmpty()) {
                extraSDPath = mExtraSDPaths.get(0);
            }
            return extraSDPath;
        }

        private static void checkSDPath(File file) {
            if (file != null && file.canRead() && file.canWrite()) {
                if (getUsableSpace(file) > MIN_SD_REMAIN_SIZE) {
                    filterPath(file.getAbsolutePath());
                }
            }
        }

        private static void filterPath(String newPath) {
            if (isHidePath(newPath) || FileUtils.isSamePhysicalPath(newPath, mSDCardPath)) {
                return;
            }
            if (mExtraSDPaths.isEmpty()) {
                mExtraSDPaths.add(newPath);
            } else {
                boolean isEqual = false;
                for (String str : mExtraSDPaths) {
                    if (str.contains(newPath) || newPath.contains(str)) {
                        isEqual = true;
                    }
                }
                if (!isEqual) {
                    mExtraSDPaths.add(newPath);
                }
            }
        }

        private static boolean isHidePath(String path) {
            return path.contains(HIDE_PATH_KEY_WORD);
        }


    }

    /**
     * CPU信息
     */
    public static class CPU {
        /** Android CPU Families */
        /**
         * Unknown cpu family
         */
        public static final int CPU_FAMILY_UNKNOWN = 0;
        /**
         * ARM cpu family
         */
        public static final int CPU_FAMILY_ARM = 1;
        /**
         * X86 cpu family
         */
        public static final int CPU_FAMILY_X86 = 2;
        /**
         * MIPS cpu family
         */
        public static final int CPU_FAMILY_MIPS = 3;

        /** ARM cpu features */
        /**
         * ARM_FEATURE_VFP3
         */
        public static final int ARM_FEATURE_VFP3 = (1 << 1);
        /**
         * ARM_FEATURE_NEON
         */
        public static final int ARM_FEATURE_NEON = (1 << 2);
        /**
         * ARM_FEATURE_VFP2
         */
        public static final int ARM_FEATURE_VFP2 = (1 << 4);
        /**
         * ARM_FEATURE_VFP
         */
        public static final int ARM_FEATURE_VFP = ARM_FEATURE_VFP2 | ARM_FEATURE_VFP3;

        /** X86 cpu features */
        /**
         * X86_FEATURE_SSE3
         */
        public static final int X86_FEATURE_SSE3 = (1 << 0);
        /**
         * X86_FEATURE_POPCNT
         */
        public static final int X86_FEATURE_POPCNT = (1 << 1);
        /**
         * X86_FEATURE_MOVBE
         */
        public static final int X86_FEATURE_MOVBE = (1 << 2);

        /** ARM architecture */
        /**
         * ARM architecture unknown
         */
        public static final int ARM_ARCH_UNKNOWN = 0;
        /**
         * ARM architecture v5
         */
        public static final int ARM_ARCH_5 = 5;
        /**
         * ARM architecture v6
         */
        public static final int ARM_ARCH_6 = 6;
        /**
         * ARM architecture v7
         */
        public static final int ARM_ARCH_7 = 7;

        static {
            try {
                System.loadLibrary("environmentutils_cpu");
            } catch (UnsatisfiedLinkError error) {
                error.printStackTrace();
            }
        }

        /**
         * 获取CPU family
         *
         * @return {@link #CPU_FAMILY_ARM}, {@link #CPU_FAMILY_X86} or {@link #CPU_FAMILY_MIPS}
         */
        public static native int cpuFamily();

        /**
         * 获取CPU feature
         *
         * @return {@link #CPU_FAMILY_ARM}返回ARM_FEATURE_XXX, {@link #CPU_FAMILY_X86}返回X86_FEATURE_XXX
         */
        public static native int cpuFeatures();

        /**
         * 返回ARM architecture
         *
         * @return {@link #CPU_FAMILY_ARM}返回{@link #ARM_ARCH_5}, {@link #ARM_ARCH_6} or {@link #ARM_ARCH_7}, 否则返回{@link #ARM_ARCH_UNKNOWN}
         */
        public static native int armArch();
    }

    /**
     * 网络信息
     */
    public static class Network {
        /**
         * 无网络
         */
        public static final int NETWORK_INVALID = -1;
        /**
         * 2G网络
         */
        public static final int NETWORK_2G = 0;
        /**
         * wap网络
         */
        public static final int NETWORK_WAP = 1;
        /**
         * wifi网络
         */
        public static final int NETWORK_WIFI = 2;
        /**
         * 3G网络
         */
        public static final int NETWORK_3G = 3;
        /**
         * 4G网络
         */
        public static final int NETWORK_4G = 4;

        private static final int[] NETWORK_MATCH_TABLE = {NETWORK_2G // NETWORK_TYPE_UNKNOWN
                , NETWORK_2G // NETWORK_TYPE_GPRS
                , NETWORK_2G // NETWORK_TYPE_EDGE
                , NETWORK_3G // NETWORK_TYPE_UMTS
                , NETWORK_2G // NETWORK_TYPE_CDMA
                , NETWORK_3G // NETWORK_TYPE_EVDO_O
                , NETWORK_3G // NETWORK_TYPE_EVDO_A
                , NETWORK_2G // NETWORK_TYPE_1xRTT
                , NETWORK_3G // NETWORK_TYPE_HSDPA
                , NETWORK_3G // NETWORK_TYPE_HSUPA
                , NETWORK_3G // NETWORK_TYPE_HSPA
                , NETWORK_2G // NETWORK_TYPE_IDEN
                , NETWORK_3G // NETWORK_TYPE_EVDO_B
                , NETWORK_4G // NETWORK_TYPE_LTE
                , NETWORK_3G // NETWORK_TYPE_EHRPD
                , NETWORK_3G // NETWORK_TYPE_HSPAP
        };

        private static String mIMEI = "";
        private static String mIMSI = "";
        private static String mWifiMac = "";

        private static NetworkInfo mNetworkInfo;
        private static int mDefaultNetworkType;
        private static ConnectivityManager mConnectManager;

        /**
         * 初始化默认网络参数
         *
         * @param context 上下文环境
         */
        public static void init(Context context) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            mIMEI = telephonyManager.getDeviceId();
            if (mIMEI == null) {
                mIMEI = "";
            }

            mIMSI = telephonyManager.getSubscriberId();
            if (mIMSI == null) {
                mIMSI = "";
            }

            mWifiMac = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();
            if (mWifiMac == null) {
                mWifiMac = "";
            }

            mDefaultNetworkType = NETWORK_MATCH_TABLE[telephonyNetworkType(context)];
            mConnectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            mNetworkInfo = mConnectManager.getActiveNetworkInfo();
        }


        /**
         * 获取IMEI串号
         *
         * @return IMEI串号。<b>有可能为空值</b>
         */
        public static String imei() {
            return mIMEI;
        }

        /**
         * 获取IMSI移动用户识别码
         *
         * @return IMSI移动用户识别码。<b>有可能为空值</b>
         */
        public static String imsi() {
            return mIMSI;
        }

        /**
         * 获取Wifi Mac地址
         *
         * @return Wifi Mac地址
         */
        public static String wifiMac() {
            return mWifiMac;
        }

        /**
         * 获取网络类型
         *
         * @return 网络类型
         */
        public static int type() {
            int networkType = mDefaultNetworkType;
            if (mConnectManager == null) {
                //当还未来得及初始化时，另一线程请求网络时通用参数中取此值时先运行到这儿，那么如不做处理将崩溃
                return NETWORK_WAP;
            }
            mNetworkInfo = mConnectManager.getActiveNetworkInfo();
            if (!networkConnected(mNetworkInfo)) {
                networkType = NETWORK_INVALID;
            } else if (isWifiNetwork(mNetworkInfo)) {
                networkType = NETWORK_WIFI;
            } else if (isWapNetwork(mNetworkInfo)) {
                networkType = NETWORK_WAP;
            }

            return networkType;
        }

        /**
         * 是否存在有效的网络连接.
         *
         * @return 存在有效的网络连接返回true，否则返回false
         */
        public static boolean isNetWorkAvailable() {
            return networkConnected(mConnectManager.getActiveNetworkInfo());
        }

        /**
         * 获取本机IPv4地址
         *
         * @return 本机IPv4地址
         */
        public static String ipv4() {
            return ipAddress(true);
        }

        /**
         * 获取本机IPv6地址
         *
         * @return 本机IPv6地址
         */
        public static String ipv6() {
            return ipAddress(false);
        }

        private static String ipAddress(boolean useIPv4) {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                    NetworkInterface netInterface = en.nextElement();
                    for (Enumeration<InetAddress> iNetEnum = netInterface.getInetAddresses(); iNetEnum.hasMoreElements();) {
                        InetAddress inetAddress = iNetEnum.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            String address = inetAddress.getHostAddress();
                            if (useIPv4 == InetAddressUtils.isIPv4Address(address)) {
                                return address;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        /**
         * 直接从系统函数里得到的network type
         *
         * @param context context
         * @return net type
         */
        private static int telephonyNetworkType(Context context) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int networkType = telephonyManager.getNetworkType();
            if (networkType < 0 || networkType >= NETWORK_MATCH_TABLE.length) {
                networkType = TelephonyManager.NETWORK_TYPE_UNKNOWN;
            }
            return networkType;
        }

        private static boolean networkConnected(NetworkInfo networkInfo) {
            return networkInfo != null && networkInfo.isConnected();
        }

        private static boolean isMobileNetwork(NetworkInfo networkInfo) {
            return networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }

        @SuppressWarnings("deprecation")
        private static boolean isWapNetwork(NetworkInfo networkInfo) {
            return isMobileNetwork(networkInfo) && !StringUtils.isEmpty(android.net.Proxy.getDefaultHost());
        }

        private static boolean isWifiNetwork(NetworkInfo networkInfo) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
    }

    /**
     * @author hu.cao
     * @version 7.0.0
     *          配置信息，来自与assets/config，build，channel文件
     */
    public static class Config {
        private static final String TAG = "Config";

        private static final String ASSET_PATH_CONFIG = "config";
        private static final String ASSET_PATH_CHANNEL = "channel";
        private static final String ASSET_PATH_BUILD = "build";

        /**
         * 预定的几个通用键值
         */
        private static final String XML_ATTRIBUTE_VALUE_APP_VERSION = "app_version";
        private static final String XML_ATTRIBUTE_VALUE_VERSION_NAME = "version_name";
        private static final String XML_ATTRIBUTE_VALUE_VERIFICATION_ENABLE = "verification_enable";
        private static final String XML_ATTRIBUTE_VALUE_URL_PRINT_ENABLE = "url_print_enable";
        private static final String XML_ATTRIBUTE_VALUE_TEST_MODE = "test_mode";
        private static final String XML_ATTRIBUTE_VALUE_APP_CHECKUPDATE_ENABLE = "app_checkupdate_enable";
        private static final String XML_ATTRIBUTE_VALUE_LOG_ENABLE = "log_enable";
        private static final String XML_ATTRIBUTE_VALUE_AD_SDK_ENABLE = "ad_sdk_enable";
        private static final String XML_ATTRIBUTE_VALUE_UPDATE_CATEGORY = "update_category";
        private static final String XML_ATTRIBUTE_VALUE_NO_AD_CHANNELS = "no_ad_channels";
        private static final String XML_ATTRIBUTE_VALUE_NO_SHORTCUT_CHANNELS = "no_shortcut_channels";
        private static final String XML_ATTRIBUTE_VALUE_360UNION_ENABLE = "360union_enable";
        private static final String XML_ATTRIBUTE_VALUE_USE_PRE_ENVIRONMENT_ENABLE = "use_pre_environment";

        private static final int RADIX_16 = 16;

        private static String mAppVersion = "";
        private static String mVersionName = "";
        private static boolean mVerificationEnable = false;
        private static boolean mUrlPrintEnable = false;
        private static boolean mTestMode = false;
        private static boolean mAppCheckUpdateEnable = true;
        private static boolean mLogEnable = true;
        private static boolean mAdSdkEnable = true;
        private static String mChannel = "";
        private static String mALChannel = "";
        private static String mBuildId = "";
        private static String mUpdateCategory = "";
        private static String mNoAdChannels = "";
        private static String mNoShortcutChannels = "";
        private static boolean m360UnionEnable = false;
        private static boolean mUsePreEnvironment = false;

        private static Map<String, String> mConfigMap;
        private static boolean mInitialized;

        /**
         * 初始化配置文件
         *
         * @param context 上下文
         */
        public static void init(Context context) {
            initConfig(context);
            initBuild(context);
            initChannel(context);
        }

        private static void initConfig(Context context) {
            InputStream inputStream = null;
            try {
                inputStream = context.getAssets().open(ASSET_PATH_CONFIG);
                mConfigMap = XmlUtils.parse(inputStream);
                parseConfig();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private static void parseConfig() {
            try {
                mAppVersion = mConfigMap.get(XML_ATTRIBUTE_VALUE_APP_VERSION);
                mVerificationEnable = parseBoolean(mConfigMap.get(XML_ATTRIBUTE_VALUE_VERIFICATION_ENABLE), false);
                mVersionName = mConfigMap.get(XML_ATTRIBUTE_VALUE_VERSION_NAME);
                mUrlPrintEnable = parseBoolean(mConfigMap.get(XML_ATTRIBUTE_VALUE_URL_PRINT_ENABLE), false);
                mTestMode = parseBoolean(mConfigMap.get(XML_ATTRIBUTE_VALUE_TEST_MODE), false);
                mAppCheckUpdateEnable = parseBoolean(mConfigMap.get(XML_ATTRIBUTE_VALUE_APP_CHECKUPDATE_ENABLE), true);
                mLogEnable = parseBoolean(mConfigMap.get(XML_ATTRIBUTE_VALUE_LOG_ENABLE), true);
                mAdSdkEnable = parseBoolean(mConfigMap.get(XML_ATTRIBUTE_VALUE_AD_SDK_ENABLE), true);
                mUpdateCategory = mConfigMap.get(XML_ATTRIBUTE_VALUE_UPDATE_CATEGORY);
                mNoAdChannels = mConfigMap.get(XML_ATTRIBUTE_VALUE_NO_AD_CHANNELS);
                mNoShortcutChannels = mConfigMap.get(XML_ATTRIBUTE_VALUE_NO_SHORTCUT_CHANNELS);
                m360UnionEnable = parseBoolean(mConfigMap.get(XML_ATTRIBUTE_VALUE_360UNION_ENABLE), false);
                mUsePreEnvironment = parseBoolean(mConfigMap.get(XML_ATTRIBUTE_VALUE_USE_PRE_ENVIRONMENT_ENABLE), false);
            } catch (Exception e) {
                e.printStackTrace();
            }

            LogUtils.i(TAG, "Build:" + mBuildId);
            LogUtils.i(TAG, "AppVersion:" + mAppVersion);
            LogUtils.i(TAG, "VerificationEnable:" + mVerificationEnable);
            LogUtils.i(TAG, "VersionName:" + mVersionName);
            LogUtils.i(TAG, "UrlPrintEnable:" + mUrlPrintEnable);
            LogUtils.i(TAG, "TestMode:" + mTestMode);
            LogUtils.i(TAG, "AppCheckUpdateEnable:" + mAppCheckUpdateEnable);
            LogUtils.i(TAG, "LogEnable:" + mLogEnable);
            LogUtils.i(TAG, "AdSdkEnable:" + mAdSdkEnable);
            LogUtils.i(TAG, "UpdateCategory:" + mUpdateCategory);
            LogUtils.i(TAG, "360UnionEnable:" + m360UnionEnable);
            LogUtils.i(TAG, "UsePreEnvironment:" + mUsePreEnvironment);
        }

        private static void initChannel(Context context) {
            String channel = "0";
            String alChannel = "0";
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(ASSET_PATH_CHANNEL)));
                channel = decodeChannel(channel, bufferedReader.readLine());
                alChannel = decodeChannel(alChannel, bufferedReader.readLine());
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            mALChannel = alChannel;
            mChannel = channel;
        }

        private static String decodeChannel(String channel, String channelInfo) {
            if (channelInfo != null) {
                channelInfo = channelInfo.trim();
                int start = channelInfo.lastIndexOf('_');
                if (start > -1) {
                    channel = channelInfo.substring(start + 1);
                }
            }
            return channel;
        }

        /**
         * 设置渠道号
         *
         * @param channel channel
         */
        public static void setChannel(String channel) {
            mChannel = channel;
        }

        private static void initBuild(Context context) {
            String buildId = "0";
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(ASSET_PATH_BUILD)));
                buildId = bufferedReader.readLine();
                if (buildId != null) {
                    buildId = buildId.trim();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            mBuildId = buildId;
        }

        /**
         * 是否打开程序版本检查更新功能
         *
         * @return true则打开程序版本检查更新功能，否则关闭。
         */
        public static boolean isAppCheckUpdateEnable() {
            return mAppCheckUpdateEnable;
        }

        /**
         * 获取渠道信息
         *
         * @return 渠道信息
         */
        public static String getChannel() {
            return mChannel;
        }

        /**
         * 获取阿里渠道信息
         *
         * @return 渠道信息
         */
        public static String getALChannel() {
            return mALChannel;
        }

        /**
         * 获取构建版本号
         *
         * @return 构建版本号
         */
        public static String getBuildId() {
            return mBuildId;
        }

        /**
         * 获取程序版本信息
         *
         * @return 程序版本信息
         */
        public static String getAppVersion() {
            return mAppVersion;
        }

        /**
         * 获取版本类型名称，alpha,beta,release
         *
         * @return 版本类型名称
         */
        public static String getVersionName() {
            return mVersionName;
        }

        /**
         * 升级种类
         *
         * @return 升级类型
         */
        public static String getUpdateCategory() {
            return mUpdateCategory;
        }

        /**
         * 是否需要注册码验证
         *
         * @return 是否需要注册码验证
         */
        public static boolean isVerificationEnable() {
            return mVerificationEnable;
        }

        /**
         * 是否允许打印交互地址
         *
         * @return 是否允许打印交互地址
         */
        public static boolean isUrlPrintEnable() {
            return mUrlPrintEnable;
        }

        /**
         * 是否允许打Log
         *
         * @return 是否允许打Log
         */
        public static boolean isLogEnable() {
            return mLogEnable;
        }

        /**
         * 是否让程序在测试模式下运行
         *
         * @return 是否让程序在测试模式下运行
         */
        public static boolean isTestMode() {
            return mTestMode;
        }

        /**
         * 是否处于测试模式，由于gradle的BuildConfig.DEBUG无法正确获取，使用这个函数代替，当解决了这个问题之后则改回BuildConfig.DEBUG
         *
         * @return true/false
         */
        public static boolean isDebugMode() {
            return mTestMode;
        }

        /**
         * 是否允许sdk广告显示
         *
         * @return true: 显示，否则不显示
         */
        public static boolean isAdSdkEnable() {
            return mAdSdkEnable;
        }

        /**
         * @return 不显示下载管理页面广告的渠道列表
         */
        public static String getNoAdChannels() {
            return mNoAdChannels;
        }

        /**
         * @return 获取启动时不创建桌面图标的渠道列表
         */
        public static String getNoShortcutChannels() {
            return mNoShortcutChannels;
        }

        /**
         * 是否在推荐界面显示360换量联盟标识
         *
         * @return true：显示
         */
        public static boolean is360UnionEnable() {
            return m360UnionEnable;
        }

        /**
         * 释放使用预发布环境(cloud_api)
         *
         * @return true 使用预发布环境
         */
        public static boolean isUsePreEnvironment() {
            return mUsePreEnvironment;
        }

        private static int optInt(String string) {
            return parseInt(string, 0);
        }

        private static int parseInt(String string, int defaultValue) {
            try {
                string = string.trim();
                if (string.startsWith("#")) {
                    return (int) Long.parseLong(string.substring(1), RADIX_16);
                } else if (string.startsWith("0x") || string.startsWith("0X")) {
                    return (int) Long.parseLong(string.substring(2), RADIX_16);
                } else {
                    return (int) Long.parseLong(string);
                }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
            return defaultValue;
        }

        private static boolean parseBoolean(String string, boolean defaultValue) {
            if ("false".equalsIgnoreCase(string)) {
                return false;
            } else if ("true".equalsIgnoreCase(string)) {
                return true;
            }
            return defaultValue;
        }
    }


    /**
     * @author hu.cao
     * @version 7.0.0
     */
    public static class GeneralParameters {
        /**
         * uid key
         */
        public static final String KEY_DEVICE_ID = "uid";   //唯一
        /**
         * hid key
         */
        public static final String KEY_WIFI_MAC = "hid";
        /**
         * mid key
         */
        public static final String KEY_MACHINE_ID = "mid";
        /**
         * imsi key
         */
        public static final String KEY_IMSI = "imsi";

        /**
         * cpu的名字
         */
        public static final String KEY_CPU_NAME = "cpu";
        /**
         * cpu的型号
         */
        public static final String KEY_CPU_HARDWARE = "cpu_model";
        /**
         * platform key
         */
        public static final String KEY_PLATFORM_ID = "s";
        /**
         * splus key
         */
        public static final String KEY_ROM_VERSION = "splus";
        /**
         * rom key
         */
        public static final String KEY_ROM_FINGER_PRINTER = "rom";
        /**
         * app version key
         */
        public static final String KEY_APP_VERSION = "v";
        /**
         * channel key
         */
        public static final String KEY_CHANNEL_ID = "f";
        /**
         * al channel key
         */
        public static final String KEY_ALCHANNEL_ID = "alf";
        /**
         * net key
         */
        public static final String KEY_NETWORK_TYPE = "net";
        /**
         * st key
         */
        public static final String KEY_SERVICE_TYPE = "st";
        /**
         * active key
         */
        public static final String KEY_ACTIVE = "active";
        /**
         * tid key
         */
        public static final String KEY_USER_ID = "tid";
        /**
         * app key
         */
        public static final String KEY_APP = "app";

        /**
         * 屏幕分辨率
         */
        public static final String KEY_RESOLUTION = "resolution";

        private static final String ANDROID_PLATFORM = "200";
        private static final String ACTIVE_FLAG_FILE = "flag";

        private static HashMap<String, Object> mParameters = new HashMap<String, Object>();
        private static JSONObject mJsonParameter;

        /**
         * 初始化通用参数
         *
         * @param context 环境上下文
         */
        public static void init(Context context) {
            try {
                String wifiMac = EnvironmentUtils.Network.wifiMac().replaceAll("[-:]", "");
                mParameters.put(KEY_WIFI_MAC, SecurityUtils.TEA.encrypt(wifiMac));
                String imei = EnvironmentUtils.Network.imei();
                mParameters.put(KEY_DEVICE_ID, StringUtils.isEmpty(imei) ? wifiMac : imei);
                mParameters.put(KEY_MACHINE_ID, URLEncoder.encode(Build.MODEL, HTTP.UTF_8));
                mParameters.put(KEY_IMSI, URLEncoder.encode(EnvironmentUtils.Network.imsi(), HTTP.UTF_8));
                mParameters.put(KEY_PLATFORM_ID, KEY_PLATFORM_ID + ANDROID_PLATFORM);
                mParameters.put(KEY_ROM_VERSION, URLEncoder.encode(Build.VERSION.RELEASE + "/" + Build.VERSION.SDK_INT, HTTP.UTF_8));
                mParameters.put(KEY_ROM_FINGER_PRINTER, URLEncoder.encode(Build.FINGERPRINT, HTTP.UTF_8));
                mParameters.put(KEY_APP_VERSION, KEY_APP_VERSION + Config.getAppVersion());
                mParameters.put(KEY_CHANNEL_ID, KEY_CHANNEL_ID + Config.getChannel());
                mParameters.put(KEY_ALCHANNEL_ID, KEY_ALCHANNEL_ID + Config.getALChannel());
                mParameters.put(KEY_ACTIVE, isActive(context) ? 1 : 0);
                mParameters.put(KEY_NETWORK_TYPE, 0);
                mParameters.put(KEY_USER_ID, new Long(0));
                mParameters.put(KEY_RESOLUTION, getResolution(context));
                Map<String, String> param = getCpuParams();
                if (param != null) {
                    //现在这个参数没有用
//                    String cpuName = param.get("Processor");
//                    if (cpuName != null) {
//                        mParameters.put(KEY_CPU_NAME, cpuName);
//                    }
                    String cpuModel = param.get("Hardware");
                    if (cpuModel != null) {
                        mParameters.put(KEY_CPU_HARDWARE, cpuModel);
                    }
                }
                List<String> list = StringUtils.splitToStringList(context.getPackageName(), ".");
                int listSize = list.size();
                if (listSize > 0) {
                    mParameters.put(KEY_APP, list.get(--listSize));
                }
                mJsonParameter = new JSONObject(mParameters);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        // 获取CPU参数信息
        private static Map<String, String> getCpuParams() {
            Map<String, String> params = new HashMap<String, String>();
            try {
                Scanner scanner = new Scanner(new FileInputStream("/proc/cpuinfo"));
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    String[] array = line.split(":\\s+", 2);
                    if (array.length == 2) {
                        params.put(array[0].trim(), array[1]);
                    }
                }
                return params;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private static String getResolution(Context context) {
            final DisplayMetrics displayMetrics =  context.getResources().getDisplayMetrics();
            return displayMetrics.widthPixels + "x" + displayMetrics.heightPixels;
        }

        /**
         * 获取平台号
         *
         * @return 返回平台号
         */
        public static String getPlatformId() {
            return (String) mParameters.get(KEY_PLATFORM_ID);
        }

        /**
         * 更换平台号，HD版或者其他版本用，手机版天天动听不用更换
         * 在init函数之后调用
         *
         * @param platformId 平台号
         */
        public static void setPlatformId(String platformId) {
            mParameters.put(KEY_PLATFORM_ID, KEY_PLATFORM_ID + platformId);
            try {
                mJsonParameter.put(KEY_PLATFORM_ID, KEY_PLATFORM_ID + platformId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 获取版本号
         *
         * @return 返回版本号
         */
        public static String getAppVersion() {
            return (String) mParameters.get(KEY_APP_VERSION);
        }

        /**
         * 获取渠道号
         *
         * @return 返回渠道号
         */
        public static String getFromId() {
            return (String) mParameters.get(KEY_CHANNEL_ID);
        }

        /**
         * 获取通用参数
         *
         * @return 通用参数
         */
        public static HashMap<String, Object> parameters() {
            mParameters.put(KEY_NETWORK_TYPE, EnvironmentUtils.Network.type());
            return mParameters;
        }

        /**
         * 获取通用参数
         *
         * @return 以&符号相连的url参数形式的通用参数
         */
        public static String parameter() {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, Object> entry : parameters().entrySet()) {
                if (builder.length() > 0) {
                    builder.append('&');
                }
                builder.append(entry.getKey()).append('=').append(entry.getValue());
            }
            return builder.toString();
        }

        /**
         * 获取通用参数
         *
         * @return 通用参数
         */
        public static JSONObject jsonParameter() {
            try {
                mJsonParameter.put(KEY_NETWORK_TYPE, EnvironmentUtils.Network.type());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mJsonParameter;
        }

        /**
         * 设置用户id，用于上传，必须在init方法调用之后调用
         *
         * @param userId user id
         */
        public static void setUserId(long userId) {
            mParameters.put(KEY_USER_ID, userId);
            try {
                mJsonParameter.put(KEY_USER_ID, userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 获取用户ID
         *
         * @return userID
         */
        public static long getUserId() {
            return (Long) mParameters.get(KEY_USER_ID);
        }

        private static boolean isActive(Context context) {
            boolean active = false;
            try {
                openPrivateFile(context, ACTIVE_FLAG_FILE);
            } catch (Exception ignored) {
                createPrivateFile(context, ACTIVE_FLAG_FILE);
                active = true;
            }
            return active;
        }

        private static void createPrivateFile(Context context, String privateFile) {
            try {
                FileOutputStream fileOutputStream = context.openFileOutput(privateFile, Context.MODE_PRIVATE);
                closeStream(fileOutputStream);
            } catch (Exception eOutputStream) {
                // 这里使用Exception捕获异常是为了在因为在某些机型上不仅仅抛出FileNotFoundException
                // 比如联想K800 4.0.4可能出现未知原因的NullPointerException
                eOutputStream.printStackTrace();
            }
        }

        private static void openPrivateFile(Context context, String privateFile) throws FileNotFoundException {
            FileInputStream fileInputStream = context.openFileInput(privateFile);
            closeStream(fileInputStream);
        }

        private static void closeStream(Closeable stream) {
            if (stream == null) {
                return;
            }

            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
