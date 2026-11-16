package com.example.pigs.model;

public class Vaccination {
    private String tagNumber;
    private String penNumber;
    private String date;
    private String vaccineName;
    private String status;

    public Vaccination(String tagNumber, String penNumber,
                       String date, String vaccineName, String status) {
        this.tagNumber = tagNumber;
        this.penNumber = penNumber;
        this.date = date;
        this.vaccineName = vaccineName;
        this.status = status;
    }

    public String getTagNumber() {
        return tagNumber;
    }

    public String getPenNumber() {
        return penNumber;
    }

    public String getDate() {
        return date;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public String getStatus() {
        return status;
    }
}
