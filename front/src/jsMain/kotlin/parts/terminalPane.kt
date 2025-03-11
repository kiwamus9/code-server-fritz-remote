@file:Suppress("PackageDirectoryMismatch")

package parts.terminalPane

import afterMountElem
import buttonClass
import dev.fritz2.core.RenderContext
import dev.fritz2.core.afterMount
import dev.fritz2.core.autocomplete
import dev.fritz2.core.beforeUnmount
import dev.fritz2.core.placeholder
import dev.fritz2.core.type
import external.fitAddon
import external.initTerminal
import external.ResizeObserver
import inputTextClass
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.ResizeQuality
import parts.titleBar.titleBar
import pasteButtonClass
import kotlin.math.log

var terminalDynamic: dynamic? = null
lateinit var observer: ResizeObserver

fun RenderContext.terminalPane(baseClass: String? = null, id: String? = null) {
    div("flex flex-col w-[100%] h-[100%]") {
        titleBar(
            leftDivContent = {
                div {
                    button(buttonClass) {
                        i("bi bi-play-fill") {}
                    }.clicks handledBy { _ ->
                        console.log("fit")
                        fitAddon.fit()
                    }
                    button(buttonClass) {
                        i("bi bi-trash-fill") {}
                    }
                }
            },
            centerDivContent = {},
            rightDivContent = {
                div("flex flex-row flex-wrap me-2") {
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
        div("grow-1 shrink-1 h-[100%] w-[100%] ", id = "terminalParent") {
//            afterMount { withDom, _ ->
//                {
//                    console.log("afterMount")
//                    initTerminal(withDom.domNode as HTMLElement)
//                    observer = ResizeObserver { entries, _ -> console.log("$entries") }
//                    observer.observe(withDom as Node)
//                }
//            }
//            beforeUnmount { withDom, _ ->
//                {
//                    console.log("beforeUnmount")
//                    observer.unobserve(withDom as Node)
//                }
//            }
        }.afterMountElem { withDom, _ ->  initTerminal(withDom.domNode as HTMLElement)}
    }
}

