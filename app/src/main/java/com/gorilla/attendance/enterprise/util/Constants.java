package com.gorilla.attendance.enterprise.util;

import com.gorilla.attendance.enterprise.R;

/**
 * Created by ggshao on 2017/2/7.
 */

public class Constants {
    public static final int GET_FACE_SUCCESS = 0;
    public static final int GET_FACE_FAIL = GET_FACE_SUCCESS + 1;
    public static final int GET_FACE_TOO_LONG = GET_FACE_SUCCESS + 2;
    public static final int SET_ACTIVITY_UI = GET_FACE_SUCCESS + 3;//onLogin to decide ui
    public static final int OFFLINE_FACE_VERIFY_SUCCESS = GET_FACE_SUCCESS + 4;
    public static final int OFFLINE_FACE_VERIFY_FAIL = GET_FACE_SUCCESS + 5;
    public static final int CLOSE_CLOCK_DIALOG = GET_FACE_SUCCESS + 6;
    public static final int SEND_ATTENDANCE_RECOGNIZED_LOG = GET_FACE_SUCCESS + 7;
    public static final int BACK_TO_INDEX_PAGE = GET_FACE_SUCCESS + 8;
    public static final int VIDEO_PATH_DONE = GET_FACE_SUCCESS + 9;
    public static final int LAUNCH_VIDEO = GET_FACE_SUCCESS + 10;
    public static final int CLOSE_MESSAGE_DIALOG = GET_FACE_SUCCESS + 11;
    public static final int CLOSE_VIDEO = GET_FACE_SUCCESS + 12;
    public static final int CLOSE_APP = GET_FACE_SUCCESS + 13;
    public static final int CLOSE_IMAGE_DIALOG = GET_FACE_SUCCESS + 14;
    public static final int MSG_WEB_SOCKET_CONNECT = GET_FACE_SUCCESS + 15;
    public static final int MSG_WEB_SOCKET_DISCONNECT = GET_FACE_SUCCESS + 16;
    public static final int MSG_CHECK_USER_CLOCK_DB = GET_FACE_SUCCESS + 17;
    public static final int MSG_UPDATE_MARQUEE = GET_FACE_SUCCESS + 18;
    public static final int MSG_RESTART = GET_FACE_SUCCESS + 19;
    public static final int MSG_CHECK_WEB_SOCKET_ALIVE = GET_FACE_SUCCESS + 20;
    public static final int MSG_CLOSE_BLUETOOTH_DOOR_ONE = GET_FACE_SUCCESS + 21;
    public static final int MSG_CLOSE_BLUETOOTH_DOOR_TWO = GET_FACE_SUCCESS + 22;
    public static final int MSG_SHOW_REGISTER_DIALOG = GET_FACE_SUCCESS + 23;
    public static final int MSG_CLOSE_REGISTER_DIALOG = GET_FACE_SUCCESS + 24;
    public static final int MSG_VISITOR_EXPIRE = GET_FACE_SUCCESS + 25;
    public static final int MSG_DO_API = GET_FACE_SUCCESS + 26;
    public static final int MSG_SHOW_EMPLOYEE_THREE_TIMES_ERROR = GET_FACE_SUCCESS + 27;
    public static final int MSG_CLOSE_VIDEO = GET_FACE_SUCCESS + 28;


    public static final int MSG_RFID_CARD_READ = GET_FACE_SUCCESS + 29;
    public static final int MSG_QRCODE_SCAN = GET_FACE_SUCCESS + 30;

    public static final int MSG_QRCODE_CANCEL = GET_FACE_SUCCESS + 31;
    public static final int MSG_QRCODE_RESCAN = GET_FACE_SUCCESS + 32;


    public static final int SET_TIMER = GET_FACE_SUCCESS + 100;

    // ==================== For sharePref  ================================
    public static final String PREF_NAME_ATTENDANCE_ENTERPRISE = "attendanceEnterprisePref";
    public static final String PREF_KEY_SERIAL_NUMBER = "downloadId";
    public static final String PREF_KEY_BLUETOOTH_DOOR_ADDRESS = "bluetoothDoorAddress";
    public static final String PREF_KEY_BLUETOOTH_DOOR_PASSWORD = "bluetoothDoorPassword";
    public static final String PREF_KEY_BLUETOOTH_DOOR_IDEL_SECONDS = "bluetoothDoorIdleSeconds";

