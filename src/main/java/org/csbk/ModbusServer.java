package org.csbk;
import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.data.ModbusHoldingRegisters;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.msg.request.ReadHoldingRegistersRequest;
import com.intelligt.modbus.jlibmodbus.msg.request.WriteMultipleRegistersRequest;
import com.intelligt.modbus.jlibmodbus.msg.response.ReadHoldingRegistersResponse;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class ModbusServer {

    private final String ipAddress;
    private final int port;
    private final int deviceAddress;
    private final int timeout;
    private final int retries;
    private final int pauseBetweenRequests;
    private final int pollingPeriod;
    private final int initialDelay;
    private final int holdingRegistersCount;
    private final int inputRegistersCount;
    private final int maxAddressGap;

    public ModbusServer(
            String ipAddress, int port, int deviceAddress, int timeout, int retries,
            int pauseBetweenRequests, int pollingPeriod, int initialDelay,
            int holdingRegistersCount, int inputRegistersCount, int maxAddressGap
    ) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.deviceAddress = deviceAddress;
        this.timeout = timeout;
        this.retries = retries;
        this.pauseBetweenRequests = pauseBetweenRequests;
        this.pollingPeriod = pollingPeriod;
        this.initialDelay = initialDelay;
        this.holdingRegistersCount = holdingRegistersCount;
        this.inputRegistersCount = inputRegistersCount;
        this.maxAddressGap = maxAddressGap;
    }

    public float readTPod(ModbusMaster master) throws ModbusIOException, ModbusProtocolException, ModbusNumberException {
        ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest();
        request.setServerAddress(deviceAddress);
        request.setStartAddress(1);  // Address 1
        request.setQuantity(2);  // 2 registers for a float value

        ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) master.processRequest(request);

        ModbusHoldingRegisters registers = response.getHoldingRegisters();
        int register1 = registers.get(0);  // get first register
        int register2 = registers.get(1);  // get second register

        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putShort((short) register1);
        buffer.putShort((short) register2);

        return buffer.getFloat(0);
    }

    public void writeTPod(ModbusMaster master, float value) throws ModbusIOException, ModbusProtocolException, ModbusNumberException {
        WriteMultipleRegistersRequest request = new WriteMultipleRegistersRequest();
        request.setServerAddress(deviceAddress);
        request.setStartAddress(1);  // Address 1

        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putFloat(value);
        buffer.flip();  // Flip the buffer to prepare for reading

        int[] data = new int[2];
        data[0] = buffer.getShort() & 0xFFFF;
        data[1] = buffer.getShort() & 0xFFFF;

        request.setRegisters(data);
        master.processRequest(request);
    }

    public void startServer(Controller controller) {
        Modbus.setLogLevel(Modbus.LogLevel.LEVEL_DEBUG);

        try {
            TcpParameters tcpParameters = new TcpParameters();
            tcpParameters.setHost(InetAddress.getByName(ipAddress));
            tcpParameters.setPort(port);
            tcpParameters.setKeepAlive(true);  // Set to true to keep the connection alive

            ModbusMaster master = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
            master.setResponseTimeout(timeout);
            while (true) {
                float tPodValue = readTPod(master);
                controller.setTextField(String.valueOf(tPodValue));
                System.out.println("tPod value: " + tPodValue);
                Thread.sleep(pollingPeriod * 1000);
            }
        } catch (ModbusProtocolException | ModbusIOException | InterruptedException | UnknownHostException e) {
            e.printStackTrace();
        } catch (ModbusNumberException e) {
            throw new RuntimeException(e);
        }
    }
}
