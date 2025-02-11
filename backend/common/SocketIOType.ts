type TerminalSizeType = {
    userName: string,
    cols: number,
    rows: number,
}

export type ServerToClientEvents = {
    tty: (message: string) => void;
};

/**
 * イベント受信時に使用する型定義
 */
export type ClientToServerEvents = {
    init: (message: TerminalSizeType) => void;
    resize: (message: TerminalSizeType) => void;
    tty: (message: string) => void;
};