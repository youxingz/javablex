import com.sun.jna.Native;
import io.javablex.BlexAdapter;
import io.javablex.BlexPeripheral;

public class Test3 {
    private static BlexPeripheral device;

    public static void main(String[] args) throws InterruptedException {
        BlexAdapter adapter = BlexAdapter.getAdapter(0);
        adapter.setCallbackOnScan(new BlexAdapter.AdapterScanCallback() {
            @Override
            public void onScanStart(BlexAdapter adapter) {

            }

            @Override
            public void onScanStop(BlexAdapter adapter) {

            }

            @Override
            public void onDeviceUpdate(BlexAdapter adapter, BlexPeripheral peripheral) {

            }

            @Override
            public void onDeviceFound(BlexAdapter adapter, BlexPeripheral peripheral) {
                if (peripheral.getIdentifier() == null) return;
                if (!peripheral.getIdentifier().startsWith("Cardioflex")) return;
                device = peripheral;
                System.out.println("Device found!");
            }
        });
        adapter.startScan();
        System.out.println("Waiting for scan...");
        while (true) {
            if (device != null) break;
            Thread.sleep(100);
        }
        System.out.println("Device found!!");

        boolean success = device.setCallbackOnConnection(new BlexPeripheral.PeripheralConnectionCallback() {
            @Override
            public void onConnection(BlexPeripheral peripheral, boolean isConnected) {
//                if (isConnected) {
//                    System.out.println("CMD sending...");
//                    byte[] cmd = "{\"cmd\":3}".getBytes();
//                    BlexPeripheral.BlexService service = peripheral.getServices(0);
//                    BlexPeripheral.BlexCharacteristic characteristic = service.getCharacteristics()[0];
//                    peripheral.writeRequest(service.getUuid(), characteristic.getUuid(), cmd, cmd.length);
//                    System.out.println("CMD sent.");
//                } else {
//                    System.out.println("disconnected.");
//                }
            }
        });
        System.out.println("Callback set: " + success);
        System.out.println("Connectable: " + device.isConnectable());
        System.out.println("Connect: " + device.connect());
//        byte[] cmd = "{\"cmd\":5}".getBytes(StandardCharsets.US_ASCII);
        byte[] cmd = Native.toByteArray("{\"cmd\":3}");
//        byte[] cmd = {0x02};
        BlexPeripheral.BlexService service = device.getServices(0);
        BlexPeripheral.BlexCharacteristic characteristic = service.getCharacteristics()[0];
        System.out.println("CMD sending...");
        device.writeCommand(service.getUuid(), characteristic.getUuid(), cmd, cmd.length);
//        device.writeCommand(new BlexPeripheral.BlexUUID("0000fade-0000-1000-8000-00805f9b34fb"), new BlexPeripheral.BlexUUID("0000fad1-0000-1000-8000-00805f9b34fb"), cmd, cmd.length);
        System.out.println("CMD sent.");
//        Thread.sleep(100000);
    }
}
