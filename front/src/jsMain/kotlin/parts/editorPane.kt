@file:Suppress("PackageDirectoryMismatch")

package parts.editorPane

import afterMountElem
import buttonClass
import external.createEditorView
import external.createState
import dev.fritz2.core.RenderContext
import dev.fritz2.core.textContent
import org.w3c.dom.HTMLDivElement
import parts.titleBar.titleBar

sealed class ModelState {
    object Empty : ModelState()
    object Loading : ModelState()
    object Loaded : ModelState()
    object Changed : ModelState()
}

fun RenderContext.editorPane(baseClass: String? = null, id: String? = null, userName: String? = null) {

    val state = createState("kiwamu")

    div("h-full w-full flex flex-col " + (baseClass ?: ""), id) {
        titleBar(
            leftDivContent = {
                button((buttonClass)) {
                    i("bi bi-floppy2") {}
                }
            },
            centerDivContent = { span { +"hooeee" } },
            rightDivContent = {}
        )
        // editor
        div("grow shrink bg-yellow-200 overflow-auto ", "editorPane") {
        }.afterMountElem { withDom, _ -> createEditorView(withDom.domNode as HTMLDivElement, state) }
    }
}