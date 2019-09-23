package com.example.tweety.networking

import android.util.Base64
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


fun getBase64(input: String): String {
    return Base64.encodeToString(input.toByteArray(), Base64.NO_WRAP)
}

fun encode(value: String): String {
    var encoded = ""
    try {
        encoded = URLEncoder.encode(value, "UTF-8")
    } catch (e: Exception) {
        e.printStackTrace()
    }

    var sb = ""
    var focus: Char
    var i = 0
    while (i < encoded.length) {
        focus = encoded[i]
        if (focus == '*') {
            sb += "%2A"
        } else if (focus == '+') {
            sb += "%20"
        } else if (focus == '%' && i + 1 < encoded.length
            && encoded[i + 1] == '7' && encoded[i + 2] == 'E'
        ) {
            sb += '~'.toString()
            i += 2
        } else {
            sb += focus
        }
        i++
    }
    return sb
}

fun generateSignature(
    signatureBaseStr: String,
    oAuthConsumerSecret: String,
    oAuthTokenSecret: String?
): String {
    var byteHMAC: ByteArray? = null
    try {
        val mac = Mac.getInstance("HmacSHA1")
        val spec: SecretKeySpec
        spec = if (oAuthTokenSecret == null) {
            val signingKey = encode(oAuthConsumerSecret) + '&'
            SecretKeySpec(signingKey.toByteArray(), "HmacSHA1")
        } else {
            val signingKey = encode(oAuthConsumerSecret) + '&'.toString() + encode(oAuthTokenSecret)
            SecretKeySpec(signingKey.toByteArray(), "HmacSHA1")
        }
        mac.init(spec)
        byteHMAC = mac.doFinal(signatureBaseStr.toByteArray())
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return String(java.util.Base64.getMimeEncoder().encode(byteHMAC!!), StandardCharsets.UTF_8)
}

fun generateSignatureBaseStr(
    requestMethod: String,
    baseURL: String,
    headers: HashMap<String, String>
): String {
    var outputString = encode(requestMethod.capitalize() + "&")
    outputString += encode("$baseURL&")
    val sortedHeaders = headers.toSortedMap()
    sortedHeaders.forEach { (key, value) ->
        outputString += encode("$key=$value&")
    }
    return outputString
}