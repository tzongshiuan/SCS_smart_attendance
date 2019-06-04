package com.gorilla.attendance.enterprise.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Base64;

import com.gorilla.attendance.enterprise.database.DatabaseAdapter;
import com.gorilla.attendance.enterprise.database.bean.VerifiedFaceBean;
import com.gorilla.attendance.enterprise.datamodel.EmployeeModel;
import com.gorilla.attendance.enterprise.datamodel.IdentitiesModel;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import gorilla.fdr.Identify;
import gorilla.fdr.Type;
import gorilla.iod.IntelligentObjectDetector;

/**
 * Created by ggshao on 2017/2/12.
 */

public class IdentifyEmployeeManager {
    private static final String TAG = "IdentifyEmployeeManager";

    private static IdentifyEmployeeManager mInstance = null;
    private Identify mIdentify = null;

    private Context mContext = null;
    private DatabaseAdapter mDatabaseAdapter = null;

    private SharedPreferences mSharedPreference = null;
//    private Thread mSearchThread = null;

    public static IdentifyEmployeeManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new IdentifyEmployeeManager(context);
        }
        return mInstance;
    }

    public IdentifyEmployeeManager(Context context){
        mContext = context;
        mInstance = this;
        mDatabaseAdapter = DatabaseAdapter.getInstance(context);
        mSharedPreference = mContext.getSharedPreferences(Constants.PREF_NAME_ATTENDANCE_ENTERPRISE, Context.MODE_PRIVATE);

    }


    public void addModelFromDb(){
        LOG.D(TAG,"addModelFromDb mIdentify = " + mIdentify);
//        mDatabaseAdapter.open();
        ArrayList<VerifiedFaceBean> employeeVerifiedFaceBeanArrayList = mDatabaseAdapter.getAllEmployeeIdentitiesArrayList();


        if(employeeVerifiedFaceBeanArrayList == null){
            return;
        }

        if(mIdentify == null){
//            mIdentify = new Identify(null);
            String libPath = mContext.getFilesDir()+"/Bin";
            mIdentify = new Identify(libPath);

        }else{

        }

        for(int i = 0 ; i < employeeVerifiedFaceBeanArrayList.size(); i++){

            int addModelReturn = mIdentify.addModel(Integer.parseInt(employeeVerifiedFaceBeanArrayList.get(i).getBapModelId()),
                    employeeVerifiedFaceBeanArrayList.get(i).getModel());
//                int addModelReturn = mIdentify.addModel(Integer.parseInt(identities.get(i).getIntId()), imageData);
            LOG.D(TAG,"addModelFromDb addModelReturn = " + addModelReturn);
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

            LOG.D(TAG,"addModel doInBackground Thread.currentThread().getId() = " + Thread.currentThread().getId());

            //add identify model
            for(int i = 0 ; i < identities.size(); i++){
                LOG.D(TAG,"model.getData().get(i).getModel() = " + identities.get(i).getModel());
                byte[] imageData = Base64.decode(identities.get(i).getModel(), Base64.DEFAULT);

                LOG.D(TAG,"imageData   =  " + imageData);
                LOG.D(TAG,"imageData.length = " + imageData.length);

                LOG.D(TAG,"identities.get(i).getBapModelId() = " + identities.get(i).getBapModelId());
                if(identities.get(i).getBapModelId() == null){
                    identities.get(i).setBapModelId("9999999");//Temp Value
                }

                //  Temp intId base64 to byte
                int addModelReturn = mIdentify.addModel(Integer.parseInt(identities.get(i).getBapModelId()), imageData);
//                int addModelReturn = mIdentify.addModel(Integer.parseInt(identities.get(i).getIntId()), imageData);
                LOG.D(TAG,"addModelReturn = " + addModelReturn);

            }

            //check verified face DB
//            EnterpriseUtils.checkEmployeeIdentitiesDb(identities, mContext);
            EnterpriseUtils.checkAllEmployeeIdentitiesDb(identities, mContext);
            return 1L;
        }

        protected void onProgressUpdate(Integer... progress) {
            LOG.D(TAG,"addModel onProgressUpdate Thread.currentThread().getId() = " + Thread.currentThread().getId());
        }

        protected void onPostExecute(Long result) {
            LOG.D(TAG,"addModel onPostExecute Thread.currentThread().getId() = " + Thread.currentThread().getId());
        }
    }

    public void addModel(final ArrayList<IdentitiesModel> identities){
        LOG.D(TAG,"addModel mIdentify = " + mIdentify);
        if(mIdentify == null){
//            newIdentityTask.execute(identities, libPath);

        }else{
            //check add or delete list
            mIdentify.free();
            mIdentify = null;
//            mIdentify = new Identify(null);
        }

        String libPath = mContext.getFilesDir()+"/Bin";

        NewIdentityTask newIdentityTask = new NewIdentityTask();
        newIdentityTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, identities, libPath);

    }

    public void search(final Handler callback){
        //can use thread to do

        Type.ONEFACE[] faceList = new Type.ONEFACE[ EnterpriseUtils.mFacePngList.size()];

        getFaceList(EnterpriseUtils.mFacePngList, faceList);

        Identify.CANDIDATE[] candidateList = new Identify.CANDIDATE[EnterpriseUtils.VERIFIED_CANDIDATE_NUMBER];
        for (int i =0; i < EnterpriseUtils.VERIFIED_CANDIDATE_NUMBER; i++ ){
            candidateList[i] = new Identify.CANDIDATE();
        }

        boolean[] decision = new boolean[1];

        LOG.D(TAG,"search mIdentify = " + mIdentify);

//        if(mIdentify == null){
//            addModelFromDb();
//        }

        if(mIdentify == null){
            callback.sendEmptyMessage(Constants.OFFLINE_FACE_VERIFY_FAIL);
            return;
        }

        LOG.D(TAG, "ClockUtils.mLoginIntId = " + ClockUtils.mLoginIntId);
        if(ClockUtils.mLoginIntId == -1){

            EmployeeModel employeeModel = ((EmployeeModel) ClockUtils.mRoleModel);
            int[] candidateIntId = new int[employeeModel.getEmployeeData().getAcceptances().size()];
            if(employeeModel.getEmployeeData() != null) {
                for (int i = 0; i < employeeModel.getEmployeeData().getAcceptances().size(); i++) {
                    LOG.D(TAG, "employeeModel.getEmployeeData().getAcceptances().get(i).getIntId() = "
                            + employeeModel.getEmployeeData().getAcceptances().get(i).getIntId());
                    candidateIntId[i] = employeeModel.getEmployeeData().getAcceptances().get(i).getIntId();
                }
            }
            mIdentify.identifyActiveID(candidateIntId.length, candidateIntId);
            mIdentify.search(faceList, candidateList, decision);
        }else{
            int[] candidateIntId = new int[1];
            candidateIntId[0] = ClockUtils.mLoginIntId;
            mIdentify.identifyActiveID(1, candidateIntId);
            mIdentify.search(faceList, ClockUtils.mLoginIntId, candidateList, decision);
        }



        //Test Search
//        int[] testID = new int[1];
//        testID[0] = ClockUtils.mLoginIntId;
//        mIdentify.identifyActiveID(1, testID);
//        mIdentify.search(faceList, ClockUtils.mLoginIntId, candidateList, decision);


        LOG.D(TAG,"candidateList = " + candidateList);
        LOG.D(TAG,"candidateList.length = " + candidateList.length);
        LOG.D(TAG,"candidateList[0].valid = " + candidateList[0].valid);
        LOG.D(TAG,"candidateList[0].template_id  = " + candidateList[0].template_id);
        LOG.D(TAG,"candidateList[0].similiarity_score   = " + candidateList[0].similiarity_score);
        LOG.D(TAG,"decision[0] =  " + decision[0]);

        if(decision[0] == true){
            //get name from db
//            mDatabaseAdapter.open();
            VerifiedFaceBean verifiedFaceBean = mDatabaseAdapter.getEmployeeIdentitiesByBapModelId(String.valueOf(candidateList[0].template_id));
            LOG.D(TAG, "verifiedFaceBean  = " + verifiedFaceBean);

            if(verifiedFaceBean != null){
                //verify success

                LOG.D(TAG,"ClockUtils.mLoginUuid   = " + ClockUtils.mLoginUuid);
                LOG.D(TAG,"ClockUtils.mLoginAccount   = " + ClockUtils.mLoginAccount);
                LOG.D(TAG,"verifiedFaceBean.getId()  = " + verifiedFaceBean.getId());
                LOG.D(TAG,"verifiedFaceBean.getSecurityCode()  = " + verifiedFaceBean.getSecurityCode());

                //check uuid if the same, if null, means face to find face
                if(ClockUtils.mLoginUuid != null){
                    if(!ClockUtils.mLoginUuid.equals(verifiedFaceBean.getId())){

                        //setSecurityCode only use for register people
                        if(verifiedFaceBean.getSecurityCode() == null){

                            callback.sendEmptyMessage(Constants.OFFLINE_FACE_VERIFY_FAIL);
                            return;
                        }

                        if(ClockUtils.mLoginAccount.equals(verifiedFaceBean.getSecurityCode())){

                        }else{
                            LOG.D(TAG,"search OFFLINE_FACE_VERIFY_FAIL");

                                callback.sendEmptyMessage(Constants.OFFLINE_FACE_VERIFY_FAIL);

                            return;
                        }
//                        return;
                    }
                }else{

                    //maybe offline face, so uuId is null
                    if(ClockUtils.mLoginAccount != null){
                        if(ClockUtils.mLoginAccount.equals(verifiedFaceBean.getSecurityCode())){

                        }else{
                            callback.sendEmptyMessage(Constants.OFFLINE_FACE_VERIFY_FAIL);
                            return;
                        }
                    }
                }

                ClockUtils.mLoginName = verifiedFaceBean.getEmployeeName();
                ClockUtils.mLoginAccount = verifiedFaceBean.getEmployeeId();
                ClockUtils.mLoginUuid = verifiedFaceBean.getId();
                ClockUtils.mLoginVerifyStatus = "SUCCEED";
                callback.sendEmptyMessage(Constants.OFFLINE_FACE_VERIFY_SUCCESS);
            }else{
                //never here
                callback.sendEmptyMessage(Constants.OFFLINE_FACE_VERIFY_FAIL);
            }
        }else{
            //verify fail

            callback.sendEmptyMessage(Constants.OFFLINE_FACE_VERIFY_FAIL);

        }

    }

    private void getFaceList(List<byte[]> pngList, Type.ONEFACE[] faceList) {

        //File root = Environment.getExternalStorageDirectory();
        //File file = new File(root, "/Download/Module/NobelHsu_0040.jpg");
        //Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        for( int i=0; i< pngList.size(); i++){

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
            faceList[i].image_nchannel = myBitmap.getRowBytes() / myBitmap.getWidth() ;
            faceList[i].roi_x = 0;
            faceList[i].roi_y = 0;
            faceList[i].roi_width = myBitmap.getWidth();
            faceList[i].roi_height = myBitmap.getHeight();
            faceList[i].image_data = byteArray;
        }
    }


}
