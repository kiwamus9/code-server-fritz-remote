import dev.fritz2.core.RootStore
import dev.fritz2.core.render
import kotlinx.coroutines.Job
import parts.editorPane.editorPane
import parts.fileListPane.fileListPane
import parts.resizableRowCol.resizableCol
import parts.resizableRowCol.resizableRow
import parts.terminalPane.terminalPane


sealed class ModelState {
    object Init : ModelState()
    object UserSelected : ModelState()
    object StudentSelected : ModelState()
    object FileSelected : ModelState()
}

sealed class Message {
    data class MouseMoving(val point: Point) : Message()
    data class MouseDownOnBorder(val point: Point) : Message()
    object MouseOnBorder : Message()
    object MouseReleased : Message()
}

fun main() {

    render("#target") {
        data class Model(val state: ModelState, val isDragging: Boolean = false)

        val rootModelStore = object : RootStore<Model>(Model(ModelState.Init), job = Job()) {}

        resizableCol(
            initialUpperHeight = "50%",
            upperDivContent = { editorPane() },
            lowerDivContent = {
                resizableRow(
                    //initialLeftWidth = "25%",
                    leftDivContent = { fileListPane() },
                    //rightDivBaseClass = "flex flex-col",
                    rightDicContent = { terminalPane() }
                )
            })
    }
}
