package com.shamim.frremoteattendence.model_class;

public class door_loc_model_class {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getE_ID() {
        return E_ID;
    }

    public void setE_ID(String e_ID) {
        E_ID = e_ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getInTime() {
        return InTime;
    }

    public void setInTime(String inTime) {
        InTime = inTime;
    }

    public String getOutTime() {
        return OutTime;
    }

    public void setOutTime(String outTime) {
        OutTime = outTime;
    }

    public String getTardiness() {
        return Tardiness;
    }

    public void setTardiness(String tardiness) {
        Tardiness = tardiness;
    }

    public String getTotal_worked_hour_in_minutes() {
        return total_worked_hour_in_minutes;
    }

    public void setTotal_worked_hour_in_minutes(String total_worked_hour_in_minutes) {
        this.total_worked_hour_in_minutes = total_worked_hour_in_minutes;
    }

    public String getCumulative_work_hour() {
        return cumulative_work_hour;
    }

    public void setCumulative_work_hour(String cumulative_work_hour) {
        this.cumulative_work_hour = cumulative_work_hour;
    }

    String id;
    String E_ID;
    String Name;
    String Date;
    String InTime;
    String OutTime;
    String Tardiness;
    String total_worked_hour_in_minutes;
    String cumulative_work_hour;




    public door_loc_model_class(String id, String e_ID, String name, String date, String inTime, String outTime, String tardiness, String total_worked_hour_in_minutes, String cumulative_work_hour) {
        this.id = id;
        E_ID = e_ID;
        Name = name;
        Date = date;
        InTime = inTime;
        OutTime = outTime;
        Tardiness = tardiness;
        this.total_worked_hour_in_minutes = total_worked_hour_in_minutes;
        this.cumulative_work_hour = cumulative_work_hour;
    }






}
