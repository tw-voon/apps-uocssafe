package app.uocssafe.com.uocs_safe.Virtual_map;

/**
 * Created by VoonTw on 28-Feb-17.
 */

public class Locations {
    private double latitude, longitude;
    private int reportID;
    public String reportTitle, reportDescription;

    public void setLatitude (double latitude){
        this.latitude = latitude;
    }

    public void setLongitude (double longitude){
        this.longitude = longitude;
    }

    public void setReportID (int reportID){
        this.reportID = reportID;
    }

    public void setReportTitle (String reportTitle){
        this.reportTitle = reportTitle;
    }

    public void setReportDescription (String reportDescription){
        this.reportDescription = reportDescription;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public int getReportID(){
        return reportID;
    }

    public String getReportTitle(){
        return reportTitle;
    }

    public String getReportDescription(){
        return reportDescription;
    }
}
