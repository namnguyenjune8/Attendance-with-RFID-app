package com.example.doantotnghiep.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class ContestParticipant extends RealmObject {
    @PrimaryKey
    int id;
    int cid;
    @Required
    String contestName;
    @Required
    String date;
    int marks;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

//    public int getMarks() {
//        return marks;
//    }
//
//    public void setMarks(int marks) {
//        this.marks = marks;
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    public int getCid() {
//        return cid;
//    }
//
//    public void setCid(int cid) {
//        this.cid = cid;
//    }
//
//    public String getContestName() {
//        return contestName;
//    }
//
//    public void setContestName(String contestName) {
//        this.contestName = contestName;
//    }
}
