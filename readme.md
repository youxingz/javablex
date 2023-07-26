## JavaBlex

A Java Library for Bluetooth Low Energy of `Windows10+` `Linux` `macOS`.

### Gradle/Maven

```gradle
allprojects {
    repositories {
    ...
    maven { url 'https://jitpack.io' }
    }
}
dependencies {
        implementation 'com.github.youxingz:javablex:v0.0.3'
}
```

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
<dependencies>
    <dependency>
        <groupId>com.github.youxingz</groupId>
        <artifactId>javablex</artifactId>
        <version>v0.0.3</version>
    </dependency>
</dependencies>
```

### Example

```java

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
// ...
adapter.stopScan();
```
When you use `BlexAdapter` find some device, i.e., `BlexPeripheral`, you can `connect` `disconnect` `read` `write` `notify` with that device.

```java

BlexPeripheral peripheral = ...;
peripheral.setCallbackOnConnection(new BlexPeripheral.PeripheralConnectionCallback() {
    @Override
    public void onConnection(BlexPeripheral peripheral, boolean isConnected) {
        System.out.println("Connection? " + isConnected);
    }
});
peripheral.connect();
// wait until connected.
if (peripheral.getServicesCount() == 0) return;
BlexPeripheral.BlexService service = peripheral.getServices(0);
BlexPeripheral.BlexCharacteristic[] characteristics = service.getCharacteristics();
if (characteristics.length == 0) return;
// subscribe a notify event:
peripheral.notify(service.getUuid(), characteristics[0].getUuid(), new BlexPeripheral.NotifyCallback() {
    @Override
    public void onNotify(BlexPeripheral.BlexUUID service, BlexPeripheral.BlexUUID characteristic, byte[] data, int data_length, boolean isIndication) {
        System.out.println("Data income: ");
        System.out.println(Arrays.toString(data));
    }
});
//...
peripheral.disconnect();
```