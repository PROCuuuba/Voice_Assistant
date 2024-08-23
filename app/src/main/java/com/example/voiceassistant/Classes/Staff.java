package com.example.voiceassistant.Classes;

public class Staff {
    private String name;
    private String surname;
    private String phoneNumber;
    private String profilePictureUrl;

    public Staff() {
        // Пустой конструктор по умолчанию для Firebase
    }

    public Staff(String name, String surname, String phoneNumber, String profilePictureUrl) {
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
}