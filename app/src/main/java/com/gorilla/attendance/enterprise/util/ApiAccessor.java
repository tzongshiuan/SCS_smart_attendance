package com.gorilla.attendance.enterprise.util;

import android.content.Context;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ggshao on 2017/2/7.
 */

public class ApiAccessor {
    private static final String TAG = "ApiAccessor";
    public static String SERVER_IP = "http://52.33.108.242";
    public static String FTP_IP = "52.33.108.242";

    public static String INNO_LUX_TEAM_PLUS_SERVER_IP = "http://mapp.innolux.com";
    public static String INNO_LUX_SYSTEM_SN = "68";
    public static String INNO_LUX_INVITE_API_KEY = "edfc63ba-9511-4467-a7f5-e5b333502c4b";
    public static String INNO_LUX_POST_MESSAGE_API_KEY = "d1e16b14-94f6-4711-afe7-a8cb66cfe82f";
    public static String INNO_LUX_OPERATOR = "YUNGYIN.HSU";
    public static String INNO_LUX_ACCOUNT = "va_22730fb32d39471281";
    public static String INNO_LUX_TEAM_SN = "2846";

//    public static String WEB_SOCKET_PATH = "/SmartEnterprise/WebSocketApi";
    public static String WEB_SOCKET_PATH = "/SmartEnterprise/V1_1beta/WebSocketApi";

    private static final String LOGIN_PATH = "/SmartEnterprise/api/clients/login?";
    private static final String GET_EMPLOYEE_PATH = "/SmartEnterprise/api/clients/employees?";
    private static final String GET_VISITOR_PATH = "/SmartEnterprise/api/clients/visitors?";

    private static final String GET_VERIFIED_ID_AND_IMAGE = "/SmartEnterprise/api/BAP/identities?";
    private static final String ATTENDANCE_RECORDS = "/SmartEnterprise/api/attendance/records?";
    private static final String ACCESS_RECORDS = "/SmartEnterprise/api/access/records?";
    private static final String VISIT_RECORDS = "/SmartEnterprise/api/visitor/records?";

    private static final String GET_VIDEO_PATH = "/SmartEnterprise/api/clients/videos?";
    private static final String GET_MARQUEES_PATH = "/SmartEnterprise/api/clients/marquees?";

    private static final String DEVICE_LOGIN_PATH = "/SmartEnterprise/api/V1_1beta/device/login?";
    private static final String GET_DEVICE_VIDEOS_PATH = "/SmartEnterprise/api/V1_1beta/device/videos?";
    private static final String GET_DEVICE_MARQUEES_PATH = "/SmartEnterprise/api/V1_1beta/device/marquees?";
    private static final String GET_DEVICE_VERIFIED_ID_AND_IMAGE = "/SmartEnterprise/api/V1_1beta/BAP/identities?";
    private static final String GET_DEVICE_EMPLOYEES_PATH = "/SmartEnterprise/api/V1_1beta/device/employees?";
    private static final String GET_DEVICE_VISITORS_PATH = "/SmartEnterprise/api/V1_1beta/device/visitors?";
    private static final String DEVICE_ATTENDANCE_RECORDS = "/SmartEnterprise/api/V1_1beta/attendance/records?";
    private static final String DEVICE_ACCESS_RECORDS = "/SmartEnterprise/api/V1_1beta/access/records?";
    private static final String DEVICE_VISITOR_ACCESS_RECORDS = "/SmartEnterprise/api/V1_1beta/visitor/access/records?";

    private static final String DEVICE_VISIT_RECORDS = "/SmartEnterprise/api/V1_1beta/visitor/records?";
    private static final String ATTENDANCE_UNRECOGNIZED_LOG = "/SmartEnterprise/api/V1_1beta/attendance/unrecognizedLog?";
    private static final String ACCESS_UNRECOGNIZED_LOG = "/SmartEnterprise/api/V1_1beta/access/unrecognizedLog?";
    private static final String ACCESS_VISITOR_UNRECOGNIZED_LOG = "/SmartEnterprise/api/V1_1beta/visitor/access/unrecognizedLog?";

