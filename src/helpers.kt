fun lStrip(input: String): String {
    var left: Int = 0
    for (i in input) {
        if (i in arrayListOf<Char>('\n', '\r', '\t', ' '))
            ++left
        else break;
    }
    var ret: String = ""
    for (i in left until input.length)
        ret += input[i]
    return ret
}

fun rStrip(input: String): String {
    var right: Int = 0
    return ""
}