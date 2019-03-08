package com.jbseppanen.shoppingjava.product;

public class Product {
    int productid;
    String productname;
    String description;
    String thumbImageUrl;
    double price;
    private int qtyinstock;

    public Product() {
    }

    public Product(int productid, String productname, String description, String thumbImageUrl, double price, int qtyinstock) {
        this.productid = productid;
        this.productname = productname;
        this.description = description;
        this.thumbImageUrl = thumbImageUrl;
        this.price = price;
        this.qtyinstock = qtyinstock;
    }

/*
    public Product(JSONObject json, int inputId) {


        this.productid = inputId;
        try {
            JSONObject names = json.getJSONObject("productname");
            String fullName = "";

            fullName += names.getString("title") + " ";
            fullName += names.getString("first") + " ";
            fullName += names.getString("last") + " ";

            this.productname = capitalize(fullName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            this.description = json.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            this.email = json.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject imageUrls = null;
        try {
            imageUrls = json.getJSONObject("picture");
            this.thumbImageUrl = imageUrls.getString("thumbnail");
            this.largeImageUrl = imageUrls.getString("large");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
*/

    public int getProductid() {
        return productid;
    }

    public void setProductid(int productid) {
        this.productid = productid;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbImageUrl() {
        return thumbImageUrl;
    }

    public void setThumbImageUrl(String thumbImageUrl) {
        this.thumbImageUrl = thumbImageUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQtyinstock() {
        return qtyinstock;
    }

    public void setQtyinstock(int qtyinstock) {
        this.qtyinstock = qtyinstock;
    }
}
