package com.teamcool.touristum.Adapters;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.DriverTrip;

import java.util.ArrayList;
import java.util.List;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.MyViewHolder> {

    private ArrayList<DriverTrip> TripList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView clientName, clientNumber,  startDate,days, nights;

        public MyViewHolder(View view) {
            super(view);
            clientName= (TextView) view.findViewById(R.id.tv_clientName);
            clientNumber = (TextView) view.findViewById(R.id.tv_contact);
            startDate=(TextView) view.findViewById(R.id.tv_from);
            days=(TextView) view.findViewById(R.id.tv_days);
            nights=(TextView) view.findViewById(R.id.tv_nights);
        }
    }


    public DriverAdapter(ArrayList<DriverTrip> TripList) {
        this.TripList = TripList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_trips,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DriverTrip driverTrips = TripList.get(position);
        holder.clientName.setText("Name : " + driverTrips.getClientName());
        holder.clientNumber.setText("Contact : " + driverTrips.getClientContact());
        holder.startDate.setText( "From : " + driverTrips.getFromDate());
        holder.days.setText("Days : " + driverTrips.getDays());
        holder.nights.setText("Nights : " + driverTrips.getNights());

    }

    @Override
    public int getItemCount() {
        return TripList.size();
    }
}