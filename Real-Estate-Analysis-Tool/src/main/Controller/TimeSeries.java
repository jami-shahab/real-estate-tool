package main.Controller;

import java.util.Objects;

public class TimeSeries {


    private String geo;
    private String from;
    private String to;

    public TimeSeries(String geo, String date1, String date2) {
        this.geo = geo;
        this.from = date1;
        this.to = date2;
    }

    public String getGeo() {
        return geo;
    }

    public String getFromDate() {
        return from;
    }

    public String getToDate() {
        return to;
    }


    // Overriding the equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSeries that = (TimeSeries) o;
        return Objects.equals(geo, that.geo) &&
                Objects.equals(from, that.from) &&
                Objects.equals(to, that.to);
    }

    //for the use of the HasSet In Add TimeSeries
    @Override
    public int hashCode() {
        return Objects.hash(geo, from, to);
    }


    @Override
    public String toString() {
        return "{" +
                "geo='" + geo + '\'' +
                ", From='" + from + '\'' +
                ", To='" + to + '\'' +
                '}';
    }
}
