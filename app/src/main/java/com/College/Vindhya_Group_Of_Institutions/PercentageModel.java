package com.College.Vindhya_Group_Of_Institutions;

public class PercentageModel {

    private String rollNumber;
    private float percentage;

    public PercentageModel(String rollNumber, float percentage) {
        this.rollNumber = rollNumber;
        this.percentage = percentage;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }
}
