import io.javablex.BlexAdapter;
import io.javablex.BlexPeripheral;

import java.util.Arrays;
import java.util.UUID;

public class Test2 {
    public static void main(String[] args) throws InterruptedException {
        BlexAdapter adapter = BlexAdapter.getAdapter(0);
        int count = adapter.getAdapterCount();
        System.out.println("Adapter count: " + count);
        final BlexPeripheral[] devices = {null};
        adapter.setCallbackOnScan(new BlexAdapter.AdapterScanCallback() {
            @Override
            public void onScanStart(BlexAdapter adapter) {
                System.out.println("scan start.");
            }

            @Override
            public void onScanStop(BlexAdapter adapter) {
                System.out.println("scan stop.");
            }

            @Override
            public void onDeviceUpdate(BlexAdapter adapter, BlexPeripheral peripheral) {
//                System.out.println("scan update:");
//                System.out.println(peripheral.getAddress());
            }

            @Override
            public void onDeviceFound(BlexAdapter adapter, BlexPeripheral peripheral) {
//                System.out.println("scan found:");
//                System.out.println("Address: " + peripheral.getAddress());
                if (peripheral.getIdentifier() == null) return;
                if (!peripheral.getIdentifier().startsWith("Cardioflex")) return;
                devices[0] = peripheral;
                return;
//                peripheral.connect();
//                // wait 2000ms
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//                System.out.println("Try to get services [Cardioflex]");
//                BlexPeripheral.BlexService service = peripheral.getServices(0);
//                System.out.println("Characteristics Count: " + service.getCharacteristicCount());
//                peripheral.setCallbackOnConnection(new BlexPeripheral.PeripheralConnectionCallback() {
//                    @Override
//                    public void onConnection(BlexPeripheral peripheral, boolean isConnected) {
//                        System.out.println(isConnected);
//                    }
//                });
//                peripheral.connect();
//                BlexPeripheral.BlexManufacturerData data = peripheral.getManufacturerData(0);
//                System.out.println(data.getData());
//                if (peripheral.getServicesCount() == 0) return;
//                BlexPeripheral.BlexService service = peripheral.getServices(0);
//                if (service == null) return;
//                System.out.println("==================================");
//                System.out.println("ID: " + peripheral.getIdentifier());
//                String uuid = service.getUuid().toString();
//                System.out.println(uuid);
//                System.out.println(peripheral.getServicesCount());
//                System.out.println(service.getCharacteristicCount());
//                if (service.getCharacteristicCount() == 0) return;
//                String cuuid = service.getCharacteristics()[0].getUuid().toString();
//                System.out.println(cuuid);
//                BlexPeripheral.BlexUUID javaUuid = new BlexPeripheral.BlexUUID(uuid);
//                System.out.println(">=========================>>>>>>>>");
//                System.out.println(uuid.toString());
//                System.out.println(javaUuid.toString());
//                System.out.println("==================================");
//                System.out.println(Arrays.toString(peripheral.getServices(0).getCharacteristics()));
            }
        });

        BlexPeripheral.BlexUUID uuid = new BlexPeripheral.BlexUUID(("0a75d55e-c078-3add-8b78-8fe18613a771"));
        System.out.println(">>");
        System.out.println(uuid);
        adapter.setScanTimeout(5000);
        adapter.startScan();
        Thread.sleep(5000);
        adapter.stopScan();
        // connect
        System.out.println("Connecting...");
        BlexPeripheral device = devices[0];
        device.setCallbackOnConnection(new BlexPeripheral.PeripheralConnectionCallback() {
            @Override
            public void onConnection(BlexPeripheral peripheral, boolean isConnected) {
                System.out.println("Connected? " + isConnected);
                BlexPeripheral.BlexService service = peripheral.getServices(0);
                int count = service.getCharacteristicCount();
                System.out.println("Characteristics count: " + count);
            }
        });
        device.connect();
        Thread.sleep(10000);
    }
}
