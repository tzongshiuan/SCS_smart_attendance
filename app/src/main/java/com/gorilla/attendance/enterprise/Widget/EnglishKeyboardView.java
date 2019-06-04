package com.gorilla.attendance.enterprise.Widget;

import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gorilla.attendance.enterprise.R;

import java.util.ArrayList;

/**
 * Created by ggshao on 2017/5/11.
 */

public class EnglishKeyboardView extends KeyboardView {
    private LayoutInflater mInflater;
    private ArrayList<TextView> mAlphabetList;

    public EnglishKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.keyboard_layout, this);

        mAlphabetList = new ArrayList<>();

        initAlphabetButton(R.id.alphabet_a);
        initAlphabetButton(R.id.alphabet_b);
        initAlphabetButton(R.id.alphabet_c);
        initAlphabetButton(R.id.alphabet_d);
        initAlphabetButton(R.id.alphabet_e);
        initAlphabetButton(R.id.alphabet_f);
        initAlphabetButton(R.id.alphabet_g);
        initAlphabetButton(R.id.alphabet_h);
        initAlphabetButton(R.id.alphabet_i);
        initAlphabetButton(R.id.alphabet_j);
        initAlphabetButton(R.id.alphabet_k);
        initAlphabetButton(R.id.alphabet_l);
        initAlphabetButton(R.id.alphabet_m);
        initAlphabetButton(R.id.alphabet_n);
        initAlphabetButton(R.id.alphabet_o);
        initAlphabetButton(R.id.alphabet_p);
        initAlphabetButton(R.id.alphabet_q);
        initAlphabetButton(R.id.alphabet_r);
        initAlphabetButton(R.id.alphabet_s);
        initAlphabetButton(R.id.alphabet_t);
        initAlphabetButton(R.id.alphabet_u);
        initAlphabetButton(R.id.alphabet_v);
        initAlphabetButton(R.id.alphabet_w);
        initAlphabetButton(R.id.alphabet_x);
        initAlphabetButton(R.id.alphabet_y);
        initAlphabetButton(R.id.alphabet_z);

        initValueButton(R.id.number_0);
        initValueButton(R.id.number_1);
        initValueButton(R.id.number_2);
        initValueButton(R.id.number_3);
        initValueButton(R.id.number_4);
        initValueButton(R.id.number_5);
        initValueButton(R.id.number_6);
        initValueButton(R.id.number_7);
        initValueButton(R.id.number_8);
        initValueButton(R.id.number_9);

        initValueButton(R.id.symbol_dash);

        initFunctionCaps();
        initDelete();
        initEnter();
    }

    private void initEnter(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.function_enter);
        final TextView text = (TextView) findViewById(R.id.function_enter_text);
        final ImageView imageView = (ImageView) findViewById(R.id.function_enter_text_image);
        layout.setBackground(getButtonBaseDrawable(getResources().getDrawable(R.drawable.keyboard_button_normal),
                new EnglishKeyboardView.OnActionDetectDrawable() {
                    @Override
                    protected void onUp() {
                        imageView.setImageResource(R.mipmap.btn_enter_n);
                        text.setTextColor(getResources().getColor(R.color.white));
                    }

                    @Override
                    protected void onDown() {
                        imageView.setImageResource(R.mipmap.btn_enter_p);
                        text.setTextColor(getResources().getColor(R.color.keyboard_pressed_text_color));
                    }
                }));
        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEnterClick();
            }
        });
    }

    private void initDelete(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.function_delete);
        final ImageView imageView = (ImageView) findViewById(R.id.function_delete_image);
        layout.setBackground(getButtonBaseDrawable(getResources().getDrawable(R.drawable.keyboard_button_normal),
                new EnglishKeyboardView.OnActionDetectDrawable() {
                    @Override
                    protected void onUp() {
                        imageView.setImageResource(R.mipmap.btn_del_n);
                    }

                    @Override
                    protected void onDown() {
                        imageView.setImageResource(R.mipmap.btn_del_p);
                    }
                }));
        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClick();
            }
        });
    }

    private void initFunctionCaps(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.function_cap);
        final TextView text = (TextView) findViewById(R.id.function_cap_text);
        final ImageView imageView = (ImageView) findViewById(R.id.function_cap_image);
        imageView.setImageResource(isCurrentUpperCase()? R.mipmap.btn_lower_case_n: R.mipmap.btn_cap_n);

        layout.setBackground(getButtonBaseDrawable(getResources().getDrawable(R.drawable.keyboard_button_normal),
                new EnglishKeyboardView.OnActionDetectDrawable() {
                    @Override
                    public void onUp() {
                        imageView.clearColorFilter();
                        text.setTextColor(getResources().getColor(R.color.white));
                    }

                    @Override
                    public void onDown() {
                        imageView.setColorFilter(getResources().getColor(R.color.keyboard_pressed_text_color));
                        text.setTextColor(getResources().getColor(R.color.keyboard_pressed_text_color));
                    }
                }));
        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCurrentUpperCase = isCurrentUpperCase();
                for(TextView alphabet : mAlphabetList){
                    if(isCurrentUpperCase){
                        alphabet.setText(alphabet.getText().toString().toLowerCase());
                    }else{
                        alphabet.setText(alphabet.getText().toString().toUpperCase());
                    }
                }
                imageView.setImageResource(isCurrentUpperCase? R.mipmap.btn_cap_n: R.mipmap.btn_lower_case_n);
            }
        });
    }

    private boolean isCurrentUpperCase(){
        char alphabet = mAlphabetList.get(0).getText().charAt(0);
        return Character.isUpperCase(alphabet);
    }

    private void initAlphabetButton(int res){
        TextView textView = (TextView) findViewById(res);
        initValueButton(textView);
        if(!mAlphabetList.contains(textView)) {
            mAlphabetList.add(textView);
        }
    }

    private void initValueButton(int res){
        initValueButton((TextView) findViewById(res));
    }

    private void initValueButton(final TextView textView){

        textView.setBackground(getButtonBaseDrawable(getResources().getDrawable(R.drawable.keyboard_button_normal)
                ,new EnglishKeyboardView.OnActionDetectDrawable() {
                    @Override
                    public void onUp() {
                        textView.setTextColor(getResources().getColor(R.color.white));
                    }

                    @Override
                    public void onDown() {
                        textView.setTextColor(getResources().getColor(R.color.keyboard_pressed_text_color));
                    }
                }));
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onKeyClick(textView.getText().toString());
            }
        });
    }
    private abstract class OnActionDetectDrawable extends BaseActionDetectDrawable {
        @Override
        protected void onDown(LayerDrawable layerDrawable) {
            layerDrawable.setDrawableByLayerId(DRAWABLE_ID_BASE, getResources().getDrawable(R.drawable.keyboard_button_pressed));
            invalidateSelf();
            invalidate();
            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            onDown();
        }

        @Override
        protected void onUp(LayerDrawable layerDrawable) {
            layerDrawable.setDrawableByLayerId(DRAWABLE_ID_BASE, getResources().getDrawable(R.drawable.keyboard_button_normal));
            invalidateSelf();
            invalidate();
            onUp();
        }
        protected abstract void onUp();
        protected abstract void onDown();
    }
}
