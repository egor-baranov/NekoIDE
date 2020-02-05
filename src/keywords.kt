val builtInTypes: Array<String> = arrayOf(
    "int8", "int16", "int32", "int64",
    "uint8", "uint16", "uint32", "uint64",
    "char8", "char16", "char32", "uchar8", "uchar16", "uchar32",
    "float", "lfloat",
    "true", "false", "null", "string", "int", "char", "object"
)

val Keywords: Set<String> = setOf(
    "var", "val",
    "and", "or", "not", "xor", "in",
    "for", "while",
    "if", "else",
    "get", "set", "field", "value",
    "fun", "lambda", "return", "yield", "break", "continue",
    "class", "this", "as", "is", "new", "delete",
    "public", "private", "protected",
    "static", "sealed", "abstract",
    "from", "import",
    "thread", "ref", "out"
)

val Constants: Set<String> = setOf(
    "null", "true", "false"
)

val ObjectConstants: Set<String> = setOf(
    "None",
    "True", "False"
)

val Operators: Set<String> = setOf(
    "+", "-", "||", "*", "/",
    "&", "|",
    "&&", "%", "=", "<", ">",
    "==", "!=", "<=", ">=",
    "++", "--",
    "<<", ">>", "!", "=", "<<<", ">>>",
    "^", "~",
    "+=", "-=", "*=", "**=", "^=", "&=", "|=", "/=", "%="
)

val Punctuations: Set<String> = setOf(
    "{", "}",
    "[", "]",
    "(", ")",
    ",", ";", ".", ":",
    "\"", "\'", "\n"
)