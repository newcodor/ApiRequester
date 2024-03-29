package com.newcodor.apirequester.UI;

import com.newcodor.apirequester.Utils.Formatter;
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
    public  void initialize(){
        this.instance = this;
        initContentTypeList();
    }
}
