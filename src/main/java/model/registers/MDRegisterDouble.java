package model.registers;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;

public class MDRegisterDouble extends RecursiveTreeObject<MDRegisterDouble> {
    public StringProperty tag;
    public IntegerProperty address;
    public DoubleProperty value;


    public MDRegisterDouble(String tag, Integer address, Double value) {
        this.tag = new SimpleStringProperty(tag);
        this.address = new SimpleIntegerProperty(address);
        this.value = new SimpleDoubleProperty(value);
    }

    public Property<Number> get() {
        return value;
    }
}
