import type {IPty} from "node-pty";
import {Socket} from "socket.io";

export type ProcessInfoType = {
    userName: string
    ptyProcess: IPty
    socket: Socket
}

export let processInfoList: ProcessInfoType[] = []




export function pInfoFindByUser(userName: string):ProcessInfoType | undefined {
    return processInfoList.find((info) =>
        info.userName === userName)
}

export function pInfoFindBySocket(socket: Socket):ProcessInfoType | undefined {
    return processInfoList.find((info) =>
        info.socket === socket)
}

export function pInfoFindByPty(pty: IPty):ProcessInfoType | undefined {
    return processInfoList.find((info) =>
        info.ptyProcess === pty)
}

export function pInfoAdd(info: ProcessInfoType) {
    processInfoList.push()
}

