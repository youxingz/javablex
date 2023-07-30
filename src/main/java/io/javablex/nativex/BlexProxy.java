package io.javablex.nativex;

import com.sun.jna.*;

import java.io.IOException;
import java.nio.ByteBuffer;
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
        int invoke(BlexUUID service, BlexUUID characteristic, byte[] data, long data_length, Pointer userdata);
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

        int blexPeripheralRead(Pointer handle, String service, String characteristic, Pointer data_length, Pointer data);

        int blexPeripheralWriteRequest(Pointer handle, String service, String characteristic, long data_length, byte[] data);

        int blexPeripheralWriteCommand(Pointer handle, String service, String characteristic, long data_length, byte[] data);

        int blexPeripheralNotify(Pointer handle, String service, String characteristic, NotifyCallback callback, Pointer userdata);

        int blexPeripheralIndicate(Pointer handle, String service, String characteristic, NotifyCallback callback, Pointer userdata);

        int blexPeripheralUnsubscribe(Pointer handle, String service, String characteristic);

        int blexPeripheralReadDescriptor(Pointer handle, String service, String characteristic, String descriptor, Pointer data_length, Pointer data);

        int blexPeripheralWriteDescriptor(Pointer handle, String service, String characteristic, String descriptor, long data_length, byte[] data);

        int blexPeripheralSetCallbackOnConnected(Pointer handle, PeripheralConnectionCallback callback, Pointer userdata);

        int blexPeripheralSetCallbackOnDisconnected(Pointer handle, PeripheralConnectionCallback callback, Pointer userdata);
    }


    public static class BlexService extends Structure {
        //        public static class ByReference extends BlexService implements Structure.ByReference { }
        private static final List<String> FIELDS = Arrays.asList("uuid", "data_length", "data", "characteristic_count", "characteristics");

        public BlexUUID uuid;
        public SizeT data_length;
        public byte[] data = new byte[27]; // size: 27
        public SizeT characteristic_count;
        public BlexCharacteristic[] characteristics = new BlexCharacteristic[16]; // BLEX_CHARACTERISTIC_MAX_COUNT

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public static class BlexCharacteristic extends Structure {
        private static final List<String> FIELDS = Arrays.asList("uuid", "can_read", "can_write_request", "can_write_command", "can_notify", "can_indicate", "descriptor_count", "descriptors");

        public BlexUUID uuid;
        public BoolT can_read;
        public BoolT can_write_request;
        public BoolT can_write_command;
        public BoolT can_notify;
        public BoolT can_indicate;
        public SizeT descriptor_count;
        public BlexDescriptor[] descriptors = new BlexDescriptor[16]; // BLEX_DESCRIPTOR_MAX_COUNT

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
        public short manufacturer_id;
        public SizeT data_length;
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

    public static class BlexBuffer extends Structure {
        private static final List<String> FIELDS = Arrays.asList("len", "data");
        public long len;
        public byte[] data = new byte[512];

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public static class SizeT extends IntegerType {
        public SizeT() {
            super(8, true);
        }
    }

    public static class BoolT extends IntegerType {
        public BoolT() {
            super(1, true);
        }
    }
}
