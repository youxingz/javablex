package io.javablex;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import io.javablex.nativex.BlexProxy;

import java.nio.ByteBuffer;
import java.util.UUID;

public class BlexPeripheral {
    private Pointer pointer;
    private static BlexProxy.Lib proxy = BlexProxy.getInstance();

    protected BlexPeripheral(Pointer handle) {
        this.pointer = handle;
    }

    public void releasePeripheral() {
        proxy.blexPeripheralReleaseHandle(pointer);
    }

    public String getIdentifier() {
        return proxy.blexPeripheralIdentifier(pointer);
    }

    public String getAddress() {
        return proxy.blexPeripheralAddress(pointer);
    }

    public AddressType getAddressType() {
        switch (proxy.blexPeripheralAddressType(pointer)) {
            case 0:
                return AddressType.PUBLIC;
            case 1:
                return AddressType.RANDOM;
            case 2:
                return AddressType.UNSPECIFIED;
        }
        return AddressType.UNSPECIFIED;
    }

    public int getRssi() {
        return proxy.blexPeripheralRssi(pointer);
    }

    public int getPower() {
        return proxy.blexPeripheralPower(pointer);
    }

    public int getMtu() {
        return proxy.blexPeripheralMtu(pointer);
    }

    public boolean connect() {
        return 0 == proxy.blexPeripheralConnect(pointer);
    }

    public boolean disconnect() {
        return 0 == proxy.blexPeripheralDisconnect(pointer);
    }


    public boolean isConnected() {
        Pointer pointer_ = new Memory(1);
        proxy.blexPeripheralIsConnected(this.pointer, pointer_);
        return pointer_.getByte(0) != 0;
    }


    public boolean isConnectable() {
        Pointer pointer_ = new Memory(1);
        proxy.blexPeripheralIsConnectable(this.pointer, pointer_);
        return pointer_.getByte(0) != 0;
    }

    public boolean isPaired() {
        Pointer pointer_ = new Memory(1);
        proxy.blexPeripheralIsPaired(this.pointer, pointer_);
        return pointer_.getByte(0) != 0;
    }

    public boolean unpair() {
        return 0 == proxy.blexPeripheralUnpair(pointer);
    }

    public int getServicesCount() {
        return proxy.blexPeripheralGetServicesCount(pointer);
    }

    public BlexService getServices(int index) {
        BlexProxy.BlexService service = new BlexProxy.BlexService();
        if (0 == proxy.blexPeripheralGetServices(pointer, index, service)) {
            return new BlexService(service);
        }
        return null;
    }

    public int getManufacturerDataCount() {
        return proxy.blexPeripheralManufacturerDataCount(pointer);
    }

    public BlexManufacturerData getManufacturerData(int index) {
        BlexProxy.BlexManufacturerData data = new BlexProxy.BlexManufacturerData();
        if (0 == proxy.blexPeripheralManufacturerDataGet(pointer, index, data)) {
            return new BlexManufacturerData(data);
        }
        return null;
    }

    public byte[] read(BlexUUID service, BlexUUID characteristic) {
        Pointer data_ = new Memory(512);
        Pointer length_ = new Memory(4); // 4bytes = 32bits
        if (0 == proxy.blexPeripheralRead(pointer, service.struct, characteristic.struct, data_, length_)) {
            int len = length_.getInt(0);
            byte[] data = new byte[len];
            // copy data
            for (int i = 0; i < len; i++) {
                data[i] = data_.getByte(i);
            }
            return data;
        }
        return null;
    }

    public boolean writeRequest(BlexUUID service, BlexUUID characteristic, byte[] data, // const uint8_t* data,
                                int data_length) {
        return 0 == proxy.blexPeripheralWriteRequest(pointer, service.struct, characteristic.struct, data, data_length);
    }

    public boolean writeCommand(BlexUUID service, BlexUUID characteristic, byte[] data, // const uint8_t* data,
                                int data_length) {
        return 0 == proxy.blexPeripheralWriteCommand(pointer, service.struct, characteristic.struct, data, data_length);
    }

