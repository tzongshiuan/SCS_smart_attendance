package com.gorilla.attendance.enterprise.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.database.DatabaseAdapter;
import com.gorilla.attendance.enterprise.database.bean.AcceptancesBean;
import com.gorilla.attendance.enterprise.database.bean.DeviceLoginBean;
import com.gorilla.attendance.enterprise.database.bean.RegisterBean;
import com.gorilla.attendance.enterprise.database.bean.UserClockBean;
import com.gorilla.attendance.enterprise.database.bean.VerifiedFaceBean;
import com.gorilla.attendance.enterprise.datamodel.AcceptancesModel;
import com.gorilla.attendance.enterprise.datamodel.EmployeeDataModel;
import com.gorilla.attendance.enterprise.datamodel.EmployeeModel;
import com.gorilla.attendance.enterprise.datamodel.GetVerifiedIdAndImageModel;
import com.gorilla.attendance.enterprise.datamodel.IdentitiesDataModel;
import com.gorilla.attendance.enterprise.datamodel.IdentitiesModel;
import com.gorilla.attendance.enterprise.datamodel.LoginModel;
import com.gorilla.attendance.enterprise.datamodel.RecordsReplyModel;
import com.gorilla.attendance.enterprise.datamodel.RegisterReplyModel;
import com.gorilla.attendance.enterprise.datamodel.RoleModel;
import com.gorilla.attendance.enterprise.datamodel.VisitorDataModel;
import com.gorilla.attendance.enterprise.datamodel.VisitorModel;
import com.gorilla.attendance.enterprise.util.apitask.listener.IAccessUnrecognizedLogFromDbListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IAccessUnrecognizedLogListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IAccessVisitorUnrecognizedLogListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IAttendanceUnrecognizedLogFromDbListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IAttendanceUnrecognizedLogListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceAccessRecordsFromDbListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceAccessRecordsListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceAttendanceRecordsFromDbListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceAttendanceRecordsListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceVisitorAccessRecordsListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceVisitorAttendanceRecordsFromDbListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceVisitorRecordsListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IRegisterEmployeeFromDbListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IRegisterVisitorFromDbListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IVisitorAttendanceUnrecognizedLogFromDbListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IVisitorsUnrecognizedLogListener;
import com.gorilla.enroll.lib.util.XAPKFile;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.conn.util.InetAddressUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import static com.gorilla.attendance.enterprise.util.DeviceUtils.mBluetoothLeService;

/**
 * Created by ggshao on 2017/2/7.
 */

public class EnterpriseUtils {
    private static final String TAG = "EnterpriseUtils";

    public static final String SD_CARD_APP = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AttendanceEnterprise";

    public static final String SD_CARD_APP_CONTENT = SD_CARD_APP + "/content";
    public static final String SD_CARD_APP_APK = SD_CARD_APP + "/Apk";
    public static final String SD_CARD_APP_LOG = SD_CARD_APP + "/log";
    public static final String SD_CARD_APP_FACE_IMAGE = SD_CARD_APP + "/FACEImage";

    public static final String SD_CARD_APP_SETTING_FILE = SD_CARD_APP + "/setting.xml";

    private static final int PHOTO_KEEP_DAYS = -2;
    private static final int BUFFER_SIZE = 4096;

    public static List<byte[]> mFacePngList = new ArrayList<byte[]>();
    public static final int VERIFIED_CANDIDATE_NUMBER = 1;

    public static Thread mUpdateLogThread = null;
    private static Process mLogProcess = null;

    public static <T> void executeAsyncTask(AsyncTask<T, ?, ?> asyncTask, T... params) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
            else
                asyncTask.execute(params);
        }catch (RejectedExecutionException localRejectedExecutionException){
            Log.w(TAG,"localRejectedExecutionException");
        }
    }


    private static Context mContext = null;

    public static String mTempClockOutRecord = null;

    public static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp90ojTJOc6o/QDkhixywIjy7e032JpzSY+5TdZ1Cp+QHlzFs8t5xboCvJQClpr/kRy++p80gYfCyC2A3deQwS5hO2i4NvR1M2UShCCr8tfTtFcSDhcvXYJrOWvO0hB/y2Oe8oz3W5E5V1HRuo1DfJxfA/9sBIhOdR9iF/lr4jiwyNxJsUa+MfO5411UPn5WWJhdtXMv4qaBA4iwLyj1dZXiiHjG2+nFHgBABWIcLvYvLrj1kCG7+c02dXkayD/t9FB1O8gy5M57kRqvneExgMBn6wuAomEi5u/T47DEEog1jyGCglLcLZrh4wbFtRSthStZXOYPpbPjWrFB6TFwpHQIDAQAB";
    public static final XAPKFile[] xAPKS = new XAPKFile[]{new XAPKFile(true, 39, 189049695L)};//obb file size , 1.5.2
