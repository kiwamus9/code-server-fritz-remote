@file:Suppress("PackageDirectoryMismatch")

package parts.fileListPane

import FileEntry
import Files
import buttonClass
import dev.fritz2.core.RenderContext
import dev.fritz2.core.RootStore
import dev.fritz2.core.d
import dev.fritz2.core.fill
import dev.fritz2.core.viewBox
import dev.fritz2.remote.decoded
import dev.fritz2.remote.http
import kotlinx.coroutines.Job
import kotlinx.serialization.ExperimentalSerializationApi
import parts.fileListPane.ModelState.*
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

@OptIn(ExperimentalSerializationApi::class)
fun RenderContext.fileListPane(
    baseClass: String? = null, id: String? = null, userName: String? = null,
    fileStore: RootStore<FileEntry?>
) {

    val modelStore = object : RootStore<Model>(Model(Init, emptyList()), job = Job()) {
        val load = handle<String> { _, name ->
            val workspace = http("/codeServer2/data/workspace/user").acceptJson().contentType("application/json")
            val resp = workspace.get(name)
            if (resp.ok && (resp.status != 404)) {
                //console.log(resp.body())
                // val l = FileEntry.parseFileAttrList(resp.decoded<Files2>().fileLists, "", 0)
                //  val f = FileEntry.flattenList(l)
                val f = FileEntry.parseFileAttrList(resp.decoded<Files>().fileLists, "", 0).run(FileEntry::flattenList)
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

    // ここからスタート
    if (userName != null) {
        update(Message.Load(userName))
    }

    div("flex flex-col h-full w-full") {
        titleBar(
            leftDivContent = {
                button(buttonClass) {
                    modelStore.data.render { model ->
                        if (model.state == ModelState.Init) {
                            div {
                                attr("role", "status")
                                svg("w-[14px] h-[20px] text-gray-200 animate-spin dark:text-gray-600 text-red-300 fill-blue-600") {
                                    attr("aria-hidden", "true")
                                    viewBox("0 0 100 101")
                                    fill("none")
                                    xmlns("http://www.w3.org/2000/svg")
                                    path {
                                        d("M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z")
                                        fill("currentColor")
                                    }
                                    path {
                                        d("M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z")
                                        fill("currentFill")
                                    }
                                }
                                span("sr-only") { +"Loading...</span>" }
                            }
                        } else {
                            i("bi bi-arrow-clockwise") {}

                        }
                    }
                }
            },
            centerDivContent = {},
            rightDivContent = {}
        )
        div("grow dark:bg-black bg-white text-sm pl-1 pt-1") {
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
                                                classList(listOf<String>("hover:bg-gray-200 bg-gray-300 dark:bg-gray-700  dark:hover:bg-gray-500"))
                                            } else {
                                                classList(listOf<String>("hover:bg-gray-200 dark:hover:bg-gray-500 active:bg-gray-300 dark:active:bg-gray-700"))
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

                    is LoadError -> +"接続エラー（${model.state.errorMsg}"
                }
            }
        }
    }

}