    private static final String VISITORS_UNRECOGNIZED_LOG = "/SmartEnterprise/api/V1_1beta/visitor/unrecognizedLog?";

    private static final String SEARCH_USER_PATH = "/SmartEnterprise/api/V1_1beta/user?";
    private static final String SEARCH_EMPLOYEE_PATH = "/SmartEnterprise/api/V1_1beta/employee?";
    private static final String SEARCH_VISITOR_PATH = "/SmartEnterprise/api/V1_1beta/visitor?";

    private static final String REGISTER_EMPLOYEE_PATH = "/SmartEnterprise/api/V1_1beta/user/employee-register?";
    private static final String REGISTER_EMPLOYEE_V2_PATH = "/SmartEnterprise/api/V1_1beta/user/employee-register-v2?";

    private static final String REGISTER_VISITOR_PATH = "/SmartEnterprise/api/V1_1beta/user/visitor-register?";
    private static final String REGISTER_VISITOR_V2_PATH = "/SmartEnterprise/api/V1_1beta/user/visitor-register-v2?";

    private static final String RENEW_SECURITY_CODE_PATH = "/SmartEnterprise/api/shbdzh/user/renewSecurityCode?";


    private static final String POST_MESSAGE_LIMITED_PATH = "/teamplus_innolux/API/TeamService.ashx?";


    private static final String BAP_IDENTIFY_PATH = "/SmartEnterprise/api/V1_1beta/BAP/identify?";
    private static final String BAP_VERIFY_PATH = "/SmartEnterprise/api/V1_1beta/BAP/verify?";





    public static JSONObject deviceLogin(Context context, String deviceToken, String deviceType, String deviceIp) throws JSONException {
        //Test data
//        return null;
        if(deviceToken == null || deviceIp == null)
            return null;

        String resultJson;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));
        params.add(new BasicNameValuePair("deviceType", deviceType));
        params.add(new BasicNameValuePair("deviceIP", deviceIp));

        LOG.D(TAG, "deviceLogin SERVER_IP = " + SERVER_IP);

        HttpHandler http = new HttpHandler(SERVER_IP + DEVICE_LOGIN_PATH, params);
        resultJson = http.getResponseByGet();
        LOG.D(TAG, "deviceLogin resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }

    }

    public static JSONObject getDeviceEmployees(Context context, String deviceToken) throws JSONException {
        //  Test data
//        return null;


        if(deviceToken == null)
            return null;

        String resultJson;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));

        LOG.D(TAG, "getDeviceEmployees SERVER_IP = " + SERVER_IP);

        HttpHandler http = new HttpHandler(SERVER_IP + GET_DEVICE_EMPLOYEES_PATH, params);
        resultJson = http.getResponseByGet();
        LOG.D(TAG, "getDeviceEmployees resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }

    }

    public static JSONObject getDeviceVisitors(Context context, String deviceToken) throws JSONException {
        //  Test data
//        return null;

        if(deviceToken == null)
            return null;

        String resultJson;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));

        LOG.D(TAG, "getDeviceVisitors SERVER_IP = " + SERVER_IP);

        HttpHandler http = new HttpHandler(SERVER_IP + GET_DEVICE_VISITORS_PATH, params);
        resultJson = http.getResponseByGet();
        LOG.D(TAG, "getDeviceVisitors resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }

    }

    public static JSONObject getDeviceVerifiedIdAndImage(Context context, String deviceToken) throws JSONException {
        //  Test data
//        return null;

        String resultJson = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));

//        params.add(new BasicNameValuePair("returnType", "json"));

        LOG.D(TAG, "getDeviceVerifiedIdAndImage SERVER_IP = " + SERVER_IP);
        HttpHandler http = new HttpHandler(SERVER_IP + GET_DEVICE_VERIFIED_ID_AND_IMAGE, params);

