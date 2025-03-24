import {Compartment, EditorState, StateField} from "@codemirror/state"
import {EditorView} from "@codemirror/view"
import "bootstrap-icons/icons/play-fill.svg"
import "bootstrap-icons/icons/floppy2-fill.svg"
import "bootstrap-icons/icons/arrow-clockwise.svg"
import {coolGlow, tomorrow} from "thememirror"
import {basicSetup} from "codemirror";
import {cpp} from "@codemirror/lang-cpp"
//import {java} from "@codemirror/lang-java"

const DARK_THEME = coolGlow
const LIGHT_THEME = tomorrow

let language = new Compartment
let theme = new Compartment

export let baseDoc = ""
let isChanged = false
let parentView = null

// Define StateField
const listenChangesExtension = StateField.define({
    // we won't use the actual StateField value, null or undefined is fine
    create: () => null,
    update: (value, transaction) => {
        if (transaction.docChanged) {
            if(transaction.newDoc.eq(baseDoc) === isChanged) {
                isChanged = !isChanged
                parentView.dispatchEvent(
                    new CustomEvent("editorStat", {
                        detail: {
                            isChanged: isChanged,
                        },
                    })
                )
            }
        }
        return null;
    },
});

// noinspection JSUnusedGlobalSymbols :kotlinから呼び出す
export function resetDocChanged(doc) {
    baseDoc = doc
    isChanged = false
}

// noinspection JSUnusedGlobalSymbols :kotlinから呼び出す
export function createState(doc) {
    resetDocChanged(doc)
    return EditorState.create({
        doc: doc, // 初期値を指定
        extensions: [
            EditorView.baseTheme({"&": {height: "100%"}}),
            basicSetup,
            listenChangesExtension,
            language.of(cpp()), //javaもこれでいい
            theme.of(coolGlow) // defaultはdark mode
        ] // 拡張機能を追加
    })
}

// noinspection JSUnusedGlobalSymbols :kotlinから呼び出す
export function createEditorView(editorPane, editorState) {
    parentView = editorPane
    return new EditorView({
        state: editorState,
        parent: editorPane
    })
}

// noinspection JSUnusedGlobalSymbols :kotlinから呼び出す
export function updateEditorView(editorView, content) {
    resetDocChanged(content)
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
