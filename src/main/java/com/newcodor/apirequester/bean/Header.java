package com.newcodor.apirequester.bean;

public class Header {

    public  String Name;
    public String Value;

    public Header(String headerName,String headerValue){
        this.Name = headerName;
        this.Value =headerValue;
    }
    public String getName() {
        return this.Name;
    }

    public void setName(String headerName) {
        this.Name = headerName;
    }

    public String getValue() {
        return this.Value;
    }

    public void setValue(String headerValue) {
        this.Value = headerValue;
    }

    @Override
    public String toString() {
        return "Header{" +
                "Name='" + Name + '\'' +
                ", Value='" + Value + '\'' +
                '}';
    }
}
