import {Terminal} from '@xterm/xterm'
import {FitAddon} from '@xterm/addon-fit'
import {io, Socket} from "socket.io-client"
import {ITerminalOptions} from "@xterm/xterm";


let socket = io({path: "/codeServer2/ws/"})


let options= {
    cursorBlink: true, fontSize: 14,
    lineHeight: 1.2
}

let terminal= new Terminal(options)
let fitAddon= new FitAddon()

let userName = "kiwamu"

export function initTerminal(terminalParent) {
    window.addEventListener('resize', resize)
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
}

function resize() {
    fitAddon.fit()
    socket?.emit("resize", {
        userName: userName, "cols": terminal.cols, "rows": terminal.rows
})
}