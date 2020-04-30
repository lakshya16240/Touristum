package com.teamcool.touristum.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.teamcool.touristum.DatabaseHelper;
import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.Client;
import com.teamcool.touristum.data.model.Employee;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

public class ClientActivity extends AppCompatActivity {

    private Client client;

    private AlertDialog.Builder builder;

    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;

    private String[] issue_options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        BottomNavigationView navView = findViewById(R.id.nav_view_client);

        client = LoginActivity.getLoggedInClient();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_clientHistory, R.id.navigation_clientReviewHistory,R.id.navigation_clientProfile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_client);
        builder = new AlertDialog.Builder(this);

        mDbHelper = new DatabaseHelper(this);
        mDb = mDbHelper.getReadableDatabase();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_client,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.navigation_report){
            View view = getLayoutInflater().inflate(R.layout.report_issues_popup, null);
            builder.setView(view);

            issue_options = new String[]{"Booking", "Client", "Tourist City", "Tourist Location", "Package", "Hotel", "Vehicle", "Employee"};

            final Spinner spinner = view.findViewById(R.id.sp_issueType);
            final EditText et_issue = view.findViewById(R.id.et_issue);

            ArrayAdapter<String> issueAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, issue_options){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    // this part is needed for hiding the original view
                    View view = super.getView(position, convertView, parent);
//                    view.setVisibility(View.GONE);

                    return view;
                }
            };

            spinner.setAdapter(issueAdapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });



            builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        String review = et_issue.getText().toString();
                        String type = spinner.getSelectedItem().toString();

                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy mm dd");
                        LocalDateTime now = LocalDateTime.now();
                        String date = dtf.format(now);

                        int issueID = -1;

                        String sql = "select issueID from issues order by issueID desc limit 1";

                        Cursor cur =mDb.rawQuery(sql,null);

                        while (cur != null && cur.moveToNext()) {
                            issueID = Integer.parseInt(cur.getString(0));
                        }

                        sql = "Select employeeID from employee where employeeType = 'customer_executive' or employeeType = 'manager'";
                        cur = mDb.rawQuery(sql,null);

                        ArrayList<Integer> possible_employees = new ArrayList<>();

                        while (cur != null && cur.moveToNext()){

                            possible_employees.add(Integer.parseInt(cur.getString(0)));

                        }

                        Random random = new Random();
                        int emp_id = possible_employees.get(random.nextInt(possible_employees.size()));


                        ContentValues contentValues = new ContentValues();
                        contentValues.put("ClientID",client.getClientID());
                        contentValues.put("IssueType",type);
                        contentValues.put("Review",review);
                        contentValues.put("Status","pending");
                        contentValues.put("DateOfIssue",date);
                        contentValues.put("issueID",String.valueOf(issueID + 1));
                        contentValues.put("employeeID",emp_id);

                        mDb.insert("issues",null,contentValues);


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
            AlertDialog dialog = builder.create();
            dialog.show();

        }
        else if(id == R.id.navigation_support){
            Intent intent = new Intent(ClientActivity.this,ChatAcivity.class);
            ChatAcivity.VIEW_MODE = ChatAcivity.VIEW_MODE_CLIENT;
            startActivity(intent);
        }
        return true;
    }
}
