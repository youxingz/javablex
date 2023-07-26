import io.javablex.BlexAdapter;
import io.javablex.BlexPeripheral;

import java.util.Arrays;
import java.util.UUID;

public class Test2 {
    public static void main(String[] args) throws InterruptedException {
        BlexAdapter adapter = BlexAdapter.getAdapter(0);
        int count = adapter.getAdapterCount();
        System.out.println("Adapter count: " + count);
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
                System.out.println("scan update:");
                System.out.println(peripheral.getAddress());
            }

            @Override
            public void onDeviceFound(BlexAdapter adapter, BlexPeripheral peripheral) {
                System.out.println("scan found:");
                System.out.println(peripheral.getAddress());
                peripheral.setCallbackOnConnection(new BlexPeripheral.PeripheralConnectionCallback() {
                    @Override
                    public void onConnection(BlexPeripheral peripheral, boolean isConnected) {
                        System.out.println(isConnected);
                    }
                });
//                peripheral.connect();
//                BlexPeripheral.BlexManufacturerData data = peripheral.getManufacturerData(0);
//                System.out.println(data.getData());
                BlexPeripheral.BlexService service = peripheral.getServices(0);
                if (service == null) return;
                String uuid = service.getUuid().toString();
                BlexPeripheral.BlexUUID suuid = new BlexPeripheral.BlexUUID(UUID.fromString(uuid));
                System.out.println("==================================");
                System.out.println(uuid.toString());
                System.out.println(suuid.toString());
                System.out.println("==================================");
//                System.out.println(Arrays.toString(peripheral.getServices(0).getCharacteristics()));
            }
        });

        BlexPeripheral.BlexUUID uuid = new BlexPeripheral.BlexUUID(UUID.fromString("0a75d55e-c078-3add-8b78-8fe18613a771"));
        System.out.println(">>");
        System.out.println(uuid);
        adapter.setScanTimeout(5000);
        adapter.startScan();
        Thread.sleep(10000);
        adapter.stopScan();
    }
}
