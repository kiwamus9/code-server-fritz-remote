@file:Suppress("PackageDirectoryMismatch")

package parts.fileListPane

import FileEntry
import Files
import SelectedFileStore
import buttonClass
import dev.fritz2.core.RenderContext
import dev.fritz2.core.RootStore
import dev.fritz2.core.disabled
import dev.fritz2.remote.decoded
import dev.fritz2.remote.http
import external.StateEvent
import external.reloadFileListCallBack
import external.setReloadFileListCallBack
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.serialization.ExperimentalSerializationApi
import org.w3c.dom.BroadcastChannel
import org.w3c.dom.CustomEvent
import org.w3c.dom.MessageEvent
import parts.fileListPane.ModelState.*
import parts.spinner
import parts.titleBar.titleBar

sealed class ModelState {
    object Init : ModelState()
    data class Loading(val userName: String) : ModelState()
    data class LoadError(val errorMsg: String) : ModelState()
    data class Loaded(val userName: String) : ModelState()
}

sealed class Message {
    data class Load(val userName: String) : Message()
    data class Select(val select: FileEntry) : Message()
}

data class Model(val state: ModelState, val fileList: List<FileEntry>, val selected: FileEntry? = null)

@Suppress("unused")
@OptIn(ExperimentalSerializationApi::class)
fun RenderContext.fileListPane(
    baseClass: String? = null, id: String? = null, userName: String? = null,
    fileStore: SelectedFileStore
) {

    val modelStore = object : RootStore<Model>(Model(Init, emptyList()), job = Job()) {
        val load = handle<String> { _, name ->
            val workspace = http("/soft_prac/codeServer2/data/workspace/user").acceptJson().contentType("application/json")
            val resp = workspace.get(name)
            if (resp.ok && (resp.status != 404)) {
                //console.log(resp.body())
                val pureBody = resp.decoded<Files>()
                fileStore.workspaces = pureBody.workspace
                val l = FileEntry.parseFileAttrList(pureBody.fileLists, "", 0)
                val f = FileEntry.flattenList(l)
                Model(Loaded(name), f)
            } else {
                Model(
                    LoadError(
                        if (!resp.ok) "サーバに接続失敗" else "ユーザ名が不正"
                    ), emptyList()
                )
            }
        }
    }

    fun update(msg: Message) {
        when (msg) {
            is Message.Load -> {
                modelStore.update(Model(Loading(msg.userName), emptyList()))
                modelStore.load(msg.userName) // Modelが変化する
            }

            is Message.Select -> {
                if (modelStore.current.state is Loaded) {
                    modelStore.update(modelStore.current.copy(selected = msg.select))
                    fileStore.update(msg.select)
                }
            }
        }
    }

//    val channel = BroadcastChannel("FileListPane")
//    channel.addEventListener("message", { event ->
//        val messageEvent = event as MessageEvent
//        if ((messageEvent.data as String)== "reload" && userName != null) {
//            update(Message.Load(userName))
//        }
//    })

    // terminal経由でwatcherがファイル変更を見張っている
    setReloadFileListCallBack{
        if (userName != null) {
            update(Message.Load(userName))
            fileStore.update(null)
        }
    }

    // ここからスタート
    if (userName != null) {
        update(Message.Load(userName))
    }

    div("flex flex-col h-full w-full bg-red-300",id) {

        titleBar(
            leftDivContent = {
                button(buttonClass) {
                    disabled(modelStore.data.map { it.state is Init })
                    modelStore.data.render { model ->
                        if (model.state is Loading) {
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
            },
            centerDivContent = {},
            rightDivContent = {}
        )
        div("grow dark:text-white text-black dark:bg-black bg-white text-sm pl-1 pt-1 overflow-auto") {
            modelStore.data.render(into = this) { model ->
                when (model.state) {
                    is Init -> +"未接続"
                    is Loading -> +"接続中"
                    is Loaded -> {
                        div("w-full") {
                            if (model.fileList.isEmpty()) {
                                +"ファイルがありません"
                            } else {
                                model.fileList.forEach { entry ->
                                    button("w-full text-left") {
                                        if (entry.isDirectory) {
                                            i("bi bi-folder2-open mr-1") {}
                                        } else {
                                            i("bi bi-file-earmark-text mr-1") {}
                                            if (entry == model.selected) {
                                                classList(
                                                    listOf<String>(
                                                        "hover:bg-gray-200 bg-gray-300 " +
                                                                "dark:bg-gray-700  dark:hover:bg-gray-500"
                                                    )
                                                )
                                            } else {
                                                classList(
                                                    listOf<String>(
                                                        "hover:bg-gray-200 dark:hover:bg-gray-500 " +
                                                                "active:bg-gray-300 dark:active:bg-gray-700"
                                                    )
                                                )
                                            }
                                            clicks handledBy { update(Message.Select(entry)) }
                                        }
                                        inlineStyle("padding-left: ${entry.level}em;")
                                        +entry.name
                                    }
                                    br {}
                                }
                            }
                        }
                    }

                    is LoadError -> +"接続エラー：${model.state.errorMsg}"
                }
            }
        }
    }
}
