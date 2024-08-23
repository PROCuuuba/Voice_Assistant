package com.example.voiceassistant.Classes;

public class Client {

    private String firebaseKey;
    private String surname;
    private String name;
    private String patronymic;
    private String telephoneNumber;
    private String problem;
    private String date;
    private String model;
    private String imageUrl;
    private String status;

    public Client() {
        // Обязательный пустой конструктор для Firebase
    }

    public Client(String surname, String name, String patronymic, String telephoneNumber, String problem, String date, String model, String imageUrl, String status) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.telephoneNumber = telephoneNumber;
        this.problem = problem;
        this.date = date;
        this.model = model;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public String getFirebaseKey() { return firebaseKey; }

    public void setFirebaseKey(String firebaseKey) { this.firebaseKey = firebaseKey; }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) { this.telephoneNumber = telephoneNumber; }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) { this.status = status; }
}