@file:Suppress("PackageDirectoryMismatch")

package parts.fileListPane

import buttonClass
import dev.fritz2.core.RenderContext
import dev.fritz2.core.RootStore
import dev.fritz2.remote.decoded
import dev.fritz2.remote.http
import kotlinx.coroutines.Job
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import parts.fileListPane.ModelState.*
import parts.titleBar.titleBar

sealed class ModelState {
    object Init: ModelState()
    data class Loading(val userName: String) : ModelState()
    data class LoadError(val errorMsg: String) : ModelState()
    data class Loaded(val userName: String) : ModelState()
    data class Selected(val select: Int) : ModelState()
}

sealed class Message {
    data class Load(val userName: String) : Message()
    data class Select(val select: Int) : Message()
}

//suspend fun loadFileLists(userName: String) {
//    val workspace = http("/codeServer2/data/workspace/user").acceptJson().contentType("application/json")
//    console.log(workspace.get(userName))
//    axiosClient.get(`/codeServer2/data/workspace/user/${it}`).then(
//        res => {
//        workSpaceStore.set(res.data.workspace)
//        let fileEntries =
//        FileEntry.parseFileAttrs(res.data.fileLists)
//
//        // 隠しフォルダを除く
//        fileEntries = fileEntries.filter((it) =>
//        !(it.name.startsWith("."))
//        )
//        // 後に提出されたものを上に持ってくる
//        fileEntries.reverse()
//
//        function flatten (top: FileEntry[]) {
//        let result : FileEntry [] = []
//        top.forEach((file: FileEntry) => {
//        result.push(file)
//        if (file.children.length !== 0) {
//            result = result.concat(flatten(file.children))
//        }
//    })
//        return result
//    }
//
//        fileList = flatten(fileEntries)
//    })
//}

@Serializable
data class FileAttr(val name: String, val path: String, val isDirectory: Boolean)

@Serializable
data class Files2(val workspace: String, val fileLists: List<FileAttr>)

data class Model(val state: ModelState, val fileList: List<FileAttr>)

@OptIn(ExperimentalSerializationApi::class)
fun RenderContext.fileListPane(baseClass: String? = null, id: String? = null, userName: String? = null) {

    val modelStore = object : RootStore<Model>(Model(ModelState.Init, emptyList()), job = Job()) {
        val load = handle<String> { _ , name->
            val workspace = http("/codeServer2/data/workspace/user").acceptJson().contentType("application/json")
            val resp = workspace.get(name)
            if(resp.ok && (resp.status != 404)) {
                //console.log(resp.body())
                Model(ModelState.Loaded(name), resp.decoded<Files2>().fileLists)
            } else {
                Model(ModelState.LoadError(
                    if(!resp.ok) "サーバに接続失敗" else "ユーザ名が不正"
                ), emptyList())
            }
        }
    }

    fun update(msg: Message) {
        when (msg) {
            is Message.Load -> {
                    modelStore.update(Model(Loading(msg.userName), emptyList()))
                    modelStore.load(msg.userName) // Modelが変化する
                }
            is Message.Select -> TODO()
        }
    }


    // ここからスタート
    if(userName != null) { update(Message.Load(userName))}

    titleBar(
        leftDivContent = {
            button(buttonClass) {
                i("bi bi-arrow-clockwise") {}
            }
        },
        centerDivContent = {},
        rightDivContent = {}
    )
    div {
        modelStore.data.render { model ->
            when (model.state) {
                is Init -> +"init"
                is Loaded -> +"loaded"
                is Loading -> +"loading"
                is Selected -> +"selected"
                is LoadError -> +"loadError"
            }
        }
    }

}
