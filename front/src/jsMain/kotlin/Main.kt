import dev.fritz2.core.RootStore
import dev.fritz2.core.render
import kotlinx.browser.window
import kotlinx.coroutines.Job
import org.w3c.dom.MediaQueryListEvent
import org.w3c.dom.url.URL
import parts.editorPane.editorPane
import parts.fileListPane.fileListPane
import parts.resizableRowCol.resizableCol
import parts.resizableRowCol.resizableRow
import parts.terminalPane.terminalPane

object SelectedFileStore : RootStore<FileEntry?>(null, job = Job()) {
    var workspaces: String? = null
}


val darkModeMediaQuery = window.matchMedia("(prefers-color-scheme: dark)")

object DarkModeStore : RootStore<Boolean>(darkModeMediaQuery.matches, job = Job()) {}


fun main() {
    val userName = URL(window.location.href).searchParams.get("userName")

    darkModeMediaQuery.addEventListener("change", { e ->
        val ev = e as MediaQueryListEvent
        DarkModeStore.update(ev.matches)
    })

    render("#target") {
        main("flex") {
            resizableCol(
                initialUpperHeight = "600px",
                upperDivContent = { editorPane(userName = userName, fileStore = SelectedFileStore, darkStore = DarkModeStore) },

                lowerDivContent = {
                    resizableRow(
                        baseClass = "bg-inherit",
                        initialLeftWidth = "200px",
                        leftDivContent = { fileListPane(userName = userName, fileStore = SelectedFileStore) },
                        rightDicContent = { terminalPane(userName = userName, fileStore = SelectedFileStore, darkStore = DarkModeStore) }
                    )
                })
        }
    }
}
/*
const darkModeMediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
const darkModeOn = darkModeMediaQuery.matches;

darkModeMediaQuery.addListener((e) => {
    const darkModeOn = e.matches;
    if (darkModeOn) {
        document.body.classList.remove('light-mode');
        document.body.classList.add('dark-mode');
    } else {
        document.body.classList.remove('dark-mode');
        document.body.classList.add('light-mode');
    }
});

 */