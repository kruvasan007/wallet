package com.example.wallet.Model;

public class Card {
    private String name;
    private String barcode;
    private String type;
    private Double distance;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNameCard() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setNameCard(String nameActivity) {
        this.name = nameActivity;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
