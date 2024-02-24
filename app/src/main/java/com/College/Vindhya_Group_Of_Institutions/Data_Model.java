package com.College.Vindhya_Group_Of_Institutions;

public class Data_Model {

String FirstName,LastName,Department,Year,Email,Phone,Father,Password,userId;

    public Data_Model() {
    }

    public Data_Model(String firstName, String lastName, String department, String year, String email, String phone, String father, String password,String userId) {
        FirstName = firstName;
        LastName = lastName;
        Department = department;
        Year = year;
        Email = email;
        Phone = phone;
        Father = father;
        Password = password;
        this.userId = userId;

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getFather() {
        return Father;
    }

    public void setFather(String father) {
        Father = father;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
