import dev.fritz2.core.d
import dev.fritz2.core.fill
import dev.fritz2.core.render
import dev.fritz2.core.viewBox
import kotlinx.browser.window
import org.w3c.dom.url.URL
import parts.editorPane.editorPane
import parts.fileListPane.fileListPane
import parts.resizableRowCol.resizableCol
import parts.resizableRowCol.resizableRow
import parts.terminalPane.terminalPane

fun main() {
    val userName = URL(window.location.href).searchParams.get("userName")
    render("#target") {
        main("flex overflow-visible") {
            resizableCol(
                initialUpperHeight = "600px",
                upperDivContent = { editorPane(userName = userName) },
                lowerDivContent = {
                    resizableRow(
                        initialLeftWidth = "200px",
                        leftDivContent = { fileListPane(userName = userName) },
                        rightDicContent = { terminalPane(userName = userName) }
                    )
                })
        }
    }
}
