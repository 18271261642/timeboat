package net.sgztech.timeboat.managerUtlis

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.ObservableSource
import java.util.concurrent.TimeUnit
import kotlin.Throws

class ConnexctTest {
    private fun Test() {
        Observable.interval(0, 1, TimeUnit.SECONDS, Schedulers.trampoline())
            .flatMap { aLong -> Observable.just(if (aLong == 3L) aLong / 0 else aLong * 2) }
            .subscribe({ integer -> println(integer.toString() + "") }) { throwable ->
                println(
                    throwable.toString()
                )
            }
    }
}