    public boolean notify(BlexUUID service, BlexUUID characteristic, NotifyCallback callback) {
        return 0 == proxy.blexPeripheralNotify(pointer, service.struct, characteristic.struct, new BlexProxy.NotifyCallback() {
            @Override
            public int invoke(BlexProxy.BlexUUID service, BlexProxy.BlexUUID characteristic, byte[] data, int data_length, Pointer userdata) {
                callback.onNotify(new BlexUUID(service), new BlexUUID(characteristic), data, data_length, false);
                return 0;
            }
        }, Pointer.NULL);
    }

    public boolean indicate(BlexUUID service, BlexUUID characteristic, NotifyCallback callback) {
        return 0 == proxy.blexPeripheralIndicate(pointer, service.struct, characteristic.struct, new BlexProxy.NotifyCallback() {
            @Override
            public int invoke(BlexProxy.BlexUUID service, BlexProxy.BlexUUID characteristic, byte[] data, int data_length, Pointer userdata) {
                callback.onNotify(new BlexUUID(service), new BlexUUID(characteristic), data, data_length, true);
                return 0;
            }
        }, Pointer.NULL);
    }

    public boolean unsubscribe(BlexUUID service, BlexUUID characteristic) {
        return 0 == proxy.blexPeripheralUnsubscribe(pointer, service.struct, characteristic.struct);
    }

    public BlexDescriptor readDescriptor(BlexUUID service, BlexUUID characteristic, BlexUUID descriptor) {
        Pointer data_ = new Memory(512);
        Pointer length_ = new Memory(4); // 4bytes = 32bits
        if (0 == proxy.blexPeripheralReadDescriptor(pointer, service.struct, characteristic.struct, descriptor.struct, data_, length_)) {
            int len = length_.getInt(0);
            byte[] data = new byte[len];
            // copy data
            for (int i = 0; i < len; i++) {
                data[i] = data_.getByte(i);
            }
            return new BlexDescriptor(descriptor, data);
        }
        return null;
    }

    public boolean writeDescriptor(BlexUUID service, BlexUUID characteristic, BlexUUID descriptorUUID, BlexDescriptor descriptor) {
        return 0 == proxy.blexPeripheralWriteDescriptor(pointer, service.struct, characteristic.struct, descriptorUUID.struct, descriptor.data, descriptor.data.length);
    }

