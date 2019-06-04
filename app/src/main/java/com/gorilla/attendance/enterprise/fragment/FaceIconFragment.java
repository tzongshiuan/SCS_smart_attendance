package com.gorilla.attendance.enterprise.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gorilla.attendance.enterprise.MainActivity;
import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.datamodel.AcceptancesModel;
import com.gorilla.attendance.enterprise.datamodel.EmployeeModel;
import com.gorilla.attendance.enterprise.datamodel.VisitorModel;
import com.gorilla.attendance.enterprise.util.ClockUtils;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.DeviceUtils;
import com.gorilla.attendance.enterprise.util.FDRControlManager;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.NetworkManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

/**
 * Created by ggshao on 2017/2/7.
 */

public class FaceIconFragment extends BaseFragment {
    public static final String TAG = "FaceIconFragment";

    private static final int PAGE_NUMBER = 21;
    private static final int ROLE_ROW_NUMBER = 3;

    private View mView = null;

    private MainActivity mMainActivity;
    private FragmentActivity mActivity = null;
    private Context mContext = null;

    private NetworkManager mNetworkManager = null;
    private Handler mActivityHandler = null;

    private RelativeLayout mLayoutFdr = null;
    private FrameLayout mFdrFrame = null;

    private ViewPager mViewPager;
    private LinearLayout mLayoutPhoto = null;

    private ArrayList<View> mList = new ArrayList<View>();
    private RolePageAdapter mAdapter;
    private ArrayList<RecyclerView> mRoleViewList = new ArrayList<RecyclerView>();

    private TextView mTxtTitle = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mMainActivity = (MainActivity) getActivity();
        mContext = getActivity();
        DeviceUtils.checkSettingLanguage(mContext);

        mActivityHandler = mMainActivity.getHandler();
        if(getFragmentManager().getBackStackEntryCount() > 0){
            mActivityHandler.removeMessages(Constants.LAUNCH_VIDEO);
            mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.PAGE_DELAYED_TIME);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LOG.V(TAG, "[onCreateView] ");

        if (mView == null) {
            mView = inflater.inflate(R.layout.face_icon_fragment, null);
        } else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
        }

