/*
 * meatplatform-sandbox
 * Distributed under CC BY-NC-SA
 */
package net.meatplatform.sandbox.http

import org.slf4j.Logger
import org.springframework.stereotype.Component
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * 간단한 HTTP 호출을 추상화한 인터페이스.
 * 내부의 통신 실패 발생시 원래 예외를 감싼 [SandboxHttpClientException] 이 발생합니다.
 *
 * @since 2023-05-07
 */
interface SandboxHttpClient {
    fun get(
        headers: Map<String, String> = emptyMap(),
        url: String,
        urlParams: Map<String, String> = emptyMap()
    ): Response

    fun post(
        headers: Map<String, String> = emptyMap(),
        url: String,
        urlParams: Map<String, String> = emptyMap(),
        body: InputStream? = null
    ): Response

    data class Response(
        val code: Int,
        val body: InputStream?
    )
}

@Component
internal class SandboxHttpClientImpl(
    private val log: Logger
) : SandboxHttpClient {
    override fun get(
        headers: Map<String, String>,
        url: String,
        urlParams: Map<String, String>
    ): SandboxHttpClient.Response {
        val request = prepareBaseHttpRequest("GET", headers, url, urlParams)
            .GET()
            .build()

        return request.toResponse()
    }

    override fun post(
        headers: Map<String, String>,
        url: String,
        urlParams: Map<String, String>,
        body: InputStream?
    ): SandboxHttpClient.Response {
        val request = prepareBaseHttpRequest("POST", headers, url, urlParams).apply {
            if (body != null) {
                POST(HttpRequest.BodyPublishers.ofInputStream { body })
            }
        }.build()

        return request.toResponse()
    }

    private fun prepareBaseHttpRequest(
        method: String,
        headers: Map<String, String>,
        url: String,
        urlParams: Map<String, String>
    ): HttpRequest.Builder {
        val constructedUrl = url.withParams(urlParams)

        log.trace(">>>> HTTP $method $constructedUrl")
        dumpHeaders(headers)

        return HttpRequest.newBuilder()
            .uri(URI(constructedUrl))
            .apply { headers.forEach { (k, v) -> header(k, v) } }
    }

    private fun String.withParams(params: Map<String, String>): String {
        if (params.isEmpty()) {
            return this
        }

        return "$this?${params.entries.joinToString("&") { (k, v) -> "$k=$v" }}"
    }

    private fun dumpHeaders(headers: Map<String, Any>) {
        if (log.isTraceEnabled) {
            val maxLength = headers.keys.maxOf { it.length }
            headers.forEach { (k, v) -> log.trace(">>>> ${k.padEnd(maxLength)} : $v") }
        }
    }

    private fun HttpRequest.toResponse() : SandboxHttpClient.Response {
        try {
            val response = HttpClient.newBuilder()
                .build()
                .send(this, HttpResponse.BodyHandlers.ofInputStream())

            log.trace("<<<< ${response.statusCode()} ${response.request().method()} ${response.request().uri()}")
            dumpHeaders(response.headers().map())

            return SandboxHttpClient.Response(response.statusCode(), response.body())
        } catch (e: IOException) {
            throw SandboxHttpClientException(
                method = method(),
                uri = uri(),
                cause = e
            )
        }
    }
}
