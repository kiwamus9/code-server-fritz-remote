@file:JsModule("./js/editor.js")
@file:JsNonModule

package external

import org.w3c.dom.HTMLElement

external var baseDoc: String
external fun createState(@Suppress("unused") doc:String): dynamic
external fun createEditorView(@Suppress("unused") editorPane: HTMLElement, @Suppress("unused") editorState: dynamic): dynamic
external fun updateEditorView(@Suppress("unused") editorView: dynamic, @Suppress("unused") content: String)
external fun toDarkMode(editorVew: dynamic, isDarkMode: Boolean)
