@file:Suppress("TooManyFunctions")

package io.github.adven27.concordion.extensions.exam.core

import com.github.jknack.handlebars.internal.text.StringEscapeUtils
import io.github.adven27.concordion.extensions.exam.core.html.Html
import io.github.adven27.concordion.extensions.exam.core.html.codeHighlight
import io.github.adven27.concordion.extensions.exam.core.html.span
import nu.xom.Builder
import org.concordion.api.Element
import org.concordion.api.Evaluator
import java.io.StringReader
import java.util.Random

fun String.toHtml() = parseTemplate(this)
fun parseTemplate(tmpl: String) = Html(Element(Builder().build(StringReader(tmpl)).rootElement).deepClone())

fun String.fileExt() = substring(lastIndexOf('.') + 1).lowercase()

fun String.toMap(): Map<String, String> = unboxIfNeeded(this).split(",").associate {
    val (n, v) = it.split("=")
    Pair(n.trim(), v.trim())
}

fun Map<String, String>.resolveValues(eval: Evaluator) = this.mapValues { eval.resolveNoType(it.value) }

private fun unboxIfNeeded(it: String) = if (it.trim().startsWith("{")) it.substring(1, it.lastIndex) else it

private fun failTemplate(header: String = "", help: String = "", cntId: String) = //language=xml
    """
    <div class="card border-danger alert-warning shadow-lg">
      ${if (header.isNotEmpty()) "<div class='card-header bg-danger text-white'>$header</div>" else ""}
      <div class="card-body mb-1 mt-1">
        <div id='$cntId'> </div>
        ${help(help, cntId)}
      </div>
    </div>
    """

//language=xml
private fun help(help: String, cntId: String) = if (help.isNotEmpty()) {
    """
<p data-bs-toggle="collapse" data-bs-target="#help-$cntId" aria-expanded="false">
    <i class="far fa-caret-square-down"> </i><span> Help</span>
</p>
<div id='help-$cntId' class='collapse'>$help</div>
"""
} else {
    ""
}

fun errorMessage(
    header: String = "",
    message: String,
    help: String = "",
    html: Html = span(),
    type: String
): Pair<String, Html> =
    "error-${Random().nextInt()}".let { id ->
        id to failTemplate(header, help, id).toHtml().apply {
            findBy(id)!!(
                codeHighlight(message, type),
                html
            )
        }
    }

fun Throwable.rootCause(): Throwable {
    var rootCause = this
    while (rootCause.cause != null && rootCause.cause !== rootCause) {
        rootCause = rootCause.cause!!
    }
    return rootCause
}

fun Throwable.rootCauseMessage() = this.rootCause().let { it.message ?: it.toString() }

fun List<String>.sameSizeWith(values: List<Any?>): List<String> = if (values.size != size) {
    fun breakReason(cols: List<String>, vals: List<Any?>) =
        if (cols.size > vals.size) {
            "variable '${cols[vals.size]}' has no value"
        } else {
            "value '${vals[cols.size]}' has no variable"
        }

    fun msg(columns: List<String>, values: List<Any?>) =
        "Zipped " + columns.zip(values) { a, b -> "$a=$b" } + " then breaks because " + breakReason(
            columns.toList(),
            values.toList()
        )
    throw IllegalArgumentException(
        "e:where has the variables and values mismatch\n" +
            "got $size vars: $this\ngot ${values.size} vals: $values:\n${msg(this, values)}"
    )
} else {
    this
}

fun String.escapeHtml(): String = StringEscapeUtils.escapeJava(this)
