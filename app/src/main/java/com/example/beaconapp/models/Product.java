package com.example.beaconapp.models;

import java.util.Objects;

public class Product {

    String _id;
    String name;
    String photo;
    String region;
    double price;
    double discount;

    public Product() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getUpdatedPrice(){
        return ((100 - discount) * this.price) / 100;
    }

    @Override
    public String toString() {
        return "Product{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                ", region='" + region + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(_id, product._id) && Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, name);
    }

}