@file:JsModule("./js/terminal.js")
@file:JsNonModule

package external

import org.w3c.dom.HTMLElement

external fun initTerminal(@Suppress("unused") terminalParent: HTMLElement): dynamic
external fun resizeTerminal()
external fun pasteTerminal(@Suppress("unused")text: String)
external fun clearTerminal()
external fun focusTerminal()
external fun toDarkModeTerminal(terminal:dynamic, isDarkMode: Boolean)
