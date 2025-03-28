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
import dev.fritz2.core.checked
import dev.fritz2.core.disabled
import dev.fritz2.core.placeholder
import dev.fritz2.core.type
import dev.fritz2.core.value
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

const val numberOfPasteArea = 2
const val numberOfCommandLineArea = 2

var terminalDynamic: dynamic? = null
var observer: ResizeObserver? = null


fun generateCommand(workSpacePath: String, fileEntry: FileEntry): String {
    val lastComma = fileEntry.name.lastIndexOf(".")
    if (lastComma == -1) return ""

    val name = fileEntry.name.substring(0, lastComma)
    val ext = fileEntry.name.substring(lastComma + 1).lowercase()

    val commandParam = buildList<String> {
        (1..numberOfCommandLineArea) .forEach {
            if(document.getElementById("commandLine${it}-check").unsafeCast< HTMLInputElement>().value == "checked") {
                console.log("cc",document.getElementById("commandLine${it}-check").unsafeCast< HTMLInputElement>().value)
                add("una")
            }
            add("how")
        }
    }

    console.log(commandParam)

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
        if ((fileEntry != null) && (terminalDynamic != null)) {
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
                    div {
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
                                    placeholder("入力文字列${it}")
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
                    div {
                        (1..numberOfCommandLineArea).forEach {
                            div("my-1 ps-2") {
                                label("inline-flex items-center cursor-pointer ") {
                                    input("sr-only peer", "commandLine${it}-check") {
                                        type("checkbox")
                                        value("")
                                        checked(false)
                                    }
                                    div(
                                        "relative w-11 h-6 bg-gray-200 rounded-l-lg border-gray-300 peer peer-focus:ring-4 peer-focus:ring-blue-300 dark:peer-focus:ring-blue-800 dark:bg-gray-700 peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-0.5 after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all dark:border-gray-600 peer-checked:bg-blue-600 dark:peer-checked:bg-blue-600"
                                    ) {}
                                    input(inputTextClass, "commandLine${it}-input") {
                                        attr("spellcheck", "false") //spellcheck関数自体はあるけれど，HTMLInputElement向けじゃない
                                        type("text")
                                        placeholder("コマンドライン文字列${it}")
                                        autocomplete("true")
                                    }
                                }
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
                                document.createTextNode(" 複数のウィンドウで同時にターミナルは開けません．")
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

