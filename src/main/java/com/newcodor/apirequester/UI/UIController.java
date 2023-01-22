package com.newcodor.apirequester.UI;

import com.newcodor.apirequester.Utils.AsyncAction;
import com.newcodor.apirequester.Utils.Cache;
import com.newcodor.apirequester.Utils.HttpClient;
import com.newcodor.apirequester.bean.HttpRequest;
import com.newcodor.apirequester.bean.HttpResponse;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Window;

import javax.net.ssl.SSLException;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UIController<T> {
    @FXML
    private Button fetchReponseBtn;
    @FXML
    private TextField urlTextField;
    @FXML
    private TextArea responseTextArea;

    @FXML
    private ComboBox httpMethod;

    @FXML
    public Label requestStatus;

    private static Lock lock = new ReentrantLock();
    private static HashMap<String,Proxy.Type> proxyType = new HashMap();
    public static  HashMap<String,Object> proxySetting = new HashMap<>();
    static {
        proxyType.put("HTTP",Proxy.Type.HTTP);
        proxyType.put("SOCKS",Proxy.Type.SOCKS);

        proxySetting.put("enable",false);
        proxySetting.put("type","HTTP");
        proxySetting.put("host","127.0.0.1");
        proxySetting.put("port","8080");
    }
    @FXML
    void addAllowMethod() throws  Exception{
        ArrayList<String> allowMethod = HttpClient.allowMethod;
//        httpMethod.setPromptText(allowMethod.get(0));
        for(String method : allowMethod){
            httpMethod.getItems().add(method);
        }
//        httpMethod.setValue(allowMethod.get(0));
        httpMethod.getSelectionModel().select(0);
    }

    @FXML
    void initialize() throws Exception{
        addAllowMethod();
        Cache.setUIController(this);
    }

    @FXML
    public void handleFetchTask(ActionEvent actionEvent) throws InvocationTargetException, IllegalAccessException, MalformedURLException, ClassNotFoundException {
        String url = this.urlTextField.getText().trim();
        if(url.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText("url can't be empty!");
            alert.showAndWait();
        }else{
            responseTextArea.setText("");
            requestStatus.setText("waiting....");
            System.out.println(httpMethod.getValue());
            System.out.println("request.....");
            HttpRequest request = new HttpRequest(httpMethod.getValue().toString(),url, HeaderController.conventListToMap(HeaderController.headers),ContentController.instance.bodyContent.getText());
            System.out.println("ok");
//            HashMap params = new HashMap();
//            params.put("text",String.class);
//            testOperation();
            System.out.println("call instance: "+this);
            new AsyncAction(this,"fetch",request,responseTextArea,requestStatus).start();
//            new AsyncAction("com.newcodor.apitest.UI.UIController","fetch",request,responseTextArea,requestStatus).start();
        }
    }


    public void fetch(HttpRequest request,TextArea targetTextArea,Label requestStatus) throws InvocationTargetException, IllegalAccessException, MalformedURLException{
        String status ="Done";
        try{
//            lock.lock();
            HttpResponse response = HttpClient.request(request.method,request.url,7,null,request.headers,request.content);
//            Cache.setRequestStatus("Done");
            StringBuffer responseText = new StringBuffer();
//            double scrollTop = targetTextArea.getScrollTop();
//            int caretPosition =targetTextArea.caretPositionProperty().get();
//            System.out.println("start position: "+caretPosition);
            for (Map.Entry<String, List<String>> entry : response.headers.entrySet()) {
                    for (String value : entry.getValue()) {
                        if (null == entry.getKey()) {
                            responseText.insert(0,value + "\n");
//                            targetTextArea.insertText(0, value + "\n");
                        } else {
                            responseText.append(entry.getKey() + ":" + value + "\n");
//                            targetTextArea.appendText(entry.getKey() + ":" + value + "\n");
                        }
                    }
                }
                responseText.append("\n");
//                targetTextArea.appendText("\n");
                responseText.append(response.responseText);
//                targetTextArea.appendText(response.responseText);
//                targetTextArea.setScrollTop(scrollTop);
//                targetTextArea.positionCaret(caretPosition);
                targetTextArea.setText(responseText.toString());
                Platform.runLater(()->{
                    requestStatus.setText("Done");
                });
        }catch (SocketTimeoutException | SSLException | SocketException e1  ){
//            Cache.uiController.requestStatus.setText(e1.getMessage());
            System.out.println(e1.getMessage());
            status= e1.getMessage().toString();
        } catch (Exception e){
//            requestStatus.setText(e.getMessage());
            System.out.println(e.getMessage());
//            e.printStackTrace();
        }finally {
            System.out.println(status);
            if(!status.equals("Done")){
                String finalStatus = status;
                Platform.runLater(()->{
                    requestStatus.setText(finalStatus);
                });
                }
//            lock.unlock();
        }
    }
    public void testThread(String showText,Integer count){
        System.out.println(showText);
        System.out.println(count+11);
//        Alert info = new Alert(Alert.AlertType.INFORMATION);
//        info.setTitle("TestInfo");
//        info.setContentText(showText);
//        info.showAndWait();
    }

    public void  getProxyStatus(){


    }
//    @FXML
    public  void showProxyPanle(ActionEvent actionEvent){
        Alert alert = new Alert(Alert.AlertType.NONE);
        Window window = alert.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest((e)->{
            System.out.println("close proxy pane");
            window.hide();
        });
        alert.setTitle("Proxy Setting");
        ToggleGroup proxyStatusSwitchGroups = new ToggleGroup();
        RadioButton enableProxy = new RadioButton("enable");
        RadioButton disableProxy = new RadioButton("disable");
        enableProxy.setToggleGroup(proxyStatusSwitchGroups);
        disableProxy.setToggleGroup(proxyStatusSwitchGroups);
        if((Boolean) proxySetting.get("enable")){
            enableProxy.setSelected(true);
        }else{
            disableProxy.setSelected(true);
        }
        HBox proxyStatusBox = new HBox();
        proxyStatusBox.getChildren().add(enableProxy);
        proxyStatusBox.getChildren().add(disableProxy);
        proxyStatusBox.setSpacing(10.0D);
        alert.setContentText("ok");
        GridPane proxyGridePane = new GridPane();
        proxyGridePane.setVgap(15.0D);
        proxyGridePane.setPadding(new Insets(20.0D,20.0D,0,10.0D));
        Label proxyTypeLablel = new Label("Type");
        ComboBox proxyTypeComboBox = new ComboBox();
        proxyTypeComboBox.getItems().addAll(new String[]{"HTTP","SOCKS"});
        proxyTypeComboBox.setValue((String)proxySetting.get("type"));
//        if(!(((String)proxySetting.get("type")).trim().equals(""))){
//            proxyTypeComboBox.setValue((String)proxySetting.get("type"));
//        }else{
//            proxyTypeComboBox.getSelectionModel().select(0);
//        }
        Label ipLabel = new Label("IP");
        Label portLabel = new Label("Port");
        TextField ipInput = new TextField((String) proxySetting.get("host"));
        TextField portInput = new TextField((String) proxySetting.get("port"));
        proxyGridePane.add(proxyStatusBox,1,0);
        proxyGridePane.add(proxyTypeLablel,0,1);
        proxyGridePane.add(proxyTypeComboBox,1,1);
        proxyGridePane.add(ipLabel,0,2);
        proxyGridePane.add(ipInput,1,2);
        proxyGridePane.add(portLabel,0,3);
        proxyGridePane.add(portInput,1,3);
        Label proxyUserLabel = new Label("User");
        Label proxyPasswordLabel = new Label("Password");
        TextField username =new TextField();
        PasswordField password = new PasswordField();
        proxyGridePane.add(proxyUserLabel,0,4);
        proxyGridePane.add(username,1,4);
        proxyGridePane.add(proxyPasswordLabel,0,5);
        proxyGridePane.add(password,1,5);
        Button ensureButton = new Button("save");
        Button cancelButton = new Button("cancel");

        ensureButton.setOnAction((e)->{
            String selectedStatus =((RadioButton) proxyStatusSwitchGroups.getSelectedToggle()).getText();
            if("enable".equals(selectedStatus)){
                proxySetting.put("enable",true);
                proxySetting.put("type",proxyTypeComboBox.getValue());
                proxySetting.put("host",ipInput.getText().trim());
                proxySetting.put("port",portInput.getText().trim());
                System.out.println("[+] enable proxy:"+(String) proxySetting.get("type")+"@"+(String) proxySetting.get("host")+":"+(String)proxySetting.get("port"));

                Proxy proxy = new Proxy((Proxy.Type)proxyType.get(proxyTypeComboBox.getValue()),new InetSocketAddress((String) proxySetting.get("host"),Integer.parseInt((String)proxySetting.get("port"))));
                HttpClient.globalProxy= proxy;
            }else{
                System.out.println("[+] disable proxy");
                proxySetting.put("enable",false);
                HttpClient.globalProxy= Proxy.NO_PROXY;
            }
            window.hide();
        });
        cancelButton.setOnAction((e)->{
            window.hide();
        });
        proxyGridePane.add(ensureButton,0,6);
        proxyGridePane.add(cancelButton,1,6);
        alert.getDialogPane().setContent(proxyGridePane);
        alert.showAndWait();
    }


}
