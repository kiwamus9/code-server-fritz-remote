@file:JsModule("./js/terminal.js")
@file:JsNonModule

package external

import org.w3c.dom.HTMLElement

external class FitAddon {
    fun fit():Unit
}


external fun initTerminal(terminalParent: HTMLElement): Unit
external val fitAddon: FitAddon