//        resultJson = http.getResponseByPostJsonBody();
        resultJson = http.getResponseByGet();


        LOG.D(TAG, "getDeviceVerifiedIdAndImage resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }

        }

    }

    public static JSONObject deviceAttendanceRecords(Context context, String deviceToken, String recordsList) throws JSONException {
        //Test data
//        return null;

        String resultJson = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));
        params.add(new BasicNameValuePair("records", recordsList));

//        params.add(new BasicNameValuePair("returnType", "json"));

        LOG.D(TAG, "deviceAttendanceRecords SERVER_IP = " + SERVER_IP);
        HttpHandler http = new HttpHandler(SERVER_IP + DEVICE_ATTENDANCE_RECORDS, params);
        resultJson = http.getResponseByPostJsonBody();
        LOG.D(TAG, "deviceAttendanceRecords resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }
    }

    public static JSONObject deviceAccessRecords(Context context, String deviceToken, String recordsList) throws JSONException {
        //Test data
//        return null;

        String resultJson = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));
        params.add(new BasicNameValuePair("records", recordsList));

        LOG.D(TAG, "deviceAccessRecords SERVER_IP = " + SERVER_IP);
        HttpHandler http = new HttpHandler(SERVER_IP + DEVICE_ACCESS_RECORDS, params);
        resultJson = http.getResponseByPostJsonBody();
        LOG.D(TAG, "deviceAccessRecords resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }

    }

    public static JSONObject deviceVisitorAccessRecords(Context context, String deviceToken, String recordsList) throws JSONException {
        //Test data
//        return null;

        String resultJson = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));
        params.add(new BasicNameValuePair("records", recordsList));

        LOG.D(TAG, "deviceVisitorAccessRecords SERVER_IP = " + SERVER_IP);
        HttpHandler http = new HttpHandler(SERVER_IP + DEVICE_VISITOR_ACCESS_RECORDS, params);
        resultJson = http.getResponseByPostJsonBody();
        LOG.D(TAG, "deviceVisitorAccessRecords resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }

    }

    public static JSONObject deviceVisitRecords(Context context, String deviceToken, String recordsList) throws JSONException {
        //Test data
//        return null;

        String resultJson = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        JSONArray recodsJsonArray = new JSONArray(recordsList);

        params.add(new BasicNameValuePair("deviceToken", deviceToken));
        params.add(new BasicNameValuePair("records", recordsList));

//        params.add(new BasicNameValuePair("returnType", "json"));

        LOG.D(TAG, "deviceVisitRecords SERVER_IP = " + SERVER_IP);
        LOG.D(TAG, "deviceVisitRecords recordsList = " + recordsList);
        HttpHandler http = new HttpHandler(SERVER_IP + DEVICE_VISIT_RECORDS, params);
        resultJson = http.getResponseByPostJsonBody();
        LOG.D(TAG, "deviceVisitRecords resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }

    }

    public static JSONObject attendanceUnrecognizedLog(Context context, String deviceToken, String errorLogsList) throws JSONException {
        //Test data
//        return null;

        String resultJson = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));
        params.add(new BasicNameValuePair("errorLogs", errorLogsList));

//        params.add(new BasicNameValuePair("returnType", "json"));

        LOG.D(TAG, "attendanceUnrecognizedLog SERVER_IP = " + SERVER_IP + " errorLogs = " + errorLogsList);
        HttpHandler http = new HttpHandler(SERVER_IP + ATTENDANCE_UNRECOGNIZED_LOG, params);
        resultJson = http.getResponseByPostJsonBody();
        LOG.D(TAG, "attendanceUnrecognizedLog resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }

    }

    public static JSONObject accessUnrecognizedLog(Context context, String deviceToken, String errorLogsList) throws JSONException {
        //Test data
//        return null;

        String resultJson = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));
        params.add(new BasicNameValuePair("errorLogs", errorLogsList));

