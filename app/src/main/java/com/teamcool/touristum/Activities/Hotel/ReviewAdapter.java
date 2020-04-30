package com.teamcool.touristum.Activities.Hotel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.hotelReview;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<hotelReview> reviews;
    private Context context;
    private onReviewClickListner listner;
    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_review,parent,false);
        return new ReviewViewHolder(itemView,listner);
    }

    public ReviewAdapter(List<hotelReview> reviews, Context context, onReviewClickListner listner) {
        this.reviews = reviews;
        this.context = context;
        this.listner = listner;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        hotelReview review=reviews.get(position);
        holder.hotelID.setText("HotelID: "+review.getHotelID());
        holder.reviewID.setText("ReviewID: "+review.getHotelReviewID());
        holder.review.setText("Review: "+review.getHotelReview());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView hotelID,reviewID,review;
        onReviewClickListner listner;
        public ReviewViewHolder(@NonNull View itemView, onReviewClickListner listner) {
            super(itemView);
            hotelID=itemView.findViewById(R.id.hotelID);
            reviewID=itemView.findViewById(R.id.hotel_review_id);
            review=itemView.findViewById(R.id.review);
            this.listner=listner;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listner.selectedClient(getAdapterPosition());
        }
    }
    public interface onReviewClickListner{
        void selectedClient(int position);
    }
}
