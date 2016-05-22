package com.example.dk.mapsviabt;

/**
 * Created by Karteek Dhara on 5/20/2016.
 */

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



@Generated("org.jsonschema2pojo")
public class ServerData {

    @SerializedName("point list")
    @Expose
    private List<List<Double>> pointList = new ArrayList<List<Double>>();

    public List<List<Double>> getPointList() { return pointList; }

    public void setPointlist(List<List<Double>> pointList)
    {
        this.pointList=pointList;
    }


}