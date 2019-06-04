package com.gorilla.attendance.enterprise.Dialolg;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.gorilla.attendance.enterprise.util.apitask.listener.IButtonClickListener;

import java.util.ArrayList;

/**
 * Created by ggshao on 2017/2/12.
 */

public class PopupDialog extends Dialog {


    /*======================================================================
     * Constant Fields
     *=======================================================================*/
    public static final String TAG = "PopupDialog";

    /*======================================================================
     * Fields
     *=======================================================================*/
    private Context m_Context;
    private ArrayList<IButtonClickListener> m_ButtonListeners;

    /*======================================================================
     * Constructors
     *=======================================================================*/
    public PopupDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.m_Context = context;
    }


    /*======================================================================
     * Add listners for button
     *=======================================================================*/
    public void notifyButtonListeners(int buttonID, String tag, Object[] args)
    {
        if(m_ButtonListeners != null)
            for(IButtonClickListener listener : m_ButtonListeners)
                listener.onButtonClicked(buttonID, tag, args);
    }

    /*======================================================================
     * Notify button listeners
     *=======================================================================*/
    public void addButtonListener(IButtonClickListener listener)
    {
        if(m_ButtonListeners == null)
            m_ButtonListeners = new ArrayList<IButtonClickListener>();

        if(!m_ButtonListeners.contains(listener))
            m_ButtonListeners.add(listener);
    }

}
