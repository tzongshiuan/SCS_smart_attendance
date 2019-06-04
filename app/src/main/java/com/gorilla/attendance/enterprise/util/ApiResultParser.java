package com.gorilla.attendance.enterprise.util;

import com.gorilla.attendance.enterprise.datamodel.AcceptancesModel;
import com.gorilla.attendance.enterprise.datamodel.BapIdentifyDataModel;
import com.gorilla.attendance.enterprise.datamodel.BapIdentifyModel;
import com.gorilla.attendance.enterprise.datamodel.BapVerifyModel;
import com.gorilla.attendance.enterprise.datamodel.EmployeeDataModel;
import com.gorilla.attendance.enterprise.datamodel.ErrorModel;
import com.gorilla.attendance.enterprise.datamodel.GetEmployeeModel;
import com.gorilla.attendance.enterprise.datamodel.GetMarqueesModel;
import com.gorilla.attendance.enterprise.datamodel.GetVerifiedIdAndImageModel;
import com.gorilla.attendance.enterprise.datamodel.GetVideoModel;
import com.gorilla.attendance.enterprise.datamodel.GetVisitorModel;
import com.gorilla.attendance.enterprise.datamodel.IdentitiesDataModel;
import com.gorilla.attendance.enterprise.datamodel.IdentitiesModel;
import com.gorilla.attendance.enterprise.datamodel.LoginDataModel;
import com.gorilla.attendance.enterprise.datamodel.LoginModel;
import com.gorilla.attendance.enterprise.datamodel.MarqueesDataModel;
import com.gorilla.attendance.enterprise.datamodel.MarqueesModel;
import com.gorilla.attendance.enterprise.datamodel.MarqueesTextModel;
import com.gorilla.attendance.enterprise.datamodel.MemberDataModel;
import com.gorilla.attendance.enterprise.datamodel.ModuleModesModel;
import com.gorilla.attendance.enterprise.datamodel.PostMessageLimitedModel;
import com.gorilla.attendance.enterprise.datamodel.RecordsReplyModel;
import com.gorilla.attendance.enterprise.datamodel.RegisterReplyDataModel;
import com.gorilla.attendance.enterprise.datamodel.RegisterReplyModel;
import com.gorilla.attendance.enterprise.datamodel.RenewSecurityCodeDataModel;
import com.gorilla.attendance.enterprise.datamodel.RenewSecurityCodeModel;
import com.gorilla.attendance.enterprise.datamodel.SearchUserModel;
import com.gorilla.attendance.enterprise.datamodel.UsersDataModel;
import com.gorilla.attendance.enterprise.datamodel.VideoDataModel;
import com.gorilla.attendance.enterprise.datamodel.VideosModel;
import com.gorilla.attendance.enterprise.datamodel.VisitorDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ggshao on 2017/2/7.
 */

public class ApiResultParser {
    private static final String TAG = "ApiResultParser";

    private static final String API_RESULT_SUCCESS = "0";


