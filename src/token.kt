enum class TokenType {
    EOfF, EOL,
    Name, // пользовательские имена
    Keyword, // служебное ключевое слово: if, for, and, or
    Constant, // служебная константа: True, False, None. Строковые и численные литералы будут отправлены в undefined,
    Punctuation,
    IntNumber,
    FloatNumber,
    Variable,
    ClassName,
    Operation,
    None
}

class Token(t: TokenType, s: String) {
    val type: TokenType = t
    val source: String = s
}

val endOfLine = Token(TokenType.EOL, "\n")

fun getToken(s: String): Token {
    val input = strip(s)
    if (Punctuations.contains(input))
        return Token(TokenType.Punctuation, input)
    if (ObjectConstants.contains(input))
        return Token(TokenType.Constant, input)
    if (Keywords.contains(input))
        return Token(TokenType.Keyword, input)
    if (Operators.contains(input))
        return Token(TokenType.Operation, input)
    if(isNumber(input))
        return Token(TokenType.IntNumber, input)
    return Token(TokenType.Name, input)
}