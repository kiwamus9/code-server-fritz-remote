@file:Suppress("PackageDirectoryMismatch")

package parts.terminalPane

import DarkModeStore
import FileEntry
import SelectedFileStore
import buttonClass
import dev.fritz2.core.RenderContext
import dev.fritz2.core.afterMount
import dev.fritz2.core.autocomplete
import dev.fritz2.core.beforeUnmount
import dev.fritz2.core.disabled
import dev.fritz2.core.placeholder
import dev.fritz2.core.type
import enterButtonClass
import external.initTerminal
import external.ResizeObserver
import external.clearTerminal
import external.focusTerminal
import external.resizeTerminal
import external.pasteTerminal
import external.toDarkModeTerminal
import inputTextClass
import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import parts.titleBar.titleBar
import pasteButtonClass

const val numberOfPasteArea = 4

var terminalDynamic: dynamic? = null
var observer: ResizeObserver? = null


fun generateCommand(workSpacePath: String, fileEntry: FileEntry): String {
    val lastComma = fileEntry.name.lastIndexOf(".")
    if (lastComma == -1) return ""

    val name = fileEntry.name.substring(0, lastComma)
    val ext = fileEntry.name.substring(lastComma + 1).lowercase()

    return when (ext) {
        "c" -> ("cd $workSpacePath/${fileEntry.path} && cc *.c -lm -o $name && ./$name \n")
        "java" -> ("cd $workSpacePath/${fileEntry.path} && javac *.java && java $name \n")
        else -> ""
    }
}

fun buildAndRun(workSpacePath: String, fileEntry: FileEntry) {
    if (terminalDynamic != null) {
        pasteTerminal(generateCommand(workSpacePath, fileEntry))
    }
}

@Suppress("unused")
fun RenderContext.terminalPane(
    baseClass: String? = null,
    id: String? = null,
    userName: String? = null,
    fileStore: SelectedFileStore,
    darkStore: DarkModeStore
) {
    // 選択ファイルが変わった時にターミナルをクリアする
    fileStore.data.handledBy { fileEntry ->
        if((fileEntry != null) && (terminalDynamic != null)) {
            clearTerminal()
        }
    }
    //　ダークモード
    darkStore.data.handledBy { isDarkMode ->
        if (terminalDynamic != null) {
            toDarkModeTerminal(terminalDynamic, isDarkMode)
        }
    }

    div("flex flex-col w-[100%] h-[100%]") {
        titleBar(
            leftDivContent = {
                div {
                    button(buttonClass) {
                        disabled(userName == null)
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
                        disabled(userName == null)
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
                        div("flex my-1 rounded-lg shadow-sm") {
                            button(pasteButtonClass) {
                                disabled(userName == null)
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
                            button(enterButtonClass) {
                                disabled(userName == null)
                                i("bi bi-arrow-return-left") {}
                            }.clicks handledBy { _ ->
                                if (terminalDynamic != null) {
                                    pasteTerminal((document.getElementById("paste${it}") as HTMLInputElement).value + "\n")
                                } else focusTerminal()
                            }
                        }
                    }
                }
            }
        )
        div("grow-1 shrink-1 min-h-[100px] w-[100%] bg-white dark:bg-black", id = "terminalParent") {
            if (userName == null) {
                +"未接続"
            } else {
                afterMount { withDom, _ ->
                    terminalDynamic = initTerminal(withDom.domNode as HTMLElement, userName) { reason, id ->
                        console.log(reason)
                        if (reason.startsWith("io server disconnect", ignoreCase = true)) {
                            withDom.domNode.innerHTML = ""
                            withDom.domNode.prepend(
                                document.createTextNode(" 複数のウィンドウでターミナルは開けません．")
                            )
                        }
                    }

                    observer = ResizeObserver { entries, _ ->
                        resizeTerminal()
                    }
                    observer?.observe(withDom.domNode)

                }
            }
            beforeUnmount { withDom, _ ->
                observer?.unobserve(withDom.domNode)
                console.log("beforeUnmount")
            }
        }
    }
}

