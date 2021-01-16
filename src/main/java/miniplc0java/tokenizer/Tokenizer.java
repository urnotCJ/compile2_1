package miniplc0java.tokenizer;

import miniplc0java.error.TokenizeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.util.Pos;

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
        } else if (Character.isAlphabetic(peek)) {
            return lexIdentOrKeyword();
        } else {
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
        while(Character.isDigit(it.peekChar())){
            char c = it.nextChar();
        }
        //char c = it.nextChar();
        Pos e = it.previousPos();
        int ans;
        String str = it.linesBuffer.get(s.row).substring(s.col,e.col+1);
        try {
            ans = Integer.parseInt(str);
            return new Token(TokenType.Uint,ans,s,e);
        } catch (NumberFormatException err) {
            throw new Error("Not implemented");
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
        while(Character.isDigit(it.peekChar())||Character.isAlphabetic(it.peekChar())){
            char c = it.nextChar();
        }
        //char c = it.nextChar();
        Pos e = it.previousPos();
        String str = it.linesBuffer.get(s.row).substring(s.col,e.col+1);
        if(str.equals("const")){
            return new Token(TokenType.Const,"const",s,e);
        }
        else{
            return new Token(TokenType.Ident,str,s,e);
        }
    }

    private Token lexOperatorOrUnknown() throws TokenizeError {
        switch (it.nextChar()) {
            case '+':
                return new Token(TokenType.Plus, '+', it.currentPos(), it.currentPos());
            case '-':
                // 填入返回语句
                return new Token(TokenType.Minus, '-', it.currentPos(), it.currentPos());


            case '*':
                // 填入返回语句
                return new Token(TokenType.Mul, '*',it.currentPos(), it.currentPos());

            case '/':
                // 填入返回语句
                return new Token(TokenType.Div, '/', it.currentPos(), it.currentPos());
            // 填入更多状态和返回语句

            case '=':
                // 填入返回语句
                return new Token(TokenType.Eq, '=', it.currentPos(), it.currentPos());

            case ';':
                // 填入返回语句
                return new Token(TokenType.Semicolon, ';', it.currentPos(), it.currentPos());


            case '(':
                // 填入返回语句
                return new Token(TokenType.LParen, '(',it.currentPos(), it.currentPos());

            case ')':
                // 填入返回语句
                return new Token(TokenType.RParen, ')',it.currentPos(), it.currentPos());
            default:
                // 不认识这个输入，摸了
                throw new TokenizeError(ErrorCode.InvalidInput, it.currentPos());
        }
    }

    private void skipSpaceCharacters() {
        while (!it.isEOF() && Character.isWhitespace(it.peekChar())) {
            it.nextChar();
        }
    }
}
