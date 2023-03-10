package com.device.rxble.exceptions;


/**
 * An exception being emitted from an {@link io.reactivex.Observable} returned by the function
 * {@link com.device.rxble.RxBleDevice#establishConnection(boolean)} or other establishConnection() overloads when this kind
 * of observable was already subscribed and {@link com.device.rxble.RxBleConnection} is currently being established or active.
 *
 * <p>
 *     To prevent this exception from being emitted one must either:<br>
 *     * always unsubscribe from the above mentioned Observable before subscribing again<br>
 *     * {@link io.reactivex.Observable#share()} or {@link io.reactivex.Observable#publish()} the above mentioned
 *     Observable so it will be subscribed only once
 * </p>
 */
public class BleAlreadyConnectedException extends BleException {

    public BleAlreadyConnectedException(String macAddress) {
        super("Already connected to device with MAC address " + macAddress);
    }
}
