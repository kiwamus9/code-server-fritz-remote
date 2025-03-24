@file:Suppress("PackageDirectoryMismatch")

package parts.editorPane

import DarkModeStore
import SelectedFileStore
import buttonClass
import dev.fritz2.core.RenderContext
import dev.fritz2.core.RootStore
import dev.fritz2.core.afterMount
import dev.fritz2.core.disabled
import dev.fritz2.remote.http
import external.EditorView
import external.StateEvent
import external.createEditorView
import external.createState
import external.resetDocChanged
import external.toDarkMode
import external.updateEditorView
import kotlinx.browser.window
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.w3c.dom.CustomEvent
import org.w3c.dom.HTMLDivElement
import parts.spinner
import parts.titleBar.titleBar

sealed class ModelState {
    object Init : ModelState()
    object Loading : ModelState()
    data class Loaded(val content: String) : ModelState()
    data class LoadError(val errMessage: String) : ModelState()
}

sealed class Message {
    data class Load(val userFullPathName: String) : Message()
    object Save : Message()
}

data class Model(val state: ModelState, val content: String)

var editorView: EditorView? = null
val editorState = createState("")

fun RenderContext.editorPane(
    baseClass: String? = null, id: String? = null, userName: String? = null,
    fileStore: SelectedFileStore, darkStore: DarkModeStore
) {
    val modelStore = object : RootStore<Model>(Model(ModelState.Init, ""), job = Job()) {
        val workspace = http("/codeServer2/data/workspace/file/v2").accept("text/plain").contentType("text/plain")
        val load = handle<String> { oldModel, userPath ->
            val resp = workspace.get("?userFullPathName=$userPath")
            if (resp.ok && (resp.status != 404)) {
                Model(ModelState.Loaded(resp.body()), "")
            } else {
                Model(
                    ModelState.LoadError(
                        if (!resp.ok) "サーバに接続失敗" else "ファイル名が不正"
                    ), ""
                )
            }
        }
    }

    // Model内で表現してたけど，editorPaneが初期化されてエディタのカーソル位置が飛ぶのでやめた
    var isChangeFlow = MutableStateFlow(false)


    suspend fun update(msg: Message) {
        when (msg) {
            is Message.Load -> {
                modelStore.update(Model(ModelState.Loading, ""))
                modelStore.load(msg.userFullPathName) // Modelが変化する
            }

            is Message.Save -> {
                if (modelStore.current.state is ModelState.Loaded) {
                    val userSavePath = userName + "/" + fileStore.current!!.fullPathName()
                    val saveContent = editorView!!.state.doc
                    val workspace = http("/codeServer2/data/workspace/file/v2").acceptJson()
                        .contentType("application/json")
                    val resp = workspace.body(JSON.stringify(saveContent.toJSON())).put("?userFullPathName=$userSavePath")
                    if (!resp.ok or (resp.status != 200)) {
                        window.alert("ファイル保存に失敗しました")
                    } else {
                        // 新しく変更チェックを始める
                        resetDocChanged(saveContent.toString())
                        isChangeFlow.value = false
                    }
                }
            }
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
        editorView?.let { toDarkMode(it, isDarkMode) }
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
                        update(
                            Message.Load(userName + "/" + fileStore.current!!.fullPathName())
                        )
                    }
                }
                button(buttonClass) {
                    disabled(isChangeFlow.map { !it })
                    i("bi bi-floppy2") {}
                }.clicks handledBy {
                    userName?.let {
                        update(
                            Message.Save
                        )
                    }
                }
            },
            centerDivContent = {
                modelStore.data.render { model ->
                    when (model.state) {
                        is ModelState.Init -> +"未接続"
                        is ModelState.Loading -> +"接続中"
                        is ModelState.LoadError -> +"接続エラー：${model.state.errMessage}"
                        is ModelState.Loaded -> {
                            +(fileStore.current?.fullPathName() ?: "ファイルパスエラー")
                            editorView?.let { updateEditorView(it, model.state.content) }
                        }
                    }
                }
            },
            rightDivContent = {}
        )
        // editor
        div("grow-1 shrink-1 w-[100%] h-[100%] overflow-auto dark:bg-black bg-white ", "editorPane") {
            subscribe<CustomEvent>("editorStat") handledBy {
                it.detail?.let { o ->
                    val s = o.unsafeCast<StateEvent>()
                    isChangeFlow.value = s.isChanged
                }
            }
        }.afterMount { withDom, _ ->
            editorView = createEditorView(withDom.domNode as HTMLDivElement, editorState)
        }
    }
}