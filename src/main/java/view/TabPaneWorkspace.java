package view;


import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.util.ModbusUtil;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.cells.editors.DoubleTextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.IntegerTextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import model.Slave;
import model.registers.MDRegisterDouble;
import model.registers.MDRegisterFloat;
import model.registers.MDRegisterInt;

import java.util.ArrayList;
import java.util.List;

public class TabPaneWorkspace {

    private Tab currentTab;
    private ArrayList<Slave> slaves;
    private int activeTabNumber = -1;

    // Registers for large types (float, double, long)
    private Register register1 = new SimpleRegister();
    private Register register2 = new SimpleRegister();
    private Register register3 = new SimpleRegister();
    private Register register4 = new SimpleRegister();

    public void createTableView(String slaveIdText, TabPane tabPane, ArrayList<Slave> slaves) {
        this.slaves = slaves;
        createTableInt(slaveIdText, tabPane);
    }

    public void changeTableDouble(TabPane tabPane) {
        JFXTreeTableView<MDRegisterDouble> tableViewDouble =
                (JFXTreeTableView<MDRegisterDouble>) slaves.get(activeTabNumber).getTableViewDouble();
        if (tableViewDouble == null) {
            JFXTreeTableView<MDRegisterDouble> tableView = getNewDoubleTableView();
            currentTab = tabPane.getTabs().get(activeTabNumber);
            slaves.get(activeTabNumber).setTableViewDouble(tableView);
            currentTab.setContent(tableView);
        } else {
            currentTab = tabPane.getTabs().get(activeTabNumber);

            int regIndex;
            // Walk through the table
            for (int rowTableIndex = 0; rowTableIndex < 2_500; rowTableIndex++) {

                // Get double value from the table
                double doubleIndex = tableViewDouble.getTreeItem(rowTableIndex).getValue().value.get();

                byte[] doubleInBytes = ModbusUtil.doubleToRegisters(doubleIndex);
                register1 = new SimpleRegister(doubleInBytes[0], doubleInBytes[1]);
                register2 = new SimpleRegister(doubleInBytes[2], doubleInBytes[3]);
                register3 = new SimpleRegister(doubleInBytes[4], doubleInBytes[5]);
                register4 = new SimpleRegister(doubleInBytes[6], doubleInBytes[7]);

                regIndex = rowTableIndex * 4;
                slaves.get(activeTabNumber).getSpi().getRegister(regIndex).setValue(register1.getValue());
                slaves.get(activeTabNumber).getSpi().getRegister(regIndex + 1).setValue(register2.getValue());
                slaves.get(activeTabNumber).getSpi().getRegister(regIndex + 2).setValue(register3.getValue());
                slaves.get(activeTabNumber).getSpi().getRegister(regIndex + 3).setValue(register4.getValue());
            }

            currentTab.setContent(tableViewDouble);
        }
    }

    public void changeTableFloat(TabPane tabPane) {
        JFXTreeTableView<MDRegisterFloat> tableViewFloat =
                (JFXTreeTableView<MDRegisterFloat>) slaves.get(activeTabNumber).getTableViewDouble();
        if (tableViewFloat == null) {
            JFXTreeTableView<MDRegisterFloat> tableView = getNewFloatTableView();
            currentTab = tabPane.getTabs().get(activeTabNumber);
            slaves.get(activeTabNumber).setTableViewDouble(tableView);
            currentTab.setContent(tableView);
        } else {
            currentTab = tabPane.getTabs().get(activeTabNumber);

            // Walk through the table
            for (int rowTableIndex = 0; rowTableIndex < 5_000; rowTableIndex++) {
                // Get double value from the table
                float floatIndex = tableViewFloat.getTreeItem(rowTableIndex).getValue().value.get();
                byte[] doubleInBytes = ModbusUtil.floatToRegisters(floatIndex);
                register1 = new SimpleRegister(doubleInBytes[0], doubleInBytes[1]);
                register2 = new SimpleRegister(doubleInBytes[2], doubleInBytes[3]);

                // Get spi register
                slaves.get(activeTabNumber).getSpi().getRegister(rowTableIndex).setValue(register1.getValue());
                slaves.get(activeTabNumber).getSpi().getRegister(rowTableIndex + 1).setValue(register2.getValue());
            }

            currentTab.setContent(tableViewFloat);
        }
    }

