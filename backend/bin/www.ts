// #!/usr/bin/env node

/**
 * Module dependencies.
 */

import {app, io , httpServer} from '../app'
import Debug from 'debug'

const debug = Debug('app')
//const HOSTNAME = 'localhost'
/**
 * Get port from environment and store in Express.
 */

const port = normalizePort(process.env.PORT || '3001');
app.set('port', port);

/**
 * Create HTTP server.
 */
// move to app.ts
//httpServer = http.createServer(app);

/**
 * Listen on provided port, on all network interfaces.
 */

io.attach(httpServer, {path: "/soft_prac/codeServer2/ws/"});
httpServer.listen(port);
httpServer.on('error', onError);
httpServer.on('listening', onListening);

/**
 * Normalize a port into a number, string, or false.
 */

function normalizePort(val: string) {
    const port = parseInt(val, 10);

    if (isNaN(port)) {
        // named pipe
        return val;
    }

    if (port >= 0) {
        // port number
        return port;
    }

    return false;
}

/**
 * Event listener for HTTP server "error" event.
 */

function onError(error: NodeJS.ErrnoException) {
    if (error.syscall !== 'listen') {
        throw error;
    }

    const bind = typeof port === 'string'
        ? 'Pipe ' + port
        : 'Port ' + port;

    // handle specific listen errors with friendly messages
    switch (error.code) {
        case 'EACCES':
            console.error(bind + ' requires elevated privileges');
            process.exit(1);
            break;
        case 'EADDRINUSE':
            console.error(bind + ' is already in use');
            process.exit(1);
            break;
        default:
            throw error;
    }
}

/**
 * Event listener for HTTP server "listening" event.
 */

function onListening() {
    const addr = httpServer.address()!!
    const bind = typeof addr === 'string'
        ? 'pipe ' + addr
        : 'port ' + addr.port;
    debug('Listening on ' + bind);
}
