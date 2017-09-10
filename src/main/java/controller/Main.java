package controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;

    @Override
    public void init() throws Exception {

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        primaryStage.setScene(new Scene(root, 640, 480));
//        primaryStage.setTitle("Modbus Simulator");
//        primaryStage.show();

//        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent event) {
//
//                Platform.exit();
//                System.exit(0);
//            }
//        });

        // When need controller
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/sample.fxml"));
        Parent root = fxmlLoader.load();
        //root.getStylesheets().add(getClass().getResource("resources/Main.css").toString());

        controller = (Controller) fxmlLoader.getController();
        System.out.println(controller);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setTitle("Modbus Simulator");
        primaryStage.setMinWidth(640);
        primaryStage.setMinHeight(480);

        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        controller.onStopAllSlave();

        Platform.exit();
        System.exit(0);
    }
}
