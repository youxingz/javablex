package io.javablex;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;

import java.io.IOException;

public class BlexProxy {
    private Lib instance;

    protected BlexProxy() {
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

    public Lib getInstance() {
        return instance;
    }


    public static interface AdapterScanCallback extends Callback {
        int invoke(Pointer adapter, Pointer userdata);
    }

    public static interface AdapterScanUpdateCallback extends Callback {
        int invoke(Pointer adapter, Pointer peripheral, Pointer userdata);
    }

    public static interface NotifyCallback extends Callback {
        int invoke(Blex.BlexUUID service, Blex.BlexUUID characteristic, byte[] data, int data_length, Pointer userdata);
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

        int blexPeripheralGetServices(Pointer handle, int index, Blex.BlexService[] services);

        int blexPeripheralManufacturerDataCount(Pointer handle);

        int blexPeripheralManufacturerDataGet(Pointer handle, int index, Blex.BlexManufacturerData[] manufacturer_data);

        int blexPeripheralRead(Pointer handle, Blex.BlexUUID service, Blex.BlexUUID characteristic, byte[] data, // uint8_t**
                               Pointer data_length);

        int blexPeripheralWriteRequest(Pointer handle, Blex.BlexUUID service, Blex.BlexUUID characteristic, byte[] data, // const uint8_t* data,
                                       int data_length);

        int blexPeripheralWriteCommand(Pointer handle, Blex.BlexUUID service, Blex.BlexUUID characteristic, byte[] data, // const uint8_t* data,
                                       int data_length);

        int blexPeripheralNotify(Pointer handle, Blex.BlexUUID service, Blex.BlexUUID characteristic, NotifyCallback callback, Pointer userdata);

        int blexPeripheralIndicate(Pointer handle, Blex.BlexUUID service, Blex.BlexUUID characteristic, NotifyCallback callback, Pointer userdata);

        int blexPeripheralUnsubscribe(Pointer handle, Blex.BlexUUID service, Blex.BlexUUID characteristic);

        int blexPeripheralReadDescriptor(Pointer handle, Blex.BlexUUID service, Blex.BlexUUID characteristic, Blex.BlexUUID descriptor, byte[][] data, // uint8_t** data,
                                         Pointer data_length);

        int blexPeripheralWriteDescriptor(Pointer handle, Blex.BlexUUID service, Blex.BlexUUID characteristic, Blex.BlexUUID descriptor, byte[] data, // const uint8_t* data,
                                          int data_length);

        int blexPeripheralSetCallbackOnConnected(Pointer handle, PeripheralConnectionCallback callback, Pointer userdata);

        int blexPeripheralSetCallbackOnDisconnected(Pointer handle, PeripheralConnectionCallback callback, Pointer userdata);
    }

}