    public static LoginModel loginParser(JSONObject obj) {

        //  Test data
//        LOG.D(TAG,"  loginParser TEST DATA");
//        LoginModel model = new LoginModel();
//        model.setStatus("success");
////        model.setStatus("error");
//
//        LoginDataModel loginDataModel = new LoginDataModel();
////        loginDataModel.setLocale("zh_TW");
//        loginDataModel.setLocale("en_US");
////        loginDataModel.setLocale("zh_CN");
//
////        ModuleModesModel moduleModesModel = new ModuleModesModel();
////        moduleModesModel.setModule(1);
////        int[] mode = {1,4,5};
////        moduleModesModel.setMode(mode);
////
////        ModuleModesModel moduleModesModel2 = new ModuleModesModel();
////        moduleModesModel2.setModule(2);
////        int[] mode2 = {1,4,5};
////        moduleModesModel2.setMode(mode2);
//
//        //visitors
//        ModuleModesModel moduleModesModel3 = new ModuleModesModel();
//        moduleModesModel3.setModule(3);
//        int[] mode3 = {1,2,3,4,5};
//        moduleModesModel3.setMode(mode3);
//
//        ModuleModesModel moduleModesModel4 = new ModuleModesModel();
//        moduleModesModel4.setModule(4);
//        int[] mode4 = {1,4,5};
//        moduleModesModel4.setMode(mode4);
//
////        loginDataModel.addModuleModes(moduleModesModel);
////        loginDataModel.addModuleModes(moduleModesModel2);
//        loginDataModel.addModuleModes(moduleModesModel4);
//        loginDataModel.addModuleModes(moduleModesModel3);
////
//        model.setLoginData(loginDataModel);
//
//
//        return model;

        if (obj == null) {
            return null;
        }

        LoginModel model = new LoginModel();

        try {
            if (!obj.isNull(LoginModel.KEY_STATUS)){
                model.setStatus(obj.getString(LoginModel.KEY_STATUS));
            }

            if (!obj.isNull(LoginModel.KEY_DATA)){
                JSONObject loginDataJsonObj = new JSONObject(obj.getString(LoginModel.KEY_DATA));
                model.setLoginData(loginDataParser(loginDataJsonObj));
            }

            if (!obj.isNull(LoginModel.KEY_ERROR)){
                JSONObject errorJsonObj = new JSONObject(obj.getString(LoginModel.KEY_ERROR));
                model.setError(ErrorDataParser(errorJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "LoginModel() - failed.", tr);
        }

        return model;

    }

    public static GetEmployeeModel getEmployeeParser(JSONObject obj) {

        //  Test Data
//        GetEmployeeModel model = new GetEmployeeModel();
//        model.setStatus("success");
//
//
//        EmployeeDataModel employeeDataModel = new EmployeeDataModel();
//        employeeDataModel.setTotalCounts(2);
//
//        AcceptancesModel acceptancesModel1 = new AcceptancesModel();
//        acceptancesModel1.setSecurityCode("1111");
//        acceptancesModel1.setUuid("uu1111");
//
//        AcceptancesModel acceptancesModel2 = new AcceptancesModel();
//        acceptancesModel2.setSecurityCode("2222");
//        acceptancesModel2.setUuid("uu2222");
//
//
//        employeeDataModel.addAcceptances(acceptancesModel1);
//        employeeDataModel.addAcceptances(acceptancesModel2);
//
//        model.setEmployeeData(employeeDataModel);
//
//
//        return model;

        if (obj == null) {
            return null;
        }

        GetEmployeeModel model = new GetEmployeeModel();

        try {
            if (!obj.isNull(GetEmployeeModel.KEY_STATUS)){
                model.setStatus(obj.getString(GetEmployeeModel.KEY_STATUS));
            }

            if (!obj.isNull(LoginModel.KEY_DATA)){
                JSONObject employeeDataJsonObj = new JSONObject(obj.getString(GetEmployeeModel.KEY_DATA));
                model.setEmployeeData(employeeDataParser(employeeDataJsonObj));
            }


        } catch (Throwable tr) {
            LOG.E(TAG, "getEmployeeParser() - failed.", tr);
        }

        return model;

    }

    public static GetVisitorModel getVisitorParser(JSONObject obj) {

        //  Test Data
//        GetEmployeeModel model = new GetEmployeeModel();
//        model.setStatus("success");
//
//
//        EmployeeDataModel employeeDataModel = new EmployeeDataModel();
//        employeeDataModel.setTotalCounts(2);
//
//        AcceptancesModel acceptancesModel1 = new AcceptancesModel();
//        acceptancesModel1.setSecurityCode("1111");
//        acceptancesModel1.setUuid("uu1111");
//
//        AcceptancesModel acceptancesModel2 = new AcceptancesModel();
//        acceptancesModel2.setSecurityCode("2222");
//        acceptancesModel2.setUuid("uu2222");
//
//
//        employeeDataModel.addAcceptances(acceptancesModel1);
//        employeeDataModel.addAcceptances(acceptancesModel2);
//
//        model.setEmployeeData(employeeDataModel);
//
//
//        return model;

        if (obj == null) {
            return null;
        }

        GetVisitorModel model = new GetVisitorModel();

        try {
            if (!obj.isNull(GetVisitorModel.KEY_STATUS)){
                model.setStatus(obj.getString(GetVisitorModel.KEY_STATUS));
            }

            if (!obj.isNull(GetVisitorModel.KEY_DATA)){
                JSONObject visitorDataJsonObj = new JSONObject(obj.getString(GetVisitorModel.KEY_DATA));
                model.setVisitorData(visitorDataParser(visitorDataJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "getVisitorParser() - failed.", tr);
        }

        return model;

    }

    public static GetVerifiedIdAndImageModel getVerifiedIdAndImageDataParser(JSONObject obj) {

        //  Test Data
//        GetVerifiedIdAndImageModel model = new GetVerifiedIdAndImageModel();
//        IdentitiesDataModel identitiesDataModel = new IdentitiesDataModel();
//
//
//
//        IdentitiesModel visitorsIdentitiesModel = new IdentitiesModel();
//        visitorsIdentitiesModel.setModel("bW9kZWw1MjAyODA1MAABAADn0vUAaQAAAAAAAAAAAAAAAACAPwAAAABepfQAAAEAAAEAAAABAAAA2ybUvMoYqD0HusS9wq8TvUwr6L0vTqk9536avRbNZj0uN7y8oFy9vTeR+rwXrrU9mDJ7OwfdXr0rO3U9IvVVPlbqsjybI5E9SAzJPDqRwjqelra8ND+EvVLdoT0I+Z09z\\/OOvUirSj1gxqQ9BrezPd8mNb0oSK68zlPdPb6P3Lx8wQ4+NxA2vTt\\/OT1WWmK9JYEUvP5uZD27x8U7C\\/ENPVzmET7xVwS+tCQ0PQdIezwAcrW9KKeGPVtqO718jkG8UE0eveDRqLt3pre9iH0Yvbf3qrzp2aO9sIOQPSCJED2rKnI8o6TYvFCnPz2zqNS8rHs\\/OiK2nL2TTEK83cyWvRDzsT2vf7C9vOYZvRJHTb209X29AuXOO99ocTwN\\/oG9w+2XPFfeTL2kguY8HHY2vQrpr7zSYck8erPCvdjRvr0JcqW962l0ve5LTT68BTq9hzsGu3L87jxqATg8u3EwvfjdQb2GxFE9AEWNPU4SPj08Xou9MhV8PHZGHz04DEK9KMkuPQ4g6TozEFS8DGjXu95wk7zT3z+7PecMPQmihb2C+EM9ksE+PWst1L1AX8+9wHAZPk4jAT3X+GW9N\\/nAvU2GoD0Q\\/AI9qFrVPdtDML1nZas9dNbMPYG0jr2WSwQ9alIjvOea4rvmJ1y9NNc0PacE\\/DyLVIA9gI6yvRfbLryG5G49iGkyPOsMh70ZPBs941vSOwqEmr1HHq28Ph2FPdxiND0MZ289tNLQO7b\\/4zzeLEU93rtFvP3ODr1GAxE80wWTOxOVcL3o1re6cvfqvLNFIb0NHhE9+MF7vTwYgLzQois+CKSsvQzFm7xnTMI7XmOjvSY5m72T22C9cZyFPGO7Ob1kLr09aFeIPUcMwjySQiU9dsiuPftFzzxS9G+8ksg1veBiTD1rDXi9aOnvPYsuFj1GRwk9lNHbPbJt7btstZg6T7E9vEPYML1y1SS9+tHmPRghKbvfMQi8mvu3PQNGGb0OkjM9C5iLurTjST2ocAU+pwbbvXRDg7zCVJ69VASqugLmmbyWoh294A3oPLT\\/dL1VoCk9hteSPfRAST1Uwkw9K9VgvOhEI76ymyA9EFTWvQ7hTz3whk09wGaEPeAznL3wCs87yGiIuoyXcbyonOK7W+D4vNzfFr2GeFA8iCyMu0zVRb2TQlI9jDndPDcl1D38RZQ8lkF1vYtKYDxYUgA8gGqNPFYEsT3+7tY7Cj4Bva71vb0OgEq9Y27jPeXVnr2tCCU9+sUEPbivAL3TfNU95zAjPHMXYD0srF09YNIPPTRNkD0cXVg99IOhPbKpIj3ydo29yiJvPU+htb3wThm8dykQPKxizLxq1D69lJFoPODPLjxeGZu8EoTvPRnI9AAAAAAAAAAAAAAAAAAAAPA\\/AAAAAAAAAABkAAAAAAAAAM3MzD5mZuY+AAAAP83MDD+amRk\\/mpkZP83MzD6amRk\\/zczMPgAAAD8AAAA\\/exSuR+F6hD8CAAAAAwAAAPyp8dJNYlA\\/AAAAAAAAAAALSjlK9ITGPzMzMzMzM8O\\/");
//        visitorsIdentitiesModel.setBapModelId("6");
//        visitorsIdentitiesModel.setId("B9CCF355-8746-4E01-B4F6-3298A8BD9B5C");
//        visitorsIdentitiesModel.setVisitorName("TestGG");
//        visitorsIdentitiesModel.setEmployeeId("9999");
//        visitorsIdentitiesModel.setCreatedTime("1487287306731");
//
//
//        identitiesDataModel.addVisitorsIdentifyData(visitorsIdentitiesModel);
////        identitiesDataModel.addIdentifyData(identitiesModel);
//        model.setIdentitiesData(identitiesDataModel);
//
//
//        return model;


        LOG.D(TAG,"getVerifiedIdAndImageDataParser");
        if (obj == null) {
            return null;
        }

        GetVerifiedIdAndImageModel model = new GetVerifiedIdAndImageModel();

        try {
            if (!obj.isNull(GetVerifiedIdAndImageModel.KEY_STATUS))
                model.setStatus(obj.getString(GetVerifiedIdAndImageModel.KEY_STATUS));

            if (!obj.isNull(GetVerifiedIdAndImageModel.KEY_DATA)){
                JSONObject identitiesDataJsonObj = new JSONObject(obj.getString(GetVerifiedIdAndImageModel.KEY_DATA));
                model.setIdentitiesData(identitiesDataParser(identitiesDataJsonObj));



//                JSONArray dataJsonArray = new JSONArray(obj.getString(GetVerifiedIdAndImageModel.KEY_DATA));
//                LOG.D(TAG,"dataJsonArray.length() = " + dataJsonArray.length());
//                for(int i = 0 ; i < dataJsonArray.length() ; i++){
//                    JSONObject dataJsonObj = dataJsonArray.getJSONObject(i);
//                    model.addIdentifyData(IdentifyDataParser(dataJsonObj));
//                }
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "getEmployeeListDataParser() - failed.", tr);
        }

        return model;

    }

    public static RecordsReplyModel attendanceRecordsParser(JSONObject obj) {

//        //  Test Data
//        RecordsReplyModel model = new RecordsReplyModel();
//        model.setStatus("success");
//
//        return model;


        LOG.D(TAG,"attendanceRecordsParser");
        if (obj == null) {
            return null;
        }

        RecordsReplyModel model = new RecordsReplyModel();

        try {
            if (!obj.isNull(RecordsReplyModel.KEY_STATUS))
                model.setStatus(obj.getString(RecordsReplyModel.KEY_STATUS));

            if (!obj.isNull(RecordsReplyModel.KEY_ERROR)){
                JSONObject errorJsonObj = new JSONObject(obj.getString(RecordsReplyModel.KEY_ERROR));
                model.setError(ErrorDataParser(errorJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "attendanceRecordsParser() - failed.", tr);
        }

        return model;

    }

    public static RecordsReplyModel accessRecordsParser(JSONObject obj) {

//        //  Test Data
//        RecordsReplyModel model = new RecordsReplyModel();
//        model.setStatus("success");
//
//        return model;


        LOG.D(TAG,"accessRecordsParser");
        if (obj == null) {
            return null;
        }

        RecordsReplyModel model = new RecordsReplyModel();

        try {
            if (!obj.isNull(RecordsReplyModel.KEY_STATUS))
                model.setStatus(obj.getString(RecordsReplyModel.KEY_STATUS));

            if (!obj.isNull(RecordsReplyModel.KEY_ERROR)){
                JSONObject errorJsonObj = new JSONObject(obj.getString(RecordsReplyModel.KEY_ERROR));
                model.setError(ErrorDataParser(errorJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "accessRecordsParser() - failed.", tr);
        }

        return model;

    }

    public static RecordsReplyModel deviceVisitorAccessRecordsParser(JSONObject obj) {

//        //  Test Data
//        RecordsReplyModel model = new RecordsReplyModel();
//        model.setStatus("success");
//
//        return model;


        LOG.D(TAG,"accessRecordsParser");
        if (obj == null) {
            return null;
        }

        RecordsReplyModel model = new RecordsReplyModel();

        try {
            if (!obj.isNull(RecordsReplyModel.KEY_STATUS))
                model.setStatus(obj.getString(RecordsReplyModel.KEY_STATUS));

            if (!obj.isNull(RecordsReplyModel.KEY_ERROR)){
                JSONObject errorJsonObj = new JSONObject(obj.getString(RecordsReplyModel.KEY_ERROR));
                model.setError(ErrorDataParser(errorJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "accessRecordsParser() - failed.", tr);
        }

        return model;

    }


    public static RecordsReplyModel visitRecordsParser(JSONObject obj) {

        //  Test Data
//        RecordsReplyModel model = new RecordsReplyModel();
//        model.setStatus("success");
//
//        return model;


        LOG.D(TAG,"visitRecordsParser");
        if (obj == null) {
            return null;
        }

        RecordsReplyModel model = new RecordsReplyModel();

        try {
            if (!obj.isNull(RecordsReplyModel.KEY_STATUS))
                model.setStatus(obj.getString(RecordsReplyModel.KEY_STATUS));

            if (!obj.isNull(RecordsReplyModel.KEY_ERROR)){
                JSONObject errorJsonObj = new JSONObject(obj.getString(RecordsReplyModel.KEY_ERROR));
                model.setError(ErrorDataParser(errorJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "accessRecordsParser() - failed.", tr);
        }

        return model;

    }


    public static RecordsReplyModel attendanceUnrecognizedLogParser(JSONObject obj) {

        //  Test Data
//        RecordsReplyModel model = new RecordsReplyModel();
//        model.setStatus("success");
//
//        return model;


        LOG.D(TAG,"attendanceUnrecognizedLogParser obj = " + obj);
        if (obj == null) {
            return null;
        }

        RecordsReplyModel model = new RecordsReplyModel();

        try {
            if (!obj.isNull(RecordsReplyModel.KEY_STATUS))
                model.setStatus(obj.getString(RecordsReplyModel.KEY_STATUS));

            if (!obj.isNull(RecordsReplyModel.KEY_ERROR)){
                JSONObject errorJsonObj = new JSONObject(obj.getString(RecordsReplyModel.KEY_ERROR));
                model.setError(ErrorDataParser(errorJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "attendanceUnrecognizedLogParser() - failed.", tr);
        }

        return model;

    }

    public static RecordsReplyModel accessUnrecognizedLogParser(JSONObject obj) {

        //  Test Data
//        RecordsReplyModel model = new RecordsReplyModel();
//        model.setStatus("success");
//
//        return model;


        LOG.D(TAG,"accessUnrecognizedLogParser");
        if (obj == null) {
            return null;
        }

        RecordsReplyModel model = new RecordsReplyModel();

        try {
            if (!obj.isNull(RecordsReplyModel.KEY_STATUS))
                model.setStatus(obj.getString(RecordsReplyModel.KEY_STATUS));

            if (!obj.isNull(RecordsReplyModel.KEY_ERROR)){
                JSONObject errorJsonObj = new JSONObject(obj.getString(RecordsReplyModel.KEY_ERROR));
                model.setError(ErrorDataParser(errorJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "accessUnrecognizedLogParser() - failed.", tr);
        }

        return model;

    }

    public static RecordsReplyModel accessVisitorUnrecognizedLogParser(JSONObject obj) {

        //  Test Data
//        RecordsReplyModel model = new RecordsReplyModel();
//        model.setStatus("success");
//
//        return model;


        LOG.D(TAG,"accessVisitorUnrecognizedLogParser");
        if (obj == null) {
            return null;
        }

        RecordsReplyModel model = new RecordsReplyModel();

        try {
            if (!obj.isNull(RecordsReplyModel.KEY_STATUS))
                model.setStatus(obj.getString(RecordsReplyModel.KEY_STATUS));

            if (!obj.isNull(RecordsReplyModel.KEY_ERROR)){
                JSONObject errorJsonObj = new JSONObject(obj.getString(RecordsReplyModel.KEY_ERROR));
                model.setError(ErrorDataParser(errorJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "accessUnrecognizedLogParser() - failed.", tr);
        }

        return model;

    }


    public static RecordsReplyModel visitorsUnrecognizedLogParser(JSONObject obj) {

        //  Test Data
//        RecordsReplyModel model = new RecordsReplyModel();
//        model.setStatus("success");
//
//        return model;


        LOG.D(TAG,"visitorsUnrecognizedLogParser");
        if (obj == null) {
            return null;
        }

        RecordsReplyModel model = new RecordsReplyModel();

        try {
            if (!obj.isNull(RecordsReplyModel.KEY_STATUS))
                model.setStatus(obj.getString(RecordsReplyModel.KEY_STATUS));

            if (!obj.isNull(RecordsReplyModel.KEY_ERROR)){
                JSONObject errorJsonObj = new JSONObject(obj.getString(RecordsReplyModel.KEY_ERROR));
                model.setError(ErrorDataParser(errorJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "visitorsUnrecognizedLogParser() - failed.", tr);
        }

        return model;

    }

    public static GetVideoModel getVideoParser(JSONObject obj) {

        //  Test Data
//        GetVideoModel model = new GetVideoModel();
//        model.setStatus("success");
//
//        return model;

        if (obj == null) {
            return null;
        }

        GetVideoModel model = new GetVideoModel();

        try {
            if (!obj.isNull(GetVideoModel.KEY_STATUS)){
                model.setStatus(obj.getString(GetVideoModel.KEY_STATUS));
            }

            if (!obj.isNull(GetVideoModel.KEY_DATA)){
                JSONObject videoDataJsonObj = new JSONObject(obj.getString(GetVideoModel.KEY_DATA));
                model.setVideoData(videoDataParser(videoDataJsonObj));
            }



        } catch (Throwable tr) {
            LOG.E(TAG, "getVideoParser() - failed.", tr);
        }

        return model;

    }


    public static GetMarqueesModel getMarqueesParser(JSONObject obj) {

        //  Test Data
//        GetMarqueesModel model = new GetMarqueesModel();
//        model.setStatus("success");
//
//        MarqueesDataModel marqueesDataModel = new MarqueesDataModel();
//        MarqueesModel marqueesModel = new MarqueesModel();
//        MarqueesTextModel marqueesTextModel = new MarqueesTextModel();
//
//        marqueesTextModel.setZhTw("跑馬燈測試跑馬燈測試跑馬燈測試跑馬燈測試跑馬燈測試跑馬燈測試跑馬燈測試跑馬燈測試");
//        marqueesTextModel.setEnUs("Marquee testMarquee testMarquee testMarquee testMarquee testMarquee test");
//        marqueesTextModel.setZhCn("跑马灯测试跑马灯测试跑马灯测试跑马灯测试跑马灯测试跑马灯测试跑马灯测试跑马灯测试");
//
//        marqueesModel.setMarqueesText(marqueesTextModel);
//        marqueesDataModel.addMarquees(marqueesModel);
//
//        model.setMarqueesDat(marqueesDataModel);
//
//        return model;

        if (obj == null) {
            return null;
        }

        GetMarqueesModel model = new GetMarqueesModel();

        try {
            if (!obj.isNull(GetMarqueesModel.KEY_STATUS)){
                model.setStatus(obj.getString(GetMarqueesModel.KEY_STATUS));
            }

            if (!obj.isNull(GetMarqueesModel.KEY_DATA)){
                JSONObject marqueesDataJsonObj = new JSONObject(obj.getString(GetMarqueesModel.KEY_DATA));
                model.setMarqueesDat(marqueesDataParser(marqueesDataJsonObj));
            }



        } catch (Throwable tr) {
            LOG.E(TAG, "getMarqueesParser() - failed.", tr);
        }

        return model;

    }

    public static SearchUserModel searchUserParser(JSONObject obj) {

        if (obj == null) {
            return null;
        }

        SearchUserModel model = new SearchUserModel();

        try {
            if (!obj.isNull(SearchUserModel.KEY_STATUS)){
                model.setStatus(obj.getString(SearchUserModel.KEY_STATUS));
            }

//            if (!obj.isNull(SearchUserModel.KEY_DATA)){
//                JSONObject usersDataJsonObj = new JSONObject(obj.getString(SearchUserModel.KEY_DATA));
//                model.setUsers(usersDataParser(usersDataJsonObj));
//            }

            if (!obj.isNull(SearchUserModel.KEY_DATA)){
                JSONObject membersDataJsonObj = new JSONObject(obj.getString(SearchUserModel.KEY_DATA));
                model.setMemberData(membersDataParser(membersDataJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "searchUserParser() - failed.", tr);
        }

        return model;

    }

    public static SearchUserModel searchEmployeeParser(JSONObject obj) {

        if (obj == null) {
            return null;
        }

        SearchUserModel model = new SearchUserModel();

        try {
            if (!obj.isNull(SearchUserModel.KEY_STATUS)){
                model.setStatus(obj.getString(SearchUserModel.KEY_STATUS));
            }

//            if (!obj.isNull(SearchUserModel.KEY_DATA)){
//                JSONObject usersDataJsonObj = new JSONObject(obj.getString(SearchUserModel.KEY_DATA));
//                model.setUsers(usersDataParser(usersDataJsonObj));
//            }

            if (!obj.isNull(SearchUserModel.KEY_DATA)){
                JSONObject membersDataJsonObj = new JSONObject(obj.getString(SearchUserModel.KEY_DATA));
                model.setMemberData(membersDataParser(membersDataJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "searchEmployeeParser() - failed.", tr);
        }

        return model;

    }

    public static SearchUserModel searchVisitorParser(JSONObject obj) {

        if (obj == null) {
            return null;
        }

        SearchUserModel model = new SearchUserModel();

        try {
            if (!obj.isNull(SearchUserModel.KEY_STATUS)){
                model.setStatus(obj.getString(SearchUserModel.KEY_STATUS));
            }

//            if (!obj.isNull(SearchUserModel.KEY_DATA)){
//                JSONObject usersDataJsonObj = new JSONObject(obj.getString(SearchUserModel.KEY_DATA));
//                model.setUsers(usersDataParser(usersDataJsonObj));
//            }

            if (!obj.isNull(SearchUserModel.KEY_DATA)){
                JSONObject membersDataJsonObj = new JSONObject(obj.getString(SearchUserModel.KEY_DATA));
                model.setMemberData(membersDataParser(membersDataJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "searchVisitorParser() - failed.", tr);
        }

        return model;

    }

    public static RegisterReplyModel registerEmployeeParser(JSONObject obj) {

        if (obj == null) {
            return null;
        }

        RegisterReplyModel model = new RegisterReplyModel();

        try {
            if (!obj.isNull(RegisterReplyModel.KEY_STATUS)){
                model.setStatus(obj.getString(RegisterReplyModel.KEY_STATUS));
            }

            if (!obj.isNull(RegisterReplyModel.KEY_DATA)){
                JSONObject registerReplyDataJsonObj = new JSONObject(obj.getString(RegisterReplyModel.KEY_DATA));
                model.setRegisterData(registerReplyDataParser(registerReplyDataJsonObj));
            }

            if (!obj.isNull(RegisterReplyModel.KEY_ERROR)){
                JSONObject errorJsonObj = new JSONObject(obj.getString(RegisterReplyModel.KEY_ERROR));
                model.setError(ErrorDataParser(errorJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "registerEmployeeParser() - failed.", tr);
        }

        return model;

    }

    public static RegisterReplyModel registerEmployeeV2Parser(JSONObject obj) {

        if (obj == null) {
            return null;
        }

        RegisterReplyModel model = new RegisterReplyModel();

        try {
            if (!obj.isNull(RegisterReplyModel.KEY_STATUS)){
                model.setStatus(obj.getString(RegisterReplyModel.KEY_STATUS));
            }

            if (!obj.isNull(RegisterReplyModel.KEY_DATA)){
                JSONObject registerReplyDataJsonObj = new JSONObject(obj.getString(RegisterReplyModel.KEY_DATA));
                model.setRegisterData(registerReplyDataParser(registerReplyDataJsonObj));
            }

            if (!obj.isNull(RegisterReplyModel.KEY_ERROR)){
                JSONObject errorJsonObj = new JSONObject(obj.getString(RegisterReplyModel.KEY_ERROR));
                model.setError(ErrorDataParser(errorJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "registerEmployeeV2Parser() - failed.", tr);
        }

        return model;

    }


    public static RegisterReplyModel registerVisitorParser(JSONObject obj) {

        if (obj == null) {
            return null;
        }

        RegisterReplyModel model = new RegisterReplyModel();

        try {
            if (!obj.isNull(RegisterReplyModel.KEY_STATUS)){
                model.setStatus(obj.getString(RegisterReplyModel.KEY_STATUS));
            }

            if (!obj.isNull(RegisterReplyModel.KEY_DATA)){
                JSONObject registerReplyDataJsonObj = new JSONObject(obj.getString(RegisterReplyModel.KEY_DATA));
                model.setRegisterData(registerReplyDataParser(registerReplyDataJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "registerVisitorParser() - failed.", tr);
        }

        return model;

    }

    public static RegisterReplyModel registerVisitorEmailParser(JSONObject obj) {

        if (obj == null) {
            return null;
        }

        RegisterReplyModel model = new RegisterReplyModel();

        try {
            if (!obj.isNull(RegisterReplyModel.KEY_STATUS)){
                model.setStatus(obj.getString(RegisterReplyModel.KEY_STATUS));
            }

            if (!obj.isNull(RegisterReplyModel.KEY_DATA)){
                JSONObject registerReplyDataJsonObj = new JSONObject(obj.getString(RegisterReplyModel.KEY_DATA));
                model.setRegisterData(registerReplyDataParser(registerReplyDataJsonObj));
            }

            if (!obj.isNull(RegisterReplyModel.KEY_ERROR)){
                JSONObject errorJsonObj = new JSONObject(obj.getString(RegisterReplyModel.KEY_ERROR));
                model.setError(ErrorDataParser(errorJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "registerVisitorEmailParser() - failed.", tr);
        }

        return model;

    }

    public static RegisterReplyModel registerVisitorV2Parser(JSONObject obj) {

        if (obj == null) {
            return null;
        }

        RegisterReplyModel model = new RegisterReplyModel();

        try {
            if (!obj.isNull(RegisterReplyModel.KEY_STATUS)){
                model.setStatus(obj.getString(RegisterReplyModel.KEY_STATUS));
            }

            if (!obj.isNull(RegisterReplyModel.KEY_DATA)){
                JSONObject registerReplyDataJsonObj = new JSONObject(obj.getString(RegisterReplyModel.KEY_DATA));
                model.setRegisterData(registerReplyDataParser(registerReplyDataJsonObj));
            }

            if (!obj.isNull(RegisterReplyModel.KEY_ERROR)){
                JSONObject errorJsonObj = new JSONObject(obj.getString(RegisterReplyModel.KEY_ERROR));
                model.setError(ErrorDataParser(errorJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "registerVisitorV2Parser() - failed.", tr);
        }

        return model;

    }


    public static RenewSecurityCodeModel renewSecurityCodeParser(JSONObject obj) {

        if (obj == null) {
            return null;
        }

        RenewSecurityCodeModel model = new RenewSecurityCodeModel();

        try {
            if (!obj.isNull(RenewSecurityCodeModel.KEY_STATUS)){
                model.setStatus(obj.getString(RenewSecurityCodeModel.KEY_STATUS));
            }

            if (!obj.isNull(RenewSecurityCodeModel.KEY_DATA)){
                JSONObject renewSecurityCodeDataJsonObj = new JSONObject(obj.getString(RenewSecurityCodeModel.KEY_DATA));
                model.setRenewSecurityCodeData(renewSecurityCodeDataParser(renewSecurityCodeDataJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "renewSecurityCodeParser() - failed.", tr);
        }

        return model;

    }

    public static PostMessageLimitedModel postMessageLimitedParser(JSONObject obj) {

        if (obj == null) {
            return null;
        }

        PostMessageLimitedModel model = new PostMessageLimitedModel();

        try {
            if (!obj.isNull(PostMessageLimitedModel.KEY_BATCH_ID)){
                model.setBatchID(obj.getString(PostMessageLimitedModel.KEY_BATCH_ID));
            }

            if (!obj.isNull(PostMessageLimitedModel.KEY_ERROR_CODE)){
                model.setErrorCode(obj.getInt(PostMessageLimitedModel.KEY_ERROR_CODE));
            }

            if (!obj.isNull(PostMessageLimitedModel.KEY_IS_SUCCESS)){
                model.setIsSuccess(obj.getBoolean(PostMessageLimitedModel.KEY_IS_SUCCESS));
            }

            if (!obj.isNull(PostMessageLimitedModel.KEY_DESCRIPTION)){
                model.setDescription(obj.getString(PostMessageLimitedModel.KEY_DESCRIPTION));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "renewSecurityCodeParser() - failed.", tr);
        }

        return model;

    }






    /************************************************************** Data Parser***************************************************************/

    public static LoginDataModel loginDataParser(JSONObject loginDataJsonObj) {
        LOG.D(TAG,"loginDataParser loginDataJsonObj = " + loginDataJsonObj);
        LoginDataModel loginDataModel = new LoginDataModel();

        try {

            if (!loginDataJsonObj.isNull(LoginDataModel.KEY_LOCALE))
                loginDataModel.setLocale(loginDataJsonObj.getString(LoginDataModel.KEY_LOCALE));

            if (!loginDataJsonObj.isNull(LoginDataModel.KEY_BG_IMAGE))
                loginDataModel.setBgImage(loginDataJsonObj.getString(LoginDataModel.KEY_BG_IMAGE));

            if (!loginDataJsonObj.isNull(LoginDataModel.KEY_DEVICE_NAME))
                loginDataModel.setDeviceName(loginDataJsonObj.getString(LoginDataModel.KEY_DEVICE_NAME));

            if (!loginDataJsonObj.isNull(LoginDataModel.KEY_TITLE_IMAGE))
                loginDataModel.setTitleImage(loginDataJsonObj.getString(LoginDataModel.KEY_TITLE_IMAGE));

            if (!loginDataJsonObj.isNull(LoginDataModel.KEY_MODULE_MODES)){

                JSONArray moduleModesJsonArray = new JSONArray(loginDataJsonObj.getString(LoginDataModel.KEY_MODULE_MODES));
                LOG.D(TAG,"moduleModesJsonArray.length() = " + moduleModesJsonArray.length());
                for(int i = 0 ; i < moduleModesJsonArray.length() ; i++){
                    JSONObject moduleModesJsonObj = moduleModesJsonArray.getJSONObject(i);
                    loginDataModel.addModuleModes(moduleModesParser(moduleModesJsonObj));
                }
            }


        } catch (JSONException e) {
            LOG.E(TAG, "JSONException occurs, ", e);
            return null;
        }

        return loginDataModel;
    }



    public static ModuleModesModel moduleModesParser(JSONObject moduleModeJsonObj) {
        ModuleModesModel moduleModesModel = new ModuleModesModel();

        try{

            if (!moduleModeJsonObj.isNull(ModuleModesModel.KEY_MODULE))
                moduleModesModel.setModule(moduleModeJsonObj.getInt(ModuleModesModel.KEY_MODULE));


            if (!moduleModeJsonObj.isNull(ModuleModesModel.KEY_MODE)){
                String arr = moduleModeJsonObj.getString(ModuleModesModel.KEY_MODE);
                String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");

                LOG.D(TAG,"moduleModesParser items.length = " + items.length);
                int[] results = new int[items.length];

                for (int i = 0; i < items.length; i++) {
                    try {
                        LOG.D(TAG,"moduleModesParser items[i] = " + items[i]);
                        results[i] = Integer.parseInt(items[i]);
                    } catch (NumberFormatException nfe) {
                        //NOTE: write something here if you need to recover from formatting errors
                    };
                }

                moduleModesModel.setMode(results);
            }



        } catch (JSONException e) {
            LOG.E(TAG, "moduleModesParser JSONException occurs, ", e);
            return null;
        }


        return moduleModesModel;
    }



    public static IdentitiesDataModel identitiesDataParser(JSONObject identitiesDataJsonObj) {
        LOG.D(TAG,"identitiesDataParser identitiesDataJsonObj = " + identitiesDataJsonObj);
        IdentitiesDataModel identitiesDataModel = new IdentitiesDataModel();

        try {

            if (!identitiesDataJsonObj.isNull(IdentitiesDataModel.KEY_EMPLOYEES)){

                JSONArray identitiesJsonArray = new JSONArray(identitiesDataJsonObj.getString(IdentitiesDataModel.KEY_EMPLOYEES));
                LOG.D(TAG,"identitiesJsonArray.length() = " + identitiesJsonArray.length());
                for(int i = 0 ; i < identitiesJsonArray.length() ; i++){
                    JSONObject identitiesJsonObj = identitiesJsonArray.getJSONObject(i);
                    identitiesDataModel.addEmployeesIdentifyData(identitiesParser(identitiesJsonObj));
                }
            }

            if (!identitiesDataJsonObj.isNull(IdentitiesDataModel.KEY_VISITORS)){

                JSONArray identitiesJsonArray = new JSONArray(identitiesDataJsonObj.getString(IdentitiesDataModel.KEY_VISITORS));
                LOG.D(TAG,"identitiesJsonArray.length() = " + identitiesJsonArray.length());
                for(int i = 0 ; i < identitiesJsonArray.length() ; i++){
                    JSONObject identitiesJsonObj = identitiesJsonArray.getJSONObject(i);
                    identitiesDataModel.addVisitorsIdentifyData(identitiesParser(identitiesJsonObj));
                }
            }


        } catch (JSONException e) {
            LOG.E(TAG, "JSONException occurs, ", e);
            return null;
        }

        return identitiesDataModel;
    }


    public static IdentitiesModel identitiesParser(JSONObject identitiesJsonObj) {
        IdentitiesModel identitiesModel = new IdentitiesModel();

        try{

//            if (!identitiesJsonObj.isNull(IdentitiesModel.KEY_BAP_MODEL_ID)){
////                identitiesModel.setBapModelId(identitiesJsonObj.getString(IdentitiesModel.KEY_BAP_MODEL_ID));
//                identitiesModel.setBapModelId(identitiesJsonObj.getString(IdentitiesModel.KEY_BAP_MODEL_ID));
//            }

            if (!identitiesJsonObj.isNull(IdentitiesModel.KEY_INT_ID)){
//                identitiesModel.setBapModelId(identitiesJsonObj.getString(IdentitiesModel.KEY_BAP_MODEL_ID));
                identitiesModel.setBapModelId(identitiesJsonObj.getString(IdentitiesModel.KEY_INT_ID));
            }

            if (!identitiesJsonObj.isNull(IdentitiesModel.KEY_ID))
                identitiesModel.setId(identitiesJsonObj.getString(IdentitiesModel.KEY_ID));

            if (!identitiesJsonObj.isNull(IdentitiesModel.KEY_EMPLOYEE_ID))
                identitiesModel.setEmployeeId(identitiesJsonObj.getString(IdentitiesModel.KEY_EMPLOYEE_ID));

            if (!identitiesJsonObj.isNull(IdentitiesModel.KEY_EMPLOYEE_NAME))
                identitiesModel.setEmployeeName(identitiesJsonObj.getString(IdentitiesModel.KEY_EMPLOYEE_NAME));

            if (!identitiesJsonObj.isNull(IdentitiesModel.KEY_VISITOR_NAME))
                identitiesModel.setVisitorName(identitiesJsonObj.getString(IdentitiesModel.KEY_VISITOR_NAME));

            if (!identitiesJsonObj.isNull(IdentitiesModel.KEY_FIRST_NAME))
                identitiesModel.setFirstName(identitiesJsonObj.getString(IdentitiesModel.KEY_FIRST_NAME));

            if (!identitiesJsonObj.isNull(IdentitiesModel.KEY_LAST_NAME))
                identitiesModel.setLastName(identitiesJsonObj.getString(IdentitiesModel.KEY_LAST_NAME));

            if (!identitiesJsonObj.isNull(IdentitiesModel.KEY_CREATED_TIME))
                identitiesModel.setCreatedTime(identitiesJsonObj.getString(IdentitiesModel.KEY_CREATED_TIME));

            if (!identitiesJsonObj.isNull(IdentitiesModel.KEY_MODEL))
                identitiesModel.setModel(identitiesJsonObj.getString(IdentitiesModel.KEY_MODEL));

        } catch (JSONException e) {
            LOG.E(TAG, "identitiesParser JSONException occurs, ", e);
            return null;
        }

        return identitiesModel;
    }


    public static EmployeeDataModel employeeDataParser(JSONObject employeeDataJsonObj) {
        LOG.D(TAG,"employeeDataParser employeeDataJsonObj = " + employeeDataJsonObj);
        EmployeeDataModel employeeDataModel = new EmployeeDataModel();

        try {

            if (!employeeDataJsonObj.isNull(EmployeeDataModel.KEY_TOTAL_COUNTS))
                employeeDataModel.setTotalCounts(employeeDataJsonObj.getInt(EmployeeDataModel.KEY_TOTAL_COUNTS));

            if (!employeeDataJsonObj.isNull(EmployeeDataModel.KEY_ACCEPTANCES)){

                JSONArray acceptancesJsonArray = new JSONArray(employeeDataJsonObj.getString(EmployeeDataModel.KEY_ACCEPTANCES));
                LOG.D(TAG,"acceptancesJsonArray.length() = " + acceptancesJsonArray.length());
                for(int i = 0 ; i < acceptancesJsonArray.length() ; i++){
                    JSONObject acceptancesJsonObj = acceptancesJsonArray.getJSONObject(i);
                    employeeDataModel.addAcceptances(acceptancesParser(acceptancesJsonObj));
                }
            }


        } catch (JSONException e) {
            LOG.E(TAG, "JSONException occurs, ", e);
            return null;
        }

        return employeeDataModel;
    }

    public static VisitorDataModel visitorDataParser(JSONObject visitorDataJsonObj) {
        LOG.D(TAG,"employeeDataParser employeeDataJsonObj = " + visitorDataJsonObj);
        VisitorDataModel visitorDataModel = new VisitorDataModel();

        try {

            if (!visitorDataJsonObj.isNull(VisitorDataModel.KEY_TOTAL_COUNTS))
                visitorDataModel.setTotalCounts(visitorDataJsonObj.getInt(VisitorDataModel.KEY_TOTAL_COUNTS));

            if (!visitorDataJsonObj.isNull(VisitorDataModel.KEY_ACCEPTANCES)){

                JSONArray acceptancesJsonArray = new JSONArray(visitorDataJsonObj.getString(VisitorDataModel.KEY_ACCEPTANCES));
                LOG.D(TAG,"acceptancesJsonArray.length() = " + acceptancesJsonArray.length());
                for(int i = 0 ; i < acceptancesJsonArray.length() ; i++){
                    JSONObject acceptancesJsonObj = acceptancesJsonArray.getJSONObject(i);
                    visitorDataModel.addAcceptances(acceptancesParser(acceptancesJsonObj));
                }
            }


        } catch (JSONException e) {
            LOG.E(TAG, "JSONException occurs, ", e);
            return null;
        }

        return visitorDataModel;
    }



    public static AcceptancesModel acceptancesParser(JSONObject acceptanceJsonObj) {
        AcceptancesModel acceptancesModel = new AcceptancesModel();

        try{

            if (!acceptanceJsonObj.isNull(AcceptancesModel.KEY_INT_ID))
                acceptancesModel.setIntId(acceptanceJsonObj.getInt(AcceptancesModel.KEY_INT_ID));

            if (!acceptanceJsonObj.isNull(AcceptancesModel.KEY_UUID))
                acceptancesModel.setUuid(acceptanceJsonObj.getString(AcceptancesModel.KEY_UUID));

            if (!acceptanceJsonObj.isNull(AcceptancesModel.KEY_EMPLOYEE_ID))
                acceptancesModel.setEmployeeId(acceptanceJsonObj.getString(AcceptancesModel.KEY_EMPLOYEE_ID));

            if (!acceptanceJsonObj.isNull(AcceptancesModel.KEY_SECURITY_CODE))
                acceptancesModel.setSecurityCode(acceptanceJsonObj.getString(AcceptancesModel.KEY_SECURITY_CODE));

            if (!acceptanceJsonObj.isNull(AcceptancesModel.KEY_RFID))
                acceptancesModel.setRfid(acceptanceJsonObj.getString(AcceptancesModel.KEY_RFID));

            if (!acceptanceJsonObj.isNull(AcceptancesModel.KEY_FIRST_NAME))
                acceptancesModel.setFirstName(acceptanceJsonObj.getString(AcceptancesModel.KEY_FIRST_NAME));

            if (!acceptanceJsonObj.isNull(AcceptancesModel.KEY_LAST_NAME))
                acceptancesModel.setLastName(acceptanceJsonObj.getString(AcceptancesModel.KEY_LAST_NAME));

            if (!acceptanceJsonObj.isNull(AcceptancesModel.KEY_START_TIME))
                acceptancesModel.setStartTime(acceptanceJsonObj.getLong(AcceptancesModel.KEY_START_TIME));

            if (!acceptanceJsonObj.isNull(AcceptancesModel.KEY_END_TIME))
                acceptancesModel.setEndTime(acceptanceJsonObj.getLong(AcceptancesModel.KEY_END_TIME));

            if (!acceptanceJsonObj.isNull(AcceptancesModel.KEY_PHOTO_URL))
                acceptancesModel.setPhotoUrl(acceptanceJsonObj.getString(AcceptancesModel.KEY_PHOTO_URL));

        } catch (JSONException e) {
            LOG.E(TAG, "acceptancesParser JSONException occurs, ", e);
            return null;
        }


        return acceptancesModel;
    }




//    public static IdentifyDataModel IdentifyDataParser(JSONObject dataJsonObj) {
//        IdentifyDataModel identifyDataModel = new IdentifyDataModel();
//
//        try{
//
//            if (!dataJsonObj.isNull(IdentifyDataModel.KEY_MODEL_ID))
//                identifyDataModel.setModelId(dataJsonObj.getString(IdentifyDataModel.KEY_MODEL_ID));
//
//            if (!dataJsonObj.isNull(IdentifyDataModel.KEY_EMPLOYEE_ID))
//                identifyDataModel.setEmployeeId(dataJsonObj.getString(IdentifyDataModel.KEY_EMPLOYEE_ID));
//
//            if (!dataJsonObj.isNull(IdentifyDataModel.KEY_EMLOYEE_NAME))
//                identifyDataModel.setEmployeeName(dataJsonObj.getString(IdentifyDataModel.KEY_EMLOYEE_NAME));
//
//            if (!dataJsonObj.isNull(IdentifyDataModel.KEY_CREATED_TIME))
//                identifyDataModel.setCreatedTime(dataJsonObj.getString(IdentifyDataModel.KEY_CREATED_TIME));
//
//            if (!dataJsonObj.isNull(IdentifyDataModel.KEY_MODEL))
//                identifyDataModel.setModel(dataJsonObj.getString(IdentifyDataModel.KEY_MODEL));
//
//        } catch (JSONException e) {
//            LOG.E(TAG, "JSONException occurs, ", e);
//            return null;
//        }
//
//        return identifyDataModel;
//    }

    public static ErrorModel ErrorDataParser(JSONObject errorJsonObj) {
        ErrorModel errorDataModel = new ErrorModel();

        try{

            if (!errorJsonObj.isNull(ErrorModel.KEY_CODE))
                errorDataModel.setCode(errorJsonObj.getString(ErrorModel.KEY_CODE));

            if (!errorJsonObj.isNull(ErrorModel.KEY_MESSAGE))
                errorDataModel.setMessage(errorJsonObj.getString(ErrorModel.KEY_MESSAGE));


        } catch (JSONException e) {
            LOG.E(TAG, "JSONException occurs, ", e);
            return null;
        }

        return errorDataModel;
    }

    public static VideoDataModel videoDataParser(JSONObject videoDataJsonObj) {
        VideoDataModel videoDataModel = new VideoDataModel();

        try{

            if (!videoDataJsonObj.isNull(VideoDataModel.KEY_TOTAL_COUNTS))
                videoDataModel.setTotalCounts(videoDataJsonObj.getInt(VideoDataModel.KEY_TOTAL_COUNTS));

            if (!videoDataJsonObj.isNull(VideoDataModel.KEY_VIDEOS)){
                JSONArray videoJsonArray = new JSONArray(videoDataJsonObj.getString(VideoDataModel.KEY_VIDEOS));
                LOG.D(TAG,"videoJsonArray.length() = " + videoJsonArray.length());
                for(int i = 0 ; i < videoJsonArray.length() ; i++){
                    JSONObject videoJsonObj = videoJsonArray.getJSONObject(i);
                    videoDataModel.addVideos(videoParser(videoJsonObj));
                }
            }


        } catch (JSONException e) {
            LOG.E(TAG, "JSONException occurs, ", e);
            return null;
        }

        return videoDataModel;
    }

    public static VideosModel videoParser(JSONObject videoJsonObj) {
        VideosModel videosModel = new VideosModel();

        try{
            if (!videoJsonObj.isNull(VideosModel.KEY_NAME)){
                videosModel.setName(videoJsonObj.getString(VideosModel.KEY_NAME));
            }

            if (!videoJsonObj.isNull(VideosModel.KEY_URL)){
                videosModel.setUrl(videoJsonObj.getString(VideosModel.KEY_URL));
            }

            if (!videoJsonObj.isNull(VideosModel.KEY_THUMB_URL)){
                videosModel.setThumbUrl(videoJsonObj.getString(VideosModel.KEY_THUMB_URL));
            }

            if (!videoJsonObj.isNull(VideosModel.KEY_PRIORITY)){
                videosModel.setPriority(videoJsonObj.getString(VideosModel.KEY_PRIORITY));
            }

            if (!videoJsonObj.isNull(VideosModel.KEY_LENGTH)){
                videosModel.setLength(videoJsonObj.getString(VideosModel.KEY_LENGTH));
            }

            if (!videoJsonObj.isNull(VideosModel.KEY_FILE_SIZE)){
                videosModel.setFileSize(videoJsonObj.getString(VideosModel.KEY_FILE_SIZE));
            }


        } catch (JSONException e) {
            LOG.E(TAG, "JSONException occurs, ", e);
            return null;
        }


        return videosModel;
    }

    public static MarqueesDataModel marqueesDataParser(JSONObject marqueesDataJsonObj) {
        MarqueesDataModel marqueesDataModel = new MarqueesDataModel();

        try{

            if (!marqueesDataJsonObj.isNull(MarqueesDataModel.KEY_TOTAL_COUNTS))
                marqueesDataModel.setTotalCounts(marqueesDataJsonObj.getInt(MarqueesDataModel.KEY_TOTAL_COUNTS));

            if (!marqueesDataJsonObj.isNull(MarqueesDataModel.KEY_MARQUEES)){
                JSONArray marqueesJsonArray = new JSONArray(marqueesDataJsonObj.getString(MarqueesDataModel.KEY_MARQUEES));
                LOG.D(TAG,"marqueesJsonArray.length() = " + marqueesJsonArray.length());
                for(int i = 0 ; i < marqueesJsonArray.length() ; i++){
                    JSONObject marqueesJsonObj = marqueesJsonArray.getJSONObject(i);
                    marqueesDataModel.addMarquees(marqueesParser(marqueesJsonObj));
                }
            }


        } catch (JSONException e) {
            LOG.E(TAG, "marqueesDataParser JSONException occurs, ", e);
            return null;
        }

        return marqueesDataModel;
    }

    public static MarqueesModel marqueesParser(JSONObject marqueesJsonObj) {
        MarqueesModel marqueesModel = new MarqueesModel();

        try{

            if (!marqueesJsonObj.isNull(MarqueesModel.KEY_TEXT)){
                JSONObject marqueesTextJsonObj = new JSONObject(marqueesJsonObj.getString(MarqueesModel.KEY_TEXT));
                marqueesModel.setMarqueesText(marqueesTextParser(marqueesTextJsonObj));
            }

            if (!marqueesJsonObj.isNull(MarqueesModel.KEY_SPEED)){
                marqueesModel.setSpeed(marqueesJsonObj.getInt(MarqueesModel.KEY_SPEED));
            }

            if (!marqueesJsonObj.isNull(MarqueesModel.KEY_DIRECTION)){
                marqueesModel.setDirecton(marqueesJsonObj.getInt(MarqueesModel.KEY_DIRECTION));
            }



        } catch (JSONException e) {
            LOG.E(TAG, "marqueesParser JSONException occurs, ", e);
            return null;
        }


        return marqueesModel;
    }

    public static MarqueesTextModel marqueesTextParser(JSONObject marqueesJsonObj) {
        MarqueesTextModel marqueesTextModel = new MarqueesTextModel();

        try{
            if (!marqueesJsonObj.isNull(MarqueesTextModel.KEY_ZH_TW)){
                marqueesTextModel.setZhTw(marqueesJsonObj.getString(MarqueesTextModel.KEY_ZH_TW));
            }

            if (!marqueesJsonObj.isNull(MarqueesTextModel.KEY_EN_US)){
                marqueesTextModel.setEnUs(marqueesJsonObj.getString(MarqueesTextModel.KEY_EN_US));
            }

            if (!marqueesJsonObj.isNull(MarqueesTextModel.KEY_ZH_CN)){
                marqueesTextModel.setZhCn(marqueesJsonObj.getString(MarqueesTextModel.KEY_ZH_CN));
            }

        } catch (JSONException e) {
            LOG.E(TAG, "marqueesTextParser JSONException occurs, ", e);
            return null;
        }


        return marqueesTextModel;
    }

    public static UsersDataModel usersDataParser(JSONObject usersDataJsonObj) {
        LOG.D(TAG,"usersDataParser usersDataJsonObj = " + usersDataJsonObj);
        UsersDataModel usersDataModel = new UsersDataModel();

        try {

            if (!usersDataJsonObj.isNull(UsersDataModel.KEY_MEMBER)){
                JSONObject memberDataJsonObj = new JSONObject(usersDataJsonObj.getString(UsersDataModel.KEY_MEMBER));
                usersDataModel.setMemberData(membersDataParser(memberDataJsonObj));
            }


        } catch (JSONException e) {
            LOG.E(TAG, "usersDataParser JSONException occurs, ", e);
            return null;
        }

        return usersDataModel;
    }


    public static MemberDataModel membersDataParser(JSONObject membersJsonObj) {
        MemberDataModel memberDataModel = new MemberDataModel();

        try{
            if (!membersJsonObj.isNull(MemberDataModel.KEY_UUID)){
                memberDataModel.setId(membersJsonObj.getString(MemberDataModel.KEY_UUID));
            }

            if (!membersJsonObj.isNull(MemberDataModel.KEY_EMPLOYEE_ID)){
                memberDataModel.setEmployeeId(membersJsonObj.getString(MemberDataModel.KEY_EMPLOYEE_ID));
            }

            if (!membersJsonObj.isNull(MemberDataModel.KEY_MOBILE_NO)){
                memberDataModel.setMobileNo(membersJsonObj.getString(MemberDataModel.KEY_MOBILE_NO));
            }

            if (!membersJsonObj.isNull(MemberDataModel.KEY_FIRST_NAME)){
                memberDataModel.setFirstName(membersJsonObj.getString(MemberDataModel.KEY_FIRST_NAME));
            }

            if (!membersJsonObj.isNull(MemberDataModel.KEY_LAST_NAME)){
                memberDataModel.setLastName(membersJsonObj.getString(MemberDataModel.KEY_LAST_NAME));
            }

            if (!membersJsonObj.isNull(MemberDataModel.KEY_PHOTO_URL)){
                memberDataModel.setPhotoUrl(membersJsonObj.getString(MemberDataModel.KEY_PHOTO_URL));
            }

        } catch (JSONException e) {
            LOG.E(TAG, "membersDataParser JSONException occurs, ", e);
            return null;
        }


        return memberDataModel;
    }

    public static RegisterReplyDataModel registerReplyDataParser(JSONObject registerReplayDataJsonObj) {
        RegisterReplyDataModel registerReplyDataModel = new RegisterReplyDataModel();

        try{
            if (!registerReplayDataJsonObj.isNull(RegisterReplyDataModel.KEY_ID)){
                registerReplyDataModel.setId(registerReplayDataJsonObj.getString(RegisterReplyDataModel.KEY_ID));
            }


        } catch (JSONException e) {
            LOG.E(TAG, "registerReplyDataParser JSONException occurs, ", e);
            return null;
        }


        return registerReplyDataModel;
    }

    public static RenewSecurityCodeDataModel renewSecurityCodeDataParser(JSONObject renewSecurityCodeDataJsonObj) {
        RenewSecurityCodeDataModel renewSecurityCodeDataModel = new RenewSecurityCodeDataModel();

        try{
            if (!renewSecurityCodeDataJsonObj.isNull(RenewSecurityCodeDataModel.KEY_NEW_SECURITY_CODE)){
                renewSecurityCodeDataModel.setNewSecurityCode(renewSecurityCodeDataJsonObj.getString(RenewSecurityCodeDataModel.KEY_NEW_SECURITY_CODE));
            }


        } catch (JSONException e) {
            LOG.E(TAG, "renewSecurityCodeDataParser JSONException occurs, ", e);
            return null;
        }


        return renewSecurityCodeDataModel;
    }

    public static BapVerifyModel bapVerifyParser(JSONObject obj) {

        if (obj == null) {
            return null;
        }

        BapVerifyModel model = new BapVerifyModel();

        try {
            if (!obj.isNull(BapVerifyModel.KEY_STATUS)){
                model.setStatus(obj.getString(BapVerifyModel.KEY_STATUS));
            }

            if (!obj.isNull(BapVerifyModel.KEY_ERROR)){
                JSONObject errorJsonObj = new JSONObject(obj.getString(BapVerifyModel.KEY_ERROR));
                model.setError(ErrorDataParser(errorJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "bapVerifyParser() - failed.", tr);
        }

        return model;

    }

    public static BapIdentifyModel bapIdentifyParser(JSONObject obj) {

        if (obj == null) {
            return null;
        }

        BapIdentifyModel model = new BapIdentifyModel();

        try {
            if (!obj.isNull(BapIdentifyModel.KEY_STATUS)){
                model.setStatus(obj.getString(BapIdentifyModel.KEY_STATUS));
            }

            if (!obj.isNull(BapIdentifyModel.KEY_DATA)){
                JSONObject bapIdentifyDataJsonObj = new JSONObject(obj.getString(BapIdentifyModel.KEY_DATA));
                model.setBapIdentifyData(bapIdentifyDataParser(bapIdentifyDataJsonObj));
            }

            if (!obj.isNull(BapIdentifyModel.KEY_ERROR)){
                JSONObject errorJsonObj = new JSONObject(obj.getString(BapIdentifyModel.KEY_ERROR));
                model.setError(ErrorDataParser(errorJsonObj));
            }

        } catch (Throwable tr) {
            LOG.E(TAG, "bapIdentifyParser() - failed.", tr);
        }

        return model;

    }

    public static BapIdentifyDataModel bapIdentifyDataParser(JSONObject bapIdentifyDataJsonObj) {
        BapIdentifyDataModel bapIdentifyDataModel = new BapIdentifyDataModel();

//        try{
//            if (!bapIdentifyDataJsonObj.isNull(BapIdentifyDataModel.KEY_USER)){
//                JSONObject bapIdentifyJsonObj = new JSONObject(bapIdentifyDataJsonObj.getString(BapIdentifyDataModel.KEY_USER));
//                bapIdentifyDataModel.setAcceptancesModel(acceptancesParser(bapIdentifyJsonObj));
//            }
//
//        } catch (JSONException e) {
//            LOG.E(TAG, "registerReplyDataParser JSONException occurs, ", e);
//            return null;
//        }


        try{

            if (!bapIdentifyDataJsonObj.isNull(BapIdentifyDataModel.KEY_INT_ID))
                bapIdentifyDataModel.setIntId(bapIdentifyDataJsonObj.getInt(BapIdentifyDataModel.KEY_INT_ID));

            if (!bapIdentifyDataJsonObj.isNull(BapIdentifyDataModel.KEY_UUID))
                bapIdentifyDataModel.setUuid(bapIdentifyDataJsonObj.getString(BapIdentifyDataModel.KEY_UUID));

            if (!bapIdentifyDataJsonObj.isNull(BapIdentifyDataModel.KEY_EMPLOYEE_ID))
                bapIdentifyDataModel.setEmployeeId(bapIdentifyDataJsonObj.getString(BapIdentifyDataModel.KEY_EMPLOYEE_ID));

            if (!bapIdentifyDataJsonObj.isNull(BapIdentifyDataModel.KEY_MOBILE_NO))
                bapIdentifyDataModel.setMobileNo(bapIdentifyDataJsonObj.getString(BapIdentifyDataModel.KEY_MOBILE_NO));

            if (!bapIdentifyDataJsonObj.isNull(BapIdentifyDataModel.KEY_SECURITY_CODE))
                bapIdentifyDataModel.setSecurityCode(bapIdentifyDataJsonObj.getString(BapIdentifyDataModel.KEY_SECURITY_CODE));

            if (!bapIdentifyDataJsonObj.isNull(BapIdentifyDataModel.KEY_RFID))
                bapIdentifyDataModel.setRfid(bapIdentifyDataJsonObj.getString(BapIdentifyDataModel.KEY_RFID));

            if (!bapIdentifyDataJsonObj.isNull(BapIdentifyDataModel.KEY_FIRST_NAME))
                bapIdentifyDataModel.setFirstName(bapIdentifyDataJsonObj.getString(BapIdentifyDataModel.KEY_FIRST_NAME));

            if (!bapIdentifyDataJsonObj.isNull(BapIdentifyDataModel.KEY_LAST_NAME))
                bapIdentifyDataModel.setLastName(bapIdentifyDataJsonObj.getString(BapIdentifyDataModel.KEY_LAST_NAME));

            if (!bapIdentifyDataJsonObj.isNull(BapIdentifyDataModel.KEY_START_TIME))
                bapIdentifyDataModel.setStartTime(bapIdentifyDataJsonObj.getLong(BapIdentifyDataModel.KEY_START_TIME));

            if (!bapIdentifyDataJsonObj.isNull(BapIdentifyDataModel.KEY_END_TIME))
                bapIdentifyDataModel.setEndTime(bapIdentifyDataJsonObj.getLong(BapIdentifyDataModel.KEY_END_TIME));

            if (!bapIdentifyDataJsonObj.isNull(BapIdentifyDataModel.KEY_PHOTO_URL))
                bapIdentifyDataModel.setPhotoUrl(bapIdentifyDataJsonObj.getString(BapIdentifyDataModel.KEY_PHOTO_URL));

            if (!bapIdentifyDataJsonObj.isNull(BapIdentifyDataModel.KEY_TYPE))
                bapIdentifyDataModel.setPhotoUrl(bapIdentifyDataJsonObj.getString(BapIdentifyDataModel.KEY_TYPE));

        } catch (JSONException e) {
            LOG.E(TAG, "acceptancesParser JSONException occurs, ", e);
            return null;
        }



        return bapIdentifyDataModel;
    }


}
