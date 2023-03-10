package com.device.rxble.internal.connection;

import com.device.rxble.RxBleConnection;
import com.device.rxble.Timeout;
import com.device.rxble.internal.operations.ConnectOperation;
import java.util.Set;

import javax.inject.Named;

import dagger.BindsInstance;
import dagger.Subcomponent;

@ConnectionScope
@Subcomponent(modules = {ConnectionModule.class})
public interface ConnectionComponent {

    class NamedBooleans {
        public static final String AUTO_CONNECT = "autoConnect";
        public static final String SUPPRESS_OPERATION_CHECKS = "suppressOperationChecks";
        private NamedBooleans() { }
    }

    class NamedInts {
        static final String GATT_WRITE_MTU_OVERHEAD = "GATT_WRITE_MTU_OVERHEAD";
        static final String GATT_MTU_MINIMUM = "GATT_MTU_MINIMUM";
        private NamedInts() { }
    }

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        Builder autoConnect(@Named(NamedBooleans.AUTO_CONNECT) boolean autoConnect);

        @BindsInstance
        Builder suppressOperationChecks(@Named(NamedBooleans.SUPPRESS_OPERATION_CHECKS) boolean suppressOperationChecks);

        @BindsInstance
        Builder operationTimeout(Timeout operationTimeout);

        ConnectionComponent build();
    }

    @ConnectionScope
    ConnectOperation connectOperation();

    @ConnectionScope
    RxBleConnection rxBleConnection();

    @ConnectionScope
    RxBleGattCallback gattCallback();

    @ConnectionScope
    Set<ConnectionSubscriptionWatcher> connectionSubscriptionWatchers();
}
