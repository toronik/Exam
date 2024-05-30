package io.github.adven27.concordion.extensions.exam

import io.github.adven27.concordion.extensions.exam.core.utils.JsonPrettyPrinter
import org.junit.Assert.assertEquals
import org.junit.Test

class JsonPrettyPrinterTest {
    @Test
    fun prettyPrint() {
        val message = JsonPrettyPrinter().prettyPrint("{a:1,b:2,c:[d:3,e:4]}")
        assertEquals(
            message,
            """{
            |  a: 1,
            |  b: 2,
            |  c: [d: 3, e: 4]
            |}
            """.trimMargin()
        )
    }
}
