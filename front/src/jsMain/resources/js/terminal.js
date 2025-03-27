import {Terminal} from '@xterm/xterm'
import {FitAddon} from '@xterm/addon-fit'
import {io} from "socket.io-client"

let socket = io({path: "/soft_prac/codeServer2/ws/"})


const darkModeTheme = {background: "black", foreground: "white", cursor: "white"};
const lightModeTheme = {background: "white", foreground: "black", cursor: "black"};

let options = {
    cursorBlink: true, fontSize: 14,
    lineHeight: 1.2, theme: darkModeTheme
}

let terminal = new Terminal(options)
export let fitAddon = new FitAddon()

let userName = "kiwamu"
let socketID

// noinspection JSUnusedGlobalSymbols :kotlinから呼び出す
export function initTerminal(terminalParent, userName, disconnectedFunc) {
    // window.addEventListener('resize', resize)
    terminal.loadAddon(fitAddon)
    terminal.open(terminalParent);
    terminal.resize(10, 10)
    fitAddon.fit()

    socket?.on('connect', () => {
        console.log('socket connect', new Date())
        socketID = socket.id
        socket.emit('init', {
            userName: userName,
            cols: terminal.cols,
            rows: terminal.rows,
        })
    })

    socket?.on('disconnect', (reason) => {
        disconnectedFunc(reason, socketID)
        console.log('socket disconnect', new Date())
    })

    socket?.on('tty', (data) => {
        terminal.write(data)
    })

    terminal.onData((data) => {
        socket?.emit('tty', data)
    })
    return terminal
}

// noinspection JSUnusedGlobalSymbols :kotlinから呼び出す
export function resizeTerminal() {
    fitAddon.fit()
    socket?.emit("resize", {
        userName: userName, "cols": terminal.cols, "rows": terminal.rows
    })
}

// noinspection JSUnusedGlobalSymbols :kotlinから呼び出す
export function pasteTerminal(text) {
    if (text !== "") {
        socket?.emit("tty", text)
    }
    terminal.focus()
}

// noinspection JSUnusedGlobalSymbols :kotlinから呼び出す
export function clearTerminal() {
    terminal.clear()
    terminal.focus()
}

// noinspection JSUnusedGlobalSymbols :kotlinから呼び出す
export function focusTerminal() {
    terminal.clear()
    terminal.focus()
}

// noinspection JSUnusedGlobalSymbols :kotlinから呼び出す
export function toDarkModeTerminal(terminal, isDarkMode) {
    if (isDarkMode) {
        terminal.options.theme = darkModeTheme
    } else {
        terminal.options.theme = lightModeTheme

    }
}