//    public static final XAPKFile[] xAPKS = new XAPKFile[]{new XAPKFile(true, 38, 189032247L)};//obb file size , 1.5.1
//    public static final XAPKFile[] xAPKS = new XAPKFile[]{new XAPKFile(true, 38, 189032247L)};//obb file size , 1.5.0
//    public static final XAPKFile[] xAPKS = new XAPKFile[]{new XAPKFile(true, 32, 194897761L)};//obb file size , 1.4.18, 20
//    public static final XAPKFile[] xAPKS = new XAPKFile[]{new XAPKFile(true, 25, 194900855L)};//obb file size , 1.4.16
//    public static final XAPKFile[] xAPKS = new XAPKFile[]{new XAPKFile(true, 25, 213390315L)};//obb file size , 1.4.14
//    public static final XAPKFile[] xAPKS = new XAPKFile[]{new XAPKFile(true, 25, 189032247L )};//obb file size, 1.4.15
//    public static final XAPKFile[] xAPKS = new XAPKFile[]{new XAPKFile(true, 25, 213329201)};//obb file size

    public static String getDeviceIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : networkInterfaces) {
                List<InetAddress> inetAddresses = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress inetAddress : inetAddresses) {
                    if (!inetAddress.isLoopbackAddress()) {
                        String sAddr = inetAddress.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                // drop ip6 port suffix
                                int delim = sAddr.indexOf('%');
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static DisplayImageOptions getCustomDisplayImageOptions(int resId)
    {
        DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(resId) // resource or drawable
                .showImageForEmptyUri(resId) // resource or drawable
                .showImageOnFail(resId) // resource or drawable
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(500))
                .build();

        return displayOptions;
    }

    public static boolean checkCameraPermission(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity,activity.getResources().getString(R.string.toast_camera_permission),Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 1);
                return false;
            }
        }
        return true;
    }



    public static void deleteFacePhoto(){
//        File dir = new File(AdDownloadManager.DOWNLOAD_FOLDER_PATH);
        File dir = new File(EnterpriseUtils.SD_CARD_APP_FACE_IMAGE);
        if(dir.isDirectory()){
            String[] children = dir.list();
            LOG.D(TAG,"children.length = " + children.length);
            for (int i = 0; i < children.length; i++)
            {
                File photo = new File(dir, children[i]);
                Calendar time = Calendar.getInstance();
                time.add(Calendar.DAY_OF_YEAR, PHOTO_KEEP_DAYS);
                //I store the required attributes here and delete them
                Date lastModified = new Date(photo.lastModified());
                if(lastModified.before(time.getTime()))
                {
                    //file is older than a week
                    photo.delete();
                }
            }
        }
    }


    public static void deleteLogFile(){
//        File dir = new File(AdDownloadManager.DOWNLOAD_FOLDER_PATH);
        File dir = new File(EnterpriseUtils.SD_CARD_APP_LOG);
        if(dir.isDirectory()){
            String[] children = dir.list();
            LOG.D(TAG,"children.length = " + children.length);
            for (int i = 0; i < children.length; i++)
            {
                File logFile = new File(dir, children[i]);
                Calendar time = Calendar.getInstance();
                time.add(Calendar.DAY_OF_YEAR, PHOTO_KEEP_DAYS);
                //I store the required attributes here and delete them
                Date lastModified = new Date(logFile.lastModified());
                if(lastModified.before(time.getTime()))
                {
                    //file is older than a week
                    logFile.delete();
                }
            }
        }
    }

    public static void uploadLogFile(String ftpUrl, String username, String password, String fileName, File uploadFile) {
        LOG.D(TAG,"uploadFtpFile ftpUrl = " + ftpUrl);
        LOG.D(TAG,"uploadFtpFile username = " + username);
        LOG.D(TAG,"uploadFtpFile password = " + password);
        LOG.D(TAG,"uploadFtpFile fileName = " + fileName);
        LOG.D(TAG,"uploadFtpFile uploadFile = " + uploadFile);

        if(uploadFile.exists()){
            FTPClient ftpClient = new FTPClient();
            FileInputStream fis = null;
            try {
                ftpClient.connect(ftpUrl);
                ftpClient.login(username, password);
                fis = new FileInputStream(uploadFile);
                //設置上傳目錄
                ftpClient.changeWorkingDirectory("/clockServer/Log");
                ftpClient.setBufferSize(1024);
//            ftpClient.setControlEncoding("GBK");
                //設置檔案類型（二進位）
//            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.storeFile(fileName, fis);
            } catch (IOException e) {
                LOG.D(TAG,"FTP FAIL");
                e.printStackTrace();

            } catch (Exception e){
                LOG.D(TAG,"FTP Exception e = " + e);
                e.printStackTrace();
            }
            finally {
                IOUtils.closeQuietly(fis);
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                    LOG.D(TAG,"FTP 連接發生異常");
//                    throw new RuntimeException("關閉FTP連接發生異常！", e);
                }
            }
        }else{
        }

    }

    public static void startRecordLog(){
        if ( isExternalStorageWritable() ) {

            File appDirectory = new File( EnterpriseUtils.SD_CARD_APP );
            File logDirectory = new File( EnterpriseUtils.SD_CARD_APP_LOG );
//                File logFile = new File( logDirectory, "logcat" + System.currentTimeMillis() + ".txt" );

            Time t = new Time();
            t.setToNow();
            int year = t.year;
            int month = t.month + 1;
            int date = t.monthDay;
            File logFile = new File(logDirectory + String.format("/%s-%d-%02d-%02d.txt", DeviceUtils.mDeviceName, year, month, date));
//            File logFile = new File(logDirectory + String.format("/%s-%d-%02d-%02d.txt", "Cold", year, month, date));
//            TSAAttendanceUtils.mFos = new OutputStreamWriter(file);

            // create app folder
            if ( !appDirectory.exists() ) {
                appDirectory.mkdir();
            }

            // create log folder
            if ( !logDirectory.exists() ) {
                logDirectory.mkdir();
            }

            // clear the previous logcat and then write the new one to the file
            try {

                if(mLogProcess != null){
                    mLogProcess.destroy();
                }
                mLogProcess = Runtime.getRuntime().exec( "logcat -c");
                mLogProcess = Runtime.getRuntime().exec( "logcat -d");
                mLogProcess = Runtime.getRuntime().exec( "logcat -f " + logFile + " -v time" );

            } catch ( IOException e ) {
                e.printStackTrace();
            }

        } else if ( isExternalStorageReadable() ) {
            // only readable
        } else {
            // not accessible
        }
    }


    public static void downloadFtpByURLConnection(String ftpFilePath, String username, String password, String fileName, int videoIndex){
        //ftp://192.168.11.208/Video/0cbd7bc48c1c-4dde8f98-87cd-3298-56d8.mp4
        //1. parse filePath
        LOG.D(TAG,"downloadFtpByURLConnection ftpFilePath = " + ftpFilePath);
        LOG.D(TAG,"downloadFtpByURLConnection username = " + username);
        LOG.D(TAG,"downloadFtpByURLConnection password = " + password);
        LOG.D(TAG,"downloadFtpByURLConnection fileName = " + fileName);

        LOG.D(TAG,"downloadFtpByURLConnection DeviceUtils.mCheckIsDownloadingVideo[videoIndex] = " + DeviceUtils.mCheckIsDownloadingVideo[videoIndex]);


        //EG : ftp://gorilla:gorillakm@192.168.11.208//clockServer/Video/testvideo.mp4
        String downloadFtpUrl = "ftp://%s:%s@%s/%s";

        downloadFtpUrl = String.format(downloadFtpUrl, username, password, ApiAccessor.FTP_IP, ftpFilePath);

        LOG.D(TAG,"downloadFtpUrl = " + downloadFtpUrl);

        try {
            URL url = new URL(downloadFtpUrl);
            URLConnection conn = url.openConnection();
            InputStream inputStream = conn.getInputStream();

            FileOutputStream outputStream = new FileOutputStream(SD_CARD_APP_CONTENT + "/" + fileName);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            LOG.D(TAG,"downloading FTP Video");
            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                LOG.D(TAG,"downloading");
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            LOG.D(TAG,"FILE DOWNLOADED videoIndex = " + videoIndex);
            //handler
            DeviceUtils.mCheckIsDownloadingVideo[videoIndex] = false;

        } catch (IOException ex) {
            ex.printStackTrace();
            DeviceUtils.mCheckIsDownloadingVideo[videoIndex] = false;
        }

    }

    //record log
    public static Runnable mUpdateLogThreadRun = new Runnable() {
        @Override
        public void run() {

            //delete image(save 3 days)
//            TSAAttendanceUtils.deleteLogFile();
//            TSAAttendanceUtils.deleteFacePhoto();
//
//            Calendar c = Calendar.getInstance();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            String yesterdayDate = sdf.format(c.getTimeInMillis() - Constants.ONE_DAY_MILLI_SECONDS);// get yesterday date
//            LOG.D(TAG,"yesterdayDate = " + yesterdayDate);
//
//            final String yesterdayLogFileName = String.format("%s-%s.txt", TSAAttendanceUtils.mClientName, yesterdayDate);
//            final File yesterdayFile = new File(TSAAttendanceUtils.SD_CARD_APP_LOG + "/" + yesterdayLogFileName);
//
//            if(yesterdayFile.exists()){
//                TSAAttendanceUtils.uploadFtpFile(ApiAccessor.FTP_IP, TSAAttendanceUtils.mFtpAccount, TSAAttendanceUtils.mFtpPassword, yesterdayLogFileName, yesterdayFile);
//            }else{
//
//            }


            if ( isExternalStorageWritable() ) {

                File appDirectory = new File( EnterpriseUtils.SD_CARD_APP );
                File logDirectory = new File( EnterpriseUtils.SD_CARD_APP_LOG );
//                File logFile = new File( logDirectory, "logcat" + System.currentTimeMillis() + ".txt" );

                Time t = new Time();
                t.setToNow();
                int year = t.year;
                int month = t.month + 1;
                int date = t.monthDay;
                File logFile = new File(logDirectory + String.format("/%s-%d-%02d-%02d.txt", DeviceUtils.mDeviceName, year, month, date));
//            TSAAttendanceUtils.mFos = new OutputStreamWriter(file);

                // create app folder
                if ( !appDirectory.exists() ) {
                    appDirectory.mkdir();
                }

                // create log folder
                if ( !logDirectory.exists() ) {
                    logDirectory.mkdir();
                }

                // clear the previous logcat and then write the new one to the file
                try {
                    Process process = Runtime.getRuntime().exec( "logcat -d");
                    process = Runtime.getRuntime().exec( "logcat -f " + logFile + " -v time -s MainActivity EnterpriseUtils ApiAccessor PinCodeFragment FaceIconFragment FaceIdentificationFragment" );
                } catch ( IOException e ) {
                    e.printStackTrace();
                }

            } else if ( isExternalStorageReadable() ) {
                // only readable
            } else {
                // not accessible
            }


            if(mUpdateLogThread != null){
                mUpdateLogThread.interrupt();
                mUpdateLogThread = null;
            }

        }
    };


    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals( state ) ) {
            return true;
        }
        return false;
    }

    public static void addUserClockToDb(Context context, String faceVerify){
        LOG.D(TAG,"addUserClockToDb faceVerify = " + faceVerify + " ClockUtils.mModule = " + ClockUtils.mModule + " ClockUtils.mMode = " + ClockUtils.mMode);
        LOG.D(TAG,"addUserClockToDb faceVerify = " + faceVerify + " ClockUtils.mModule = " + ClockUtils.mModule + " ClockUtils.mType = " + ClockUtils.mType);
        LOG.D(TAG,"addUserClockToDb faceVerify = " + faceVerify + " ClockUtils.mRfid = " + ClockUtils.mRfid + " ClockUtils.mRecordMode = " + ClockUtils.mRecordMode);
        LOG.D(TAG,"addUserClockToDb faceVerify = " + faceVerify + " ClockUtils.mRfid = " + ClockUtils.mRfid + " ClockUtils.mSerialNumber = " + ClockUtils.mSerialNumber);
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance(context);
//        databaseAdapter.open();
        UserClockBean bean = new UserClockBean();

        bean.setClientName(DeviceUtils.mDeviceName);
        bean.setSerial(ClockUtils.mSerialNumber);
        bean.setId(ClockUtils.mLoginUuid);
        bean.setSecurityCode(ClockUtils.mLoginAccount);
        bean.setType(ClockUtils.mType);


        if(EnterpriseUtils.mFacePngList != null && EnterpriseUtils.mFacePngList.size() > 0){
            bean.setPngInfo1(EnterpriseUtils.mFacePngList.get(0));
        }else{
            bean.setPngInfo1(new byte[0]);
        }

//        bean.setPngInfo1(EnterpriseUtils.mFacePngList.get(0));
        bean.setPngInfo2(null);
//            bean.setPngInfo3(ShangHaiAttendanceUtils.mPngList.get(2));
        bean.setPngInfo3(null);
        bean.setPngInfo4(null);
        bean.setPngInfo5(null);
        bean.setFaceVerify(faceVerify);

        bean.setClientTime(ClockUtils.mLoginTime);
        bean.setClockType(ClockUtils.mType);
        bean.setLiveness(ClockUtils.mLiveness);
        bean.setMode(ClockUtils.mMode);
        bean.setModule(ClockUtils.mModule);

        bean.setRfid(ClockUtils.mRfid);
        bean.setRecordMode(ClockUtils.mRecordMode);

        databaseAdapter.createUserClock(bean);
//        databaseAdapter.close();



    }


    public static void addUserClockToDb(Context context, String faceVerify, int module){
        LOG.D(TAG,"addUserClockToDb22222 faceVerify = " + faceVerify + " ClockUtils.mModule = " + ClockUtils.mModule + " ClockUtils.mMode = " + ClockUtils.mMode);

        DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance(context);
//        databaseAdapter.open();
        UserClockBean bean = new UserClockBean();


        bean.setClientName(DeviceUtils.mDeviceName);
        bean.setSerial(ClockUtils.mSerialNumber);
        bean.setId(ClockUtils.mLoginUuid);
        bean.setSecurityCode(ClockUtils.mLoginAccount);

        LOG.D(TAG,"addUserClockToDb22222 ClockUtils.mModule = " + ClockUtils.mModule + " mTempClockOutRecord = " +mTempClockOutRecord +
                " module = " + module);

        LOG.D(TAG,"addUserClockToDb22222 ClockUtils.mType = " + ClockUtils.mType);
        LOG.D(TAG,"addUserClockToDb22222 ClockUtils.mRecordMode = " + ClockUtils.mRecordMode);
        //for module 4 and clockOut
        if(ClockUtils.mModule == Constants.MODULES_ATTENDANCE_ACCESS && mTempClockOutRecord != null && module == Constants.MODULES_ATTENDANCE){
            bean.setType(mTempClockOutRecord);
            bean.setClockType(mTempClockOutRecord);
            mTempClockOutRecord = null;
        }else{
            bean.setType(ClockUtils.mType);
            bean.setClockType(ClockUtils.mType);
        }
//        bean.setType(ClockUtils.mType);


        if(EnterpriseUtils.mFacePngList != null && EnterpriseUtils.mFacePngList.size() > 0){
            bean.setPngInfo1(EnterpriseUtils.mFacePngList.get(0));
        }else{
            bean.setPngInfo1(new byte[0]);
        }

//        bean.setPngInfo1(EnterpriseUtils.mFacePngList.get(0));
        bean.setPngInfo2(null);
//            bean.setPngInfo3(ShangHaiAttendanceUtils.mPngList.get(2));
        bean.setPngInfo3(null);
        bean.setPngInfo4(null);
        bean.setPngInfo5(null);
        bean.setFaceVerify(faceVerify);

        bean.setClientTime(ClockUtils.mLoginTime);


        //for module 4 and clockOut
//        if(ClockUtils.mModule == Constants.MODULES_ATTENDANCE_ACCESS && mTempClockOutRecord != null && module == Constants.MODULES_ATTENDANCE){
//            bean.setClockType(mTempClockOutRecord);
//            mTempClockOutRecord = null;
//        }else{
//            bean.setClockType(ClockUtils.mType);
//        }


        bean.setLiveness(ClockUtils.mLiveness);
        bean.setMode(ClockUtils.mMode);
        bean.setModule(module);

        bean.setRfid(ClockUtils.mRfid);
        bean.setRecordMode(ClockUtils.mRecordMode);

        bean.setIsEmployeeOpenDoor(DeviceUtils.mIsEmployeeOpenDoor);
        bean.setIsVisitorOpenDoor(DeviceUtils.mIsVisitorOpenDoor);

        databaseAdapter.createUserClock(bean);
//        databaseAdapter.close();


    }

    public static void uploadUserClockDb(Context context){
        LOG.D(TAG,"uploadUserClockDb");
        mContext = context;

        //Old Function
//        ApiUtils.deviceAttendanceRecords(TAG, context, DeviceUtils.mDeviceName, true, null, -1, deviceAttendanceRecordsListener);
//        ApiUtils.deviceVisitorRecords(TAG, context, DeviceUtils.mDeviceName, true, deviceVisitorRecordsListener);
//        ApiUtils.deviceAccessRecords(TAG, context, DeviceUtils.mDeviceName, true, null, -1, accessRecordsListener);
//
//        ApiUtils.attendanceUnrecognizedLog(TAG, context, DeviceUtils.mDeviceName, true, attendanceUnrecognizedLogListener);
//        ApiUtils.accessUnrecognizedLog(TAG, context, DeviceUtils.mDeviceName, true, accessUnrecognizedLogListener);
//        ApiUtils.visitorsUnrecognizedLog(TAG, context, DeviceUtils.mDeviceName, true, visitorsUnrecognizedLogListener);
//
//        ApiUtils.deviceVisitorAccessRecords(TAG, context, DeviceUtils.mDeviceName, true, null, -1, deviceVisitorAccessRecordsListener);
//        ApiUtils.accessVisitorUnrecognizedLog(TAG, context, DeviceUtils.mDeviceName, true, accessVisitorUnrecognizedLogListener);




        ApiUtils.deviceAttendanceRecordsFromDb(TAG, context,DeviceUtils.mDeviceName,  deviceAttendanceRecordsFromDbListener);
        ApiUtils.deviceAccessRecordsFromDb(TAG, context, DeviceUtils.mDeviceName, deviceAccessRecordsFromDbListener);

        //send 2 api, visitorAttendance and visitorAccess
        ApiUtils.deviceVisitorAttendanceRecordsFromDb(TAG, context, DeviceUtils.mDeviceName, deviceVisitorAttendanceRecordsFromDbListener);

        ApiUtils.attendanceUnrecognizedLogFromDb(TAG, context, DeviceUtils.mDeviceName, attendanceUnrecognizedLogFromDbListener);
        ApiUtils.accessUnrecognizedLogFromDb(TAG, context, DeviceUtils.mDeviceName, accessUnrecognizedLogFromDbListener);

        //send 2 api, visitorUnrecognizedAttendance and visitorUnrecognizedAccess
        ApiUtils.visitorAttendanceUnrecognizedLogFromDb(TAG, context, DeviceUtils.mDeviceName, visitorsAttendanceUnrecognizedLogFromDbListener);

    }


    private static IDeviceAttendanceRecordsFromDbListener deviceAttendanceRecordsFromDbListener = new IDeviceAttendanceRecordsFromDbListener() {
        @Override
        public void onDeviceAttendanceRecordsFromDb(RecordsReplyModel model) {
            LOG.D(TAG,"onDeviceAttendanceRecordsFromDb model = " + model);
            if(model != null){
                if(model.getStatus().equals(Constants.STATUS_SUCCESS)){
                    //delete DB Data
//                    DatabaseAdapter.getInstance(mContext).deleteUserClockByModuleIdRecordMode(Constants.MODULES_ATTENDANCE, Constants.RECORD_MODE_RECORD);
                }
            }else{

            }
        }
    };

    private static IDeviceAttendanceRecordsListener deviceAttendanceRecordsListener = new IDeviceAttendanceRecordsListener() {
        @Override
        public void onDeviceAttendanceRecords(RecordsReplyModel model) {
            LOG.D(TAG,"onDeviceAttendanceRecords model = " + model);
            if(model != null){
                if(model.getStatus().equals(Constants.STATUS_SUCCESS)){
                    //delete DB Data
                    DatabaseAdapter.getInstance(mContext).deleteUserClockByModuleIdRecordMode(Constants.MODULES_ATTENDANCE, Constants.RECORD_MODE_RECORD);
                }
            }else{

            }
        }
    };

    private static IDeviceAccessRecordsFromDbListener deviceAccessRecordsFromDbListener = new IDeviceAccessRecordsFromDbListener() {
        @Override
        public void onDeviceAccessRecordsFromDb(RecordsReplyModel model) {
            LOG.D(TAG,"onDeviceAccesseRecordsFromDb model = " + model);
            if(model != null){
                if(model.getStatus().equals(Constants.STATUS_SUCCESS)){
                    //delete DB Data
//                    DatabaseAdapter.getInstance(mContext).deleteUserClockByModuleIdRecordMode(Constants.MODULES_ATTENDANCE, Constants.RECORD_MODE_RECORD);
                }
            }else{

            }
        }
    };


    private static IDeviceVisitorRecordsListener deviceVisitorRecordsListener = new IDeviceVisitorRecordsListener() {
        @Override
        public void onDeviceVisitorRecords(RecordsReplyModel model) {
            LOG.D(TAG,"onDeviceVisitorRecords model = " + model);
            if(model != null){
                if(model.getStatus().equals(Constants.STATUS_SUCCESS)){
                    //delete DB Data
//                    DatabaseAdapter.getInstance(mContext).open();
                    DatabaseAdapter.getInstance(mContext).deleteUserClockByModuleIdRecordModeVisitorOpenDoor(Constants.MODULES_VISITORS, Constants.RECORD_MODE_RECORD, false);
//                    DatabaseAdapter.getInstance(mContext).close();
                }
            }else{

            }
        }
    };


    private static IDeviceVisitorAttendanceRecordsFromDbListener deviceVisitorAttendanceRecordsFromDbListener = new IDeviceVisitorAttendanceRecordsFromDbListener() {
        @Override
        public void onDeviceVisitorAttendanceRecordsFromDb(RecordsReplyModel model) {
            LOG.D(TAG,"onDeviceVisitorAttendanceRecordsFromDb model = " + model);
            if(model != null){
                if(model.getStatus().equals(Constants.STATUS_SUCCESS)){
                    //delete DB Data
//                    DatabaseAdapter.getInstance(mContext).open();
//                    DatabaseAdapter.getInstance(mContext).deleteUserClockByModuleIdRecordModeVisitorOpenDoor(Constants.MODULES_VISITORS, Constants.RECORD_MODE_RECORD, false);
//                    DatabaseAdapter.getInstance(mContext).close();
                }
            }else{

            }
        }

    };

    private static IDeviceAccessRecordsListener accessRecordsListener = new IDeviceAccessRecordsListener() {
        @Override
        public void onDeviceAccessRecords(RecordsReplyModel model) {
            LOG.D(TAG,"onDeviceAccessRecords model = " + model);
            if(model != null){
                if(model.getStatus().equals(Constants.STATUS_SUCCESS)){
                    //delete DB Data
//                    DatabaseAdapter.getInstance(mContext).open();
                    DatabaseAdapter.getInstance(mContext).deleteUserClockByModuleIdRecordMode(Constants.MODULES_ACCESS, Constants.RECORD_MODE_RECORD);
//                    DatabaseAdapter.getInstance(mContext).deleteUserClockByModuleIdRecordMode(Constants.MODULES_ATTENDANCE_ACCESS, Constants.RECORD_MODE_RECORD);
//                    DatabaseAdapter.getInstance(mContext).close();
                }
            }else{

            }
        }
    };


    private static IDeviceVisitorAccessRecordsListener deviceVisitorAccessRecordsListener = new IDeviceVisitorAccessRecordsListener() {
        @Override
        public void onDeviceVisitorAccessRecords(RecordsReplyModel model) {
            LOG.D(TAG,"onDeviceVisitorAccessRecords model = " + model);
            if(model != null){
                if(model.getStatus().equals(Constants.STATUS_SUCCESS)){
                    //delete DB Data
//                    DatabaseAdapter.getInstance(mContext).open();
                    DatabaseAdapter.getInstance(mContext).deleteUserClockByModuleIdRecordModeVisitorOpenDoor(Constants.MODULES_VISITORS, Constants.RECORD_MODE_RECORD, true);
//                    DatabaseAdapter.getInstance(mContext).deleteUserClockByModuleIdRecordMode(Constants.MODULES_ATTENDANCE_ACCESS, Constants.RECORD_MODE_RECORD);
//                    DatabaseAdapter.getInstance(mContext).close();
                }
            }else{

            }
        }
    };


    private static IAttendanceUnrecognizedLogListener attendanceUnrecognizedLogListener = new IAttendanceUnrecognizedLogListener() {
        @Override
        public void onAttendanceUnrecognizedLog(RecordsReplyModel model) {
            LOG.D(TAG,"onAttendanceUnrecognizedLog model = " + model);
            if(model != null){
                if(model.getStatus().equals(Constants.STATUS_SUCCESS)){
                    //delete DB Data
                    //delete DB Data
//                    DatabaseAdapter.getInstance(mContext).open();
//                    DatabaseAdapter.getInstance(mContext).deleteUserClockByModule(Constants.MODULES_VISITORS);
                    DatabaseAdapter.getInstance(mContext).deleteUserClockByModuleIdRecordMode(Constants.MODULES_ATTENDANCE, Constants.RECORD_MODE_UNRECOGNIZED);
//                    DatabaseAdapter.getInstance(mContext).deleteUserClockByModuleIdRecordMode(Constants.MODULES_ATTENDANCE_ACCESS, Constants.RECORD_MODE_UNRECOGNIZED);

//                    DatabaseAdapter.getInstance(mContext).close();
                }
            }else{

            }
        }
    };

    private static IAttendanceUnrecognizedLogFromDbListener attendanceUnrecognizedLogFromDbListener = new IAttendanceUnrecognizedLogFromDbListener() {
        @Override
        public void onAttendanceUnrecognizedLogFromDb(RecordsReplyModel model) {
            LOG.D(TAG,"attendanceUnrecognizedLogFromDbListener model = " + model);
        }
    };

    private static IVisitorsUnrecognizedLogListener visitorsUnrecognizedLogListener = new IVisitorsUnrecognizedLogListener() {
        @Override
        public void onVisitorsUnrecognizedLog(RecordsReplyModel model) {
            LOG.D(TAG,"onAttendanceUnrecognizedLog model = " + model);
            if(model != null){
                if(model.getStatus().equals(Constants.STATUS_SUCCESS)){
                    //delete DB Data
//                    DatabaseAdapter.getInstance(mContext).open();
//                    DatabaseAdapter.getInstance(mContext).deleteUserClockByModule(Constants.MODULES_VISITORS);
                    DatabaseAdapter.getInstance(mContext).deleteUserClockByModuleIdRecordModeVisitorOpenDoor(Constants.MODULES_VISITORS, Constants.RECORD_MODE_UNRECOGNIZED, false);

//                    DatabaseAdapter.getInstance(mContext).close();
                }
            }else{

            }
        }
    };

    private static IVisitorAttendanceUnrecognizedLogFromDbListener visitorsAttendanceUnrecognizedLogFromDbListener = new IVisitorAttendanceUnrecognizedLogFromDbListener() {
        @Override
        public void onVisitorAttendanceUnrecognizedLogFromDb(RecordsReplyModel model) {
            LOG.D(TAG,"onVisitorAttendanceUnrecognizedLogFromDb model = " + model);
        }
    };


    private static IAccessUnrecognizedLogListener accessUnrecognizedLogListener = new IAccessUnrecognizedLogListener() {
        @Override
        public void onAccessUnrecognizedLog(RecordsReplyModel model) {
            LOG.D(TAG,"onAccessUnrecognizedLog model = " + model);
            if(model != null){
                if(model.getStatus().equals(Constants.STATUS_SUCCESS)){
                    //delete DB Data
//                    DatabaseAdapter.getInstance(mContext).open();
//                    DatabaseAdapter.getInstance(mContext).deleteUserClockByModule(Constants.MODULES_VISITORS);
                    DatabaseAdapter.getInstance(mContext).deleteUserClockByModuleIdRecordMode(Constants.MODULES_ACCESS, Constants.RECORD_MODE_UNRECOGNIZED);
//                    DatabaseAdapter.getInstance(mContext).deleteUserClockByModuleIdRecordMode(Constants.MODULES_ATTENDANCE_ACCESS, Constants.RECORD_MODE_UNRECOGNIZED);

//                    DatabaseAdapter.getInstance(mContext).close();
                }
            }else{

            }
        }
    };

    private static IAccessUnrecognizedLogFromDbListener accessUnrecognizedLogFromDbListener = new IAccessUnrecognizedLogFromDbListener() {
        @Override
        public void onAccessUnrecognizedLogFromDb(RecordsReplyModel model) {
            LOG.D(TAG,"onAccessUnrecognizedLogFromDb model = " + model);
        }

    };

    private static IAccessVisitorUnrecognizedLogListener accessVisitorUnrecognizedLogListener = new IAccessVisitorUnrecognizedLogListener() {
        @Override
        public void onAccessVisitorUnrecognizedLog(RecordsReplyModel model) {
            LOG.D(TAG,"onAccessUnrecognizedLog model = " + model);
            if(model != null){
                if(model.getStatus().equals(Constants.STATUS_SUCCESS)){
                    //delete DB Data
                    DatabaseAdapter.getInstance(mContext).deleteUserClockByModuleIdRecordModeVisitorOpenDoor(Constants.MODULES_VISITORS, Constants.RECORD_MODE_UNRECOGNIZED, true);

                }
            }else{

            }
        }
    };

    /*******************************************BAP VERIFIED USE START******************************************************/


    public static void checkEmployeeIdentitiesDb(ArrayList<IdentitiesModel> identities, Context context){
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance(context);
//        databaseAdapter.open();

        //1. get all data from DB
        ArrayList<VerifiedFaceBean> verifiedFaceBean = databaseAdapter.getAllEmployeeIdentitiesArrayList();

        LOG.D(TAG,"checkVerifiedFaceDb identities.size() = " + identities.size());

        if(verifiedFaceBean == null || verifiedFaceBean.size() == 0){

            for(int i = 0 ; i < identities.size() ; i++){
                VerifiedFaceBean verifiedFaceBeanData = new VerifiedFaceBean();

                verifiedFaceBeanData.setBapModelId(identities.get(i).getBapModelId());
                verifiedFaceBeanData.setId(identities.get(i).getId());
                verifiedFaceBeanData.setEmployeeId(identities.get(i).getEmployeeId());

//                verifiedFaceBeanData.setEmployeeName(identities.get(i).getEmployeeName());
//                verifiedFaceBeanData.setVisitorName(identities.get(i).getVisitorName());

                verifiedFaceBeanData.setEmployeeName(identities.get(i).getFirstName() + " " + identities.get(i).getLastName());
                verifiedFaceBeanData.setVisitorName(identities.get(i).getFirstName() + " " + identities.get(i).getLastName());

                verifiedFaceBeanData.setCreatedTime(identities.get(i).getCreatedTime());

                byte[] imageData = Base64.decode(identities.get(i).getModel(), Base64.DEFAULT);
                verifiedFaceBeanData.setModel(imageData);

                databaseAdapter.createEmployeeIdentitiesFace(verifiedFaceBeanData);

            }

        }else{
            //check add or delete or update
            //update
            //1. prepare two copy data

            //model
            //verifiedFaceBean

            LOG.D(TAG,"checkVerifiedFaceDb verifiedFaceBean.size() = " + verifiedFaceBean.size());

            LOG.D(TAG,"checkVerifiedFaceDb verifiedFaceBean.size() = " + verifiedFaceBean.size());
            GetVerifiedIdAndImageModel getVerifiedIdAndImageModel = new GetVerifiedIdAndImageModel();
            IdentitiesDataModel identitiesDataModel = new IdentitiesDataModel();
            getVerifiedIdAndImageModel.setIdentitiesData(identitiesDataModel);


            for(int i = 0 ; i < identities.size() ; i++){
                getVerifiedIdAndImageModel.getIdentitiesData().addEmployeesIdentifyData(identities.get(i));
            }
            ArrayList<VerifiedFaceBean> verifiedFaceBeanArrayList = databaseAdapter.getAllEmployeeIdentitiesArrayList();

            LOG.D(TAG,"checkVerifiedFaceDb do compare");

            //2. compare each other and update
            for(int i = 0 ; i < identities.size() ; i++){
                String modelModelId = identities.get(i).getBapModelId();

                LOG.D(TAG,"checkVerifiedFaceDb modelModelId = " + modelModelId);

                for(int j = 0 ; j < verifiedFaceBean.size() ; j++){
                    String dbModelId = verifiedFaceBean.get(j).getBapModelId();
                    LOG.D(TAG,"checkVerifiedFaceDb dbModelId = " + dbModelId + " j = " + j);
                    if(modelModelId.equals(dbModelId)){
                        LOG.D(TAG,"checkVerifiedFaceDb modelModelId same modelModelId = " + modelModelId);
                        //check created time
                        String modelCreatedTime = identities.get(i).getCreatedTime();
                        String dbCreatedTime = verifiedFaceBean.get(j).getCreatedTime();

                        LOG.D(TAG,"checkVerifiedFaceDb modelCreatedTime = " + modelCreatedTime);
                        LOG.D(TAG,"checkVerifiedFaceDb dbCreatedTime = " + dbCreatedTime);

                        if(Long.parseLong(modelCreatedTime) > Long.parseLong(dbCreatedTime)){
                            //need update
                            LOG.D(TAG,"checkVerifiedFaceDb need update");
                            databaseAdapter.updateEmployeeIdentitiesByBapModelId(identities.get(i), modelModelId);
                        }else{
                            //same data, do nothing
                            LOG.D(TAG,"checkVerifiedFaceDb same data");

                        }

                        verifiedFaceBeanArrayList.get(j).setIsAlreadyFix(true);
                        getVerifiedIdAndImageModel.getIdentitiesData().getEmployeesIdentifyData().get(i).setIsAlreadyFix(true);
                        break;

                    }else{
//                        LOG.D(TAG,"checkVerifiedFaceDb need update");
                    }

                }

            }

            LOG.D(TAG,"checkVerifiedFaceDb getVerifiedIdAndImageModel.getIdentitiesData().getIdentifyData().size() = " +
                    getVerifiedIdAndImageModel.getIdentitiesData().getEmployeesIdentifyData().size());
            LOG.D(TAG,"checkVerifiedFaceDb verifiedFaceBeanArrayList.size() = " + verifiedFaceBeanArrayList.size());

            //3. Add data to DB
            if(getVerifiedIdAndImageModel.getIdentitiesData().getEmployeesIdentifyData().size() > 0){
                for(int i = 0 ; i < getVerifiedIdAndImageModel.getIdentitiesData().getEmployeesIdentifyData().size() ; i++){
                    LOG.D(TAG,"checkVerifiedFaceDb getVerifiedIdAndImageModel.getData().get(i).getIsAlreadyFix() = " +
                            getVerifiedIdAndImageModel.getIdentitiesData().getEmployeesIdentifyData().get(i).getIsAlreadyFix());
                    if(getVerifiedIdAndImageModel.getIdentitiesData().getEmployeesIdentifyData().get(i).getIsAlreadyFix() == true){

                    }else{
                        LOG.D(TAG,"checkVerifiedFaceDb getVerifiedIdAndImageModel.getData().get(i).getEmployeeId() = " +
                                getVerifiedIdAndImageModel.getIdentitiesData().getEmployeesIdentifyData().get(i).getEmployeeId());
                        VerifiedFaceBean verifiedFaceBeanData = new VerifiedFaceBean();
                        verifiedFaceBeanData.setBapModelId(identities.get(i).getBapModelId());
                        verifiedFaceBeanData.setId(identities.get(i).getId());
                        verifiedFaceBeanData.setEmployeeId(identities.get(i).getEmployeeId());


//                        verifiedFaceBeanData.setEmployeeName(identities.get(i).getEmployeeName());
//                        verifiedFaceBeanData.setVisitorName(identities.get(i).getVisitorName());

                        verifiedFaceBeanData.setEmployeeName(identities.get(i).getFirstName() + " " + identities.get(i).getLastName());
                        verifiedFaceBeanData.setVisitorName(identities.get(i).getFirstName() + " " + identities.get(i).getLastName());


                        verifiedFaceBeanData.setCreatedTime(identities.get(i).getCreatedTime());

                        byte[] imageData = Base64.decode(identities.get(i).getModel(), Base64.DEFAULT);
                        verifiedFaceBeanData.setModel(imageData);

                        databaseAdapter.createEmployeeIdentitiesFace(verifiedFaceBeanData);
                    }


                }
            }

            //4. delete data to DB
            if(verifiedFaceBeanArrayList.size() > 0){

                for(int i = 0 ; i < verifiedFaceBeanArrayList.size() ; i++){
                    LOG.D(TAG,"checkVerifiedFaceDb verifiedFaceBeanArrayList.get(i).getIsAlreadyFix() = " + verifiedFaceBeanArrayList.get(i).getIsAlreadyFix());
                    if(verifiedFaceBeanArrayList.get(i).getIsAlreadyFix() == true){

                    }else{
                        databaseAdapter.deleteEmployeeIdentitiesByBapModelId(verifiedFaceBeanArrayList.get(i).getBapModelId());
                    }

                }
            }

        }

//        databaseAdapter.close();

    }

    public static void checkAllEmployeeIdentitiesDb(ArrayList<IdentitiesModel> identities, Context context){
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance(context);

        databaseAdapter.deleteAllEmployeesIdentities();


        for(int i = 0 ; i < identities.size() ; i++){
            VerifiedFaceBean verifiedFaceBeanData = new VerifiedFaceBean();
            verifiedFaceBeanData.setBapModelId(identities.get(i).getBapModelId());
            verifiedFaceBeanData.setId(identities.get(i).getId());
            verifiedFaceBeanData.setEmployeeId(identities.get(i).getEmployeeId());

//                        verifiedFaceBeanData.setEmployeeName(identities.get(i).getEmployeeName());
//                        verifiedFaceBeanData.setVisitorName(identities.get(i).getVisitorName());

            verifiedFaceBeanData.setEmployeeName(identities.get(i).getFirstName() + " " + identities.get(i).getLastName());
            verifiedFaceBeanData.setVisitorName(identities.get(i).getFirstName() + " " + identities.get(i).getLastName());


            verifiedFaceBeanData.setCreatedTime(identities.get(i).getCreatedTime());

            byte[] imageData = Base64.decode(identities.get(i).getModel(), Base64.DEFAULT);
            verifiedFaceBeanData.setModel(imageData);

            databaseAdapter.createEmployeeIdentitiesFace(verifiedFaceBeanData);
        }

    }


    public static void checkVisitorIdentitiesDb(ArrayList<IdentitiesModel> identities, Context context){
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance(context);
//        databaseAdapter.open();

        //1. get all data from DB
        ArrayList<VerifiedFaceBean> verifiedFaceBean = databaseAdapter.getAllVisitorIdentitiesArrayList();

        LOG.D(TAG,"checkVerifiedFaceDb identities.size() = " + identities.size());

        if(verifiedFaceBean == null || verifiedFaceBean.size() == 0){

            for(int i = 0 ; i < identities.size() ; i++){
                VerifiedFaceBean verifiedFaceBeanData = new VerifiedFaceBean();

                verifiedFaceBeanData.setBapModelId(identities.get(i).getBapModelId());
                verifiedFaceBeanData.setId(identities.get(i).getId());
                verifiedFaceBeanData.setEmployeeId(identities.get(i).getEmployeeId());

//                verifiedFaceBeanData.setEmployeeName(identities.get(i).getEmployeeName());
//                verifiedFaceBeanData.setVisitorName(identities.get(i).getVisitorName());

                verifiedFaceBeanData.setEmployeeName(identities.get(i).getFirstName() + " " + identities.get(i).getLastName());
                verifiedFaceBeanData.setVisitorName(identities.get(i).getFirstName() + " " + identities.get(i).getLastName());

                verifiedFaceBeanData.setCreatedTime(identities.get(i).getCreatedTime());

                byte[] imageData = Base64.decode(identities.get(i).getModel(), Base64.DEFAULT);
                verifiedFaceBeanData.setModel(imageData);

                databaseAdapter.createVisitorIdentitiesFace(verifiedFaceBeanData);

            }

        }else{
            //check add or delete or update
            //update
            //1. prepare two copy data

            //model
            //verifiedFaceBean

            LOG.D(TAG,"checkVerifiedFaceDb verifiedFaceBean.size() = " + verifiedFaceBean.size());
            GetVerifiedIdAndImageModel getVerifiedIdAndImageModel = new GetVerifiedIdAndImageModel();
            IdentitiesDataModel identitiesDataModel = new IdentitiesDataModel();
            getVerifiedIdAndImageModel.setIdentitiesData(identitiesDataModel);
            for(int i = 0 ; i < identities.size() ; i++){
                getVerifiedIdAndImageModel.getIdentitiesData().addVisitorsIdentifyData(identities.get(i));
            }
            ArrayList<VerifiedFaceBean> verifiedFaceBeanArrayList = databaseAdapter.getAllVisitorIdentitiesArrayList();

            LOG.D(TAG,"checkVerifiedFaceDb do compare");

            //2. compare each other and update
            for(int i = 0 ; i < identities.size() ; i++){
                String modelModelId = identities.get(i).getBapModelId();

                LOG.D(TAG,"checkVerifiedFaceDb modelModelId = " + modelModelId);

                for(int j = 0 ; j < verifiedFaceBean.size() ; j++){
                    String dbModelId = verifiedFaceBean.get(j).getBapModelId();
                    LOG.D(TAG,"checkVerifiedFaceDb dbModelId = " + dbModelId + " j = " + j);
                    if(modelModelId.equals(dbModelId)){
                        LOG.D(TAG,"checkVerifiedFaceDb modelModelId same modelModelId = " + modelModelId);
                        //check created time
                        String modelCreatedTime = identities.get(i).getCreatedTime();
                        String dbCreatedTime = verifiedFaceBean.get(j).getCreatedTime();

                        LOG.D(TAG,"checkVerifiedFaceDb modelCreatedTime = " + modelCreatedTime);
                        LOG.D(TAG,"checkVerifiedFaceDb dbCreatedTime = " + dbCreatedTime);

                        if(Long.parseLong(modelCreatedTime) > Long.parseLong(dbCreatedTime)){
                            //need update
                            LOG.D(TAG,"checkVerifiedFaceDb need update");
                            databaseAdapter.updateVisitorIdentitiesByBapModelId(identities.get(i), modelModelId);
                        }else{
                            //same data, do nothing
                            LOG.D(TAG,"checkVerifiedFaceDb same data");

                        }

                        verifiedFaceBeanArrayList.get(j).setIsAlreadyFix(true);
                        getVerifiedIdAndImageModel.getIdentitiesData().getVisitorsIdentifyData().get(i).setIsAlreadyFix(true);
                        break;

                    }else{
//                        LOG.D(TAG,"checkVerifiedFaceDb need update");
                    }

                }

            }

            LOG.D(TAG,"checkVerifiedFaceDb getVerifiedIdAndImageModel.getIdentitiesData().getIdentifyData().size() = " +
                    getVerifiedIdAndImageModel.getIdentitiesData().getVisitorsIdentifyData().size());
            LOG.D(TAG,"checkVerifiedFaceDb verifiedFaceBeanArrayList.size() = " + verifiedFaceBeanArrayList.size());

            //3. Add data to DB
            if(getVerifiedIdAndImageModel.getIdentitiesData().getVisitorsIdentifyData().size() > 0){
                for(int i = 0 ; i < getVerifiedIdAndImageModel.getIdentitiesData().getVisitorsIdentifyData().size() ; i++){
                    LOG.D(TAG,"checkVerifiedFaceDb getVerifiedIdAndImageModel.getData().get(i).getIsAlreadyFix() = " +
                            getVerifiedIdAndImageModel.getIdentitiesData().getVisitorsIdentifyData().get(i).getIsAlreadyFix());
                    if(getVerifiedIdAndImageModel.getIdentitiesData().getVisitorsIdentifyData().get(i).getIsAlreadyFix() == true){

                    }else{
                        LOG.D(TAG,"checkVerifiedFaceDb getVerifiedIdAndImageModel.getData().get(i).getVisitorId() = " +
                                getVerifiedIdAndImageModel.getIdentitiesData().getVisitorsIdentifyData().get(i).getEmployeeId());
                        VerifiedFaceBean verifiedFaceBeanData = new VerifiedFaceBean();
                        verifiedFaceBeanData.setBapModelId(identities.get(i).getBapModelId());
                        verifiedFaceBeanData.setId(identities.get(i).getId());
                        verifiedFaceBeanData.setEmployeeId(identities.get(i).getEmployeeId());

//                        verifiedFaceBeanData.setEmployeeName(identities.get(i).getEmployeeName());
//                        verifiedFaceBeanData.setVisitorName(identities.get(i).getVisitorName());

                        verifiedFaceBeanData.setEmployeeName(identities.get(i).getFirstName() + " " + identities.get(i).getLastName());
                        verifiedFaceBeanData.setVisitorName(identities.get(i).getFirstName() + " " + identities.get(i).getLastName());

                        verifiedFaceBeanData.setCreatedTime(identities.get(i).getCreatedTime());

                        byte[] imageData = Base64.decode(identities.get(i).getModel(), Base64.NO_WRAP);
                        verifiedFaceBeanData.setModel(imageData);

                        databaseAdapter.createVisitorIdentitiesFace(verifiedFaceBeanData);
                    }


                }
            }

            //4. delete data to DB
            if(verifiedFaceBeanArrayList.size() > 0){

                for(int i = 0 ; i < verifiedFaceBeanArrayList.size() ; i++){
                    LOG.D(TAG,"checkVerifiedFaceDb verifiedFaceBeanArrayList.get(i).getIsAlreadyFix() = " + verifiedFaceBeanArrayList.get(i).getIsAlreadyFix());
                    if(verifiedFaceBeanArrayList.get(i).getIsAlreadyFix() == true){

                    }else{
                        databaseAdapter.deleteVisitorIdentitiesByBapModelId(verifiedFaceBeanArrayList.get(i).getBapModelId());
                    }

                }
            }

        }

//        databaseAdapter.close();

    }

    public static void checkAllVisitorIdentitiesDb(ArrayList<IdentitiesModel> identities, Context context){
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance(context);

        databaseAdapter.deleteAllVisitorsIdentities();


        for(int i = 0 ; i < identities.size() ; i++){
            VerifiedFaceBean verifiedFaceBeanData = new VerifiedFaceBean();
            verifiedFaceBeanData.setBapModelId(identities.get(i).getBapModelId());
            verifiedFaceBeanData.setId(identities.get(i).getId());
            verifiedFaceBeanData.setEmployeeId(identities.get(i).getEmployeeId());


//                        verifiedFaceBeanData.setEmployeeName(identities.get(i).getEmployeeName());
//                        verifiedFaceBeanData.setVisitorName(identities.get(i).getVisitorName());

            verifiedFaceBeanData.setEmployeeName(identities.get(i).getFirstName() + " " + identities.get(i).getLastName());
            verifiedFaceBeanData.setVisitorName(identities.get(i).getFirstName() + " " + identities.get(i).getLastName());


            verifiedFaceBeanData.setCreatedTime(identities.get(i).getCreatedTime());

            byte[] imageData = Base64.decode(identities.get(i).getModel(), Base64.DEFAULT);
            verifiedFaceBeanData.setModel(imageData);

            databaseAdapter.createVisitorIdentitiesFace(verifiedFaceBeanData);
        }

    }


    public static void checkEmployeeAcceptancesDb(ArrayList<AcceptancesModel> acceptancesModels, Context context) {
        LOG.D(TAG,"checkEmployeeAcceptancesDb");
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance(context);
//        databaseAdapter.open();

        //1. delete all db data
        databaseAdapter.deleteAllEmployeesAcceptances();

        //2. add all data to db
        for(int i = 0 ; i < acceptancesModels.size() ; i++){
            AcceptancesBean acceptancesBean = new AcceptancesBean();

            acceptancesBean.setId(acceptancesModels.get(i).getUuid());
            acceptancesBean.setEmployeeId(acceptancesModels.get(i).getEmployeeId());
            acceptancesBean.setSecurityCode(acceptancesModels.get(i).getSecurityCode());
            acceptancesBean.setRfid(acceptancesModels.get(i).getRfid());
            acceptancesBean.setFirstName(acceptancesModels.get(i).getFirstName());
            acceptancesBean.setLastName(acceptancesModels.get(i).getLastName());
            acceptancesBean.setPhotoUrl(acceptancesModels.get(i).getPhotoUrl());
            acceptancesBean.setIntId(acceptancesModels.get(i).getIntId());

//            acceptancesBean.setPhoto();

            databaseAdapter.createEmployeesAttendances(acceptancesBean);

        }

//        databaseAdapter.close();

    }

    public static void checkVisitorsAcceptancesDb(ArrayList<AcceptancesModel> acceptancesModels, Context context) {
        LOG.D(TAG,"checkVisitorsAcceptancesDb");
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance(context);
//        databaseAdapter.open();

        //1. delete all db data
        databaseAdapter.deleteAllVisitorsAcceptances();

        //2. add all data to db
        for(int i = 0 ; i < acceptancesModels.size() ; i++){
            LOG.D(TAG,"checkVisitorsAcceptancesDb acceptancesModels.get(i).getSecurityCode()");

            AcceptancesBean acceptancesBean = new AcceptancesBean();

            acceptancesBean.setId(acceptancesModels.get(i).getUuid());
            acceptancesBean.setEmployeeId(acceptancesModels.get(i).getEmployeeId());
            acceptancesBean.setSecurityCode(acceptancesModels.get(i).getSecurityCode());
            acceptancesBean.setRfid(acceptancesModels.get(i).getRfid());
            acceptancesBean.setFirstName(acceptancesModels.get(i).getFirstName());
            acceptancesBean.setLastName(acceptancesModels.get(i).getLastName());
            acceptancesBean.setPhotoUrl(acceptancesModels.get(i).getPhotoUrl());
            acceptancesBean.setIntId(acceptancesModels.get(i).getIntId());

//            acceptancesBean.setPhoto();

            databaseAdapter.createVisitorsAttendances(acceptancesBean);

        }

//        databaseAdapter.close();

    }

    public static void addEmployeesAcceptancesDbToModel(Context context){
        LOG.D(TAG,"addEmployeesAcceptancesDbToModel");
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance(context);
//        databaseAdapter.open();

        EmployeeDataModel employeeDataModel = new EmployeeDataModel();

        ArrayList<AcceptancesBean> acceptancesBeanArrayList = databaseAdapter.getAllEmployeesAcceptancesArrayList();

        //  Test
//        acceptancesBeanArrayList = null;
        LOG.D(TAG,"addEmployeesAcceptancesDbToModel acceptancesBeanArrayList = " + acceptancesBeanArrayList);
        if(acceptancesBeanArrayList != null){
            LOG.D(TAG,"addEmployeesAcceptancesDbToModel acceptancesBeanArrayList.size() = " + acceptancesBeanArrayList.size());
            ArrayList<AcceptancesModel> acceptancesModelArrayList = new ArrayList<>(acceptancesBeanArrayList.size());

            for(int i = 0 ; i < acceptancesBeanArrayList.size() ; i++){

                AcceptancesModel acceptancesModel = new AcceptancesModel();

                acceptancesModel.setUuid(acceptancesBeanArrayList.get(i).getId());
                acceptancesModel.setEmployeeId(acceptancesBeanArrayList.get(i).getEmployeeId());
                acceptancesModel.setSecurityCode(acceptancesBeanArrayList.get(i).getSecurityCode());
                acceptancesModel.setRfid(acceptancesBeanArrayList.get(i).getRfid());
                acceptancesModel.setFirstName(acceptancesBeanArrayList.get(i).getFirstName());
                acceptancesModel.setLastName(acceptancesBeanArrayList.get(i).getLastName());
                acceptancesModel.setPhotoUrl(acceptancesBeanArrayList.get(i).getPhotoUrl());
                acceptancesModel.setIntId(acceptancesBeanArrayList.get(i).getIntId());
//            acceptancesModelArrayList.get(i).setUuid(acceptancesBeanArrayList.get(i).getPhoto());

                acceptancesModelArrayList.add(acceptancesModel);
            }

            employeeDataModel.setAcceptances(acceptancesModelArrayList);

            DeviceUtils.mEmployeeModel.setEmployeeData(employeeDataModel);
        }else{
            DeviceUtils.mEmployeeModel.setEmployeeData(null);
        }

//        databaseAdapter.close();
    }

