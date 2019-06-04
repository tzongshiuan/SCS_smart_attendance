package com.gorilla.attendance.enterprise.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.datamodel.EmployeeModel;
import com.gorilla.attendance.enterprise.datamodel.GetEmployeeModel;
import com.gorilla.attendance.enterprise.datamodel.GetMarqueesModel;
import com.gorilla.attendance.enterprise.datamodel.GetVerifiedIdAndImageModel;
import com.gorilla.attendance.enterprise.datamodel.GetVideoModel;
import com.gorilla.attendance.enterprise.datamodel.GetVisitorModel;
import com.gorilla.attendance.enterprise.datamodel.PlayVideoModel;
import com.gorilla.attendance.enterprise.datamodel.RecordsReplyModel;
import com.gorilla.attendance.enterprise.datamodel.VideosModel;
import com.gorilla.attendance.enterprise.datamodel.VisitorModel;
import com.gorilla.attendance.enterprise.util.apitask.listener.IAccessUnrecognizedLogListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IAccessVisitorUnrecognizedLogListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IAttendanceUnrecognizedLogListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IGetDeviceEmployeesListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IGetDeviceMarqueesListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IGetDeviceVerifiedIdAndImageListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IGetDeviceVideosListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IGetDeviceVisitorsListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IGetVerifiedIdAndImageListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IVisitorsUnrecognizedLogListener;
import com.gorilla.enroll.lib.listener.OnEnrollListener;
import com.gorilla.enroll.lib.listener.OnVerifyListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//import com.gorilla.enroll.lib.listener.OnEnrollListener;
//import com.gorilla.enroll.lib.listener.OnVerifyListener;

//import com.gorilla.enroll.lib.listener.OnEnrollListener;
//import com.gorilla.enroll.lib.listener.OnVerifyListener;

//import com.gorilla.enroll.lib.listener.OnEnrollListener;
//import com.gorilla.enroll.lib.listener.OnVerifyListener;

/**
 * Created by ggshao on 2017/2/12.
 */

public class CallbackUtils {
    public static final String TAG = "CallbackUtils";
    private static Context mContext = null;
    private static Handler mCallback = null;
    private static SharedPreferences mSharedPreference = null;
    private static boolean isModelDone = false;

    private static byte[] mModel = null;

    public static void setContext(Context context, Handler callback) {
        mContext = context;
        mCallback = callback;
        mSharedPreference = mContext.getSharedPreferences(Constants.PREF_NAME_ATTENDANCE_ENTERPRISE, Context.MODE_PRIVATE);

    }


    public static IGetVerifiedIdAndImageListener getVerifiedIdAndImageListener = new IGetVerifiedIdAndImageListener() {
        @Override
        public void onGetVerifiedIdAndImage(GetVerifiedIdAndImageModel model) {
            //  Test
//            model = null;
            LOG.D(TAG, "onGetVerifiedIdAndImage model = " + model);

            if (model != null) {

                IdentifyEmployeeManager.getInstance(mContext).addModel(model.getIdentitiesData().getEmployeesIdentifyData());
                IdentifyVisitorManager.getInstance(mContext).addModel(model.getIdentitiesData().getVisitorsIdentifyData());

                //set UI initial done
//                mCallback.sendEmptyMessage(Constants.SET_ACTIVITY_UI);
//                mCallback.sendEmptyMessage(Constants.CLOSE_MESSAGE_DIALOG);

            } else {
                LOG.D(TAG, "onGetVerifiedIdAndImage model == null");
                //add DB Data to identity

                IdentifyEmployeeManager.getInstance(mContext).addModelFromDb();
                IdentifyVisitorManager.getInstance(mContext).addModelFromDb();


            }

        }
    };

