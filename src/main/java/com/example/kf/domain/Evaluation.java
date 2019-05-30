package com.example.kf.domain;


import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("int default 0")
    private int id;

    private int customerId;

    private int orderId;

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

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
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