//    public static void addVisitorsAcceptancesDbToModel(Context context){
//        LOG.D(TAG,"addVisitorsAcceptancesDbToModel");
//        DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance(context);
//        databaseAdapter.open();
//
//        VisitorDataModel visitorDataModel = new VisitorDataModel();
//        ArrayList<AcceptancesBean> acceptancesBeanArrayList = databaseAdapter.getAllVisitorsAcceptancesArrayList();
//
//        //  Test
////        acceptancesBeanArrayList = null;
//        LOG.D(TAG,"addVisitorsAcceptancesDbToModel acceptancesBeanArrayList = " + acceptancesBeanArrayList);
//        if(acceptancesBeanArrayList != null){
//            LOG.D(TAG,"addVisitorsAcceptancesDbToModel acceptancesBeanArrayList.size() = " + acceptancesBeanArrayList.size());
//            ArrayList<AcceptancesModel> acceptancesModelArrayList = new ArrayList<>(acceptancesBeanArrayList.size());
//
//            for(int i = 0 ; i < acceptancesBeanArrayList.size() ; i++){
//
//                AcceptancesModel acceptancesModel = new AcceptancesModel();
//
//                acceptancesModel.setUuid(acceptancesBeanArrayList.get(i).getId());
//                acceptancesModel.setEmployeeId(acceptancesBeanArrayList.get(i).getEmployeeId());
//                acceptancesModel.setSecurityCode(acceptancesBeanArrayList.get(i).getSecurityCode());
//                acceptancesModel.setRfid(acceptancesBeanArrayList.get(i).getRfid());
//                acceptancesModel.setFirstName(acceptancesBeanArrayList.get(i).getFirstName());
//                acceptancesModel.setLastName(acceptancesBeanArrayList.get(i).getLastName());
//                acceptancesModel.setPhotoUrl(acceptancesBeanArrayList.get(i).getPhotoUrl());
//                acceptancesModel.setIntId(acceptancesBeanArrayList.get(i).getIntId());
////            acceptancesModelArrayList.get(i).setUuid(acceptancesBeanArrayList.get(i).getPhoto());
//
//                acceptancesModelArrayList.add(acceptancesModel);
//            }
//
//            visitorDataModel.setAcceptances(acceptancesModelArrayList);
//
//            DeviceUtils.mVisitorModel.setVisitorData(visitorDataModel);
//        }else{
//            DeviceUtils.mVisitorModel.setVisitorData(null);
//        }
//
//        databaseAdapter.close();
//    }


    public static void addVisitorsAcceptancesDbToModel(Context context){
        LOG.D(TAG,"addVisitorsAcceptancesDbToModel");
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance(context);

        VisitorDataModel visitorDataModel = new VisitorDataModel();
        ArrayList<AcceptancesBean> acceptancesBeanArrayList = databaseAdapter.getAllVisitorsAcceptancesArrayList();

        //  Test
//        acceptancesBeanArrayList = null;
        LOG.D(TAG,"addVisitorsAcceptancesDbToModel acceptancesBeanArrayList = " + acceptancesBeanArrayList);
        if(acceptancesBeanArrayList != null){
            LOG.D(TAG,"addVisitorsAcceptancesDbToModel acceptancesBeanArrayList.size() = " + acceptancesBeanArrayList.size());
            ArrayList<AcceptancesModel> acceptancesModelArrayList = new ArrayList<>(acceptancesBeanArrayList.size());

            for(int i = 0 ; i < acceptancesBeanArrayList.size() ; i++){

                AcceptancesModel acceptancesModel = new AcceptancesModel();

                acceptancesModel.setUuid(acceptancesBeanArrayList.get(i).getId());
                acceptancesModel.setEmployeeId(acceptancesBeanArrayList.get(i).getEmployeeId());
                acceptancesModel.setSecurityCode(acceptancesBeanArrayList.get(i).getSecurityCode());
                acceptancesModel.setRfid(acceptancesBeanArrayList.get(i).getRfid());
                acceptancesModel.setFirstName(acceptancesBeanArrayList.get(i).getFirstName());
                acceptancesModel.setLastName(acceptancesBeanArrayList.get(i).getLastName());
                acceptancesModel.setPhotoUrl(acceptancesBeanArrayList.get(i).getPhotoUrl());
                acceptancesModel.setIntId(acceptancesBeanArrayList.get(i).getIntId());
//            acceptancesModelArrayList.get(i).setUuid(acceptancesBeanArrayList.get(i).getPhoto());

                acceptancesModelArrayList.add(acceptancesModel);
            }

            visitorDataModel.setAcceptances(acceptancesModelArrayList);

            DeviceUtils.mVisitorModel.setVisitorData(visitorDataModel);
        }else{
            DeviceUtils.mVisitorModel.setVisitorData(null);
        }

    }


    public static void addDeviceLoginToDb(Context context, LoginModel loginModel){
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance(context);
//        databaseAdapter.open();

        databaseAdapter.deleteAllDeviceLogin();

        for(int i = 0 ; i < loginModel.getLoginData().getModuleModes().size() ; i++){
            DeviceLoginBean deviceLoginBean = new DeviceLoginBean();

            deviceLoginBean.setLocale(loginModel.getLoginData().getLocale());
            deviceLoginBean.setModule(String.valueOf( loginModel.getLoginData().getModuleModes().get(i).getModule()));
            deviceLoginBean.setModes(Arrays.toString(loginModel.getLoginData().getModuleModes().get(i).getMode()));

            databaseAdapter.createDeviceLogin(deviceLoginBean);

        }

//        databaseAdapter.close();

    }

    public static ArrayList<DeviceLoginBean> getAllDeviceLogin(Context context){
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance(context);
//        databaseAdapter.open();

        ArrayList<DeviceLoginBean> deviceLoginBeanArrayList =  databaseAdapter.getAllDeviceInfo();

//        databaseAdapter.close();

        return deviceLoginBeanArrayList;

    }

    public static void addRegisterToEmployeeAcceptanceDb(Context context, RegisterBean registerBean){
        LOG.D(TAG,"addRegisterToEmployeeAcceptanceDb registerBean.getSecurityCode() = " + registerBean.getSecurityCode());
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance(context);

        AcceptancesBean acceptancesBean = new AcceptancesBean();

        //check id if in DB
//        registerBean.getModel()

        boolean isRepeat = databaseAdapter.SearchEmployeeAcceptanceBySecurityCode(registerBean.getSecurityCode());

        LOG.D(TAG,"addRegisterToEmployeeAcceptanceDb isRepeat = " + isRepeat);
        acceptancesBean.setId(null);
        acceptancesBean.setEmployeeId(registerBean.getEmployeeId());
        acceptancesBean.setSecurityCode(registerBean.getSecurityCode());
        acceptancesBean.setRfid(registerBean.getRfid());
        acceptancesBean.setFirstName(null);
        acceptancesBean.setLastName(registerBean.getName());
        acceptancesBean.setPhotoUrl(null);
        acceptancesBean.setIntId(registerBean.getModelId());

//            acceptancesBean.setPhoto();

        if(isRepeat){
            databaseAdapter.updateEmployeeAcceptancesBySecurityCode(acceptancesBean, registerBean.getSecurityCode());
        }else{
            databaseAdapter.createEmployeesAttendances(acceptancesBean);
        }



    }

    public static void addRegisterToVisitorAcceptanceDb(Context context, RegisterBean registerBean){
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance(context);

        AcceptancesBean acceptancesBean = new AcceptancesBean();

        //check id if in DB
        boolean isRepeat = databaseAdapter.SearchVisitorAcceptanceBySecurityCode(registerBean.getSecurityCode());

        acceptancesBean.setId(null);
        acceptancesBean.setEmployeeId(registerBean.getEmployeeId());
        acceptancesBean.setSecurityCode(registerBean.getMobileNo());
        acceptancesBean.setRfid(null);
        acceptancesBean.setFirstName("");
        acceptancesBean.setLastName(registerBean.getName());
        acceptancesBean.setPhotoUrl(null);
        acceptancesBean.setIntId(registerBean.getModelId());

//            acceptancesBean.setPhoto();

        if(isRepeat){
            databaseAdapter.updateVisitorAcceptancesBySecurityCode(acceptancesBean, registerBean.getMobileNo());
        }else{
            databaseAdapter.createVisitorsAttendances(acceptancesBean);
        }


    }

    public static void addRegisterToEmployeeIdentityDb(Context context, RegisterBean registerBean){
        LOG.D(TAG,"addRegisterToEmployeeIdentityDb");
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance(context);


        boolean isRepeat = databaseAdapter.SearchEmployeeIdentityBySecurityCode(registerBean.getSecurityCode());

        LOG.D(TAG,"addRegisterToEmployeeIdentityDb isRepeat = " + isRepeat);

        VerifiedFaceBean verifiedFaceBeanData = new VerifiedFaceBean();
        verifiedFaceBeanData.setBapModelId(String.valueOf(registerBean.getModelId()));
//        verifiedFaceBeanData.setId(identities.get(i).getId());
        verifiedFaceBeanData.setEmployeeId(registerBean.getEmployeeId());


//                        verifiedFaceBeanData.setEmployeeName(identities.get(i).getEmployeeName());
//                        verifiedFaceBeanData.setVisitorName(identities.get(i).getVisitorName());

        verifiedFaceBeanData.setEmployeeName(registerBean.getName());
        verifiedFaceBeanData.setVisitorName(registerBean.getName());


        verifiedFaceBeanData.setCreatedTime(registerBean.getCreateTime());
        verifiedFaceBeanData.setSecurityCode(registerBean.getSecurityCode());

//        byte[] imageData = Base64.decode(registerBean.getModel(), Base64.DEFAULT);
        verifiedFaceBeanData.setModel(registerBean.getModel());

        if(isRepeat){
            databaseAdapter.updateEmployeeIdentitiesFaceBySecurityCode(verifiedFaceBeanData, registerBean.getSecurityCode());
        }else{
            databaseAdapter.createEmployeeIdentitiesFace(verifiedFaceBeanData);
        }




    }

    public static void addRegisterToVisitorIdentityDb(Context context, RegisterBean registerBean){
        LOG.D(TAG,"addRegisterToVisitorIdentityDb registerBean.getMobileNo() = " + registerBean.getMobileNo());
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance(context);

        boolean isRepeat = databaseAdapter.SearchVisitorIdentityByMobileNo(registerBean.getMobileNo());

        VerifiedFaceBean verifiedFaceBeanData = new VerifiedFaceBean();
        verifiedFaceBeanData.setBapModelId(String.valueOf(registerBean.getModelId()));
//        verifiedFaceBeanData.setId(identities.get(i).getId());
        verifiedFaceBeanData.setEmployeeId(registerBean.getEmployeeId());


//                        verifiedFaceBeanData.setEmployeeName(identities.get(i).getEmployeeName());
//                        verifiedFaceBeanData.setVisitorName(identities.get(i).getVisitorName());

        verifiedFaceBeanData.setEmployeeName(registerBean.getName());
        verifiedFaceBeanData.setVisitorName(registerBean.getName());


        verifiedFaceBeanData.setCreatedTime(registerBean.getCreateTime());
        verifiedFaceBeanData.setSecurityCode(registerBean.getMobileNo());

//        byte[] imageData = Base64.decode(registerBean.getModel(), Base64.DEFAULT);
        verifiedFaceBeanData.setModel(registerBean.getModel());




        if(isRepeat){
            databaseAdapter.updateVisitorIdentitiesFaceByMobileNo(verifiedFaceBeanData, registerBean.getMobileNo());
        }else{
            databaseAdapter.createVisitorIdentitiesFace(verifiedFaceBeanData);
        }

    }

    public static void addRegisterToEmployeeRegisterDb(Context context, RegisterBean registerBean){
        LOG.D(TAG,"addRegisterToEmployeeRegisterDb");
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance(context);

        databaseAdapter.createEmployeesRegister(registerBean);
    }

    public static void addRegisterToVisitorRegisterDb(Context context, RegisterBean registerBean){
        LOG.D(TAG,"addRegisterToVisitorRegisterDb");
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance(context);

        databaseAdapter.createVisitorsRegister(registerBean);
    }


    public static void addRegisterToEmployeeModel(Context context, RegisterBean registerBean){
        LOG.D(TAG,"addRegisterToEmployeeModel DeviceUtils.mEmployeeModel = " + DeviceUtils.mEmployeeModel);
        if(DeviceUtils.mEmployeeModel != null){
            if(DeviceUtils.mEmployeeModel.getEmployeeData() != null){
                //check if repeat security number

                int index = -1;
                if(DeviceUtils.mEmployeeModel.getEmployeeData().getAcceptances() != null){
                    for(int i = 0 ; i < DeviceUtils.mEmployeeModel.getEmployeeData().getAcceptances().size(); i++){
                        if(registerBean.getSecurityCode().equals(DeviceUtils.mEmployeeModel.getEmployeeData().getAcceptances().get(i).getSecurityCode())){
                            index = i;
                            break;
                        }
                    }
                }


                AcceptancesModel acceptancesModel = new AcceptancesModel();
                acceptancesModel.setEmployeeId(registerBean.getEmployeeId());
                acceptancesModel.setIntId(registerBean.getModelId());
                acceptancesModel.setSecurityCode(registerBean.getSecurityCode());
                acceptancesModel.setUuid(null);
                acceptancesModel.setPhotoUrl("");
                acceptancesModel.setFirstName("");
                acceptancesModel.setLastName(registerBean.getName());
                acceptancesModel.setRfid(registerBean.getRfid());

                LOG.D(TAG,"addRegisterToEmployeeModel index = " + index);
                if(index != -1){
                    //update
                    DeviceUtils.mEmployeeModel.getEmployeeData().updateAcceptance(index, acceptancesModel);
                }else{
                    //Add
                    DeviceUtils.mEmployeeModel.getEmployeeData().addAcceptances(acceptancesModel);
                }


            }
        }

    }

    public static void addRegisterToVisitorModel(Context context, RegisterBean registerBean){
        LOG.D(TAG,"addRegisterToVisitorModel DeviceUtils.mEmployeeModel = " + DeviceUtils.mEmployeeModel);
        if(DeviceUtils.mVisitorModel != null){
            if(DeviceUtils.mVisitorModel.getVisitorData() != null){
                //check if repeat security number

                int index = -1;
                if(DeviceUtils.mVisitorModel.getVisitorData().getAcceptances() != null){
                    for(int i = 0 ; i < DeviceUtils.mVisitorModel.getVisitorData().getAcceptances().size(); i++){
                        if(registerBean.getSecurityCode().equals(DeviceUtils.mVisitorModel.getVisitorData().getAcceptances().get(i).getSecurityCode())){
                            index = i;
                            break;
                        }
                    }
                }


                AcceptancesModel acceptancesModel = new AcceptancesModel();
                acceptancesModel.setEmployeeId(registerBean.getEmployeeId());
                acceptancesModel.setIntId(registerBean.getModelId());
                acceptancesModel.setSecurityCode(registerBean.getMobileNo());
                acceptancesModel.setUuid(null);
                acceptancesModel.setPhotoUrl("");
                acceptancesModel.setFirstName("");
                acceptancesModel.setLastName(registerBean.getName());
                acceptancesModel.setRfid(null);

                LOG.D(TAG,"addRegisterToEmployeeModel index = " + index);
                if(index != -1){
                    //update
                    DeviceUtils.mVisitorModel.getVisitorData().updateAcceptance(index, acceptancesModel);
                }else{
                    //Add
                    DeviceUtils.mVisitorModel.getVisitorData().addAcceptances(acceptancesModel);
                }


            }
        }

    }

    private static IRegisterEmployeeFromDbListener registerEmployeeFromDbListener = new IRegisterEmployeeFromDbListener() {
        @Override
        public void onRegisterEmployeeFromDb(RegisterReplyModel model) {
            LOG.D(TAG,"onRegisterEmployeeFromDb model = " + model);
        }
    };

    private static IRegisterVisitorFromDbListener registerVisitorFromDbListener = new IRegisterVisitorFromDbListener() {
        @Override
        public void onRegisterVisitorFromDb(RegisterReplyModel model) {
            LOG.D(TAG,"registerVisitorFromDbListener model = " + model);
        }
    };



    //******************************************  bluetooth door***************************************/
    public static void openDoorOne(Context context){
        LOG.D(TAG, "openDoorOne DeviceUtils.mIsBTConnected = " + DeviceUtils.mIsBTConnected);
        if(!DeviceUtils.mIsBTConnected){

            return;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME_ATTENDANCE_ENTERPRISE, Context.MODE_PRIVATE);
        mHandler.removeMessages(Constants.MSG_CLOSE_BLUETOOTH_DOOR_ONE);

        String bluetoothDoorPassword = sharedPreferences.getString(Constants.PREF_KEY_BLUETOOTH_DOOR_PASSWORD, null);
        openDoorOne(bluetoothDoorPassword, context);

        int bluetoothDoorIdleSeconds = sharedPreferences.getInt(Constants.PREF_KEY_BLUETOOTH_DOOR_IDEL_SECONDS, 5);
        mHandler.sendEmptyMessageDelayed(Constants.MSG_CLOSE_BLUETOOTH_DOOR_ONE, (bluetoothDoorIdleSeconds * 1000));
        Message msg = new Message();
        msg.what = Constants.MSG_CLOSE_BLUETOOTH_DOOR_ONE;
        msg.obj = bluetoothDoorPassword;

        mHandler.sendMessageDelayed(msg, (bluetoothDoorIdleSeconds * 1000));


    }

    public static void openDoorTwo(Context context){
        LOG.D(TAG, "openDoorTwo");
        LOG.D(TAG, "openDoorTwo DeviceUtils.mIsBTConnected = " + DeviceUtils.mIsBTConnected);
        if(!DeviceUtils.mIsBTConnected){

            return;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME_ATTENDANCE_ENTERPRISE, Context.MODE_PRIVATE);
        mHandler.removeMessages(Constants.MSG_CLOSE_BLUETOOTH_DOOR_TWO);

        String bluetoothDoorPassword = sharedPreferences.getString(Constants.PREF_KEY_BLUETOOTH_DOOR_PASSWORD, null);
        openDoorTwo(bluetoothDoorPassword);

        int bluetoothDoorIdleSeconds = sharedPreferences.getInt(Constants.PREF_KEY_BLUETOOTH_DOOR_IDEL_SECONDS, 5);
        mHandler.sendEmptyMessageDelayed(Constants.MSG_CLOSE_BLUETOOTH_DOOR_TWO, (bluetoothDoorIdleSeconds * 1000));
        Message msg = new Message();
        msg.what = Constants.MSG_CLOSE_BLUETOOTH_DOOR_TWO;
        msg.obj = bluetoothDoorPassword;

        mHandler.sendMessageDelayed(msg, (bluetoothDoorIdleSeconds * 1000));

    }



    public static void openDoorOne(String password, Context context){
        //1路开
        byte[] data = {(byte) 0xC5,(byte)0x04,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0xAA};

        if(password == null){
            password = "12345678";
        }

        Log.d(TAG, "password = " + password);
        Log.d(TAG, "password.length() = " + password.length());
        if(password.length() < 8){
            Toast.makeText(context, context.getString(R.string.input_8_digit_password), Toast.LENGTH_SHORT).show();
        }else{
            data[2]=(byte) password.charAt(0);
            data[3]=(byte) password.charAt(1);
            data[4]=(byte) password.charAt(2);
            data[5]=(byte) password.charAt(3);
            data[6]=(byte) password.charAt(4);
            data[7]=(byte) password.charAt(5);
            data[8]=(byte) password.charAt(6);
            data[9]=(byte) password.charAt(7);

            mBluetoothLeService.WriteBytes(data);}

    }

    public static void closeDoorOne(String password){
        //1路关
        byte[] data = {(byte) 0xC5,(byte)0x06,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0xAA};
        if(password == null){
            password = "12345678";
        }

        if(password.length() < 8){
//            Toast.makeText(BTTrsActivity.this, "请在设置里设置正确的8位密码",
//                    Toast.LENGTH_SHORT).show();
        }else{
            data[2]=(byte) password.charAt(0);
            data[3]=(byte) password.charAt(1);
            data[4]=(byte) password.charAt(2);
            data[5]=(byte) password.charAt(3);
            data[6]=(byte) password.charAt(4);
            data[7]=(byte) password.charAt(5);
            data[8]=(byte) password.charAt(6);
            data[9]=(byte) password.charAt(7);
            mBluetoothLeService.WriteBytes(data);
        }

    }


    public static void openDoorTwo(String password){
        //1路开
        byte[] data = {(byte) 0xC5,(byte)0x05,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0xAA};

        if(password == null){
            password = "12345678";
        }

        LOG.D(TAG, "password = " + password);
        LOG.D(TAG, "password.length() = " + password.length());
        if(password.length() < 8){
            Toast.makeText(mContext, mContext.getString(R.string.input_8_digit_password), Toast.LENGTH_SHORT).show();
        }else{
            data[2]=(byte) password.charAt(0);
            data[3]=(byte) password.charAt(1);
            data[4]=(byte) password.charAt(2);
            data[5]=(byte) password.charAt(3);
            data[6]=(byte) password.charAt(4);
            data[7]=(byte) password.charAt(5);
            data[8]=(byte) password.charAt(6);
            data[9]=(byte) password.charAt(7);

            mBluetoothLeService.WriteBytes(data);}

    }

    public static void closeDoorTwo(String password){
        //2路关
        byte[] data = {(byte) 0xC5,(byte)0x07,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0x99,(byte)0xAA};
        if(password == null){
            password = "12345678";
        }

        if(password.length() < 8){
//            Toast.makeText(BTTrsActivity.this, "请在设置里设置正确的8位密码",
//                    Toast.LENGTH_SHORT).show();
        }else{
            data[2]=(byte) password.charAt(0);
            data[3]=(byte) password.charAt(1);
            data[4]=(byte) password.charAt(2);
            data[5]=(byte) password.charAt(3);
            data[6]=(byte) password.charAt(4);
            data[7]=(byte) password.charAt(5);
            data[8]=(byte) password.charAt(6);
            data[9]=(byte) password.charAt(7);
            mBluetoothLeService.WriteBytes(data);
        }

    }

    public static boolean checkIfSecurityCodeLegal(String securityCode, RoleModel roleModel){
        boolean isLoginAccountCanClock = false;

        if(roleModel instanceof EmployeeModel){
            EmployeeModel employeeModel = ((EmployeeModel) roleModel);

            if(employeeModel.getEmployeeData() != null){
                for(int i = 0 ; i < employeeModel.getEmployeeData().getAcceptances().size() ; i++){
                    if(securityCode.equals(employeeModel.getEmployeeData().getAcceptances().get(i).getSecurityCode())){
                        isLoginAccountCanClock = true;
                        break;
                    }
                }

            }else{

            }

        }else if(roleModel instanceof VisitorModel){
            VisitorModel visitorModel = ((VisitorModel) roleModel);
            if(visitorModel.getVisitorData() != null){
                for(int i = 0 ; i < visitorModel.getVisitorData().getAcceptances().size() ; i++){
                    if(securityCode.equals(visitorModel.getVisitorData().getAcceptances().get(i).getSecurityCode())){
                        isLoginAccountCanClock = true;

                        Calendar c = Calendar.getInstance();
                        if(visitorModel.getVisitorData().getAcceptances().get(i).getStartTime() <= c.getTimeInMillis() &&
                                visitorModel.getVisitorData().getAcceptances().get(i).getEndTime() >= c.getTimeInMillis()){
                            ClockUtils.mStartTime = visitorModel.getVisitorData().getAcceptances().get(i).getStartTime();
                            ClockUtils.mEndTime = visitorModel.getVisitorData().getAcceptances().get(i).getEndTime();
                            break;
                        }else{
                            //expire time
                            ClockUtils.mStartTime = -1;
                            ClockUtils.mEndTime = -1;
                        }

                    }
                }
            }else{

            }
        }

        return isLoginAccountCanClock;
    }


    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LOG.D(TAG, "mHandler msg.what = " + msg.what);
            String bluetoothDoorPassword = null;
            switch (msg.what) {
                case Constants.MSG_CLOSE_BLUETOOTH_DOOR_ONE:
                    if(!DeviceUtils.mIsBTConnected){

                        return;
                    }

                    bluetoothDoorPassword = (String) msg.obj;
                    LOG.D(TAG, "MSG_CLOSE_BLUETOOTH_DOOR_ONE  bluetoothDoorPassword = " + bluetoothDoorPassword);
                    closeDoorOne(bluetoothDoorPassword);

                    break;

                case Constants.MSG_CLOSE_BLUETOOTH_DOOR_TWO:
                    if(!DeviceUtils.mIsBTConnected){

                        return;
                    }

                    bluetoothDoorPassword = (String) msg.obj;
                    LOG.D(TAG, "MSG_CLOSE_BLUETOOTH_DOOR_TWO  bluetoothDoorPassword = " + bluetoothDoorPassword);
                    closeDoorTwo(bluetoothDoorPassword);

                    break;
            }
        }
    };


}
