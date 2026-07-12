package com.firstclub.membership.model;

public final class User {
    private final long id;
    private final String name;
    private final String email;
    private volatile String cohort;

    public User(long id, String name, String email, String cohort) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.cohort = cohort;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCohort() {
        return cohort;
    }

    public void setCohort(String cohort) {
        this.cohort = cohort;
    }
}
