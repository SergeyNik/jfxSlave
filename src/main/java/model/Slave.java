package model;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleProcessImage;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.slave.ModbusSlave;
import com.ghgande.j2mod.modbus.slave.ModbusSlaveFactory;
import com.ghgande.j2mod.modbus.util.ModbusUtil;
import javafx.scene.Node;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by home on 7/16/17.
 */

public class Slave {
    private int id;
    private SimpleProcessImage spi = null;
    private ModbusSlave slave = null;
    private Node tableViewInt = null;
    private Node tableViewDouble = null;

    public Slave(int id) {
        this.id = id;
        spi = new SimpleProcessImage(id);
    }

    public String getConnInfo() {
        String result = null;

        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        if (interfaces != null) {
            while (interfaces.hasMoreElements()) {
                NetworkInterface i = interfaces.nextElement();
                if(i != null) {
                    Enumeration<InetAddress> addresses = i.getInetAddresses();
                    System.out.println(i.getDisplayName());
                    while(addresses.hasMoreElements()){
                        InetAddress address = addresses.nextElement();
                        String hostAddr = address.getHostAddress();

                        // internal ip addresses (behind this router)
                        if(hostAddr.indexOf("192.168") == 0 ||
                                hostAddr.indexOf("10.") == 0 ||
                                hostAddr.indexOf("172.16") == 0) {
                            result = "Ip address: " + address.getHostAddress() + " Port: " + slave.getPort();
                        }
                    }
                }
            }
        }
        return result;
    }

    public void fillHoldingRegs() throws ModbusException {
        //2. Prepare a process image
        for (int address = 0; address < 10_000; address++) {
            spi.addRegister(new SimpleRegister(0));
        }
        slave = ModbusSlaveFactory.createTCPSlave(502, 5);
        slave.addProcessImage(id, spi);
    }

    public void setHoldingIntRegs(List<Integer> regs) {
        if (regs != null && spi != null) {
            for (int register = 0; register < 10000; register++) {
                spi.getRegister(register).setValue(regs.get(register));
            }
        }
    }

    public void setHoldingDoubleRegs(List<Double> regs) {
        if (regs != null && spi != null) {
            for (int register = 0; register < 10000; register++) {
                byte[] bytes = ModbusUtil.doubleToRegisters(regs.get(register));
                System.out.println(bytes.length);
                System.out.println(Arrays.toString(bytes));

            }
//                spi.getRegister(register).setValue(regs.get(register));
        }
    }

    public void start() throws ModbusException {
        slave.open();
    }
    public void stop() throws ModbusException {
    }

    public int[] getHoldingRegs(){
        Register[] registerRange = spi.getRegisterRange(0, 9999);
        int[] result = new int[10000];
        for (int reg = 0; reg < 9999; reg++) {
            result[reg] = registerRange[reg].getValue();
        }
        return result;
    }

    public SimpleProcessImage getSpi() {
        return spi;
    }

    public int getId() {
        return id;
    }

    public Node getTableViewInt() {
        return tableViewInt;
    }

    public void setTableViewInt(Node tableViewInt) {
        this.tableViewInt = tableViewInt;
    }

    public Node getTableViewDouble() {
        return tableViewDouble;
    }

    public void setTableViewDouble(Node tableViewDouble) {
        this.tableViewDouble = tableViewDouble;
    }
}
