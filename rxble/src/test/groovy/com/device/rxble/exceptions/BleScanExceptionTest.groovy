package com.device.rxble.exceptions

import spock.lang.Specification

import static com.device.rxble.exceptions.BleScanException.BLUETOOTH_DISABLED

/**
 * Tests BleScanException
 */
class BleScanExceptionTest extends Specification {

    BleScanException objectUnderTest

    def "toString should include message"() {

        when:
        objectUnderTest = new BleScanException(BLUETOOTH_DISABLED)

        then:
        assert objectUnderTest.toString() ==
                "com.device.rxandroidble2.exceptions.BleScanException: Bluetooth disabled (code 1)"
    }
}
