package com.newcodor.apirequester.UI;
import com.newcodor.apirequester.bean.Header;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;


public class HeaderController<T> {

    @FXML
    public TextField headerName;

    @FXML
    public  TextField headerValue;

    @FXML
    public   TableView headersTable;
    public static ObservableList<Header> headers = FXCollections.observableArrayList();

//    public ArrayList<String> tableColumnName = new ArrayList<>();
    public  ArrayList<String>  headerNames = new ArrayList<>();

    public  static HeaderController instance;
    @FXML
   public void initialize() {
        this.instance = this;
       ObservableList<TableColumn> columns = headersTable.getColumns();
       headers.add(new Header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36"));
       headerNames.add("User-Agent");
       ArrayList<String> headerFields = new ArrayList(){};
       for (Field field : Header.class.getFields()) {
           headerFields.add(field.getName());
       }
       if(!headerFields.isEmpty()){
           for (TableColumn column : columns){
               if(headerFields.contains(column.getText().trim())){
                   column.setCellValueFactory(new PropertyValueFactory<>(column.getText().trim()));
                   column.setCellFactory(TextFieldTableCell.forTableColumn());
                       column.setOnEditCommit(t-> {
                           try {
                               updateHeaderNames((TableColumn.CellEditEvent<T, Boolean>) t);
                           } catch (NoSuchMethodException e) {
                               throw new RuntimeException(e);
                           } catch (InvocationTargetException e) {
                               throw new RuntimeException(e);
                           } catch (IllegalAccessException e) {
                               throw new RuntimeException(e);
                           }
                       });
               }
//               tableColumnName.add(column.getText().trim());
           }
           System.out.println(headerFields.toString());
//           System.out.println(tableColumnName.toString());
       }
       headersTable.setItems(headers);
       //headersTable.setEditable(true);

   }

    public void  updateHeaderNames(TableColumn.CellEditEvent<T, Boolean> t) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//            String editName = ((Header)t.getRowValue()).getName();
        String oldValue = String.valueOf(t.getOldValue()).trim();
        String newValue = String.valueOf(t.getNewValue()).trim();
        System.out.println("old:"+oldValue+",new:"+newValue);
        String propertyName = t.getTableColumn().getText().trim();
        System.out.println(propertyName);
            if(oldValue!=newValue){
                if(propertyName.equals("Name") && headerNames.contains(newValue)){
                    Alert info = new Alert(Alert.AlertType.WARNING);
                    info.setTitle("Warning");
                    info.setContentText(newValue+" is exit!");
                    info.showAndWait();
//                    ((Header)t.getRowValue()).setName(oldName);
//                    t.getRowValue()
                    t.getTableView().refresh();
                    System.out.println(headers);

                }else{

//                    System.out.println(t.getTableView().getItems().get(t.getTableView().getSelectionModel().getSelectedIndex()));
                    Header oldHeader = (Header) t.getRowValue();


                    Method setPropertyMethod=  oldHeader.getClass().getDeclaredMethod("set"+propertyName,String.class);
                    setPropertyMethod.invoke(oldHeader,newValue);
                    //                    this.headers.set(t.getTableView().getSelectionModel().getSelectedIndex(),().setName(newValue));
                    if(propertyName.equals("Name")){
                        headerNames.remove(oldValue);
                        headerNames.add(newValue);
                    }

                    System.out.println("new: "+this.headers);
                }
            }
    }

    @FXML
    public void addHeaderToTableView(ActionEvent actionEvent){
        if(!this.headerNames.contains(headerName.getText().trim())){
            this.headers.add(new Header(headerName.getText().trim(),headerValue.getText().trim()));
            headerNames.add(headerName.getText().trim());
            System.out.println(headers);
        }else{
            Alert info = new Alert(Alert.AlertType.WARNING);
            info.setTitle("Warning");
            info.setContentText(headerName.getText().trim()+" is exit");
            info.showAndWait();
        }
    }

    @FXML
    public void deleteHeaderFromTableView(ActionEvent actionEvent){
        if(this.headersTable.getItems().size()>0){
            Header deleteHeader = (Header) this.headersTable.getSelectionModel().getSelectedItem();
            this.headers.remove(deleteHeader);
            headerNames.remove(deleteHeader.getName());
        }
    }

    public static HashMap conventListToMap(ObservableList<Header> headers){
        HashMap<String,String> headerMap = new HashMap();
        for(Header header: headers){
            headerMap.put(header.getName(),header.getValue());
        }
        headerMap.put("Content-Type",ContentController.instance.contentTypeList.getValue().toString().trim());
        return  headerMap;
    }
}
