val builtInTypes: Array<String> = arrayOf(
    "int8", "int16", "int32", "int64",
    "uint8", "uint16", "uint32", "uint64",
    "char8", "char16", "char32", "uchar8", "uchar16", "uchar32",
    "float", "lfloat"
)

val Keywords: Set<String> = setOf(
    "var", "val",
    "and", "or", "not", "in",
    "for", "while",
    "get", "set",
    "fun", "lambda", "return", "break", "continue",
    "this",
    "class", "as", "is", "new", "delete",
    "public", "private", "protected",
    "static", "sealed", "abstract"
)

val Constants: Set<String> = setOf(
    "None",
    "True", "False"
)

val Operators: Set<String> = setOf(
    "+", "-", "||", "*", "/",
    "&&", "%", "=", "<", ">",
    "==", "!=", "<=", ">=",
    "++", "--",
    "<<", ">>", "!", "="
)

val Punctuation: Set<String> = setOf(
    "{", "}",
    "[", "]",
    "(", ")",
    ",", ";", "."
)