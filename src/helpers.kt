import java.lang.Exception

fun lStrip(input: String): String {
    var left: Int = 0
    for (i in input) {
        if (i in arrayListOf<Char>('\n', '\r', '\t', ' '))
            ++left
        else break
    }
    var ret: String = ""
    for (i in left until input.length)
        ret += input[i]
    return ret
}

fun rStrip(input: String): String {
    var right: Int = 0
    for (i in input.length - 1..0) {
        if (input[i] in arrayListOf<Char>('\n', '\r', '\t', ' '))
            ++right
        else break
    }
    var ret: String = ""
    for (i in 0 until input.length - right)
        ret += input[i]
    return ret
}

fun strip(input: String): String {
    return lStrip(rStrip(input))
}

fun unite(s1: Set<String>, s2: Set<String>): Set<String> {
    val ret: MutableSet<String> = mutableSetOf()
    for (i in s1) ret.add(i)
    for (i in s2) ret.add(i)
    return ret
}

fun toString(input: Char): String = input.toString()

fun toString(input: ArrayList<Word>): String {
    var ret = "["
    for (i in 0 until input.size) {
        ret += "{${input[i].source}, ${input[i].index}}"
        if (i != input.size - 1) ret += ", "
    }
    ret += "]"
    return ret
}

class Word(s: String, i: Int) {
    val source = s
    val index = i
}

fun split(input: String, sep: Set<Char>): ArrayList<Word> {
    val ret = arrayListOf<Word>()
    var tmp: String = ""
    for (i in input.indices) {
        if (sep.contains(input[i])) {
            if (tmp.isNotEmpty())
                ret.add(Word(tmp, i - tmp.length))
            tmp = ""
            continue
        }
        tmp += input[i]
    }
    if (tmp.isNotEmpty())
        ret.add(Word(tmp, input.length - tmp.length))
    return ret;
}

fun isNumber(input: String): Boolean {
    if (input[0] > '9' || input[0] < '0') return false
    var dots: Int = 0
    for (i in 1 until input.length - 1) {
        if (input[i] == '.') {
            ++dots
            continue
        }
        if (input[i] > '9' || input[i] < '0') return false
    }
    if (input[input.length - 1] > '9' || input[input.length - 1] < '0') return false
    return dots <= 1
}

fun cleared(input: String): String {
    var ret = ""
    for (i in input) {
        if (i == '\r')  continue
        ret += i
    }
    return ret
}