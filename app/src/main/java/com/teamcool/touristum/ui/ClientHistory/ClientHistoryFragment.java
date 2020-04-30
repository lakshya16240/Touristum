package com.teamcool.touristum.ui.ClientHistory;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.material.chip.ChipGroup;
import com.teamcool.touristum.Activities.LoginActivity;
import com.teamcool.touristum.Adapters.ClientHistoryAdapter;
import com.teamcool.touristum.DatabaseHelper;
import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.Booking;
import com.teamcool.touristum.data.model.Client;
import com.teamcool.touristum.data.model.Filter;
import com.teamcool.touristum.ui.Reviews.ReviewFragment;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ClientHistoryFragment extends Fragment {

    private RecyclerView rv_data;
    private ClientHistoryAdapter clientHistoryAdapter;
    private EditText et_search, et_filter;

    private Client currClient;
    private ArrayList<Booking> bookings;

    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;


    private String[] filter_options;
    private AlertDialog.Builder builder;
    private View dialog_view;
    private Spinner sp_dialog_filter;
    private ChipGroup cg_filters;
    private ImageButton bv_filter;

    private ArrayList<Filter> employee_filters;

    public static final String TAG = "ClientHistoryFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_client_history, container, false);
//        final TextView textView = root.findViewById(R.id.text_employees);

        rv_data = root.findViewById(R.id.rv_client_booking_data);
        currClient = LoginActivity.getLoggedInClient();

        mDbHelper = new DatabaseHelper(getContext());
        mDb = mDbHelper.getReadableDatabase();

        bookings = getBookings();
        employee_filters = new ArrayList<>();

        builder = new AlertDialog.Builder(getContext());

        rv_data.setLayoutManager(new LinearLayoutManager(getContext()));

        clientHistoryAdapter = new ClientHistoryAdapter(bookings, getActivity(), new ClientHistoryAdapter.onBookingClickListener() {
            @Override
            public void selectedBooking(Booking booking) {
                FragmentManager manager = getFragmentManager();
                Fragment fragment = new ReviewFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                Bundle bundle=new Bundle();
                bundle.putSerializable("SelectedBooking",booking);
                fragment.setArguments(bundle);
                transaction.replace(R.id.nav_host_fragment_client,fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        rv_data.setAdapter(clientHistoryAdapter);


        return root;
    }

    private ArrayList<Booking> getBookings() {

        Log.d(TAG, "getBookings: " + currClient.getClientID());
        String sql = "SELECT bookingID, clientName, b.packageType, cityName, vehicleName, vehicleType, dateOfBooking, fromDate, days, nights, packagePrice, agencyName, b.vehicleID,p.agencyID, b.cityID, b.packageID,b.clientID, h.hotelName, b.hotelID " +
                "FROM booking b,client c,TouristCity t,Vehicle v,Package p,agencies a, hotelInformation h " +
                "WHERE b.clientID = c.clientID and b.packageID = p.packageID and b.cityID = t.cityID and b.vehicleID = v.vehicleID and p.agencyID = a.agencyID and b.hotelID = h.hotelID and b.clientID = '" + currClient.getClientID() + "';";
        Cursor cur = mDb.rawQuery(sql, null);

        ArrayList<Booking> bookings = new ArrayList<>();

        while(cur!=null && cur.moveToNext()){
            Booking booking = new Booking(cur.getString(0),
                    cur.getString(1),
                    cur.getString(2),
                    cur.getString(3),
                    cur.getString(4),
                    cur.getString(5),
                    cur.getString(6),
                    cur.getString(7),
                    cur.getString(8),
                    cur.getString(9),
                    cur.getString(10),
                    cur.getString(11),
                    cur.getString(12),
                    cur.getString(13),
                    cur.getString(14),
                    cur.getString(15),
                    cur.getString(16),
                    cur.getString(17),
                    cur.getString(18));
            bookings.add(booking);

        }
        Log.d(TAG, "onItemSelected: bookings" + bookings.size());

        return bookings;


    }

}
