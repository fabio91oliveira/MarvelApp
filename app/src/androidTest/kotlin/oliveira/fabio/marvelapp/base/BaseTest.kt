package oliveira.fabio.marvelapp.base

import br.com.concretesolutions.requestmatcher.InstrumentedTestRequestMatcherRule
import br.com.concretesolutions.requestmatcher.RequestMatcherRule
import org.junit.Before
import org.junit.Rule
import org.koin.test.KoinTest

abstract class BaseTest : KoinTest {

    @get:Rule
    val server: RequestMatcherRule = InstrumentedTestRequestMatcherRule()

    @Before
    fun baseSetUp() {
        TestUrl.urlTest = server.url("/").toString()
    }
}