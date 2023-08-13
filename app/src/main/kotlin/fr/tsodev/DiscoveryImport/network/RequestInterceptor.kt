package network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import utils.logging.TLogger
import java.io.IOException

private val logger = TLogger
object RequestInterceptor : Interceptor {


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val token = TokenHolder.token
        logger.addToPrefix("[network]")

        var request: Request = chain.request()
        logger.debug("Outgoing request to ${request.url}")

        request = if (token.isNullOrEmpty()) {
            request
                .newBuilder()
                .build()
        } else {
            logger.debug("Request : $request")
            request
                .newBuilder()
                .addHeader(
                    "Authorization",
                    "Bearer $token"
                )
                .build()

        }
        logger.removeToPrefix("[network]")
        return chain.proceed(request)           //response
    }
}