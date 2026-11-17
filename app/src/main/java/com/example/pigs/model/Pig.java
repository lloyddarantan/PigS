
package com.example.pigs.model;

public class Pig {

    private int pigId;
    private String tagNumber;
    private String penNumber;

    public Pig(int pigId, String tagNumber, String penNumber) {
        this.pigId = pigId;
        this.tagNumber = tagNumber;
        this.penNumber = penNumber;
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

    @Override
    public String toString() {
        return tagNumber + " - Pen " + penNumber;
    }
}
