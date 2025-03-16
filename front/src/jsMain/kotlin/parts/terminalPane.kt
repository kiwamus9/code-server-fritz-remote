@file:Suppress("PackageDirectoryMismatch")

package parts.terminalPane

import FileEntry
import SelectedFileStore
import buttonClass
import dev.fritz2.core.RenderContext
import dev.fritz2.core.afterMount
import dev.fritz2.core.autocomplete
import dev.fritz2.core.beforeUnmount
import dev.fritz2.core.placeholder
import dev.fritz2.core.type
import external.initTerminal
import external.ResizeObserver
import external.clearTerminal
import external.focusTerminal
import external.resizeTerminal
import external.pasteTerminal
import inputTextClass
import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import parts.titleBar.titleBar
import pasteButtonClass
import kotlin.math.log

const val numberOfPasteArea = 4

var terminalDynamic: dynamic? = null
lateinit var observer: ResizeObserver


fun generateCommand(workSpacePath: String, fileEntry: FileEntry): String {
    val lastComma = fileEntry.name.lastIndexOf(".")
    if (lastComma == -1) return ""

    val name = fileEntry.name.substring(0, lastComma)
    val ext = fileEntry.name.substring(lastComma + 1).lowercase()

    when (ext) {
        "c" -> return ("cd $workSpacePath/${fileEntry.path} && cc *.c -o $name && ./$name")
        "java" -> return "java"
        else -> return ""
    }
}

fun buildAndRun(workSpacePath: String, fileEntry: FileEntry) {
    if (terminalDynamic != null) {
        pasteTerminal(generateCommand(workSpacePath, fileEntry))
    }
}


fun RenderContext.terminalPane(
    baseClass: String? = null,
    id: String? = null,
    userName: String? = null,
    fileStore: SelectedFileStore
) {
    div("flex flex-col w-[100%] h-[100%]") {
        titleBar(
            leftDivContent = {
                div {
                    button(buttonClass) {
                        i("bi bi-play-fill") {}
                    }.clicks handledBy { _ ->
                        fileStore.current?.let {
                            if (terminalDynamic != null) {
                                //console.log(fileStore.workspaces)
                                buildAndRun(fileStore.workspaces!!, it)
                            }
                        }
                    }
                    button(buttonClass) {
                        i("bi bi-trash-fill") {}
                    }.clicks handledBy { _ ->
                        if (terminalDynamic != null) {
                            clearTerminal()
                        }
                    }
                }
            },
            centerDivContent = {},
            rightDivContent = {
                div("flex flex-row flex-wrap me-2") {
                    (1..numberOfPasteArea).forEach {
                        div("flex rounded-lg shadow-sm") {
                            button(pasteButtonClass) {
                                i("bi bi-clipboard-fill") {}
                            }.clicks handledBy { _ ->
                                if (terminalDynamic != null) {
                                    pasteTerminal((document.getElementById("paste${it}") as HTMLInputElement).value)
                                } else focusTerminal()
                            }
                            input(inputTextClass, "paste${it}") {
                                attr("spellcheck", "false") //spellcheck関数自体はあるけれど，HTMLInputElement向けじゃない
                                type("text")
                                placeholder("paste文字列${it}")
                                autocomplete("true")
                            }
                        }
                    }
                }
            }
        )
        div("grow-1 shrink-1 min-h-[100px] w-[100%] bg-black", id = "terminalParent") {
            afterMount { withDom, _ ->
                terminalDynamic = initTerminal(withDom.domNode as HTMLElement)

                observer = ResizeObserver { entries, _ ->
//                    console.log(entries[0].borderBoxSize[0].blockSize.toString())
//                    console.log(entries[0].borderBoxSize[0].inlineSize)
                    resizeTerminal()
                }
                observer.observe(withDom.domNode)

            }
            beforeUnmount { withDom, _ ->
                observer.unobserve(withDom.domNode)
                console.log("beforeUnmount")
            }
        }
    }
}

