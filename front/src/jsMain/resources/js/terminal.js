import {Terminal} from '@xterm/xterm'
import {FitAddon} from '@xterm/addon-fit'
import {io} from "socket.io-client"

let socket = io({path: "/codeServer2/ws/"})


let options = {
    cursorBlink: true, fontSize: 14,
    lineHeight: 1.2
}

let terminal = new Terminal(options)
export let fitAddon = new FitAddon()

let userName = "kiwamu"

// noinspection JSUnusedGlobalSymbols
export function initTerminal(terminalParent) {
    // window.addEventListener('resize', resize)
    terminal.loadAddon(fitAddon)
    terminal.open(terminalParent);
    terminal.resize(10, 10)
    fitAddon.fit()
    terminal.onData((data) => {
        socket?.emit('tty', data)
    })
    socket?.on('disconnect', () => {
            console.log('socket disconnect', new Date())
        }
    )
    socket?.on('connect', () => {
        console.log('socket connect', new Date())
        socket.emit('init', {
            userName: userName,
            cols: terminal.cols,
            rows: terminal.rows,
        })
    })
    socket?.on('connect', () => {
        console.log('socket connect', new Date())
    })
    socket?.on('tty', (data) => {
        terminal.write(data)
    })
    return terminal
}

// noinspection JSUnusedGlobalSymbols
export function resizeTerminal() {
    fitAddon.fit()
    socket?.emit("resize", {
        userName: userName, "cols": terminal.cols, "rows": terminal.rows
    })
}

// noinspection JSUnusedGlobalSymbols
export function pasteTerminal(text) {
    if (text !== "") {
        socket?.emit("tty", text)
    }
    terminal.focus()
}

// noinspection JSUnusedGlobalSymbols
export function clearTerminal() {
    terminal.clear()
    terminal.focus()
}

// noinspection JSUnusedGlobalSymbols
export function focusTerminal() {
    terminal.clear()
    terminal.focus()
}