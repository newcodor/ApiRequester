package com.newcodor.apirequester.Utils;

import com.newcodor.apirequester.UI.UIController;
import javafx.application.Platform;

public class Cache {

    public  static UIController uiController;
    public  static void setUIController(UIController _uiController){
        uiController = _uiController;
    }
    public  static  void setRequestStatus(String status){
        Platform.runLater(()->{
            uiController.requestStatus.setText(status);
        });
    }
}
