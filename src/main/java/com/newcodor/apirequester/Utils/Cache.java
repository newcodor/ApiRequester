package com.newcodor.apirequester.Utils;

import com.newcodor.apirequester.UI.UIController;
import com.newcodor.apirequester.bean.HttpRequest;
import javafx.application.Platform;

public class Cache {

    public  static UIController uiController;

    public  static boolean isUIUpdating = false;

    public  static HttpRequest currentRequest = new HttpRequest();
    public  static void setUIController(UIController _uiController){
        uiController = _uiController;
    }
    public  static  void setRequestStatus(String status){
        Platform.runLater(()->{
            uiController.requestStatus.setText(status);
        });
    }
}
