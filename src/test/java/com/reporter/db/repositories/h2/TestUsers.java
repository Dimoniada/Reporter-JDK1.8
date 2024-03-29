package com.reporter.db.repositories.h2;

import com.google.common.base.MoreObjects;

import javax.persistence.*;

@Entity
@Table(name = "site_users")
public class TestUsers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected String username;
    protected String password;

    public TestUsers() { /**/ }

    public TestUsers(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("username", username)
            .add("password", password)
            .toString();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