    public void changeTableInt(TabPane tabPane) {
        JFXTreeTableView<MDRegisterInt> tableViewInt =
                (JFXTreeTableView<MDRegisterInt>) slaves.get(activeTabNumber).getTableViewInt();
        if (tableViewInt == null) {
            JFXTreeTableView<MDRegisterInt> tableView = getNewIntTableView();
            currentTab = tabPane.getTabs().get(activeTabNumber);
            slaves.get(activeTabNumber).setTableViewInt(tableView);
            currentTab.setContent(tableView);
        } else {
            currentTab = tabPane.getTabs().get(activeTabNumber);
            for (int regIndex = 0; regIndex < 10_000; regIndex++) {
                slaves.get(activeTabNumber).getSpi().getRegister(regIndex).setValue(
                        tableViewInt.getTreeItem(regIndex).getValue().value.get()
                );
            }
            currentTab.setContent(tableViewInt);
        }
    }

    private void createTableInt(String slaveIdText, TabPane tabPane) {
        JFXTreeTableView<MDRegisterInt> tableView = getNewIntTableView();
        slaves.get(activeTabNumber).setTableViewInt(tableView);
        //----------------------------------------------------------------
        // Create new Tab with slaveId in TabPane
        Tab tab = new Tab(slaveIdText);
        currentTab = tab;
        tab.setContent(tableView);
        tabPane.getTabs().add(tab);
        tabPane.getStylesheets().add("Main.css");

    }

    private JFXTreeTableView<MDRegisterInt> getNewIntTableView() {
        // Create columns and rows in table view
        JFXTreeTableColumn<MDRegisterInt, String> tagColumn = new JFXTreeTableColumn<>("Tag");
        tagColumn.setPrefWidth(150);
        tagColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<MDRegisterInt, String> param) ->{
            if(tagColumn.validateValue(param)) return param.getValue().getValue().tag;
            else return tagColumn.getComputedValue(param);
        });
        tagColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<MDRegisterInt, String> t)->{
            ((MDRegisterInt) t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue()).tag
                    .set(t.getNewValue());
        });
        tagColumn.setCellFactory((TreeTableColumn<MDRegisterInt, String> param) -> {
            return new GenericEditableTreeTableCell<>(
                    new TextFieldEditorBuilder());
        });

        JFXTreeTableColumn<MDRegisterInt, Number> addressColumn = new JFXTreeTableColumn<>("Address");
        addressColumn.setPrefWidth(150);
        addressColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<MDRegisterInt, Number> param) ->{
            if(addressColumn.validateValue(param)) return param.getValue().getValue().address;
            else return addressColumn.getComputedValue(param);
        });

        JFXTreeTableColumn<MDRegisterInt, Number> valueColumn = new JFXTreeTableColumn<>("Value");
        valueColumn.setPrefWidth(150);
        valueColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<MDRegisterInt, Number> param) ->{
            if(valueColumn.validateValue(param)) return param.getValue().getValue().value;
            else return valueColumn.getComputedValue(param);
        });


        valueColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<MDRegisterInt, Number> t)->{
            ((MDRegisterInt) t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue()).value
                    .set((Integer) t.getNewValue());

            // Send data to slave spi
            int row = t.getTreeTablePosition().getRow();
            slaves.get(activeTabNumber).getSpi().getRegister(row).setValue((Integer) t.getNewValue());

        });
        valueColumn.setCellFactory((TreeTableColumn<MDRegisterInt, Number> param) -> {
            return new GenericEditableTreeTableCell<>(
                    new IntegerTextFieldEditorBuilder());
        });


        // Fill registers
        List<MDRegisterInt> decimalList = new ArrayList<>();
        for (int count = 0; count < 10_000; count++) {
            decimalList.add(new MDRegisterInt("name" , count + 40_000, 0));
        }


        // Fill out the data with registers
        final ObservableList<MDRegisterInt> data =
                FXCollections.observableArrayList(
                        decimalList
                );


        // build tree
        final TreeItem<MDRegisterInt> root = new RecursiveTreeItem<MDRegisterInt>(data, RecursiveTreeObject::getChildren);
        JFXTreeTableView<MDRegisterInt> tableView = new JFXTreeTableView<MDRegisterInt>(root);
        tableView.setShowRoot(false);
        tableView.setEditable(true);

