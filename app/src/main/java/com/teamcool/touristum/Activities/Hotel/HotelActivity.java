package com.teamcool.touristum.Activities.Hotel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.teamcool.touristum.R;

public class HotelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);
        BottomNavigationView bottomNav=findViewById(R.id.hotel_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListner);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_hotel,new Home_Fragment_hotel()).commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListner=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selected = null;
                    switch (item.getItemId()) {
                        case R.id.hotel_home:
                            selected=new Home_Fragment_hotel();
                            break;
                        case R.id.hotel_client_info:
                            selected=new client_fragment_hotel();
                            break;
                        case R.id.hotel_review:
                            selected=new Review_Fragment_hotel();
                            break;
                        case R.id.hotel_account_info:
                            selected=new Account_Fragment_hotel();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_hotel,
                            selected).commit();
                    return true;
                }
            };
}
