package com.alpha.hxq;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends BaseFragment implements View.OnClickListener{

    private int mType;
    private ImageView imgF;
    TextView light;
    private LinearLayout fs;
    private ImageView imgFs;
    private int [] images = new int[]{R.mipmap.fs1,R.mipmap.fs2,R.mipmap.fs3};
    private int mFengSu;
    private Button done;
    private AlertDialog alertDialog;
    private TextView yy;
    private Button done1;
    private Button cancel;
    private AlertDialog alertDialog1;
    private EditText edtTime;
    private String data;
    private TextView tvFs;
    private TextView tvTem;
    private TextView tvTime;
    private TextView tvError;
    private TextView tvQh;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(int type,String data) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt("type",type);
        args.putString("data",data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt("type");
            data = getArguments().getString("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_main, container, false);
        imgF = inflate.findViewById(R.id.img_f);
        light = inflate.findViewById(R.id.tv_light);
        fs = inflate.findViewById(R.id.ll_fs);
        yy = inflate.findViewById(R.id.tv_yy);
        tvFs = inflate.findViewById(R.id.tv_fs);
        tvTem = inflate.findViewById(R.id.tv_tem);
        tvTime = inflate.findViewById(R.id.tv_time);
        tvError = inflate.findViewById(R.id.error);
        tvQh = inflate.findViewById(R.id.tv_qh);
        yy.setOnClickListener(this);
        light.setOnClickListener(this);
        fs.setOnClickListener(this);
        tvQh.setOnClickListener(this);
        Animation circle_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_round_rotate);
        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
        circle_anim.setInterpolator(interpolator);
        if (circle_anim != null) {
            imgF.startAnimation(circle_anim);  //开始动画
        }
        if (MainActivity.lightOn==0){
            light.setText("趣味灯");
        }else {
            light.setText("趣味灯"+MainActivity.lightOn);
        }

        return inflate;
    }
    private int ifs;
    @Override
    public void upDateUi(int type ,String data) {
        mType = type;
        this.data = data;
        if (data!=null){
            tvTem.setText( Integer.parseInt(data.substring(12,14), 16)+"℃");
            if (data.substring(10,12).equals("01")){
                tvFs.setText("低速");
                mFengSu =0;
                ifs = 0;
            }else if (data.substring(10,12).equals("02")){
                tvFs.setText("中速");
                mFengSu =1;
                ifs = 1;
            }else {
                tvFs.setText("高速");
                mFengSu =2;
                ifs  = 2;
            }
            tvTime.setText(Integer.parseInt(data.substring(8,10),16)+"min");
            if (data.substring(14,16).equals("01")){
                tvError.setText("风扇故障");
            }else  if (data.substring(14,16).equals("02")){
                tvError.setText("温度过高");
            }else  if (data.substring(14,16).equals("03")){
                tvError.setText("传感器短路");
            }else  if (data.substring(14,16).equals("04")){
                tvError.setText("传感器开路");
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_light:
                if (MainActivity.mBluetoothLeService!=null){

                    MainActivity.lightOn++;

                    if (MainActivity.lightOn>3){
                        MainActivity.lightOn = 0;
                    }
                    MainActivity.mBluetoothLeService.txxx(Utils.getCheckSum("7704050"+mType+"00000"+MainActivity.lightOn+"0"+(ifs+1)),false);
                    if (MainActivity.lightOn==0){
                        light.setText("趣味灯");
                    }else {
                        light.setText("趣味灯"+MainActivity.lightOn);
                    }

                }
                break;
            case R.id.ll_fs:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_fs, null);
                builder.setView(inflate);
                alertDialog = builder.create();
                Button plus = inflate.findViewById(R.id.btn_plus);
                Button minus = inflate.findViewById(R.id.btn_minus);
                plus.setOnClickListener(this);
                minus.setOnClickListener(this);
                imgFs = inflate.findViewById(R.id.img_fs);
                done = inflate.findViewById(R.id.btn_done);
                done.setOnClickListener(this);
                imgFs.setImageResource(images[ifs]);
                alertDialog.show();
                break;
            case R.id.btn_plus:
                mFengSu++;
                if (mFengSu>2){
                    mFengSu = 2;
                    return;
                }
                imgFs.setImageResource(images[mFengSu]);
                break;
            case R.id.btn_minus:
                mFengSu--;
                if (mFengSu<0){
                    mFengSu = 0;
                    return;
                }
                imgFs.setImageResource(images[mFengSu]);
                break;
            case R.id.btn_done:
                alertDialog.dismiss();
                if (MainActivity.mBluetoothLeService!=null){
                    MainActivity.mBluetoothLeService.txxx(Utils.getCheckSum("7704050"+mType+"0000000"+(mFengSu+1)),false);
                }
                break;
            case R.id.tv_yy:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                View inflate1 = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
                builder1.setView(inflate1);
                alertDialog1 = builder1.create();
                Button plus1 = inflate1.findViewById(R.id.btn_plus1);
                Button minus1 = inflate1.findViewById(R.id.btn_minus1);
                edtTime = inflate1.findViewById(R.id.edt_time1);
                if (data!=null&&data.length()>10){
                    if (mType == 2&&Integer.parseInt(data.substring(8,10),16)>15){

                        edtTime.setText("15");
                    }else {

                        edtTime.setText(Integer.parseInt(data.substring(8,10),16)+"");
                    }
                }
                plus1.setOnClickListener(this);
                minus1.setOnClickListener(this);
                done1 = inflate1.findViewById(R.id.btn_done1);
                done1.setOnClickListener(this);
                cancel = inflate1.findViewById(R.id.btn_cancel);
                cancel.setOnClickListener(this);
                alertDialog1.show();
                break;
            case R.id.btn_minus1:
                String mTime = edtTime.getText().toString();
                Integer iTime2 = 0;
                if (mTime==null||mTime.length()==0){
                    iTime2 = 15;
                }else {
                    iTime2= Integer.valueOf(mTime);
                }
                if (iTime2<1){
                    L.showMessage(getActivity(),"设置时间不能超过0分钟");
                    return;
                }
                edtTime.setText((Integer.valueOf(mTime)-1)+"");
                break;
            case R.id.btn_plus1:
                String pTime = edtTime.getText().toString();
                Integer iTime1=0;
                if (pTime==null||pTime.length()==0){
                    iTime1 = 15;
                }else {
                    iTime1 = Integer.valueOf(pTime);
                }
                int i = 0;
                if (mType==1){
                    i = 99;
                }else {
                    i = 15;
                }
                Log.i("hukang", "onClick: "+i);
                if (iTime1>i-1){
                    L.showMessage(getActivity(),"设置时间不能超过"+i+"分钟");
                    return;
                }
                edtTime.setText((iTime1+1)+"");
                break;
            case R.id.btn_done1:
                int time = Integer.parseInt(edtTime.getText().toString());
                if (mType==1){
                    if (time>99){

                        L.showMessage1(getActivity(),"设置时间不能超过99分钟");
                        return;
                    }
                }else {
                    if (time>15){

                        L.showMessage1(getActivity(),"设置时间不能超过15分钟");
                        return;
                    }
                }
                alertDialog1.dismiss();
                if (MainActivity.mBluetoothLeService!=null){

                    Log.i("hukang", "onClick: "+Integer.toHexString(time));
                    String hexTime = Integer.toHexString(time);
                    if (hexTime.length()==1){
                        hexTime = "0"+hexTime;
                    }
                    MainActivity.mBluetoothLeService.txxx(Utils.getCheckSum("7704050"+mType+"00"+hexTime+"000"+(mFengSu+1)),false);
                }
                break;
            case R.id.btn_cancel:
                alertDialog1.dismiss();
                break;
            case R.id.tv_qh:
                if (mType ==1){
                    if (MainActivity.mBluetoothLeService!=null){
                        MainActivity.mBluetoothLeService.txxx(Utils.getCheckSum("7704050200000003"),false);
//                        mType = 2;
                    }
                }else {
                    if (MainActivity.mBluetoothLeService!=null){
                        MainActivity.mBluetoothLeService.txxx(Utils.getCheckSum("7704050100000003"),false);
//                        mType = 1;
                    }
                }


                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (data!=null){
            tvTem.setText( Integer.parseInt(data.substring(12,14), 16)+"℃");
            if (data.substring(10,12).equals("01")){
                tvFs.setText("低速");
            }else if (data.substring(10,12).equals("02")){
                tvFs.setText("中速");
            }else {
                tvFs.setText("高速");
            }
            tvTime.setText(Integer.parseInt(data.substring(8,10),16)+"min");
            if (data.substring(14,16).equals("01")){
                tvError.setText("风扇故障");
            }else  if (data.substring(14,16).equals("02")){
                tvError.setText("温度过高");
            }else  if (data.substring(14,16).equals("03")){
                tvError.setText("传感器短路");
            }else  if (data.substring(14,16).equals("04")){
                tvError.setText("传感器开路");
            }
        }
    }

}
