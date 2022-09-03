package com.appzum.smartshoppinglist;

public class Product implements Comparable<Product> {
    private final String id;
    private String name;
    private String description;
    private String status;
    private String created;
    private String edited;
    private String creator;
    private String purchased;


    public Product(String id, String name,
                   String description, String status,
                   String created, String edited,
                   String creator, String purchased) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.created = created;
        this.edited = edited;
        this.creator = creator;
        this.purchased = purchased;
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getEdited() {
        return edited;
    }

    public void setEdited(String edited) {
        this.edited = edited;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getPurchased() {
        return purchased;
    }

    public void setPurchased(String purchased) {
        this.purchased = purchased;
    }
}
