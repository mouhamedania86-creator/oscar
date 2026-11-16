package com.alphaflix.app.api

import okhttp3.Interceptor
import okhttp3.Response

class IronInterceptor : Interceptor {

    private val iron = IronHeadersGenerator(
        certSha256 = ApiConstants.CERT_SHA256,
        pkgName = ApiConstants.PKG_NAME
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val url = original.url.toString()
        val ironHeaders = iron.generate(url)

        val builder = original.newBuilder()
            .header("Authorization", "Bearer ${ApiConstants.BEARER_TOKEN}")
            .header("X-Device-Id", ApiConstants.DEVICE_ID)
            .header("X-Key-Id", ApiConstants.KEY_ID)
            .header("X-App-Ver", ApiConstants.APP_VERSION)
            .header("X-Iron-Sig", ironHeaders["x-iron-sig"] ?: "")
            .header("X-Iron-Ts", ironHeaders["x-iron-ts"] ?: "")
            .header("X-Iron-Nonce", ironHeaders["x-iron-nonce"] ?: "")
            .header("X-Iron-Diag", ironHeaders["x-iron-diag"] ?: "")
            .header("User-Agent", "okhttp/4.12.0")
            // ✅ تم حذف Accept-Encoding و Connection

        return chain.proceed(builder.build())
    }
}
