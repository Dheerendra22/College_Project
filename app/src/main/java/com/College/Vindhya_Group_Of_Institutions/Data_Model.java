package com.College.Vindhya_Group_Of_Institutions;

import java.util.ArrayList;

public class Data_Model {

String FirstName,LastName,Department,Year,Email,Phone,FatherName,Password,UserId,EnrollmentNumber,RollNumber,Collection,Role;
    ArrayList<String> SubjectList = new ArrayList<>();
    public Data_Model() {

    }

    public Data_Model(String firstName, String lastName, String department, String year,
                      String email, String phone, String fatherName, String password,
                      String userId, String enrollmentNumber, String rollNumber,
                      String collection, String role, ArrayList<String> subjectsList)
    {
        FirstName = firstName;
        LastName = lastName;
        Department = department;
        Year = year;
        Email = email;
        Phone = phone;
        FatherName = fatherName;
        Password = password;
        UserId = userId;
        EnrollmentNumber = enrollmentNumber;
        RollNumber = rollNumber;
        Collection = collection;
        Role = role;
        this.SubjectList = subjectsList;
    }

    public ArrayList<String> getSubjectList() {
        return SubjectList;
    }

    public void setSubjectList(ArrayList<String> subjectList) {
        SubjectList = subjectList;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getCollection() {
        return Collection;
    }

    public void setCollection(String collection) {
        Collection = collection;
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
