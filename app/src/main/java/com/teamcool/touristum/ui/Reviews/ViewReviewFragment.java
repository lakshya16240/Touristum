package com.teamcool.touristum.ui.Reviews;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.teamcool.touristum.Activities.LoginActivity;
import com.teamcool.touristum.Adapters.ViewReviewAdapter;
import com.teamcool.touristum.DatabaseHelper;
import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.Client;
import com.teamcool.touristum.data.model.Filter;
import com.teamcool.touristum.data.model.Hotel;
import com.teamcool.touristum.data.model.Issue;
import com.teamcool.touristum.data.model.Package;
import com.teamcool.touristum.data.model.TouristCity;
import com.teamcool.touristum.ui.home.HomeFragment;

import java.util.ArrayList;

public class ViewReviewFragment extends Fragment {

    public static int VIEW_MODE_REVIEWS = 0;

    private TouristCity city;
    private Hotel hotel;
    private Package aPackage;

    private RecyclerView rv_reviews;
    private TextView tv_selected;

    private EditText et_searchReviews,et_search_filterReviews,et_filter;
    private LinearLayout ll_reviewHisotry;
    private Spinner sp_clientOptions;

    private String[] filter_options;

    private View dialog_view;
    private Spinner sp_dialog_filter;
    private ChipGroup cg_filters;
    private ImageButton bv_filter;

    private ArrayList<Filter> review_filters, issue_filters;

    private ViewReviewAdapter viewReviewAdapter;

    private int SEARCH_MODE;
    public static final int SEARCH_MODE_FILTER = 1;
    public static final int SEARCH_MODE_NON_FILTER = 2;

    public static final int VIEW_MODE_CLIENT_REVIEW = 5;
    public static final int VIEW_MODE_CLIENT_ISSUES = 6;
    private int VIEW_MODE;


    private int no_fd=1;
    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;

    private ArrayList<String> reviews;

    private AlertDialog.Builder builder;

    private Client client;

    public static final String TAG = "ViewReviewFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_view_reviews_history, container, false);

        rv_reviews = root.findViewById(R.id.rv_reviews);
        tv_selected = root.findViewById(R.id.tv_selectedID);

        sp_clientOptions = root.findViewById(R.id.sp_clientOptions);
        ll_reviewHisotry = root.findViewById(R.id.ll_reviewHistory);

//        et_searchReviews.setFocusable(false);
//        et_search_filterReviews.setFocusable(false);

        rv_reviews.setLayoutManager(new LinearLayoutManager(getContext()));

        builder = new AlertDialog.Builder(getContext());


        mDbHelper = new DatabaseHelper(getContext());
        mDb = mDbHelper.getReadableDatabase();

        viewReviewAdapter = new ViewReviewAdapter(getContext(), new ViewReviewAdapter.onIssueClickListener() {
            @Override
            public void selectedIssue(Issue issue) {

            }
        });
        viewReviewAdapter.setVIEW_MODE_REVIEWS(VIEW_MODE_REVIEWS);

        Log.d(TAG, "onBindViewHolder: " + VIEW_MODE_REVIEWS + " " + VIEW_MODE + " ");


        Log.d(TAG, "onCreateView: " + VIEW_MODE_REVIEWS);
//        rv_reviews.setAdapter(viewReviewAdapter);

        client = LoginActivity.getLoggedInClient();


        displayReviewsUI();




        return root;

    }

    private void displayReviewsUI() {

        Log.d(TAG, "displayReviewsUI: ");

        ll_reviewHisotry.setVisibility(View.GONE);
        if (VIEW_MODE_REVIEWS == HomeFragment.VIEW_MODE_CITY) {
            city = (TouristCity) getArguments().getSerializable("SelectedCity");
            tv_selected.setText("City : " + city.getCityName());
            getCityReviews(city);

        } else if (VIEW_MODE_REVIEWS == HomeFragment.VIEW_MODE_HOTEL) {
            hotel = (Hotel) getArguments().getSerializable("SelectedHotel");
            tv_selected.setText("Hotel : " + hotel.getHotelName());
            getHotelReviews(hotel);
        } else if (VIEW_MODE_REVIEWS == HomeFragment.VIEW_MODE_PACKAGE) {
            aPackage = (Package) getArguments().getSerializable("SelectedPackage");
            tv_selected.setText("Package : " + aPackage.getPackageType() + "(Days:" + aPackage.getDays() + "/Nights:" + aPackage.getNights() + ")");
            getPackageReviews(aPackage);
        }
    }

    private void getCityReviews(TouristCity city) {

        ArrayList<String> reviews = new ArrayList<>();
        ArrayList<String> client_ids = new ArrayList<>();
        ArrayList<String> review_ids = new ArrayList<>();

        String sql = "SELECT cityReviewID, ClientID, review " +
                "FROM cityReview " +
                "WHERE cityID = '" + city.getCityID() + "';";

        Cursor cur = mDb.rawQuery(sql,null);

        while(cur!=null && cur.moveToNext()){

            review_ids.add("ReviewID : " + cur.getString(0));
            client_ids.add("ClientID : " + cur.getString(1));
            reviews.add(cur.getString(2));

        }

        viewReviewAdapter.setClient_ids(client_ids);
        viewReviewAdapter.setReview_ids(review_ids);
        viewReviewAdapter.setReviews(reviews);
        rv_reviews.setAdapter(viewReviewAdapter);
//        viewReviewAdapter.notifyDataSetChanged();
    }

    private void getHotelReviews(Hotel hotel) {

        ArrayList<String> reviews = new ArrayList<>();
        ArrayList<String> client_ids = new ArrayList<>();
        ArrayList<String> review_ids = new ArrayList<>();

        String sql = "SELECT hotelReviewID, ClientID, hotelReview " +
                "FROM hotelReview " +
                "WHERE hotelID = '" + hotel.getHotelID() + "';";

        Cursor cur = mDb.rawQuery(sql,null);

        while(cur!=null && cur.moveToNext()){

            review_ids.add(cur.getString(0));
            client_ids.add(cur.getString(1));
            reviews.add(cur.getString(2));

        }

        viewReviewAdapter.setClient_ids(client_ids);
        viewReviewAdapter.setReview_ids(review_ids);
        viewReviewAdapter.setReviews(reviews);
//        viewReviewAdapter.notifyDataSetChanged();
        rv_reviews.setAdapter(viewReviewAdapter);

    }

    private void getPackageReviews(Package aPackage) {

        ArrayList<String> reviews = new ArrayList<>();
        ArrayList<String> client_ids = new ArrayList<>();
        ArrayList<String> review_ids = new ArrayList<>();

        String sql = "SELECT packageReviewID, ClientID, Review " +
                "FROM packageReview " +
                "WHERE packageID = '" + aPackage.getPackageID() + "';";

        Cursor cur = mDb.rawQuery(sql,null);

        while(cur!=null && cur.moveToNext()){

            review_ids.add(cur.getString(0));
            client_ids.add(cur.getString(1));
            reviews.add(cur.getString(2));

        }

        viewReviewAdapter.setClient_ids(client_ids);
        viewReviewAdapter.setReview_ids(review_ids);
        viewReviewAdapter.setReviews(reviews);
//        viewReviewAdapter.notifyDataSetChanged();
        rv_reviews.setAdapter(viewReviewAdapter);

    }


}
