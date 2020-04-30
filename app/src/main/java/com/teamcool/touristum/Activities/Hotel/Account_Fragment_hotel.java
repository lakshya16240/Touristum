package com.teamcool.touristum.Activities.Hotel;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.teamcool.touristum.Activities.LoginActivity;
import com.teamcool.touristum.DatabaseHelper;
import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.Hotel;

import java.io.IOException;

public class Account_Fragment_hotel extends Fragment {
    private View v;
    private EditText hotelname,AvailableRooms;
    private TextView hotelID,cityID,locationID;
    private Button button;
    private Hotel hotel;
    private RatingBar ratingBar;
    private SQLiteDatabase db;
    private DatabaseHelper helper;
    private ImageView im1,im2;

    @Override
    public void onPause() {
        super.onPause();
        db=helper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("HotelID",hotel.getHotelID());
        contentValues.put("CityID",hotel.getCityID());
        contentValues.put("LocationID",hotel.getLocationID());
        contentValues.put("HotelName",hotelname.getText().toString());
        contentValues.put("AvailableRooms",AvailableRooms.getText().toString());
        contentValues.put("Rating",hotel.getHotelID());
        db.update("hotelinformation",contentValues,"HotelID = ?",new String[]{hotel.getHotelID()});

        hotel.setAvailableRooms(AvailableRooms.getText().toString());
        hotel.setHotelName(hotelname.getText().toString());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.hotel_account,container,false);
        hotelname=v.findViewById(R.id.hotel_name_account);
        AvailableRooms=v.findViewById(R.id.hotel_rooms);
        hotel= LoginActivity.getLoggedInHotel();
        button=v.findViewById(R.id.logout_hotel);
        hotelID=v.findViewById(R.id.hotelID_hotel);
        cityID=v.findViewById(R.id.CityID_hotel);
        locationID=v.findViewById(R.id.locationID_hotel);
        ratingBar=v.findViewById(R.id.ratingBar_hotel);
        im1=v.findViewById(R.id.img_account);
        im2=v.findViewById(R.id.img_hotel_rooms);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        helper=new DatabaseHelper(getContext());
        hotelname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    hotelname.setInputType(InputType.TYPE_NULL);
                }
                return false;
            }
        });
        AvailableRooms.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    AvailableRooms.setInputType(InputType.TYPE_NULL);
                }
                return false;
            }
        });
        im1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hotelname.setInputType(InputType.TYPE_CLASS_TEXT);
                hotelname.requestFocus();
                getActivity();
                InputMethodManager imm = (InputMethodManager)   getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
        im2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AvailableRooms.setInputType(InputType.TYPE_CLASS_TEXT);
                AvailableRooms.requestFocus();
                getActivity();
                InputMethodManager imm = (InputMethodManager)   getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
        try {
            helper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(hotel!=null){
            hotelID.setText("HotelID : " + hotel.getHotelID());
            cityID.setText("CityID : " + hotel.getCityID());
            locationID.setText("LocationsID : " +  hotel.getLocationID());
            hotelname.setText(hotel.getHotelName());
            AvailableRooms.setText(hotel.getAvailableRooms());
            ratingBar.setRating(Float.parseFloat(hotel.getHotelRating()));
        }
        return v;
    }
}
