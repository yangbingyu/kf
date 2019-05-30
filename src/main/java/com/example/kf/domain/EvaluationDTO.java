package com.example.kf.domain;

public class EvaluationDTO {

    private int id;

    private String username;

    private String overallEvaluation;

    private String driverEvaluation;

    private String employeeEvaluation;

    private String review;

    private String tag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOverallEvaluation() {
        return overallEvaluation;
    }

    public void setOverallEvaluation(String overallEvaluation) {
        this.overallEvaluation = overallEvaluation;
    }

    public String getDriverEvaluation() {
        return driverEvaluation;
    }

    public void setDriverEvaluation(String driverEvaluation) {
        this.driverEvaluation = driverEvaluation;
    }

    public String getEmployeeEvaluation() {
        return employeeEvaluation;
    }

    public void setEmployeeEvaluation(String employeeEvaluation) {
        this.employeeEvaluation = employeeEvaluation;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