//        params.add(new BasicNameValuePair("returnType", "json"));

        LOG.D(TAG, "accessUnrecognizedLog SERVER_IP = " + SERVER_IP + " errorLogs = " + errorLogsList);

        HttpHandler http = new HttpHandler(SERVER_IP + ACCESS_UNRECOGNIZED_LOG, params);
        resultJson = http.getResponseByPostJsonBody();
        LOG.D(TAG, "accessUnrecognizedLog resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }

    }

    public static JSONObject accessVisitorUnrecognizedLog(Context context, String deviceToken, String errorLogsList) throws JSONException {
        //Test data
//        return null;

        String resultJson = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));
        params.add(new BasicNameValuePair("errorLogs", errorLogsList));

//        params.add(new BasicNameValuePair("returnType", "json"));

        LOG.D(TAG, "accessVisitorUnrecognizedLog SERVER_IP = " + SERVER_IP);
        HttpHandler http = new HttpHandler(SERVER_IP + ACCESS_VISITOR_UNRECOGNIZED_LOG, params);
        resultJson = http.getResponseByPostJsonBody();
        LOG.D(TAG, "accessVisitorUnrecognizedLog resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }

    }



    public static JSONObject visitorsUnrecognizedLog(Context context, String deviceToken, String errorLogsList) throws JSONException {
        //  Test data
//        return null;

        String resultJson = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));
        params.add(new BasicNameValuePair("errorLogs", errorLogsList));

//        params.add(new BasicNameValuePair("returnType", "json"));

        LOG.D(TAG, "visitorsUnrecognizedLog SERVER_IP = " + SERVER_IP);
        HttpHandler http = new HttpHandler(SERVER_IP + VISITORS_UNRECOGNIZED_LOG, params);
        resultJson = http.getResponseByPostJsonBody();
        LOG.D(TAG, "visitorsUnrecognizedLog resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }

    }

    public static JSONObject getDeviceVideos(Context context, String deviceToken) throws JSONException {
        //  Test data
//        return null;

        if(deviceToken == null)
            return null;

        String resultJson;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));

        LOG.D(TAG, "getDeviceVideos SERVER_IP = " + SERVER_IP);

        HttpHandler http = new HttpHandler(SERVER_IP + GET_DEVICE_VIDEOS_PATH, params);
        resultJson = http.getResponseByGet();
        LOG.D(TAG, "getDeviceVideos resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }

    }

    public static JSONObject getDeviceMarquees(Context context, String deviceToken) throws JSONException {
        //  Test data
//        return null;


        if(deviceToken == null)
            return null;

        String resultJson;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));

        LOG.D(TAG, "getDeviceMarquees SERVER_IP = " + SERVER_IP);

        HttpHandler http = new HttpHandler(SERVER_IP + GET_DEVICE_MARQUEES_PATH, params);
        resultJson = http.getResponseByGet();
        LOG.D(TAG, "getDeviceMarquees resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }

    }

    public static JSONObject searchUser(Context context, String deviceToken, String type, String securityCode, String rfid) throws JSONException {
        //  Test data
//        return null;


        if(deviceToken == null)
            return null;

        String resultJson;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));
        params.add(new BasicNameValuePair("type", type));
        if(securityCode != null){
            params.add(new BasicNameValuePair("securityCode", securityCode));
        }

        if(rfid != null){
            params.add(new BasicNameValuePair("rfid", rfid));
        }

        LOG.D(TAG, "searchUser SERVER_IP = " + SERVER_IP);

        HttpHandler http = new HttpHandler(SERVER_IP + SEARCH_USER_PATH, params);
        resultJson = http.getResponseByGet();
        LOG.D(TAG, "searchUser resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }

    }

    public static JSONObject searchEmployee(Context context, String deviceToken, String employeeId) throws JSONException {
        //  Test data
//        return null;


        if(deviceToken == null)
            return null;

        String resultJson;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));
        params.add(new BasicNameValuePair("employeeId", employeeId));

        LOG.D(TAG, "searchEmployee SERVER_IP = " + SERVER_IP);

        HttpHandler http = new HttpHandler(SERVER_IP + SEARCH_EMPLOYEE_PATH, params);
        resultJson = http.getResponseByGet();
        LOG.D(TAG, "searchEmployee resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }

    }

    public static JSONObject searchVisitor(Context context, String deviceToken, String mobileNo) throws JSONException {
        //  Test data
//        return null;


        if(deviceToken == null)
            return null;

        String resultJson;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));
        params.add(new BasicNameValuePair("mobileNo", mobileNo));

        LOG.D(TAG, "searchVisitor SERVER_IP = " + SERVER_IP);

        HttpHandler http = new HttpHandler(SERVER_IP + SEARCH_VISITOR_PATH, params);
        resultJson = http.getResponseByGet();
        LOG.D(TAG, "searchVisitor resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }

    }

    public static JSONObject registerEmployee(Context context, String deviceToken, String employeeId, String name, String email, String password,
                                              String createTime, String imageFormat, String imageList) throws JSONException {
        //Test data
//        return null;

        String resultJson = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));
        params.add(new BasicNameValuePair("employeeId", employeeId));

        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("createTime", createTime));
