import io.javablex.BlexAdapter;
import io.javablex.BlexPeripheral;

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
            }
        });
        adapter.setScanTimeout(5000);
        adapter.startScan();
        Thread.sleep(10000);
        adapter.stopScan();
    }
}
