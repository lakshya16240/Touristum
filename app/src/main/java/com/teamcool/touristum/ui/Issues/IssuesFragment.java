package com.teamcool.touristum.ui.Issues;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.teamcool.touristum.Activities.EditBookingActivity;
import com.teamcool.touristum.Activities.LoginActivity;
import com.teamcool.touristum.Adapters.ViewReviewAdapter;
import com.teamcool.touristum.DatabaseHelper;
import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.Employee;
import com.teamcool.touristum.data.model.Filter;
import com.teamcool.touristum.data.model.Issue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class IssuesFragment extends Fragment {
    public static int VIEW_MODE_REVIEWS = 0;

    private RecyclerView rv_issues;

    private EditText et_searchReviews,et_search_filterReviews,et_filter;
    private LinearLayout ll_reviewHisotry;
    private String[] filter_options;

    private View dialog_view;
    private Spinner sp_dialog_filter;
    private ChipGroup cg_filters;
    private ImageButton bv_filter;

    private ArrayList<Filter> issue_filters;

    private ViewReviewAdapter viewReviewAdapter;

    private int SEARCH_MODE;
    public static final int SEARCH_MODE_FILTER = 1;
    public static final int SEARCH_MODE_NON_FILTER = 2;

    public static final int VIEW_MODE_CLIENT_ISSUES = 6;
    private int VIEW_MODE;

    private int no_fd=1;
    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;

    private AlertDialog.Builder builder;

    private Employee employee;

    public static final String TAG = "IssuesFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_issues, container, false);

        rv_issues = root.findViewById(R.id.rv_issues);
        et_search_filterReviews = root.findViewById(R.id.et_searchIssues);
        et_searchReviews = root.findViewById(R.id.et_filterIssues);
        ll_reviewHisotry = root.findViewById(R.id.ll_reviewIssues);

        et_searchReviews.setFocusable(false);
        et_search_filterReviews.setFocusable(false);

        employee = LoginActivity.getLoggedInEmployee();

        rv_issues.setLayoutManager(new LinearLayoutManager(getContext()));

        builder = new AlertDialog.Builder(getContext());

        mDbHelper = new DatabaseHelper(getContext());
        mDb = mDbHelper.getReadableDatabase();

        viewReviewAdapter = new ViewReviewAdapter(getContext(), new ViewReviewAdapter.onIssueClickListener() {
            @Override
            public void selectedIssue(final Issue issue) {
                View view = getLayoutInflater().inflate(R.layout.issue_popup, null);
                builder = new AlertDialog.Builder(getContext());
                builder.setView(view);

                final Spinner sp_employeeOptions = view.findViewById(R.id.sp_employeeOptions);;
                final EditText et_tentativeDate = view.findViewById(R.id.et_tentativeDate);

                if(employee.getEmp_type().equalsIgnoreCase("CEO")){
                    view.findViewById(R.id.ll_changeEmployee).setVisibility(View.VISIBLE);

                    ArrayList<String> employee_options = new ArrayList<>();
                    String sql = "Select employeeID from employee where employeeID!='" + employee.getEmp_id() + "';";
                    Cursor cur = mDb.rawQuery(sql,null);
                    while(cur!=null && cur.moveToNext()){
                        employee_options.add(cur.getString(0));
                    }

                    cur.close();

                    ArrayAdapter<String> employeeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,employee_options ){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
//                            // this part is needed for hiding the original view
                            View view = super.getView(position, convertView, parent);
//                            view.setVisibility(View.GONE);

                            return view;
                        }
                    };

                    sp_employeeOptions.setAdapter(employeeAdapter);

                    sp_employeeOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });


                }


                builder.setNeutralButton("Switch", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEUTRAL) {
                            if(issue.getStatus().equalsIgnoreCase("pending")) {
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy mm dd");
                                LocalDateTime now = LocalDateTime.now();
                                String date = dtf.format(now);

                                ContentValues contentValues = new ContentValues();
                                contentValues.put("status", "Resolved");
                                contentValues.put("IssueResolvedDate", date);
                                contentValues.put("employeeID", employee.getEmp_id());

                                mDb.update("Issues", contentValues, "issueID = ?", new String[]{issue.getIssueID()});
                                updateData();
                            }
                            else if(issue.getStatus().equalsIgnoreCase("resolved")) {
                                String date = et_tentativeDate.getText().toString();
                                Log.d(TAG, "onClick: " + date);

                                ContentValues contentValues = new ContentValues();
                                contentValues.put("status", "Pending");
                                contentValues.putNull("IssueResolvedDate");
                                if (!date.matches(""))
                                    contentValues.put("TentativeResolvedDate", date);
                                else
                                    contentValues.putNull("TentativeResolvedDate");
                                contentValues.put("employeeID", employee.getEmp_id());

                                mDb.update("Issues", contentValues, "issueID = ?", new String[]{issue.getIssueID()});
                                updateData();
                            }
                        }
                    }
                });


                builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            ContentValues contentValues = new ContentValues();
                            String date = et_tentativeDate.getText().toString();

                            if (employee.getEmp_type().equalsIgnoreCase("CEO")) {
                                contentValues.put("employeeID", sp_employeeOptions.getSelectedItem().toString());
                            }
                            if (!date.matches("")) {
                                contentValues.put("TentativeResolvedDate", date);
                            }
                            else
                                contentValues.putNull("TentativeResolvedDate");

                            mDb.update("Issues", contentValues, "issueID = ?", new String[]{issue.getIssueID()});
                            updateData();
                        }

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            dialog.dismiss();
                        }
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
        viewReviewAdapter.setVIEW_MODE_REVIEWS(VIEW_MODE_REVIEWS);
        viewReviewAdapter.setEmployee(true);

        issue_filters = new ArrayList<>();
        displayReviewHistoryUI();

        return root;
    }

    private void displayReviewHistoryUI() {

        final String[] client_options = new String[]{"Reviews","Issues"};

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,client_options ){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // this part is needed for hiding the original view
                View view = super.getView(position, convertView, parent);
                view.setVisibility(View.GONE);

                return view;
            }
        };

        VIEW_MODE = VIEW_MODE_CLIENT_ISSUES;

        if(employee.getEmp_type().equalsIgnoreCase("CEO"))
            getIssues("",issue_filters);
        else
            getIssues(employee.getEmp_id(),issue_filters);


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


        filter_options = new String[]{"IssueID","EmployeeID","IssueType","Status","DateOfIssue"};
        builder.setTitle("Filter Issues");



        launchDialog();
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
        return issue_filters.get(i).getType() + ":" + issue_filters.get(i).getFilter();
    }


    private int getFilterSize() {
        return issue_filters.size();
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

        if(employee.getEmp_type().equalsIgnoreCase("CEO"))
            getIssues("",issue_filters);
        else
            getIssues(employee.getEmp_id(),issue_filters);

    }

    private void addToFilter(Filter filter){
        issue_filters.add(filter);

    }

    private void removeFromFilter(Filter filter){

        issue_filters.remove(filter);


    }

    private void getIssues(String id, ArrayList<Filter> issue_filters) {
        String sql;
        ArrayList<Issue> issues = new ArrayList<>();
        Log.d(TAG, "getIssues: " + issue_filters.size());
        if(issue_filters.size() == 0) {

            if(id.equals(""))
                sql = "Select issueID, employeeID, clientID, issueType, review, status, dateOfIssue, tentativeResolvedDate,issueResolvedDate " +
                        "From Issues ";
            else

                sql = "Select issueID, employeeID, clientID, issueType, review, status, dateOfIssue, tentativeResolvedDate,issueResolvedDate " +
                    "From Issues " +
                    "Where employeeID = '" + id + "'";

            issues = getAllIssues(sql);

            viewReviewAdapter.setIssues(issues);
            viewReviewAdapter.setVIEW_MODE(VIEW_MODE);
            rv_issues.setAdapter(viewReviewAdapter);

        }
        else{
            sql = generateIssueSql(id,issue_filters);

            issues = getAllIssues(sql);
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
            rv_issues.setAdapter(viewReviewAdapter);


        }


    }

    private ArrayList<Issue> getAllIssues(String sql) {
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

        Toast.makeText(getContext(), "Number of issues : " + issues.size(), Toast.LENGTH_SHORT).show();


        return issues;

    }

    private String generateIssueSql(String id,ArrayList<Filter> issue_filters) {
        StringBuilder sql = new StringBuilder("Select issueID, employeeID, clientID, issueType, review, status, dateOfIssue, tentativeResolvedDate,issueResolvedDate " +
                "From Issues ");

        boolean isClient = false;

        if(!id.equals("")) {
            sql.append("Where employeeID = '" + id + "' ");
            isClient = true;
        }


        for(int i=0;i<issue_filters.size();i++){


            if(issue_filters.get(i).isSearch()) {

                if(isClient)
                    sql.append("and ");
                if(!isClient) {
                    sql.append("Where ");
                    isClient = true;
                }



                if (issue_filters.get(i).getType().equalsIgnoreCase("IssueID")) {
                    sql.append("IssueID = '" + issue_filters.get(i).getFilter() + "' ");

                } else if (issue_filters.get(i).getType().equalsIgnoreCase("EmployeeID")) {
                    sql.append("EmployeeID = '" + issue_filters.get(i).getFilter() + "' ");

                } else if (issue_filters.get(i).getType().equalsIgnoreCase("IssueType")) {
                    sql.append("IssueType = '" + issue_filters.get(i).getFilter() + "' ");

                } else if (issue_filters.get(i).getType().equalsIgnoreCase("Status")) {
                    sql.append("Status = '" + issue_filters.get(i).getFilter() + "' ");

                }else if (issue_filters.get(i).getType().equalsIgnoreCase("DateOfIssue")) {
                    sql.append("DateOfIssue = '" + issue_filters.get(i).getFilter() + "' ");

                }


            }

        }

        boolean inside = false;


        for(int i=0;i<issue_filters.size();i++){

            if(!issue_filters.get(i).isSearch()) {
                if (isClient && inside) {
                    sql.append("or ");

                }

                if (isClient && !inside && !(issue_filters.get(i).getType().equalsIgnoreCase("DateOfIssue"))) {
                    sql.append("and (");
                    inside = true;
                }

                if(!isClient){
                    sql.append("Where ");
                    isClient = true;
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
                    if(inside)
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

}