    public static IGetDeviceVerifiedIdAndImageListener getDeviceVerifiedIdAndImageListener = new IGetDeviceVerifiedIdAndImageListener() {
        @Override
        public void onGetDeviceVerifiedIdAndImage(GetVerifiedIdAndImageModel model) {
            //  Test
//            model = null;
            LOG.D(TAG, "onGetVerifiedIdAndImage model = " + model);

            if (model != null) {

                IdentifyEmployeeManager.getInstance(mContext).addModel(model.getIdentitiesData().getEmployeesIdentifyData());
                IdentifyVisitorManager.getInstance(mContext).addModel(model.getIdentitiesData().getVisitorsIdentifyData());


            } else {
                LOG.D(TAG, "onGetVerifiedIdAndImage model == null");
                //add DB Data to identity

                IdentifyEmployeeManager.getInstance(mContext).addModelFromDb();
                IdentifyVisitorManager.getInstance(mContext).addModelFromDb();

            }

            LOG.D(TAG, "onGetVerifiedIdAndImageOKOK model = " + model);
        }
    };


    public static IAttendanceUnrecognizedLogListener attendanceUnrecognizedLogListener = new IAttendanceUnrecognizedLogListener() {

        @Override
        public void onAttendanceUnrecognizedLog(RecordsReplyModel model) {
            LOG.D(TAG, "onAttendanceUnrecognizedLog model = " + model);


            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {


                } else {
                    //Server fail , add to DB
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);

                }
            } else {
                //Fail
                EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
            }


        }
    };

    public static IAccessUnrecognizedLogListener accessUnrecognizedLogListener = new IAccessUnrecognizedLogListener() {
        @Override
        public void onAccessUnrecognizedLog(RecordsReplyModel model) {
            LOG.D(TAG, "onAccessUnrecognizedLog model = " + model);


            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {


                } else {
                    //Server fail , add to DB
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);

                }
            } else {
                //Error save to DB
                LOG.D(TAG, "onAccessUnrecognizedLog model == null");
                EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
            }
        }
    };

    public static IAccessVisitorUnrecognizedLogListener accessVisitorUnrecognizedLogListener = new IAccessVisitorUnrecognizedLogListener() {
        @Override
        public void onAccessVisitorUnrecognizedLog(RecordsReplyModel model) {
            LOG.D(TAG, "onAccessVisitorUnrecognizedLog model = " + model);

            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {


                } else {
                    //Server fail , add to DB
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_VISITORS);

                }
            } else {
                //Error save to DB
                LOG.D(TAG, "onAccessUnrecognizedLog model == null");
                EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_VISITORS);
            }
        }
    };

    public static IVisitorsUnrecognizedLogListener visitorsUnrecognizedLogListener = new IVisitorsUnrecognizedLogListener() {
        @Override
        public void onVisitorsUnrecognizedLog(RecordsReplyModel model) {
            LOG.D(TAG, "onVisitorsUnrecognizedLog model = " + model);


            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {


                } else {
                    //Server fail , add to DB
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_VISITORS);

                }
            } else {
                //Error save to DB
                LOG.D(TAG, "onVisitorsUnrecognizedLog model == null");

                EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_VISITORS);
            }
        }
    };


    public static IGetDeviceVideosListener getDeviceVideosListener = new IGetDeviceVideosListener() {
        @Override
        public void onGetDeviceVideos(GetVideoModel model) {
            LOG.D(TAG, "onGetDeviceVideos model = " + model);

            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    LOG.D(TAG, "onGetDeviceVideos model.getVideoData().getVideos().size() = " + model.getVideoData().getVideos().size());


                    if (model.getIsFromWebSocket() == true) {
                        File dir = new File(EnterpriseUtils.SD_CARD_APP_CONTENT);
                        if (dir.isDirectory()) {
                            String[] children = dir.list();
                            LOG.D(TAG, "children.length = " + children.length);
                            for (int i = 0; i < children.length; i++) {
                                new File(dir, children[i]).delete();
                            }
                        }
                    }

                    DeviceUtils.mCheckIsDownloadingVideo = new boolean[model.getVideoData().getVideos().size()];
                    DeviceUtils.mVideoList = new ArrayList<>();

                    for (int i = 0; i < model.getVideoData().getVideos().size(); i++) {
                        DeviceUtils.mCheckIsDownloadingVideo[i] = false;
                        LOG.D(TAG, "onGetDeviceVideos model.getVideoData().getVideos().get(i).getFileSize() = " + model.getVideoData().getVideos().get(i).getFileSize());
                        LOG.D(TAG, "onGetDeviceVideos model.getVideoData().getVideos().get(i).getLength() = " + model.getVideoData().getVideos().get(i).getLength());
                        LOG.D(TAG, "onGetDeviceVideos model.getVideoData().getVideos().get(i).getPriority() = " + model.getVideoData().getVideos().get(i).getPriority());
                        LOG.D(TAG, "onGetDeviceVideos model.getVideoData().getVideos().get(i).getThumbUrl() = " + model.getVideoData().getVideos().get(i).getThumbUrl());
                        LOG.D(TAG, "onGetDeviceVideos model.getVideoData().getVideos().get(i).getUrl() = " + model.getVideoData().getVideos().get(i).getUrl());


                        String[] fileNameArray = model.getVideoData().getVideos().get(i).getUrl().split("/");
                        LOG.D(TAG, "fileNameArray.length = " + fileNameArray.length);
                        for (int j = 0; j < fileNameArray.length; j++) {
                            LOG.D(TAG, "fileNameArray[j] =  " + fileNameArray[j]);
                        }

                        final String fileName = fileNameArray[fileNameArray.length - 1];
                        final String fileUrl = model.getVideoData().getVideos().get(i).getUrl();

                        LOG.D(TAG, "fileNameArray.length = " + fileNameArray.length);
                        LOG.D(TAG, "fileName = " + fileName);
                        LOG.D(TAG, "fileUrl = " + fileUrl);

                        PlayVideoModel p = new PlayVideoModel();
                        p.setFilename(fileName);
                        p.setPriority(Integer.valueOf(model.getVideoData().getVideos().get(i).getPriority()));
                        DeviceUtils.mVideoList.add(p);

                        String ftpIp = mSharedPreference.getString(Constants.PREF_KEY_FTP_IP, null);
                        String[] splitFtpIpArray = model.getVideoData().getVideos().get(i).getUrl().split(ftpIp);


                        if (splitFtpIpArray != null && splitFtpIpArray.length > 1) {
                            LOG.D(TAG, "splitFtpIpArray.length = " + splitFtpIpArray.length);
                            for (int j = 0; j < splitFtpIpArray.length; j++) {
                                LOG.D(TAG, "splitFtpIpArray[j] =  " + splitFtpIpArray[j]);
                            }

                            final String filePath = splitFtpIpArray[1];
                            final int videoIndex = i;

                            LOG.D(TAG, "fileNameArray.length = " + fileNameArray.length);
                            LOG.D(TAG, "filePath = " + filePath);


                            String tempFilePath = EnterpriseUtils.SD_CARD_APP_CONTENT + "/" + fileName;

                            File tempFile = new File(tempFilePath);
                            LOG.D(TAG, "tempFile.exists() = " + tempFile.exists());
                            //check if file exist
                            if (tempFile.exists()) {

                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DeviceUtils.mCheckIsDownloadingVideo[videoIndex] = true;
//                                    TSAAttendanceUtils.downloadFtpByURLConnection(fileUrl, TSAAttendanceUtils.mFtpAccount, TSAAttendanceUtils.mFtpPassword, fileName);
                                        EnterpriseUtils.downloadFtpByURLConnection(filePath, DeviceUtils.mFtpAccount, DeviceUtils.mFtpPassword, fileName, videoIndex);
                                    }
                                }).start();
                            }
                        }
                    }
                    Collections.sort(DeviceUtils.mVideoList, new Comparator<PlayVideoModel>() {
                        @Override
                        public int compare(PlayVideoModel v1, PlayVideoModel v2) {
                            return v1.getPriority() - v2.getPriority();
                        }
                    });

                } else {

                }
            } else {

            }
        }
    };

    public static IGetDeviceEmployeesListener getDeviceEmployeesListener = new IGetDeviceEmployeesListener() {
        @Override
        public void onGetDeviceEmployees(final GetEmployeeModel model) {
            LOG.D(TAG, "onGetDeviceEmployees model  = " + model);

            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    if (DeviceUtils.mEmployeeModel != null) {
                        DeviceUtils.mEmployeeModel.setEmployeeData(model.getEmployeeData());

                        //add Data to DB, use thread
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                EnterpriseUtils.checkEmployeeAcceptancesDb(model.getEmployeeData().getAcceptances(), mContext);
                            }
                        }).start();

                    }

