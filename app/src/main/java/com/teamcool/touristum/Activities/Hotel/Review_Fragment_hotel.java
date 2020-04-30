package com.teamcool.touristum.Activities.Hotel;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teamcool.touristum.Activities.LoginActivity;
import com.teamcool.touristum.DatabaseHelper;
import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.Hotel;
import com.teamcool.touristum.data.model.hotelReview;

import java.util.ArrayList;
import java.util.List;

public class Review_Fragment_hotel extends Fragment implements ReviewAdapter.onReviewClickListner{
    private View v;
    private RecyclerView recyclerView;
    private Hotel hotel;

    private DatabaseHelper helper;
    private SQLiteDatabase db;
    private List<hotelReview> reviews;
    private AlertDialog.Builder builder;
    private ReviewAdapter reviewAdapter;

    private TextView ID,review;
    private Button button;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.hotel_review,container,false);
        recyclerView=v.findViewById(R.id.recycler_review);
        hotel= LoginActivity.getLoggedInHotel();
        helper = new DatabaseHelper(getContext());
        db = helper.getWritableDatabase();
        reviews=getReview();
        builder = new AlertDialog.Builder(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewAdapter=new ReviewAdapter(reviews, getContext(), this);
        recyclerView.setAdapter(reviewAdapter);
        return v;
    }

    private List<hotelReview> getReview() {
        ArrayList<hotelReview> c=new ArrayList<>();
        String sql="SELECT HotelReviewID, ClientID, HotelID, HotelReview "+
                "FROM  hotelreview "+
                "where HotelID = '"+hotel.getHotelID()+"' ;";
        Cursor cur = db.rawQuery(sql, null);
        hotelReview hr=null;
        while (cur != null && cur.moveToNext()) {
            hr = new hotelReview(cur.getString(0),
                    cur.getString(1),
                    cur.getString(2),
                    cur.getString(3));
            c.add(hr);
        }
        return c;
    }

    @Override
    public void selectedClient(int position) {
        final hotelReview reviewew=reviews.get(position);
        final View view = getLayoutInflater().inflate(R.layout.review_popup, null);
        ID=view.findViewById(R.id.hotel_review_id);
        review=view.findViewById(R.id.review_full);
        button=view.findViewById(R.id.close_popup);
        ID.setText("HotelID: "+reviewew.getHotelReviewID());
        builder.setView(view);
        final AlertDialog dialog;
        review.setText("Review : "+reviewew.getHotelReview());
        dialog=builder.create();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
