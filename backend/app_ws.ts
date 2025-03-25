import createError, {HttpError} from 'http-errors'
import express, {type Request, type Response, type NextFunction} from 'express';
import path from 'path'
import cookieParser from 'cookie-parser'
import logger from 'morgan'
import {fileURLToPath} from 'url'
import type {IPty} from "node-pty"
import indexRouter from './routes/index'
import  dataRouter from './routes/data'
import expressWs from "express-ws"

//express server
const application = express()
//socket.io server
//const io = new Server<ClientToServerEvents, ServerToClientEvents>()

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
export const __workspaces = path.join(__dirname, "workspaces")


export const app = expressWs(application).app

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'twig');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({extended: false}));
app.use(cookieParser());
//app.use("/oo", express.static(path.join(__dirname, 'public')));

app.use('/codeServer2', indexRouter);
app.use('/codeServer2/data', dataRouter);
//svelte root
app.use("/codeServer2/body", express.static(path.join(__dirname, 'body')))


app.get("/codeServer2/hoee2", (_req: Request, res: Response) => {
    res.render("index", {title: "kiwamu"})
})
app.ws('/codeServer2/guu', function(ws, req) {
    ws.on("connection", function(ws) {
        console.log("connection");
        ws.send("hello");
    })
    ws.on('message', function(msg) {
        console.log(msg);
    });
    console.log('socket', req);
});

// catch 404 and forward to error handler
app.use(function (req: Request, res: Response, next: NextFunction) {
    next(createError(404));
});

// error handler
app.use(function (err: HttpError, req: Request, res: Response, _next: NextFunction) {
    // set locals, only providing error in development
    res.locals.message = err.message;
    res.locals.error = req.app.get('env') === 'development' ? err : {};

    // render the error page
    res.status(err.status || 500);
    res.send('error');
});

app.listen(4000);

// このファイルは使っていない


// export {app, __workspaces}