package com.gorilla.attendance.enterprise.util.apitask.listener;

import com.gorilla.attendance.enterprise.datamodel.PostMessageLimitedModel;

/**
 * Created by ggshao on 2017/8/9.
 */

public interface IPostMessageLimitedListener {
    public void onPostMessageLimited(PostMessageLimitedModel model);
}
