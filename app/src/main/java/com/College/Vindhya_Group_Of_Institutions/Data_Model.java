package com.College.Vindhya_Group_Of_Institutions;

public class Data_Model {

String FirstName,LastName,Department,Year,Email,Phone,FatherName,Password,UserId,EnrollmentNumber,RollNumber;

    public Data_Model() {
    }

    public Data_Model(String firstName, String lastName, String department, String year, String email, String phone, String father, String password, String userId, String enrollmentNumber, String rollNumber) {
        FirstName = firstName;
        LastName = lastName;
        Department = department;
        Year = year;
        Email = email;
        Phone = phone;
        FatherName = father;
        Password = password;
        UserId = userId;
        EnrollmentNumber = enrollmentNumber;
        RollNumber = rollNumber;
    }

    public String getEnrollmentNumber() {
        return EnrollmentNumber;
    }

    public void setEnrollmentNumber(String enrollmentNumber) {
        EnrollmentNumber = enrollmentNumber;
    }

    public String getRollNumber() {
        return RollNumber;
    }

    public void setRollNumber(String rollNumber) {
        RollNumber = rollNumber;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        userId = userId;
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

    public String getFatherName() {
        return FatherName;
    }

    public void setFatherName(String father) {
        FatherName = father;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
