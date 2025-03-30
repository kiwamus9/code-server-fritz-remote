@file:Suppress("PackageDirectoryMismatch")

package parts.resizableRowCol

import Point
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.RootStore
import exec
import jsObject
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.Job
import org.w3c.dom.HTMLDivElement
import parts.resizableRowCol.ResizableRowColType.*

enum class ResizableRowColType {
    Row, Col;
}

sealed class ModelState {
    object NonTracking : ModelState()
    object OnBorder : ModelState()
    data class Dragging(val point: Point) : ModelState()
    object Released : ModelState()
}

sealed class Message {
    data class MouseMoving(val point: Point) : Message()
    data class MouseDownOnBorder(val point: Point) : Message()
    object MouseOnBorder : Message()
    object MouseReleased : Message()
}

class ResizableRowCol(
    val type: ResizableRowColType, val baseClass: String? = null, val id: String? = null,
    val borderWidth: Int = 5,
    val initialContent1Size: String? = null,
    var divBaseClass1: String? = null,
    var divBaseClass2: String? = null,
    val divContent1: RenderContext.() -> Unit,
    val divContent2: RenderContext.() -> Unit
) {

    data class Model(val state: ModelState, val isDragging: Boolean = false)

    val modelStore = object : RootStore<Model>(Model(ModelState.NonTracking), job = Job()) {}

    val cursorType: String
    val flexDirection: String
    val borderPos: String
    val borderStyle: String
    val initFlexBasis: String

    init {
        modelStore.update(Model(ModelState.NonTracking, isDragging = false))
        document.body?.style?.cursor = "auto"
        when (type) {
            Row -> {
                cursorType = "col-resize"
                flexDirection = "flex-row"
                borderPos = "r"
                borderStyle = "border-right-width: ${borderWidth}px;"
                initFlexBasis = " flex-basis: ${initialContent1Size}; max-width: ${initialContent1Size}; "
            }

            Col -> {
                cursorType = "row-resize"
                flexDirection = "flex-col"
                borderPos = "b"
                borderStyle = "border-bottom-width: ${borderWidth}px;"
                initFlexBasis = " flex-basis: ${initialContent1Size}; max-height: ${initialContent1Size};"
                divBaseClass2 += " min-h-[100px] overflow-clip"
            }
        }
    }

    fun isOverBorder(mousePos: Point, content1Pane: HtmlTag<*>): Boolean {
        return when (type) {
            Row -> {
                (mousePos.x in (content1Pane.domNode.clientWidth..(content1Pane.domNode.clientWidth + borderWidth)))
            }

            Col -> {
                (mousePos.y in (content1Pane.domNode.clientHeight..(content1Pane.domNode.clientHeight + borderWidth)))
            }
        }
    }

    fun update(msg: Message) {
        when (msg) {
            is Message.MouseOnBorder -> {
                modelStore.update(Model(ModelState.OnBorder, isDragging = false))
            }

            is Message.MouseDownOnBorder -> {
                modelStore.update(Model(ModelState.Dragging(msg.point), isDragging = true))
            }

            is Message.MouseMoving -> {
                if (modelStore.current.isDragging) {
                    modelStore.update(Model(ModelState.Dragging(msg.point), isDragging = true))
                } else {
                    modelStore.update(Model(ModelState.NonTracking, isDragging = false))
                }
            }

            is Message.MouseReleased -> {
                modelStore.update(Model(ModelState.NonTracking, isDragging = false))
            }
        }
    }
}

