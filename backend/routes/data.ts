import express from 'express'
import {__workspaces} from "../app";
import path from "path"
import * as fs_p from "node:fs/promises"
import * as os from "node:os";
import * as fs from "node:fs";

const router = express.Router();

type FileQuery = { user: string, root: string, sub: string, name: string }
type FileQueryV2 = { userFullPathName: string }

/* workspace file  */
router.get('/workspace/file/v2',
    async (req,
           res,
           _next) => {
        const {userFullPathName} = req.query as FileQueryV2
        const filePath = path.join(__workspaces, userFullPathName)
        //console.log("fp", filePath)
        //res.json(req.params.userName +":"+req.params.fileDir + ":" + req.params.fileName);
        if( fs.existsSync( filePath ) ){
            res.status(200).sendFile(filePath)
        }else{
            res.status(404).send("File not found")
        }
    });

router.put('/workspace/file/v2',
    (req,
     res,
     _next) => {
        const {userFullPathName} = req.query as FileQueryV2
        const filePath = path.join(__workspaces, userFullPathName)

        console.log(req.body)
        //console.log("fp", filePath, req.body.join(os.EOL))

        fs_p.writeFile(filePath, req.body.join(os.EOL), "utf8").then(_ => {
            res.status(200).send("write success")
        }).catch(_ => {
            res.status(500).send("write failed")
        })
    });

router.get('/workspace/file/',
    async (req,
           res,
           _next) => {
        const {user, root, sub, name} = req.query as FileQuery
        const filePath = path.join(__workspaces, user, root, sub, name)
        //console.log("fp", filePath)
        //res.json(req.params.userName +":"+req.params.fileDir + ":" + req.params.fileName);
        res.sendFile(filePath)

    });

router.put('/workspace/file/',
    (req,
     res,
     _next) => {
        const {user, root, sub, name} = req.query as FileQuery
        const filePath = path.join(__workspaces, user, root, sub, name)
        console.log("fp", filePath, req.body.join(os.EOL))
        //res.json(req.params.userName +":"+req.params.fileDir + ":" + req.params.fileName);
        //res.sendFile(filePath)
        fs_p.writeFile(filePath, req.body.join(os.EOL), "utf8").then(_ => {
            res.status(200).send("write success")
        }).catch(_ => {
            res.status(500).send("write failed")
        })
    });

/* workspace file list */
router.get('/workspace/user/:userName',
    async (req,
           res,
           _next) => {
        const userWorkspacePath = path.join(__workspaces, req.params.userName)
        //const isExist =  await fs_p.stat(userWorkspacePath)
        fs_p.readdir(userWorkspacePath, {withFileTypes: true, recursive: true}).then(direntLists => {
            const fileLists = direntLists.map(dirent => {
                const relativePath = path.relative(userWorkspacePath, dirent.parentPath)
                return {name: dirent.name, path: relativePath, isDirectory: dirent.isDirectory()}
            })
            res.json({"workspace": userWorkspacePath, "fileLists": fileLists})
        }).catch(err => {
            res.status(404).send(err.message)
        })
    })


export default router;