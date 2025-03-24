@file:JsModule("./js/editor.js")
@file:JsNonModule
@file:Suppress("unused")

package external

import org.w3c.dom.HTMLElement

external interface StateEvent {
    val isChanged: Boolean
}
external interface EditorView {
    val state: EditorViewState
}
external interface EditorViewState {
    val doc: dynamic  // Text Interface of CodeMirror
}
external fun resetDocChanged(doc: String)
external fun createState(doc: String): dynamic
external fun createEditorView(editorPane: HTMLElement, editorState: dynamic): EditorView
external fun updateEditorView(editorView: EditorView, content: String)
external fun toDarkMode(editorVew: EditorView, isDarkMode: Boolean)
