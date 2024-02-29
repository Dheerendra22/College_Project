package com.College.Vindhya_Group_Of_Institutions;

import java.util.ArrayList;

public class Subject_Data {
    ArrayList<String> SubjectList;

    public Subject_Data() {

    }

    public Subject_Data(ArrayList<String> subjectList) {
        SubjectList = subjectList;
    }

    public ArrayList<String> getSubjectList() {
        return SubjectList;
    }

    public void setSubjectList(ArrayList<String> subjectList) {
        SubjectList = subjectList;
    }
}
