@file:Suppress("PackageDirectoryMismatch")

package parts.editorPane

import DarkModeStore
import Point
import SelectedFileStore
import buttonClass
import dev.fritz2.core.RenderContext
import dev.fritz2.core.RootStore
import dev.fritz2.core.afterMount
import dev.fritz2.core.disabled
import dev.fritz2.remote.http
import external.createEditorView
import external.createState
import external.toDarkMode
import external.updateEditorView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import org.w3c.dom.HTMLDivElement
import parts.spinner
import parts.titleBar.titleBar

sealed class ModelState {
    object Init : ModelState()
    object Loading : ModelState()
    data class Loaded(val content: String) : ModelState()
    data class LoadError(val errMessage: String) : ModelState()
    object Changed : ModelState()
}

sealed class Message {
    data class Load(val userFullPathName: String) : Message()
    data class Save(val point: Point) : Message()
}

data class Model(val state: ModelState, val content: String, val changed: Boolean)

var editorView: dynamic? = null
val editorState = createState("")

fun RenderContext.editorPane(
    baseClass: String? = null, id: String? = null, userName: String? = null,
    fileStore: SelectedFileStore, darkStore: DarkModeStore
) {
    val modelStore = object : RootStore<Model>(Model(ModelState.Init, "", false), job = Job()) {
        val load = handle<String> { oldState, userPath ->
            val workspace = http("/codeServer2/data/workspace/file/v2").accept("text/plain").contentType("text/plain")
            val resp = workspace.get("?userFullPathName=$userPath")
            if (resp.ok && (resp.status != 404)) {
                Model(ModelState.Loaded(resp.body()), "", false)
            } else {
                Model(
                    ModelState.LoadError(
                        if (!resp.ok) "サーバに接続失敗" else "ファイル名が不正"
                    ), "", false
                )
            }
        }
    }


    fun update(msg: Message) {
        when (msg) {
            is Message.Load -> {
                modelStore.update(Model(ModelState.Loading, "", false))
                modelStore.load(msg.userFullPathName) // Modelが変化する
            }

            is Message.Save -> TODO()
        }
    }

    // 読み込みファイル切り替え
    fileStore.data.handledBy { fileEntry ->
        fileEntry?.let {
            if (userName != null) update(Message.Load(userName + "/" + it.fullPathName()))
        }
    }

    // ダークモード切替
    darkStore.data.handledBy { isDarkMode ->
        if (editorView != null) {
            toDarkMode(editorView, isDarkMode)
        }
    }

    div("flex flex-col h-[100%] w-[100%] overflow-auto" + (baseClass ?: ""), id) {
        titleBar(
            leftDivContent = {
                button(buttonClass) {
                    disabled(modelStore.data.map { it.state is ModelState.Init })
                    modelStore.data.render { model ->
                        if (model.state is ModelState.Loading) {
                            spinner()
                        } else {
                            i("bi bi-arrow-clockwise") {}
                        }
                    }
                }.clicks handledBy {
                    userName?.let {
                        update(Message.Load(it))
                    }
                }
                button(buttonClass) {
                    disabled(modelStore.data.map { it.state is ModelState.Init })
                    modelStore.data.render { model ->
                        if (model.state is ModelState.Loading) {
                            spinner()
                        } else {
                            i("bi bi-floppy2") {}
                        }
                    }
                }.clicks handledBy {
                    userName?.let {
                        update(Message.Load(it))
                    }
                }
            },
            centerDivContent = {
                modelStore.data.render { model ->
                    when (model.state) {
                        ModelState.Changed -> TODO()
                        ModelState.Init -> +"未接続"
                        is ModelState.LoadError -> +"接続エラー：${model.state.errMessage}"
                        is ModelState.Loaded -> {
                            +(fileStore.current?.fullPathName() ?: "ファイルパスエラー")
                            updateEditorView(editorView, model.state.content)
                        }

                        ModelState.Loading -> +"接続中"
                    }
                }
            },
            rightDivContent = {}
        )
        // editor
        div("grow-1 shrink-1 w-[100%] h-[100%] overflow-auto dark:bg-black bg-white ", "editorPane") {
        }.afterMount { withDom, _ ->
            editorView = createEditorView(withDom.domNode as HTMLDivElement, editorState)
        }
    }
}