    public boolean setCallbackOnConnection(PeripheralConnectionCallback callback) {
        boolean success = 0 == proxy.blexPeripheralSetCallbackOnConnected(pointer, new BlexProxy.PeripheralConnectionCallback() {
            @Override
            public int invoke(Pointer peripheral, Pointer userdata) {
                callback.onConnection(BlexPeripheral.this, true);
                return 0;
            }
        }, Pointer.NULL);
        if (success) {
            success = 0 == proxy.blexPeripheralSetCallbackOnDisconnected(pointer, new BlexProxy.PeripheralConnectionCallback() {
                @Override
                public int invoke(Pointer peripheral, Pointer userdata) {
                    callback.onConnection(BlexPeripheral.this, false);
                    return 0;
                }
            }, Pointer.NULL);
        }
        return success;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlexPeripheral) {
            return pointer.equals(((BlexPeripheral) obj).pointer);
        }
        return super.equals(obj);
    }

    public interface NotifyCallback {
        void onNotify(BlexUUID service, BlexUUID characteristic, byte[] data, int data_length, boolean isIndication);
    }

    public interface PeripheralConnectionCallback {
        void onConnection(BlexPeripheral peripheral, boolean isConnected);
    }

    public enum AddressType {
        PUBLIC,
        RANDOM,
        UNSPECIFIED;
    }


    public static class BlexService {
        BlexUUID uuid;
        int dataLength;
        byte data[]; // size: 27
        int characteristicCount;
        BlexCharacteristic characteristics[]; // BLEX_CHARACTERISTIC_MAX_COUNT

        private BlexService(BlexProxy.BlexService service) {
            this.uuid = new BlexUUID(service.uuid);
            this.dataLength = service.data_length;
            this.data = service.data;
            this.characteristicCount = service.characteristic_count;
            this.characteristics = new BlexCharacteristic[service.characteristics.length];
            for (int i = 0; i < service.characteristics.length; i++) {
                this.characteristics[i] = new BlexCharacteristic(service.characteristics[i]);
            }
        }

        public BlexUUID getUuid() {
            return uuid;
        }

        public int getDataLength() {
            return dataLength;
        }

        public byte[] getData() {
            return data;
        }

        public int getCharacteristicCount() {
            return characteristicCount;
        }

        public BlexCharacteristic[] getCharacteristics() {
            return characteristics;
        }

    }

    public static class BlexCharacteristic {

        BlexUUID uuid;
        boolean canRead;
        boolean canWriteRequest;
        boolean canWriteCommand;
        boolean canNotify;
        boolean canIndicate;
        int descriptorCount;
        BlexDescriptor descriptors[]; // BLEX_DESCRIPTOR_MAX_COUNT

        private BlexCharacteristic(BlexProxy.BlexCharacteristic characteristic) {
            this.uuid = new BlexUUID(characteristic.uuid);
            this.canRead = characteristic.can_read;
            this.canWriteRequest = characteristic.can_write_request;
            this.canWriteCommand = characteristic.can_write_command;
            this.canNotify = characteristic.can_notify;
            this.canIndicate = characteristic.can_indicate;
            this.descriptorCount = characteristic.descriptor_count;
            this.descriptors = new BlexDescriptor[characteristic.descriptors.length];
            for (int i = 0; i < characteristic.descriptors.length; i++) {
                this.descriptors[i] = new BlexDescriptor(characteristic.descriptors[i]);
            }
        }

        public BlexUUID getUuid() {
            return uuid;
        }

        public boolean isCanRead() {
            return canRead;
        }

        public boolean isCanWriteRequest() {
            return canWriteRequest;
        }

        public boolean isCanWriteCommand() {
            return canWriteCommand;
        }

        public boolean isCanNotify() {
            return canNotify;
        }

        public boolean isCanIndicate() {
            return canIndicate;
        }

        public int getDescriptorCount() {
            return descriptorCount;
        }

        public BlexDescriptor[] getDescriptors() {
            return descriptors;
        }
    }

    public static class BlexDescriptor {
        BlexUUID uuid;
        byte data[];

        private BlexDescriptor(BlexProxy.BlexDescriptor descriptor) {
            this.uuid = new BlexUUID(descriptor.uuid);
        }

        private BlexDescriptor(BlexUUID uuid, byte[] data) {
            this.uuid = uuid;
            this.data = data;
        }

        public BlexUUID getUuid() {
            return uuid;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }
    }

    public static class BlexManufacturerData {
        int manufacturerId;
        int dataLength;
        byte data[]; // 27

        private BlexManufacturerData(BlexProxy.BlexManufacturerData manufacturerData) {
            this.manufacturerId = manufacturerData.manufacturer_id;
            this.dataLength = manufacturerData.data_length;
            this.data = manufacturerData.data;
        }

        public int getManufacturerId() {
            return manufacturerId;
        }

        public int getDataLength() {
            return dataLength;
        }

        public byte[] getData() {
            return data;
        }
    }

    public static class BlexUUID {
        byte value[]; // SIMPLEBLE_UUID_STR_LEN
        private BlexProxy.BlexUUID struct;

        private BlexUUID(BlexProxy.BlexUUID uuid) {
            this.struct = uuid;
            this.value = uuid.value;
        }

        public BlexUUID(UUID uuid) {
            struct = new BlexProxy.BlexUUID();
            struct.value = asBytes(uuid);
        }

        @Override
        public String toString() {
            return getUuid().toString();
        }

        private UUID getUuid() {
            return asUuid(struct.value);
        }

        private static UUID asUuid(byte[] bytes) {
            ByteBuffer bb = ByteBuffer.wrap(bytes);
            long firstLong = bb.getLong();
            long secondLong = bb.getLong();
            return new UUID(firstLong, secondLong);
        }

        private static byte[] asBytes(UUID uuid) {
            ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
            bb.putLong(uuid.getMostSignificantBits());
            bb.putLong(uuid.getLeastSignificantBits());
            return bb.array();
        }
    }
}
