@file:Suppress("PackageDirectoryMismatch")

package parts.titleBar

import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import org.w3c.dom.HTMLDivElement
import titleBarClass

lateinit var leftPane: HtmlTag<HTMLDivElement>
lateinit var centerPane: HtmlTag<HTMLDivElement>
lateinit var rightPane: HtmlTag<HTMLDivElement>

fun RenderContext.titleBar(
    baseClass: String? = null, id: String? = null,
    leftDivContent: RenderContext.() -> Unit,
    centerDivContent: RenderContext.() -> Unit,
    rightDivContent: RenderContext.() -> Unit
) {
    //div("w-full h-auto min-h-[2.3em] shrink-0 flex flex-row justify-between bg-blue-300 " + (baseClass ?: titleBarClass), id) {
    div(
        "py-1 w-full h-auto h-min-[33px] shrink-0 flex flex-row justify-between bg-blue-300 " + (baseClass
            ?: titleBarClass), id
    ) {
        leftPane = div("flex items-center") { leftDivContent() }
        centerPane = div("flex items-center") { centerDivContent() }
        rightPane = div("flex items-center") { rightDivContent() }
    }
}