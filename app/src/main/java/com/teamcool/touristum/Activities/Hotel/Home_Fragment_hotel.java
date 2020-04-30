package com.teamcool.touristum.Activities.Hotel;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.teamcool.touristum.Activities.LoginActivity;
import com.teamcool.touristum.DatabaseHelper;
import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.Booking;
import com.teamcool.touristum.data.model.Client;
import com.teamcool.touristum.data.model.Filter;
import com.teamcool.touristum.data.model.Hotel;
import com.teamcool.touristum.data.model.Package;

import java.util.ArrayList;
import java.util.List;

public class Home_Fragment_hotel extends Fragment implements hoteladapter_home.onBookingClickListner{
    private View v;
    private hoteladapter_home adapter;
    private Hotel hotel;

    private RecyclerView recyclerView;
    private EditText search_bar;
    private DatabaseHelper helper;
    private SQLiteDatabase db;
    private AlertDialog.Builder builder;
    private List<Booking> bookings;

    private EditText et_searchHotel,et_search_filterHotel;

    private ArrayList<Filter> booking_filter;
    private String[] filter_options;
    private View dialog_view;
    private Spinner spinner;
    private ImageButton imageButton;
    private ChipGroup chipGroup;
    private TextView bookingID,packageID,clientId,VehicelID,CityID,hotelID;
    private EditText type,bookingdate,fromdate,price,edit_filter;
    private Button update,delete;

    private int no_fd=1,n_days=1,n_nights=1,n_price=1;

    private int SEARCH_MODE;
    public static final int SEARCH_MODE_FILTER = 1;
    public static final int SEARCH_MODE_NON_FILTER = 2;

    public static final String TAG = "HomeFragmentHotel";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.hotel_home,container,false);
        hotel= LoginActivity.getLoggedInHotel();

        recyclerView=v.findViewById(R.id.recycle);
        et_search_filterHotel = v.findViewById(R.id.et_searchHotel);
        et_searchHotel = v.findViewById(R.id.et_filterHotel);

        et_searchHotel.setFocusable(false);
        et_search_filterHotel.setFocusable(false);

        helper = new DatabaseHelper(getContext());
        db = helper.getWritableDatabase();

        bookings=getBookings();
        booking_filter=new ArrayList<>();
        builder = new AlertDialog.Builder(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new hoteladapter_home(bookings,getContext(),this);
        recyclerView.setAdapter(adapter);


        et_searchHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SEARCH_MODE = SEARCH_MODE_FILTER;
                setup();
            }
        });
//
        et_search_filterHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SEARCH_MODE = SEARCH_MODE_NON_FILTER;
                setup();
            }
        });
