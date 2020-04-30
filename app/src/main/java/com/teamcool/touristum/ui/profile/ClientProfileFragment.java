package com.teamcool.touristum.ui.profile;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamcool.touristum.Activities.LoginActivity;
import com.teamcool.touristum.DatabaseHelper;
import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.Client;
import com.teamcool.touristum.data.model.Employee;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ClientProfileFragment extends Fragment {


    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;

    private Client client;

    private TextView tv_id;
    private Button bv_logout;
    private EditText et_name, et_address, et_contact, et_email;
    private ImageView iv_edit_name, iv_edit_address, iv_edit_contact, iv_edit_email, iv_profile;

    public static final String TAG = "PROFILEPAGE";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_client_profile, container, false);

        et_name = root.findViewById(R.id.et_name);
        et_address = root.findViewById(R.id.et_address);
        et_contact = root.findViewById(R.id.et_contact);
        et_email = root.findViewById(R.id.et_email);
        bv_logout = root.findViewById(R.id.bv_logout);

        tv_id = root.findViewById(R.id.tv_id);

        iv_edit_address = root.findViewById(R.id.iv_edit_address);
        iv_edit_contact = root.findViewById(R.id.iv_edit_contact);
        iv_edit_email = root.findViewById(R.id.iv_edit_email);
        iv_profile = root.findViewById(R.id.iv_profile);
        iv_edit_name = root.findViewById(R.id.iv_edit_name);

        mDbHelper = new DatabaseHelper(getContext());

        bv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        iv_edit_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_address.setInputType(InputType.TYPE_CLASS_TEXT);
                et_address.requestFocus();
                getActivity();
                InputMethodManager imm = (InputMethodManager)   getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

        iv_edit_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_contact.setInputType(InputType.TYPE_CLASS_TEXT);
                et_contact.requestFocus();
                getActivity();
                InputMethodManager imm = (InputMethodManager)   getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

        iv_edit_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_email.setInputType(InputType.TYPE_CLASS_TEXT);
                et_email.requestFocus();
                getActivity();
                InputMethodManager imm = (InputMethodManager)   getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            }
        });

        iv_edit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_name.setInputType(InputType.TYPE_CLASS_TEXT);
                et_name.requestFocus();
                getActivity();
                InputMethodManager imm = (InputMethodManager)   getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            }
        });

        et_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    et_name.setInputType(InputType.TYPE_NULL);

                }
                return false;
            }
        });

        et_address.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    et_address.setInputType(InputType.TYPE_NULL);

                }
                return false;
            }
        });

        et_email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    et_email.setInputType(InputType.TYPE_NULL);

                }
                return false;
            }
        });

        et_contact.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    et_contact.setInputType(InputType.TYPE_NULL);

                }
                return false;
            }
        });

        mDbHelper = new DatabaseHelper(getContext());

        client = LoginActivity.getLoggedInClient();

        if(client != null){
            et_name.setText(client.getClientName());
            et_contact.setText(String.valueOf(client.getClientContact()));
            et_address.setText(client.getClientAddress());
            et_email.setText(client.getClientEmail());
            tv_id.setText("ID : " + client.getClientID());
        }

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "onPause: ");

        mDb = mDbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("ClientEmail",et_email.getText().toString());
        contentValues.put("ClientAddress",et_address.getText().toString());
        contentValues.put("ClientContact",et_contact.getText().toString());
        contentValues.put("ClientName",et_name.getText().toString());

        mDb.update("Client",contentValues,"clientID = ?",new String[]{client.getClientID()});

        client.setClientName(et_name.getText().toString());
        client.setClientEmail(et_email.getText().toString());
        client.setClientAddress(et_address.getText().toString());
        client.setClientContact(et_contact.getText().toString());


    }
}