@file:JsModule("./js/terminal.js")
@file:JsNonModule
@file:Suppress("unused")

package external

import org.w3c.dom.HTMLElement

external fun initTerminal(terminalParent: HTMLElement, userName: String): dynamic
external fun resizeTerminal()
external fun pasteTerminal(text: String)
external fun clearTerminal()
external fun focusTerminal()
external fun toDarkModeTerminal(terminal: dynamic, isDarkMode: Boolean)
external fun hoe(ff:(Int) -> Unit)