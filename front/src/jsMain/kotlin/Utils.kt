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

const val titleBarClass = "text-gray-900 dark:text-white  bg-zinc-100 dark:bg-zinc-800 align-text-bottom"

val buttonColorDef = listOf(
    "px-2 text-sm font-medium text-center shadow-lg",
    "border border-blue-500",
    "text-gray-200 bg-blue-500 ",
    "hover:text-white hover:bg-blue-600 hover:shadow-blue-600/50",
    "active:bg-blue-700",
    "disabled:bg-blue-400 disabled:text-white",
    "disabled:hover:shadow-blue-600/50",
)
val buttonClass = (buttonColorDef + "py-1 my-1  ms-2 rounded-lg").joinToString(
    separator = " ",
    postfix = " ", prefix = " "
)

val pasteButtonClass = (buttonColorDef + "ms-2 rounded-l-lg").joinToString(
    separator = " ",
    postfix = " ", prefix = " "
)

val enterButtonClass = (buttonColorDef + "rounded-r-lg").joinToString(
    separator = " ",
    postfix = " ", prefix = " "
)
const val inputTextClass = "text-sm border border-gray-300 dark:border-gray-600 ps-1"
