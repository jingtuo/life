package com.jingtuo.android.libu.model.repo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateUtils;

import com.jingtuo.android.libu.Constants;
import com.jingtuo.android.libu.model.service.InitService;
import com.jingtuo.android.libu.model.service.LoginService;
import com.jingtuo.android.util.LogUtils;
import com.netease.cloud.nos.android.core.AcceleratorConf;
import com.netease.cloud.nos.android.core.CallRet;
import com.netease.cloud.nos.android.core.Callback;
import com.netease.cloud.nos.android.core.WanAccelerator;
import com.netease.cloud.nos.android.core.WanNOSObject;
import com.netease.cloud.nos.android.exception.InvalidChunkSizeException;
import com.netease.cloud.nos.android.exception.InvalidParameterException;
import com.netease.cloud.nos.android.utils.Util;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;

/**
 * Repository
 *
 * @author JingTuo
 */
public class Repository {

    private static final String TAG = Repository.class.getSimpleName();

    public static Repository getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private Repository() {
    }

    public static class SingletonHolder {
        private static final Repository INSTANCE = new Repository();

        private SingletonHolder() {
        }
    }


    public Observable<Class<? extends Activity>> init(Context context) {
        return Observable.fromCallable(new InitService(context));
    }


    public Observable<Boolean> login(Context context, String userId) {
        return Observable.fromCallable(new LoginService(context, userId));
    }

    /**
     * @return
     */
    public static AcceleratorConf createAcceleratorConfig() {
        /**
         * 对NOS上传加速Android-SDK进行配置，请在初始化时设置配置，初始化完成后修改配置是无效的
         */
        AcceleratorConf conf = new AcceleratorConf();

        try {
            /**
             * SDK会根据网络类型自动调整上传分块大小，如果网络类型无法识别，将采用设置的上传分块大小
             * 默认32K，如果网络环境较差，可以设置更小的分块
             * ChunkSize的取值范围为：[4K, 4M]，不在范围内将抛异常InvalidChunkSizeException
             */
            conf.setChunkSize(1024 * 32);
        } catch (InvalidChunkSizeException e) {
            LogUtils.e(TAG, e);
        }

        try {
            /**
             * 设置分块上传失败时的重试次数，默认2次
             * 如果设置的值小于或等于0，将抛异常InvalidParameterException
             */
            conf.setChunkRetryCount(2);
        } catch (InvalidParameterException e) {
            LogUtils.e(TAG, e);
        }

        try {
            /**
             * 设置文件上传socket连接超时，默认为10s
             * 如果设置的值小于或等于0，将抛异常InvalidParameterException
             */
            conf.setConnectionTimeout(10 * 1000);
        } catch (InvalidParameterException e) {
            LogUtils.e(TAG, e);
        }

        try {
            /**
             * 设置文件上传socket读写超时，默认30s
             * 如果设置的值小于或等于0，将抛异常InvalidParameterException
             */
            conf.setSoTimeout(30 * 1000);
        } catch (InvalidParameterException e) {
            LogUtils.e(TAG, e);
        }

        try {
            /**
             * 设置LBS查询socket连接超时，默认为10s
             * 如果设置的值小于或等于0，将抛异常InvalidParameterException
             */
            conf.setLbsConnectionTimeout(10 * 1000);
        } catch (InvalidParameterException e) {
            LogUtils.e(TAG, e);
        }

        try {
            /**
             * 设置LBS查询socket读写超时，默认10s
             * 如果设置的值小于或等于0，将抛异常InvalidParameterException
             */
            conf.setLbsSoTimeout(10 * 1000);
        } catch (InvalidParameterException e) {
            LogUtils.e(TAG, e);
        }

        /**
         * 设置刷新上传边缘节点的时间间隔，默认2小时
         * 合法值为大于或等于60s，设置非法将采用默认值
         * 注：当发生网络切换，Android-SDK会在下次上传文件时做一次接入点刷新
         */
        conf.setRefreshInterval(DateUtils.HOUR_IN_MILLIS * 2);

        /**
         * 设置统计监控程序统计发送间隔，默认120s
         * 合法值为大于或等于60s，设置非法将采用默认值
         */
        conf.setMonitorInterval(120 * 1000);

        /**
         * 设置httpClient，默认值为null
         * 非null：使用设置的httpClient进行文件上传和统计信息上传
         * null：使用sdk内部的机制进行文件上传和统计信息上传
         */
        conf.setHttpClient(null);

        /**
         * 设置是否用线程进行统计信息上传，默认值为false
         * true：创建线程进行统计信息上传
         * false：使用service进行统计信息上传
         */
        conf.setMonitorThread(false);
        return conf;
    }

    /**
     * eventName
     *
     * @param context
     * @param eventName
     * @param filePath
     */
    public void uploadData(Context context, String eventName, String filePath) {
        WanAccelerator.setConf(createAcceleratorConfig());
        File file = new File(filePath);
        String uploadContext = null;
        String bucketName = Constants.BUCKET_NAME;
        String objectName = getObjectName(context, eventName);
        long expires = System.currentTimeMillis() + 60 * 1000L;
        String uploadToken = null;
        try {
            uploadToken = Util.getToken(bucketName, objectName, expires, Constants.ACCESS_KEY, Constants.SECRET_KEY);
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
        WanNOSObject nosObject = new WanNOSObject();
        nosObject.setNosBucketName(bucketName);
        nosObject.setNosObjectName(objectName);
        nosObject.setContentType("text/plain");
        nosObject.setUploadToken(uploadToken);
        try {
            WanAccelerator.putFileByHttps(context.getApplicationContext(), file, null, uploadContext, nosObject, new Callback() {
                @Override
                public void onUploadContextCreate(Object o, String s, String s1) {

                }

                @Override
                public void onProcess(Object o, long l, long l1) {

                }

                @Override
                public void onSuccess(CallRet callRet) {

                }

                @Override
                public void onFailure(CallRet callRet) {

                }

                @Override
                public void onCanceled(CallRet callRet) {

                }
            });
        } catch (InvalidParameterException e) {
            LogUtils.e(TAG, e);
        }
    }

    public static String getObjectName(Context context, String eventName) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        String userId = preferences.getString(Constants.USER_ID, "");
        return userId + "/" + eventName + ".txt";
    }


    /**
     *
     * @param context
     * @param userId
     * @return
     */
    public static boolean createUserIdFolder(Context context, String userId) {
        File rootDir = context.getFilesDir();
        File userIdFolder = new File(rootDir, userId);
        if (userIdFolder.exists()) {
            return true;
        }
        boolean createSuccess = false;
        try {
            createSuccess = userIdFolder.createNewFile();
        } catch (IOException e) {
            LogUtils.e(TAG, e);
        }
        return createSuccess;
    }
}
