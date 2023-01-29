package io.github.adven27.concordion.extensions.exam.db.commands

import org.junit.Assert.assertEquals
import org.junit.Test

class ColParserTest {
    private val sut = ColParser()

    @Test
    fun canParseColsDescription() {
        assertEquals(
            mapOf(
                "NOTHING" to (0 to null),
                "MARKED" to (2 to null),
                "HAS_VAL" to (0 to "''"),
                "MARKED_AND_VAL" to (1 to "42")
            ),
            sut.parse("NOTHING, **MARKED, HAS_VAL='', *MARKED_AND_VAL=42")
        )
    }

    @Test
    fun canParseSingleFieldColsDescription() {
        assertEquals(mapOf("some_column" to (0 to null)), sut.parse("some_column"))
    }
}
