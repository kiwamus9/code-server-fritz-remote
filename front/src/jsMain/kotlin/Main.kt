import dev.fritz2.core.RootStore
import dev.fritz2.core.render
import kotlinx.browser.window
import kotlinx.coroutines.Job
import org.w3c.dom.url.URL
import parts.editorPane.editorPane
import parts.fileListPane.fileListPane
import parts.resizableRowCol.resizableCol
import parts.resizableRowCol.resizableRow
import parts.terminalPane.terminalPane

object SelectedFileStore : RootStore<FileEntry?>(null, job = Job()) {
    var workspaces: String? = null
}

fun main() {
    val userName = URL(window.location.href).searchParams.get("userName")

    render("#target") {
        main("flex overflow-clip") {
            resizableCol(
                initialUpperHeight = "600px",
                upperDivContent = { editorPane(userName = userName, fileStore = SelectedFileStore) },
                lowerDivContent = {
                    resizableRow(
                        initialLeftWidth = "200px",
                        leftDivContent = { fileListPane(userName = userName, fileStore = SelectedFileStore) },
                        rightDicContent = { terminalPane(userName = userName, fileStore = SelectedFileStore) }
                    )
                })
        }
    }
}
