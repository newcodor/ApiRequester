package com.newcodor.apirequester.UI;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

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
    public  void base64EncodeContent(){
        int selectedContentLength = bodyContent.getSelection().getLength();
        if(selectedContentLength==0){
            String fullContent = bodyContent.getText();
            if(!fullContent.isEmpty()){
                bodyContent.setText(new sun.misc.BASE64Encoder().encode(fullContent.getBytes()));
            }
        }else{
            bodyContent.replaceSelection(new sun.misc.BASE64Encoder().encode(bodyContent.getSelectedText().getBytes()));
        }
    }

    @FXML
    public  void initialize(){
        this.instance = this;
        initContentTypeList();
    }
}
