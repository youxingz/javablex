import io.javablex.BlexPeripheral;

import java.util.Arrays;

public class TestDevice {

    public static void main(String[] args) {
        BlexPeripheral peripheral = null;
        peripheral.setCallbackOnConnection(new BlexPeripheral.PeripheralConnectionCallback() {
            @Override
            public void onConnection(BlexPeripheral peripheral, boolean isConnected) {
                System.out.println("Connection? " + isConnected);
            }
        });
        peripheral.connect();
        if (peripheral.getServicesCount() == 0) return;
        BlexPeripheral.BlexService service = peripheral.getServices(0);
        BlexPeripheral.BlexCharacteristic[] characteristics = service.getCharacteristics();
        if (characteristics.length == 0) return;
        peripheral.notify(service.getUuid(), characteristics[0].getUuid(), new BlexPeripheral.NotifyCallback() {
            @Override
            public void onNotify(BlexPeripheral.BlexUUID service, BlexPeripheral.BlexUUID characteristic, byte[] data, boolean isIndication) {
                System.out.println("Data income: ");
                System.out.println(Arrays.toString(data));
            }
        });
        //...
        peripheral.disconnect();
    }
}
