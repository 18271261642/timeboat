package com.device.rxble.internal.serialization

import com.device.rxble.internal.operations.Operation
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.TrampolineScheduler
import io.reactivex.schedulers.TestScheduler
import java.util.concurrent.atomic.AtomicBoolean
import spock.lang.Specification

class FIFORunnableEntryTest extends Specification {

    def mockOperation = Mock Operation
    def mockQueueReleaseInterface = Mock QueueReleaseInterface
    def mockObservableEmitter = Mock ObservableEmitter
    def mockQueueSemaphore = Mock QueueSemaphore
    def testException = new RuntimeException("test")

    FIFORunnableEntry objectUnderTest

    void setup() {
        objectUnderTest = new FIFORunnableEntry(mockOperation, mockObservableEmitter)
    }

    def "should release the semaphore if ObservableEmitter.isDisposed() returns true at the time of run"() {
        given:
        mockObservableEmitter.isDisposed() >> true

        when:
        objectUnderTest.run(mockQueueSemaphore, TrampolineScheduler.instance())

        then:
        1 * mockQueueSemaphore.release()
    }

    def "should not run operation if ObservableEmitter.isDisposed() returns true at the time of run"() {
        given:
        mockObservableEmitter.isDisposed() >> true

        when:
        objectUnderTest.run(mockQueueSemaphore, TrampolineScheduler.instance())

        then:
        0 * mockOperation.run(_)
    }

    def "should run operation if ObservableEmitter.isDisposed() returns false"() {
        given:
        mockObservableEmitter.isDisposed() >> false

        when:
        objectUnderTest.run(mockQueueSemaphore, TrampolineScheduler.instance())

        then:
        1 * mockOperation.run(mockQueueSemaphore) >> Observable.empty()
    }

    def "should pass operation next to ObservableEmitter"() {
        given:
        mockObservableEmitter.isDisposed() >> false
        mockOperation.run(_) >> Observable.just(1)

        when:
        objectUnderTest.run(mockQueueSemaphore, TrampolineScheduler.instance())

        then:
        1 * mockObservableEmitter.onNext(1)
    }

    def "should pass operation completion to ObservableEmitter"() {
        given:
        mockObservableEmitter.isDisposed() >> false
        mockOperation.run(_) >> Observable.empty()

        when:
        objectUnderTest.run(mockQueueSemaphore, TrampolineScheduler.instance())

        then:
        1 * mockObservableEmitter.onComplete()
    }

    def "should pass operation error to ObservableEmitter"() {
        given:
        mockObservableEmitter.isDisposed() >> false
        mockOperation.run(_) >> Observable.error(testException)

        when:
        objectUnderTest.run(mockQueueSemaphore, TrampolineScheduler.instance())

        then:
        1 * mockObservableEmitter.tryOnError(testException)
    }

    def "should run operation even if ObservableEmitter was unsubscribed at the time of subscription (race condition scenario)"() {

        given:
        AtomicBoolean operationWasRun = new AtomicBoolean(false)
        TestScheduler testScheduler = new TestScheduler()
        mockObservableEmitter.isDisposed() >>> [false, true]
        mockObservableEmitter.setDisposable(_) >> { Disposable disposable -> disposable.dispose() }
        mockOperation.run(_) >> Observable.fromCallable({
            return operationWasRun.getAndSet(true)
        })
        objectUnderTest.run(mockQueueSemaphore, testScheduler)

        when:
        testScheduler.triggerActions()

        then:
        operationWasRun.get()
    }
}