//                    mCallback.sendEmptyMessage(Constants.CLOSE_MESSAGE_DIALOG);
                } else {
                    //  use DB Data
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            EnterpriseUtils.addEmployeesAcceptancesDbToModel(mContext);
                        }
                    }).start();


                }

            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EnterpriseUtils.addEmployeesAcceptancesDbToModel(mContext);
                    }
                }).start();
            }
        }
    };

    public static IGetDeviceVisitorsListener getDeviceVisitorsListener = new IGetDeviceVisitorsListener() {
        @Override
        public void onGetDeviceVisitors(final GetVisitorModel model) {
            LOG.D(TAG, "onGetDeviceVisitors model = " + model);
            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    if (DeviceUtils.mVisitorModel != null) {
                        DeviceUtils.mVisitorModel.setVisitorData(model.getVisitorData());

                        //add Data to DB
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                EnterpriseUtils.checkVisitorsAcceptancesDb(model.getVisitorData().getAcceptances(), mContext);
                            }
                        }).start();

                    }

//                    mCallback.sendEmptyMessage(Constants.CLOSE_MESSAGE_DIALOG);
                } else {
                    //  use DB Data
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            EnterpriseUtils.addVisitorsAcceptancesDbToModel(mContext);
                        }
                    }).start();

                }


            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EnterpriseUtils.addVisitorsAcceptancesDbToModel(mContext);
                    }
                }).start();
            }
        }
    };

    public static IGetDeviceMarqueesListener getDeviceMarqueesListener = new IGetDeviceMarqueesListener() {
        @Override
        public void onGetDeviceMarquees(GetMarqueesModel model) {
            LOG.D(TAG, "onGetDeviceMarquees model = " + model);
            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    DeviceUtils.mMarqueeString = "";

                    for (int i = 0; i < model.getMarqueesDat().getMarquees().size(); i++) {
                        //set marquee
                        if (DeviceUtils.mLocale.equals(Constants.LOCALE_EN)) {
                            DeviceUtils.mMarqueeString += model.getMarqueesDat().getMarquees().get(i).getMarqueesText().getEnUs();
                        } else if (DeviceUtils.mLocale.equals(Constants.LOCALE_TW)) {
                            DeviceUtils.mMarqueeString += model.getMarqueesDat().getMarquees().get(i).getMarqueesText().getZhTw();
                        } else if (DeviceUtils.mLocale.equals(Constants.LOCALE_CN)) {
                            DeviceUtils.mMarqueeString += model.getMarqueesDat().getMarquees().get(i).getMarqueesText().getZhCn();
                        } else {
                            DeviceUtils.mMarqueeString += model.getMarqueesDat().getMarquees().get(i).getMarqueesText().getEnUs();
                        }
                    }

                    mCallback.sendEmptyMessage(Constants.MSG_UPDATE_MARQUEE);

                } else {

                }


            } else {

            }
        }
    };

    public static OnEnrollListener onEnrollListener = new OnEnrollListener() {
        @Override
        public boolean onEnroll(Activity activity, boolean isEnrollSuccess, String imageFormat, final ArrayList<byte[]> arrayList) {
            LOG.D(TAG, "onEnroll isEnrollSuccess = " + isEnrollSuccess);
            LOG.D(TAG, "onEnroll imageFormat = " + imageFormat);
            LOG.D(TAG, "onEnroll arrayList = " + arrayList);

            if (isEnrollSuccess == false) {
                return false;
            } else {

                if (arrayList != null) {
                    LOG.D(TAG, "onEnroll arrayList.size() = " + arrayList.size());

                    mModel = EnrollmentManager.getInstance(mContext).trainModel(arrayList);
                    LOG.D(TAG, "onEnroll mModel = " + mModel);
                    isModelDone = true;

                    if (mModel != null) {
                        ClockUtils.mRegisterBean.setModel(mModel);
                        ClockUtils.mRegisterBean.setDataInBase64(arrayList.get(0));
                    } else {
                        //return train fail. try again
                        Message message = new Message();
                        Bundle bundle = new Bundle();

                        bundle.putString(Constants.KEY_REGISTER_MESSAGE, mContext.getString(R.string.register_success));
                        bundle.putString(Constants.KEY_REGISTER_ID_TITLE, null);
                        bundle.putString(Constants.KEY_REGISTER_ID, null);
                        bundle.putString(Constants.KEY_REGISTER_NAME_TITLE, null);
                        bundle.putString(Constants.KEY_REGISTER_NAME, null);

                        message.setData(bundle);
                        message.what = Constants.MSG_SHOW_REGISTER_DIALOG;

                        mCallback.sendMessage(message);

                        if (isEnrollSuccess) {
                            return false;
                        } else {
                            activity.finish();
                            return true;
                        }

                    }

                    //update local acceptance and identity DB, but server need to record
                    if (ClockUtils.mRegisterBean.getIsSearchUserSuccess()) {
                        //Done, update acceptance and identity DB

                        LOG.D(TAG, "onEnroll ClockUtils.mRegisterBean.getEmployeeId() = " + ClockUtils.mRegisterBean.getEmployeeId());
                        LOG.D(TAG, "onEnroll ClockUtils.mRegisterBean.getMobileNo() = " + ClockUtils.mRegisterBean.getMobileNo());
                        LOG.D(TAG, "onEnroll ClockUtils.mRegisterBean.getCreateTime() = " + ClockUtils.mRegisterBean.getCreateTime());
                        LOG.D(TAG, "onEnroll ClockUtils.mRegisterBean.getDepartment() = " + ClockUtils.mRegisterBean.getDepartment());
                        LOG.D(TAG, "onEnroll ClockUtils.mRegisterBean.getDeviceToken() = " + ClockUtils.mRegisterBean.getDeviceToken());
                        LOG.D(TAG, "onEnroll ClockUtils.mRegisterBean.getModelId() = " + ClockUtils.mRegisterBean.getModelId());
                        LOG.D(TAG, "onEnroll ClockUtils.mRegisterBean.getFormat() = " + ClockUtils.mRegisterBean.getFormat());
                        LOG.D(TAG, "onEnroll ClockUtils.mRegisterBean.getName() = " + ClockUtils.mRegisterBean.getName());
                        LOG.D(TAG, "onEnroll ClockUtils.mRegisterBean.getPassword() = " + ClockUtils.mRegisterBean.getPassword());
                        LOG.D(TAG, "onEnroll ClockUtils.mRegisterBean.getTitle() = " + ClockUtils.mRegisterBean.getTitle());
                        LOG.D(TAG, "onEnroll ClockUtils.mRegisterBean.getDataInBase64() = " + ClockUtils.mRegisterBean.getDataInBase64());
                        LOG.D(TAG, "onEnroll ClockUtils.mRegisterBean.getModel() = " + ClockUtils.mRegisterBean.getModel());
                        LOG.D(TAG, "onEnroll ClockUtils.mRegisterBean.getRegisterType()  = " + ClockUtils.mRegisterBean.getRegisterType());
                        LOG.D(TAG, "onEnroll ClockUtils.mRegisterBean.getSecurityCode()  = " + ClockUtils.mRegisterBean.getSecurityCode());

                        if (ClockUtils.mRegisterBean.getRegisterType() == Constants.REGISTER_TYPE_EMPLOYEE) {

                            //check if security code repeat

                            //if can clock in this device
                            EmployeeModel employeeModel = DeviceUtils.mEmployeeModel;

                            boolean isLoginAccountCanClock = false;

                            LOG.D(TAG, "DeviceUtils.mEmployeeModel = " + DeviceUtils.mEmployeeModel);

                            if (employeeModel.getEmployeeData() != null) {
                                for (int i = 0; i < employeeModel.getEmployeeData().getAcceptances().size(); i++) {
                                    LOG.D(TAG, "onEnroll employeeModel.getEmployeeData().getAcceptances().get(i).getSecurityCode() = " +
                                            employeeModel.getEmployeeData().getAcceptances().get(i).getSecurityCode());
                                    LOG.D(TAG, "onEnroll employeeModel.getEmployeeData().getAcceptances().get(i).getRfid() = " +
                                            employeeModel.getEmployeeData().getAcceptances().get(i).getRfid());
                                    if (ClockUtils.mRegisterBean.getSecurityCode().equals(employeeModel.getEmployeeData().getAcceptances().get(i).getSecurityCode())) {
                                        isLoginAccountCanClock = true;

                                        //SAVE RFID DATA
                                        ClockUtils.mRegisterBean.setRfid(employeeModel.getEmployeeData().getAcceptances().get(i).getRfid());
                                        break;
                                    }
                                }

                            } else {
                                //do nothing
                            }

                            LOG.D(TAG, "onEnroll employeeModel isLoginAccountCanClock = " + isLoginAccountCanClock);
                            if (isLoginAccountCanClock) {
                                EnterpriseUtils.addRegisterToEmployeeModel(mContext, ClockUtils.mRegisterBean);
                                EnterpriseUtils.addRegisterToEmployeeAcceptanceDb(mContext, ClockUtils.mRegisterBean);
                                EnterpriseUtils.addRegisterToEmployeeIdentityDb(mContext, ClockUtils.mRegisterBean);
                                IdentifyEmployeeManager.getInstance(mContext).addModelFromDb();
                            } else {

                            }

//                            EnterpriseUtils.addRegisterToEmployeeRegisterDb(mContext, ClockUtils.mRegisterBean);

                        } else if (ClockUtils.mRegisterBean.getRegisterType() == Constants.REGISTER_TYPE_VISITOR) {


                            boolean isLoginAccountCanClock = false;
                            VisitorModel visitorModel = DeviceUtils.mVisitorModel;

                            if (visitorModel != null) {
                                if (visitorModel.getVisitorData() != null) {
                                    for (int i = 0; i < visitorModel.getVisitorData().getAcceptances().size(); i++) {
                                        LOG.D(TAG, "onEnroll ClockUtils.mLoginAccount = " + ClockUtils.mLoginAccount);
//                                    if(ClockUtils.mLoginAccount.equals(visitorModel.getVisitorData().getAcceptances().get(i).getSecurityCode())){
//                                        isLoginAccountCanClock = true;
//
//                                        break;
//                                    }

                                        if (ClockUtils.mRegisterBean.getSecurityCode().equals(
                                                visitorModel.getVisitorData().getAcceptances().get(i).getSecurityCode())) {
                                            isLoginAccountCanClock = true;

                                            break;
                                        }
                                    }
                                } else {

                                }
                            }

                            LOG.D(TAG, "onEnroll visitorModel isLoginAccountCanClock = " + isLoginAccountCanClock);
                            if (isLoginAccountCanClock) {
                                EnterpriseUtils.addRegisterToVisitorModel(mContext, ClockUtils.mRegisterBean);
                                EnterpriseUtils.addRegisterToVisitorAcceptanceDb(mContext, ClockUtils.mRegisterBean);
                                EnterpriseUtils.addRegisterToVisitorIdentityDb(mContext, ClockUtils.mRegisterBean);
                                IdentifyVisitorManager.getInstance(mContext).addModelFromDb();
                            }


//                            EnterpriseUtils.addRegisterToVisitorRegisterDb(mContext, ClockUtils.mRegisterBean);
//                IdentifyVisitorManager.getInstance(mContext).addSingleModel(ClockUtils.mRegisterBean.getModelId(), ClockUtils.mRegisterBean.getModel());

                        }

                        return false;

                    } else {
                        //never go here
                        return false;
                    }


                } else {
                    //return train fail. try again, Enroll no data
                    Message message = new Message();
                    Bundle bundle = new Bundle();

                    bundle.putString(Constants.KEY_REGISTER_MESSAGE, mContext.getString(R.string.register_fail_again));
                    bundle.putString(Constants.KEY_REGISTER_ID_TITLE, null);
                    bundle.putString(Constants.KEY_REGISTER_ID, null);
                    bundle.putString(Constants.KEY_REGISTER_NAME_TITLE, null);
                    bundle.putString(Constants.KEY_REGISTER_NAME, null);

                    message.setData(bundle);
                    message.what = Constants.MSG_SHOW_REGISTER_DIALOG;

                    mCallback.sendMessage(message);

                    if (isEnrollSuccess) {
                        return false;
                    } else {
                        activity.finish();
                        return true;
                    }

                }
            }

        }
    };


    public static OnVerifyListener onVerifyListener = new OnVerifyListener() {
        @Override
        public boolean onVerify(Activity activity, boolean b, String s, byte[] bytes) {
            return false;
        }
    };


}
