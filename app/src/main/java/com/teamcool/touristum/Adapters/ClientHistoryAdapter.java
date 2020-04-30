package com.teamcool.touristum.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.Booking;
import com.teamcool.touristum.data.model.Employee;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ClientHistoryAdapter extends RecyclerView.Adapter<ClientHistoryAdapter.BookingsViewHolder> {

    private ArrayList<Booking> bookings;
    private Context context;
    private onBookingClickListener bookingClickListener;

    public interface onBookingClickListener{
        void selectedBooking(Booking booking);
    }

    public ClientHistoryAdapter(ArrayList<Booking> bookings, Context context, onBookingClickListener bookingClickListener) {
        this.bookings = bookings;
        this.context = context;
        this.bookingClickListener = bookingClickListener;
    }


    @NonNull
    @Override
    public BookingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.list_item_client_booking,parent,false);
        return new ClientHistoryAdapter.BookingsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingsViewHolder holder, int position) {

        Booking booking = bookings.get(position);
        holder.tv_city.setText("City : " + booking.getCity().toString());
        holder.tv_price.setText("Price : "  + booking.getPrice().toString());
        holder.tv_package.setText("Package : " + booking.getPackageType().toString());
        holder.tv_from.setText("From : " + booking.getFromDate().toString());
        holder.tv_booking.setText("BookingID : " + booking.getBookingID().toString());
        holder.tv_vehicle.setText("Vehicle : " + booking.getVehicleName().toString());
        holder.tv_agency.setText("Agency : " + booking.getAgencyName().toString());
        holder.tv_hotel.setText("Hotel : " + booking.getHotel().toString());

    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }


    public void setBookings(ArrayList<Booking> list){
        bookings = list;
    }

    public class BookingsViewHolder extends RecyclerView.ViewHolder{

        TextView tv_city,tv_package,tv_from,tv_price, tv_booking, tv_vehicle, tv_agency, tv_hotel;
        Button bv_review;

        public BookingsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_city = itemView.findViewById(R.id.tv_city);
            tv_from = itemView.findViewById(R.id.tv_from);
            tv_package = itemView.findViewById(R.id.tv_package);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_booking = itemView.findViewById(R.id.tv_booking);
            tv_vehicle = itemView.findViewById(R.id.tv_vehicle);
            tv_agency = itemView.findViewById(R.id.tv_agency);
            tv_hotel = itemView.findViewById(R.id.tv_hotel);
            bv_review = itemView.findViewById(R.id.bv_review);

            bv_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bookingClickListener.selectedBooking(bookings.get(getAdapterPosition()));
                }
            });


        }
    }
}
