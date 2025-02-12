package com.newcodor.apirequester.UI;

import com.newcodor.apirequester.Utils.Cache;
import com.newcodor.apirequester.Utils.HttpClient;
import com.newcodor.apirequester.bean.HttpRequest;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class RawController {
    @FXML
    public TextArea rawContent;
    public  static RawController instance;
    @FXML
    public  void initialize(){
        this.instance = this;
        rawContent.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!Cache.isUIUpdating && newValue!=oldValue) { // 当焦点离开时（newValue为false）
                String input = rawContent.getText().trim();
                if (input.isEmpty()) {
                    System.out.println("error: empty");
                } else {
                    System.out.println("udpate request");
                    String Host = Cache.currentRequest.getHost();
                    String Protocol = Cache.currentRequest.getProtocol();
                    Cache.currentRequest = HttpRequest.StringToRequest(rawContent.getText().trim());
                    Cache.currentRequest.setHost(Host);
                    Cache.currentRequest.setProtocol(Protocol);
                    Cache.uiController.setRequestToUI(Cache.currentRequest);
                    // 此处可提交数据或更新UI
                }
            }
        });
    }

    public  void setHttpRaw(String httpRaw){
        this.rawContent.setText(httpRaw);

    }

    public  String getHttpRaw(){
        return this.rawContent.getText().trim();
    }

}
