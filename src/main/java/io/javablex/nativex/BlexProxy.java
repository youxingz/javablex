package io.javablex.nativex;

import com.sun.jna.*;

import java.io.IOException;

public class BlexProxy {
    private static Lib instance;

    static {
        try {
            String file = "/lib/libblex";
            if (Platform.isWindows()) {
                file += ".dll";
            } else if (Platform.isMac()) {
                file += ".dylib";
            } else if (Platform.isLinux()) {
                file += ".so";
            }
            instance = NativeUtils.loadLibraryFromJar(file, Lib.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Lib getInstance() {
        return instance;
    }


    public static interface AdapterScanCallback extends Callback {
        int invoke(Pointer adapter, Pointer userdata);
    }

    public static interface AdapterScanUpdateCallback extends Callback {
        int invoke(Pointer adapter, Pointer peripheral, Pointer userdata);
    }

    public static interface NotifyCallback extends Callback {
        int invoke(BlexUUID service, BlexUUID characteristic, byte[] data, int data_length, Pointer userdata);
    }

    public static interface PeripheralConnectionCallback extends Callback {
        int invoke(Pointer peripheral, Pointer userdata);
    }

    public interface Lib extends Library {

        void main();

        // adapter.h

        boolean blexAdapterIsBluetoothEnabled();

        int blexAdapterGetCount();

        Pointer blexAdapterGetHandle(int index);

        void blexAdapterReleaseHandle(Pointer handle);

        String blexAdapterIdentifier(Pointer handle);

        String blexAdapterAddress(Pointer handle);

        int blexAdapterScanStart(Pointer handle);

        int blexAdapterScanStop(Pointer handle);

        int blexAdapterScanIsActive(Pointer handle, Pointer pointer);

        int blexAdapterScanFor(Pointer handle, int timeout_ms);

        int blexAdapterScanGetResultsCount(Pointer handle);

        Pointer blexAdapterScanGetResultsHandle(Pointer handle, int index);

        int blexAdapterGetPairedPeripheralsCount(Pointer handle);

        Pointer blexAdapterGetPairedPeripheralsHandle(Pointer handle, int index);

        int blexAdapterSetCallbackOnScanStart(Pointer handle, AdapterScanCallback callback, Pointer userdata);

        int blexAdapterSetCallbackOnScanStop(Pointer handle, AdapterScanCallback callback, Pointer userdata);

        int blexAdapterSetCallbackOnScanUpdated(Pointer handle, AdapterScanUpdateCallback callback, Pointer userdata);

        int blexAdapterSetCallbackOnScanFound(Pointer handle, AdapterScanUpdateCallback callback, Pointer userdata);

// peripheral.h

        void blexPeripheralReleaseHandle(Pointer handle);

        String blexPeripheralIdentifier(Pointer handle);

        String blexPeripheralAddress(Pointer handle);

        int blexPeripheralAddressType(Pointer handle);

        int blexPeripheralRssi(Pointer handle);

        int blexPeripheralPower(Pointer handle);

        int blexPeripheralMtu(Pointer handle);

        int blexPeripheralConnect(Pointer handle);

        int blexPeripheralDisconnect(Pointer handle);

        int blexPeripheralIsConnected(Pointer handle, Pointer connected);

        int blexPeripheralIsConnectable(Pointer handle, Pointer connectable);

        int blexPeripheralIsPaired(Pointer handle, Pointer paired);

        int blexPeripheralUnpair(Pointer handle);

        int blexPeripheralGetServicesCount(Pointer handle);

        int blexPeripheralGetServices(Pointer handle, int index, BlexService services);

        int blexPeripheralManufacturerDataCount(Pointer handle);

        int blexPeripheralManufacturerDataGet(Pointer handle, int index, BlexManufacturerData manufacturer_data);

        int blexPeripheralRead(Pointer handle, BlexUUID service, BlexUUID characteristic, Pointer data, // uint8_t**
                               Pointer data_length);

        int blexPeripheralWriteRequest(Pointer handle, BlexUUID service, BlexUUID characteristic, byte[] data, // const uint8_t* data,
                                       int data_length);

        int blexPeripheralWriteCommand(Pointer handle, BlexUUID service, BlexUUID characteristic, byte[] data, // const uint8_t* data,
                                       int data_length);

        int blexPeripheralNotify(Pointer handle, BlexUUID service, BlexUUID characteristic, NotifyCallback callback, Pointer userdata);

        int blexPeripheralIndicate(Pointer handle, BlexUUID service, BlexUUID characteristic, NotifyCallback callback, Pointer userdata);

        int blexPeripheralUnsubscribe(Pointer handle, BlexUUID service, BlexUUID characteristic);

        int blexPeripheralReadDescriptor(Pointer handle, BlexUUID service, BlexUUID characteristic, BlexUUID descriptor, Pointer data, // uint8_t** data,
                                         Pointer data_length);

        int blexPeripheralWriteDescriptor(Pointer handle, BlexUUID service, BlexUUID characteristic, BlexUUID descriptor, byte[] data, // const uint8_t* data,
                                          int data_length);

        int blexPeripheralSetCallbackOnConnected(Pointer handle, PeripheralConnectionCallback callback, Pointer userdata);

        int blexPeripheralSetCallbackOnDisconnected(Pointer handle, PeripheralConnectionCallback callback, Pointer userdata);
    }


    public static class BlexService extends Structure {

        public BlexUUID uuid;
        public int data_length;
        public byte[] data; // size: 27
        public int characteristic_count;
        public BlexCharacteristic[] characteristics; // BLEX_CHARACTERISTIC_MAX_COUNT
    }

    public static class BlexCharacteristic extends Structure {

        public BlexUUID uuid;
        public boolean can_read;
        public boolean can_write_request;
        public boolean can_write_command;
        public boolean can_notify;
        public boolean can_indicate;
        public int descriptor_count;
        public BlexDescriptor descriptors[]; // BLEX_DESCRIPTOR_MAX_COUNT
    }

    public static class BlexDescriptor extends Structure {
        public BlexUUID uuid;
    }

    public static class BlexManufacturerData extends Structure {
        public int manufacturer_id;
        public int data_length;
        public byte data[]; // 27
    }

    public static class BlexUUID extends Structure {
        public byte[] value; // SIMPLEBLE_UUID_STR_LEN
    }
}
