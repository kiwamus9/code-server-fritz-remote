import DarkModeStore.handledBy
import dev.fritz2.core.RootStore
import dev.fritz2.core.render
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import org.w3c.dom.BroadcastChannel
import org.w3c.dom.HTMLHtmlElement
import org.w3c.dom.MediaQueryListEvent
import org.w3c.dom.MessageEvent
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

val channel = BroadcastChannel("soft-prac-dark-mode")
val channelReportChange = BroadcastChannel("soft-prac-report-change");

/*
    val channel = BroadcastChannel("FileListPane")
    channel.addEventListener("message", { event ->
        val messageEvent = event as MessageEvent
        if ((messageEvent.data as String)== "reload" && userName != null) {
            update(Message.Load(userName))
        }
    })
 */

fun main() {

    val userName = URL(window.location.href).searchParams.get("userName")
    val mode = URL(window.location.href).searchParams.get("mode") ?: run {
        if (darkModeMediaQuery.matches) "dark" else "light"
    }
    val darkModeStore = DarkModeStore

    // ダークモード
    darkModeStore.update(mode == "dark")
    channel.addEventListener("message", { ev ->
        val event = ev.unsafeCast<MessageEvent>()
        DarkModeStore.update((event.data as String) == "dark")
    })

    // 選択している課題が変わったとき
    channelReportChange.addEventListener("message", { event ->
        val messageEvent = event as MessageEvent
        if ((messageEvent.data as String)== "change") {
            SelectedFileStore.update(null)
        }
    })

    render("#target") {

        DarkModeStore.data.handledBy { isDark ->
            val rootHtml = document.getElementById("rootHtml") as HTMLHtmlElement
            if (isDark) rootHtml.addClass("dark")
            else rootHtml.removeClass("dark")
        }

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
