package parts

import FileEntry

fun generateCommand(workSpacePath: String, fileEntry: FileEntry, commandLineParam: String): String {
    val lastComma = fileEntry.name.lastIndexOf(".")
    if (lastComma == -1) return ""

    val name = fileEntry.name.substring(0, lastComma)
    val ext = fileEntry.name.substring(lastComma + 1).lowercase()


    var realPath = fileEntry.path
    var realName = name
    val pathElems = fileEntry.path.split("/")
    val indexOfSrc = pathElems.indexOf("src")
    if (indexOfSrc != -1) { // src contain
        realPath = pathElems.take(indexOfSrc + 1).joinToString("/")
        if (pathElems.drop(indexOfSrc + 1).isNotEmpty()) {
            realName =
                pathElems.drop(indexOfSrc + 1).joinToString("/", postfix = "/") + realName
        }
    } else if ((pathElems.size >= 2) && (ext == "java")) {
        realPath = pathElems.first()
        realName = pathElems.drop(1).joinToString("/", postfix = "/") + realName
    }

    /*
    a/main.c -> "a","main.c"
    a/b/main.c -> "a/b","main.c" // C only
    a/src/main.c -> "a/src", "main.c"
    a/src/a/main.c -> "a/src, "a/main.c

    a/main.java -> "a","main.java"
    a/b/main.java -> "a","b/main.java" // Java only
    a/src/main.java -> "a/src", "main.java"
    a/src/a/main.java -> "a/src, "a/main.java
     */

//    console.log(fileEntry)
//    console.log(realPath, realName)

    return when (ext) {
        "c" -> ("cd $workSpacePath/$realPath " +
                "&& cc *.c -lm -o $name " +
                "&& ./$realName $commandLineParam\n")

        "java" -> {
            val realNameByComma = realName.replace("/", ".")
            ("cd $workSpacePath/$realPath " +
                    "&& javac $realName.java " +
                    "&& java $realNameByComma $commandLineParam\n")
        }

        else -> ""
    }
}