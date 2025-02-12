package com.newcodor.apirequester.UI;

import com.newcodor.apirequester.Utils.Cache;
import com.newcodor.apirequester.Utils.Formatter;
import com.newcodor.apirequester.Utils.HttpClient;
import com.newcodor.apirequester.bean.HttpRequest;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import java.util.Base64;

public class ContentController {



    @FXML
    public  ComboBox contentTypeList;

    @FXML
    public  TextArea bodyContent;

    public final  static String [] contentTypes = new String[]{
            "application/json",
            "application/x-www-form-urlencoded",
            "text/plain",
            "text/html",
            "application/xml",
            "application/rdf+xml"
    };

    public  static ContentController instance;

    public void setContentUIParam(String  contentType, String body) {
        if(null!=contentType){
            this.contentTypeList.setValue(contentType);
        }
        this.bodyContent.setText(body);
    }

    public void setContentUIParam(String body) {
        this.bodyContent.setText(body);
    }

    @FXML
    private void initContentTypeList(){
//        contentTypeList.setItems(contentType);
        ObservableList contentTypeListItem = contentTypeList.getItems();
        contentTypeList.setValue(contentTypes[0]);
        for (String contentType: contentTypes) {
            contentTypeListItem.add(contentType);

        }
    }

    @FXML
    public void setKVParameterType(){
        contentTypeList.setValue("application/x-www-form-urlencoded");
    }

    @FXML
    public void makeJsonPretty(){
        String contentText =  bodyContent.getText().trim();
        if(contentText.startsWith("{")){
            bodyContent.setText(Formatter.prettyJson(contentText));
        }

    }
    @FXML
    public  void base64EncodeContent(){
        int selectedContentLength = bodyContent.getSelection().getLength();
        if(selectedContentLength==0){
            String fullContent = bodyContent.getText();
            if(!fullContent.isEmpty()){
                bodyContent.setText(Base64.getEncoder().encodeToString(fullContent.getBytes()));
            }
        }else{
            bodyContent.replaceSelection(Base64.getEncoder().encodeToString(bodyContent.getSelectedText().getBytes()));
        }
    }

    @FXML
    public  void base64DecodeContent(){
        int selectedContentLength = bodyContent.getSelection().getLength();
        if(selectedContentLength==0){
            String fullContent = bodyContent.getText();
            if(!fullContent.isEmpty()){
                bodyContent.setText(new String(Base64.getDecoder().decode(fullContent.replace("\n", ""))));
            }
        }else{
            bodyContent.replaceSelection(new String(Base64.getDecoder().decode(bodyContent.getSelectedText().replace("\n", ""))));
        }
    }

    @FXML
    public  void initialize(){
        this.instance = this;
        initContentTypeList();
        this.instance = this;
        bodyContent.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!Cache.isUIUpdating && newValue!=oldValue) { // 当焦点离开时（newValue为false）
                String input = bodyContent.getText().trim();
                if (input.isEmpty()) {
                    System.out.println("error: empty");
                } else {
                    Cache.currentRequest.setBody(input);
                    RawController.instance.rawContent.setText(HttpRequest.RequestToString(Cache.currentRequest));
                }
            }
        });
        contentTypeList.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!Cache.isUIUpdating && newValue!=oldValue) { // 当焦点离开时（newValue为false）
                if (contentTypeList.getValue().toString().isEmpty()) {
                    System.out.println("error: empty");
                } else {
                    Cache.currentRequest.setHeadersByKV("Content-Type",contentTypeList.getValue().toString());
                    RawController.instance.setHttpRaw(HttpRequest.RequestToString(Cache.currentRequest));

                }
            }
        });

    }
}
