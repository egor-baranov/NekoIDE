fun lStrip(input: String): String {
    if (input.isEmpty()) return ""
    var left: Int = 0
    for (i in input) {
        if (i in arrayListOf('\n', '\r', '\t', ' '))
            ++left
        else break
    }
    val c = 's'
    var ret: String = ""
    for (i in left until input.length) {
        ret += input[i]
    }
    return ret
}

fun rStrip(input: String): String {
    if (input.isEmpty()) return ""
    var right: Int = 0
    for (i in input.length - 1..0) {
        if (input[i] in arrayListOf('\n', '\r', '\t', ' '))
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

fun toString(input: ArrayList<Word>): String {
    var ret = "["
    for (i in 0 until input.size) {
        if (input[i].source == "\n")
            ret += "{${"\\n"}, ${input[i].index}}"
        else {
            ret += "{${input[i].source}, ${input[i].index}}"
        }
        if (i != input.size - 1) ret += ", "
    }
    ret += "]"
    return ret
}

class Word(s: String, i: Int) {
    val source = s
    val index = i

    fun isLeftBracket(): Boolean {
        return arrayListOf("(", "[", "{").contains(source)
    }

    fun isRightBracket(): Boolean {
        return arrayListOf(")", "]", "}").contains(source)
    }

    fun isBracket(): Boolean {
        return isLeftBracket() || isRightBracket()
    }

    fun toPair(): Pair<String, Int> {
        return Pair<String, Int>(source, index)
    }
}

fun toWord(pair: Pair<String, Int>): Word {
    return Word(pair.first, pair.second)
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

fun join(v: ArrayList<String>, sep: String = " "): String {
    var ret: String = ""
    for (i in 0 until v.size - 1) {
        ret += v[i] + sep
    }
    return ret + v[v.size - 1]
}

fun join(v: ArrayList<String>, sep: Char = ' '): String {
    return join(v, sep.toString())
}

fun isNumber(input: String): Boolean {
    if (input.isEmpty()) return false
    if (input[0] > '9' || input[0] < '0') {
        return false
    }
    var dots = 0
    for (i in 1 until input.length - 1) {
        if (input[i] == '.') {
            ++dots
            continue
        }
        if (input[i] > '9' || input[i] < '0') {
            return false
        }
    }
    val last = input[input.length - 1]
    if ((last > '9' || last < '0') && last != 'F' && last != 'f' && last != 'L' && last != 'l') {
        return false
    }
    return dots <= 1
}

fun formatted(input: String): String {
    var ret = ""
    for (i in input) {
        if (i == '\r') {
            continue
        }
        if (i == '\t') {
            ret += " "
            continue
        }
        ret += i
    }
    return ret
}

fun separate(input: String, sep: Set<String>): ArrayList<Word> {
    val ret = arrayListOf<Word>()
    var tmp: String = ""
    for (shift in input.indices) {
        if (sep.contains(input[shift].toString())) {
            if (tmp.isNotEmpty()) {
                for (i in split(tmp, setOf(' ', '\r')))
                    ret.add(Word(i.source, i.index + shift - tmp.length))
            }
            ret.add(Word(input[shift].toString(), shift))
            tmp = ""
        } else tmp += input[shift].toString()
    }
    if (tmp.isNotEmpty()) {
        for (i in split(tmp, setOf(' ', '\r')))
            ret.add(Word(i.source, i.index + input.length - tmp.length))
    }
    return ret
}

fun isLineCommented(input: String): Boolean {
    if (strip(input).length < 2) {
        return false
    }
    return strip(input).substring(0..1) == "//";
}

fun commented(input: String): String {
    return "//$input"
}

fun uncommented(input: String): String {
    val index = input.indexOf("//")
    return input.substring(0 until index) + input.substring(index + 2 until input.length)
}

fun isBracketPair(left: Word, right: Word): Boolean {
    return setOf("()", "{}", "[]").contains(left.source + right.source)
}

fun isLetter(c: Char): Boolean {
    return (c in 'a'..'z') || (c in 'A'..'Z')
}

fun isDigit(c: Char): Boolean {
    return c in '0'..'9'
}

fun isDigitOrLetter(c: Char): Boolean {
    return isDigit(c) or isLetter(c)
}

fun isCorrectName(input: String): Boolean {
    if (input.isEmpty()) return false
    if (!isLetter(input[0]) && input[0] != '_') {
        return false
    }
    for (c in input) {
        if (!isDigitOrLetter(c) && c != '_') {
            return false
        }
    }
    return true
}

// TODO: сделать так чтобы 2n подсвечивалось отдельно как 2 и отдельно как n
fun canBeDivided(word: Word): Boolean {
    var number = ""
    var name = ""
    var canBeNum = true
    for (i in word.source) {
        if (canBeNum && (i == '.' || isDigit(i))) {
            number += i
            continue
        }
        canBeNum = false
        name += i
    }
    return isNumber(number) && isCorrectName(name)
}