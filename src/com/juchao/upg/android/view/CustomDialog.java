package com.juchao.upg.android.view;

import java.lang.reflect.Method;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.juchao.upg.android.MyApp;
import com.juchao.upg.android.R;

public class CustomDialog extends Dialog {
	public CustomDialog(Context context) {  
        super(context);  
    }  
  
    public CustomDialog(Context context, int theme) {  
        super(context, theme);  
    }  
  
    public static class Builder {  
        private Context context;  
        private String title;  
        private String message;  
        private String positiveButtonText;  
        private String negativeButtonText;  
        private View contentView;  
        private DialogInterface.OnClickListener positiveButtonClickListener;  
        private DialogInterface.OnClickListener negativeButtonClickListener;  
  
        public Builder(Context context) {  
            this.context = context;  
        }  
  
        public Builder setMessage(String message) {  
            this.message = message;  
            return this;  
        }  
  
        /** 
         * Set the Dialog message from resource 
         *  
         * @param title 
         * @return 
         */  
        public Builder setMessage(int message) {  
            this.message = (String) context.getText(message);  
            return this;  
        }  
  
        /** 
         * Set the Dialog title from resource 
         *  
         * @param title 
         * @return 
         */  
        public Builder setTitle(int title) {  
            this.title = (String) context.getText(title);  
            return this;  
        }  
  
        /** 
         * Set the Dialog title from String 
         *  
         * @param title 
         * @return 
         */  
  
        public Builder setTitle(String title) {  
            this.title = title;  
            return this;  
        }  
  
        public Builder setContentView(View v) {  
            this.contentView = v;  
            return this;  
        }  
  
        /** 
         * Set the positive button resource and it's listener 
         *  
         * @param positiveButtonText 
         * @return 
         */  
        public Builder setPositiveButton(int positiveButtonText,  
                DialogInterface.OnClickListener listener) {  
            this.positiveButtonText = (String) context  
                    .getText(positiveButtonText);  
            this.positiveButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setPositiveButton(String positiveButtonText,  
                DialogInterface.OnClickListener listener) {  
            this.positiveButtonText = positiveButtonText;  
            this.positiveButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setNegativeButton(int negativeButtonText,  
                DialogInterface.OnClickListener listener) {  
            this.negativeButtonText = (String) context  
                    .getText(negativeButtonText);  
            this.negativeButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setNegativeButton(String negativeButtonText,  
                DialogInterface.OnClickListener listener) {  
            this.negativeButtonText = negativeButtonText;  
            this.negativeButtonClickListener = listener;  
            return this;  
        }  
  
        public CustomDialog create() {  
            LayoutInflater inflater = (LayoutInflater) context  
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
            // instantiate the dialog with the custom Theme  
            final CustomDialog dialog = new CustomDialog(context,R.style.Dialog);  
            View layout = inflater.inflate(R.layout.dialog_normal_layout, null);  
            dialog.addContentView(layout, new LayoutParams(  
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));  
            // set the dialog title  
//            ((TextView) layout.findViewById(R.id.title)).setText(title);  
            // set the confirm button  
            if (positiveButtonText != null) {  
            	Button btn =  ((Button) layout.findViewById(R.id.btnConfirm));
            	btn.setText(positiveButtonText);  
            	btn.setBackgroundColor(Color.parseColor("#005FCB"));
                
                if (positiveButtonClickListener != null) {  
                    ((Button) layout.findViewById(R.id.btnConfirm))
                    		.setOnClickListener(new View.OnClickListener() {  
                                public void onClick(View v) {  
                                    positiveButtonClickListener.onClick(dialog,  
                                            DialogInterface.BUTTON_POSITIVE);  
                                }  
                            });  
                }  
            } else {  
                // if no confirm button just set the visibility to GONE  
                layout.findViewById(R.id.btnConfirm).setVisibility(  
                        View.GONE);  
            }  
            // set the cancel button  
            if (negativeButtonText != null) {  
                ((Button) layout.findViewById(R.id.btnCancel))  
                        .setText(negativeButtonText);  
                if (negativeButtonClickListener != null) {  
                    ((Button) layout.findViewById(R.id.btnCancel))  
                            .setOnClickListener(new View.OnClickListener() {  
                                public void onClick(View v) {  
                                    negativeButtonClickListener.onClick(dialog,  
                                            DialogInterface.BUTTON_NEGATIVE);  
                                }  
                            });  
                }  
            } else {  
                // if no confirm button just set the visibility to GONE  
                layout.findViewById(R.id.btnCancel).setVisibility(  
                        View.GONE);  
            }  
            // set the content message 
            
            if (message != null) {  
                TextView tv = ((TextView) layout.findViewById(R.id.message));  
                tv.setText(message);
            } 
            dialog.setContentView(layout);  
            return dialog;  
        }  
  
    }
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		//super.onWindowFocusChanged(hasFocus);
        try {
        		
            Object service = MyApp.application.getSystemService("statusbar");
            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            Method test = statusbarManager.getMethod("collapse");
            test.invoke(service);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}
    
}
