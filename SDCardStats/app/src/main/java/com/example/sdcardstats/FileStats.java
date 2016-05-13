package com.example.sdcardstats;

import android.os.Parcel;
import android.os.Parcelable;

public class FileStats implements Parcelable{
    private String name;
    private double size;

    public FileStats(){

    }

    public FileStats(Parcel source) {
        this.name = source.readString();
        this.size = source.readDouble();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(size);
    }

    public static final Parcelable.Creator<FileStats> CREATOR = new Parcelable.Creator<FileStats>(){
        @Override
        public FileStats createFromParcel(Parcel source) {
            return new FileStats(source);
        }

        @Override
        public FileStats[] newArray(int size) {
            return new FileStats[0];
        }
    };

    @Override
    public String toString() {
        return "FileStats{" +
                "name='" + name + '\'' +
                ", size=" + size +
                '}';
    }
}