//        search_bar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog_view=getLayoutInflater().inflate(R.layout.search_filter,null);
//                spinner=dialog_view.findViewById(R.id.sp_filter1);
//                edit_filter=dialog_view.findViewById(R.id.et_filter1);
//                imageButton=dialog_view.findViewById(R.id.bv_filter1);
//                chipGroup=dialog_view.findViewById(R.id.cg_filters1);
//                addChips();
//                filter_options=new String[]{"BookingID","ClientID","PackageID","cityID","VehicleID","agencyID","ClientName","PackageType","CityName","VehicleName","VehicleType","DateOfBooking","FromDate","Days","Nights","Price", "agencyName","hotelID","HotelName"};
//                builder.setTitle("Filter Bookings");
//                launchDialog();
//            }
//        });
        Log.d("home","bboking");
        return v;
    }

    private void setup() {
        Log.d(TAG, "setup: ");
        
        dialog_view=getLayoutInflater().inflate(R.layout.search_filter,null);
        spinner=dialog_view.findViewById(R.id.sp_filter1);
        edit_filter=dialog_view.findViewById(R.id.et_filter1);
        imageButton=dialog_view.findViewById(R.id.bv_filter1);
        chipGroup=dialog_view.findViewById(R.id.cg_filters1);

        addChips();

        filter_options=new String[]{"BookingID","ClientID","PackageID","cityID","VehicleID","agencyID","ClientName","PackageType","CityName","VehicleName","VehicleType","DateOfBooking","FromDate","Days","Nights","Price", "agencyName","hotelID","HotelName"};
        builder.setTitle("Filter Bookings");

        launchDialog();
    }

    private void launchDialog() {

        Log.d(TAG, "launchDialog: ");
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, filter_options);
        spinner.setAdapter(filterAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String type = filter_options[position];
                edit_filter.setHint(type);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!((type.equalsIgnoreCase("fromDate") && no_fd>2) || (type.equalsIgnoreCase("price") && n_price > 2))) {
                            if (edit_filter.getText().length() != 0) {
                                Filter filter;
                                if (SEARCH_MODE == SEARCH_MODE_FILTER)
                                    filter = new Filter(type, edit_filter.getText().toString(), false);
                                else
                                    filter = new Filter(type, edit_filter.getText().toString(), true);

                                booking_filter.add(filter);
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
                                            else if(type.equalsIgnoreCase("price"))
                                                n_price -= 1;
                                        }
                                        else
                                            removeFromFilter(new Filter(type, text, true));

                                        chipGroup.removeView(chip);
                                    }
                                });
                                chipGroup.addView(chip);

                                if (type.equalsIgnoreCase("fromDate"))
                                    no_fd += 1;
                                else if (type.equalsIgnoreCase("price"))
                                    n_price += 1;
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

    private ArrayList<Booking> getBookings() {

        Log.d(TAG, "getBookings: ");

        String sql = "SELECT bookingID, clientName, b.packageType, cityName, vehicleName, vehicleType, dateOfBooking, fromDate, days, nights, packagePrice, agencyName, b.vehicleID,p.agencyID, b.packageID, b.clientID,h.cityID, h.hotelName, b.hotelID " +
                "FROM booking b,client c,TouristCity t,Vehicle v,Package p,agencies a, hotelinformation h " +
                "WHERE b.clientID = c.clientID and b.packageID = p.packageID and h.cityID = t.cityID and b.vehicleID = v.vehicleID and p.agencyID = a.agencyID and b.hotelID = h.hotelID and h.hotelID = '"+hotel.getHotelID()+"' ;";
        Cursor cur = db.rawQuery(sql, null);

        Log.d(TAG, "getBookings: "+ sql);

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

    @Override
    public void selectedBooking(int position) {
//        final Booking booking=bookings.get(position);
//        Log.d("test", String.valueOf(position));
//        Log.d("test",booking.getBookingID());
//        final View view = getLayoutInflater().inflate(R.layout.bookingpopup, null);
//        bookingID=view.findViewById(R.id.home_booking_id);
//        bookingID.setText("BookingID : "+booking.getBookingID());
//        clientId=view.findViewById(R.id.home_client_id);
//        clientId.setText("ClientID : "+booking.getClientID());
//        packageID=view.findViewById(R.id.home_package_id);
//        packageID.setText("PackageID : "+booking.getPackageID());
//        VehicelID=view.findViewById(R.id.home_vehicle_id);
//        VehicelID.setText("BookingID : "+booking.getVehicleID());
//        CityID=view.findViewById(R.id.home_city_id);
//        CityID.setText("CityID : "+booking.getCityID());
//        type=view.findViewById(R.id.home_type);
//        type.setText(booking.getPackageType());
//        bookingdate=view.findViewById(R.id.home_booking_date);
//        bookingdate.setText(booking.getDateOfBooking());
//        fromdate=view.findViewById(R.id.home_fromb_date);
//        fromdate.setText(booking.getFromDate());
//        price=view.findViewById(R.id.home_package_price);
//        price.setText(booking.getPrice());
//        hotelID=view.findViewById(R.id.home_hotel_id);
//        hotelID.setText("HotelID: "+booking.getHotelID());
//        builder.setView(view);
//        final AlertDialog dialog;
//        dialog=builder.create();
//
//        update=view.findViewById(R.id.home_but_update);
//        update.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ContentValues contentValues = new ContentValues();
//                contentValues.put("BookingID",booking.getBookingID());
//                contentValues.put("ClientID",booking.getClientID());
//                contentValues.put("PackageID",booking.getPackageID());
//                contentValues.put("PackageType",type.getText().toString());
//                contentValues.put("CityID",booking.getCityID());
//                contentValues.put("VehicleID",booking.getVehicleID());
//                contentValues.put("DateOfBooking",bookingdate.getText().toString());
//                contentValues.put("FromDate",fromdate.getText().toString());
//                contentValues.put("Price",price.getText().toString());
//                contentValues.put("HotelID",booking.getHotelID());
//                //Log.d(TAG,curr.getClientName());
//                db.update("Booking",contentValues,"BookingID = ?",new String[]{booking.getBookingID()});
//                updateData();
//                dialog.dismiss();
//            }
//        });
//        delete=view.findViewById(R.id.home_but_delete);
//        delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                db.delete("Booking","BookingID = ?",new String[]{booking.getBookingID()});
//                updateData();
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
    }

    private void updateData() {
        ArrayList<Booking> b=getFilteredBookings(booking_filter);
        adapter.setBookings(b);
        adapter.notifyDataSetChanged();
    }
    private void addChips() {
        int size=booking_filter.size();
        for(int i=0;i<size;i++){
            final Chip chip = new Chip(chipGroup.getContext());
            chip.setCloseIconVisible(true);
            chip.setText(booking_filter.get(i).getType() + ":" + booking_filter.get(i).getFilter());
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = chip.getText().toString().split(":")[1];
                    String type = chip.getText().toString().split(":")[0];

                    if(SEARCH_MODE == SEARCH_MODE_FILTER) {
                        removeFromFilter(new Filter(type, text, false));
                        if(type.equalsIgnoreCase("fromDate"))
                            no_fd -= 1;
                        else if(type.equalsIgnoreCase("price"))
                            n_price -= 1;
                    }
                    else
                        removeFromFilter(new Filter(type,text,true));

                    chipGroup.removeView(chip);
                }
            });
            chipGroup.addView(chip);
        }
    }

    private void removeFromFilter(Filter filter) {
        booking_filter.remove(filter);
    }

    private ArrayList<Booking> getFilteredBookings(ArrayList<Filter> booking_filters) {

        String sql = generateSqlBookings(booking_filters);
        Cursor cur;
        cur = db.rawQuery(sql, null);

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
        Log.d(TAG, "generated: bookings" + bookings.size());

        cur.close();

        String filter;

        String date1 = "",date2 = "";
        int price1=0, price2=0;
        int date_count=0,price_count = 0;

        for(int i=0;i<booking_filters.size();i++){
            if(!booking_filters.get(i).isSearch()){

                if(booking_filters.get(i).getType().equalsIgnoreCase("FromDate")){
                    if(date_count == 0){
                        date_count += 1;
                        date1 = booking_filters.get(i).getFilter();
                    }
                    else if(date_count == 1){
                        date_count += 1;
                        date2 = booking_filters.get(i).getFilter();

                        if(date1.compareTo(date2) > 0 ){
                            String temp = date1;
                            date1 = date2;
                            date2 = temp;
                        }
                    }

                }
                else if(booking_filters.get(i).getType().equalsIgnoreCase("price")){
                    if(price_count == 0){
                        price_count += 1;
                        price1 = Integer.parseInt(booking_filters.get(i).getFilter());
                    }
                    else if(price_count == 1){
                        price_count += 1;
                        price2 = Integer.parseInt(booking_filters.get(i).getFilter());

                        if(price1 > price2 ){
                            int temp = price1;
                            price1 = price2;
                            price2 = temp;
                        }
                    }

                }
            }
        }



        Log.d(TAG, "getFilteredBookings: " + bookings.size());

        int i=0;
        boolean flag = false;
        while(i<bookings.size()){

            flag = false;

            Log.d(TAG, "getFilteredBookings: " + i);

            if(date_count == 1){
                if(bookings.get(i).getFromDate().compareTo(date1) < 0 ) {
                    bookings.remove(i);
                    flag = true;
                }
            }
            else if(date_count == 2){
                if(bookings.get(i).getFromDate().compareTo(date1) < 0 ) {
                    bookings.remove(i);
                    flag = true;

                }
                else if(bookings.get(i).getFromDate().compareTo(date1) > 0 && bookings.get(i).getFromDate().compareTo(date2) > 0){
                    bookings.remove(i);
                    flag = true;
                }

            }


            if(price_count == 1){
                if(Integer.parseInt(bookings.get(i).getPrice()) < price1) {
                    bookings.remove(i);
                    flag = true;

                }

            }
            else if(price_count == 2){
                if(Integer.parseInt(bookings.get(i).getPrice()) < price1 ) {
                    bookings.remove(i);
                    flag = true;

                }
                else if(Integer.parseInt(bookings.get(i).getPrice()) > price1 && Integer.parseInt(bookings.get(i).getPrice()) > price2){
                    bookings.remove(i);
                    flag = true;

                }
            }

            if(!flag)
                i += 1;
        }

        Toast.makeText(getContext(), "Number of bookings : " + bookings.size(), Toast.LENGTH_SHORT).show();

        return bookings;
    }
    private String generateSqlBookings(ArrayList<Filter> booking_filters) {

        StringBuilder sql = new StringBuilder("SELECT bookingID, clientName, b.packageType, cityName, vehicleName, vehicleType, dateOfBooking, fromDate, days, nights, b.price, agencyName,b.vehicleID,p.agencyID, b.packageID, b.clientID,b.cityID,h.hotelName,b.hotelID " +
                "FROM Booking b,Client c,TouristCity t,vehicle v,package p,agencies a, hotelinformation h " +
                "WHERE b.clientID = c.clientID and b.packageID = p.packageID and b.cityID = t.cityID and b.vehicleID = v.vehicleID and p.agencyID = a.agencyID and b.hotelID = h.hotelID and h.HotelID = '"+hotel.getHotelID()+"' ");

        for(int i=0;i<booking_filters.size();i++){

            if(booking_filters.get(i).isSearch()) {

                sql.append("and ");


                if (booking_filters.get(i).getType().equalsIgnoreCase("BookingID")) {
                    sql.append("b.bookingID = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("ClientName")) {
                    sql.append("c.ClientName = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("PackageType")) {
                    sql.append("b.PackageType = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("CityName")) {
                    sql.append("t.cityName = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("VehicleName")) {
                    sql.append("v.vehicleName = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("VehicleType")) {
                    sql.append("v.vehicleType = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("DateOfBooking")) {
                    sql.append("b.DateOfBooking = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("FromDate")) {
                    sql.append("b.FromDate = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("Days")) {
                    sql.append("p.Days = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("Nights")) {
                    sql.append("p.Nights = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("Price")) {
                    sql.append("b.price = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("agencyName")) {
                    sql.append("a.agencyName = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("PackageID")) {
                    sql.append("b.PackageID = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("cityID")) {
                    sql.append("b.cityID = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("VehicleID")) {
                    sql.append("b.VehicleID = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("ClientID")) {
                    sql.append("b.ClientID = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("AgencyID")) {
                    sql.append("p.agencyID = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("HotelID")) {
                    sql.append("b.hotelID = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("HotelName")) {
                    sql.append("h.hotelName = '" + booking_filters.get(i).getFilter() + "' ");

                }

            }

        }

        boolean inside = false;

        int maxDays = 0,maxNights = 0;
        for(int i=0;i<booking_filters.size();i++){
            if(!booking_filters.get(i).isSearch()) {
                if(booking_filters.get(i).getType().equalsIgnoreCase("days")) {
                    if (Integer.parseInt(booking_filters.get(i).getFilter()) > maxDays) {
                        maxDays = Integer.parseInt(booking_filters.get(i).getFilter());

                    }
                }
                else if(booking_filters.get(i).getType().equalsIgnoreCase("nights")) {
                    if (Integer.parseInt(booking_filters.get(i).getFilter()) > maxNights) {
                        maxNights = Integer.parseInt(booking_filters.get(i).getFilter());

                    }
                }
            }
        }

        int i=0;
        boolean flag = false;

        while(i < booking_filters.size()){
            flag = false;
            if(!booking_filters.get(i).isSearch()) {
                if(booking_filters.get(i).getType().equalsIgnoreCase("days")) {
                    if (Integer.parseInt(booking_filters.get(i).getFilter()) < maxDays) {
                        booking_filters.remove(i);
                        flag = true;

                    }
                }
                else if(booking_filters.get(i).getType().equalsIgnoreCase("nights")) {
                    if (Integer.parseInt(booking_filters.get(i).getFilter()) < maxNights) {
                        booking_filters.remove(i);
                        flag = true;
                    }
                }
            }
            if(!flag)
                i+=1;
        }

        for(i=0;i<booking_filters.size();i++){

            if(!booking_filters.get(i).isSearch()) {
                if (inside) {
                    sql.append("or ");

                }

                if (!inside && !(booking_filters.get(i).getType().equalsIgnoreCase("fromDate") || booking_filters.get(i).getType().equalsIgnoreCase("price"))) {
                    sql.append("and (");
                    inside = true;
                }


                if (booking_filters.get(i).getType().equalsIgnoreCase("BookingID")) {
                    sql.append("b.bookingID = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("ClientName")) {
                    sql.append("c.ClientName = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("PackageType")) {
                    sql.append("b.PackageType = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("CityName")) {
                    sql.append("t.cityName = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("VehicleName")) {
                    sql.append("v.vehicleName = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("VehicleType")) {
                    sql.append("v.vehicleType = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("DateOfBooking")) {
                    sql.append("b.DateOfBooking = '" + booking_filters.get(i).getFilter() + "' ");

                }
                else if (booking_filters.get(i).getType().equalsIgnoreCase("FromDate")) {
//                    sql.append("b.FromDate = '" + booking_filters.get(i).getFilter() + "' ");
                    if(inside)
                        sql = new StringBuilder(sql.substring(0,sql.length() - 3));

                }
                else if (booking_filters.get(i).getType().equalsIgnoreCase("Days")) {
                    if(Integer.parseInt(booking_filters.get(i).getFilter()) == maxDays) {
                        sql.append("p.Days >= '" + booking_filters.get(i).getFilter() + "' ");
                    }


                } else if (booking_filters.get(i).getType().equalsIgnoreCase("Nights")) {
                    if(Integer.parseInt(booking_filters.get(i).getFilter()) == maxNights) {
                        sql.append("p.Nights >= '" + booking_filters.get(i).getFilter() + "' ");
                    }

                }
                else if (booking_filters.get(i).getType().equalsIgnoreCase("Price")) {
//                    sql.append("b.price = '" + booking_filters.get(i).getFilter() + "' ");
                    if(inside)
                        sql = new StringBuilder(sql.substring(0,sql.length() - 3));
                }
                else if (booking_filters.get(i).getType().equalsIgnoreCase("agencyName")) {
                    sql.append("a.agencyName = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("PackageID")) {
                    sql.append("b.PackageID = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("cityID")) {
                    sql.append("b.cityID = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("VehicleID")) {
                    sql.append("b.VehicleID = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("ClientID")) {
                    sql.append("b.ClientID = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("AgencyID")) {
                    sql.append("p.agencyID = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("HotelID")) {
                    sql.append("b.hotelID = '" + booking_filters.get(i).getFilter() + "' ");

                } else if (booking_filters.get(i).getType().equalsIgnoreCase("HotelName")) {
                    sql.append("h.hotelName = '" + booking_filters.get(i).getFilter() + "' ");

                }

            }

        }

        if(inside)
            sql.append(")");


        Log.d(TAG, "generatedSql: " + sql.toString());

        return sql.toString();
    }
}
