package model.registers;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;

public class MDRegisterInt extends RecursiveTreeObject<MDRegisterInt> {
    public StringProperty tag;
    public IntegerProperty address;
    public IntegerProperty value;


    public MDRegisterInt(String tag, Integer address, Integer value) {
        this.tag = new SimpleStringProperty(tag);
        this.address = new SimpleIntegerProperty(address);
        this.value = new SimpleIntegerProperty(value);
    }

    public Property<Number> get() {
        return value;
    }
}
