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
    var javaRealName = name
    var javaClassPath = "."
    var javaRealClass = name

    if (ext == "c") {
        if (indexOfSrc != -1) { // src contain
            realPath = pathElems.take(indexOfSrc + 1).joinToString("/")
            if (pathElems.drop(indexOfSrc + 1).isNotEmpty()) {
                realName =
                    pathElems.drop(indexOfSrc + 1).joinToString("/", postfix = "/") + realName
            }
        }
    } else if (ext == "java") {
        realPath = pathElems.first()
        if (indexOfSrc == -1) { //パスにsrcが入っていない
            //javaClassPath = "."
            //javaRealClass = name
            if (pathElems.size > 1) { //パッケージがある
                javaRealName = pathElems.drop(1).joinToString("/", postfix = "/") + realName
                javaRealClass = pathElems.drop(1).joinToString(".", postfix = ".") + realName
                console.log("realName", realName)
                console.log("javaRealClass", javaRealClass)
            }
        } else { //パスにsrcが入っている
            javaClassPath = "src"
            javaRealName = pathElems.drop(indexOfSrc).joinToString("/", postfix = "/") + realName
            val javaRealClassPrefix = pathElems.drop(indexOfSrc + 1).joinToString(".", postfix = ".")
            javaRealClass = if (javaRealClassPrefix == ".") realName else javaRealClassPrefix + realName
        }
    }

    /*
    a/main.c -> "a","main.c"
    a/b/main.c -> "a/b","main.c" // C only
    a/src/main.c -> "a/src", "main.c"
    a/src/a/main.c -> "a/src, "a/main.c

    a/main.java -> "a", "." ,"main.java", "main"
    a/b/main.java -> "a",".", "b/main.java", "b.main" // Java only
    a/src/main.java -> "a", "src", "src/main.java", "main"
    a/src/b/main.java -> "a", "src", "src/b/main.java", "b.main"
     */

//    console.log(fileEntry)
//    console.log(realPath, realName)

    return when (ext) {
        "c" -> ("cd $workSpacePath/$realPath " +
                "&& cc *.c -lm -o $name " +
                "&& ./$realName $commandLineParam\n")

        "java" -> ("cd $workSpacePath/$realPath " +
                "&& javac -cp $javaClassPath $javaRealName.java " +
                "&& java -cp $javaClassPath $javaRealClass $commandLineParam\n")
        else -> ""
    }
}