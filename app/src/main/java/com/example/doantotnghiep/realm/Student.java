package com.example.doantotnghiep.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Student extends RealmObject
{
    @PrimaryKey
    private String Roll_number;

    private String Phone_no;
    private String Student_name;
    private String Mathe;
    private String Mac_ID2;

    public String getRoll_number() {return Roll_number;}
    public String getPhone_no() {return Phone_no;}
    public String getStudent_name() {return Student_name;}
    public String getMathe() {return Mathe;}
    public String getMac_ID2() {return Mac_ID2;}

    public void setRoll_number(String Roll_number) {this.Roll_number = Roll_number;}
    public void setPhone_no(String Phone_no) {this.Phone_no = Phone_no;}
    public void setStudent_name(String Student_name) {this.Student_name = Student_name;}
    public void setMathe(String Mathe) {this.Mathe = Mathe;}
    public void setMac_ID2(String Mac_ID2) {this.Mac_ID2 = Mac_ID2;}

}
