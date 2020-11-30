package com.optic.projectofinal.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class User implements Serializable {
    private String id;
    private String name;
    private String email;
    private String lastName;
    private String profileImage;
    private String coverPageImage;
    private String schedule;
    private String about;
    private Long birthdate;
    private int codeCountry;
    private int phoneNumber;
    private String location;
    private double pricePerHour;
    private int sex;
    private boolean professional;
    private boolean online;
    private long lastConnection;
    private long timestamp;
    private ArrayList<String> idsSubCategories;
    private ArrayList<String> idsCategories;
    private boolean verified;
    private ArrayList<Integer> resources;
    private ArrayList<Skill> skills;


    public User() {

    }

    public User(String name, String lastName, String schedule, String about, String location, int sex,String profileImage) {
        this.name = name;
        this.lastName = lastName;
        this.schedule = schedule;
        this.about = about;
        this.location = location;
        this.sex = sex;
        this.profileImage=profileImage;
        ///

    }
    public ArrayList<Integer> getResources() {
        return resources;
    }

    public void setResources(ArrayList<Integer> resources) {
        this.resources = resources;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public int getCodeCountry() {
        return codeCountry;
    }

    public void setCodeCountry(int codeCountry) {
        this.codeCountry = codeCountry;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public ArrayList<String> getIdsSubCategories() {
        return idsSubCategories;
    }

    public void setIdsSubCategories(ArrayList<String> idsSubCategories) {
        this.idsSubCategories = idsSubCategories;
    }

    public ArrayList<String> getIdsCategories() {
        return idsCategories;
    }

    public void setIdsCategories(ArrayList<String> idsCategories) {
        this.idsCategories = idsCategories;
    }

    public boolean isProfessional() {
        return professional;
    }

    public void setProfessional(boolean professional) {
        this.professional = professional;
    }

    public ArrayList<Skill> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<Skill> skills) {
        this.skills = skills;
    }

    public String getCoverPageImage() {
        return coverPageImage;
    }
    public void setCoverPageImage(String coverPageImage) {
        this.coverPageImage = coverPageImage;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Long getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Long birthdate) {
        this.birthdate = birthdate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }


    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public long getLastConnection() {
        return lastConnection;
    }

    public void setLastConnection(long lastConnection) {
        this.lastConnection = lastConnection;
    }


    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }




    public User(String name, String lastName, String age) {
        this.name =name;
        this.lastName =lastName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setDefaultData() {
        professional =false;
        timestamp=new Date().getTime();
        online =false;
    }
}