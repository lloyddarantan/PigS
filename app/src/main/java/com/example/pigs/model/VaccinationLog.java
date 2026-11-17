package com.example.pigs.model;
import com.example.pigs.model.VaccinationLog;
public class VaccinationLog {
    private String date;
    private String tagNumber;
    private String vaccineName;
    private float dose;
    private String staffId;

    public VaccinationLog(String date, String tagNumber,
                          String vaccineName, float dose, String staffId) {
        this.date = date;
        this.tagNumber = tagNumber;
        this.vaccineName = vaccineName;
        this.dose = dose;
        this.staffId = staffId;
    }

    public String getDate() {
        return date;
    }

    public String getTagNumber() {
        return tagNumber;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public float getDose() {
        return dose;
    }

    public String getStaffId() {
        return staffId;
    }
}
