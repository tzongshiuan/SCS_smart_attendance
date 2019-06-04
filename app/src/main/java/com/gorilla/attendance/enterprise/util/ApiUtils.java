package com.gorilla.attendance.enterprise.util;

import android.content.Context;
import android.os.AsyncTask;

import com.gorilla.attendance.enterprise.util.apitask.AccessUnrecognizedLogFromDbTask;
import com.gorilla.attendance.enterprise.util.apitask.AccessUnrecognizedLogTask;
import com.gorilla.attendance.enterprise.util.apitask.AccessVisitorUnrecognizedLogTask;
import com.gorilla.attendance.enterprise.util.apitask.AttendanceUnrecognizedLogFromDbTask;
import com.gorilla.attendance.enterprise.util.apitask.AttendanceUnrecognizedLogTask;
import com.gorilla.attendance.enterprise.util.apitask.BapIdentifyTask;
import com.gorilla.attendance.enterprise.util.apitask.BapVerifyTask;
import com.gorilla.attendance.enterprise.util.apitask.DeviceAccessRecordsFromDbTask;
import com.gorilla.attendance.enterprise.util.apitask.DeviceAccessRecordsTask;
import com.gorilla.attendance.enterprise.util.apitask.DeviceAttendanceAccessRecordsTask;
import com.gorilla.attendance.enterprise.util.apitask.DeviceAttendanceRecordsFromDbTask;
import com.gorilla.attendance.enterprise.util.apitask.DeviceAttendanceRecordsTask;
import com.gorilla.attendance.enterprise.util.apitask.DeviceLoginTask;
import com.gorilla.attendance.enterprise.util.apitask.DeviceVisitorAccessRecordsFromDbTask;
import com.gorilla.attendance.enterprise.util.apitask.DeviceVisitorAccessRecordsTask;
import com.gorilla.attendance.enterprise.util.apitask.DeviceVisitorAttendanceRecordsFromDbTask;
import com.gorilla.attendance.enterprise.util.apitask.DeviceVisitorRecordsTask;
import com.gorilla.attendance.enterprise.util.apitask.GetDeviceEmployeesTask;
import com.gorilla.attendance.enterprise.util.apitask.GetDeviceMarqueesTask;
import com.gorilla.attendance.enterprise.util.apitask.GetDeviceVerifiedIdAndImageTask;
import com.gorilla.attendance.enterprise.util.apitask.GetDeviceVideosTask;
import com.gorilla.attendance.enterprise.util.apitask.GetDeviceVisitorsTask;
import com.gorilla.attendance.enterprise.util.apitask.PostMessageLimitedTask;
import com.gorilla.attendance.enterprise.util.apitask.RegisterEmployeeFromDbTask;
import com.gorilla.attendance.enterprise.util.apitask.RegisterEmployeeTask;
import com.gorilla.attendance.enterprise.util.apitask.RegisterEmployeeV2RfidTask;
import com.gorilla.attendance.enterprise.util.apitask.RegisterEmployeeV2Task;
import com.gorilla.attendance.enterprise.util.apitask.RegisterVisitorEmailTask;
import com.gorilla.attendance.enterprise.util.apitask.RegisterVisitorFromDbTask;
import com.gorilla.attendance.enterprise.util.apitask.RegisterVisitorTask;
import com.gorilla.attendance.enterprise.util.apitask.RegisterVisitorV2RfidTask;
import com.gorilla.attendance.enterprise.util.apitask.RegisterVisitorV2Task;
import com.gorilla.attendance.enterprise.util.apitask.RenewSecurityCodeTask;
import com.gorilla.attendance.enterprise.util.apitask.SearchEmployeeTask;
import com.gorilla.attendance.enterprise.util.apitask.SearchUserTask;
import com.gorilla.attendance.enterprise.util.apitask.SearchVisitorTask;
import com.gorilla.attendance.enterprise.util.apitask.VisitorsAttendanceUnrecognizedLogFromDbTask;
import com.gorilla.attendance.enterprise.util.apitask.VisitorsUnrecognizedLogTask;
import com.gorilla.attendance.enterprise.util.apitask.listener.IAccessUnrecognizedLogFromDbListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IAccessUnrecognizedLogListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IAccessVisitorUnrecognizedLogListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IAttendanceUnrecognizedLogFromDbListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IAttendanceUnrecognizedLogListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IBapIdentifyListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IBapVerifyListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceAccessRecordsFromDbListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceAccessRecordsListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceAttendanceAccessRecordsListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceAttendanceRecordsFromDbListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceAttendanceRecordsListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceLoginListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceVisitorAccessRecordsFromDbListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceVisitorAccessRecordsListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceVisitorAttendanceRecordsFromDbListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceVisitorRecordsListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IGetDeviceEmployeesListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IGetDeviceMarqueesListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IGetDeviceVerifiedIdAndImageListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IGetDeviceVideosListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IGetDeviceVisitorsListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IPostMessageLimitedListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IRegisterEmployeeFromDbListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IRegisterEmployeeListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IRegisterEmployeeV2Listener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IRegisterVisitorEmailListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IRegisterVisitorFromDbListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IRegisterVisitorListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IRegisterVisitorV2Listener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IRenewSecurityCodeListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.ISearchEmployeeListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.ISearchUserListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.ISearchVisitorListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IVisitorAttendanceUnrecognizedLogFromDbListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IVisitorsUnrecognizedLogListener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by ggshao on 2017/2/9.
 */

