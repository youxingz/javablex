package io.javablex;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import io.javablex.nativex.BlexProxy;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class BlexPeripheral {
    private Pointer pointer;
    private static BlexProxy.Lib proxy = BlexProxy.getInstance();

    private BlexProxy.NotifyCallback notifyCallback;
    private BlexProxy.NotifyCallback indicateCallback;

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

    public BlexService getServices(BlexUUID uuid) {
        int count = getServicesCount();
        for (int i = 0; i < count; i++) {
            BlexService service = getServices(i);
            if (service.getUuid().equals(uuid)) {
                return service;
            }
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
        if (0 == proxy.blexPeripheralRead(pointer, service.value, characteristic.value, length_, data_)) {
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
                                long data_length) {
        return 0 == proxy.blexPeripheralWriteRequest(pointer, service.value, characteristic.value, data_length, data);
    }

    public boolean writeCommand(BlexUUID service, BlexUUID characteristic, byte[] data, // const uint8_t* data,
                                long data_length) {
        return 0 == proxy.blexPeripheralWriteCommand(pointer, service.value, characteristic.value, data_length, data);
    }

    public boolean notify(BlexUUID service, BlexUUID characteristic, NotifyCallback callback) {
        notifyCallback = new BlexProxy.NotifyCallback() {
            @Override
            public int invoke(String service_, String characteristic_, Pointer dataPointer, long data_length) {
                byte[] data = new byte[(int) data_length];
                for (int i = 0; i < data.length; i++)
                    data[i] = dataPointer.getByte(i);
                callback.onNotify(service, characteristic, data, false);
                return 0;
            }
        };
        return 0 == proxy.blexPeripheralNotify(pointer, service.value, characteristic.value, notifyCallback);
    }

    public boolean indicate(BlexUUID service, BlexUUID characteristic, NotifyCallback callback) {
        indicateCallback = new BlexProxy.NotifyCallback() {
            @Override
            public int invoke(String service_, String characteristic_, Pointer dataPointer, long data_length) {
                byte[] data = new byte[(int) data_length];
                for (int i = 0; i < data.length; i++)
                    data[i] = dataPointer.getByte(i);
                callback.onNotify(service, characteristic, data, true);
                return 0;
            }
        };
        return 0 == proxy.blexPeripheralIndicate(pointer, service.value, characteristic.value, indicateCallback);
    }

    public boolean unsubscribe(BlexUUID service, BlexUUID characteristic) {
        return 0 == proxy.blexPeripheralUnsubscribe(pointer, service.value, characteristic.value);
    }

    public BlexDescriptor readDescriptor(BlexUUID service, BlexUUID characteristic, BlexUUID descriptor) {
        Pointer data_ = new Memory(512);
        Pointer length_ = new Memory(4); // 4bytes = 32bits
        if (0 == proxy.blexPeripheralReadDescriptor(pointer, service.value, characteristic.value, descriptor.value, length_, data_)) {
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
        return 0 == proxy.blexPeripheralWriteDescriptor(pointer, service.value, characteristic.value, descriptorUUID.value, descriptor.data.length, descriptor.data);
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
        void onNotify(BlexUUID service, BlexUUID characteristic, byte[] data, boolean isIndication);
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
            this.dataLength = service.data_length.intValue();
            this.data = new byte[(int) this.dataLength];
            System.arraycopy(service.data, 0, this.data, 0, this.dataLength);
            this.characteristicCount = service.characteristic_count.intValue();
            this.characteristics = new BlexCharacteristic[this.characteristicCount];
            for (int i = 0; i < this.characteristicCount; i++) {
                this.characteristics[i] = new BlexCharacteristic(service.characteristics[i]);
            }
        }

        public BlexUUID getUuid() {
            return uuid;
        }

        public int getDataLength() {
            return (int) dataLength;
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
            this.canRead = characteristic.can_read.intValue() == 1;
            this.canWriteRequest = characteristic.can_write_request.intValue() == 1;
            this.canWriteCommand = characteristic.can_write_command.intValue() == 1;
            this.canNotify = characteristic.can_notify.intValue() == 1;
            this.canIndicate = characteristic.can_indicate.intValue() == 1;
            this.descriptorCount = characteristic.descriptor_count.intValue();
            this.descriptors = new BlexDescriptor[this.descriptorCount];
            for (int i = 0; i < this.descriptorCount; i++) {
                this.descriptors[i] = new BlexDescriptor(characteristic.descriptors[i]);
            }
            // depr
            // descriptor_count 值不对，jna 映射 structure 问题，改为遍历验证
//            List<BlexDescriptor> descriptorList = new ArrayList<>();
//            for (BlexProxy.BlexDescriptor descriptor : characteristic.descriptors) {
//                boolean isValid = false;
//                for (byte v : descriptor.uuid.value) {
//                    if (v != 0) {
//                        isValid = true;
//                    }
//                }
//                if (isValid) {
//                    descriptorList.add(new BlexDescriptor(descriptor));
//                } else {
//                    break; // 一般如果遇到 false 就表示此后都无有效的 desc 了
//                }
//            }
//            this.descriptorCount = descriptorList.size();
//            this.descriptors = descriptorList.toArray(new BlexDescriptor[this.descriptorCount]);
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
            this.dataLength = manufacturerData.data_length.intValue();
            this.data = new byte[this.dataLength];
            System.arraycopy(manufacturerData.data, 0, this.data, 0, this.dataLength);
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
        String value; // len: 36
        private BlexProxy.BlexUUID struct;

        private BlexUUID(BlexProxy.BlexUUID uuid) {
            this.struct = uuid;
//            this.value = struct.value.substring(0, 36);
            this.value = new String(struct.value, 0, 36);
        }

        public BlexUUID(String uuid) {
            this.value = uuid.toLowerCase();
            struct = new BlexProxy.BlexUUID();
//            struct.value = this.value + '\0';
            System.arraycopy(this.value.getBytes(StandardCharsets.US_ASCII), 0, struct.value, 0, 36);
        }

        @Override
        public String toString() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BlexUUID blexUUID = (BlexUUID) o;
            return Objects.equals(value, blexUUID.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}
