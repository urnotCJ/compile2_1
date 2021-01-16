package miniplc0java.tokenizer;

import miniplc0java.error.TokenizeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.util.Pos;

import java.util.regex.Pattern;

public class Tokenizer {

    private StringIter it;

    public Tokenizer(StringIter it) {
        this.it = it;
    }

    // 这里本来是想实现 Iterator<Token> 的，但是 Iterator 不允许抛异常，于是就这样了
    /**
     * 获取下一个 Token
     * 
     * @return
     * @throws TokenizeError 如果解析有异常则抛出
     */
    public Token nextToken() throws TokenizeError {
        it.readAll();

        // 跳过之前的所有空白字符
        skipSpaceCharacters();

        if (it.isEOF()) {
            return new Token(TokenType.EOF, "", it.currentPos(), it.currentPos());
        }

        char peek = it.peekChar();
        if (Character.isDigit(peek)) {
            return lexUInt();
        } else if (Character.isAlphabetic(peek)||peek=='_') {
            return lexIdentOrKeyword();
        } else if(peek=='"'){
            return lexString();
        }
        else if(peek=='\''){
            return lexChar();
        }
        else {
            return lexOperatorOrUnknown();
        }
    }

    private Token lexUInt() throws TokenizeError {
        // 请填空：
        // 直到查看下一个字符不是数字为止:
        // -- 前进一个字符，并存储这个字符
        //
        // 解析存储的字符串为无符号整数
        // 解析成功则返回无符号整数类型的token，否则返回编译错误
        //
        // Token 的 Value 应填写数字的值
        Pos s = it.currentPos();
        while(Character.isDigit(it.peekChar())||it.peekChar()=='.'||it.peekChar()=='e'
        ||it.peekChar()=='E'||it.peekChar()=='+'||it.peekChar()=='-'){
            char c = it.nextChar();
        }
        //char c = it.nextChar();
        Pos e = it.previousPos();
        String str = it.linesBuffer.get(s.row).substring(s.col,e.col+1);
        String doublea = "[0-9]+.[0-9]+([eE][-+]?[0-9]+)?";
        String inta = "[0-9]+";
        if(Pattern.matches(inta,str)){
            return new Token(TokenType.Uint,Integer.parseInt(str),it.previousPos(),it.currentPos());
        }
        else if(Pattern.matches(doublea,str)){
            return new Token(TokenType.Double,Double.valueOf(str),it.previousPos(),it.currentPos());
        }
        else{
            throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        }
    }

    private Token lexIdentOrKeyword() throws TokenizeError {
        // 请填空：
        // 直到查看下一个字符不是数字或字母为止:
        // -- 前进一个字符，并存储这个字符
        //
        // 尝试将存储的字符串解释为关键字
        // -- 如果是关键字，则返回关键字类型的 token
        // -- 否则，返回标识符
        //
        // Token 的 Value 应填写标识符或关键字的字符串
        Pos s = it.currentPos();
        while(Character.isDigit(it.peekChar())||Character.isAlphabetic(it.peekChar())||it.peekChar()=='_'){
            char c = it.nextChar();
        }
        //char c = it.nextChar();
        Pos e = it.previousPos();
        String str = it.linesBuffer.get(s.row).substring(s.col,e.col+1);
        if(str.equals("fn")){
            return new Token(TokenType.Fn,str,it.previousPos(),it.currentPos());
        }
        else if(str.equals("let")){
            return new Token(TokenType.Let,str,it.previousPos(),it.currentPos());
        }
        else if(str.equals("const")){
            return new Token(TokenType.Const,str,it.previousPos(),it.currentPos());
        }
        else if(str.equals("as")){
            return new Token(TokenType.As,str,it.previousPos(),it.currentPos());
        }
        else if(str.equals("while")){
            return new Token(TokenType.While,str,it.previousPos(),it.currentPos());
        }
        else if(str.equals("if")){
            return new Token(TokenType.If,str,it.previousPos(),it.currentPos());
        }
        else if(str.equals("else")){
            return new Token(TokenType.Else,str,it.previousPos(),it.currentPos());
        }
        else if(str.equals("return")){
            return new Token(TokenType.Return,str,it.previousPos(),it.currentPos());
        }
        else if(str.equals("break")){
            return new Token(TokenType.Break,str,it.previousPos(),it.currentPos());
        }
        else if(str.equals("continue")){
            return new Token(TokenType.Continue,str,it.previousPos(),it.currentPos());
        }
        else{
            return new Token(TokenType.Ident, str, it.previousPos(), it.currentPos());
        }
    }

