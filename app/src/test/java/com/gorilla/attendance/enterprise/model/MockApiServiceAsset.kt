package com.gorilla.enterprise.model

import java.io.File
import java.nio.charset.Charset

object MockApiServiceAsset {
    private val BASE_PATH = "${System.getProperty("user.dir")}\\src\\test\\java\\com\\gorilla\\attendance\\enterprise\\mocks\\data"

    // User API corresponding file path
    val AD_AUTH_FAIL_DATA = "$BASE_PATH\\Ad_Auth_Failed_Json_Test"
    val AD_AUTH_SUCCESS_DATA = "$BASE_PATH\\Ad_Auth_Success_Json_Test"

    // Read data through file path
    fun readFile(path: String): String {
        return file2String(File(path))
    }

    //kotlin丰富的I/O API,我们可以通过file.readText（charset）直接获取结果
    private fun file2String(f: File, charset: String = "UTF-8"): String {
        return f.readText(Charset.forName(charset))
    }
}