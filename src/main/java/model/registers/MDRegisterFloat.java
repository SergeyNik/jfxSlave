package model.registers;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;

public class MDRegisterFloat extends RecursiveTreeObject<MDRegisterFloat> {
    public StringProperty tag;
    public IntegerProperty address;
    public FloatProperty value;


    public MDRegisterFloat(String tag, Integer address, Float value) {
        this.tag = new SimpleStringProperty(tag);
        this.address = new SimpleIntegerProperty(address);
        this.value = new SimpleFloatProperty(value);
    }

    public Property<Number> get() {
        return value;
    }
}
