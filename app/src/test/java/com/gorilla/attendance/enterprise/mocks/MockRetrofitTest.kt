import com.gorilla.attendance.enterprise.api.ApiService
import com.gorilla.attendance.enterprise.mocks.MockRetrofit
import com.gorilla.enterprise.model.MockApiServiceAsset
import io.reactivex.Observable
import org.junit.Test

class MockRetrofitTest {

    private val testId = "11751"

    /**
     * Make sure our fake participants data are get successfully
     */
    @Test
    fun adAuthFailAssetTest() {
        val participants = MockApiServiceAsset.readFile(MockApiServiceAsset.AD_AUTH_FAIL_DATA).replace("\r", "")
        val expectData = "{\n" +
                "    \"status\": \"error\",\n" +
                "    \"error\": {\n" +
                "        \"code\": \"user_forbidden_2\",\n" +
                "        \"message\": \"Wrong username or password\"\n" +
                "    }\n" +
                "}\n"

        Observable.just(participants)
                .test()
                .assertValue(expectData)
    }

    @Test
    fun adAuthSuccessIdAssetTest() {
        val participant = MockApiServiceAsset.readFile(MockApiServiceAsset.AD_AUTH_SUCCESS_DATA).replace("\r", "")
        val expectData = "{\n" +
                "    \"status\": \"success\"\n" +
                "}\n"

        Observable.just(participant)
                .test()
                .assertValue(expectData)
    }

    /**
     * To make sure that Retrofit could intercept API request, and return local mock data
     */
    @Test
    fun mockRetrofitTest() {
        val retrofit = MockRetrofit()
        val service = retrofit.create(ApiService::class.java)

        /**
         * Corresponding to [adAuthFailAssetTest]
         */
        retrofit.path = MockApiServiceAsset.AD_AUTH_FAIL_DATA
        service.adAuth("hsuan", "hsuan")
                .test()
                .assertValue { it ->
                    val response = it.body()

                    response?.status == "error"
                            && response.error?.message == "Wrong username or password"
                }

        /**
         * Corresponding to [adAuthSuccessIdAssetTest]
         */
        retrofit.path = MockApiServiceAsset.AD_AUTH_SUCCESS_DATA
        service.adAuth("hsuan", "hsuan")
                .test()
                .assertValue { it ->
                    val response = it.body()

                    response?.status == "success"
                }
    }
}