public class ApiUtils {
    public static final String TAG = "ApiUtils";

    public static class TagAsyncTask {
        String tag;
        AsyncTask task;

        public TagAsyncTask(String tag, AsyncTask task)
        {
            this.tag = tag;
            this.task = task;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public AsyncTask getTask() {
            return task;
        }

        public void setTask(AsyncTask task) {
            this.task = task;
        }

    }

    private static ArrayList<TagAsyncTask> taskList;

    static {
        if(taskList == null)
            taskList = new ArrayList<TagAsyncTask>();
    }

    public static void addTagTask(String tag, AsyncTask task)
    {
        TagAsyncTask tagTask = new TagAsyncTask(tag, task);
        synchronized(taskList)
        {
            taskList.add(tagTask);
        }
    }

    public static void cancelTaskByTag(String tag)
    {
        Iterator<TagAsyncTask> iter = taskList.iterator();
        synchronized(taskList)
        {
            while (iter.hasNext()) {
                TagAsyncTask task = iter.next();

                if(tag.equals(task.getTag()))
                {
                    if (task.getTask() != null)
                        task.getTask().cancel(true);
                    iter.remove();
                }
            }
        }
    }

    public static DeviceLoginTask deviceLogin(String tag, Context context, String deviceToken, String deviceType, String deviceIp, IDeviceLoginListener callback)
    {
        DeviceLoginTask task = new DeviceLoginTask();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, deviceType, deviceIp, callback);
        addTagTask(tag, task);
        return task;
    }