//        params.add(new BasicNameValuePair("imageFormat", imageFormat));
        params.add(new BasicNameValuePair("imageList", imageList));


//        params.add(new BasicNameValuePair("returnType", "json"));

        LOG.D(TAG, "registerEmployee SERVER_IP = " + SERVER_IP);
        LOG.D(TAG, "registerEmployee name = " + name);

        HttpHandler http = new HttpHandler(SERVER_IP + REGISTER_EMPLOYEE_PATH, params);
        resultJson = http.getResponseByPostJsonBody();
        LOG.D(TAG, "registerEmployee resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }
    }

    public static JSONObject registerEmployeeV2(Context context, String deviceToken, String employeeId, String name, String email, String password,
                                              String securityCode, String createTime, String imageFormat, String imageList) throws JSONException {
        //Test data
//        return null;

        String resultJson = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));
        params.add(new BasicNameValuePair("employeeId", employeeId));

        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("securityCode", securityCode));
        params.add(new BasicNameValuePair("createTime", createTime));
//        params.add(new BasicNameValuePair("imageFormat", imageFormat));
        params.add(new BasicNameValuePair("imageList", imageList));


//        params.add(new BasicNameValuePair("returnType", "json"));

        LOG.D(TAG, "registerEmployeeV2 SERVER_IP = " + SERVER_IP);
        LOG.D(TAG, "registerEmployeeV2 name = " + name);

        HttpHandler http = new HttpHandler(SERVER_IP + REGISTER_EMPLOYEE_V2_PATH, params);
        resultJson = http.getResponseByPostJsonBody();
        LOG.D(TAG, "registerEmployeeV2 resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }
    }

    public static JSONObject registerEmployeeV2Rfid(Context context, String deviceToken, String employeeId, String name, String email, String password,
                                                String securityCode, String createTime, String imageFormat, String imageList, String rfid) throws JSONException {
        //Test data
//        return null;

        String resultJson = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));
        params.add(new BasicNameValuePair("employeeId", employeeId));

        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("securityCode", securityCode));
        params.add(new BasicNameValuePair("createTime", createTime));
//        params.add(new BasicNameValuePair("imageFormat", imageFormat));
        params.add(new BasicNameValuePair("imageList", imageList));
        params.add(new BasicNameValuePair("rfid", rfid));

