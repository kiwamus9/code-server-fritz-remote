@file:Suppress("PackageDirectoryMismatch")

package parts.terminalPane

import afterMountElem
import buttonClass
import dev.fritz2.core.RenderContext
import dev.fritz2.core.autocomplete
import dev.fritz2.core.placeholder
import dev.fritz2.core.type
import external.initTerminal
import inputTextClass
import org.w3c.dom.HTMLElement
import parts.titleBar.titleBar
import pasteButtonClass


fun RenderContext.terminalPane(baseClass: String? = null, id: String? = null) {
    titleBar(
        leftDivContent = {
            div {
                button(buttonClass) {
                    i("bi bi-play-fill") {}
                }
                button(buttonClass) {
                    i("bi bi-trash-fill") {}
                }
            }
        },
        centerDivContent = {},
        rightDivContent = {
            div("flex flex-row flex-wrap\t me-2") {
                div("flex rounded-lg shadow-sm") {
                    button(pasteButtonClass) {
                        i("bi bi-clipboard-fill") {}
                    }
                    input(inputTextClass) {
                        type("text")
                        placeholder("paste文字列1")
                        autocomplete("true")
                    }
                }
                div("flex rounded-lg shadow-sm") {
                    button(pasteButtonClass) {
                        i("bi bi-clipboard-fill") {}
                    }
                    input(inputTextClass) {
                        type("text")
                        placeholder("paste文字列2")
                        autocomplete("true")
                    }
                }
                div("flex rounded-lg shadow-sm") {
                    button(pasteButtonClass) {
                        i("bi bi-clipboard-fill") {}
                    }
                    input(inputTextClass) {
                        type("text")
                        placeholder("paste文字列3")
                        autocomplete("true")
                    }
                }
                div("flex rounded-lg shadow-sm") {
                    button(pasteButtonClass) {
                        i("bi bi-clipboard-fill") {}
                    }
                    input(inputTextClass) {
                        type("text")
                        placeholder("paste文字列4")
                        autocomplete("true")
                    }
                }
            }
        }
    )
//    div("grow-1 shrink-1", id = "terminalParent") {
//    }.afterMountElem { withDom, _ -> initTerminal(withDom.domNode as HTMLElement) }
}

