package com.anthony.revolut.ui

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anthony.revolut.data.DataResource
import com.anthony.revolut.data.entity.Rates
import com.anthony.revolut.data.repository.RatesRepository
import com.anthony.revolut.domain.GetRatesUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


/**
 * Created by Anthony Koueik on 12/5/2019.
 * KOA
 * anthony.koueik@gmail.com
 */
class MainActivityViewModel @Inject constructor(@VisibleForTesting val ratesUseCase: GetRatesUseCase) :
    ViewModel() {

    private var disposable: Disposable? = null

    val liveData = MutableLiveData<DataResource<MutableList<Rates>>>()

    private var _currentCurrency = "EUR"
    private var _currentAmount = 1.00


    fun loadLatestRates() {

        disposable = ratesUseCase.getRates(_currentCurrency)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { liveData.setValue(DataResource.loading(null)) }
            .subscribe(
                { result ->
                    val ratesList = ArrayList<Rates>()
                    ratesList.add(Rates(Currency.getInstance(result.base), _currentAmount))
                    ratesList.addAll(
                        result.rates.map {
                            Rates(Currency.getInstance(it.key), it.value)
                        }
                    )
                    liveData.setValue(DataResource.success(ratesList))
                },
                { throwable ->
                    liveData.setValue(
                        DataResource.error(
                            ratesUseCase.getCustomErrorMessage(throwable), null
                        )
                    )
                }
            )
    }

    fun onNewAmountInput(newAmount: Double) {
        /*userCurrencyAmountDisposable?.dispose()
        updateCurrencyAmountUseCase.run(newCurrencyAmount)
            .subscribe()
            .let { userCurrencyAmountDisposable = it }*/
        _currentAmount = newAmount
        Timber.d("_currentAmount $_currentAmount")
    }

    fun onCurrencyChanged(rates: Rates){
        _currentAmount = rates.rate
        _currentCurrency = rates.currency.currencyCode
        Timber.d("_currentCurrency $_currentCurrency")
        Timber.d("_currentAmount $_currentAmount")
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.let {
            if (!it.isDisposed) it.dispose()
        }
    }


}