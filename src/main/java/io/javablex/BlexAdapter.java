package io.javablex;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import io.javablex.nativex.BlexProxy;

public class BlexAdapter {
    private Pointer pointer;
    private static BlexProxy.Lib proxy = BlexProxy.getInstance();

    private BlexProxy.AdapterScanCallback adapterScanCallbackOnStart;
    private BlexProxy.AdapterScanCallback adapterScanCallbackOnStop;
    private BlexProxy.AdapterScanUpdateCallback adapterScanUpdateCallbackOnUpdate;
    private BlexProxy.AdapterScanUpdateCallback adapterScanUpdateCallbackOnFound;

    private BlexAdapter(Pointer adapter) {
        this.pointer = adapter;
    }

    public static BlexAdapter getAdapter(int index) {
        return new BlexAdapter(proxy.blexAdapterGetHandle(index));
    }

    public static boolean isBluetoothEnabled() {
        return proxy.blexAdapterIsBluetoothEnabled();
    }

    public static int getAdapterCount() {
        return proxy.blexAdapterGetCount();
    }

    public void releaseAdapter() {
        proxy.blexAdapterReleaseHandle(pointer);
    }

    public String getIdentifier() {
        return proxy.blexAdapterIdentifier(pointer);
    }

    public String getAddress() {
        return proxy.blexAdapterAddress(pointer);
    }

    public boolean startScan() {
        return 0 == proxy.blexAdapterScanStart(pointer);
    }

    public boolean stopScan() {
        return 0 == proxy.blexAdapterScanStop(pointer);
    }

    public boolean isScanActive() {
        Pointer pointer_ = new Memory(1);
        proxy.blexAdapterScanIsActive(this.pointer, pointer_);
        return pointer_.getByte(0) != 0;
    }

    public boolean setScanTimeout(int timeout_ms) {
        return 0 == proxy.blexAdapterScanFor(pointer, timeout_ms);
    }

    public int getPeripheralsCount() {
        return proxy.blexAdapterScanGetResultsCount(pointer);
    }

    public BlexPeripheral getPeripheral(int index) {
        return new BlexPeripheral(proxy.blexAdapterScanGetResultsHandle(pointer, index));
    }

    public int getPairedPeripheralsCount() {
        return proxy.blexAdapterGetPairedPeripheralsCount(pointer);
    }

    public BlexPeripheral getPairedPeripheral(int index) {
        return new BlexPeripheral(proxy.blexAdapterGetPairedPeripheralsHandle(pointer, index));
    }

    public boolean setCallbackOnScan(AdapterScanCallback callback) {
        adapterScanCallbackOnStart = (adapter, userdata) -> {
            callback.onScanStart(BlexAdapter.this);
            return 0;
        };
        adapterScanCallbackOnStop = (adapter, userdata) -> {
            callback.onScanStop(BlexAdapter.this);
            return 0;
        };
        adapterScanUpdateCallbackOnUpdate = (adapter, peripheral, userdata) -> {
            callback.onDeviceUpdate(BlexAdapter.this, new BlexPeripheral(peripheral));
            return 0;
        };
        adapterScanUpdateCallbackOnFound = (adapter, peripheral, userdata) -> {
            callback.onDeviceFound(BlexAdapter.this, new BlexPeripheral(peripheral));
            return 0;
        };
        boolean success = 0 == proxy.blexAdapterSetCallbackOnScanStart(pointer, adapterScanCallbackOnStart, Pointer.NULL);
        if (success) {
            success = 0 == proxy.blexAdapterSetCallbackOnScanStop(pointer, adapterScanCallbackOnStop, Pointer.NULL);
        }
        if (success) {
            success = 0 == proxy.blexAdapterSetCallbackOnScanUpdated(pointer, adapterScanUpdateCallbackOnUpdate, Pointer.NULL);
        }
        if (success) {
            success = 0 == proxy.blexAdapterSetCallbackOnScanFound(pointer, adapterScanUpdateCallbackOnFound, Pointer.NULL);
        }
        return success;
    }

    public interface AdapterScanCallback {
        void onScanStart(BlexAdapter adapter);

        void onScanStop(BlexAdapter adapter);

        void onDeviceUpdate(BlexAdapter adapter, BlexPeripheral peripheral);

        void onDeviceFound(BlexAdapter adapter, BlexPeripheral peripheral);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlexAdapter) {
            return pointer.equals(((BlexAdapter) obj).pointer);
        }
        return super.equals(obj);
    }
}
