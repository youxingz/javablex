//package io.javablex;
//
//import com.sun.jna.Pointer;
//
//public class Test {
//    public static void main(String[] args) throws InterruptedException {
//        Blex blex = new Blex();
//        Blex.BlexAdapter adapter = blex.blexAdapterGetHandle(0);
//        blex.blexAdapterSetCallbackOnScanStart(adapter, new Blex.AdapterScanCallback() {
//            @Override
//            public int onScanStart(Blex.BlexAdapter adapter, Blex.Userdata userdata) {
//                System.out.println("start");
//                return 0;
//            }
//
//            @Override
//            public int onScanStop(Blex.BlexAdapter adapter, Blex.Userdata userdata) {
//                System.out.println("end");
//                return 0;
//            }
//        }, new Blex.Userdata());
//        blex.blexAdapterSetCallbackOnScanFound(adapter, new Blex.AdapterScanUpdateCallback() {
//            @Override
//            public int onDeviceUpdate(Blex.BlexAdapter adapter, Blex.BlexPeripheral peripheral, Blex.Userdata userdata) {
//                System.out.println("device update one::");
//                return 0;
//            }
//
//            @Override
//            public int onDeviceFound(Blex.BlexAdapter adapter, Blex.BlexPeripheral peripheral, Blex.Userdata userdata) {
//                System.out.println("found one::");
//                String name =  blex.blexPeripheralAddress(peripheral);
//                System.out.println(name);
//                return 0;
//            }
//        }, new Blex.Userdata());
//        blex.blexAdapterScanStart(adapter);
//
//        System.out.println(adapter);
//        Thread.sleep(100000);
//    }
//}
