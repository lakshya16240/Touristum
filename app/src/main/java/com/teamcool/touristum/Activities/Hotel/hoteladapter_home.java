package com.teamcool.touristum.Activities.Hotel;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.Booking;

import java.util.List;

public class hoteladapter_home extends RecyclerView.Adapter<hoteladapter_home.HomeViewHolder> {
    private List<Booking> bookings;
    private Context context;
    private onBookingClickListner listner;

    public hoteladapter_home(List<Booking> bookings, Context context, onBookingClickListner listner) {
        this.bookings = bookings;
        this.context = context;
        this.listner = listner;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.list_item_booking,parent,false);
        return new HomeViewHolder(itemView,listner);
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Booking booking=bookings.get(position);
        Log.d("TAG",booking.getBookingID());
//        holder.bookingID.setText("BookingID: " + book.getBookingID()+" ");
//        holder.CityID.setText("CityID: " + book.getCityID()+" ");
//        holder.clientId.setText("ClientID: " + book.getClientID()+" ");
//        holder.VehicelID.setText("VehicleID: " + book.getVehicleID()+" ");
//        holder.packageID.setText("PackageID: " + book.getPackageID()+" ");
//        holder.type.setText("BookingType: " + book.getPackageType()+" ");
//        holder.bookingdate.setText("BookingDate: " + book.getDateOfBooking()+" ");
//        holder.fromdate.setText("FromDate: " + book.getFromDate()+" ");
//        holder.price.setText("PackagePrice: " + book.getPrice()+" ");
//        holder.hotelID.setText("HotelID: "+book.getHotelID());
        holder.tv_name.setText("Client : " + booking.getClientName().toString() +"(Id : " + booking.getClientID() + ")");
        holder.tv_city.setText("City : " + booking.getCity().toString() +"(Id : " + booking.getCityID() + ")");
        holder.tv_price.setText("Price : "  + booking.getPrice().toString());
        holder.tv_package.setText("Package : " + booking.getPackageType().toString() +"(Id : " + booking.getPackageID() + ")");
        holder.tv_from.setText("From : " + booking.getFromDate().toString());
        holder.tv_booking.setText("BookingID : " + booking.getBookingID().toString());
        holder.tv_vehicle.setText("Vehicle : " + booking.getVehicleName().toString() + " (" + booking.getVehicleType() + ")(Id : " + booking.getVehicleID() + ")");
        holder.tv_agency.setText("Agency : " + booking.getAgencyName().toString() +"(Id : " + booking.getAgencyID() + ")");
        holder.tv_hotel.setText("Hotel : " + booking.getHotel().toString() +"(Id : " + booking.getHotelID() + ")");
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
//        TextView bookingID,packageID,clientId,VehicelID,CityID,type,bookingdate,fromdate,price,hotelID;
        onBookingClickListner listner;
        TextView tv_name,tv_city,tv_package,tv_from,tv_price, tv_booking, tv_vehicle, tv_agency, tv_hotel;
        public HomeViewHolder(@NonNull View itemView,onBookingClickListner listner) {
            super(itemView);
//            bookingID=itemView.findViewById(R.id.home_bookingID);
//            CityID=itemView.findViewById(R.id.home_CityID);
//            clientId=itemView.findViewById(R.id.home_ClientID);
//            VehicelID=itemView.findViewById(R.id.home_VehicleID);
//            packageID=itemView.findViewById(R.id.home_PackageID);
//            type=itemView.findViewById(R.id.home_PackageType);
//            bookingdate=itemView.findViewById(R.id.home_BookingDate);
//            fromdate=itemView.findViewById(R.id.home_FromDate);
//            price=itemView.findViewById(R.id.home_Price);
//            hotelID=itemView.findViewById(R.id.home_hotel_iD);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_city = itemView.findViewById(R.id.tv_city);
            tv_from = itemView.findViewById(R.id.tv_from);
            tv_package = itemView.findViewById(R.id.tv_package);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_booking = itemView.findViewById(R.id.tv_booking);
            tv_vehicle = itemView.findViewById(R.id.tv_vehicle);
            tv_agency = itemView.findViewById(R.id.tv_agency);
            tv_hotel = itemView.findViewById(R.id.tv_hotel);
            this.listner=listner;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {listner.selectedBooking(getAdapterPosition());
        }
    }
    public interface onBookingClickListner{
        void selectedBooking(int position);
    }
}
