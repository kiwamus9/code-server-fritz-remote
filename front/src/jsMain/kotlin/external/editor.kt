@file:JsModule("./js/editor.js")
@file:JsNonModule

package external

import org.w3c.dom.HTMLElement

external fun createState(doc:String): dynamic
external fun createEditorView(editorPane: HTMLElement, editorState: dynamic): dynamic