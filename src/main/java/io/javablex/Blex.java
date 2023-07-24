package io.javablex;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class Blex {


    // BlexErrorCode
    public static final int BLEX_SUCCESS = 0;
    public static final int BLEX_FAILURE = 1;


    // BlexAddressType
    public static final int BLEX_ADDRESS_TYPE_PUBLIC = 0;
    public static final int BLEX_ADDRESS_TYPE_RANDOM = 1;
    public static final int BLEX_ADDRESS_TYPE_UNSPECIFIED = 2;


    public static class BlexAdapter {
        Pointer pointer;

        public BlexAdapter(Pointer pointer) {
            this.pointer = pointer;
        }

        Pointer getPointer() {
            return pointer;
        }
    }

    public static class BlexPeripheral {
        Pointer pointer;

        public BlexPeripheral(Pointer pointer) {
            this.pointer = pointer;
        }

        Pointer getPointer() {
            return pointer;
        }
    }

    public static class Userdata {

        Pointer pointer;

        public Userdata() {
        }

        public Userdata(Pointer pointer) {
            this.pointer = pointer;
        }

        Pointer getPointer() {
            return pointer;
        }
    }

    public static class BlexService extends Structure {

        BlexUUID uuid;
        int data_length;
        byte data[]; // size: 27
        int characteristic_count;
        BlexCharacteristic characteristics[]; // BLEX_CHARACTERISTIC_MAX_COUNT
    }

    public static class BlexCharacteristic extends Structure {

        BlexUUID uuid;
        boolean can_read;
        boolean can_write_request;
        boolean can_write_command;
        boolean can_notify;
        boolean can_indicate;
        int descriptor_count;
        BlexDescriptor descriptors[]; // BLEX_DESCRIPTOR_MAX_COUNT
    }

    public static class BlexDescriptor extends Structure {
        BlexUUID uuid;
    }

    public static class BlexManufacturerData extends Structure {
        int manufacturer_id;
        int data_length;
        byte data[]; // 27
    }

    public static class BlexUUID extends Structure {
        short value[]; // SIMPLEBLE_UUID_STR_LEN
    }


    public static interface AdapterScanCallback extends Callback {
        int onScanStart(BlexAdapter adapter, Userdata userdata);
        int onScanStop(BlexAdapter adapter, Userdata userdata);
    }

    public static interface AdapterScanUpdateCallback extends Callback {
        int onDeviceUpdate(BlexAdapter adapter, BlexPeripheral peripheral, Userdata userdata);
        int onDeviceFound(BlexAdapter adapter, BlexPeripheral peripheral, Userdata userdata);
    }

    public static interface NotifyCallback extends Callback {
        int onNotify(BlexUUID service, BlexUUID characteristic, byte[] data, int data_length, Userdata userdata, boolean isIndication);
    }

    public static interface PeripheralConnectionCallback extends Callback {
        int onConnection(BlexPeripheral peripheral, Userdata userdata);
    }

    //////////// implements ////////////

    private BlexProxy.Lib proxy;

    public Blex() {
        proxy = new BlexProxy().getInstance();
    }

    boolean blexAdapterIsBluetoothEnabled() {
        return proxy.blexAdapterIsBluetoothEnabled();
    }

    int blexAdapterGetCount() {
        return proxy.blexAdapterGetCount();
    }

    BlexAdapter blexAdapterGetHandle(int index) {

        return new BlexAdapter(proxy.blexAdapterGetHandle(index));
    }

    void blexAdapterReleaseHandle(BlexAdapter handle) {
        proxy.blexAdapterReleaseHandle(handle.getPointer());
    }

    String blexAdapterIdentifier(BlexAdapter handle) {
        return proxy.blexAdapterIdentifier(handle.getPointer());
    }

    String blexAdapterAddress(BlexAdapter handle) {
        return proxy.blexAdapterAddress(handle.getPointer());
    }

    int blexAdapterScanStart(BlexAdapter handle) {
        return proxy.blexAdapterScanStart(handle.getPointer());
    }

    int blexAdapterScanStop(BlexAdapter handle) {
        return proxy.blexAdapterScanStop(handle.getPointer());
    }

    boolean blexAdapterScanIsActive(BlexAdapter handle) {
        Pointer pointer = Pointer.createConstant(0);
        proxy.blexAdapterScanIsActive(handle.getPointer(), pointer);
        return pointer.getByte(0) != 0;
    }

    int blexAdapterScanFor(BlexAdapter handle, int timeout_ms) {
        return proxy.blexAdapterScanFor(handle.getPointer(), timeout_ms);
    }

    int blexAdapterScanGetResultsCount(BlexAdapter handle) {
        return proxy.blexAdapterScanGetResultsCount(handle.getPointer());
    }

    BlexPeripheral blexAdapterScanGetResultsHandle(BlexAdapter handle, int index) {
        return new BlexPeripheral(proxy.blexAdapterScanGetResultsHandle(handle.getPointer(), index));
    }

    int blexAdapterGetPairedPeripheralsCount(BlexAdapter handle) {
        return proxy.blexAdapterGetPairedPeripheralsCount(handle.getPointer());
    }

    BlexPeripheral blexAdapterGetPairedPeripheralsHandle(BlexAdapter handle, int index) {
        return new BlexPeripheral(proxy.blexAdapterGetPairedPeripheralsHandle(handle.getPointer(), index));
    }

    int blexAdapterSetCallbackOnScanStart(BlexAdapter handle, AdapterScanCallback callback, Userdata userdata) {
        return proxy.blexAdapterSetCallbackOnScanStart(handle.getPointer(), new BlexProxy.AdapterScanCallback() {
            @Override
            public int invoke(Pointer adapter, Pointer userdata) {
                return callback.onScanStart(new BlexAdapter(adapter), new Userdata(userdata));
            }
        }, userdata.getPointer());
    }

    int blexAdapterSetCallbackOnScanStop(BlexAdapter handle, AdapterScanCallback callback, Userdata userdata) {
        return proxy.blexAdapterSetCallbackOnScanStop(handle.getPointer(), new BlexProxy.AdapterScanCallback() {
            @Override
            public int invoke(Pointer adapter, Pointer userdata) {
                return callback.onScanStop(new BlexAdapter(adapter), new Userdata(userdata));
            }
        }, userdata.getPointer());
    }

    int blexAdapterSetCallbackOnScanUpdated(BlexAdapter handle, AdapterScanUpdateCallback callback, Userdata userdata) {
        return proxy.blexAdapterSetCallbackOnScanUpdated(handle.getPointer(), new BlexProxy.AdapterScanUpdateCallback() {
            @Override
            public int invoke(Pointer adapter, Pointer peripheral, Pointer userdata) {
                return callback.onDeviceUpdate(new BlexAdapter(adapter), new BlexPeripheral(peripheral), new Userdata(userdata));
            }
        }, userdata.getPointer());
    }

    int blexAdapterSetCallbackOnScanFound(BlexAdapter handle, AdapterScanUpdateCallback callback, Userdata userdata) {
        return proxy.blexAdapterSetCallbackOnScanFound(handle.getPointer(), new BlexProxy.AdapterScanUpdateCallback() {
            @Override
            public int invoke(Pointer adapter, Pointer peripheral, Pointer userdata) {
                return callback.onDeviceFound(new BlexAdapter(adapter), new BlexPeripheral(peripheral), new Userdata(userdata));
            }
        }, userdata.getPointer());
    }

// peripheral.h

    void blexPeripheralReleaseHandle(BlexPeripheral handle) {
        proxy.blexPeripheralReleaseHandle(handle.getPointer());
    }

    String blexPeripheralIdentifier(BlexPeripheral handle) {
        return proxy.blexPeripheralIdentifier(handle.getPointer());
    }

    String blexPeripheralAddress(BlexPeripheral handle) {
        return proxy.blexPeripheralAddress(handle.getPointer());
    }

    int blexPeripheralAddressType(BlexPeripheral handle) {
        return proxy.blexPeripheralAddressType(handle.getPointer());
    }

    int blexPeripheralRssi(BlexPeripheral handle) {
        return proxy.blexPeripheralRssi(handle.getPointer());
    }

    int blexPeripheralPower(BlexPeripheral handle) {
        return proxy.blexPeripheralPower(handle.getPointer());
    }

    int blexPeripheralMtu(BlexPeripheral handle) {
        return proxy.blexPeripheralMtu(handle.getPointer());
    }

    int blexPeripheralConnect(BlexPeripheral handle) {
        return proxy.blexPeripheralConnect(handle.getPointer());
    }

    int blexPeripheralDisconnect(BlexPeripheral handle) {
        return proxy.blexPeripheralDisconnect(handle.getPointer());
    }


    boolean blexPeripheralIsConnected(BlexPeripheral handle) {
        Pointer pointer = Pointer.createConstant(0);
        proxy.blexPeripheralIsConnected(handle.getPointer(), pointer);
        return pointer.getByte(0) != 0;
    }


    boolean blexPeripheralIsConnectable(BlexPeripheral handle) {
        Pointer pointer = Pointer.createConstant(0);
        proxy.blexPeripheralIsConnectable(handle.getPointer(), pointer);
        return pointer.getByte(0) != 0;
    }

    boolean blexPeripheralIsPaired(BlexPeripheral handle) {
        Pointer pointer = Pointer.createConstant(0);
        proxy.blexPeripheralIsPaired(handle.getPointer(), pointer);
        return pointer.getByte(0) != 0;
    }

    int blexPeripheralUnpair(BlexPeripheral handle) {
        return proxy.blexPeripheralUnpair(handle.getPointer());
    }

    int blexPeripheralGetServicesCount(BlexPeripheral handle) {
        return proxy.blexPeripheralGetServicesCount(handle.getPointer());
    }

    int blexPeripheralGetServices(BlexPeripheral handle, int index, BlexService[] services) {
        return proxy.blexPeripheralGetServices(handle.getPointer(), index, services);
    }

    int blexPeripheralManufacturerDataCount(BlexPeripheral handle) {
        return proxy.blexPeripheralManufacturerDataCount(handle.getPointer());
    }

    int blexPeripheralManufacturerDataGet(BlexPeripheral handle, int index, BlexManufacturerData[] manufacturer_data) {
        return proxy.blexPeripheralManufacturerDataGet(handle.getPointer(), index, manufacturer_data);
    }

    int blexPeripheralRead(BlexPeripheral handle, BlexUUID service, BlexUUID characteristic, byte[] data, // uint8_t**
                           Pointer data_length) {
        return proxy.blexPeripheralRead(handle.getPointer(), service, characteristic, data, data_length);
    }

    int blexPeripheralWriteRequest(BlexPeripheral handle, BlexUUID service, BlexUUID characteristic, byte[] data, // const uint8_t* data,
                                   int data_length) {
        return proxy.blexPeripheralWriteRequest(handle.getPointer(), service, characteristic, data, data_length);
    }

    int blexPeripheralWriteCommand(BlexPeripheral handle, BlexUUID service, BlexUUID characteristic, byte[] data, // const uint8_t* data,
                                   int data_length) {
        return proxy.blexPeripheralWriteCommand(handle.getPointer(), service, characteristic, data, data_length);
    }

    int blexPeripheralNotify(BlexPeripheral handle, BlexUUID service, BlexUUID characteristic, NotifyCallback callback, Userdata userdata) {
        return proxy.blexPeripheralNotify(handle.getPointer(), service, characteristic, new BlexProxy.NotifyCallback() {
            @Override
            public int invoke(BlexUUID service, BlexUUID characteristic, byte[] data, int data_length, Pointer userdata) {
                return callback.onNotify(service, characteristic, data, data_length, new Userdata(userdata), false);
            }
        }, userdata.getPointer());
    }

    int blexPeripheralIndicate(BlexPeripheral handle, BlexUUID service, BlexUUID characteristic, NotifyCallback callback, Userdata userdata) {
        return proxy.blexPeripheralIndicate(handle.getPointer(), service, characteristic, new BlexProxy.NotifyCallback() {
            @Override
            public int invoke(BlexUUID service, BlexUUID characteristic, byte[] data, int data_length, Pointer userdata) {
                return callback.onNotify(service, characteristic, data, data_length, new Userdata(userdata), true);
            }
        }, userdata.getPointer());
    }

    int blexPeripheralUnsubscribe(BlexPeripheral handle, BlexUUID service, BlexUUID characteristic) {
        return proxy.blexPeripheralUnsubscribe(handle.getPointer(), service, characteristic);
    }

    int blexPeripheralReadDescriptor(BlexPeripheral handle, BlexUUID service, BlexUUID characteristic, BlexUUID descriptor, byte[][] data, // uint8_t** data,
                                     Pointer data_length) {
        return proxy.blexPeripheralReadDescriptor(handle.getPointer(), service, characteristic, descriptor, data, data_length);
    }

    int blexPeripheralWriteDescriptor(BlexPeripheral handle, BlexUUID service, BlexUUID characteristic, BlexUUID descriptor, byte[] data, // const uint8_t* data,
                                      int data_length) {
        return proxy.blexPeripheralWriteDescriptor(handle.getPointer(), service, characteristic, descriptor, data, data_length);
    }

    int blexPeripheralSetCallbackOnConnected(BlexPeripheral handle, PeripheralConnectionCallback callback, Userdata userdata) {
        return proxy.blexPeripheralSetCallbackOnConnected(handle.getPointer(), new BlexProxy.PeripheralConnectionCallback() {
            @Override
            public int invoke(Pointer peripheral, Pointer userdata) {
                return callback.onConnection(new BlexPeripheral(peripheral), new Userdata(userdata));
            }
        }, userdata.getPointer());
    }

    int blexPeripheralSetCallbackOnDisconnected(BlexPeripheral handle, PeripheralConnectionCallback callback, Userdata userdata) {
        return proxy.blexPeripheralSetCallbackOnDisconnected(handle.getPointer(), new BlexProxy.PeripheralConnectionCallback() {
            @Override
            public int invoke(Pointer peripheral, Pointer userdata) {
                return callback.onConnection(new BlexPeripheral(peripheral), new Userdata(userdata));
            }
        }, userdata.getPointer());
    }
}
