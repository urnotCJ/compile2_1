package miniplc0java.tokenizer;

public enum TokenType {
    /** 空 */
    None,
    /**fn  */
    Fn,
    /**let*/
    Let,
    /** Const */
    Const,
    /** as */
    As,
    /** while */
    While,
    /** if */
    If,
    /** Else */
    Else,
    /**return  */
    Return,
    /** break */
    Break,
    /**continue  */
    Continue,
    /** 无符号整数 */
    Uint,
    /** double */
    Double,
    /** string*/
    String,
    /** char */
    Char,
    /** 标识符 */
    Ident,


    /** 加号 */
    Plus,
    /** 减号 */
    Minus,
    /** 乘号 */
    Mul,
    /** 除号 */
    Div,
    /** = */
    Assign,
    /** == */
    Eq,
    /** != */
    Neq,
    /** < */
    Lt,
    /** Rt */
    Gt,
    /** Le */
    Le,
    /** Ge */
    Ge,
    /** 左括号 */
    LParen,
    /** 右括号 */
    RParen,
    /** 左括号{ */
    LBrace,
    /** 右括号} */
    RBrace,
    /** -> */
    Arrow,
    /** , */
    Comma,
    /** :*/
    Colon,
    /** 分号 */
    Semicolon,
    /** 文件尾 */
    EOF;

    @Override
    public String toString() {
        switch (this) {
            case None:
                return "NullToken";
            case Fn:
                return "FunctionName";
            case Let:
                return "Let";
            case Const:
                return "Const";
            case As:
                return "As";
            case While:
                return "While";
            case If:
                return "If";
            case Else:
                return "Else";
            case Return:
                return "Return";
            case Break:
                return "Break";
            case Continue:
                return "Continue";
            case Uint:
                return "Uint";
            case Double:
                return "Double";
            case String:
                return "String";
            case Char:
                return "Char";
            case Ident:
                return "Identifier";
            case Plus:
                return "PlusSign";
            case Minus:
                return "MinusSign";
            case Mul:
                return "MultiSign";
            case Div:
                return "DivisionSign";
            case Assign:
                return "Assign";
            case Eq:
                return "Equal";
            case Neq:
                return "NoEqual";
            case Lt:
                return "LessThan";
            case Gt:
                return "GianterThan";
            case Le:
                return "LessEqual";
            case Ge:
                return "GianterEqual";
            case LParen:
                return "LeftBracket";
            case RParen:
                return "RightBracket";
            case LBrace:
                return "LBrace";
            case RBrace:
                return "RBrace";
            case Arrow:
                return "Arrow";
            case Comma:
                return "Comma";
            case Colon:
                return "Colon";
            case Semicolon:
                return "Semicolon";
            case EOF:
                return "EOF";
            default:
                return "InvalidToken";
        }
    }
}