    public static GetDeviceEmployeesTask getDeviceEmployees(String tag, Context context, String deviceToken, IGetDeviceEmployeesListener callback)
    {
        GetDeviceEmployeesTask task = new GetDeviceEmployeesTask();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, callback);
        addTagTask(tag, task);
        return task;
    }

    public static GetDeviceVisitorsTask getDeviceVisitors(String tag, Context context, String deviceToken, IGetDeviceVisitorsListener callback)
    {
        GetDeviceVisitorsTask task = new GetDeviceVisitorsTask();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, callback);
        addTagTask(tag, task);
        return task;
    }

    public static GetDeviceVerifiedIdAndImageTask getDeviceVerifiedIdAndImage(String tag, Context context, String deviceToken, IGetDeviceVerifiedIdAndImageListener callback)
    {
        GetDeviceVerifiedIdAndImageTask task = new GetDeviceVerifiedIdAndImageTask();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, callback);
        addTagTask(tag, task);
        return task;
    }

    public static DeviceAttendanceRecordsTask deviceAttendanceRecords(String tag, Context context, String deviceToken, boolean isPassDbData, String type, int serialNumber, IDeviceAttendanceRecordsListener callback)
    {
        DeviceAttendanceRecordsTask task = new DeviceAttendanceRecordsTask();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, isPassDbData, type, serialNumber, callback);
        addTagTask(tag, task);
        return task;
    }

    public static DeviceAttendanceRecordsFromDbTask deviceAttendanceRecordsFromDb(String tag, Context context, String deviceToken, IDeviceAttendanceRecordsFromDbListener callback)
    {
        DeviceAttendanceRecordsFromDbTask task = new DeviceAttendanceRecordsFromDbTask();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, callback);
        addTagTask(tag, task);
        return task;
    }

    public static DeviceAttendanceAccessRecordsTask deviceAttendanceAccessRecords(String tag, Context context, String deviceToken, boolean isPassDbData, String attendanceType, int attendanceSerialNumber, String accessType, int accessSerialNumber, IDeviceAttendanceAccessRecordsListener callback)
    {
        DeviceAttendanceAccessRecordsTask task = new DeviceAttendanceAccessRecordsTask();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, isPassDbData, attendanceType, attendanceSerialNumber, accessType, accessSerialNumber, callback);
        addTagTask(tag, task);
        return task;
    }




    public static DeviceAccessRecordsTask deviceAccessRecords(String tag, Context context, String clientName, boolean isPassDbData, String type, int serialNumber, IDeviceAccessRecordsListener callback)
    {
        DeviceAccessRecordsTask task = new DeviceAccessRecordsTask();
        EnterpriseUtils.executeAsyncTask(task, context, clientName, isPassDbData, type, serialNumber, callback);
        addTagTask(tag, task);
        return task;
    }

    public static DeviceAccessRecordsFromDbTask deviceAccessRecordsFromDb(String tag, Context context, String deviceToken, IDeviceAccessRecordsFromDbListener callback)
    {
        DeviceAccessRecordsFromDbTask task = new DeviceAccessRecordsFromDbTask();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, callback);
        addTagTask(tag, task);
        return task;
    }

    public static DeviceVisitorAccessRecordsTask deviceVisitorAccessRecords(String tag, Context context, String clientName, boolean isPassDbData, String type, int serialNumber, IDeviceVisitorAccessRecordsListener callback)
    {
        DeviceVisitorAccessRecordsTask task = new DeviceVisitorAccessRecordsTask();
        EnterpriseUtils.executeAsyncTask(task, context, clientName, isPassDbData, type, serialNumber, callback);
        addTagTask(tag, task);
        return task;
    }

    public static DeviceVisitorAccessRecordsFromDbTask deviceVisitorAccessRecordsFromDb(String tag, Context context, String clientName, IDeviceVisitorAccessRecordsFromDbListener callback)
    {
        DeviceVisitorAccessRecordsFromDbTask task = new DeviceVisitorAccessRecordsFromDbTask();
        EnterpriseUtils.executeAsyncTask(task, context, clientName, callback);
        addTagTask(tag, task);
        return task;
    }

    public static DeviceVisitorRecordsTask deviceVisitorRecords(String tag, Context context, String clientName, boolean isPassDbData, IDeviceVisitorRecordsListener callback)
    {
        DeviceVisitorRecordsTask task = new DeviceVisitorRecordsTask();
        EnterpriseUtils.executeAsyncTask(task, context, clientName, isPassDbData, callback);
        addTagTask(tag, task);
        return task;
    }

    public static DeviceVisitorAttendanceRecordsFromDbTask deviceVisitorAttendanceRecordsFromDb(String tag, Context context, String clientName, IDeviceVisitorAttendanceRecordsFromDbListener callback)
    {
        DeviceVisitorAttendanceRecordsFromDbTask task = new DeviceVisitorAttendanceRecordsFromDbTask();
        EnterpriseUtils.executeAsyncTask(task, context, clientName, callback);
        addTagTask(tag, task);
        return task;
    }


    public static AttendanceUnrecognizedLogTask attendanceUnrecognizedLog(String tag, Context context, String clientName, boolean isPassDbData, IAttendanceUnrecognizedLogListener callback)
    {
        AttendanceUnrecognizedLogTask task = new AttendanceUnrecognizedLogTask();
        EnterpriseUtils.executeAsyncTask(task, context, clientName, isPassDbData, callback);
        addTagTask(tag, task);
        return task;
    }

    public static AttendanceUnrecognizedLogFromDbTask attendanceUnrecognizedLogFromDb(String tag, Context context, String clientName, IAttendanceUnrecognizedLogFromDbListener callback)
    {
        AttendanceUnrecognizedLogFromDbTask task = new AttendanceUnrecognizedLogFromDbTask();
        EnterpriseUtils.executeAsyncTask(task, context, clientName, callback);
        addTagTask(tag, task);
        return task;
    }


    public static AccessUnrecognizedLogTask accessUnrecognizedLog(String tag, Context context, String clientName, boolean isPassDbData, IAccessUnrecognizedLogListener callback)
    {
        AccessUnrecognizedLogTask task = new AccessUnrecognizedLogTask();
        EnterpriseUtils.executeAsyncTask(task, context, clientName, isPassDbData, callback);
        addTagTask(tag, task);
        return task;
    }

    public static AccessUnrecognizedLogFromDbTask accessUnrecognizedLogFromDb(String tag, Context context, String clientName, IAccessUnrecognizedLogFromDbListener callback)
    {
        AccessUnrecognizedLogFromDbTask task = new AccessUnrecognizedLogFromDbTask();
        EnterpriseUtils.executeAsyncTask(task, context, clientName, callback);
        addTagTask(tag, task);
        return task;
    }

    public static AccessVisitorUnrecognizedLogTask accessVisitorUnrecognizedLog(String tag, Context context, String clientName, boolean isPassDbData, IAccessVisitorUnrecognizedLogListener callback)
    {
        AccessVisitorUnrecognizedLogTask task = new AccessVisitorUnrecognizedLogTask();
        EnterpriseUtils.executeAsyncTask(task, context, clientName, isPassDbData, callback);
        addTagTask(tag, task);
        return task;
    }

    public static VisitorsUnrecognizedLogTask visitorsUnrecognizedLog(String tag, Context context, String clientName, boolean isPassDbData, IVisitorsUnrecognizedLogListener callback)
    {
        VisitorsUnrecognizedLogTask task = new VisitorsUnrecognizedLogTask();
        EnterpriseUtils.executeAsyncTask(task, context, clientName, isPassDbData, callback);
        addTagTask(tag, task);
        return task;
    }

    public static VisitorsAttendanceUnrecognizedLogFromDbTask visitorAttendanceUnrecognizedLogFromDb(String tag, Context context, String clientName, IVisitorAttendanceUnrecognizedLogFromDbListener callback)
    {
        VisitorsAttendanceUnrecognizedLogFromDbTask task = new VisitorsAttendanceUnrecognizedLogFromDbTask();
        EnterpriseUtils.executeAsyncTask(task, context, clientName, callback);
        addTagTask(tag, task);
        return task;
    }


    public static GetDeviceVideosTask getDeviceVideos(String tag, Context context, String deviceToken, boolean isFromWebSocket, IGetDeviceVideosListener callback)
    {
        GetDeviceVideosTask task = new GetDeviceVideosTask();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, isFromWebSocket, callback);
        addTagTask(tag, task);
        return task;
    }

    public static GetDeviceMarqueesTask getDeviceMarquees(String tag, Context context, String deviceToken, IGetDeviceMarqueesListener callback)
    {
        GetDeviceMarqueesTask task = new GetDeviceMarqueesTask();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, callback);
        addTagTask(tag, task);
        return task;
    }

    public static SearchUserTask searchUser(String tag, Context context, String deviceToken, String type, String securityCode, String rfid, ISearchUserListener callback)
    {
        SearchUserTask task = new SearchUserTask();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, type, securityCode, rfid, callback);
        addTagTask(tag, task);
        return task;
    }

    public static SearchEmployeeTask searchEmployee(String tag, Context context, String deviceToken, String employeeId, ISearchEmployeeListener callback)
    {
        SearchEmployeeTask task = new SearchEmployeeTask();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, employeeId, callback);
        addTagTask(tag, task);
        return task;
    }

    public static SearchVisitorTask searchVisitor(String tag, Context context, String deviceToken, String mobileNo, ISearchVisitorListener callback)
    {
        SearchVisitorTask task = new SearchVisitorTask();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, mobileNo, callback);
        addTagTask(tag, task);
        return task;
    }

    public static RegisterEmployeeTask registerEmployee(String tag, Context context, String deviceToken, String employeeId, String name, String email, String password,
                                                  String createTime, String imageFormat, byte[] imageList, IRegisterEmployeeListener callback)
    {
        RegisterEmployeeTask task = new RegisterEmployeeTask();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, employeeId, name, email, password, createTime, imageFormat, imageList, callback);
        addTagTask(tag, task);
        return task;
    }

    public static RegisterEmployeeV2Task registerEmployeeV2(String tag, Context context, String deviceToken, String employeeId, String name, String email, String password,
                                                        String securityCode, String createTime, String imageFormat, byte[] imageList, IRegisterEmployeeV2Listener callback)
    {
        RegisterEmployeeV2Task task = new RegisterEmployeeV2Task();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, employeeId, name, email, password, securityCode, createTime, imageFormat, imageList, callback);
        addTagTask(tag, task);
        return task;
    }

    public static RegisterEmployeeV2RfidTask registerEmployeeV2Rfid(String tag, Context context, String deviceToken, String employeeId, String name, String email, String password,
                                                            String securityCode, String createTime, String imageFormat, byte[] imageList, String rfid, IRegisterEmployeeV2Listener callback)
    {
        RegisterEmployeeV2RfidTask task = new RegisterEmployeeV2RfidTask();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, employeeId, name, email, password, securityCode, createTime, imageFormat, imageList, rfid, callback);
        addTagTask(tag, task);
        return task;
    }

    public static RegisterVisitorTask registerVisitor(String tag, Context context, String deviceToken, String mobileNo, String name, String department, String title,
                                                        String createTime, String imageFormat, byte[] imageList, IRegisterVisitorListener callback)
    {
        RegisterVisitorTask task = new RegisterVisitorTask();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, mobileNo, name, department, title, createTime, imageFormat, imageList, callback);
        addTagTask(tag, task);
        return task;
    }

    public static RegisterVisitorEmailTask registerVisitorEmail(String tag, Context context, String deviceToken, String mobileNo, String name, String department, String title,
                                                      String createTime, String imageFormat, byte[] imageList, String email, IRegisterVisitorEmailListener callback)
    {
        RegisterVisitorEmailTask task = new RegisterVisitorEmailTask();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, mobileNo, name, department, title, createTime, imageFormat, imageList, email, callback);
        addTagTask(tag, task);
        return task;
    }

    public static RegisterVisitorV2Task registerVisitorV2(String tag, Context context, String deviceToken, String mobileNo, String name, String company, String title,
                                                          String createTime, String imageFormat, byte[] imageList, String email, String securityCode,
                                                          IRegisterVisitorV2Listener callback)
    {
        RegisterVisitorV2Task task = new RegisterVisitorV2Task();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, mobileNo, name, company, title, createTime, imageFormat, imageList, email, securityCode, callback);
        addTagTask(tag, task);
        return task;
    }

    public static RegisterVisitorV2RfidTask registerVisitorV2Rfid(String tag, Context context, String deviceToken, String mobileNo, String name, String company, String title,
                                                          String createTime, String imageFormat, byte[] imageList, String email, String securityCode,
                                                                  String rfid, IRegisterVisitorV2Listener callback)
    {
        RegisterVisitorV2RfidTask task = new RegisterVisitorV2RfidTask();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, mobileNo, name, company, title, createTime, imageFormat, imageList,
                email, securityCode, rfid, callback);
        addTagTask(tag, task);
        return task;
    }

    public static RegisterEmployeeFromDbTask registerEmployeeFromDb(String tag, Context context, IRegisterEmployeeFromDbListener callback)
    {
        RegisterEmployeeFromDbTask task = new RegisterEmployeeFromDbTask();
        EnterpriseUtils.executeAsyncTask(task, context, callback);
        addTagTask(tag, task);
        return task;
    }

    public static RegisterVisitorFromDbTask registerVisitorFromDb(String tag, Context context, IRegisterVisitorFromDbListener callback)
    {
        RegisterVisitorFromDbTask task = new RegisterVisitorFromDbTask();
        EnterpriseUtils.executeAsyncTask(task, context, callback);
        addTagTask(tag, task);
        return task;
    }

    public static RenewSecurityCodeTask renewSecurityCode(String tag, Context context, String deviceToken, String type, String securityCode, IRenewSecurityCodeListener callback)
    {
        RenewSecurityCodeTask task = new RenewSecurityCodeTask();
        EnterpriseUtils.executeAsyncTask(task, context, deviceToken, type, securityCode, callback);
        addTagTask(tag, task);
        return task;
    }

    public static PostMessageLimitedTask postMessageLimited(String tag, Context context, String account, String apiKey, String teamSn,
                                                          String contentType, String textContent, String mediaContent,
                                                          String fileShowName, String subject, String accountList, IPostMessageLimitedListener callback)
    {
        PostMessageLimitedTask task = new PostMessageLimitedTask();
        EnterpriseUtils.executeAsyncTask(task, context, account, apiKey, teamSn, contentType, textContent, mediaContent, fileShowName, subject, accountList, callback);
        addTagTask(tag, task);
        return task;
    }

    /**************************************** Online Mode***************************************************************/
    public static BapIdentifyTask bapIdentify(String tag, Context context, String type, String imageFormat, byte[] imageList, IBapIdentifyListener callback)
    {
        BapIdentifyTask task = new BapIdentifyTask();
        EnterpriseUtils.executeAsyncTask(task, context, type, imageFormat, imageList, callback);
        addTagTask(tag, task);
        return task;
    }

    public static BapVerifyTask bapVerify(String tag, Context context, String id, String type, String imageFormat, byte[] imageList, IBapVerifyListener callback)
    {
        BapVerifyTask task = new BapVerifyTask();
        EnterpriseUtils.executeAsyncTask(task, context, id, type, imageFormat, imageList, callback);
        addTagTask(tag, task);
        return task;
    }


}
