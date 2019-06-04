package com.gorilla.attendance.enterprise.Widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by ggshao on 2017/5/11.
 */

public class KeyboardView extends RelativeLayout {

    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected final static int DRAWABLE_ID_BASE = 12;
    protected final static int DRAWABLE_ID_ACTION_DETECT = 13;

    private KeyboardView.OnKeyClickListener mOnKeyClickListener;

    protected void onKeyClick(String key){
        if(mOnKeyClickListener!=null){
            mOnKeyClickListener.onKeyClick(key);
        }
    }

    protected void onDeleteClick(){
        if(mOnKeyClickListener!=null){
            mOnKeyClickListener.onDeleteClick();
        }
    }

    protected void onEnterClick(){
        if(mOnKeyClickListener!=null){
            mOnKeyClickListener.onEnterClick();
        }
    }

    public void setOnKeyClickListener(KeyboardView.OnKeyClickListener listener){
        mOnKeyClickListener = listener;
    }

    public interface OnKeyClickListener{
        public void onKeyClick(String key);
        public void onDeleteClick();
        public void onEnterClick();
    }

    protected LayerDrawable getButtonBaseDrawable(Drawable drawable, KeyboardView.BaseActionDetectDrawable baseActionDetectDrawable){
        final Drawable[] drawables = new Drawable[2];
        drawables[0] = drawable;
        drawables[1] = baseActionDetectDrawable;
        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        layerDrawable.setId(0,DRAWABLE_ID_BASE);
        layerDrawable.setId(1,DRAWABLE_ID_ACTION_DETECT);
        baseActionDetectDrawable.setLayerDrawable(layerDrawable);
        return layerDrawable;
    }

    protected abstract class BaseActionDetectDrawable extends StateListDrawable {
        private LayerDrawable mLayerDrawable;
        public void setLayerDrawable(LayerDrawable layerDrawable){
            mLayerDrawable= layerDrawable;
        }
        @Override
        protected boolean onStateChange(int[] stateSet) {
            if(checkIsPressed(stateSet)){
                if(mLayerDrawable!=null)
                    onDown(mLayerDrawable);
            }else{
                if(mLayerDrawable!=null)
                    onUp(mLayerDrawable);
            }
            return super.onStateChange(stateSet);
        }
        protected abstract void onUp(LayerDrawable layerDrawable);
        protected abstract void onDown(LayerDrawable layerDrawable);
    }
    private boolean checkIsPressed(int[] stateSet){
        for(int state :stateSet){
            if(state==android.R.attr.state_pressed){
                return true;
            }
        }
        return false;
    }

}
