import {Compartment, EditorState, StateField} from "@codemirror/state"
import {EditorView} from "@codemirror/view"
import "bootstrap-icons/icons/play-fill.svg"
import "bootstrap-icons/icons/floppy2-fill.svg"
import "bootstrap-icons/icons/arrow-clockwise.svg"
import {coolGlow, tomorrow} from "thememirror"

import {cpp} from "@codemirror/lang-cpp"
//import {java} from "@codemirror/lang-java"


const DARK_THEME = coolGlow
const LIGHT_THEME = tomorrow

let language = new Compartment
let theme = new Compartment

export let baseDoc = ""

// Define StateField
const listenChangesExtension = StateField.define({
    // we won't use the actual StateField value, null or undefined is fine
    create: () => null,
    update: (value, transaction) => {
        if (transaction.docChanged) {
            // access new content via the Transaction
            console.log(transaction.newDoc.eq(baseDoc));
        }
        return null;
    },
});


// noinspection JSUnusedGlobalSymbols :kotlinから呼び出す
export function createState(doc) {
    return EditorState.create({
        doc: doc, // 初期値を指定
        extensions: [
            EditorView.baseTheme({"&": {height: "100%"}}),
            listenChangesExtension,
            language.of(cpp()), //javaもこれでいい
            theme.of(coolGlow) // defaultはdark mode
        ] // 拡張機能を追加
    })
}

// noinspection JSUnusedGlobalSymbols :kotlinから呼び出す
export function createEditorView(editorPane, editorState) {
    return new EditorView({
        state: editorState,
        parent: editorPane
    })
}

// noinspection JSUnusedGlobalSymbols :kotlinから呼び出す
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

// noinspection JSUnusedGlobalSymbols :kotlinから呼び出す
export function toDarkMode(view, isDarkMode) {
    if (isDarkMode) {
        view.dispatch({
            effects: theme.reconfigure(DARK_THEME)
        })
    } else {
        view.dispatch({
            effects: theme.reconfigure(LIGHT_THEME)
        })
    }
}


//baseDoc.eq(state.doc)