fun RenderContext.resizableColRow(
    setting: ResizableRowCol
) {
    lateinit var allPane: HtmlTag<HTMLDivElement>
    lateinit var content1Pane: HtmlTag<HTMLDivElement>
    lateinit var content2Pane: HtmlTag<HTMLDivElement>

    allPane = div("h-[100%] w-[100%] flex ${setting.flexDirection} ${setting.baseClass ?: ""}", setting.id) {
        setting.modelStore.data.exec(job) { modelStore ->
            when (val state = modelStore.state) {
                is ModelState.Dragging -> {
                    document.body?.style?.cursor = setting.cursorType
                    allPane.inlineStyle("user-select: none; -webkit-user-select: none;")
                    when (setting.type) {
                        Row -> {
                            content1Pane.inlineStyle(setting.borderStyle +
                                    " width:${state.point.x}px; flex-basis: ${state.point.x}px;" +
                                    "  max-width: ${state.point.x}px")
                        }

                        Col -> {
                            content1Pane.inlineStyle(setting.borderStyle +
                                    " flex-basis: ${state.point.y}px; max-height: ${state.point.y}px")
                        }
                    }
                }

                is ModelState.NonTracking -> {
                    if (document.body?.style?.cursor == setting.cursorType)
                        document.body?.style?.cursor = "auto"
                }

                is ModelState.OnBorder -> {
                    if (document.body?.style?.cursor == "auto")
                        document.body?.style?.cursor = setting.cursorType
                }

                is ModelState.Released -> {
                    if (document.body?.style?.cursor == setting.cursorType)
                        document.body?.style?.cursor = "auto"
                    allPane.inlineStyle("-webkit-user-select: auto; user-select: auto;")
                }
            }
        }
        content1Pane =
            div("dark:border-zinc-600 border-zinc-300  shrink-0 ${setting.divBaseClass1 ?: ""}") {
                inlineStyle(setting.borderStyle + setting.initFlexBasis)
//            div("dark:border-zinc-600 border-zinc-200 border-b-4") {
                (setting.divContent1)()
            }
        content2Pane = div("bg-gray-300 grow-1 shrink-1 min-w-[150px] ${setting.divBaseClass2 ?: ""}") {
            (setting.divContent2)()
        }
    }.apply {
        mousemoves handledBy { mouseEv ->
            if ((setting.isOverBorder(Point(mouseEv.clientX, mouseEv.clientY), content1Pane)) &&
                (!setting.modelStore.current.isDragging)
            ) {
                //ボーダ上でかつドラッグ中じゃないとき
                setting.update(Message.MouseOnBorder)
            } else {
                setting.update(Message.MouseMoving(Point(mouseEv.clientX, mouseEv.clientY)))
            }
        }
        mousedowns handledBy { mouseEv ->
            if (setting.isOverBorder(Point(mouseEv.clientX, mouseEv.clientY), content1Pane)) {
                window.addEventListener(
                    "mouseup",
                    { _ -> setting.update(Message.MouseReleased) },
                    options = jsObject { onece = True })
                console.log(mouseEv.clientX, mouseEv.clientY)
                setting.update(Message.MouseDownOnBorder(Point(mouseEv.clientX, mouseEv.clientY)))
            }
        }
    }
}


fun RenderContext.resizableCol(
    baseClass: String? = null, id: String? = null, initialUpperHeight: String? = null,
    upperDivBaseClass: String? = null,
    lowerDivBaseClass: String? = null,
    upperDivContent: RenderContext.() -> Unit = { p { +"upperDivContent" } },
    lowerDivContent: RenderContext.() -> Unit = { p { +"lowerDivContent" } }
) {
    val setting = ResizableRowCol(
        type = Col, baseClass = baseClass, id = id, initialContent1Size = initialUpperHeight,
        divBaseClass1 = upperDivBaseClass, divBaseClass2 = lowerDivBaseClass,
        divContent1 = upperDivContent, divContent2 = lowerDivContent
    )
    resizableColRow(setting)
}

fun RenderContext.resizableRow(
    baseClass: String? = null, id: String? = null, initialLeftWidth: String? = null,
    leftDivBaseClass: String? = null,
    rightDivBaseClass: String? = null,
    leftDivContent: RenderContext.() -> Unit = { p { +"leftDivContent" } },
    rightDicContent: RenderContext.() -> Unit = { p { +"rightDicContent" } }
) {
    val setting = ResizableRowCol(
        type = Row, baseClass = baseClass, id = id, initialContent1Size = initialLeftWidth,
        divBaseClass1 = leftDivBaseClass, divBaseClass2 = rightDivBaseClass,
        divContent1 = leftDivContent, divContent2 = rightDicContent
    )
    resizableColRow(setting)
}
