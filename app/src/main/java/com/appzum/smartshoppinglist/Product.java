package com.appzum.smartshoppinglist;

public class Product implements Comparable<Product> {
    private final String id;
    private String name;
    private String description;
    private String status;

    public Product(String id, String name, String description, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @Override
    public int compareTo(Product product) {
        switch (status) {
            case "purchased":
                if (product.getStatus().equals("purchased")) {
                    return 0;
                } else {
                    return 1;
                }
            case "need":
                if (product.getStatus().equals("need")) {
                    return 0;
                } else {
                    return -1;
                }
            case "picked":
                if (product.getStatus().equals("need")) {
                    return 1;
                } else {
                    return 0;
                }
            default:
                return 0;
        }
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
