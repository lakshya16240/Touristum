package com.teamcool.touristum.ui.home;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.teamcool.touristum.Activities.EditBookingActivity;
import com.teamcool.touristum.Activities.LoginActivity;
import com.teamcool.touristum.Adapters.ManagerHomeAdapter;
import com.teamcool.touristum.DatabaseHelper;
import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.Agency;
import com.teamcool.touristum.data.model.Booking;
import com.teamcool.touristum.data.model.Client;
import com.teamcool.touristum.data.model.Filter;
import com.teamcool.touristum.data.model.Hotel;
import com.teamcool.touristum.data.model.Package;
import com.teamcool.touristum.data.model.TouristCity;
import com.teamcool.touristum.ui.AddBooking.AddBookingFragment;
import com.teamcool.touristum.ui.Reviews.ViewReviewFragment;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {

    private Spinner sp_manager_options, sp_dialog_filter;
    private ChipGroup cg_filters;
    private ImageButton bv_filter;
    private RecyclerView rv_data;
    private EditText et_search, et_filter,et_search_filter;
    private FloatingActionButton fab_addBooking;

    private ManagerHomeAdapter managerHomeAdapter;
    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;

    private ArrayList<Booking> bookings;
    private ArrayList<Agency> agencies;
    private ArrayList<Client> clients;
    private ArrayList<Package> packages;
    private ArrayList<TouristCity> cities;
    private ArrayList<Hotel> hotels;
    private ArrayList<Filter> booking_filters;
    private ArrayList<Filter> client_filters;
    private ArrayList<Filter> hotel_filters;
    private ArrayList<Filter> package_filters;
    private ArrayList<Filter> agency_filters;
    private ArrayList<Filter> city_filters;

    private int no_fd=1,n_days=1,n_nights=1,n_price=1,n_packPrice=1;

    private String[] filter_options;
    private String[] manager_options;

    private AlertDialog.Builder builder,bookingBuilder;
    private View dialog_view;

    public static final int VIEW_MODE_BOOKING = 1;
    public static final int VIEW_MODE_AGENCY = 2;
    public static final int VIEW_MODE_PACKAGE = 3;
    public static final int VIEW_MODE_HOTEL = 4;
    public static final int VIEW_MODE_CLIENT = 5;
    public static final int VIEW_MODE_CITY = 6;
    private int VIEW_MODE;

    private int SEARCH_MODE;
    public static final int SEARCH_MODE_FILTER = 1;
    public static final int SEARCH_MODE_NON_FILTER = 2;

    public static int LOGGED_IN_MODE ;

    public static final int BOOKING_UPDATE = 2;

    public static final String TAG = "Home Fragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        booking_filters = new ArrayList<>();
        client_filters = new ArrayList<>();
        package_filters = new ArrayList<>();
        hotel_filters = new ArrayList<>();
        agency_filters = new ArrayList<>();
        city_filters = new ArrayList<>();

        sp_manager_options = root.findViewById(R.id.sp_manager_options);
        rv_data = root.findViewById(R.id.rv_data);
        et_search = root.findViewById(R.id.et_filter);
        et_search_filter = root.findViewById(R.id.et_search);
        fab_addBooking = root.findViewById(R.id.fab_addBooking);

        builder = new AlertDialog.Builder(getContext());

        managerHomeAdapter = new ManagerHomeAdapter(getContext(), new ManagerHomeAdapter.onBookingClickListener() {
            @Override
            public void selectedBooking(Booking booking) {

                if (LOGGED_IN_MODE == LoginActivity.LOGGED_IN_EMPLOYEE) {

                    Intent intent = new Intent(getActivity(), EditBookingActivity.class);
                    intent.putExtra("EditBooking", booking);
                    startActivityForResult(intent, BOOKING_UPDATE);
                }

            }
        }, new ManagerHomeAdapter.onBookingLongClickListener() {
            @Override
            public void selectedBooking(Booking booking) {
                if (LOGGED_IN_MODE == LoginActivity.LOGGED_IN_EMPLOYEE) {
                    Log.d(TAG, "selectedBooking: ");
                    final Booking b = booking;

                    View view = getLayoutInflater().inflate(R.layout.booking_popup, null);
                    builder.setView(view);

                    final AlertDialog dialog = builder.create();
                    dialog.show();

                    view.findViewById(R.id.bv_updateBooking).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), EditBookingActivity.class);
                            intent.putExtra("EditBooking", b);
                            startActivityForResult(intent, BOOKING_UPDATE);

                        }
                    });

                    view.findViewById(R.id.bv_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelBooking(b);
                            dialog.dismiss();


                        }
                    });
                }

            }
        }, new ManagerHomeAdapter.onPackageClickListener() {
            @Override
            public void selectedPackage(Package pack) {

                ViewReviewFragment.VIEW_MODE_REVIEWS = VIEW_MODE;
                FragmentManager manager = getFragmentManager();
                Fragment fragment = new ViewReviewFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putSerializable("SelectedPackage", pack);
                fragment.setArguments(bundle);

                if(LOGGED_IN_MODE == LoginActivity.LOGGED_IN_CLIENT)
                    transaction.replace(R.id.nav_host_fragment_client, fragment);
                else if(LOGGED_IN_MODE == LoginActivity.LOGGED_IN_EMPLOYEE ) {
                    if(LoginActivity.getLoggedInEmployee().getEmp_type().equalsIgnoreCase("customer_executive"))
                        transaction.replace(R.id.nav_host_fragment_customer_executive, fragment);
                    else
                        transaction.replace(R.id.nav_host_fragment, fragment);

                }
                transaction.addToBackStack(null);
                transaction.commit();


            }
        }, new ManagerHomeAdapter.onPackageLongClickListener() {
            @Override
            public void selectedPackage(final Package pack) {

                if (LOGGED_IN_MODE == LoginActivity.LOGGED_IN_EMPLOYEE && (LoginActivity.getLoggedInEmployee().getEmp_type().equalsIgnoreCase("manager") || LoginActivity.getLoggedInEmployee().getEmp_type().equalsIgnoreCase("CEO"))) {

                    final View view = getLayoutInflater().inflate(R.layout.package_price_popup, null);
                    builder.setView(view);

                    final AlertDialog dialog;
                    builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                String newPrice = ((EditText) view.findViewById(R.id.et_newPrice)).getText().toString();
                                updatePrice(newPrice, pack.getPackageID());
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


                    dialog = builder.create();
                    dialog.show();
                } else if (LOGGED_IN_MODE == LoginActivity.LOGGED_IN_CLIENT) {
                    AddBookingFragment.LOGGED_IN_MODE = LOGGED_IN_MODE;
                    FragmentManager manager = getFragmentManager();
                    Fragment fragment = new AddBookingFragment();
                    FragmentTransaction transaction = manager.beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("SelectedPackage", pack);
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.nav_host_fragment_client, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }

            }
        }, new ManagerHomeAdapter.onCityClickListener() {
            @Override
            public void selectedCity(TouristCity city) {
                ViewReviewFragment.VIEW_MODE_REVIEWS = VIEW_MODE;
                FragmentManager manager = getFragmentManager();
                Fragment fragment = new ViewReviewFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putSerializable("SelectedCity", city);
                fragment.setArguments(bundle);

                if(LOGGED_IN_MODE == LoginActivity.LOGGED_IN_CLIENT)
                    transaction.replace(R.id.nav_host_fragment_client, fragment);
                else if(LOGGED_IN_MODE == LoginActivity.LOGGED_IN_EMPLOYEE ) {
                    if(LoginActivity.getLoggedInEmployee().getEmp_type().equalsIgnoreCase("customer_executive"))
                        transaction.replace(R.id.nav_host_fragment_customer_executive, fragment);
                    else
                        transaction.replace(R.id.nav_host_fragment, fragment);

                }
                transaction.addToBackStack(null);
                transaction.commit();


            }
        }, new ManagerHomeAdapter.onHotelClickListener() {
            @Override
            public void selectedCHotel(Hotel hotel) {

                ViewReviewFragment.VIEW_MODE_REVIEWS = VIEW_MODE;
                FragmentManager manager = getFragmentManager();
                Fragment fragment = new ViewReviewFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putSerializable("SelectedHotel", hotel);
                fragment.setArguments(bundle);

                if(LOGGED_IN_MODE == LoginActivity.LOGGED_IN_CLIENT)
                    transaction.replace(R.id.nav_host_fragment_client, fragment);
                else if(LOGGED_IN_MODE == LoginActivity.LOGGED_IN_EMPLOYEE ) {
                    if(LoginActivity.getLoggedInEmployee().getEmp_type().equalsIgnoreCase("customer_executive"))
                        transaction.replace(R.id.nav_host_fragment_customer_executive, fragment);
                    else
                        transaction.replace(R.id.nav_host_fragment, fragment);

                }
                transaction.addToBackStack(null);
                transaction.commit();



            }
        });

        fab_addBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                Fragment fragment = new AddBookingFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment,fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        rv_data.setLayoutManager(new LinearLayoutManager(getContext()));

        mDbHelper = new DatabaseHelper(getContext());
        mDb = mDbHelper.getReadableDatabase();

        et_search.setFocusable(false);
        et_search_filter.setFocusable(false);

        et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SEARCH_MODE = SEARCH_MODE_FILTER;
                setup();
            }
        });

        et_search_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SEARCH_MODE = SEARCH_MODE_NON_FILTER;
                setup();
            }
        });


        if(LOGGED_IN_MODE == LoginActivity.LOGGED_IN_EMPLOYEE)
            manager_options = new String[]{"Bookings", "Hotels", "Packages", "Agencies","Clients","Cities"};
        else if(LOGGED_IN_MODE == LoginActivity.LOGGED_IN_CLIENT)
            manager_options = new String[]{"Hotels", "Packages","Cities"};

        bookings = getBookings();
        clients = getClients();
        packages = getPackages();
        hotels = getHotels();
        agencies = getAgencies();
        cities = getCities();

        sp_manager_options.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                et_search.setHint("Filter " + manager_options[position]);
                et_search_filter.setHint("Search " + manager_options[position]);
                fab_addBooking.setVisibility(View.GONE);
                if(manager_options[position].equals("Bookings")){

                    if(LOGGED_IN_MODE == LoginActivity.LOGGED_IN_EMPLOYEE && LoginActivity.getLoggedInEmployee().getEmp_type().equalsIgnoreCase("manager"))
                        fab_addBooking.setVisibility(View.VISIBLE);

                    managerHomeAdapter.setBookings(bookings);
                    managerHomeAdapter.setViewMode(ManagerHomeAdapter.VIEW_MODE_BOOKING);
                    rv_data.setAdapter(managerHomeAdapter);
                    VIEW_MODE = VIEW_MODE_BOOKING;

                }
                else if(manager_options[position].equals("Hotels")){


                    managerHomeAdapter.setHotels(hotels);
                    managerHomeAdapter.setViewMode(ManagerHomeAdapter.VIEW_MODE_HOTEL);
                    rv_data.setAdapter(managerHomeAdapter);
                    VIEW_MODE = VIEW_MODE_HOTEL;

                }
                else if(manager_options[position].equals("Packages")){


                    managerHomeAdapter.setPackages(packages);
                    managerHomeAdapter.setViewMode(ManagerHomeAdapter.VIEW_MODE_PACKAGE);
                    rv_data.setAdapter(managerHomeAdapter);
                    VIEW_MODE = VIEW_MODE_PACKAGE;

                }
                else if(manager_options[position].equals("Agencies")){


                    managerHomeAdapter.setAgencies(agencies);
                    managerHomeAdapter.setViewMode(ManagerHomeAdapter.VIEW_MODE_AGENCY);
                    rv_data.setAdapter(managerHomeAdapter);
                    VIEW_MODE = VIEW_MODE_AGENCY;

                }
                else if(manager_options[position].equals("Clients")){


                    managerHomeAdapter.setClients(clients);
                    managerHomeAdapter.setViewMode(ManagerHomeAdapter.VIEW_MODE_CLIENT);
                    rv_data.setAdapter(managerHomeAdapter);
                    VIEW_MODE = VIEW_MODE_CLIENT;

                }
                else if(manager_options[position].equals("Cities")){


                    managerHomeAdapter.setCities(cities);
                    managerHomeAdapter.setViewMode(ManagerHomeAdapter.VIEW_MODE_CITY);
                    rv_data.setAdapter(managerHomeAdapter);
                    VIEW_MODE = VIEW_MODE_CITY;

                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, manager_options){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // this part is needed for hiding the original view
                View view = super.getView(position, convertView, parent);
                view.setVisibility(View.GONE);

                return view;
            }
        };

        sp_manager_options.setAdapter(dataAdapter);

        return root;
    }

    public void setup(){

        dialog_view = getLayoutInflater().inflate(R.layout.dialog_filter,null);
        sp_dialog_filter = dialog_view.findViewById(R.id.sp_filter);
        et_filter = dialog_view.findViewById(R.id.et_filter);
        bv_filter = dialog_view.findViewById(R.id.bv_filter);
        cg_filters = dialog_view.findViewById(R.id.cg_filters);

        addChips();

        if(VIEW_MODE == VIEW_MODE_BOOKING){

            filter_options = new String[]{"BookingID","ClientID","PackageID","cityID","VehicleID","agencyID","ClientName","PackageType","CityName","VehicleName","VehicleType","DateOfBooking","FromDate","Days","Nights","Price", "agencyName","hotelID","HotelName"};
            builder.setTitle("Filter Bookings");
        }
        else if(VIEW_MODE == VIEW_MODE_AGENCY){
            filter_options = new String[]{"AgencyID","AgencyName","AgencyAddress","AgencyContact","NoOfPackages"};
            builder.setTitle("Filter Agencies");
        }
        else if(VIEW_MODE == VIEW_MODE_HOTEL){
            filter_options = new String[]{"HotelID","HotelName","HotelCity","HotelLocation","HotelRating","AvailableRooms","cityID", "locationID"};
            builder.setTitle("Filter Hotels");
        }
        else if(VIEW_MODE == VIEW_MODE_CLIENT){
            filter_options = new String[]{"ClientID","ClientName","ClientContact","ClientAddress","ClientEmail","NoOfBookings"};
            builder.setTitle("Filter Clients");
        }
        else if(VIEW_MODE == VIEW_MODE_PACKAGE){
            filter_options = new String[]{"PackageID","AgencyName","PackageType","Days","Nights","City","PackagePrice","AgencyID","cityID"};
            builder.setTitle("Filter Packages");
        }
        else if(VIEW_MODE == VIEW_MODE_CITY){
            filter_options = new String[]{"cityID","cityName","rating","noOfLocations"};
            builder.setTitle("Filter Cities");
        }
        launchDialog();

    }


    private void updatePrice(String newPrice, String packageID) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("PackagePrice",newPrice);
        mDb.update("package",contentValues,"packageID = ?",new String[]{packageID});
        updateData();
    }

    private void cancelBooking(Booking b) {
        mDb.delete("booking","bookingID = ?",new String[]{b.getBookingID()});
        updateData();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BOOKING_UPDATE){
            if(resultCode == BOOKING_UPDATE){
                updateData();
            }
        }
    }

    private ArrayList<Booking> getFilteredBookings(ArrayList<Filter> booking_filters) {

        String sql = generateSqlBookings(booking_filters);
        Cursor cur;
        cur = mDb.rawQuery(sql, null);

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

    private ArrayList<Package> getFilteredPackages(ArrayList<Filter> package_filters) {
        String sql = generateSqlPackages(package_filters);

        Cursor cur;
        cur = mDb.rawQuery(sql, null);

        ArrayList<Package> packages = new ArrayList<>();

        while(cur!=null && cur.moveToNext()){
            Package aPackage = new Package(cur.getString(0),
                    cur.getString(1),
                    cur.getString(2),
                    cur.getString(3),
                    cur.getString(4),
                    cur.getString(5),
                    cur.getString(6),
                    cur.getString(7),
                    cur.getString(8));
            packages.add(aPackage);

        }
        Log.d(TAG, "onItemSelected: " + packages.size());

        cur.close();

        int packPrice1=0, packPrice2=0;
        int packprice_count = 0;

        for(int i=0;i<package_filters.size();i++){
            if(!package_filters.get(i).isSearch()){

                if(package_filters.get(i).getType().equalsIgnoreCase("packagePrice")){
                    if(packprice_count == 0){
                        packprice_count += 1;
                        packPrice1 = Integer.parseInt(package_filters.get(i).getFilter());
                    }
                    else if(packprice_count == 1){
                        packprice_count += 1;
                        packPrice2 = Integer.parseInt(package_filters.get(i).getFilter());
                        if(packPrice1 > packPrice2){
                            int temp = packPrice1;
                            packPrice1 = packPrice2;
                            packPrice2 = temp;
                        }
                    }

                }
            }
        }

        Log.d(TAG, "getFilteredPackages: " + packages.size());

        int i=0;
        boolean flag = false;
        while(i<packages.size()){

            flag = false;

            Log.d(TAG, "getFilteredPackages: " +  packPrice1 + " " + packPrice2);

            if(packprice_count == 1){
                if(Integer.parseInt(packages.get(i).getPrice())< packPrice1 ) {
                    packages.remove(i);
                    flag = true;

                }

            }
            else if(packprice_count == 2){
                if(Integer.parseInt(packages.get(i).getPrice())< packPrice1  ) {
                    packages.remove(i);
                    flag = true;

                }
                else if(Integer.parseInt(packages.get(i).getPrice()) > packPrice1  && Integer.parseInt(packages.get(i).getPrice())> packPrice2 ){
                    packages.remove(i);
                    flag = true;

                }
            }

            if(!flag)
                i += 1;
        }

        Toast.makeText(getContext(), "Number of packages : " + packages.size(), Toast.LENGTH_SHORT).show();


        return packages;

    }

    private ArrayList<Client> getFilteredClients(ArrayList<Filter> client_filters) {

        String sql = generateSqlClients(client_filters);
        Cursor cur;
        cur = mDb.rawQuery(sql, null);

        ArrayList<Client> clients = new ArrayList<>();

        while(cur!=null && cur.moveToNext()){

            Client client = new Client(cur.getString(0),
                    cur.getString(1),
                    String.format("%.0f",cur.getFloat(2)),
                    cur.getString(3),
                    cur.getString(4),
                    cur.getString(5));
            clients.add(client);

        }
        Log.d(TAG, "onItemSelected: " + clients.size());

        cur.close();


        Toast.makeText(getContext(), "Number of clients : " + clients.size(), Toast.LENGTH_SHORT).show();


        return clients;

    }

    private ArrayList<TouristCity> getFilteredCities(ArrayList<Filter> cityFilters) {

        String sql = generateSqlCities(city_filters);
        Cursor cur;
        cur = mDb.rawQuery(sql, null);

        ArrayList<TouristCity> cities = new ArrayList<>();

        while(cur!=null && cur.moveToNext()){
            TouristCity city = new TouristCity(cur.getString(0),
                    cur.getString(1),
                    cur.getString(2),
                    cur.getString(3));
            cities.add(city);

        }
        Log.d(TAG, "onItemSelected: " + cities.size());

        cur.close();

        Toast.makeText(getContext(), "Number of Cities : " + cities.size(), Toast.LENGTH_SHORT).show();


        return cities;

    }

    private ArrayList<Hotel> getFilteredHotels(ArrayList<Filter> hotel_filters) {

        String sql = generateSqlHotels(hotel_filters);
        Cursor cur;
        cur = mDb.rawQuery(sql, null);

        ArrayList<Hotel> hotels = new ArrayList<>();

        while(cur!=null && cur.moveToNext()){
            Hotel hotel = new Hotel(cur.getString(0),
                    cur.getString(1),
                    cur.getString(2),
                    cur.getString(3),
                    cur.getString(4),
                    cur.getString(5),
                    cur.getString(6),
                    cur.getString(7));
            hotels.add(hotel);

        }

        cur.close();

        Toast.makeText(getContext(), "Number of hotels : " + hotels.size(), Toast.LENGTH_SHORT).show();

        Log.d(TAG, "onItemSelected: " + hotels.size());
        return hotels;
    }

    private ArrayList<Agency> getFilteredAgencies(ArrayList<Filter> agency_filters) {

        String sql = generateSqlAgencies(agency_filters);

        Cursor cur;
        cur = mDb.rawQuery(sql, null);

        ArrayList<Agency> agencies = new ArrayList<>();


        while(cur!=null && cur.moveToNext()){
            Agency agency = new Agency(cur.getString(0),
                    cur.getString(1),
                    cur.getString(2),
                    String.format("%.0f",cur.getFloat(3)),
                    cur.getString(4));
            agencies.add(agency);

        }

        Log.d(TAG, "onItemSelected: " + agencies.size());

        cur.close();

        Toast.makeText(getContext(), "Number of agencies : " + agencies.size(), Toast.LENGTH_SHORT).show();


        return agencies;

    }


    private String generateSqlBookings(ArrayList<Filter> booking_filters) {

        StringBuilder sql = new StringBuilder("SELECT bookingID, clientName, b.packageType, cityName, vehicleName, vehicleType, dateOfBooking, fromDate, days, nights, b.price, agencyName,b.vehicleID,p.agencyID, b.cityID, b.packageID,b.clientID,h.hotelName,b.hotelID " +
                "FROM booking b,client c,TouristCity t,Vehicle v,Package p,agencies a, hotelInformation h " +
                "WHERE b.clientID = c.clientID and b.packageID = p.packageID and b.cityID = t.cityID and b.vehicleID = v.vehicleID and p.agencyID = a.agencyID and b.hotelID = h.hotelID ");

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

    private String generateSqlAgencies(ArrayList<Filter> agency_filters) {
        StringBuilder sql = new StringBuilder("SELECT agencyID, agencyName, agencyAddress, agencyContact, numberOfPackages " +
                "FROM agencies a ");

        Log.d(TAG, "generateSqlAgencies: " + agency_filters.get(0).isSearch());
        if(agency_filters.size() > 0)
            sql.append("WHERE ");

        boolean inside = false;
        for(int i=0;i<agency_filters.size();i++){

            if(agency_filters.get(i).isSearch()) {
                if (inside) {
                    sql.append("and ");

                }

                if(!inside){
                    inside = true;
                }


                if (agency_filters.get(i).getType().equalsIgnoreCase("agencyID")) {
                    sql.append("a.agencyID = '" + agency_filters.get(i).getFilter() + "' ");

                } else if (agency_filters.get(i).getType().equalsIgnoreCase("agencyName")) {
                    sql.append("a.agencyName = '" + agency_filters.get(i).getFilter() + "' ");

                } else if (agency_filters.get(i).getType().equalsIgnoreCase("agencyAddress")) {
                    sql.append("a.agencyAddress = '" + agency_filters.get(i).getFilter() + "' ");

                } else if (agency_filters.get(i).getType().equalsIgnoreCase("agencyContact")) {
                    sql.append("a.agencyContact = '" + agency_filters.get(i).getFilter() + "' ");

                } else if (agency_filters.get(i).getType().equalsIgnoreCase("NoOfPackages")) {
                    sql.append("a.numberOfPackages = '" + agency_filters.get(i).getFilter() + "' ");

                }
            }

        }

        boolean inside2 = false;
        int maxPackages = 0;

        for(int i=0;i<agency_filters.size();i++){
            if(!agency_filters.get(i).isSearch()){
                if(agency_filters.get(i).getType().equalsIgnoreCase("NoOfPackages"))
                    if(Integer.parseInt(agency_filters.get(i).getFilter()) > maxPackages)
                        maxPackages = Integer.parseInt(agency_filters.get(i).getFilter());

            }
        }
        int i=0;

        boolean flag = false;

        while(i<agency_filters.size()){
            flag = false;
            if(!agency_filters.get(i).isSearch()){
                if(agency_filters.get(i).getType().equalsIgnoreCase("NoOfPackages")) {
                    if (Integer.parseInt(agency_filters.get(i).getFilter()) < maxPackages) {
                        agency_filters.remove(i);
                        flag = true;
                    }
                }

            }
            if(!flag)
                 i += 1;
        }

        for(i=0;i<agency_filters.size();i++){

            if(!agency_filters.get(i).isSearch()) {

                if (inside2) {
                    sql.append("or ");
                }

                if(inside && !inside2){
                    sql.append("and (");
                    inside2 = true;
                }
                else if(!inside && !inside2){
                    inside2 = true;
                }

                if (agency_filters.get(i).getType().equalsIgnoreCase("agencyID")) {
                    sql.append("a.agencyID = '" + agency_filters.get(i).getFilter() + "' ");

                } else if (agency_filters.get(i).getType().equalsIgnoreCase("agencyName")) {
                    sql.append("a.agencyName = '" + agency_filters.get(i).getFilter() + "' ");

                } else if (agency_filters.get(i).getType().equalsIgnoreCase("agencyAddress")) {
                    sql.append("a.agencyAddress = '" + agency_filters.get(i).getFilter() + "' ");

                } else if (agency_filters.get(i).getType().equalsIgnoreCase("agencyContact")) {
                    sql.append("a.agencyContact = '" + agency_filters.get(i).getFilter() + "' ");

                } else if (agency_filters.get(i).getType().equalsIgnoreCase("NoOfPackages")) {
                    if(Integer.parseInt(agency_filters.get(i).getFilter()) == maxPackages)
                        sql.append("a.numberOfPackages >= '" + agency_filters.get(i).getFilter() + "' ");

                }

            }

        }

        if(inside && inside2)
            sql.append(")");

        Log.d(TAG, "generatedSql: " + sql.toString());

        return sql.toString();
    }

    private String generateSqlPackages(ArrayList<Filter> package_filters) {
        StringBuilder sql = new StringBuilder("SELECT packageID, agencyName, packageType, days, nights, cityName, packagePrice,p.agencyID,p.cityID" +
                " FROM Package p,agencies a, TouristCity c " +
                "WHERE p.agencyID = a.agencyID and p.cityID = c.cityID ");

        for(int i=0;i<package_filters.size();i++){

            if(package_filters.get(i).isSearch()) {

                sql.append("and ");

                if (package_filters.get(i).getType().equalsIgnoreCase("packageID")) {
                    Log.d(TAG, "generateSqlPackages: " + "herer");
                    sql.append("p.packageID = '" + package_filters.get(i).getFilter() + "' ");

                } else if (package_filters.get(i).getType().equalsIgnoreCase("agencyName")) {
                    sql.append("a.agencyName = '" + package_filters.get(i).getFilter() + "' ");

                } else if (package_filters.get(i).getType().equalsIgnoreCase("packageType")) {
                    sql.append("p.PackageType = '" + package_filters.get(i).getFilter() + "' ");

                } else if (package_filters.get(i).getType().equalsIgnoreCase("days")) {
                    sql.append("p.days = '" + package_filters.get(i).getFilter() + "' ");

                } else if (package_filters.get(i).getType().equalsIgnoreCase("nights")) {
                    sql.append("p.nights = '" + package_filters.get(i).getFilter() + "' ");

                } else if (package_filters.get(i).getType().equalsIgnoreCase("city")) {
                    sql.append("c.cityName = '" + package_filters.get(i).getFilter() + "' ");

                }
                else if (package_filters.get(i).getType().equalsIgnoreCase("packagePrice")) {
                    sql.append("p.packagePrice = '" + package_filters.get(i).getFilter() + "' ");

                }
                else if (package_filters.get(i).getType().equalsIgnoreCase("agencyID")) {
                    sql.append("p.agencyID = '" + package_filters.get(i).getFilter() + "' ");

                } else if (package_filters.get(i).getType().equalsIgnoreCase("cityID")) {
                    sql.append("p.cityID = '" + package_filters.get(i).getFilter() + "' ");

                }

            }

        }

        boolean inside = false;

        int maxDays = 0,maxNights = 0;
        for(int i=0;i<package_filters.size();i++){
            if(!package_filters.get(i).isSearch()) {
                if(package_filters.get(i).getType().equalsIgnoreCase("days")) {
                    if (Integer.parseInt(package_filters.get(i).getFilter()) > maxDays) {
                        maxDays = Integer.parseInt(package_filters.get(i).getFilter());

                    }
                }
                else if(package_filters.get(i).getType().equalsIgnoreCase("nights")){
                    if(Integer.parseInt(package_filters.get(i).getFilter()) > maxNights){
                        maxNights = Integer.parseInt(package_filters.get(i).getFilter());

                    }
                }
            }
        }

        boolean flag = false;
        int i= 0;

        while(i<package_filters.size()){
            flag = false;
            if(!package_filters.get(i).isSearch()) {
                if(package_filters.get(i).getType().equalsIgnoreCase("days")) {
                    if (Integer.parseInt(package_filters.get(i).getFilter()) < maxDays) {
                        package_filters.remove(i);
                        flag = true;

                    }
                }
                else if(package_filters.get(i).getType().equalsIgnoreCase("nights")) {
                    if (Integer.parseInt(package_filters.get(i).getFilter()) < maxNights) {
                        package_filters.remove(i);
                        flag = true;
                    }
                }
            }
            if(!flag)
                i++;
        }

        for(i=0;i<package_filters.size();i++){
            Log.d(TAG, "generateSqlPackages: " + "inside");

            if(!package_filters.get(i).isSearch()) {

                if (inside) {
                    sql.append("or ");

                }

                if (!inside && !(package_filters.get(i).getType().equalsIgnoreCase("packagePrice"))) {
                    sql.append("and (");
                    inside = true;
                }


                if (package_filters.get(i).getType().equalsIgnoreCase("packageID")) {
                    Log.d(TAG, "generateSqlPackages: " + "herer");
                    sql.append("p.packageID = '" + package_filters.get(i).getFilter() + "' ");

                } else if (package_filters.get(i).getType().equalsIgnoreCase("agencyName")) {
                    sql.append("a.agencyName = '" + package_filters.get(i).getFilter() + "' ");

                } else if (package_filters.get(i).getType().equalsIgnoreCase("packageType")) {
                    sql.append("p.PackageType = '" + package_filters.get(i).getFilter() + "' ");

                } else if (package_filters.get(i).getType().equalsIgnoreCase("days")) {
                    if(Integer.parseInt(package_filters.get(i).getFilter()) == maxDays)
                        sql.append("p.days >= '" + package_filters.get(i).getFilter() + "' ");

                } else if (package_filters.get(i).getType().equalsIgnoreCase("nights")) {
                    if(Integer.parseInt(package_filters.get(i).getFilter()) == maxNights)
                        sql.append("p.nights >= '" + package_filters.get(i).getFilter() + "' ");

                } else if (package_filters.get(i).getType().equalsIgnoreCase("city")) {
                    sql.append("c.cityName = '" + package_filters.get(i).getFilter() + "' ");

                }
                else if (package_filters.get(i).getType().equalsIgnoreCase("packagePrice")) {
//                    sql.append("p.packagePrice = '" + package_filters.get(i).getFilter() + "' ");
                    if(inside)
                        sql = new StringBuilder(sql.substring(0,sql.length() - 3));

                }
                else if (package_filters.get(i).getType().equalsIgnoreCase("agencyID")) {
                    sql.append("p.agencyID = '" + package_filters.get(i).getFilter() + "' ");

                } else if (package_filters.get(i).getType().equalsIgnoreCase("cityID")) {
                    sql.append("p.cityID = '" + package_filters.get(i).getFilter() + "' ");

                }



            }

        }

        if (inside) {
            sql.append(")");
        }

        Log.d(TAG, "generatedSql: " + sql.toString());

        return sql.toString();
    }

    private String generateSqlClients(ArrayList<Filter> client_filters) {
        StringBuilder sql = new StringBuilder("SELECT c.clientID, clientName, clientContact, clientAddress, clientEmail, count(b.bookingID) as NoOfBookings " +
                "FROM Client c, Booking b " +
                "WHERE c.clientID = b.clientID ");

        ArrayList<Filter> otherFilters = new ArrayList<>();
        boolean isOtherFilter = false;
        for(int i=0;i<client_filters.size();i++){
            if(!client_filters.get(i).getType().equalsIgnoreCase("NoOfBookings")){
                otherFilters.add(client_filters.get(i));
                isOtherFilter  = true;
            }
        }

        Collections.sort(client_filters);

        if(isOtherFilter) {

            for (int i = 0; i < client_filters.size(); i++) {

                if (client_filters.get(i).isSearch()) {

                    sql.append("and ");

                    if (client_filters.get(i).getType().equalsIgnoreCase("clientID")) {
                        sql.append("c.clientID = '" + client_filters.get(i).getFilter() + "' ");

                    } else if (client_filters.get(i).getType().equalsIgnoreCase("clientName")) {
                        sql.append("c.clientName = '" + client_filters.get(i).getFilter() + "' ");

                    } else if (client_filters.get(i).getType().equalsIgnoreCase("clientContact")) {
                        sql.append("c.clientContact = '" + client_filters.get(i).getFilter() + "' ");

                    } else if (client_filters.get(i).getType().equalsIgnoreCase("clientAddress")) {
                        sql.append("c.clientAddress = '" + client_filters.get(i).getFilter() + "' ");

                    } else if (client_filters.get(i).getType().equalsIgnoreCase("clientEmail")) {
                        sql.append("c.clientEmail = '" + client_filters.get(i).getFilter() + "' ");

                    }

                }

            }

            boolean inside = false;

            for (int i = 0; i < client_filters.size(); i++) {

                if (!client_filters.get(i).isSearch()) {

                    if (inside) {
                        sql.append("or ");

                    }

                    if (!inside) {
                        sql.append("and (");
                        inside = true;
                    }


                    if (client_filters.get(i).getType().equalsIgnoreCase("clientID")) {
                        sql.append("c.clientID = '" + client_filters.get(i).getFilter() + "' ");

                    } else if (client_filters.get(i).getType().equalsIgnoreCase("clientName")) {
                        sql.append("c.clientName = '" + client_filters.get(i).getFilter() + "' ");

                    } else if (client_filters.get(i).getType().equalsIgnoreCase("clientContact")) {
                        sql.append("c.clientContact = '" + client_filters.get(i).getFilter() + "' ");

                    } else if (client_filters.get(i).getType().equalsIgnoreCase("clientAddress")) {
                        sql.append("c.clientAddress = '" + client_filters.get(i).getFilter() + "' ");

                    } else if (client_filters.get(i).getType().equalsIgnoreCase("clientEmail")) {
                        sql.append("c.clientEmail = '" + client_filters.get(i).getFilter() + "' ");

                    }


                }


            }

            if (inside) {
                sql.append(") ");
            }
        }


        sql.append("GROUP BY c.clientID ");

        boolean flag = false;

        for(int i=0;i<client_filters.size();i++){

            if(client_filters.get(i).isSearch()) {

                if (!flag && client_filters.get(i).getType().equalsIgnoreCase("NoOfBookings")) {
                    sql.append("having NoOfBookings = " + client_filters.get(i).getFilter() + " ");
                    flag = true;

                } else if (flag && client_filters.get(i).getType().equalsIgnoreCase("NoOfBookings")) {
                    sql.append("and NoOfBookings = " + client_filters.get(i).getFilter() + " ");
                }
            }

        }

        int maxBookings = 0;

        for(int i=0;i<client_filters.size();i++){
            if(!client_filters.get(i).isSearch()){
                if(client_filters.get(i).getType().equalsIgnoreCase("NoOfBookings"))
                    if(Integer.parseInt(client_filters.get(i).getFilter()) > maxBookings)
                        maxBookings = Integer.parseInt(client_filters.get(i).getFilter());

            }
        }

        boolean alert = false;
        int i=0;

        while(i<client_filters.size()){
            alert = false;
            if(!client_filters.get(i).isSearch()){
                if(client_filters.get(i).getType().equalsIgnoreCase("NoOfBookings"))
                    if(Integer.parseInt(client_filters.get(i).getFilter()) < maxBookings){
                        client_filters.remove(i);
                        alert = true;
                    }


            }
            if(!alert)
                i++;

        }


        for(i=0;i<client_filters.size();i++){

            if(!client_filters.get(i).isSearch()) {

                if (!flag && client_filters.get(i).getType().equalsIgnoreCase("NoOfBookings")) {
                    if(Integer.parseInt(client_filters.get(i).getFilter()) == maxBookings) {
                        sql.append("having NoOfBookings = " + client_filters.get(i).getFilter() + " ");
                        flag = true;
                    }

                } else if (flag && client_filters.get(i).getType().equalsIgnoreCase("NoOfBookings")) {
                    if(Integer.parseInt(client_filters.get(i).getFilter()) == maxBookings) {
                        sql.append("and NoOfBookings >= " + client_filters.get(i).getFilter() + " ");
                    }
                }
            }

        }


        Log.d(TAG, "generatedSql: " + sql.toString());

        return sql.toString();
    }

    private String generateSqlHotels(ArrayList<Filter> hotel_filters) {
        StringBuilder sql = new StringBuilder("SELECT hotelID, hotelName, cityName, locationName, h.Rating, availableRooms,h.cityID, h.locationID" +
                " FROM hotelinformation h, touristCity c, touristlocations l " +
                "WHERE h.cityID = c.cityID and h.locationID = l.locationID ");

        Collections.sort(hotel_filters);

        for(int i=0;i<hotel_filters.size();i++){

            if(hotel_filters.get(i).isSearch()) {
                sql.append("and ");



                if (hotel_filters.get(i).getType().equalsIgnoreCase("hotelID")) {
                    sql.append("h.hotelID = '" + hotel_filters.get(i).getFilter() + "' ");

                } else if (hotel_filters.get(i).getType().equalsIgnoreCase("hotelName")) {
                    sql.append("h.hotelName = '" + hotel_filters.get(i).getFilter() + "' ");

                } else if (hotel_filters.get(i).getType().equalsIgnoreCase("hotelCity")) {
                    sql.append("c.cityName = '" + hotel_filters.get(i).getFilter() + "' ");

                } else if (hotel_filters.get(i).getType().equalsIgnoreCase("hotelLocation")) {
                    sql.append("l.locationName = '" + hotel_filters.get(i).getFilter() + "' ");

                } else if (hotel_filters.get(i).getType().equalsIgnoreCase("hotelRating")) {
                    sql.append("h.Rating = '" + hotel_filters.get(i).getFilter() + "' ");

                } else if (hotel_filters.get(i).getType().equalsIgnoreCase("availableRooms")) {
                    sql.append("h.availableRooms = '" + hotel_filters.get(i).getFilter() + "' ");

                } else if (hotel_filters.get(i).getType().equalsIgnoreCase("cityID")) {
                    sql.append("h.cityID = '" + hotel_filters.get(i).getFilter() + "' ");

                } else if (hotel_filters.get(i).getType().equalsIgnoreCase("locationID")) {
                    sql.append("h.locationID = '" + hotel_filters.get(i).getFilter() + "' ");

                }


            }

        }

        boolean inside = false;

        int maxRating = 0, maxRooms = 0;

        for(int i=0;i<hotel_filters.size();i++){
            if(!hotel_filters.get(i).isSearch()){
                if(hotel_filters.get(i).getType().equalsIgnoreCase("hotelRating")) {
                    if (Integer.parseInt(hotel_filters.get(i).getFilter()) > maxRating)
                        maxRating = Integer.parseInt(hotel_filters.get(i).getFilter());
                }
                else if(hotel_filters.get(i).getType().equalsIgnoreCase("availableRooms")) {
                    if (Integer.parseInt(hotel_filters.get(i).getFilter()) > maxRooms)
                        maxRooms = Integer.parseInt(hotel_filters.get(i).getFilter());
                }
            }
        }

        int i=0;
        boolean flag = false;
        while(i<hotel_filters.size()){
            flag = false;
            if(!hotel_filters.get(i).isSearch()){
                if(hotel_filters.get(i).getType().equalsIgnoreCase("hotelRating")) {
                    if (Integer.parseInt(hotel_filters.get(i).getFilter()) < maxRating) {
                        hotel_filters.remove(i);
                        flag = true;
                    }
                }
                else if(hotel_filters.get(i).getType().equalsIgnoreCase("availableRooms")) {
                    if (Integer.parseInt(hotel_filters.get(i).getFilter()) < maxRooms) {
                        hotel_filters.remove(i);
                        flag = true;
                    }
                }
            }

            if(!flag)
                i+= 1;

        }

        for(i=0;i<hotel_filters.size();i++){

            if(!hotel_filters.get(i).isSearch()) {
                if (inside) {
                    sql.append("or ");

                }

                if (!inside) {
                    sql.append("and (");
                    inside = true;
                }


                if (hotel_filters.get(i).getType().equalsIgnoreCase("hotelID")) {
                    sql.append("h.hotelID = '" + hotel_filters.get(i).getFilter() + "' ");

                } else if (hotel_filters.get(i).getType().equalsIgnoreCase("hotelName")) {
                    sql.append("h.hotelName = '" + hotel_filters.get(i).getFilter() + "' ");

                } else if (hotel_filters.get(i).getType().equalsIgnoreCase("hotelCity")) {
                    sql.append("c.cityName = '" + hotel_filters.get(i).getFilter() + "' ");

                } else if (hotel_filters.get(i).getType().equalsIgnoreCase("hotelLocation")) {
                    sql.append("l.locationName = '" + hotel_filters.get(i).getFilter() + "' ");

                } else if (hotel_filters.get(i).getType().equalsIgnoreCase("hotelRating")) {
                    if(Integer.parseInt(hotel_filters.get(i).getFilter()) == maxRating)
                        sql.append("h.Rating >= '" + hotel_filters.get(i).getFilter() + "' ");

                } else if (hotel_filters.get(i).getType().equalsIgnoreCase("availableRooms")) {
                    if(Integer.parseInt(hotel_filters.get(i).getFilter()) == maxRooms)
                        sql.append("h.availableRooms >= '" + hotel_filters.get(i).getFilter() + "' ");

                } else if (hotel_filters.get(i).getType().equalsIgnoreCase("cityID")) {
                    sql.append("h.cityID = '" + hotel_filters.get(i).getFilter() + "' ");

                } else if (hotel_filters.get(i).getType().equalsIgnoreCase("locationID")) {
                    sql.append("h.locationID = '" + hotel_filters.get(i).getFilter() + "' ");

                }



            }

        }

        if (inside) {
            sql.append(")");
        }

        Log.d(TAG, "generatedSql: " + sql.toString());

        return sql.toString();
    }

    private String generateSqlCities(ArrayList<Filter> city_filters) {

        StringBuilder sql = new StringBuilder("SELECT t.cityID, cityName, t.rating, count(locationID) as noOfLocations " +
                "FROM touristCity t, touristLocations l " +
                "where t.cityID = l.cityID ");

        ArrayList<Filter> otherFilters = new ArrayList<>();
        boolean isOtherFilter = false;
        for(int i=0;i<city_filters.size();i++){
            if(!city_filters.get(i).getType().equalsIgnoreCase("noOfLocations")){
                otherFilters.add(city_filters.get(i));
                isOtherFilter  = true;
            }
        }

        Collections.sort(city_filters);

        if(isOtherFilter) {

            for (int i = 0; i < otherFilters.size(); i++) {

                if(otherFilters.get(i).isSearch()) {

                    sql.append("and ");



                    if (otherFilters.get(i).getType().equalsIgnoreCase("cityID")) {
                        sql.append("t.cityID = '" + otherFilters.get(i).getFilter() + "' ");

                    } else if (otherFilters.get(i).getType().equalsIgnoreCase("cityName")) {
                        sql.append("cityName = '" + otherFilters.get(i).getFilter() + "' ");

                    } else if (otherFilters.get(i).getType().equalsIgnoreCase("rating")) {
                        sql.append("t.rating = '" + otherFilters.get(i).getFilter() + "' ");

                    }

                }

            }

            boolean inside = false;

            int maxRating = 0;

            for(int i=0;i<otherFilters.size();i++){
                if(!otherFilters.get(i).isSearch()){
                    if(otherFilters.get(i).getType().equalsIgnoreCase("rating"))
                        if(Integer.parseInt(otherFilters.get(i).getFilter()) > maxRating)
                            maxRating = Integer.parseInt(otherFilters.get(i).getFilter());

                }
            }

            boolean flag = false;
            int i=0;

            while(i<otherFilters.size()){
                flag = false;
                if(!otherFilters.get(i).isSearch()){
                    if(otherFilters.get(i).getType().equalsIgnoreCase("rating"))
                        if(Integer.parseInt(otherFilters.get(i).getFilter()) < maxRating) {
                            otherFilters.remove(i);
                            flag = true;
                        }

                }
                if(!flag)
                    i++;
            }

            for (i = 0; i < otherFilters.size(); i++) {

                if(!otherFilters.get(i).isSearch()) {

                    if (inside) {
                        sql.append("or ");

                    }

                    if (!inside) {
                        sql.append("and (");
                        inside = true;
                    }


                    if (otherFilters.get(i).getType().equalsIgnoreCase("cityID")) {
                        sql.append("t.cityID = '" + otherFilters.get(i).getFilter() + "' ");

                    } else if (otherFilters.get(i).getType().equalsIgnoreCase("cityName")) {
                        sql.append("cityName = '" + otherFilters.get(i).getFilter() + "' ");

                    } else if (otherFilters.get(i).getType().equalsIgnoreCase("rating")) {
                        if(Integer.parseInt(city_filters.get(i).getFilter()) == maxRating)
                            sql.append("t.rating >= '" + otherFilters.get(i).getFilter() + "' ");

                    }


                }

            }

            if (inside) {
                sql.append(") ");
            }
        }

        sql.append("group by t.cityID ");

        boolean flag = false;

        for(int i=0;i<city_filters.size();i++){

            if(city_filters.get(i).isSearch()) {

                if (!flag && city_filters.get(i).getType().equalsIgnoreCase("noOfLocations")) {
                    sql.append("having noOfLocations = " + city_filters.get(i).getFilter() + " ");
                    flag = true;

                } else if (flag && city_filters.get(i).getType().equalsIgnoreCase("noOfLocations")) {
                    sql.append("and noOfLocations = " + city_filters.get(i).getFilter() + " ");
                }
            }

        }

        boolean inside = false;

        int maxLocations = 0;

        for(int i=0;i<city_filters.size();i++){
            if(!city_filters.get(i).isSearch()){
                if(city_filters.get(i).getType().equalsIgnoreCase("noOfLocations"))
                    if(Integer.parseInt(city_filters.get(i).getFilter()) > maxLocations)
                        maxLocations = Integer.parseInt(city_filters.get(i).getFilter());

            }
        }

        boolean alert = false;
        int i=0;

        while(i<city_filters.size()){
            alert = false;
            if(!city_filters.get(i).isSearch()){
                if(city_filters.get(i).getType().equalsIgnoreCase("noOfLocations"))
                    if(Integer.parseInt(city_filters.get(i).getFilter()) < maxLocations) {
                        city_filters.remove(i);
                        alert = true;
                    }

            }
            if(!alert)
                i++;
        }

        for(i=0;i<city_filters.size();i++){

            if(!city_filters.get(i).isSearch()) {


                if (!flag && city_filters.get(i).getType().equalsIgnoreCase("noOfLocations")) {
                    if(Integer.parseInt(city_filters.get(i).getFilter()) == maxLocations)
                        sql.append("having noOfLocations >= " + city_filters.get(i).getFilter() + " ");

                } else if (flag && city_filters.get(i).getType().equalsIgnoreCase("noOfLocations")) {
                    if(Integer.parseInt(city_filters.get(i).getFilter()) == maxLocations)
                        sql.append("and noOfLocations >= " + city_filters.get(i).getFilter() + " ");
                }
            }

        }




        Log.d(TAG, "generatedSql: " + sql.toString());

        return sql.toString();
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
                        else if(type.equalsIgnoreCase("packagePrice"))
                            n_packPrice -= 1;
                        else if(type.equalsIgnoreCase("price"))
                            n_price -= 1;
                    }
                    else
                        removeFromFilter(new Filter(type,text,true));

                    cg_filters.removeView(chip);

                }
            });

            cg_filters.addView(chip);

        }
    }

    private String getFilterText(int i){
        if(VIEW_MODE == VIEW_MODE_BOOKING){
            return booking_filters.get(i).getType() + ":" + booking_filters.get(i).getFilter();
        }
        else if(VIEW_MODE == VIEW_MODE_AGENCY){
            return agency_filters.get(i).getType() + ":" + agency_filters.get(i).getFilter();
        }
        else if(VIEW_MODE == VIEW_MODE_HOTEL){
            return hotel_filters.get(i).getType() + ":" + hotel_filters.get(i).getFilter();
        }
        else if(VIEW_MODE == VIEW_MODE_CLIENT){
            return client_filters.get(i).getType() + ":" + client_filters.get(i).getFilter();
        }
        else if(VIEW_MODE == VIEW_MODE_PACKAGE) {
            return package_filters.get(i).getType() + ":" + package_filters.get(i).getFilter();
        }
        else {
            return city_filters.get(i).getType() + ":" + city_filters.get(i).getFilter();
        }
    }

    private int getFilterSize() {
        if(VIEW_MODE == VIEW_MODE_BOOKING){
            return booking_filters.size();
        }
        else if(VIEW_MODE == VIEW_MODE_AGENCY){
            return agency_filters.size();
        }
        else if(VIEW_MODE == VIEW_MODE_HOTEL){
            return hotel_filters.size();
        }
        else if(VIEW_MODE == VIEW_MODE_CLIENT){
            return client_filters.size();
        }
        else if(VIEW_MODE == VIEW_MODE_PACKAGE) {
            return package_filters.size();
        }
        else
            return city_filters.size();
    }

    private void removeFromFilter(Filter filter){
        if(VIEW_MODE == VIEW_MODE_BOOKING){
            booking_filters.remove(filter);
        }
        else if(VIEW_MODE == VIEW_MODE_AGENCY){
            agency_filters.remove(filter);
        }
        else if(VIEW_MODE == VIEW_MODE_HOTEL){
            hotel_filters.remove(filter);
        }
        else if(VIEW_MODE == VIEW_MODE_CLIENT){
            client_filters.remove(filter);
        }
        else if(VIEW_MODE == VIEW_MODE_PACKAGE){
            package_filters.remove(filter);
        }
        else
            city_filters.remove(filter);
    }

    private void addToFilter(Filter filter){
        if(VIEW_MODE == VIEW_MODE_BOOKING){
            booking_filters.add(filter);
        }
        else if(VIEW_MODE == VIEW_MODE_AGENCY){
            agency_filters.add(filter);
        }
        else if(VIEW_MODE == VIEW_MODE_HOTEL){
            hotel_filters.add(filter);
        }
        else if(VIEW_MODE == VIEW_MODE_CLIENT){
            client_filters.add(filter);
        }
        else if(VIEW_MODE == VIEW_MODE_PACKAGE){
            package_filters.add(filter);
        }
        else
            city_filters.add(filter);
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

                        Log.d(TAG, "onClick: " + n_price + " " + no_fd + " " + n_packPrice);

                        if(!((VIEW_MODE == VIEW_MODE_BOOKING || VIEW_MODE == VIEW_MODE_PACKAGE) && ((type.equalsIgnoreCase("fromDate") && no_fd>2)
                                || (type.equalsIgnoreCase("packagePrice") && n_packPrice > 2)
                                || (type.equalsIgnoreCase("price") && n_price > 2)))) {

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
                                            else if(type.equalsIgnoreCase("packagePrice"))
                                                n_packPrice -= 1;
                                            else if(type.equalsIgnoreCase("price"))
                                                n_price -= 1;
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
                                else if (type.equalsIgnoreCase("packagePrice"))
                                    n_packPrice += 1;
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

    private void updateData() {

        if(VIEW_MODE == VIEW_MODE_BOOKING){
            ArrayList<Booking> bookings = getFilteredBookings(booking_filters);
            managerHomeAdapter.setBookings(bookings);
            managerHomeAdapter.notifyDataSetChanged();
        }
        else if(VIEW_MODE == VIEW_MODE_AGENCY){
            ArrayList<Agency> agencies = getFilteredAgencies(agency_filters);
            managerHomeAdapter.setAgencies(agencies);
            managerHomeAdapter.notifyDataSetChanged();
        }
        else if(VIEW_MODE == VIEW_MODE_HOTEL){
            ArrayList<Hotel> hotels = getFilteredHotels(hotel_filters);
            managerHomeAdapter.setHotels(hotels);
            managerHomeAdapter.notifyDataSetChanged();
        }
        else if(VIEW_MODE == VIEW_MODE_CLIENT){
            ArrayList<Client> clients = getFilteredClients(client_filters);
            managerHomeAdapter.setClients(clients);
            managerHomeAdapter.notifyDataSetChanged();
        }
        else if(VIEW_MODE == VIEW_MODE_PACKAGE){
            ArrayList<Package> packages = getFilteredPackages(package_filters);
            managerHomeAdapter.setPackages(packages);
            managerHomeAdapter.notifyDataSetChanged();
        }
        else if(VIEW_MODE == VIEW_MODE_CITY){
            ArrayList<TouristCity> cities = getFilteredCities(city_filters);
            managerHomeAdapter.setCities(cities);
            managerHomeAdapter.notifyDataSetChanged();
        }
    }


    private ArrayList<Agency> getAgencies() {

        String sql = "SELECT agencyID, agencyName, agencyAddress, agencyContact, numberOfPackages " +
                "FROM agencies a;";

        Cursor cur = mDb.rawQuery(sql, null);

        ArrayList<Agency> agencies = new ArrayList<>();


        while(cur!=null && cur.moveToNext()){
            Agency agency = new Agency(cur.getString(0),
                    cur.getString(1),
                    cur.getString(2),
                    String.format("%.0f",cur.getFloat(3)),
                    cur.getString(4));
            agencies.add(agency);

        }
        Log.d(TAG, "onItemSelected: " + agencies.size());

        return agencies;
    }

    private ArrayList<Hotel> getHotels() {

        String sql = "SELECT hotelID, hotelName, cityName, locationName, h.Rating, availableRooms,h.cityID, h.locationID" +
                " FROM hotelinformation h, touristCity c, touristlocations l " +
                "WHERE h.cityID = c.cityID and h.locationID = l.locationID;";
        Cursor cur = mDb.rawQuery(sql, null);

        ArrayList<Hotel> hotels = new ArrayList<>();

        while(cur!=null && cur.moveToNext()){
            Hotel hotel = new Hotel(cur.getString(0),
                    cur.getString(1),
                    cur.getString(2),
                    cur.getString(3),
                    cur.getString(4),
                    cur.getString(5),
                    cur.getString(6),
                    cur.getString(7));
            hotels.add(hotel);

        }
        Log.d(TAG, "onItemSelected: " + hotels.size());
        return hotels;
    }

    private ArrayList<Package> getPackages() {
        String sql = "SELECT packageID, agencyName, packageType, days, nights, cityName, packagePrice,p.agencyID,p.cityID" +
                " FROM Package p,agencies a, TouristCity c " +
                "WHERE p.agencyID = a.agencyID and p.cityID = c.cityID;";

        Cursor cur = mDb.rawQuery(sql, null);

        ArrayList<Package> packages = new ArrayList<>();

        while(cur!=null && cur.moveToNext()){
            Package aPackage = new Package(cur.getString(0),
                    cur.getString(1),
                    cur.getString(2),
                    cur.getString(3),
                    cur.getString(4),
                    cur.getString(5),
                    cur.getString(6),
                    cur.getString(7),
                    cur.getString(8));
            packages.add(aPackage);

        }
        Log.d(TAG, "onItemSelected: " + packages.size());

        return packages;
    }

    private ArrayList<Client> getClients() {

        String sql = "SELECT c.clientID, clientName, clientContact, clientAddress, clientEmail, count(b.bookingID) " +
                "FROM Client c, Booking b " +
                "WHERE c.clientID = b.clientID " +
                "GROUP BY c.clientID;";
        Cursor cur = mDb.rawQuery(sql, null);

        ArrayList<Client> clients = new ArrayList<>();

        while(cur!=null && cur.moveToNext()){

//            String sql2 = "SELECT bookingID, clientName, b.packageType, cityName, vehicleName, vehicleType, dateOfBooking, fromDate, days, nights, packagePrice, agencyName,b.vehicleID, p.agencyID, b.packageID, b.clientID,b.cityID " +
//                    "FROM booking b,client c,TouristCity t,Vehicle v,Package p,agencies a " +
//                    "WHERE b.clientID = c.clientID and b.packageID = p.packageID and b.cityID = t.cityID and b.vehicleID = v.vehicleID and p.agencyID = a.agencyID and c.clientID = "+cur.getString(0) + ";";
//
//            Cursor cur2 = mDb.rawQuery(sql2, null);
//            ArrayList<Booking> bookings = new ArrayList<>();
//            while(cur2!=null && cur2.moveToNext()){
//                Booking booking = new Booking(cur2.getString(0),
//                        cur2.getString(1),
//                        String.format("%.0f",cur.getFloat(2)),
//                        cur2.getString(3),
//                        cur2.getString(4),
//                        cur2.getString(5),
//                        cur2.getString(6),
//                        cur2.getString(7),
//                        cur2.getString(8),
//                        cur2.getString(9),
//                        cur2.getString(10),
//                        cur2.getString(11),
//                        cur2.getString(12),
//                        cur2.getString(13),
//                        cur2.getString(14),
//                        cur2.getString(15),
//                        cur2.getString(16));
//                bookings.add(booking);
//
//            }

            Client client = new Client(cur.getString(0),
                    cur.getString(1),
                    String.format("%.0f",cur.getFloat(2)),
                    cur.getString(3),
                    cur.getString(4),
                    cur.getString(5));
            clients.add(client);

        }
        Log.d(TAG, "onItemSelected: " + clients.size());

        return clients;
    }

    private ArrayList<Booking> getBookings() {

        String sql = "SELECT bookingID, clientName, b.packageType, cityName, vehicleName, vehicleType, dateOfBooking, fromDate, days, nights, packagePrice, agencyName, b.vehicleID,p.agencyID, b.cityID, b.packageID,b.clientID, h.hotelName, b.hotelID " +
                "FROM booking b,client c,TouristCity t,Vehicle v,Package p,agencies a, hotelInformation h " +
                "WHERE b.clientID = c.clientID and b.packageID = p.packageID and b.cityID = t.cityID and b.vehicleID = v.vehicleID and p.agencyID = a.agencyID and b.hotelID = h.hotelID;";
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

    private ArrayList<TouristCity> getCities(){

        String sql = "SELECT t.cityID, cityName, t.rating, count(locationID)" +
                "FROM touristCity t, touristLocations l " +
                "where t.cityID = l.cityID " +
                "group by t.cityID";

        Cursor cur = mDb.rawQuery(sql, null);

        ArrayList<TouristCity> cities = new ArrayList<>();


        while(cur!=null && cur.moveToNext()){
            TouristCity city = new TouristCity(cur.getString(0),
                    cur.getString(1),
                    cur.getString(2),
                    cur.getString(3));
            cities.add(city);

        }
        Log.d(TAG, "onItemSelected: " + cities.size());

        return cities;

    }
}