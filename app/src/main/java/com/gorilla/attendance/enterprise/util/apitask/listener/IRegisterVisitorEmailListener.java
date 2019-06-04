package com.gorilla.attendance.enterprise.util.apitask.listener;

import com.gorilla.attendance.enterprise.datamodel.RegisterReplyModel;

/**
 * Created by ggshao on 2017/6/20.
 */

public interface IRegisterVisitorEmailListener {
    public void onRegisterVisitorEmail(RegisterReplyModel model);
}
