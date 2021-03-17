package com.eventapp.helpers;

import com.eventapp.models.BasicModel;

import java.util.ArrayList;
import java.util.List;

public class mConstants {

    public static final ArrayList<BasicModel> Types = new ArrayList<BasicModel>() {
        {
            add(new BasicModel(1, "Party"));
            add(new BasicModel(2, "Conference"));
            add(new BasicModel(3, "Fashion show"));
            add(new BasicModel(4, "Festival"));
            add(new BasicModel(5, "Gaming"));
            add(new BasicModel(6, "Performance art"));
            add(new BasicModel(7, "Presentation"));
            add(new BasicModel(8, "Prom"));
            add(new BasicModel(9, "Race"));
            add(new BasicModel(10, "Reunion"));
            add(new BasicModel(11, "Religious event"));
            add(new BasicModel(12, "Signing"));
        }
    };

}
