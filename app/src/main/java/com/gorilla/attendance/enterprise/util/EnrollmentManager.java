package com.gorilla.attendance.enterprise.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.gorilla.attendance.enterprise.database.DatabaseAdapter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import gorilla.fdr.Enrollment;
import gorilla.fdr.Type;
import gorilla.iod.IntelligentObjectDetector;

/**
 * Created by ggshao on 2017/5/4.
 */

public class EnrollmentManager {
    private static final String TAG = "EnrollmentManager";

    private static EnrollmentManager mInstance = null;
    private Enrollment mEnrollment = null;

    private Context mContext = null;
    private DatabaseAdapter mDatabaseAdapter = null;

    public static EnrollmentManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new EnrollmentManager(context);
        }
        return mInstance;
    }

    public EnrollmentManager(Context context){
        mContext = context;
        mInstance = this;
        mDatabaseAdapter = DatabaseAdapter.getInstance(context);

    }

    public byte[] trainModel(ArrayList<byte[]> photoList){
        if(mEnrollment == null){
            String libPath = mContext.getFilesDir()+"/Bin";
            mEnrollment = new Enrollment(libPath);
//            mEnrollment = new Enrollment(null);
        }else{
            mEnrollment.free();
//            mEnrollment = new Enrollment(null);
            String libPath = mContext.getFilesDir()+"/Bin";
            mEnrollment = new Enrollment(libPath);
        }


        Type.ONEFACE[] faceList = new Type.ONEFACE[photoList.size()];

        getFaceList(photoList, faceList); //last used function
        LOG.D(TAG,"trainModel mEnrollment.getModelSize() = " + mEnrollment.getModelSize());

        byte[] model = new byte[mEnrollment.getModelSize()];
        int trainResult = mEnrollment.train(faceList,model);

        LOG.D(TAG,"trainModel trainResult = " + trainResult);
        if(trainResult > 0){
            return model;
        }else{
            return null;
        }


//        IdentifyEmployeeManager.getInstance(mContext).addSingleModel(7779, model);




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
