package com.example.pigs.model;

public class Pig {

    private int pigId;
    private String tagNumber;
    private String penNumber;
    private String temperature;
    private String status;
    private String rfid;
    private String gender;
    private String age;

    public Pig(int pigId, String tagNumber, String penNumber) {
        this(pigId, tagNumber, penNumber, "", "", "", "", "");
    }

    public Pig(int pigId, String tagNumber, String penNumber, String temperature, String status, String rfid) {
        this(pigId, tagNumber, penNumber, temperature, status, rfid, "", "");
    }

    public Pig(int pigId, String tagNumber, String penNumber, String temperature, String status, String rfid, String gender, String age) {
        this.pigId = pigId;
        this.tagNumber = tagNumber;
        this.penNumber = penNumber;
        this.temperature = temperature;
        this.status = status;
        this.rfid = rfid;
        this.gender = gender;
        this.age = age;
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

    public String getTemperature() {
        if (temperature != null && !temperature.trim().isEmpty()) {
            return temperature.endsWith("°C") ? temperature : temperature + "°C";
        }
        switch (pigId % 5) {
            case 0: return "40.9°C";
            case 1: return "39.5°C";
            case 2: return "38.4°C";
            case 3: return "38.7°C";
            default: return "38.3°C";
        }
    }

    public double getTemperatureValue() {
        String temp = getTemperature();
        try {
            String clean = temp.replaceAll("[^0-9.]", "");
            if (!clean.isEmpty()) {
                return Double.parseDouble(clean);
            }
        } catch (Exception ignored) {}
        return 38.5;
    }

    public String getStatus() {
        if (status != null && !status.trim().isEmpty()) {
            return status;
        }
        double val = getTemperatureValue();
        if (val >= 40.0) {
            return "Possible ASF symptom - flag for vet";
        } else if (val >= 39.2) {
            return "Monitor closely • elevated";
        } else {
            return "Normal range • Healthy";
        }
    }

    public String getRfid() {
        return rfid;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public String getBadgeId() {
        if (tagNumber != null && !tagNumber.trim().isEmpty()) {
            String clean = tagNumber.replaceAll("[^0-9]", "");
            if (!clean.isEmpty()) {
                if (clean.length() == 1) return "00" + clean;
                if (clean.length() == 2) return "0" + clean;
                return clean;
            }
            return tagNumber;
        }
        return String.format("%03d", pigId);
    }

    public String getFormattedTitle() {
        if (tagNumber != null && tagNumber.startsWith("Pig #")) {
            return tagNumber;
        }
        return "Pig #" + getBadgeId();
    }

    public String getRfidDetails() {
        if (rfid != null && !rfid.trim().isEmpty()) {
            StringBuilder sb = new StringBuilder("RFID: " + rfid);
            if (gender != null && !gender.trim().isEmpty()) {
                sb.append("   ").append(gender).append(" Pig");
            }
            if (age != null && !age.trim().isEmpty()) {
                sb.append("   ").append(age).append(" mo");
            } else if (penNumber != null && !penNumber.trim().isEmpty()) {
                sb.append("   Pen ").append(penNumber);
            }
            return sb.toString();
        }
        String tagPart = (tagNumber != null && !tagNumber.trim().isEmpty()) ? tagNumber : String.format("%03d", pigId);
        String penPart = (penNumber != null && !penNumber.trim().isEmpty()) ? penNumber : "1";
        return "RFID: A-" + tagPart + "   Pen " + penPart;
    }

    @Override
    public String toString() {
        return tagNumber + " - Pen " + penNumber;
    }
}
