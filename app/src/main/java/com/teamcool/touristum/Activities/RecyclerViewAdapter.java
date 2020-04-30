package com.teamcool.touristum.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import com.teamcool.touristum.data.model.Package;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.TouristCityAgency;
import com.teamcool.touristum.data.model.touristlocations;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Package> l1;
    private List<TouristCityAgency> l2;
    private List<touristlocations> l3;
    private static final int view_packages=0;
    private static final int view_city=1;
    private static final int view_locations=2;
    public void setL1(List<Package> l1) {
        this.l1 = l1;
    }
    public void setL2(List<TouristCityAgency> l2) {
        this.l2 = l2;
    }
    public void setL3(List<touristlocations> l3) {
        this.l3 = l3;
    }
    private onPackageClickListner clickListner;
    private int view;

    public RecyclerViewAdapter(Context context, List<Package> l1, List<TouristCityAgency> l2, List<touristlocations> l3, onPackageClickListner clickListner) {
        this.context = context;
        this.l1 = l1;
        this.l2 = l2;
        this.l3 = l3;
        this.clickListner = clickListner;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(view==view_packages){
            View v;
            v= LayoutInflater.from(context).inflate(R.layout.list_item_packages,parent,false);
            return new PackageViewHolder(v,clickListner);
        }
        else if(view==view_city){
            View v;
            v= LayoutInflater.from(context).inflate(R.layout.item_city,parent,false);
            return new TouristCityAgencyViewHolder(v);
        }
        else{
            View v;
            v= LayoutInflater.from(context).inflate(R.layout.item_location,parent,false);
            return new ToursitLocationsViewHolder(v);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(view==view_packages){
            Package aPackage = l1.get(position);
            PackageViewHolder packageViewHolder = (RecyclerViewAdapter.PackageViewHolder) holder;
//            packageViewHolder.t_packageid.setText("PackageID : " + apackage.getPackageID());
//            packageViewHolder.t_agencyname.setText("AgencyName : " + apackage.getAgencyName());
//            packageViewHolder.t_packagetype.setText("PackageType : " + apackage.getPackageType());
//            packageViewHolder.t_nights.setText("Nights : " + apackage.getNights());
//            packageViewHolder.t_days.setText("Days : " + apackage.getDays());
//            packageViewHolder.t_city.setText("City : " + apackage.getCity());
//            packageViewHolder.t_cityid.setText("CityID : " + apackage.getCityID());
//            packageViewHolder.t_price.setText("Price : " + apackage.getPrice());
//            packageViewHolder.t_agencyid.setText("AgencyID : " + apackage.getAgencyID());
            packageViewHolder.tv_days.setText("Days : " + aPackage.getDays().toString());
            packageViewHolder.tv_city.setText("City : " + aPackage.getCity().toString() +"(Id : " + aPackage.getCityID() + ")");
            packageViewHolder.tv_price.setText("Price : "  + aPackage.getPrice().toString());
            packageViewHolder.tv_packege.setText("Package : " + aPackage.getPackageType().toString() +"(Id : " + aPackage.getPackageID() + ")");
            packageViewHolder.tv_nights.setText("Nights : " + aPackage.getNights().toString());
            packageViewHolder.tv_agency.setText("Agency : " + aPackage.getAgencyName().toString()+"(Id : " + aPackage.getAgencyID() + ")");
        }
        else if (view==view_city){
            TouristCityAgency TouristCityAgency = l2.get(position);
            TouristCityAgencyViewHolder ViewHolder = (RecyclerViewAdapter.TouristCityAgencyViewHolder) holder;
            ViewHolder.t_ID.setText("CityID : "+ TouristCityAgency.getCityID());
            ViewHolder.t_Name.setText("CityName : "+ TouristCityAgency.getCityName());
            ViewHolder.t_Rating.setRating(Float.parseFloat(TouristCityAgency.getRating()));
        }
        else {
            touristlocations tourist = l3.get(position);
            ToursitLocationsViewHolder ViewHolder=(RecyclerViewAdapter.ToursitLocationsViewHolder) holder;
            ViewHolder.t_CityID.setText("CityID : "+tourist.getCityID());
            ViewHolder.t_Name.setText("LocationName : "+tourist.getLocationName());
            ViewHolder.t_LocationID.setText("LocationID : "+tourist.getLocationID());
            ViewHolder.t_Rating.setRating(Float.parseFloat(tourist.getRating()));
        }
    }
    @Override
    public int getItemCount() {
        if(view==view_packages){
            return l1.size();
        }
        else if(view==view_city){
            return l2.size();
        }
        else{
            return l3.size();
        }
    }
    //done
    public class TouristCityAgencyViewHolder extends RecyclerView.ViewHolder{
        private TextView t_ID;
        private TextView t_Name;
        private RatingBar t_Rating;
        public TouristCityAgencyViewHolder(@NonNull View itemView) {
            super(itemView);
            t_ID=(TextView)itemView.findViewById(R.id.CityID);
            t_Name=(TextView)itemView.findViewById(R.id.CityName);
            t_Rating=(RatingBar) itemView.findViewById(R.id.ratingBar);
        }
    }
    public class ToursitLocationsViewHolder extends RecyclerView.ViewHolder{
        private TextView t_LocationID;
        private TextView t_CityID;
        private TextView t_Name;
        private RatingBar t_Rating;
        public ToursitLocationsViewHolder(@NonNull View itemView) {
            super(itemView);
            t_LocationID=(TextView)itemView.findViewById(R.id.LocationID);
            t_CityID=(TextView)itemView.findViewById(R.id.CityID2);
            t_Name=(TextView)itemView.findViewById(R.id.LocationName);
            t_Rating=(RatingBar)itemView.findViewById(R.id.ratingBar2);
        }
    }
    public class PackageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //private LinearLayout item_package;
//        private TextView t_city;
//        private TextView t_price;
//        private TextView t_packageid;
//        private TextView t_agencyname;
//        private TextView t_packagetype;
//        private TextView t_days;
//        private TextView t_nights;
//        private TextView t_cityid;
//        private TextView t_agencyid;
        TextView tv_city, tv_packege, tv_agency, tv_days, tv_nights, tv_price;
        private onPackageClickListner listner;
        public PackageViewHolder(@NonNull View itemView,onPackageClickListner l) {
            super(itemView);
//            t_city=(TextView)itemView.findViewById(R.id.city);
//            t_price=(TextView)itemView.findViewById(R.id.price);
//            t_packageid=(TextView)itemView.findViewById(R.id.packageid);
//            t_agencyname=(TextView)itemView.findViewById(R.id.agencyname);
//            t_packagetype=(TextView)itemView.findViewById(R.id.packagetype);
//            t_days=(TextView)itemView.findViewById(R.id.days);
//            t_nights=(TextView)itemView.findViewById(R.id.nights);
//            t_cityid=(TextView)itemView.findViewById(R.id.cityid);
//            t_agencyid=(TextView)itemView.findViewById(R.id.agencyid);
            tv_city = itemView.findViewById(R.id.tv_city);
            tv_packege = itemView.findViewById(R.id.tv_package_type);
            tv_agency = itemView.findViewById(R.id.tv_agency);
            tv_days = itemView.findViewById(R.id.tv_days);
            tv_nights = itemView.findViewById(R.id.tv_nights);
            tv_price = itemView.findViewById(R.id.tv_price);
            this.listner=l;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listner.selectedPackage(getAdapterPosition());
        }
    }
    public void setView(int view) {
        this.view = view;
    }
    public interface onPackageClickListner{
        void selectedPackage(int position);
    }
}
