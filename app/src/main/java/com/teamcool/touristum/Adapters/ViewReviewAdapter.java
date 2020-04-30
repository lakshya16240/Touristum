package com.teamcool.touristum.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teamcool.touristum.R;
import com.teamcool.touristum.data.model.Issue;
import com.teamcool.touristum.ui.Reviews.ViewReviewsHistoryFragment;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> reviews;
    private ArrayList<String> client_ids,review_ids,reviewFor;
    private ArrayList<Issue> issues;
    private Context context;

    public static final String TAG = "ViewReviewAdapter";

    private int VIEW_MODE_REVIEWS;
    private int VIEW_MODE;

    private boolean isEmployee;
    private onIssueClickListener issueClickListener;

    public interface onIssueClickListener{
        void selectedIssue(Issue issue);
    }



    public ViewReviewAdapter(Context context,onIssueClickListener issueClickListener) {
        this.context = context;
        this.issueClickListener = issueClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;
        itemView = LayoutInflater.from(context).inflate(R.layout.list_item_reviews,parent,false);

        if(VIEW_MODE_REVIEWS == 0 && VIEW_MODE == ViewReviewsHistoryFragment.VIEW_MODE_CLIENT_ISSUES) {
            itemView = LayoutInflater.from(context).inflate(R.layout.list_item_issues, parent, false);
            return new IssueViewHoler(itemView);

        }

        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(VIEW_MODE_REVIEWS == 0 && VIEW_MODE == ViewReviewsHistoryFragment.VIEW_MODE_CLIENT_ISSUES){
            Issue issue = issues.get(position);
            IssueViewHoler issueViewHoler = (IssueViewHoler) holder;
            issueViewHoler.et_issue.setText(issue.getReview());
            issueViewHoler.tv_issueType.setText("Type : " + issue.getIssueType());
            issueViewHoler.tv_dateOfIssue.setText("Issue Date : " + issue.getDatOfIssue());
            issueViewHoler.tv_employeeId.setText("EmployeeID : " + issue.getEmployeeID());
            issueViewHoler.tv_issueId.setText("IssueID : " + issue.getIssueID());
            issueViewHoler.tv_tentativeDate.setText("Tentative Date : " + issue.getTentativeResolvedDate());
            issueViewHoler.tv_status.setText("Status : " + issue.getStatus());
            if(isEmployee)
                issueViewHoler.tv_clientID.setText("ClientID : " + issue.getClientID());
        }
        else {
            ReviewViewHolder reviewViewHolder = (ReviewViewHolder) holder;
            reviewViewHolder.tv_review.setText(reviews.get(position));

            Log.d(TAG, "onBindViewHolder: " + VIEW_MODE_REVIEWS + " " + VIEW_MODE + " " + ViewReviewsHistoryFragment.VIEW_MODE_CLIENT_REVIEW);
            if (VIEW_MODE_REVIEWS != 0) {
                reviewViewHolder.tv_clientID.setText(client_ids.get(position));
            } else
                reviewViewHolder.tv_reviewFor.setText(reviewFor.get(position));
            reviewViewHolder.tv_reviewID.setText(review_ids.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if(VIEW_MODE_REVIEWS == 0 && VIEW_MODE== ViewReviewsHistoryFragment.VIEW_MODE_CLIENT_REVIEW)
            return reviewFor.size();
        else if(VIEW_MODE_REVIEWS == 0 && VIEW_MODE== ViewReviewsHistoryFragment.VIEW_MODE_CLIENT_ISSUES)
            return issues.size();
        else
            return reviews.size();
    }

    public void setReviews(ArrayList<String> reviews) {
        this.reviews = reviews;
    }

    public void setClient_ids(ArrayList<String> client_ids) {
        this.client_ids = client_ids;
    }

    public void setReview_ids(ArrayList<String> review_ids) {
        this.review_ids = review_ids;
    }

    public void setReviewFor(ArrayList<String> reviewFor) {
        this.reviewFor = reviewFor;
    }

    public void setVIEW_MODE_REVIEWS(int VIEW_MODE_REVIEWS) {
        this.VIEW_MODE_REVIEWS = VIEW_MODE_REVIEWS;
    }

    public void setVIEW_MODE(int VIEW_MODE) {
        this.VIEW_MODE = VIEW_MODE;
    }

    public void setIssues(ArrayList<Issue> issues) {
        this.issues = issues;
    }

    public void setEmployee(boolean employee) {
        isEmployee = employee;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder{

        TextView tv_review,tv_reviewID,tv_clientID,tv_reviewFor;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_review = itemView.findViewById(R.id.tv_review);
            tv_reviewID = itemView.findViewById(R.id.tv_reviewID);
            tv_clientID = itemView.findViewById(R.id.tv_reviewBy);
            tv_reviewFor = itemView.findViewById(R.id.tv_reviewFor);


        }
    }

    public class IssueViewHoler extends RecyclerView.ViewHolder{
        TextView tv_issueId, tv_employeeId, tv_status, tv_dateOfIssue, tv_tentativeDate, tv_issueType,et_issue,tv_clientID;

        public IssueViewHoler(@NonNull View itemView) {
            super(itemView);
            tv_issueId = itemView.findViewById(R.id.tv_issueID);
            tv_employeeId = itemView.findViewById(R.id.tv_employeeID);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_dateOfIssue = itemView.findViewById(R.id.tv_dateOfIssue);
            tv_tentativeDate = itemView.findViewById(R.id.tv_tentativeResolveDate);
            tv_issueType = itemView.findViewById(R.id.tv_issueType);
            et_issue = itemView.findViewById(R.id.et_issue);
            tv_clientID = itemView.findViewById(R.id.tv_clientID);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    issueClickListener.selectedIssue(issues.get(getAdapterPosition()));
                }
            });
        }
    }

}