//        tableView.setNodeOrientation();
        tableView.getStylesheets().add("Main.css");
        tableView.getColumns().setAll(tagColumn, addressColumn, valueColumn);
        return tableView;
    }

    public void createTableDouble(String slaveIdText, TabPane tabPane) {

        JFXTreeTableView<MDRegisterDouble> tableView = getNewDoubleTableView();
        //----------------------------------------------------------------
        // Create new Tab with slaveId in TabPane
        Tab tab = new Tab(slaveIdText);
        tab.setContent(tableView);
        tabPane.getTabs().add(tab);
        tabPane.getStylesheets().add("Main.css");

    }

    private JFXTreeTableView<MDRegisterDouble> getNewDoubleTableView() {
        // Create columns and rows in table view
        JFXTreeTableColumn<MDRegisterDouble, String> tagColumn = new JFXTreeTableColumn<>("Tag");
        tagColumn.setPrefWidth(150);
        tagColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<MDRegisterDouble, String> param) ->{
            if(tagColumn.validateValue(param)) return param.getValue().getValue().tag;
            else return tagColumn.getComputedValue(param);
        });
        tagColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<MDRegisterDouble, String> t)->{
            ((MDRegisterDouble) t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue()).tag
                    .set(t.getNewValue());
        });
        tagColumn.setCellFactory((TreeTableColumn<MDRegisterDouble, String> param) -> {
            return new GenericEditableTreeTableCell<>(
                    new TextFieldEditorBuilder());
        });

        JFXTreeTableColumn<MDRegisterDouble, Number> addressColumn = new JFXTreeTableColumn<>("Address");
        addressColumn.setPrefWidth(150);
        addressColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<MDRegisterDouble, Number> param) ->{
            if(addressColumn.validateValue(param)) return param.getValue().getValue().address;
            else return addressColumn.getComputedValue(param);
        });
//        addressColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<MDRegister, Number> t)->{
//            ((MDRegister) t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue()).address
//                    .set((Integer) t.getNewValue());
//        });
//        addressColumn.setCellFactory((TreeTableColumn<MDRegister, Number> param) -> {
//            return new GenericEditableTreeTableCell<>(
//                    new IntegerTextFieldEditorBuilder());
//        });

        JFXTreeTableColumn<MDRegisterDouble, Number> valueColumn = new JFXTreeTableColumn<>("Value");
        valueColumn.setPrefWidth(150);
        valueColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<MDRegisterDouble, Number> param) ->{
            if(valueColumn.validateValue(param)) return param.getValue().getValue().value;
            else return valueColumn.getComputedValue(param);
        });

        valueColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<MDRegisterDouble, Number> t)->{
            ((MDRegisterDouble) t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue()).value
                    .set((Double) t.getNewValue());

            // Get value from the table
            byte[] doubleInBytes = ModbusUtil.doubleToRegisters((Double) t.getNewValue());

            // Convert double value to registers
            register1 = new SimpleRegister(doubleInBytes[0], doubleInBytes[1]);
            register2 = new SimpleRegister(doubleInBytes[2], doubleInBytes[3]);
            register3 = new SimpleRegister(doubleInBytes[4], doubleInBytes[5]);
            register4 = new SimpleRegister(doubleInBytes[6], doubleInBytes[7]);

            // Send data to slave spi
            int row = t.getTreeTablePosition().getRow();

            // 4 registers (8 bytes) in double value
            slaves.get(activeTabNumber).getSpi().getRegister(row).setValue(register1.getValue());
            slaves.get(activeTabNumber).getSpi().getRegister(row + 1).setValue(register2.getValue());
            slaves.get(activeTabNumber).getSpi().getRegister(row + 2).setValue(register3.getValue());
            slaves.get(activeTabNumber).getSpi().getRegister(row + 3).setValue(register4.getValue());
        });

        valueColumn.setCellFactory((TreeTableColumn<MDRegisterDouble, Number> param) -> {
            return new GenericEditableTreeTableCell<>(
                    new DoubleTextFieldEditorBuilder());
        });


        // Fill registers
        List<MDRegisterDouble> doubleList = new ArrayList<>();
        for (int count = 0; count < 10_000; count += 4) {
            doubleList.add(new MDRegisterDouble("name" , count + 40_000, 0.0));
        }

        // Fill out the data with registers
        final ObservableList<MDRegisterDouble> data =
                FXCollections.observableArrayList(
                        doubleList
                );


        // build tree
        final TreeItem<MDRegisterDouble> root =
                new RecursiveTreeItem<MDRegisterDouble>(data, RecursiveTreeObject::getChildren);
        JFXTreeTableView<MDRegisterDouble> tableView = new JFXTreeTableView<MDRegisterDouble>(root);
        tableView.setShowRoot(false);
        tableView.setEditable(true);
        tableView.getStylesheets().add("Main.css");
        tableView.getColumns().setAll(tagColumn, addressColumn, valueColumn);
        return tableView;
    }

    private JFXTreeTableView<MDRegisterFloat> getNewFloatTableView() {
        // Create columns and rows in table view
        JFXTreeTableColumn<MDRegisterFloat, String> tagColumn = new JFXTreeTableColumn<>("Tag");
        tagColumn.setPrefWidth(150);
        tagColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<MDRegisterFloat, String> param) ->{
            if(tagColumn.validateValue(param)) return param.getValue().getValue().tag;
            else return tagColumn.getComputedValue(param);
        });
        tagColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<MDRegisterFloat, String> t)->{
            ((MDRegisterFloat) t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue()).tag
                    .set(t.getNewValue());
        });
        tagColumn.setCellFactory((TreeTableColumn<MDRegisterFloat, String> param) -> {
            return new GenericEditableTreeTableCell<>(
                    new TextFieldEditorBuilder());
        });

        JFXTreeTableColumn<MDRegisterFloat, Number> addressColumn = new JFXTreeTableColumn<>("Address");
        addressColumn.setPrefWidth(150);
        addressColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<MDRegisterFloat, Number> param) ->{
            if(addressColumn.validateValue(param)) return param.getValue().getValue().address;
            else return addressColumn.getComputedValue(param);
        });
