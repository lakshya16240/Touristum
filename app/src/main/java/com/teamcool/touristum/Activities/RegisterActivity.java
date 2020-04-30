package com.teamcool.touristum.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.teamcool.touristum.DatabaseHelper;
import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.LoggedInUser;

public class RegisterActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;
    private EditText et_name, et_address, et_contact, et_email;

    private Button bv_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_name = findViewById(R.id.et_name);
        et_address = findViewById(R.id.et_address);
        et_contact = findViewById(R.id.et_contact);
        et_email = findViewById(R.id.et_email);
        bv_register = findViewById(R.id.bv_register);

        mDbHelper = new DatabaseHelper(this);
        mDb = mDbHelper.getWritableDatabase();

        bv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sql = "Select clientID from client order by clientID DESC LIMIT 1";

                Cursor cur = mDb.rawQuery(sql,null);
                int clientID = 0;
                while(cur!=null && cur.moveToNext()){
                    clientID = Integer.parseInt(cur.getString(0));
                }

                clientID += 1;

                ContentValues contentValues = new ContentValues();
                contentValues.put("clientID",clientID);
                contentValues.put("clientAddress",et_address.getText().toString());
                contentValues.put("clientContact",et_contact.getText().toString());
                contentValues.put("clientEmail",et_email.getText().toString());

                mDb.insert("Client",null,contentValues);

                SharedPreferences sharedPreferences = getSharedPreferences("LoginCreds", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                Gson gson = new Gson();
                String json;

                LoggedInUser user = new LoggedInUser(String.valueOf(clientID),"client","client");
                json = gson.toJson(user);
                edit.putString("client"+clientID, json);
                edit.apply();

                Toast.makeText(RegisterActivity.this, "Your username is : client" + clientID + " and password : client", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }
}
