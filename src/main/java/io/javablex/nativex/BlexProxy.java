package io.javablex.nativex;

import com.sun.jna.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class BlexProxy {
    private static final Lib instance;

    static {
        try {
            String file = "/lib/libblex";
            if (Platform.isWindows()) {
                file += ".dll";
            } else if (Platform.isMac()) {
                if (Platform.isIntel()) {
                    file += "_x86_64.dylib"; // Intel
                } else {
                    file += "_arm64.dylib"; // M1
                }
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
        private static final List<String> FIELDS = Arrays.asList("uuid", "data_length", "data", "characteristic_count", "characteristics");

        public BlexUUID uuid;
        public int data_length;
        public byte[] data = new byte[27]; // size: 27
        public int characteristic_count;
        public BlexCharacteristic[] characteristics = new BlexCharacteristic[16]; // BLEX_CHARACTERISTIC_MAX_COUNT

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public static class BlexCharacteristic extends Structure {
        private static final List<String> FIELDS = Arrays.asList("uuid", "can_read", "can_write_request", "can_write_command", "can_notify", "can_indicate", "descriptor_count", "descriptors");

        public BlexUUID uuid;
        public boolean can_read;
        public boolean can_write_request;
        public boolean can_write_command;
        public boolean can_notify;
        public boolean can_indicate;
        public int descriptor_count;
        public BlexDescriptor descriptors[] = new BlexDescriptor[16]; // BLEX_DESCRIPTOR_MAX_COUNT

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public static class BlexDescriptor extends Structure {
        private static final List<String> FIELDS = Arrays.asList("uuid");
        public BlexUUID uuid;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public static class BlexManufacturerData extends Structure {
        private static final List<String> FIELDS = Arrays.asList("data", "data_length", "manufacturer_id");
        public int manufacturer_id;
        public int data_length;
        public byte data[] = new byte[27]; // 27

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public static class BlexUUID extends Structure {
        private static final List<String> FIELDS = Arrays.asList("value");
        public byte[] value = new byte[37]; // SIMPLEBLE_UUID_STR_LEN

        public BlexUUID() {
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
}
