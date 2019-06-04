package com.gorilla.attendance.enterprise.util.apitask.listener;

import com.gorilla.attendance.enterprise.datamodel.RegisterReplyModel;

/**
 * Created by ggshao on 2017/5/4.
 */

public interface IRegisterEmployeeListener {
    public void onRegisterEmployee(RegisterReplyModel model);
}