//        initView(mView);

        initView();
        initData();

        return mView;
    }

    @Override
    public void onPause() {
        LOG.D(TAG,"onPause");
        super.onPause();

    }

    @Override
    public void onResume(){
        super.onResume();


        if(getFragmentManager().getBackStackEntryCount() > 0){
            mMainActivity.setHomeSettingWord(getString(R.string.txt_home_page));
            mMainActivity.setHomeSettingBackGround(R.mipmap.icon_back_to_home);
        }else{
            mMainActivity.setHomeSettingWord(getString(R.string.txt_home_setting));
            mMainActivity.setHomeSettingBackGround(R.mipmap.icon_back_to_home_setting);
        }

//        mAdapter.notifyDataSetChanged();


    }

    @Override
    public void onStop() {
        LOG.D(TAG,"onStop");
        super.onStop();

        mList.clear();
        mRoleViewList.clear();

    }

    @Override
    public void onDestroy() {
        LOG.D(TAG,"onDestroy");
        super.onDestroy();

        mFdrFrame.removeAllViews();

        FDRControlManager.getInstance(mContext).stopFdr(mActivityHandler, mFragmentHandler);


    }

    private void initView(){
        LOG.D(TAG, "initView");

        mLayoutFdr = (RelativeLayout) mView.findViewById(R.id.layout_fdr);
        mFdrFrame = (FrameLayout) mView.findViewById(R.id.fdr_frame);

        mTxtTitle = (TextView) mView.findViewById(R.id.txt_face_icon_title);

        mViewPager = (ViewPager)mView.findViewById(R.id.view_pager);
        mLayoutPhoto = (LinearLayout) mView.findViewById(R.id.layout_photo);



    }

    public void backToHome(){
        if(mFdrFrame != null){
            mFdrFrame.removeAllViews();
        }

        FDRControlManager.getInstance(mContext).stopFdr(mActivityHandler, mFragmentHandler);
        mFragmentHandler.removeMessages(Constants.GET_FACE_TOO_LONG);

        if(mLayoutFdr != null){
            mLayoutFdr.setVisibility(FrameLayout.GONE);
        }

        if(mLayoutPhoto != null){
            mLayoutPhoto.setVisibility(View.VISIBLE);
        }

        if(mTxtTitle != null){
            mTxtTitle.setVisibility(View.VISIBLE);
        }





    }


    private void initData(){
        LOG.D(TAG,"initData");
        LayoutInflater li = LayoutInflater.from(mContext);

        if(ClockUtils.mRoleModel instanceof EmployeeModel){

            if(DeviceUtils.mEmployeeModel.getEmployeeData() != null){
                int pageCount = ((DeviceUtils.mEmployeeModel.getEmployeeData().getAcceptances().size()) / PAGE_NUMBER) + 1;
                LOG.D(TAG,"initData pageCount  = " + pageCount);
                for(int i = 0 ; i < pageCount ; i++){
                    mList.add(li.inflate(R.layout.face_icon_viewpager_indicater, null));

                    int totalFaceNumber = -1;
                    if(i == (pageCount - 1)){
                        totalFaceNumber = DeviceUtils.mEmployeeModel.getEmployeeData().getAcceptances().size() % PAGE_NUMBER;
                    }else{
                        totalFaceNumber = PAGE_NUMBER;
                    }


                    RecyclerView recyclerView = (RecyclerView)mList.get(i).findViewById(R.id.recycler_view_face_icon);
                    RoleAdapter adapter = new RoleAdapter(i, totalFaceNumber);
                    recyclerView.setAdapter(adapter);
//            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                    recyclerView.setLayoutManager(new GridLayoutManager(mContext, ROLE_ROW_NUMBER, GridLayoutManager.HORIZONTAL, false));

                    mRoleViewList.add(recyclerView);

                }
            }else{

            }


        } else if(ClockUtils.mRoleModel instanceof VisitorModel){

            if(DeviceUtils.mVisitorModel.getVisitorData() != null){
                int pageCount = ((DeviceUtils.mVisitorModel.getVisitorData().getAcceptances().size()) / PAGE_NUMBER) + 1;
                LOG.D(TAG,"initData pageCount = " + pageCount);
                for(int i = 0 ; i < pageCount ; i++){
                    mList.add(li.inflate(R.layout.face_icon_viewpager_indicater, null));

                    int totalFaceNumber = -1;
                    if(i == (pageCount - 1)){
                        totalFaceNumber = DeviceUtils.mVisitorModel.getVisitorData().getAcceptances().size() % PAGE_NUMBER;
                    }else{
                        totalFaceNumber = PAGE_NUMBER;
                    }

                    RecyclerView recyclerView = (RecyclerView)mList.get(i).findViewById(R.id.recycler_view_face_icon);
                    RoleAdapter adapter = new RoleAdapter(i, totalFaceNumber);
                    recyclerView.setAdapter(adapter);
//            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                    recyclerView.setLayoutManager(new GridLayoutManager(mContext, ROLE_ROW_NUMBER, GridLayoutManager.HORIZONTAL, false));

                    mRoleViewList.add(recyclerView);
                }
            }else{

            }


        }


        mAdapter = new RolePageAdapter();
        mViewPager.setAdapter(mAdapter);

        CirclePageIndicator indicator = (CirclePageIndicator) mView.findViewById(R.id.view_pager_indicator);
        indicator.setViewPager(mViewPager);

//                mStudentInfoListData.getRecordListData()
        mAdapter.notifyDataSetChanged();

    }

    public void startFdr(){
        LOG.D(TAG, "startFdr");
        LOG.D(TAG, "startFdr mFdrFrame.getChildCount() = " + mFdrFrame.getChildCount());

//        Calendar calendar = Calendar.getInstance();
//        ClockUtils.mLoginTime = calendar.get(Calendar.SECOND);
        ClockUtils.mLoginTime = System.currentTimeMillis();

        mLayoutPhoto.setVisibility(View.GONE);
        mLayoutFdr.setVisibility(View.VISIBLE);
        mTxtTitle.setVisibility(View.GONE);

        mFdrFrame.removeAllViews();
        mFdrFrame.addView(FDRControlManager.getInstance(mContext).getFdrCtrl());
        FDRControlManager.getInstance(mContext).startFdr(mActivityHandler, mFragmentHandler);

    }


    public class RolePageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            LOG.D(TAG,"RolePageAdapter getCount mList.size() = " + mList.size());
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0==arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LOG.D(TAG,"StudentInfoPageAdapter instantiateItem position = " + position);
            ((ViewGroup)container).addView((View)mList.get(position));

            return mList.get(position);

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mList.get(position));
        }

    }


    public class RoleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private int mPageNumber = -1;
        private int mTotalNumber = -1;

        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;

        public class ViewHolder extends RecyclerView.ViewHolder{
            public LinearLayout mLayoutRole;
            public ImageView mImgRolePhoto;
            public ImageView mImgRoleCover;
            public TextView mTxtRoleName;

//        TextView txtStudentNote = (TextView) convertView.findViewById(R.id.txt_student_note);


            public ViewHolder(View itemView){
                super(itemView);

                mLayoutRole = (LinearLayout) itemView.findViewById(R.id.layout_role);
                mImgRolePhoto = (ImageView) itemView.findViewById(R.id.img_role_photo);
                mImgRoleCover = (ImageView) itemView.findViewById(R.id.img_role_cover);
                mTxtRoleName = (TextView) itemView.findViewById(R.id.txt_role_name);

            }
        }

        public RoleAdapter(int pageNumber, int totalNumber){
            mPageNumber = pageNumber;
            mTotalNumber = totalNumber;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();


//            ViewHolder viewHolder = new ViewHolder(contactView);
//
//            return viewHolder;
            LOG.D(TAG,"onCreateViewHolder viewType = " + viewType);

            if (viewType == TYPE_ITEM) {
                //inflate your layout and pass it to view holder
                View roleView = LayoutInflater.from(context).inflate(R.layout.item_role_holder, parent, false);
                ViewHolder viewHolder = new ViewHolder(roleView);

                return viewHolder;
            } else if (viewType == TYPE_HEADER) {
                //inflate your layout and pass it to view holder
//                View contactViewHeader = LayoutInflater.from(context).inflate(R.layout.item_student_info_header, parent, false);
//                ViewHolderHeader viewHolderHeader = new ViewHolderHeader(contactViewHeader);
//
//                return viewHolderHeader;
            }

            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
//            TextView txtStudentIndex = holder.mTxtStudentIndex;
            LOG.D(TAG," onBindViewHolder position = " + position);
            LOG.D(TAG," onBindViewHolder mPageNumber = " + mPageNumber);

            LOG.D(TAG," onBindViewHolder holder = " + holder);
//            LOG.D(TAG," onBindViewHolder holder = " + holder);
//            txtStudentIndex.setText("" + ((mPageNumber * PAGE_NUMBER) + position));

            if (holder instanceof ViewHolder) {
//                String dataItem = getItem(position);
                //cast holder to VHItem and set data
                ViewHolder viewHolder = (ViewHolder)holder;
                LinearLayout layoutRole = viewHolder.mLayoutRole;

                final ImageView imgRolePhoto = viewHolder.mImgRolePhoto;
                final ImageView imgRoleCover = viewHolder.mImgRoleCover;
                final TextView txtRoleName = viewHolder.mTxtRoleName;

                AcceptancesModel acceptancesModel = null;
                if(ClockUtils.mRoleModel instanceof EmployeeModel){
                    acceptancesModel = DeviceUtils.mEmployeeModel.getEmployeeData().getAcceptances().get(mPageNumber * PAGE_NUMBER + position);
                }else if(ClockUtils.mRoleModel instanceof VisitorModel){
                    acceptancesModel = DeviceUtils.mVisitorModel.getVisitorData().getAcceptances().get(mPageNumber * PAGE_NUMBER + position);
                }

                txtRoleName.setText(acceptancesModel.getFirstName() + " " + acceptancesModel.getLastName());


//                imgStudentPhoto.set

                String rolePhotoUrl = acceptancesModel.getPhotoUrl();

                LOG.D(TAG,"rolePhotoUrl = " + rolePhotoUrl);
                if(rolePhotoUrl == null || rolePhotoUrl.isEmpty()){
//                    rolePhotoUrl = ApiAccessor.SERVER_IP + "/SH_Clock_Server/images/defaultProfileImage.jpg";
                }

                ImageLoader imageLoader = ImageLoader.getInstance();
//                    imageLoader.displayImage(model.getTeacherPhotoUrl(), mImgTeacher);

                DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                        .showImageOnFail(R.mipmap.icon_default_photo)
                        .showImageOnLoading(R.mipmap.icon_default_photo)
                        .showImageForEmptyUri(R.mipmap.icon_default_photo)
                        .cacheInMemory(true)
                        .resetViewBeforeLoading(true)
                        .displayer(new RoundedBitmapDisplayer(100))
                        .build();

                imageLoader.displayImage(rolePhotoUrl, imgRolePhoto, imageOptions);

//                acceptancesModel.getS

//                final String loginAccount = acceptancesModel.getEmployeeId();
                final String loginAccount = acceptancesModel.getSecurityCode();
                final String uuId = acceptancesModel.getUuid();

                layoutRole.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LOG.D(TAG,"layoutRole onClick position = " + position);

                        ClockUtils.mLoginAccount = loginAccount;
                        ClockUtils.mLoginUuid = uuId;

                        mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                        mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.FDR_DELAYED_TIME);
                        startFdr();

                    }
                });



            }
