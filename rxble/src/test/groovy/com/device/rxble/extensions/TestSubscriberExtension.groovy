package com.device.rxble.extensions

import android.bluetooth.BluetoothGattService
import com.device.rxble.RxBleDeviceServices
import com.device.rxble.RxBleScanResult
import io.reactivex.observers.BaseTestConsumer
import io.reactivex.observers.TestObserver
import io.reactivex.subscribers.TestSubscriber

class TestSubscriberExtension {

    static boolean assertAnyOnNext(final TestSubscriber subscriber, Closure<Boolean> closure) {
        subscriber.values().any closure
    }

    static boolean assertAnyOnNext(final TestObserver subscriber, Closure<Boolean> closure) {
        subscriber.values().any closure
    }

    static boolean assertOneMatches(final TestObserver subscriber, Closure<Boolean> closure) {
        subscriber.values().count(closure) == 1
    }

    static boolean assertScanRecord(final TestObserver<RxBleScanResult> subscriber, int rssi, String macAddress, byte[] scanRecord) {
        assertAnyOnNext(subscriber, {
            it.rssi == rssi && it.bleDevice.macAddress == macAddress && scanRecord == it.scanRecord
        })
    }

    static boolean assertScanRecordsByMacWithOrder(final TestObserver<RxBleScanResult> subscriber, List<String> expectedDevices) {
        subscriber.assertValueCount(expectedDevices.size())
        expectedDevices == subscriber.values()*.bleDevice.macAddress
    }

    static boolean assertSubscribed(final TestSubscriber subscriber) {
        !subscriber.isUnsubscribed()
    }

    static boolean assertReceivedOnNextNot(final TestObserver subscriber, List expectedList) {
        !subscriber.values().equals(expectedList)
    }

    static boolean assertServices(final TestSubscriber<RxBleDeviceServices> subscriber, List<BluetoothGattService> services) {
        assertAnyOnNext(subscriber, {
            it.bluetoothGattServices == services
        })
    }

    static public <T> void assertValues(final TestObserver<T> subscriber, List<T> values) {
        subscriber.assertValueSequence(values)
    }

    static boolean assertAllBatchesSmaller(final TestSubscriber<byte[]> subscriber, int maxBatchSize) {
        def emittedValues = subscriber.values()
        def size = emittedValues.size()
        for (int i = 0; i < size - 1; i++) {
            if (!(emittedValues.get(i).length <= maxBatchSize)) {
                return false
            }
        }
        return true
    }

    static boolean assertLastBatchesSize(final TestSubscriber<byte[]> subscriber, int lastBatchSize) {
        def onNextEvents = subscriber.values()
        def size = onNextEvents.size()
        return onNextEvents.get(size - 1).length == lastBatchSize
    }

    static boolean assertValuesEquals(final BaseTestConsumer<byte[], ? extends BaseTestConsumer> consumer, byte[]... values) {
        List<List<byte[]>> onNextEvents = consumer.values()
        def size = onNextEvents.size()
        if (size != values.length) {
            throw new IllegalArgumentException(String.format("onNext length (%d) does not match values length (%d)", size, values.length))
        }
        for (int i = 0; i < size; i++) {
            if (!(values[i] == onNextEvents[i])) {
                throw new IllegalArgumentException(
                        String.format("onNext[%d] (%s) != value[%d] (%s)", i, Arrays.toString(values()[i]), i, Arrays.toString(values[i]))
                )
            }
        }
        true
    }
}
