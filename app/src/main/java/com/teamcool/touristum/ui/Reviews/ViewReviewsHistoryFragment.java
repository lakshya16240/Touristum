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

public class ViewReviewsHistoryFragment extends Fragment {

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
        et_search_filterReviews = root.findViewById(R.id.et_searchReviews);
        et_searchReviews = root.findViewById(R.id.et_filterReviews);
        sp_clientOptions = root.findViewById(R.id.sp_clientOptions);
        ll_reviewHisotry = root.findViewById(R.id.ll_reviewHistory);

        et_searchReviews.setFocusable(false);
        et_search_filterReviews.setFocusable(false);

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

        Log.d(TAG, "onCreateView: " + VIEW_MODE_REVIEWS);
//        rv_reviews.setAdapter(viewReviewAdapter);

        client = LoginActivity.getLoggedInClient();

        issue_filters = new ArrayList<>();
        review_filters = new ArrayList<>();
        displayReviewHistoryUI();





        return root;

    }


    private void displayReviewHistoryUI() {
//        ll_reviewHisotry.setVisibility(View.GONE);

        final String[] client_options = new String[]{"Reviews","Issues"};

        tv_selected.setVisibility(View.GONE);


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,client_options ){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // this part is needed for hiding the original view
                View view = super.getView(position, convertView, parent);
                view.setVisibility(View.GONE);

                return view;
            }
        };

        sp_clientOptions.setAdapter(dataAdapter);

        sp_clientOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                et_searchReviews.setHint("");
                et_search_filterReviews.setHint("");
                if(client_options[position].equals("Reviews")){

                    VIEW_MODE = VIEW_MODE_CLIENT_REVIEW;
                    getAllReviews(client.getClientID(),review_filters);

                }
                else if(client_options[position].equals("Issues")){
                    et_searchReviews.setHint("Filter " + client_options[position]);
                    et_search_filterReviews.setHint("Search " + client_options[position]);
                    VIEW_MODE = VIEW_MODE_CLIENT_ISSUES;
                    getIssues(client.getClientID(),issue_filters);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        et_searchReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SEARCH_MODE = SEARCH_MODE_FILTER;
                setup();
            }
        });
