package controller; /**
 * Sample Skeleton for 'sample.fxml' Controller Class
 */

import com.ghgande.j2mod.modbus.ModbusException;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import com.jfoenix.validation.NumberValidator;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.Slave;
import view.TabPaneWorkspace;

import java.io.IOException;
import java.util.ArrayList;

public class Controller {
    private static final String MAX_SLAVE_ADDRESS = "255";
    private static final String MIN_SLAVE_ADDRESS = "0";

    // int value id for spi
    private int slaveId;

    private int selectedTab = -1;

    // List all slaves
    private ArrayList<Slave> slaves = null;

    private JFXDialogLayout dialogLayout = null;
    private HamburgerBackArrowBasicTransition burgerTransition = null;
    private JFXPopup toolbarPopup = null;

    // TableView with data
    private TabPaneWorkspace tabPaneWorkspace = null;


    // JFX init
    @FXML
    private JFXButton btnChoiseInteger;

    @FXML
    private JFXButton btnChoiseDouble;

    @FXML
    private StackPane stackBurger;

    @FXML
    private JFXBadge btnAddSlave;

    @FXML
    private StackPane dialogSlavePane;

    @FXML
    private JFXTabPane tabPane;

    @FXML
    private JFXHamburger mainBurger;

    @FXML
    private JFXHamburger rightBurger;

    @FXML // Navigation Drawer Menu
    private JFXDrawer drawer;

    @FXML
    private Label connetion_info;

    @FXML
    private Label online_status;



    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainPopup.fxml"));
        try {
            toolbarPopup = new JFXPopup(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        stackBurger.setOnMouseClicked((event) ->
                toolbarPopup.show(mainBurger,
                        JFXPopup.PopupVPosition.TOP,
                        JFXPopup.PopupHPosition.RIGHT,
                        -12,
                        15));

        dialogLayout = new JFXDialogLayout();
        dialogLayout.setDisable(true);

        slaves = new ArrayList<>();
        tabPaneWorkspace = new TabPaneWorkspace();

        btnChoiseInteger.setDisable(true);
        btnChoiseDouble.setDisable(true);

        burgerTransition = new HamburgerBackArrowBasicTransition(rightBurger);
        burgerTransition.setRate(-1);
        rightBurger.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            drawer.setDisable(false);
            burgerTransition.setRate(burgerTransition.getRate() * -1);
            burgerTransition.play();

            if (drawer.isShown()) {
                drawer.setDisable(true);
                drawer.close();
            } else {
                drawer.open();
            }
        });

        // tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends Tab> ov, Tab t, Tab t1) -> {
                    selectedTab = tabPane.getSelectionModel().getSelectedIndex();
                    tabPaneWorkspace.setLastIndexTab(selectedTab);
                }
        );

        try {
            AnchorPane anchorDrawer = FXMLLoader.load(getClass().getResource("/drawer.fxml"));
            ObservableList<Node> anchorNodes = anchorDrawer.getChildren();
            VBox drawerButtons = (VBox) anchorNodes.get(0);
            drawer.setSidePane(anchorDrawer);
            // when create drawer, send back
            drawer.setDisable(true);

            for (Node node : drawerButtons.getChildren()) {
                if (node.getAccessibleText() != null) {
                    node.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
                        switch (node.getAccessibleText()) {
                            case "Settings":
                                drawer.setDisable(true);

                                burgerTransition.setRate(burgerTransition.getRate() * -1);
                                burgerTransition.play();
                                drawer.close();

                                if (dialogLayout.isDisabled()) {

                                    // So that dialogLayout was active
                                    tabPane.setDisable(true);

                                    // Show me dialog with settings
                                    dialogLayout.setDisable(false);
                                    dialogLayout.setHeading(new Text("Change port"));

                                    JFXDialog dialog = new JFXDialog(dialogSlavePane, dialogLayout,
                                            JFXDialog.DialogTransition.CENTER);
                                    //dialog.setOverlayClose(false);

                                    VBox dialogBox = new VBox();

                                    // TODO: replace with fxml
                                    // Edit text field for slave id
                                    JFXTextField editPort = new JFXTextField();
                                    editPort.setFocusColor(Color.RED);

                                    dialogBox.getChildren().add(editPort);

                                    editPort.setOnKeyReleased(event2 -> {
                                        String portText = editPort.getText();
                                        if (portText.equals("")) {
                                            editPort.setFocusColor(Color.RED);
                                        } else {
                                            editPort.setFocusColor(Color.GREEN);

                                        }
                                    });

                                    // Create buttons for dialog
                                    JFXButton btnOk = new JFXButton("ok");
                                    JFXButton btnCancel = new JFXButton("Cancel");

                                    // Buttons listener's
                                    btnOk.setOnAction(event1 -> {
                                        // Read input information about slave id
                                        tabPane.setDisable(false);
                                        dialog.close();
                                        dialogLayout.setDisable(true);
                                    });

                                    btnCancel.setOnAction(event1 -> {
                                        tabPane.setDisable(false);
                                        dialog.close();
                                        dialogLayout.setDisable(true);
                                    });

                                    // Dialog formation
                                    dialogLayout.setBody(dialogBox);
                                    dialogLayout.setActions(btnOk, btnCancel);
                                    dialog.show();
                                }

                                break;

                            case "Exit":
                                onStopAllSlave();
                                Platform.exit();
                                System.exit(0);
                                break;
                        }
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Button add slave by id
        btnAddSlave.setOnMouseClicked((click) -> {
            // create here new dialog handler
            if (dialogLayout.isDisabled()) {
                dialogLayout.setDisable(false);

                // So that dialogLayout was active
                tabPane.setDisable(true);
                dialogLayout.setHeading(new Text("Add new slave"));

                JFXDialog dialog = new JFXDialog(dialogSlavePane, dialogLayout,
                        JFXDialog.DialogTransition.CENTER);
                dialog.setOverlayClose(false);

                // TODO: replace with fxml
                // Edit text field for slave id
                JFXTextField editSlaveId = new JFXTextField();

                // Check on validate number in 1 - 255
                NumberValidator numberValidator = new NumberValidator();
                editSlaveId.setValidators(numberValidator);
                numberValidator.setMessage("Only number (1 - 255)");

                editSlaveId.setOnKeyReleased((event)-> {
                            System.out.println(event);
                            boolean validate = editSlaveId.validate();
                            String slaveText = editSlaveId.getText();
                            //boolean textMatches = slaveText.matches("^[0-9][0-9]*");
                            if (validate) {
                                //   if (validate) {
                                Integer slaveValue = Integer.valueOf(slaveText);
                                if (slaveValue > 255) {
                                    editSlaveId.setText(MAX_SLAVE_ADDRESS);
                                }
                                if (slaveValue < 1) {
                                    editSlaveId.setText(MIN_SLAVE_ADDRESS);
                                }
                                // }
                            } else {
                                editSlaveId.setText("");
                            }
                        }
                );
                editSlaveId.setOnKeyReleased((event1)-> {
                            String ipText = editSlaveId.getText();
                            boolean textMatches = ipText.matches("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
                            if (textMatches) {
                                editSlaveId.setFocusColor(Color.GREEN);
                            } else {
                                editSlaveId.setFocusColor(Color.RED);
                            }
                        }
                );
                // Create buttons for dialog
                JFXButton btnOk = new JFXButton("ok");
                JFXButton btnCancel = new JFXButton("Cancel");

                // Add slave dialog layout buttons listener's
                btnOk.setOnAction(event -> {
                    // Read input information about slave id
                    String slaveIdText = editSlaveId.getText();
                    if ("".equals(slaveIdText)) {
                        slaveIdText = "1";
                        slaveId = 1;
                    } else {
                        slaveId = Integer.valueOf(slaveIdText);
                    }

                    Slave slave = new Slave(slaveId);
                    try {
                        slave.fillHoldingRegs();
                        slave.start();
                        btnChoiseInteger.setStyle("-fx-background-color: white");
                        connetion_info.setText(slave.getConnInfo());
                        online_status.setText("online");
                    } catch (ModbusException e) {
                        e.printStackTrace();
                    }

                    slaves.add(slave);
                    // by default
                    tabPaneWorkspace.setLastIndexTab(slaves.size() - 1);
                    tabPaneWorkspace.createTableView(slaveIdText, tabPane, slaves);

                    // Confirm input info
                    btnChoiseInteger.setDisable(false);
                    btnChoiseDouble.setDisable(false);
                    tabPane.setDisable(false);
                    dialog.close();
                    dialogLayout.setDisable(true);
                });

                btnCancel.setOnAction(event -> {
                    tabPane.setDisable(false);
                    dialog.close();
                    dialogLayout.setDisable(true);
                });


                // Dialog formation
                dialogLayout.setBody(editSlaveId);
                dialogLayout.setActions(btnOk, btnCancel);
                dialog.show();
            }
        });
    }


    // Stop slaves connections
    public void onStopAllSlave() {
        for (Slave slave : slaves) {
            try {
                slave.stop();
            } catch (ModbusException e) {
                e.printStackTrace();
            }
        }
    }

    public void onClickInteger(ActionEvent actionEvent) {
        btnChoiseDouble.setStyle("-fx-background-color: #117bf3");
        btnChoiseInteger.setStyle("-fx-background-color: white");
        tabPaneWorkspace.changeTableInt(tabPane);
    }

    public void onClickDouble(ActionEvent actionEvent) {
        btnChoiseInteger.setStyle("-fx-background-color: #117bf3");
        btnChoiseDouble.setStyle("-fx-background-color: white");
        tabPaneWorkspace.changeTableDouble(tabPane);
    }
}


//        Create Hamburger menu
//        JFXPopup popup = new JFXPopup();
//        JFXButton btnAbout = new JFXButton("About");
//        JFXButton btnPop = new JFXButton("prop1");
//        btnPop.setPadding(new Insets(10));
//        VBox vBox = new VBox(btnAbout, btnPop);
//        popup.setPopupContent(vBox);

//=====================================  Ip regex validator ===============================================

