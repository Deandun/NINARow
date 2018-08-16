package com.DeaNoy;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NINARowUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("JavaFX Timeline Animation Example");

        /*Button btn1 = new Button("Timeline");
       btn1.setOnAction(new Button1Listener());
        HBox buttonHb1 = new HBox(10);
        buttonHb1.setAlignment(Pos.CENTER);
        buttonHb1.getChildren().addAll(btn1);

        VBox vbox = new VBox(30);
        vbox.setPadding(new Insets(100, 25, 100, 25));
        vbox.getChildren().addAll(buttonHb1);

        Scene scene = new Scene(vbox, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
*/
    }
    public static void main(String[] args) {
        Application.launch(args);
    }
}
