import kotlinx.serialization.Serializable

@Serializable
data class FileAttr(val name: String, val path: String, val isDirectory: Boolean)

@Serializable
data class Files(val workspace: String, val fileLists: List<FileAttr>)

class FileEntry(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val children: MutableList<FileEntry>,
    val level: Int
) {
    companion object {
        fun parseFileAttrList(list: List<FileAttr>, path: String, level: Int): MutableList<FileEntry> {
            if (list.isEmpty()) return mutableListOf()

            val matchList = mutableListOf<FileAttr>()
            val unMatchList = mutableListOf<FileAttr>()
            val resultList = mutableListOf<FileEntry>()
            list.forEach {
                if (it.path == path) matchList.add(it) else unMatchList.add(it)
            }
            matchList.apply {
                removeAll { it.name.startsWith(".") } //隠しファイル，ディレクトリ除去
                sortByDescending { it.name }
                sortByDescending { it.isDirectory } // ディレクトリを上に
            }

            matchList.map {
                resultList.add(
                    FileEntry(
                        name = it.name,
                        path = it.path,
                        isDirectory = it.isDirectory,
                        children = parseFileAttrList(
                            unMatchList,
                            if (path.isEmpty()) it.name else path + "/" + it.name,
                            level + 1
                        ),
                        level = level
                    )
                )
            }
            return resultList
        }

        fun flattenList(list: List<FileEntry>): List<FileEntry> {
            if (list.isEmpty()) return mutableListOf()
            val result = mutableListOf<FileEntry>()

            list.forEach {
                result.add(it)
                result.addAll(flattenList(it.children))
            }
            return result
        }
    }
    fun fullPathName() = "$path/$name"
}
