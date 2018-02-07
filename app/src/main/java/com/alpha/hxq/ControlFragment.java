package com.alpha.hxq;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ControlFragment extends BaseFragment implements View.OnClickListener{


    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private AlertDialog alertDialog1;
    private EditText edtTime;
    private Button done1;
    private Button cancel;
    private TextView tv11;
    private TextView tvDs;
    private TimePicker tp;

    public ControlFragment() {
        // Required empty public constructor
    }
    private String mData;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     */
    // TODO: Rename and change types and number of parameters
    public static ControlFragment newInstance(String data) {
        ControlFragment fragment = new ControlFragment();
        Bundle args = new Bundle();
        args.putString("data",data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mData = getArguments().getString("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_control, container, false);
        tv1 = inflate.findViewById(R.id.tv1);
        tv2 = inflate.findViewById(R.id.tv2);
        tv3 = inflate.findViewById(R.id.tv3);
        tv11 = inflate.findViewById(R.id.tv11);
        tvDs = inflate.findViewById(R.id.tv_ds);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        return inflate;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mData!=null){
            if (mData.startsWith("770505")){
                tv11.setVisibility(View.VISIBLE);
                tvDs.setVisibility(View.VISIBLE);
                int h = Integer.parseInt(mData.substring(6, 8), 16);
                int m = Integer.parseInt(mData.substring(8, 10), 16);
                tvDs.setText((h<10?("0"+h):(h+""))+":"+(m<10?("0"+m):m+""));
            }else {
                tv11.setVisibility(View.INVISIBLE);
                tvDs.setVisibility(View.INVISIBLE);
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv1:
                ((MainActivity)getActivity()).changeFragment(1,mData);
                if (MainActivity.mBluetoothLeService!=null){
                    MainActivity.mBluetoothLeService.txxx(Utils.getCheckSum("7704050100000003"),false);
                }
                break;
            case R.id.tv2:
                ((MainActivity)getActivity()).changeFragment(2,mData);
                if (MainActivity.mBluetoothLeService!=null){
                    MainActivity.mBluetoothLeService.txxx(Utils.getCheckSum("7704050200000003"),false);
                }
                break;
            case R.id.tv3:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                View inflate1 = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time1, null);
                builder1.setView(inflate1);
                alertDialog1 = builder1.create();
                tp = inflate1.findViewById(R.id.tp);
                tp.setIs24HourView(true);
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
                    L.showMessage(getActivity(),"设置时间不能超过0小时");
                    return;
                }
                edtTime.setText((Integer.valueOf(mTime)-1)+"");
                break;
            case R.id.btn_plus1:
                String pTime = edtTime.getText().toString();
                Integer iTime1=0;
                if (pTime==null||pTime.length()==0){
                    iTime1 = 1;
                }else {
                    iTime1 = Integer.valueOf(pTime);
                }

                if (iTime1>24){
                    L.showMessage(getActivity(),"设置时间不能超过24小时");
                    return;
                }
                edtTime.setText((iTime1+1)+"");
                break;
            case R.id.btn_done1:
                alertDialog1.dismiss();
                if (MainActivity.mBluetoothLeService!=null){
//                    String hour = tp.getCurrentHour()+"";
//                    String minute = tp.getCurrentMinute()+"";
//                    if (hour.length()==1){
//                        hour = "0"+hour;
//                    }
                    String hour = Integer.toHexString(tp.getCurrentHour());
                    String minute = Integer.toHexString(tp.getCurrentMinute());
                    if (hour.length()==1){
                        hour = "0"+hour;
                    }
                    if (minute.length() ==1){
                        minute = "0"+minute;
                    }
                    MainActivity.mBluetoothLeService.txxx(Utils.getCheckSum("77040504"+hour+minute+"0003"),false);
                }
                break;
            case R.id.btn_cancel:
                alertDialog1.dismiss();
                break;
        }

    }

    @Override
    public void upDateUi(int type ,String data) {
        mData = data;
        if (mData!=null){
            if (mData.startsWith("770505")){
                tv11.setVisibility(View.VISIBLE);
                tvDs.setVisibility(View.VISIBLE);
                int h = Integer.parseInt(mData.substring(6, 8), 16);
                int m = Integer.parseInt(mData.substring(8, 10), 16);
                tvDs.setText((h<10?("0"+h):(h+""))+":"+(m<10?("0"+m):m+""));
            }else {
                tv11.setVisibility(View.INVISIBLE);
                tvDs.setVisibility(View.INVISIBLE);
            }

        }
    }
}