//
        et_search_filterReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SEARCH_MODE = SEARCH_MODE_NON_FILTER;
                setup();
            }
        });


    }

    private void setup(){
        dialog_view = getLayoutInflater().inflate(R.layout.dialog_filter,null);
        sp_dialog_filter = dialog_view.findViewById(R.id.sp_filter);
        et_filter= dialog_view.findViewById(R.id.et_filter);
        bv_filter = dialog_view.findViewById(R.id.bv_filter);
        cg_filters = dialog_view.findViewById(R.id.cg_filters);

        addChips();

        if(VIEW_MODE == VIEW_MODE_CLIENT_REVIEW){

            filter_options = new String[]{"HotelName","CityName","PackageType"};
            builder.setTitle("Filter Reviews");
        }
        else if(VIEW_MODE == VIEW_MODE_CLIENT_ISSUES){
            filter_options = new String[]{"IssueID","EmployeeID","IssueType","Status","DateOfIssue"};
            builder.setTitle("Filter Issues");
            launchDialog();
        }



    }

    private void addChips() {
        int size = getFilterSize();
        for(int i=0;i<size;i++){
            final Chip chip = new Chip(cg_filters.getContext());
            chip.setCloseIconVisible(true);
            chip.setText(getFilterText(i));
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = chip.getText().toString().split(":")[1];
                    String type = chip.getText().toString().split(":")[0];

                    if(SEARCH_MODE == SEARCH_MODE_FILTER) {
                        removeFromFilter(new Filter(type, text, false));
                        if(type.equalsIgnoreCase("fromDate"))
                            no_fd -= 1;
                    }
                    else
                        removeFromFilter(new Filter(type,text,true));

                    cg_filters.removeView(chip);

                }
            });

            cg_filters.addView(chip);

        }
    }

    private String getFilterText(int i) {
        if (VIEW_MODE == VIEW_MODE_CLIENT_REVIEW) {
            return review_filters.get(i).getType() + ":" + review_filters.get(i).getFilter();
        } else if (VIEW_MODE == VIEW_MODE_CLIENT_ISSUES) {
            return issue_filters.get(i).getType() + ":" + issue_filters.get(i).getFilter();
        }
        return "";
    }


    private int getFilterSize() {
        if(VIEW_MODE == VIEW_MODE_CLIENT_REVIEW){
            return review_filters.size();
        }
        else if(VIEW_MODE == VIEW_MODE_CLIENT_ISSUES){
            return issue_filters.size();
        }
        return 0;

    }


    private void launchDialog(){
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, filter_options);
        sp_dialog_filter.setAdapter(filterAdapter);

        sp_dialog_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String type = filter_options[position];

                et_filter.setHint(type);

                bv_filter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!((VIEW_MODE == VIEW_MODE_CLIENT_ISSUES) && ((type.equalsIgnoreCase("DateOfIssue") && no_fd>2)))) {

                            Log.d(TAG, "onClick: " + "filter");
                            if (et_filter.getText().length() != 0) {
                                Filter filter;

                                if (SEARCH_MODE == SEARCH_MODE_FILTER)
                                    filter = new Filter(type, et_filter.getText().toString(), false);
                                else
                                    filter = new Filter(type, et_filter.getText().toString(), true);

                                addToFilter(filter);
//                            booking_filters.add(filter);
                                final Chip chip = new Chip(getContext());
                                chip.setCloseIconVisible(true);
                                chip.setText(type + ":" + filter.getFilter());
                                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String text = chip.getText().toString().split(":")[1];
                                        String type = chip.getText().toString().split(":")[0];

                                        if (SEARCH_MODE == SEARCH_MODE_FILTER) {
                                            removeFromFilter(new Filter(type, text, false));
                                            if(type.equalsIgnoreCase("fromDate"))
                                                no_fd -= 1;

                                        }
                                        else
                                            removeFromFilter(new Filter(type, text, true));
//                                    booking_filters.remove(new Filter(type,text));
                                        cg_filters.removeView(chip);

                                    }
                                });

                                cg_filters.addView(chip);

                                if (type.equalsIgnoreCase("fromDate"))
                                    no_fd += 1;

                            }
                        }
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setView(dialog_view);
        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateData();



            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateData() {

        if(VIEW_MODE == VIEW_MODE_CLIENT_REVIEW){
            getAllReviews(client.getClientID(),review_filters);
        }
        else if(VIEW_MODE == VIEW_MODE_CLIENT_ISSUES){
            getIssues(client.getClientID(),issue_filters);
        }

    }

    private void addToFilter(Filter filter){
        if(VIEW_MODE == VIEW_MODE_CLIENT_REVIEW){
            review_filters.add(filter);
        }
        else if(VIEW_MODE == VIEW_MODE_CLIENT_ISSUES){
            issue_filters.add(filter);
        }

    }

    private void removeFromFilter(Filter filter){
        if(VIEW_MODE == VIEW_MODE_CLIENT_REVIEW){
            review_filters.remove(filter);
        }
        else if(VIEW_MODE == VIEW_MODE_CLIENT_ISSUES){
            issue_filters.remove(filter);
        }

    }

    private void getIssues(String clientID, ArrayList<Filter> issue_filters) {
        String sql;
        ArrayList<Issue> issues = new ArrayList<>();
        Log.d(TAG, "getIssues: " + issue_filters.size());
        if(issue_filters.size() == 0) {
            sql = "Select issueID, employeeID, clientID, issueType, review, status, dateOfIssue, tentativeResolvedDate,issueResolvedDate " +
                    "From Issues " +
                    "Where clientID = '" + clientID + "'";

            issues = getAllIssues(sql,clientID);

            viewReviewAdapter.setIssues(issues);
            viewReviewAdapter.setVIEW_MODE(VIEW_MODE);
            rv_reviews.setAdapter(viewReviewAdapter);

        }
        else{
            sql = generateIssueSql(clientID,issue_filters);

            issues = getAllIssues(sql,clientID);
            String date1 = "",date2 = "";
            int date_count=0;

            for(int i=0;i<issue_filters.size();i++){
                if(!issue_filters.get(i).isSearch()){

                    if(issue_filters.get(i).getType().equalsIgnoreCase("dateOfIssue")){
                        if(date_count == 0){
                            date_count += 1;
                            date1 = issue_filters.get(i).getFilter();
                        }
                        else if(date_count == 1){
                            date_count += 1;
                            date2 = issue_filters.get(i).getFilter();

                            if(date1.compareTo(date2) > 0 ){
                                String temp = date1;
                                date1 = date2;
                                date2 = temp;
                            }
                        }

                    }
                }
            }

            int i=0;
            boolean flag = false;
            while(i<issues.size()){

                flag = false;

                Log.d(TAG, "getFilteredBookings: " + i);

                if(date_count == 1){
                    if(issues.get(i).getDatOfIssue().compareTo(date1) < 0 ) {
                        issues.remove(i);
                        flag = true;
                    }
                }
                else if(date_count == 2){
                    if(issues.get(i).getDatOfIssue().compareTo(date1) < 0 ) {
                        issues.remove(i);
                        flag = true;

                    }
                    else if(issues.get(i).getDatOfIssue().compareTo(date1) > 0 && issues.get(i).getDatOfIssue().compareTo(date2) > 0){
                        issues.remove(i);
                        flag = true;
                    }

                }

                if(!flag)
                    i += 1;
            }

            viewReviewAdapter.setIssues(issues);
            viewReviewAdapter.setVIEW_MODE(VIEW_MODE);
            rv_reviews.setAdapter(viewReviewAdapter);


        }


    }

    private ArrayList<Issue> getAllIssues(String sql, String clientID) {
        Cursor cur = mDb.rawQuery(sql,null);

        ArrayList<Issue> issues = new ArrayList<>();
        while(cur!=null && cur.moveToNext()){
            issues.add(new Issue(cur.getString(0),
                    cur.getString(1),
                    cur.getString(2),
                    cur.getString(3),
                    cur.getString(4),
                    cur.getString(5),
                    cur.getString(6),
                    cur.getString(7),
                    cur.getString(8)));

        }

        return issues;

    }

    private String generateIssueSql(String clientID,ArrayList<Filter> issue_filters) {
        StringBuilder sql = new StringBuilder("Select issueID, employeeID, clientID, issueType, review, status, dateOfIssue, tentativeResolvedDate,issueResolvedDate " +
                            "From Issues " +
                            "Where clientID = '" + clientID + "'");

        for(int i=0;i<issue_filters.size();i++){

            if(issue_filters.get(i).isSearch()) {
                sql.append("and ");



                if (issue_filters.get(i).getType().equalsIgnoreCase("IssueID")) {
                    sql.append("IssueID = '" + issue_filters.get(i).getFilter() + "' ");

                } else if (issue_filters.get(i).getType().equalsIgnoreCase("EmployeeID")) {
                    sql.append("EmployeeID = '" + issue_filters.get(i).getFilter() + "' ");

                } else if (issue_filters.get(i).getType().equalsIgnoreCase("IssueType")) {
                    sql.append("IssueType = '" + issue_filters.get(i).getFilter() + "' ");

                } else if (issue_filters.get(i).getType().equalsIgnoreCase("Status")) {
                    sql.append("Status = '" + issue_filters.get(i).getFilter() + "' ");

                } else if (issue_filters.get(i).getType().equalsIgnoreCase("DateOfIssue")) {
                    sql.append("DateOfIssue = '" + issue_filters.get(i).getFilter() + "' ");

                }


            }

        }

        boolean inside = false;


        for(int i=0;i<issue_filters.size();i++){

            if(!issue_filters.get(i).isSearch()) {
                if (inside) {
                    sql.append("or ");

                }

                if (!inside && !(issue_filters.get(i).getType().equalsIgnoreCase("DateOfIssue"))) {
                    sql.append("and (");
                    inside = true;
                }


                if (issue_filters.get(i).getType().equalsIgnoreCase("IssueID")) {
                    sql.append("IssueID = '" + issue_filters.get(i).getFilter() + "' ");

                } else if (issue_filters.get(i).getType().equalsIgnoreCase("EmployeeID")) {
                    sql.append("EmployeeID = '" + issue_filters.get(i).getFilter() + "' ");

                } else if (issue_filters.get(i).getType().equalsIgnoreCase("IssueType")) {
                    sql.append("IssueType = '" + issue_filters.get(i).getFilter() + "' ");

                } else if (issue_filters.get(i).getType().equalsIgnoreCase("Status")) {
                    sql.append("Status = '" + issue_filters.get(i).getFilter() + "' ");

                } else if (issue_filters.get(i).getType().equalsIgnoreCase("DateOfIssue")) {
//                    sql.append("h.Rating = '" + issue_filters.get(i).getFilter() + "' ");
                    sql = new StringBuilder(sql.substring(0,sql.length() - 3));
                }



            }

        }

        if (inside) {
            sql.append(")");
        }

        Log.d(TAG, "generatedSql: " + sql.toString());

        return sql.toString();
    }

    private void getAllReviews(String clientID,ArrayList<Filter> review_filters) {

        ArrayList<String> reviews = new ArrayList<>();
        ArrayList<String> reviewFor = new ArrayList<>();
        ArrayList<String> review_ids = new ArrayList<>();

        String sql;
        Cursor cur;

        sql = "SELECT cityReviewID, clientID, review, cityName " +
                "FROM cityReview cr, touristCity t " +
                "WHERE t.cityID = cr.cityID and clientID = '" + clientID + "';";

        cur = mDb.rawQuery(sql,null);

        while(cur!=null && cur.moveToNext()){

            review_ids.add("CityReviewID : " + cur.getString(0));
            reviewFor.add("City : " + cur.getString(3));
            reviews.add(cur.getString(2));

        }

        int i=0;
        boolean flag = false;

        if(review_filters.size() > 0) {
            while (i < reviewFor.size()) {
                flag = false;
                for (int j = 0; j < review_filters.size(); j++) {
                    if (review_filters.get(j).getType().equalsIgnoreCase("cityName")) {
                        if (!reviewFor.get(i).split(":")[1].split(" ")[1].equalsIgnoreCase(review_filters.get(j).getFilter())) {
                            reviewFor.remove(i);
                            flag = true;
                            break;
                        }
                    }
                }
                if (!flag)
                    i++;
            }
        }

        i = reviewFor.size() - 1;

        sql = "SELECT hotelReviewID, ClientID, hotelReview,hotelName " +
                "FROM hotelReview hr,hotelInformation h " +
                "WHERE hr.hotelID = h.hotelID and clientID = '" + clientID + "';";

        cur = mDb.rawQuery(sql,null);

        while(cur!=null && cur.moveToNext()){

            review_ids.add("HotelReviewID : " + cur.getString(0));
            reviewFor.add("Hotel : " + cur.getString(3));
            reviews.add(cur.getString(2));

        }

        flag = false;
        if(review_filters.size() > 0) {
            while (i < reviewFor.size()) {
                flag = false;
                for (int j = 0; j < review_filters.size(); j++) {
                    if (review_filters.get(j).getType().equalsIgnoreCase("hotelName")) {
                        if (!reviewFor.get(i).split(":")[1].split(" ")[1].equalsIgnoreCase(review_filters.get(j).getFilter())) {
                            reviewFor.remove(i);
                            flag = true;
                            break;
                        }
                    }
                }
                if (!flag)
                    i++;
            }
        }

        i = reviewFor.size() - 1;

        sql = "SELECT packageReviewID, ClientID, Review,packageType,days,nights " +
                "FROM packageReview pr, package p " +
                "WHERE p.packageID = pr.packageID and clientID = '" + clientID + "';";

        cur = mDb.rawQuery(sql,null);

        while(cur!=null && cur.moveToNext()){

            review_ids.add("PackageReviewID : " + cur.getString(0));
            reviewFor.add("Package : " + cur.getString(3) + "(Days:" + cur.getString(4) + "/Nights:" + cur.getString(5) + ")");
            reviews.add(cur.getString(2));

        }

        flag = false;
        if(review_filters.size() > 0) {
            while (i < reviewFor.size()) {
                flag = false;
                for (int j = 0; j < review_filters.size(); j++) {
                    if (review_filters.get(j).getType().equalsIgnoreCase("packageType")) {
                        String temp = reviewFor.get(i).split(":")[1].split(" ")[1];
                        if (!temp.split("\\(")[0].equalsIgnoreCase(review_filters.get(j).getFilter())) {
                            reviewFor.remove(i);
                            flag = true;
                            break;
                        }
                    }
                }
                if (!flag)
                    i++;
            }
        }

        i = reviewFor.size() - 1;


        viewReviewAdapter.setReviewFor(reviewFor);
        viewReviewAdapter.setReview_ids(review_ids);
        viewReviewAdapter.setReviews(reviews);
        viewReviewAdapter.setVIEW_MODE(VIEW_MODE);
//        viewReviewAdapter.notifyDataSetChanged();
        rv_reviews.setAdapter(viewReviewAdapter);
    }


}