//        addressColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<MDRegister, Number> t)->{
//            ((MDRegister) t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue()).address
//                    .set((Integer) t.getNewValue());
//        });
//        addressColumn.setCellFactory((TreeTableColumn<MDRegister, Number> param) -> {
//            return new GenericEditableTreeTableCell<>(
//                    new IntegerTextFieldEditorBuilder());
//        });

        JFXTreeTableColumn<MDRegisterFloat, Number> valueColumn = new JFXTreeTableColumn<>("Value");
        valueColumn.setPrefWidth(150);
        valueColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<MDRegisterFloat, Number> param) ->{
            if(valueColumn.validateValue(param)) return param.getValue().getValue().value;
            else return valueColumn.getComputedValue(param);
        });

        valueColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<MDRegisterFloat, Number> t)->{
            ((MDRegisterFloat) t.getTreeTableView().getTreeItem(t.getTreeTablePosition().getRow()).getValue()).value
                    .set((Float) t.getNewValue());

            // Get value from the table
            byte[] doubleInBytes = ModbusUtil.doubleToRegisters((Double) t.getNewValue());

            // Convert double value to registers
            register1 = new SimpleRegister(doubleInBytes[0], doubleInBytes[1]);
            register2 = new SimpleRegister(doubleInBytes[2], doubleInBytes[3]);
            register3 = new SimpleRegister(doubleInBytes[4], doubleInBytes[5]);
            register4 = new SimpleRegister(doubleInBytes[6], doubleInBytes[7]);

            // Send data to slave spi
            int row = t.getTreeTablePosition().getRow();

            // 4 registers (8 bytes) in double value
            slaves.get(activeTabNumber).getSpi().getRegister(row).setValue(register1.getValue());
            slaves.get(activeTabNumber).getSpi().getRegister(row + 1).setValue(register2.getValue());
            slaves.get(activeTabNumber).getSpi().getRegister(row + 2).setValue(register3.getValue());
            slaves.get(activeTabNumber).getSpi().getRegister(row + 3).setValue(register4.getValue());
        });

        valueColumn.setCellFactory((TreeTableColumn<MDRegisterFloat, Number> param) -> {
            return new GenericEditableTreeTableCell<>(
                    new DoubleTextFieldEditorBuilder());
        });


        // Fill registers
        List<MDRegisterFloat> doubleList = new ArrayList<>();
        for (int count = 0; count < 10_000; count += 2) {
            doubleList.add(new MDRegisterFloat("name" , count + 40_000, 0.0f));
        }

        // Fill out the data with registers
        final ObservableList<MDRegisterFloat> data =
                FXCollections.observableArrayList(
                        doubleList
                );


        // build tree
        final TreeItem<MDRegisterFloat> root =
                new RecursiveTreeItem<MDRegisterFloat>(data, RecursiveTreeObject::getChildren);
        JFXTreeTableView<MDRegisterFloat> tableView = new JFXTreeTableView<MDRegisterFloat>(root);
        tableView.setShowRoot(false);
        tableView.setEditable(true);

//        tableView.setNodeOrientation();
        tableView.getStylesheets().add("Main.css");
        tableView.getColumns().setAll(tagColumn, addressColumn, valueColumn);
        return tableView;
    }

    public void setLastIndexTab(int activeTabNumber) {
        this.activeTabNumber = activeTabNumber;
    }
}
