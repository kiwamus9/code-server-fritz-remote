import {viteCommonjs} from '@originjs/vite-plugin-commonjs'
import commonjs from "@rollup/plugin-commonjs"
import {nodePolyfills} from "vite-plugin-node-polyfills"

/** @type {import('vite').UserConfig} */
export default {
    root: "",
    base: "$base",
    server: {
        cors: false,
        proxy: {
            "/codeServer2/data": {
                target: "http://localhost:3001",
                changeOrigin: true,
                secure: false,
            },
            "/codeServer2/ws": {
                target: "ws://localhost:3001",
                rewriteWsOrigin: true,
                secure: false,
                ws: true,
            },
        }
    },
    plugins: [
        viteCommonjs(),
        commonjs(),
    ],
    build: {
        target: "modules",
        modulePreload: true,
        outDir: "$outDir",
        emptyOutDir: true
    },
}

