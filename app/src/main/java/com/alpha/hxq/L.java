package com.alpha.hxq;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;



public class L {
	public static boolean isDebug=true;
	public static boolean isToast=true;
	private static String oldMsg;
	private static long time;
	public static void i(String value){
		if(isDebug){
			Log.i("DEBUG", value);
		}
	}
	public static void showMessage(Context context,String value){
		View inflate = LayoutInflater.from(App.getInstance()).inflate(R.layout.toast_view, null);
		TextView content = (TextView) inflate.findViewById(R.id.tv_content);
		Toast toast = new Toast(context);
		content.setText(value);
		toast.setView(inflate);
		toast.setDuration(Toast.LENGTH_LONG);
		if(isToast){
			if (!value.equals(oldMsg)) { // 当显示的内容不一样时，即断定为不是同一个Toast
				toast.show();
				time = System.currentTimeMillis();
			} else {
				// 显示内容一样时，只有间隔时间大于2秒时才显示
				if (System.currentTimeMillis() - time > 2000) {
					toast.show();
					time = System.currentTimeMillis();
				}
			}
			oldMsg = value;
		}
	}
	
	public static void showMessage1(Context context, String value){
		View inflate = LayoutInflater.from(App.getInstance()).inflate(R.layout.toast_view, null);
		TextView content = (TextView) inflate.findViewById(R.id.tv_content);
		Toast toast = new Toast(context);
		content.setText(value);
		toast.setView(inflate);
		toast.setMargin(0f, 0.3f);
		toast.setDuration(Toast.LENGTH_LONG);
		if(isToast){
			if (!value.equals(oldMsg)) { // 当显示的内容不一样时，即断定为不是同一个Toast

				time = System.currentTimeMillis();
				toast.show();
			} else {
				// 显示内容一样时，只有间隔时间大于2秒时才显示
				if (System.currentTimeMillis() - time > 2000) {
					time = System.currentTimeMillis();
					toast.show();
				}
			}
			oldMsg = value;

		}
	}	
}
