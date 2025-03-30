import {Terminal} from '@xterm/xterm'
import {FitAddon} from '@xterm/addon-fit'
import {io} from "socket.io-client"

let socket
let socketID
let userName

const darkModeTheme = {background: "black", foreground: "white", cursor: "white"};
const lightModeTheme = {background: "white", foreground: "black", cursor: "black"};

let options = {
    cursorBlink: true, fontSize: 13,
    lineHeight: 1.2, theme: darkModeTheme
}

export let reloadFileListCallBack

let terminal = new Terminal(options)
export let fitAddon = new FitAddon()


// noinspection JSUnusedGlobalSymbols :kotlinから呼び出す
export function initTerminal(terminalParent, user_name,
                             disconnectedFunc,
                             fileListReloadFunc) {
    // window.addEventListener('resize', resize)

    userName = user_name
    socket = io({path: "/soft_prac/codeServer2/ws/"})
    let isReadyToWatchFile = false
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
        isReadyToWatchFile = false
        console.log('socket disconnect', new Date())
    })

    socket?.on('tty', (data) => {
        terminal.write(data)
    })

    socket?.on('changeFileList', (data) => {
        if (data === "ready") isReadyToWatchFile = true
        if (isReadyToWatchFile && (reloadFileListCallBack !== undefined)) {
            reloadFileListCallBack(data)
        }
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

// noinspection JSUnusedGlobalSymbols :kotlinから呼び出す
export function setReloadFileListCallBack(func) {
    reloadFileListCallBack = func
}

