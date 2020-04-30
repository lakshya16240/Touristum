package com.teamcool.touristum.ui.Driver;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teamcool.touristum.Activities.LoginActivity;
import com.teamcool.touristum.Adapters.DriverAdapter;
import com.teamcool.touristum.DatabaseHelper;
import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.DriverTrip;
import com.teamcool.touristum.data.model.Employee;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UpcomingTripsFragment extends Fragment {

    private ArrayList<DriverTrip> driverList;
    private RecyclerView recyclerView;
    private DriverAdapter mAdapter;

    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;

    public static final String TAG = "UpcomingTrips";

    private Employee driver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_upcoming_trips, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);

        driver = LoginActivity.getLoggedInEmployee();


        mDbHelper = new DatabaseHelper(getContext());
        mDb = mDbHelper.getReadableDatabase();

        driverList = getDriverTrips(driver.getEmp_id());


        mAdapter = new DriverAdapter(driverList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return  root;
    }

    private ArrayList<DriverTrip> getDriverTrips(String emp_id) {


        ArrayList<DriverTrip> trips = new ArrayList<>();

        String sql = "Select fromDate, days, nights, cityname, b.cityID, clientName, b.clientID, clientContact " +
                "From Booking b, Vehicle v, Client c, Package p, TouristCity t " +
                "Where b.vehicleID = v.vehicleID and t.cityID = b.cityID and p.packageID = b.packageID and b.clientID = c.clientID and v.driverID = '" + emp_id + "';";

        Cursor cur = mDb.rawQuery(sql,null);

        while(cur!=null && cur.moveToNext()){
            trips.add(new DriverTrip(cur.getString(0),
                    cur.getString(1),
                    cur.getString(2),
                    cur.getString(3),
                    cur.getString(4),
                    cur.getString(5),
                    cur.getString(6),
                    String.format("%.0f",cur.getFloat(7))));


        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy MM dd");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);

        int i=0;
        boolean flag = false;
        while(i<trips.size()){

            flag = false;

            Log.d(TAG, "getFilteredBookings: " + i);

            if(trips.get(i).getFromDate().compareTo(date) < 0 ) {
                trips.remove(i);
                flag = true;

            }


            if(!flag)
                i += 1;
        }

        return trips;
    }
}