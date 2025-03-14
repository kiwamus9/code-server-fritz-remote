@file:JsModule("./js/terminal.js")
@file:JsNonModule

package external

import org.w3c.dom.HTMLElement

external class FitAddon {
    fun fit():Unit
}

external fun initTerminal(terminalParent: HTMLElement): dynamic
external fun resizeTerminal()
external fun pasteTerminal(text: String)
external fun clearTerminal()
external val fitAddon: FitAddon