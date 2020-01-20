enum class TokenType {
    EOfF,
    EOL,
    Name, // пользовательские имена
    Keyword, // служебное ключевое слово: if, for, and, or
    Constant, // служебная константа: True, False, None. Строковые и численные литералы будут отправлены в undefined,
    Punctuation,
    Number,
    Operation,
    None
}

class Token(t: TokenType, s: String) {
    val type: TokenType = t
    val source: String = s
}

val endOfLine = Token(TokenType.EOL, "\n")

