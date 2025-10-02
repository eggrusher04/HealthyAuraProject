package com.FeedEmGreens.HealthyAura.entity;


//For now ignore this class, inheritance for role based access might not be recommended for Spring boot security
public class Admin extends Users {

    //private String role;

    public Admin(){
        super();
    }

    public void changeToAdmin(String role){
        setRole(role);
    }
}
