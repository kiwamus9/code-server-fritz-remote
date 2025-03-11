package external

import org.w3c.dom.DOMRectReadOnly
import org.w3c.dom.MutationObserver
import org.w3c.dom.Node


interface SizeRefer {
    val blockSize: Int
    val inlineSize: Int
}
external interface ResizeObserverEntry{
    val borderBoxSize: SizeRefer
    // 読取専用
    // コールバックが実行されたときに監視された要素の新しい境界ボックスサイズを含むオブジェクトの配列です。

    val contentBoxSize: SizeRefer
    // 読取専用
    // コールバックが実行されたときに監視された要素の新しいコンテンツボックスサイズを含むオブジェクトの配列です。

    val devicePixelContentBoxSize: SizeRefer
    // 読取専用
    // コールバック実行時に監視される要素の新しいコンテンツボックスサイズをデバイスピクセル単位で含むオブジェクトの配列です。

    val contentRect: DOMRectReadOnly
    // 読取専用
    // コールバックが実行されたときに監視された要素の新しいサイズを含む DOMRectReadOnly オブジェクトです。これは、上記の 2 つのプロパティよりも対応されていますが、リサイズオブザーバー API の以前の実装から残ったものであり、ウェブの互換性のために仕様に含まれているため、将来のバージョンで非推奨となる可能性があることに注意してください。

    val target: Node
    //読取専用
    //監視対象の Element または SVGElement オブジェクト。
}

external class ResizeObserver(callback: (Array<ResizeObserverEntry>, ResizeObserver) -> Unit) {
    fun observe(target: Node, options: SizeObserverInit = definedExternally)
    fun unobserve(target: Node)
    fun disconnect()
}

external interface SizeObserverInit {
    var box: String? /* = false */
        get() = definedExternally
        set(value) = definedExternally
}