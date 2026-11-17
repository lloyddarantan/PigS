
package com.example.pigs.model;

public class Vaccine {

    private int vaccineId;
    private String vaccineName;
    private String description;

    public Vaccine(int vaccineId, String vaccineName, String description) {
        this.vaccineId = vaccineId;
        this.vaccineName = vaccineName;
        this.description = description;
    }

    public int getVaccineId() {
        return vaccineId;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return vaccineName;
    }
}
