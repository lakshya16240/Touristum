package com.teamcool.touristum.ui.Reviews;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.teamcool.touristum.DatabaseHelper;
import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.Booking;
import com.teamcool.touristum.data.model.Package;

public class ReviewFragment extends Fragment {

    private Booking booking;

    private EditText et_packageReview,et_cityReview,et_hotelReview;
    private Button bv_addPackageReview,bv_addcityReview,bv_addhotelReview;

    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_review, container, false);

        et_packageReview = root.findViewById(R.id.et_packageReview);
        et_cityReview = root.findViewById(R.id.et_cityReview);
        et_hotelReview = root.findViewById(R.id.et_hotelReview);
        bv_addPackageReview = root.findViewById(R.id.bv_addPackageReview);
        bv_addcityReview= root.findViewById(R.id.bv_addCityReview);
        bv_addhotelReview = root.findViewById(R.id.bv_addHotelReview);

        mDbHelper = new DatabaseHelper(getContext());
        mDb = mDbHelper.getWritableDatabase();

        booking = (Booking) getArguments().getSerializable("SelectedBooking");

        bv_addPackageReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_packageReview.getText().length() > 0){

                    String packageReviewId = "0";
                    String sql = "select packageReviewID from packageReview order by packageReviewID DESC LIMIT 1";
                    Cursor cur = mDb.rawQuery(sql,null);
                    while (cur != null && cur.moveToNext()) {
                        packageReviewId = cur.getString(0);
                    }
                    packageReviewId = String.valueOf(Integer.parseInt(packageReviewId) + 1);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("ClientID",booking.getClientID());
                    contentValues.put("PackageID",booking.getPackageID());
                    contentValues.put("Review",et_packageReview.getText().toString());
                    contentValues.put("PackageReviewID",packageReviewId);

                    mDb.insert("packageReview",null,contentValues);

                    Toast.makeText(getActivity(), "Package Review Added", Toast.LENGTH_SHORT).show();
                    et_packageReview.setText("");
                }
            }
        });

        bv_addcityReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_cityReview.getText().length() > 0){

                    String cityReviewId = "0";
                    String sql = "select cityReviewID from cityReview order by cityReviewID DESC LIMIT 1";
                    Cursor cur = mDb.rawQuery(sql,null);
                    while (cur != null && cur.moveToNext()) {
                        cityReviewId = cur.getString(0);
                    }
                    cityReviewId = String.valueOf(Integer.parseInt(cityReviewId) + 1);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("ClientID",booking.getClientID());
                    contentValues.put("CityID",booking.getCityID());
                    contentValues.put("Review",et_cityReview.getText().toString());
                    contentValues.put("CityReviewID",cityReviewId);

                    mDb.insert("cityReview",null,contentValues);

                    Toast.makeText(getActivity(), "City Review Added", Toast.LENGTH_SHORT).show();

                    et_cityReview.setText("");
                }
            }
        });

        bv_addhotelReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_hotelReview.getText().length() > 0){

                    String hotelReviewId = "0";
                    String sql = "select hotelReviewId from hotelReview order by hotelReviewId DESC LIMIT 1";
                    Cursor cur = mDb.rawQuery(sql,null);
                    while (cur != null && cur.moveToNext()) {
                        hotelReviewId = cur.getString(0);
                    }
                    hotelReviewId = String.valueOf(Integer.parseInt(hotelReviewId) + 1);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("ClientID",booking.getClientID());
                    contentValues.put("hotelID",booking.getHotelID());
                    contentValues.put("hotelReview",et_hotelReview.getText().toString());
                    contentValues.put("hotelReviewId",hotelReviewId);

                    mDb.insert("hotelReview",null,contentValues);

                    Toast.makeText(getActivity(), "Hotel Review Added", Toast.LENGTH_SHORT).show();

                    et_hotelReview.setText("");
                }
            }
        });

        return root;

    }


}
