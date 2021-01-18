package miniplc0java.analyser;
import miniplc0java.tokenizer.TokenType;

public class Operator {
    //运算符优先矩阵
    public static int priority[][]={
            {1,1,-1,-1,-1,1,1,1,1,1,1,1,-1},
            {1,1,-1,-1,-1,1,1,1,1,1,1,1,-1},
            {1,1,1,1,-1,1,1,1,1,1,1,1,-1},
            {1,1,1,1,-1,1,1,1,1,1,1,1,-1},
            {-1,-1,-1,-1,-1,100,-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,0,0,-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,1,1,1,1,1,1,1,-1},
            {-1,-1,-1,-1,-1,1,1,1,1,1,1,1,-1},
            {-1,-1,-1,-1,-1,1,1,1,1,1,1,1,-1},
            {-1,-1,-1,-1,-1,1,1,1,1,1,1,1,-1},
            {-1,-1,-1,-1,-1,1,1,1,1,1,1,1,-1},
            {-1,-1,-1,-1,-1,1,1,1,1,1,1,1,-1},
            {1,1,1,1,-1,1,1,1,1,1,1,1,-1}
    };

    public static int getOrder(TokenType tokenType){
        if(tokenType== TokenType.Plus){
            return 0;
        }
        else if(tokenType== TokenType.Minus){
            return 1;
        }
        else if(tokenType== TokenType.Mul){
            return 2;
        }
        else if(tokenType== TokenType.Div){
            return 3;
        }
        else if(tokenType== TokenType.LParen){
            return 4;
        }
        else if(tokenType== TokenType.RParen){
            return 5;
        }
        else if(tokenType== TokenType.Lt){
            return 6;
        }
        else if(tokenType== TokenType.Gt){
            return 7;
        }
        else if(tokenType== TokenType.Le){
            return 8;
        }
        else if(tokenType== TokenType.Ge){
            return 9;
        }
        else if(tokenType== TokenType.Eq){
            return 10;
        }
        else if(tokenType== TokenType.Neq){
            return 11;
        }
        return -1;
    }

    public static int[][] getPriority() {
        return priority;
    }
}
