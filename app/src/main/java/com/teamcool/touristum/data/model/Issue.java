package com.teamcool.touristum.data.model;

public class Issue {

    String issueID, employeeID, clientID, issueType, review, status, dateOfIssue, tentativeResolvedDate,issueResolvedDate;

    public Issue(String issueID, String employeeID, String clientID, String issueType, String review, String status, String dateOfIssue, String tentativeResolvedDate, String issueResolvedDate) {
        this.issueID = issueID;
        this.employeeID = employeeID;
        this.clientID = clientID;
        this.issueType = issueType;
        this.review = review;
        this.status = status;
        this.dateOfIssue = dateOfIssue;
        this.tentativeResolvedDate = tentativeResolvedDate;
        this.issueResolvedDate = issueResolvedDate;
    }

    public String getIssueID() {
        return issueID;
    }

    public void setIssueID(String issueID) {
        this.issueID = issueID;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDatOfIssue() {
        return dateOfIssue;
    }

    public void setDatOfIssue(String dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    public String getTentativeResolvedDate() {
        return tentativeResolvedDate;
    }

    public void setTentativeResolvedDate(String tentativeResolvedDate) {
        this.tentativeResolvedDate = tentativeResolvedDate;
    }

    public String getIssueResolvedDate() {
        return issueResolvedDate;
    }

    public void setIssueResolvedDate(String issueResolvedDate) {
        this.issueResolvedDate = issueResolvedDate;
    }
}
