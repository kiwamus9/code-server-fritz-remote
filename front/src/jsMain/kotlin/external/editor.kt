@file:JsModule("./js/editor.js")
@file:JsNonModule
@file:Suppress("unused")

package external

import org.w3c.dom.HTMLElement

external var baseDoc: String
external fun createState(doc: String): dynamic
external fun createEditorView(editorPane: HTMLElement, editorState: dynamic): dynamic
external fun updateEditorView(editorView: dynamic, content: String)
external fun toDarkMode(editorVew: dynamic, isDarkMode: Boolean)
