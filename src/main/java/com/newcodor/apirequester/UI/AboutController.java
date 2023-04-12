package com.newcodor.apirequester.UI;

import com.newcodor.apirequester.App;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;

public class AboutController {

    @FXML
    private Hyperlink projectLink;

    @FXML
    public void openLinkOnBrowser(){
        App.getInstance().getHostServices().showDocument(projectLink.getText());
    }
}