    public static final String PREF_KEY_SERVER_IP = "pref-key-server-ip";
    public static final String PREF_KEY_FTP_IP = "pref-key-ftp-ip";
    public static final String PREF_KEY_WS_IP = "pref-key-ws-ip";
    public static final String PREF_KEY_FTP_ACCOUNT = "pref-key-ftp-account";
    public static final String PREF_KEY_FTP_PASSWORD = "pref-key-ftp-password";
    public static final String PREF_KEY_DEVICE_TOKEN = "pref-key-device-token";
    //    public static final String PREF_KEY_DEVICE_NAME = "pref-key-device-name";
    public static final String PREF_KEY_EMPLOYEE_OPEN_DOOR = "pref-key-employee-open-door";
    public static final String PREF_KEY_VISITOR_OPEN_DOOR = "pref-key-visitor-open-door";
    public static final String PREF_KEY_LIVENESS = "pref-key-liveness";
    public static final String PREF_KEY_VIDEO_IDLE_SECONDS = "pref-key-video-idle-seconds";
    public static final String PREF_KEY_SYNC_TIME_OUT_SECONDS = "pref-key-sync-time-out-seconds";
    public static final String PREF_KEY_DEVICE_SHOW_NAME = "pref-key-device-show-name";
    public static final String PREF_KEY_REGISTER_MODEL_ID = "pref-key-register-model-id";
    public static final String PREF_KEY_IDENTIFY_RESULT_IDLE_MILLI_SECONDS = "pref-key-identify-result-idle-milli-seconds";
    public static final String PREF_KEY_IDENTIFY_THRESHOLD = "pref-key-identify-threshold";
    public static final String PREF_KEY_RETRY = "pref-key-retry";
    public static final String PREF_KEY_INNO_LUX = "pref-key-inno-lux";


    public static final String PREF_KEY_RTSP_SETTING = "pref-key-rtsp-setting";
    public static final String PREF_KEY_RTSP_IP = "pref-key-rtsp-ip";
    public static final String PREF_KEY_RTSP_URL = "pref-key-rtsp-url";
    public static final String PREF_KEY_RTSP_ACCOUNT = "pref-key-rtsp-account";
    public static final String PREF_KEY_RTSP_PASSWORD = "pref-key-rtsp-password";

    public static final String PREF_KEY_CLOCK_AUTO = "pref-key-clock-auto";
    public static final String PREF_KEY_RADIO_CLOCK_IN = "pref-key-radio-clock-in";
    public static final String PREF_KEY_RADIO_CLOCK_OUT = "pref-key-radio-clock-out";

    public static final String PREF_KEY_ONLINE_MODE = "pref-key-online-mode";


    public static final String PREF_KEY_FDR_RANGE = "pref-key-fdr-range";

    public static final String KEY_REGISTER_MESSAGE = "key-register-message";
    public static final String KEY_REGISTER_ID_TITLE = "key-register-id-title";
    public static final String KEY_REGISTER_ID = "key-register-id";
    public static final String KEY_REGISTER_NAME_TITLE = "key-register-name-title";
    public static final String KEY_REGISTER_NAME = "key-register-name";
    public static final String KEY_REGISTER_DELAY_TIME = "key-register-delay-time";
    public static final String KEY_REGISTER_RFID = "key-register-rfid";

    public static final int GET_FACE_DELAYED_TIME = 20000;//20 seconds
    public static final int REGISTER_MODEL_ID_INIT = 1000001;//local model id init 1000001

    public static final String CODE_ERROR_10 = "user_badRequest_10";
    public static final String CODE_ERROR_12 = "user_badRequest_12";


    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_ERROR = "error";

    public static final String CLOCK_IN = "IN";
    public static final String CLOCK_OUT = "OUT";

    public static final String ACCESS_IN = "IN";
    public static final String ACCESS_OUT = "OUT";

    public static final String VISITOR_VISIT = "IN";
    public static final String VISITOR_LEAVE = "OUT";

    public static final String CLOCK_UNKNOWN = "UNKNOWN";
    public static final String LIVENESS_OFF = "OFF";
    public static final String LIVENESS_SUCCEED = "SUCCEED";
    public static final String LIVENESS_FAILED = "FAILED";


    public static final String LOCALE_TW = "zh_TW";
    public static final String LOCALE_EN = "en_US";
    public static final String LOCALE_CN = "zh_CN";

    public static final String RECORD_MODE_RECORD = "RECORD";
    public static final String RECORD_MODE_UNRECOGNIZED = "UNRECOGNIZED";

    public static final int FDR_DEFAULT_RANGE = 100;

    public static final int LOGIN_STATUS_LOGOUT = 1;
    public static final int LOGIN_STATUS_LOGIN = 2;

