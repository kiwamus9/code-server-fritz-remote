import {EditorState} from "@codemirror/state"
import {EditorView} from "@codemirror/view"
import "bootstrap-icons/icons/play-fill.svg"
import "bootstrap-icons/icons/floppy2-fill.svg"
import "bootstrap-icons/icons/arrow-clockwise.svg"
import {coolGlow} from "thememirror"
import {cpp} from "@codemirror/lang-cpp"
import {basicSetup} from "codemirror";
// import path from "path";
// import type {FileTarget} from "../utils/FileEntry";
// import {scope} from "../utils/Scope";
// import {axiosClient} from "../main";
// import {isRunnableFile} from "../utils/Stores.svelte.js";

// export const hoee2 = "hook-editor";
//
// export function hoee() {
//     console.log("hoee")
//     return "hoee"
// }



export function createState(doc) {
    return EditorState.create({
        doc: doc, // 初期値を指定
        extensions: [
            //keymap.of(defaultKeymap),
            EditorView.baseTheme({"&": {height: "100%"}}),
            // lineNumbers(),
            // indentOnInput(),
            // bracketMatching(),
            // syntaxHighlighting(defaultHighlightStyle),
            basicSetup,
            cpp(),
            coolGlow
        ] // 拡張機能を追加
    })
}

export function createEditorView(editorPane, editorState)  {
    return new EditorView({
        state: editorState,
        parent: editorPane
    })
}