//        params.add(new BasicNameValuePair("returnType", "json"));

        LOG.D(TAG, "registerEmployeeV2Rfid SERVER_IP = " + SERVER_IP);
        LOG.D(TAG, "registerEmployeeV2Rfid name = " + name);

        HttpHandler http = new HttpHandler(SERVER_IP + REGISTER_EMPLOYEE_V2_PATH, params);
        resultJson = http.getResponseByPostJsonBody();
        LOG.D(TAG, "registerEmployeeV2Rfid resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }
    }


    public static JSONObject registerVisitor(Context context, String deviceToken, String mobileNo, String name, String department, String title,
                                             String createTime, String imageFormat, String imageList) throws JSONException {
        //Test data
//        return null;

        String resultJson = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));
        params.add(new BasicNameValuePair("mobileNo", mobileNo));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("department", department));
        params.add(new BasicNameValuePair("title", title));
        params.add(new BasicNameValuePair("createTime", createTime));
//        params.add(new BasicNameValuePair("imageFormat", imageFormat));
        params.add(new BasicNameValuePair("imageList", imageList));


//        params.add(new BasicNameValuePair("returnType", "json"));

        LOG.D(TAG, "registerVisitor SERVER_IP = " + SERVER_IP);
        HttpHandler http = new HttpHandler(SERVER_IP + REGISTER_VISITOR_PATH, params);
        resultJson = http.getResponseByPostJsonBody();
        LOG.D(TAG, "registerVisitor resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }
    }

    public static JSONObject registerVisitorEmail(Context context, String deviceToken, String mobileNo, String name, String department, String title,
                                             String createTime, String imageFormat, String imageList, String email) throws JSONException {
        //Test data
//        return null;

        String resultJson = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));
        params.add(new BasicNameValuePair("mobileNo", mobileNo));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("department", department));
        params.add(new BasicNameValuePair("title", title));
        params.add(new BasicNameValuePair("createTime", createTime));
        params.add(new BasicNameValuePair("email", email));
//        params.add(new BasicNameValuePair("imageFormat", imageFormat));
        params.add(new BasicNameValuePair("imageList", imageList));



//        params.add(new BasicNameValuePair("returnType", "json"));

        LOG.D(TAG, "registerVisitorEmail SERVER_IP = " + SERVER_IP);
        LOG.D(TAG, "registerVisitorEmail email = " + email);
        HttpHandler http = new HttpHandler(SERVER_IP + REGISTER_VISITOR_PATH, params);
        resultJson = http.getResponseByPostJsonBody();
        LOG.D(TAG, "registerVisitorEmail resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }
    }

    public static JSONObject registerVisitorV2(Context context, String deviceToken, String mobileNo, String name, String company, String title,
                                                  String createTime, String imageFormat, String imageList, String email, String securityCode) throws JSONException {
        //Test data
//        return null;

        String resultJson = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));
        params.add(new BasicNameValuePair("mobileNo", mobileNo));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("company", company));
        params.add(new BasicNameValuePair("title", title));
        params.add(new BasicNameValuePair("createTime", createTime));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("securityCode", securityCode));
//        params.add(new BasicNameValuePair("imageFormat", imageFormat));
        params.add(new BasicNameValuePair("imageList", imageList));



//        params.add(new BasicNameValuePair("returnType", "json"));

        LOG.D(TAG, "registerVisitorV2 SERVER_IP = " + SERVER_IP);
        LOG.D(TAG, "registerVisitorV2 email = " + email);
        HttpHandler http = new HttpHandler(SERVER_IP + REGISTER_VISITOR_V2_PATH, params);
        resultJson = http.getResponseByPostJsonBody();
        LOG.D(TAG, "registerVisitorV2 resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }
    }

    public static JSONObject registerVisitorV2Rfid(Context context, String deviceToken, String mobileNo, String name, String company, String title,
                                               String createTime, String imageFormat, String imageList, String email, String securityCode, String rfid) throws JSONException {
        //Test data
//        return null;

        String resultJson = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));
        params.add(new BasicNameValuePair("mobileNo", mobileNo));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("company", company));
        params.add(new BasicNameValuePair("title", title));
        params.add(new BasicNameValuePair("createTime", createTime));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("securityCode", securityCode));
//        params.add(new BasicNameValuePair("imageFormat", imageFormat));
        params.add(new BasicNameValuePair("imageList", imageList));
        params.add(new BasicNameValuePair("rfid", rfid));




