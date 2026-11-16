package com.alphaflix.app.api

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

/**
 * Iron headers generator — faithful Kotlin port of OscarTV.php
 * Derives signing key from cert + pkg through XOR stages + 3x SHA-256,
 * then signs path|timestamp|nonce with HMAC-SHA256.
 */
class IronHeadersGenerator(
    private val certSha256: String,
    private val pkgName: String = "com.drama.mp4"
) {
    private val GUARD_1 = "OscarTVIronGuard"
    private val GUARD_2 = "IronGuard"

    private val fingerprint = certSha256.take(8)

    private fun deriveKey(): ByteArray {
        val raw = "$certSha256|$pkgName"
        val data = raw.toByteArray(Charsets.ISO_8859_1)
        val len = data.size

        // Stage 1: XOR with GUARD_1, index mod 7
        val guard1Bytes = GUARD_1.toByteArray(Charsets.ISO_8859_1)
        val stage1 = ByteArray(len) { i ->
            (data[i].toInt() xor guard1Bytes[i % 7].toInt()).toByte()
        }

        // Stage 2: Reverse
        val stage2 = stage1.reversedArray()

        // Stage 3: XOR with GUARD_2, index mod 9
        val guard2Bytes = GUARD_2.toByteArray(Charsets.ISO_8859_1)
        val stage3 = ByteArray(len) { i ->
            (stage2[i].toInt() xor guard2Bytes[i % 9].toInt()).toByte()
        }

        // Stage 4: 3x SHA-256 (binary digest each pass)
        val md = MessageDigest.getInstance("SHA-256")
        val pass1 = md.digest(stage3)
        val pass2 = md.digest(pass1)
        return md.digest(pass2)
    }

    fun generate(url: String): Map<String, String> {
        val path = extractPath(url)
        val timestamp = (System.currentTimeMillis() / 1000L).toString()
        val nonce = generateNonce()

        val key = deriveKey()
        val payload = "$path|$timestamp|$nonce"
        val sig = hmacSha256(payload, key)

        return mapOf(
            "x-iron-sig" to sig,
            "x-iron-ts" to timestamp,
            "x-iron-nonce" to nonce,
            "x-iron-diag" to "f=$fingerprint p=$fingerprint h=0"
        )
    }

    private fun extractPath(url: String): String {
        return try {
            val withoutScheme = if (url.contains("://")) url.substringAfter("://") else url
            val pathPart = withoutScheme.substringAfter("/", missingDelimiterValue = "")
            val path = "/$pathPart".substringBefore("?")
            if (path.isEmpty()) "/" else path
        } catch (e: Exception) {
            "/"
        }
    }

    private fun generateNonce(): String {
        val bytes = ByteArray(4)
        Random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun hmacSha256(data: String, key: ByteArray): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key, "HmacSHA256"))
        return mac.doFinal(data.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }
}
