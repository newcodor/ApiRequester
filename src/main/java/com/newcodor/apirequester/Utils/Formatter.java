package com.newcodor.apirequester.Utils;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

public class Formatter {

    public  static String  prettyJson(String originStr){
        try {
            return new JSONObject(originStr).toString(4);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
//            throw new RuntimeException(e);
        }
        return originStr;
    }

}
