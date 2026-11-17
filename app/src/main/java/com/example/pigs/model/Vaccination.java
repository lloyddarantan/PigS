
package com.example.pigs.model;

public class Vaccination {

    private int vaccinationId;
    private int pigId;
    private String tagNumber;
    private String penNumber;
    private String date;
    private String vaccineName;
    private int doseNumber;
    private float dose;
    private String status;

    public Vaccination(int vaccinationId, int pigId,
                       String tagNumber, String penNumber,
                       String date, String vaccineName,
                       int doseNumber, float dose, String status) {

        this.vaccinationId = vaccinationId;
        this.pigId = pigId;
        this.tagNumber = tagNumber;
        this.penNumber = penNumber;
        this.date = date;
        this.vaccineName = vaccineName;
        this.doseNumber = doseNumber;
        this.dose = dose;
        this.status = status;
    }

    public int getVaccinationId() {
        return vaccinationId;
    }

    public int getPigId() {
        return pigId;
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

    public int getDoseNumber() {
        return doseNumber;
    }

    public float getDose() {
        return dose;
    }

    public String getStatus() {
        return status;
    }
}
