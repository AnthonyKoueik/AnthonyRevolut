package com.anthony.revolut.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.anthony.revolut.any
import com.anthony.revolut.data.Success
import com.anthony.revolut.data.entity.LatestRatesResponse
import com.anthony.revolut.domain.GetRatesUseCase
import com.anthony.revolut.util.TestSchedulers
import io.reactivex.Single
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.*
import org.mockito.junit.MockitoJUnit
import java.io.IOException


/**
 * Created by Anthony Koueik on 12/6/2019.
 * KOA
 * anthony.koueik@gmail.com
 */
class MainActivityViewModelTest {

    // val rates
    companion object {
        private val rateForUSD = HashMap<String, Double>()
        private val rateForEURO = HashMap<String, Double>()

        val currencyRateResponseForEUR: LatestRatesResponse
        val currencyRateResponseForUSD: LatestRatesResponse

        init {
            rateForUSD.apply {
                put("EUR", 0.86295)
                put("GBP", 0.77424)
            }
            rateForEURO.apply {
                put("USD", 1.1662)
                put("PLN", 4.3228)
            }

            currencyRateResponseForUSD = LatestRatesResponse("", "USD", "12/6/2019", rateForUSD)
            currencyRateResponseForEUR = LatestRatesResponse("", "EUR", "12/6/2019", rateForEURO)


        }
    }

    @Rule
    @JvmField
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    private val useCase: GetRatesUseCase = mock(GetRatesUseCase::class.java)


    var testSchedulers: TestSchedulers = TestSchedulers()
    /**
     * Sets up Dagger components for testing.
     */
    @Rule
    @JvmField
    var mockitoRule = MockitoJUnit.rule()!!

    private lateinit var viewModel: MainActivityViewModel

    @Before
    fun init() {


        //  val repositoryImpl = Mockito.mock(RatesRepositoryImpl::class.java)

        `when`(useCase.getRates("EUR")).thenReturn(Single.just(currencyRateResponseForEUR))
        //`when`(apiService.getRates("USD")).thenReturn(Single.just(currencyRateResponseForUSD))

        viewModel = MainActivityViewModel(useCase, testSchedulers)
    }

 /*   @Test
    fun `given a successful use case then result is correct`() {

        `when`(useCase.getRates(any())).thenReturn(Single.just(currencyRateResponseForUSD))

        viewModel.loadLatestRates()

        assertThat(viewModel.liveData.value, instanceOf(Success::class.java))
        val resource = viewModel.liveData.value as Success
        assertEquals(currencyRateResponseForUSD, resource.data)
    }*/

    @Test
    fun `given an error use case Then result is error`() {

        `when`(useCase.getRates(any())).thenReturn(Single.error(IOException("some message")))

        viewModel.loadLatestRates()

        assertThat(viewModel.liveData.value, instanceOf(com.anthony.revolut.data.Error::class.java))
        val resource = viewModel.liveData.value as Error
        assertEquals("some message", resource.message)
    }

    @Test
    fun `given base currency and correct api When getting latest rates  Then return results`() {
        viewModel.liveData.observeForever { result ->
            when (result) {
                is Success -> {
                    assertEquals(2, result.data.size)
                    assertEquals(
                        " ", result.data[0].currency
                    )
                }
            }
        }
    }
}