import dev.fritz2.core.CollectionLensGetException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

data class Point(val x: Int, val y: Int)

inline fun jsObject(init: dynamic.() -> Unit): dynamic {
    val o = js("{}")
    init(o)
    return o
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <V> Flow<V>.exec(
    parentJob: Job? = null,
    content: suspend (V) -> Unit
) {
    val upstream = this
    (MainScope() + Job(parentJob)).launch(start = CoroutineStart.UNDISPATCHED) {
        upstream.distinctUntilChanged().mapLatest { content(it);it }.catch {
            when (it) {
                is CollectionLensGetException -> {}
                else -> console.error(it)
            }
            // do not do anything here but canceling the coroutine, because this is an expected
            // behaviour when dealing with filtering, renderEach and idProvider
            cancel("error exec", it)
        }.collect()
    }
}

/*
fun <T : Element> Tag<T>.afterMountElem(
    payload: Any? = null,
    handler: suspend (WithDomNode<Element>, Any?) -> Unit,
): Tag<T> {
    this.afterMount(payload, handler)
    return this
}

fun String.joinWithSpace(vararg strs: String): String {
    val startStr = this.trimEnd()
    val trimStrs = strs.joinToString(" ") { it.trim() }
    return "$startStr $trimStrs"
}
 */

const val titleBarClass = "text-gray-900 dark:text-white  bg-zinc-100 dark:bg-zinc-800 align-text-bottom"
const val buttonClass = "text-gray-500 bg-white border border-gray-300 focus:outline-none " +
        "hover:bg-gray-200 hover:text-black " +
        "focus:ring-4 focus:ring-gray-100 font-medium rounded-lg text-sm px-2 py-1 ms-2 " +
        "dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 " +
        "dark:hover:text-white dark:hover:bg-gray-700 dark:hover:border-gray-600 " +
        "dark:focus:ring-gray-700"
const val pasteButtonClass = "text-gray-500 bg-white border border-gray-300 focus:outline-none " +
        "hover:bg-gray-200 hover:text-black " +
        "focus:ring-4 focus:ring-gray-100 font-medium rounded-l-lg text-sm px-2 py-1 ms-2 " +
        "dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 " +
        "dark:hover:text-white dark:hover:bg-gray-700 dark:hover:border-gray-600 " +
        "dark:focus:ring-gray-700"
const val enterButtonClass = "text-gray-500 bg-white border border-gray-300 focus:outline-none " +
        "hover:bg-gray-200 hover:text-black " +
        "focus:ring-4 focus:ring-gray-100 font-medium rounded-r-lg text-sm px-2 py-1 " +
        "dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 " +
        "dark:hover:text-white dark:hover:bg-gray-700 dark:hover:border-gray-600 " +
        "dark:focus:ring-gray-700"
const val inputTextClass = "border border-gray-300 dark:border-gray-600 ps-1"