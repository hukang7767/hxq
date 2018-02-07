package com.alpha.hxq;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



public class ProgressUtils {
    private static ProgressDialog mProgressDialog;// 进度对话框
    private static Dialog dialog;

    /**
     * 显示一个进度对话框
     */
    public static void showProgressDialog(Context context) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("正在处理中...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
    }

    /**
     * 显示一个进度对话框
     */
    public static void showProgressDialog2(Context context) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
    }

    /**
     * 自定义显示内容的进度对话框
     */
    public static void showProgressDialog(Context context, String msg) {
        mProgressDialog = new ProgressDialog(context, R.style.DialogStyleBottom);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

    }

    public static void cancelProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /*
     * 得到自定义的progressDialog
     * @param context
     * @param msg
     * @return
     */
    public static void createDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.progress_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.progress_dialog_anim);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息
        dialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        dialog.setCancelable(false);// 不可以用“返回键”取消
        dialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        dialog.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.8f;
        window.setAttributes(lp);
    }

    public static void cancelDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}

