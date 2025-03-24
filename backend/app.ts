import http from 'http'

import createError, {HttpError} from 'http-errors'
import express, {type Request, type Response, type NextFunction} from 'express';
import path from 'path'
import cookieParser from 'cookie-parser'
import logger from 'morgan'
import {fileURLToPath} from 'url'
import {Server} from 'socket.io';
import pty from "node-pty"
import indexRouter from './routes/index'
import dataRouter from './routes/data'
import type {ClientToServerEvents, ServerToClientEvents} from "./common/SocketIOType";

//express server
export const app = express()
export const httpServer = http.createServer(app)
//socket.io server
export const io = new Server<ClientToServerEvents, ServerToClientEvents>()

// @ts-ignore
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
export const __workspaces = path.join(__dirname, "workspaces")

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'twig');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({extended: false}));
app.use(cookieParser());

// routing
app.use('/codeServer2', indexRouter);
app.use('/codeServer2/data', dataRouter);
//svelte root
app.use("/codeServer2/body", express.static(path.join(__dirname, 'body')))


// catch 404 and forward to error handler
app.use(function (_req: Request, _res: Response, next: NextFunction) {
    next(createError(404));
});

// error handler
app.use(function (err: HttpError, req: Request, res: Response, _next: NextFunction) {
    // set locals, only providing error in development
    res.locals.message = err.message;
    res.locals.error = req.app.get('env') === 'development' ? err : {};

    // render the error page
    res.status(err.status || 500);
    res.render('error');
});

// socket.io setting
io.on('connection', (socket) => {
    console.log('a user connected')
    socket.on('init', async (param) => {

        const sockets = await io.fetchSockets()
        let already = sockets.find(socket =>
            socket.data["userName"] === param.userName
        )
        console.log("al", !(already === undefined))




        socket.data["userName"] = param.userName
        let ptyProcess = pty.spawn('zsh', ['--login'], {
            name: 'xterm-256color',
            cols: param.cols,
            rows: param.rows,
            cwd: process.env.HOME,
            env: process.env,
        });
        socket.data["ptyProcess"] = ptyProcess
        ptyProcess.onData((data) => {
            io.to(socket.id).emit('tty', data)
        })
        console.log("pty", ptyProcess.pid, "user", param.userName, "cols", param.cols, "rows", param.rows)
    })
    socket.on('resize', (param) => {
        console.log('user resize')
        socket.data["ptyProcess"]?.resize(param.cols, param.rows)
    })
    socket.on('tty', (data) => {
        // console.log('user tty')
        socket.data["ptyProcess"].write(data)
    })
    socket.on('disconnect', () => {
        console.log('user disconnected')
        // const ps = socket.data["ptyProcess"]
        // console.dir(ptyProcess.pid)
        socket.data["ptyProcess"]?.kill("SIGKILL")
        // console.dir(ptyProcess)
    })
});

// process.on('SIGTERM', () => {
//     console.log('SIGTERM.')
//     httpServer.close(() => {
//         io.close().then(() => {
//             console.log('Socket.IO server terminated.')
//         })
//         console.log('HTTPServer terminated.')
//     })
// });