//        params.add(new BasicNameValuePair("returnType", "json"));

        LOG.D(TAG, "registerVisitorV2Rfid SERVER_IP = " + SERVER_IP);
        LOG.D(TAG, "registerVisitorV2Rfid email = " + email);
        HttpHandler http = new HttpHandler(SERVER_IP + REGISTER_VISITOR_V2_PATH, params);
        resultJson = http.getResponseByPostJsonBody();
        LOG.D(TAG, "registerVisitorV2Rfid resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }
    }


    public static JSONObject renewSecurityCode(Context context, String deviceToken, String type, String securityCode) throws JSONException {
        //Test data
//        return null;

        String resultJson = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("deviceToken", deviceToken));
        params.add(new BasicNameValuePair("type", type));
        params.add(new BasicNameValuePair("securityCode", securityCode));



        LOG.D(TAG, "renewSecurityCode SERVER_IP = " + SERVER_IP);
        HttpHandler http = new HttpHandler(SERVER_IP + RENEW_SECURITY_CODE_PATH, params);
        resultJson = http.getResponseByPostJsonBody();
        LOG.D(TAG, "renewSecurityCode resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }
    }


    public static JSONObject postMessageLimited(Context context, String account, String apiKey, String teamSn,
                                                String contentType, String textContent, String mediaContent,
                                                String fileShowName, String subject, String accountList) throws JSONException {
        //Test data
//        return null;

        String resultJson = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("ask", "postMessageLimited"));
        params.add(new BasicNameValuePair("account", account));
        params.add(new BasicNameValuePair("api_Key", apiKey));
        params.add(new BasicNameValuePair("team_sn", teamSn));
        params.add(new BasicNameValuePair("content_type", contentType));
        params.add(new BasicNameValuePair("text_content", textContent));
        params.add(new BasicNameValuePair("media_content", mediaContent));
        params.add(new BasicNameValuePair("file_show_name", fileShowName));
        params.add(new BasicNameValuePair("subject", subject));
        params.add(new BasicNameValuePair("account_list", accountList));



        LOG.D(TAG, "postMessageLimited SERVER_IP = " + SERVER_IP);
        HttpHandler http = new HttpHandler(INNO_LUX_TEAM_PLUS_SERVER_IP + POST_MESSAGE_LIMITED_PATH, params);
        resultJson = http.getResponseByPostUrl();
//        resultJson = http.getResponseByGet();
        LOG.D(TAG, "postMessageLimited resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }
    }


    /******************************** Online Mode***********************************************/
    public static JSONObject bapIdentify(Context context, String type, String imageList) throws JSONException {
        //Test data
//        return null;

        String resultJson = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

//        params.add(new BasicNameValuePair("type", type));
        params.add(new BasicNameValuePair("type", type));
        params.add(new BasicNameValuePair("imageList", imageList));

//        params.add(new BasicNameValuePair("returnType", "json"));

        LOG.D(TAG, "bapIdentify type = " + type);
        HttpHandler http = new HttpHandler(SERVER_IP + BAP_IDENTIFY_PATH, params);
        resultJson = http.getResponseByPostJsonBody();
        LOG.D(TAG, "bapIdentify resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }
    }

    public static JSONObject bapVerify(Context context, String id, String type, String imageList) throws JSONException {
        //Test data
//        return null;

        String resultJson = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("type", type));
        params.add(new BasicNameValuePair("imageList", imageList));

        LOG.D(TAG, "bapVerify type = " + type);
        HttpHandler http = new HttpHandler(SERVER_IP + BAP_VERIFY_PATH, params);
        resultJson = http.getResponseByPostJsonBody();
        LOG.D(TAG, "bapVerify resultJson = " + resultJson);
        if(resultJson == null){
            return null;
        }else{
            if(resultJson.isEmpty()){
                return null;
            }else{
                JSONObject result = new JSONObject(resultJson);
                return result;
            }
        }
    }



}
