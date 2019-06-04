package com.gorilla.attendance.enterprise.util.apitask.listener;

import com.gorilla.attendance.enterprise.datamodel.RegisterReplyModel;

/**
 * Created by ggshao on 2017/5/15.
 */

public interface IRegisterVisitorFromDbListener {
    public void onRegisterVisitorFromDb(RegisterReplyModel model);
}
