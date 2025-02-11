@file:Suppress("PackageDirectoryMismatch")

package parts.fileListPane

import buttonClass
import dev.fritz2.core.RenderContext
import parts.titleBar.titleBar

fun RenderContext.fileListPane(baseClass: String? = null, id: String? = null) {
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