    public static final int MODULES_ATTENDANCE = 1;
    public static final int MODULES_ACCESS = 2;
    public static final int MODULES_VISITORS = 3;
    public static final int MODULES_ATTENDANCE_ACCESS = 4;

    public static final int MODES_SECURITY_CODE = 1;
    public static final int MODES_RFID = 2;//Android not use this
    public static final int MODES_QR_CODE = 3;
    public static final int MODES_FACE_ICON = 4;
    public static final int MODES_FACE_IDENTIFICATION = 5;
    public static final int MODES_FACE_SCANNER = 6;//windows support

    public static final String FACE_VERIFY_SUCCEED = "SUCCEED";
    public static final String FACE_VERIFY_FAILED = "FAILED";
    public static final String FACE_VERIFY_IDENTIFIED = "IDENTIFIED";
    public static final String FACE_VERIFY_NONE = "NONE";

    public final static int CONTENT_FRAME_ID = R.id.content_frame;

    public static final int REGISTER_TYPE_EMPLOYEE = 0;
    public static final int REGISTER_TYPE_VISITOR = 1;


    public static final String KEY_RFID_CARD_NUMBER = "key-rfid-card-number";
    public static final String KEY_QRCODE_NUMBER = "key-qrcode-number";

    // ==================== For Fragment identify ================================

    public static final int FRAGMENT_TAG_CHOOSE_MODULE = 0;
    public static final int FRAGMENT_TAG_CHOOSE_MODE = FRAGMENT_TAG_CHOOSE_MODULE + 1;
    public static final int FRAGMENT_TAG_PIN_CODE = FRAGMENT_TAG_CHOOSE_MODULE + 2;
    public static final int FRAGMENT_TAG_FACE_ICON = FRAGMENT_TAG_CHOOSE_MODULE + 3;
    public static final int FRAGMENT_TAG_FACE_IDENTIFICATION = FRAGMENT_TAG_CHOOSE_MODULE + 4;
    public static final int FRAGMENT_TAG_CONF_LOGIN = FRAGMENT_TAG_CHOOSE_MODULE + 5;
    public static final int FRAGMENT_TAG_CONF_END_POINT_SETTING = FRAGMENT_TAG_CHOOSE_MODULE + 6;
    public static final int FRAGMENT_TAG_CONF_BLUETOOTH_SETTING = FRAGMENT_TAG_CHOOSE_MODULE + 7;
    public static final int FRAGMENT_TAG_REGISTER_INPUT_ID = FRAGMENT_TAG_CHOOSE_MODULE + 8;
    public static final int FRAGMENT_TAG_REGISTER_EMPLOYEE = FRAGMENT_TAG_CHOOSE_MODULE + 9;
    public static final int FRAGMENT_TAG_REGISTER_VISITOR = FRAGMENT_TAG_CHOOSE_MODULE + 10;
    public static final int FRAGMENT_TAG_ENGLISH_PIN_CODE = FRAGMENT_TAG_CHOOSE_MODULE + 11;
    public static final int FRAGMENT_TAG_VIDEO = FRAGMENT_TAG_CHOOSE_MODULE + 12;
    public static final int FRAGMENT_TAG_RFID = FRAGMENT_TAG_CHOOSE_MODULE + 13;
    public static final int FRAGMENT_TAG_REGISTER_INPUT_RFID = FRAGMENT_TAG_CHOOSE_MODULE + 14;
    public static final int FRAGMENT_TAG_REGISTER_CHOOSE_MODE = FRAGMENT_TAG_CHOOSE_MODULE + 15;
    public static final int FRAGMENT_TAG_QR_CODE = FRAGMENT_TAG_CHOOSE_MODULE + 16;
    public static final int FRAGMENT_TAG_REGISTER_INPUT_QR = FRAGMENT_TAG_CHOOSE_MODULE + 17;


    public static final String PUSH_MESSAGE_SYNC_EMPLOYEE = "SyncEmployee";
    public static final String PUSH_MESSAGE_SYNC_VISITOR = "SyncVisitor";
    public static final String PUSH_MESSAGE_SYNC_VIDEO = "SyncVideo";
    public static final String PUSH_MESSAGE_SYNC_MARQUEE = "SyncMarquee";
    public static final String PUSH_MESSAGE_RESTART = "Restart";
    public static final String PUSH_MESSAGE_TEST_CONNECTION = "TestConnection";
    public static final String PUSH_MESSAGE_SYNC_ALL = "SyncAll";

    public static final String PUSH_MESSAGE_UPGRADE = "Upgrade";

    public static final String BROADCAST_RECEIVER_DAY_PASS = "com.gorilla.attendance.enterprise.day.pass";


}
