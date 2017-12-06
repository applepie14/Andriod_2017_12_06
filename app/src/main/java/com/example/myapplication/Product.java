package com.example.myapplication;

/**
 * Created by 혜영 on 2017-12-06.
 */

public class Product {

    private int _id;
    private String _productname;
    private int _quantity;

    public Product() {
    }

    public Product(int id, String productname, int quantity) {
        this._id = id;
        this._productname = productname;
        this._quantity = quantity;
    }

//    public Product(String productname, int quantity) {
//        this._productname = productname;
//        this._quantity = quantity;
//    }

    // setter
    public void setID(int id) {
        this._id = id;
    }

    public void setProductName(String productname) {
        this._productname = productname;
    }

    public void setQuantity(int quantity) {
        this._quantity = quantity;
    }

    // getter
    public int getID() {
        return this._id;
    }

    public String getProductName() {
        return this._productname;
    }

    public int getQuantity() {
        return this._quantity;
    }


}


