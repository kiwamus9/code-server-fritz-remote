import {EditorState} from "@codemirror/state"
import {EditorView} from "@codemirror/view"
import "bootstrap-icons/icons/play-fill.svg"
import "bootstrap-icons/icons/floppy2-fill.svg"
import "bootstrap-icons/icons/arrow-clockwise.svg"
import {coolGlow} from "thememirror"
import {cpp} from "@codemirror/lang-cpp"
import {basicSetup} from "codemirror";

// noinspection JSUnusedGlobalSymbols
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

// noinspection JSUnusedGlobalSymbols
export function createEditorView(editorPane, editorState)  {
    return new EditorView({
        state: editorState,
        parent: editorPane
    })
}

// noinspection JSUnusedGlobalSymbols
export function updateEditorView(editorView, content) {
     const transaction =
        editorView.state.update({
            changes: {
                from: 0, to: editorView.state.doc.length,
                insert: content
            }
        })
    editorView.dispatch(transaction)
}
