package net.sgztech.timeboat.managerUtlis

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import androidx.core.util.Pair
import com.device.rxble.RxBleDevice
import net.sgztech.timeboat.TimeBoatApplication
import com.device.rxble.RxBleConnection
import com.device.rxble.RxBleDeviceServices
import com.device.rxble.NotificationSetupMode
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import java.util.*

class BleConnectManager {
    lateinit var mac:String
    private fun connectMulNotifyChar() {
        val bleDevice: RxBleDevice = TimeBoatApplication.rxBleClient.getBleDevice(mac)
        val disposable = bleDevice.establishConnection(false)
            .flatMap { connection: RxBleConnection ->
                connection.discoverServices()
                    .map { obj: RxBleDeviceServices -> obj.bluetoothGattServices } // get them
                    .flatMapObservable { source: List<BluetoothGattService> ->
                        Observable.fromIterable(
                            source
                        )
                    } // map to individual services
                    .map { obj: BluetoothGattService -> obj.characteristics } // for each service take all characteristics
                    .flatMap { source: List<BluetoothGattCharacteristic> ->
                        Observable.fromIterable(
                            source
                        )
                    } // map to individual characteristic)
                    .filter { characteristic: BluetoothGattCharacteristic -> characteristic.properties and (BluetoothGattCharacteristic.PROPERTY_NOTIFY or BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0 } // consider only characteristics that have indicate or notify property
                    .flatMap { characteristic: BluetoothGattCharacteristic ->
                        val notificationSetupper: NotificationSetupper
                        notificationSetupper =
                            if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0) {
                                setupNotifications(characteristic) // if supports notifications
                            } else {
                                setupIndications(characteristic) // else indications
                            }
                        notificationSetupper.setupOn(connection)
                    }
            }
            .subscribe(
                { pair: Pair<BluetoothGattCharacteristic, ByteArray> ->
                    val characteristic = pair.first
                    val value = pair.second
                }
            ) { throwable: Throwable? -> }
    }

    interface NotificationSetupper {
        fun setupOn(connection: RxBleConnection): Observable<Pair<BluetoothGattCharacteristic, ByteArray>>
    }

    companion object {
        fun setupNotifications(characteristic: BluetoothGattCharacteristic): NotificationSetupper {
            return object : NotificationSetupper {

                   override fun setupOn(connection: RxBleConnection): Observable<Pair<BluetoothGattCharacteristic, ByteArray>> {
                    return connection.setupNotification(characteristic, getModeFor(characteristic))
                        .compose<Pair<BluetoothGattCharacteristic, ByteArray>>(
                            pairNotificationsWith(characteristic)
                        )
                }

            }
        }

        fun setupIndications(characteristic: BluetoothGattCharacteristic): NotificationSetupper {
            return object : NotificationSetupper {
                override fun setupOn(connection: RxBleConnection): Observable<Pair<BluetoothGattCharacteristic, ByteArray>> {
                    return connection.setupIndication(characteristic, getModeFor(characteristic))
                        .compose<Pair<BluetoothGattCharacteristic, ByteArray>>(
                            pairNotificationsWith(characteristic)
                        )
                }

            }
        }

        fun getModeFor(characteristic: BluetoothGattCharacteristic): NotificationSetupMode { // a different mode is needed if a characteristic has no Client Characteristic Configuration Descriptor
            val clientCharacteristicConfigurationDescriptorUuid =
                UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
            return if (characteristic.getDescriptor(clientCharacteristicConfigurationDescriptorUuid) != null) NotificationSetupMode.DEFAULT else NotificationSetupMode.COMPAT
        }

        fun pairNotificationsWith(characteristic: BluetoothGattCharacteristic): ObservableTransformer<in Observable<ByteArray>, out Pair<BluetoothGattCharacteristic, ByteArray>> { // to distinguish the source of notification we need to pair the value with the source
            return ObservableTransformer { upstream: Observable<Observable<ByteArray>> ->
                upstream.flatMap { it: Observable<ByteArray> -> it }
                    .map { bytes: ByteArray -> Pair.create(characteristic, bytes) }
            }
        }
    }
}