@file:Suppress("PackageDirectoryMismatch")

package parts.fileListPane

import Point
import buttonClass
import dev.fritz2.core.RenderContext
import dev.fritz2.core.RootStore
import dev.fritz2.remote.http
import kotlinx.coroutines.Job
import parts.titleBar.titleBar

sealed class ModelState {
    object Init : ModelState()
    object Loading : ModelState()
    data class Selected(val select: Int) : ModelState()
}

sealed class Message {
    data class Select(val select: Int) : Message()
}

fun loadFileLists(userName: String) {
//    http("/codeServer2/data/workspace/user/${it}")
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
}

fun RenderContext.fileListPane(baseClass: String? = null, id: String? = null) {
    data class Model(val state: ModelState, val fileList: List<String>)

    val modelStore = object : RootStore<Model>(Model(ModelState.Init, emptyList()), job = Job()) {}

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
        +"list"
    }

}