    private Token lexOperatorOrUnknown() throws TokenizeError {
        switch (it.nextChar()) {
            case '+':
                return new Token(TokenType.Plus, '+', it.previousPos(), it.currentPos());
            case '-':
                // 填入返回语句
                if (it.peekChar() == '>') {
                    it.nextChar();
                    return new Token(TokenType.Arrow, "->",it.previousPos(), it.currentPos());
                }
                return new Token(TokenType.Minus, '-', it.previousPos(),  it.currentPos());
            case '*':
                // 填入返回语句
                return new Token(TokenType.Mul, '*',it.previousPos(), it.currentPos());

            case '/':
                // 填入返回语句
                if(it.peekChar() == '/'){
                    it.nextChar();
                    char a = it.nextChar();
                    while(a!='\n'){
                        a = it.nextChar();
                    }
                    return nextToken();
                }
                return new Token(TokenType.Div, '/', it.previousPos(), it.currentPos());
            // 填入更多状态和返回语句

            case '=':
                // 填入返回语句
                if (it.peekChar() == '=') {
                    it.nextChar();
                    return new Token(TokenType.Eq, "==", it.previousPos(), it.currentPos());
                }
                return new Token(TokenType.Assign, '=', it.previousPos(),it.currentPos());

            case '!':
                // 填入返回语句
                if (it.peekChar() == '=') {
                    it.nextChar();
                    return new Token(TokenType.Neq, "!=", it.previousPos(), it.currentPos());
                }
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
            case '<':
                // 填入返回语句
                if (it.peekChar() == '=') {
                    it.nextChar();
                    return new Token(TokenType.Le, "<=", it.previousPos(), it.currentPos());
                }
                return new Token(TokenType.Lt, '<', it.previousPos(), it.currentPos());
            case '>':
                // 填入返回语句
                if (it.peekChar() == '=') {
                    it.nextChar();
                    return new Token(TokenType.Ge, ">=", it.previousPos(), it.currentPos());
                }
                return new Token(TokenType.Gt, '>', it.previousPos(), it.currentPos());

            case '(':
                // 填入返回语句
                return new Token(TokenType.LParen, '(',it.previousPos(), it.currentPos());

            case ')':
                // 填入返回语句
                return new Token(TokenType.RParen, ')',it.previousPos(), it.currentPos());
            case '{':
                // 填入返回语句
                return new Token(TokenType.LBrace, '{',it.previousPos(), it.currentPos());
            case '}':
                // 填入返回语句
                return new Token(TokenType.RBrace, '}',it.previousPos(), it.currentPos());
            case ',':
                // 填入返回语句
                return new Token(TokenType.Comma, ',',it.previousPos(), it.currentPos());
            case ':':
                // 填入返回语句
                return new Token(TokenType.Colon, ':',it.previousPos(), it.currentPos());
            case ';':
                // 填入返回语句
                return new Token(TokenType.Semicolon, ';',it.previousPos(), it.currentPos());

            default:
                // 不认识这个输入，摸了
                throw new TokenizeError(ErrorCode.InvalidInput, it.currentPos());
        }
    }

    private Token lexString() throws TokenizeError{
        String str = "";
        int limit=20000;
        char a = it.nextChar();
        while(limit>0){
            limit--;
            a=it.nextChar();
            if(a=='\\'){
                a=it.nextChar();
                if(a=='\\'){
                    str+='\\';
                }
                else if(a=='n'){
                    str+='\n';
                }
                else if(a=='"'){
                    str+='"';
                }
                else if(a=='\''){
                    str+='\'';
                }
                else if(a=='r'){
                    str+='\r';
                }
                else if(a=='t'){
                    str+='\t';
                }
                else{
                    throw new TokenizeError(ErrorCode.InvalidInput, it.currentPos());
                }
            }
            else{
                if(a=='"'){
                    return new Token(TokenType.String, str, it.previousPos(), it.currentPos());
                }
                else{
                    str+=a;
                }
            }
        }
        throw new TokenizeError(ErrorCode.InvalidInput, it.currentPos());
    }

    private Token lexChar() throws TokenizeError{
        char a=it.nextChar();
        a=it.nextChar();
        if(a == '\''){
            throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        }
        else if(a=='\\'){
            a=it.nextChar();
            if(it.nextChar()=='\''){
                if(a == '\'')
                    return new Token(TokenType.Char, '\'', it.previousPos(), it.currentPos());
                else if(a == '"')
                    return new Token(TokenType.Char, '"', it.previousPos(), it.currentPos());
                else if(a == '\\')
                    return new Token(TokenType.Char, '\\', it.previousPos(), it.currentPos());
                else if(a == 'n')
                    return new Token(TokenType.Char, '\n', it.previousPos(), it.currentPos());
                else if(a == 'r')
                    return new Token(TokenType.Char, '\r', it.previousPos(), it.currentPos());
                else if(a == 't')
                    return new Token(TokenType.Char, '\t', it.previousPos(), it.currentPos());
            }
            else {
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
            }
        }
        else{
            if(it.nextChar()=='\''){
                return new Token(TokenType.Char, a, it.previousPos(), it.currentPos());
            }
            else{
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
            }
        }
        throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
    }

    private void skipSpaceCharacters() {
        while (!it.isEOF() && Character.isWhitespace(it.peekChar())) {
            it.nextChar();
        }
    }
}
