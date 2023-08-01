import com.sun.jna.Native;
import io.javablex.BlexAdapter;
import io.javablex.BlexPeripheral;

import java.util.Arrays;
import java.util.Date;

public class Test3 {
    private static BlexPeripheral device;

    public static void main(String[] args) throws InterruptedException {
        BlexAdapter adapter = BlexAdapter.getAdapter(0);
        System.out.println("Scanner Count: " + adapter.getAdapterCount());

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
                if (!peripheral.getAddress().startsWith("58:cf:79:da:b2:7a")) return;
//                if (!peripheral.getIdentifier().startsWith("Cardioflex")) return;
                if (peripheral.getServicesCount() == 0) return;
                BlexPeripheral.BlexService service = peripheral.getServices(0);
                if (!service.getUuid().toString().startsWith("0000fade")) return;
                System.out.println(service.getUuid());
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
        adapter.stopScan();
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
        BlexPeripheral.BlexService service = device.getServices(2);
        BlexPeripheral.BlexCharacteristic characteristic = service.getCharacteristics()[0];
        BlexPeripheral.BlexCharacteristic notifyChar = service.getCharacteristics()[2];
        BlexPeripheral.BlexUUID serviceUUID = new BlexPeripheral.BlexUUID("0000fade-0000-1000-8000-00805f9b34fb");
        BlexPeripheral.BlexUUID notifyUUID = new BlexPeripheral.BlexUUID("0000fad3-0000-1000-8000-00805f9b34fb");
        System.out.println("CMD sending...");
        device.writeCommand(service.getUuid(), characteristic.getUuid(), cmd, cmd.length);
//        device.writeCommand(new BlexPeripheral.BlexUUID("0000fade-0000-1000-8000-00805f9b34fb"), new BlexPeripheral.BlexUUID("0000fad1-0000-1000-8000-00805f9b34fb"), cmd, cmd.length);
        System.out.println("CMD sent.");
//        Thread.sleep(100000);
        boolean notifySuccess = device.notify(serviceUUID, notifyUUID, new BlexPeripheral.NotifyCallback() {
            @Override
            public void onNotify(BlexPeripheral.BlexUUID service, BlexPeripheral.BlexUUID characteristic, byte[] data, boolean isIndication) {
                System.out.println("==========notify=========");
                System.out.println("Length: " + data.length);
                System.out.println(Arrays.toString(data));
            }
        });
        System.out.println("Notify Success: " + notifySuccess);
        System.out.println("Connected? " + device.isConnected());
        while (true) {
            String cmdHeartStr = "{\"cmd\":4,\"secret\":\"xxsecret\",\"ts\":" + System.currentTimeMillis() + "}";
            byte[] cmdHeart = cmdHeartStr.getBytes();
            device.writeCommand(service.getUuid(), characteristic.getUuid(), cmdHeart, cmdHeart.length);
            Thread.sleep(1000);
//            break;
        }
//        final int[] notifyCount = {0};
//        while (true) {
//            Thread.sleep(1000);
////            device.unsubscribe(service.getUuid(), notifyChar.getUuid());
////            System.out.println("Notify End");
//            notifyCount[0]++;
//            device.notify(service.getUuid(), notifyChar.getUuid(), new BlexPeripheral.NotifyCallback() {
//                private int notifyC = notifyCount[0];
//                @Override
//                public void onNotify(BlexPeripheral.BlexUUID service, BlexPeripheral.BlexUUID characteristic, byte[] data, boolean isIndication) {
//                    System.out.println("==========notify=========>>>>>>>>>>>>>>>>>>" + (notifyC));
//                    System.out.println("Length: " + data.length);
//                    System.out.println(System.currentTimeMillis());
//                    System.out.println(Arrays.toString(data));
//                }
//            });
//            System.out.println("Notify Start Again");
//        }
    }
}
