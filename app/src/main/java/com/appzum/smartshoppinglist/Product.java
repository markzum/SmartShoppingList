package com.appzum.smartshoppinglist;

public class Product {
    private final String id;
    private String name;
    private String description;
    private String status;

    public Product(String id, String name, String description, String status){
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
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
