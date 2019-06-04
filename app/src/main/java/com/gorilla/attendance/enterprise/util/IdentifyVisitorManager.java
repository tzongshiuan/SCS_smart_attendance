package com.gorilla.attendance.enterprise.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.WorkerThread;
import android.util.Base64;

import com.gorilla.attendance.enterprise.database.DatabaseAdapter;
import com.gorilla.attendance.enterprise.database.bean.VerifiedFaceBean;
import com.gorilla.attendance.enterprise.datamodel.IdentitiesModel;
import com.gorilla.attendance.enterprise.datamodel.VisitorModel;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import gorilla.fdr.Identify;
import gorilla.fdr.Type;
import gorilla.iod.IntelligentObjectDetector;

import static com.gorilla.attendance.enterprise.util.ClockUtils.mLoginAccount;

/**
 * Created by ggshao on 2017/2/17.
 */

public class IdentifyVisitorManager {
    private static final String TAG = "IdentifyVisitorManager";

    private static IdentifyVisitorManager mInstance = null;
    private Identify mIdentify = null;

    private Context mContext = null;
    private DatabaseAdapter mDatabaseAdapter = null;

    private SharedPreferences mSharedPreference = null;

    public static IdentifyVisitorManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new IdentifyVisitorManager(context);
        }
        return mInstance;
    }

    public IdentifyVisitorManager(Context context) {
        mContext = context;
        mInstance = this;
        mDatabaseAdapter = DatabaseAdapter.getInstance(context);
        mSharedPreference = mContext.getSharedPreferences(Constants.PREF_NAME_ATTENDANCE_ENTERPRISE, Context.MODE_PRIVATE);

    }

    @WorkerThread
    public void addModelFromDb() {
        LOG.D(TAG, "addModelFromDb mIdentify = " + mIdentify);
//        mDatabaseAdapter.open();
        ArrayList<VerifiedFaceBean> visitorVerifiedFaceBeanArrayList = mDatabaseAdapter.getAllVisitorIdentitiesArrayList();
        if (visitorVerifiedFaceBeanArrayList == null) {
            return;
        }

        if (mIdentify == null) {
//            mIdentify = new Identify(null);
            String libPath = mContext.getFilesDir() + "/Bin";
            mIdentify = new Identify(libPath);


        } else {
//            mIdentify.free();
//            mIdentify = null;
////            mIdentify = new Identify(null);
//            String libPath = mContext.getFilesDir()+"/Bin";
//            mIdentify = new Identify(libPath);

        }


        for (int i = 0; i < visitorVerifiedFaceBeanArrayList.size(); i++) {

//            byte[] imageData = Base64.decode(visitorVerifiedFaceBeanArrayList.get(i).getModel(), Base64.DEFAULT);
            int addModelReturn = mIdentify.addModel(Integer.parseInt(visitorVerifiedFaceBeanArrayList.get(i).getBapModelId()),
                    visitorVerifiedFaceBeanArrayList.get(i).getModel());
//                int addModelReturn = mIdentify.addModel(Integer.parseInt(identities.get(i).getIntId()), imageData);
            LOG.D(TAG, "addModelFromDb addModelReturn = " + addModelReturn);
        }


//        mDatabaseAdapter.close();
    }

    private class NewIdentityTask extends AsyncTask<Object, Integer, Long> {
        protected Long doInBackground(Object... params) {
            ArrayList<IdentitiesModel> identities = (ArrayList<IdentitiesModel>) params[0];
            String libPath = (String) params[1];
            mIdentify = new Identify(libPath);
//            mIdentify = new Identify(null);
            //check DB need modify

            LOG.D(TAG, "addModel doInBackground Thread.currentThread().getId() = " + Thread.currentThread().getId());

            //add identify model
            for (int i = 0; i < identities.size(); i++) {
                LOG.D(TAG, "model.getData().get(i).getModel() = " + identities.get(i).getModel());
                byte[] imageData = Base64.decode(identities.get(i).getModel(), Base64.DEFAULT);

                LOG.D(TAG, "imageData   =  " + imageData);
                LOG.D(TAG, "imageData.length = " + imageData.length);

                //  Temp intId base64 to byte
                LOG.D(TAG, "identities.get(i).getBapModelId() = " + identities.get(i).getBapModelId());
                if (identities.get(i).getBapModelId() == null) {
                    identities.get(i).setBapModelId("9999");
                }

                int addModelReturn = mIdentify.addModel(Integer.parseInt(identities.get(i).getBapModelId()), imageData);
//                int addModelReturn = mIdentify.addModel(Integer.parseInt(identities.get(i).getIntId()), imageData);
                LOG.D(TAG, "addModelReturn = " + addModelReturn);

            }

            //check verified face DB
//            EnterpriseUtils.checkVisitorIdentitiesDb(identities, mContext);
            EnterpriseUtils.checkAllVisitorIdentitiesDb(identities, mContext);
            return 1L;
        }

        protected void onProgressUpdate(Integer... progress) {
            LOG.D(TAG, "addModel onProgressUpdate Thread.currentThread().getId() = " + Thread.currentThread().getId());
        }

        protected void onPostExecute(Long result) {
            LOG.D(TAG, "addModel onPostExecute Thread.currentThread().getId() = " + Thread.currentThread().getId());
        }
    }

    public void addModel(ArrayList<IdentitiesModel> identities) {
        LOG.D(TAG, "addModel mIdentify = " + mIdentify);

        if (mIdentify == null) {
//            mIdentify = new Identify(null);

        } else {
            //check add or delete list
            mIdentify.free();
            mIdentify = null;

        }
        String libPath = mContext.getFilesDir() + "/Bin";

        NewIdentityTask newIdentityTask = new NewIdentityTask();
        newIdentityTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, identities, libPath);

    }

    public void search(Handler callback) {
        LOG.D(TAG, "search mIdentify = " + mIdentify);
        Type.ONEFACE[] faceList = new Type.ONEFACE[EnterpriseUtils.mFacePngList.size()];

        getFaceList(EnterpriseUtils.mFacePngList, faceList);

        Identify.CANDIDATE[] candidateList = new Identify.CANDIDATE[EnterpriseUtils.VERIFIED_CANDIDATE_NUMBER];
        for (int i = 0; i < EnterpriseUtils.VERIFIED_CANDIDATE_NUMBER; i++) {
            candidateList[i] = new Identify.CANDIDATE();
        }

        boolean[] decision = new boolean[1];

//        mIdentify = null;

//        if(mIdentify == null){
//            addModelFromDb();
//        }
//
//        if(mIdentify == null){
//            callback.sendEmptyMessage(Constants.OFFLINE_FACE_VERIFY_FAIL);
//            return;
//        }

        if (mIdentify == null) {
            callback.sendEmptyMessage(Constants.OFFLINE_FACE_VERIFY_FAIL);
            return;
        }

        if (ClockUtils.mLoginIntId == -1) {
            VisitorModel visitorModel = ((VisitorModel) ClockUtils.mRoleModel);
            int[] candidateIntId = new int[visitorModel.getVisitorData().getAcceptances().size()];
            if (visitorModel.getVisitorData() != null) {
                for (int i = 0; i < visitorModel.getVisitorData().getAcceptances().size(); i++) {
                    LOG.D(TAG, "employeeModel.getEmployeeData().getAcceptances().get(i).getIntId() = "
                            + visitorModel.getVisitorData().getAcceptances().get(i).getIntId());
                    candidateIntId[i] = visitorModel.getVisitorData().getAcceptances().get(i).getIntId();
                }
            }

            mIdentify.identifyActiveID(candidateIntId.length, candidateIntId);
            mIdentify.search(faceList, candidateList, decision);

        } else {

            int[] candidateIntId = new int[1];
            candidateIntId[0] = ClockUtils.mLoginIntId;
            mIdentify.identifyActiveID(1, candidateIntId);
            mIdentify.search(faceList, ClockUtils.mLoginIntId, candidateList, decision);
        }


        //mIdentify.search(faceList, candidateList, decision);

        LOG.D(TAG, "candidateList = " + candidateList);
        LOG.D(TAG, "candidateList[0].valid = " + candidateList[0].valid);
        LOG.D(TAG, "candidateList[0].template_id  = " + candidateList[0].template_id);
        LOG.D(TAG, "candidateList[0].similiarity_score   = " + candidateList[0].similiarity_score);
        LOG.D(TAG, "decision[0] = " + decision[0]);

        float identifyThreshold = mSharedPreference.getFloat(Constants.PREF_KEY_IDENTIFY_THRESHOLD, (float) 0.6);

        if (decision[0] == true && candidateList[0].similiarity_score > identifyThreshold) {
            //get name from db
//            mDatabaseAdapter.open();
            VerifiedFaceBean verifiedFaceBean = mDatabaseAdapter.getVisitorIdentitiesByBapModelId(String.valueOf(candidateList[0].template_id));
            LOG.D(TAG, "verifiedFaceBean  = " + verifiedFaceBean);

            if (verifiedFaceBean != null) {
                //verify success

                LOG.D(TAG, "ClockUtils.mLoginUuid   = " + ClockUtils.mLoginUuid);
                LOG.D(TAG, "ClockUtils.mLoginAccount   = " + mLoginAccount);
                LOG.D(TAG, "verifiedFaceBean.getId()  = " + verifiedFaceBean.getId());
                LOG.D(TAG, "verifiedFaceBean.getSecurityCode()  = " + verifiedFaceBean.getSecurityCode());

                //check uuid if the same, if null, means face to find face
                if (ClockUtils.mLoginUuid != null) {

                    //ID 與 找到的不符合，  就
                    if (!ClockUtils.mLoginUuid.equals(verifiedFaceBean.getId())) {

                        //setSecurityCode only use for register people
                        if (verifiedFaceBean.getSecurityCode() == null) {
                            //check if 3 times

                            callback.sendEmptyMessage(Constants.OFFLINE_FACE_VERIFY_FAIL);
                            return;
                        }

                        if (mLoginAccount.equals(verifiedFaceBean.getSecurityCode())) {

                        } else {
                            callback.sendEmptyMessage(Constants.OFFLINE_FACE_VERIFY_FAIL);
                            return;
                        }
                    }
                } else {
                    //maybe offline face, so uuId is null
                    if (mLoginAccount != null) {
                        if (mLoginAccount.equals(verifiedFaceBean.getSecurityCode())) {

                        } else {
                            callback.sendEmptyMessage(Constants.OFFLINE_FACE_VERIFY_FAIL);
                            return;
                        }
                    }
                }

                ClockUtils.mLoginName = verifiedFaceBean.getVisitorName();
//                ClockUtils.mLoginAccount = verifiedFaceBean.getSecurityCode();
                ClockUtils.mLoginUuid = verifiedFaceBean.getId();
                ClockUtils.mLoginVerifyStatus = "SUCCEED";
                callback.sendEmptyMessage(Constants.OFFLINE_FACE_VERIFY_SUCCESS);

            } else {
                //verify fail

                //never here
                callback.sendEmptyMessage(Constants.OFFLINE_FACE_VERIFY_FAIL);
            }
//            mDatabaseAdapter.close();

        } else {
            //verify fail
            callback.sendEmptyMessage(Constants.OFFLINE_FACE_VERIFY_FAIL);

        }

    }


    private void getFaceList(List<byte[]> pngList, Type.ONEFACE[] faceList) {

        //File root = Environment.getExternalStorageDirectory();
        //File file = new File(root, "/Download/Module/NobelHsu_0040.jpg");
        //Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        for (int i = 0; i < pngList.size(); i++) {

            Bitmap myBitmap = BitmapFactory.decodeByteArray(pngList.get(i), 0, pngList.get(i).length);
            int size = myBitmap.getRowBytes() * myBitmap.getHeight();

            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            myBitmap.copyPixelsToBuffer(byteBuffer);
            byte[] byteArray = byteBuffer.array();
            IntelligentObjectDetector.convertRGBAtoBGRA(byteArray, myBitmap.getWidth(), myBitmap.getHeight(), myBitmap.getRowBytes());

            faceList[i] = new Type.ONEFACE();
            faceList[i].image_width = myBitmap.getWidth();
            faceList[i].image_height = myBitmap.getHeight();
            faceList[i].image_step = myBitmap.getRowBytes();
            faceList[i].image_nchannel = myBitmap.getRowBytes() / myBitmap.getWidth();
            faceList[i].roi_x = 0;
            faceList[i].roi_y = 0;
            faceList[i].roi_width = myBitmap.getWidth();
            faceList[i].roi_height = myBitmap.getHeight();
            faceList[i].image_data = byteArray;
        }
    }

}
