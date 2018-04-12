package com.example.gerrys.merchantapps.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gerrys.merchantapps.Model.ReimburseReq;
import com.example.gerrys.merchantapps.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Admin on 5/25/2016.
 */
public class ReimburseSaldo extends Fragment {

    EditText ReimburseAmount;
    TextView tv_saldo;
    Button oke;
    DatabaseReference reimburse;
    FirebaseDatabase database;
    private Dialog MyDialog;
    double vol, luas, kel;
    String ID;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.geometry_cone, container, false);
        ReimburseAmount = (EditText)v.findViewById(R.id.amount);
        tv_saldo = (TextView) v.findViewById(R.id.saldo_amount);
        oke = (Button) v.findViewById(R.id.button2);
        database = FirebaseDatabase.getInstance();
        reimburse = database.getReference("ReimbureseReq");
//        ID = getArguments().getString("merch");
        oke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                MyDialog = new Dialog(getActivity().getApplicationContext());
                MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                MyDialog.setContentView(R.layout.reimburse_saldo_popup);
                MyDialog.setTitle("Reimburse Saldo");

                Button button = (Button) MyDialog.findViewById(R.id.oke12);
                final Button button2 = (Button) MyDialog.findViewById(R.id.cancan);
                final EditText ovoID = (EditText) MyDialog.findViewById(R.id.OvoId);
                final EditText AN = (EditText) MyDialog.findViewById(R.id.dit);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ReimburseReq reqs= new ReimburseReq(ID,ovoID.getText().toString(),AN.getText().toString(),"Waiting"," ");
                        reimburse.push().setValue(reqs);
                    }
                });
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyDialog.cancel();
                    }
                });
                MyDialog.show();
            }
        });
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}