//            else if (holder instanceof ViewHolderHeader) {
//                //cast holder to VHHeader and set data for header.
//            }


        }

        @Override
        public int getItemCount() {
            return mTotalNumber;
        }

        @Override
        public int getItemViewType(int position) {
            LOG.D(TAG,"getItemViewType position = " + position);
//            if(position == 0 || position == 1){
//                return TYPE_HEADER;
//            }

            return TYPE_ITEM;
        }


    }


    private Handler mFragmentHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LOG.D(TAG,"mHandler msg.what = " + msg.what);
            switch(msg.what) {
                case Constants.GET_FACE_SUCCESS:

                    mFragmentHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
                    mLayoutFdr.setVisibility(FrameLayout.GONE);
                    mLayoutPhoto.setVisibility(View.VISIBLE);
                    mTxtTitle.setVisibility(View.VISIBLE);

                    break;
                case Constants.GET_FACE_FAIL:

                    mFragmentHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
                    mLayoutFdr.setVisibility(FrameLayout.GONE);
                    mLayoutPhoto.setVisibility(View.VISIBLE);
                    mTxtTitle.setVisibility(View.VISIBLE);
                    break;
                case Constants.GET_FACE_TOO_LONG:

                    mFdrFrame.removeAllViews();
                    FDRControlManager.getInstance(mContext).stopFdr(mActivityHandler, mFragmentHandler);
                    mFragmentHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
                    mLayoutFdr.setVisibility(FrameLayout.GONE);
                    mLayoutPhoto.setVisibility(View.VISIBLE);
                    mTxtTitle.setVisibility(View.VISIBLE);

                    break;

            }
        }
    };


}
