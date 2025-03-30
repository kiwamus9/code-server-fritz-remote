import dev.fritz2.core.RootStore
import dev.fritz2.core.render
import kotlinx.browser.window
import kotlinx.coroutines.Job
import org.w3c.dom.BroadcastChannel
import org.w3c.dom.MediaQueryListEvent
import org.w3c.dom.url.URL
import parts.editorPane.editorPane
import parts.fileListPane.fileListPane
import parts.resizableRowCol.resizableCol
import parts.resizableRowCol.resizableRow
import parts.terminalPane.terminalPane

val darkModeMediaQuery = window.matchMedia("(prefers-color-scheme: dark)")

object SelectedFileStore : RootStore<FileEntry?>(null, job = Job()) {
    var workspaces: String? = null
}

object DarkModeStore : RootStore<Boolean>(darkModeMediaQuery.matches, job = Job())

val channel = BroadcastChannel("soft-prac-dark-mode");



fun main() {
    val userName = URL(window.location.href).searchParams.get("userName")
    val mode = URL(window.location.href).searchParams.get("mode") ?: "unu"

    console.log("mode $mode")
    channel.addEventListener ("message", {
            event -> {
        console.log("sss",event);
    }
    });

//    darkModeMediaQuery.addEventListener("change", { e ->
//        val ev = e as MediaQueryListEvent
//        DarkModeStore.update(ev.matches)
//    })

    render("#target") {
        main("flex") {
            resizableCol(
                initialUpperHeight = "60%",
                upperDivContent = {
                    editorPane(
                        userName = userName,
                        fileStore = SelectedFileStore,
                        darkStore = DarkModeStore
                    )
                },

                lowerDivContent = {
                    resizableRow(
                        baseClass = "bg-inherit",
                        initialLeftWidth = "25%",
                        leftDivContent = { fileListPane(userName = userName, fileStore = SelectedFileStore) },
                        rightDicContent = {
                            terminalPane(
                                userName = userName,
                                fileStore = SelectedFileStore,
                                darkStore = DarkModeStore
                            )
                        }
                    )
                })
        }
    }
}