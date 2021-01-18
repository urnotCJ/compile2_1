package miniplc0java.analyser;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.CompileError;
import miniplc0java.error.ErrorCode;
import miniplc0java.error.ExpectedTokenError;
import miniplc0java.error.TokenizeError;
import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;
import miniplc0java.util.Pos;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;

import java.security.PrivateKey;
import java.util.*;

public final class Analyser {

    Tokenizer tokenizer;
    ArrayList<Instruction> initInstructions;
    ArrayList<Instruction> instructions = new ArrayList<>();

    /** 当前偷看的 token */
    Token peekedToken = null;

    /** 符号表 */
    ArrayList<SymbolEntry> symbols = new ArrayList<>();
    ArrayList<SymbolDecl> decls = new ArrayList<>();
    ArrayList<Func> funcs = new ArrayList<>();

    Func start;
    int level=1;


    /** 下一个变量的栈偏移 */
    int nextOffset = 0;

    int declsnum;
    int localsnum;
    int funcsnum;

    Stack<TokenType> operator = new Stack<>();


    public Analyser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public void analyse() throws CompileError {
        analyseProgram();
    }

    /**
     * 查看下一个 Token
     * 
     * @return
     * @throws TokenizeError
     */
    private Token peek() throws TokenizeError {
        if (peekedToken == null) {
            peekedToken = tokenizer.nextToken();
        }
        return peekedToken;
    }

    /**
     * 获取下一个 Token
     * 
     * @return
     * @throws TokenizeError
     */
    private Token next() throws TokenizeError {
        if (peekedToken != null) {
            var token = peekedToken;
            peekedToken = null;
            return token;
        } else {
            return tokenizer.nextToken();
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则返回 true
     * 
     * @param tt
     * @return
     * @throws TokenizeError
     */
    private boolean check(TokenType tt) throws TokenizeError {
        var token = peek();
        return token.getTokenType() == tt;
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回这个 token
     * 
     * @param tt 类型
     * @return 如果匹配则返回这个 token，否则返回 null
     * @throws TokenizeError
     */
    private Token nextIf(TokenType tt) throws TokenizeError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            return null;
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回，否则抛出异常
     * 
     * @param tt 类型
     * @return 这个 token
     * @throws CompileError 如果类型不匹配
     */
    private Token expect(TokenType tt) throws CompileError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            throw new ExpectedTokenError(tt, token);
        }
    }

    /**
     * 获取下一个变量的栈偏移
     * 
     * @return
     */
    private int getNextVariableOffset() {
        return this.nextOffset++;
    }

    /**
     * 添加一个符号
     * 
     * @param name          名字
     * @param isInitialized 是否已赋值
     * @param isConstant    是否是常量
     * @param curPos        当前 token 的位置（报错用）
     * @throws AnalyzeError 如果重复定义了则抛异常
     */
    private void addSymbol(String name,boolean isConstant, String type, boolean isInitialized, int level, ArrayList<SymbolEntry> params, String returns, int paramlocation, SymbolEntry func, int part, int global,Pos curPos) throws AnalyzeError {
        int a = findSymbol(name);
        if(a==-1){
            this.symbols.add(new SymbolEntry(name,isConstant,type,isInitialized,getNextVariableOffset(),level,params,returns,paramlocation,func,part,global));
        }
        else {
           if(symbols.get(a).getLevel()==level){
               throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
           }
           else{
               this.symbols.add(new SymbolEntry(name,isConstant,type,isInitialized,getNextVariableOffset(),level,params,returns,paramlocation,func,part,global));
           }
        }
    }

    private int findSymbol(String name){
        for(int i=0;i<symbols.size();i++){
            if(symbols.get(i).getName().equals(name)){
                return i;
            }
        }
        return -1;
    }

    /**
     * 设置符号为已赋值
     * 
     * @param name   符号名称
     * @param curPos 当前位置（报错用）
     * @throws AnalyzeError 如果未定义则抛异常
     */
    private void initializeSymbol(String name, Pos curPos) throws AnalyzeError {
        int a = findSymbol(name);
        if(a==-1){
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        }
        else {
            symbols.get(a).setInitialized(true);
        }
    }



    private void analyseProgram() throws CompileError {
        // 程序 -> 'begin' 主过程 'end'
        // 示例函数，示例如何调用子程序
        // 'begin'
        while(check(TokenType.Let)||check(TokenType.Const)){
            analyseDeclStmt();
        }
        initInstructions=instructions;

        // 'end'
        while(check(TokenType.Fn)){
            instructions = new ArrayList<>();
            analyseFunction();
            declsnum++;
            funcsnum++;
        }
        if(findSymbol("main")==-1){
            throw new Error("");
        }
        decls.add(new SymbolDecl("_start",1,6));
        if(symbols.get(findSymbol("main")).getReturns()=="void"){
            initInstructions.add(new Instruction("stackalloc", 0));
            initInstructions.add(new Instruction("call", funcsnum-1));
        }
        else{
            initInstructions.add(new Instruction("stackalloc", 1));
            initInstructions.add(new Instruction("call", funcsnum-1));
            initInstructions.add(new Instruction("popn", 1));
        }
        start = new Func("_start",0,0,declsnum,0,initInstructions,level,"void");
        declsnum++;
    }

    public void analyseDeclStmt() throws CompileError{
        if(check(TokenType.Let))
            analyseLetDeclStmt();
        else if(check(TokenType.Const))
            analyseConstDeclStmt();
        if(level==1){declsnum++;}
        else{localsnum++;}
    }

    private void analyseLetDeclStmt() throws CompileError{
        String name;
        String ty;
        boolean initialized = false;
        ArrayList<SymbolEntry> params = null;
        expect(TokenType.Let);
        String ety = "";
        name = expect(TokenType.Ident).getValueString();

        expect(TokenType.Colon);
        ty = analyseTy();
        if(ty.equals("void")){
            throw new Error("letdecl wrong");
        }
        if(check(TokenType.Assign)){
            initialized=true;
            if (level == 1) {
                Instruction instruction = new Instruction("globa", declsnum);
                instructions.add(instruction);
            }
            else {
                Instruction instruction = new Instruction("loca", localsnum);
                instructions.add(instruction);
            }
            next();
            ety = analyseExpr();
            while (operator.empty()!=true){
                opinstr(operator.pop(),instructions,ety);
            }
            instructions.add(new Instruction("store.64", null));
        }
        if(initialized&&ty.equals(ety)||!initialized){
            if(level==1){
                addSymbol(name,false,ty,initialized,level,params,"",-1,null,-1,declsnum,peek().getStartPos());
            }
            else {
                addSymbol(name,false,ty,initialized,level,params,"",-1,null,localsnum,-1,peek().getStartPos());
            }
        }
        else
            throw new Error("wrong");
        expect(TokenType.Semicolon);
        if (level==1)
            decls.add(new SymbolDecl(null,0,0));
    }
    private void analyseFunction(){

    }

    private void analyseConstDeclStmt() throws CompileError{
        String name;
        String ty;
        boolean initialized = true;
        ArrayList<SymbolEntry> params = null;
        expect(TokenType.Const);
        String ety = "";
        name = expect(TokenType.Ident).getValueString();

        expect(TokenType.Colon);
        ty = analyseTy();
        if(ty.equals("void")){
            throw new Error("letdecl wrong");
        }
        expect(TokenType.Assign);
        if (level == 1) {
            instructions.add(new Instruction("globa", declsnum));
        }
        else {
            instructions.add(new Instruction("loca", localsnum));
        }
        ety = analyseExpr();
        while (operator.empty()!=true){
            opinstr(operator.pop(),instructions,ety);
        }
        instructions.add(new Instruction("store.64", null));
        if(ty.equals(ety)){
            if(level==1){
                addSymbol(name,true,ty,initialized,level,params,"",-1,null,-1,declsnum,peek().getStartPos());
            }
            else {
                addSymbol(name,true,ty,initialized,level,params,"",-1,null,localsnum,-1,peek().getStartPos());
            }
        }
        else
            throw new Error("wrong");
        expect(TokenType.Semicolon);
        if (level==1)
            decls.add(new SymbolDecl(null,1,0));
    }

    private String analyseTy() throws CompileError {
        Token a = peek();
        if(a.getValue().equals("int")||a.getValue().equals("double")||a.getValue().equals("void")){
            next();
            return a.getValueString();
        }
        else{
            throw new Error("get ty wrong");
        }
    }

    private String analyseExpr() throws CompileError{
        String ety = "";
        if(check(TokenType.Ident)){
            Token id = next();
            String name = id.getValueString();
            int a = findSymbol(name);
            SymbolEntry s = null;
            if(a!=-1){
                s = symbols.get(a);
            }
            boolean library = false;
            if(a==-1){
                s = getlibrary(name);
                if(s==null){
                    throw new Error("");
                }
                library = true;
            }
            if(check(TokenType.LParen)){
                ety = analyseCallExpr(s,id,library);
            }
            else if(check(TokenType.Assign)){
                ety = analyseAssignExpr(s,id);
            }
            else{
                analyseIdentExpr(s,id);
            }
        }
        else if(check(TokenType.Minus)){
            ety = analyseNegateExpr();
        }
        else if(check(TokenType.Uint)||check(TokenType.Double)||check(TokenType.Char)||check(TokenType.String)){
            ety = analyseLiteralExpr();
        }
        else if(check(TokenType.LParen)){
            ety = analyseGroupExpr();
        }
        while(check(TokenType.Mul)|| check(TokenType.Div)|| check(TokenType.Eq)||check(TokenType.Neq)||
                check(TokenType.Lt)|| check(TokenType.Gt)|| check(TokenType.Le)|| check(TokenType.Ge)||
                check(TokenType.Plus)|| check(TokenType.Minus)||check(TokenType.As)){
            if(check(TokenType.As)){
                ety = analyseAsExpr(ety);
            }
            else{
                ety = analyseOperatorExpr(ety);
            }
        }
        if(ety.equals("")!=true) {
            return ety;
        }
        else throw new Error("");
    }

    private void analyseCallParamList(SymbolEntry s) throws CompileError{
        int i = 0;
        ArrayList<SymbolEntry> params = s.getParams();
        int num = params.size();

        String ety = analyseExpr();
        while (!operator.empty() && operator.peek() != TokenType.LParen)
            opinstr(operator.pop(), instructions, ety);

        if(!params.get(i).getType().equals(ety))
            throw new Error("");
        i++;

        while(check(TokenType.Comma)){
            next();
            ety = analyseExpr();
            while (!operator.empty() && operator.peek() != TokenType.LParen)
                opinstr(operator.pop(), instructions, ety);

            if(!params.get(i).getType().equals(ety))
                throw new Error("") ;
            while (!operator.empty() && operator.peek() != TokenType.LParen)
                opinstr(operator.pop(), instructions, ety);
            i++;
        }
        if(i != num)
            throw new Error("");
    }

    private String analyseCallExpr(SymbolEntry s, Token id, boolean library) throws CompileError{
        Instruction instruction;

        if(library){
            String name = s.getName();
            decls.add(new SymbolDecl(name,1, name.length() ));
            instruction = new Instruction("callname", declsnum);
            declsnum++;
        }

        else{
            if(!s.getType().equals("function"))
                throw new Error("");
            int d = getFunc(s.getName());
            instruction = new Instruction("call", d + 1);
        }

        String name = s.getName();
        expect(TokenType.LParen);
        operator.push(TokenType.LParen);

        if (checkreturn(name))
            instructions.add(new Instruction("stackalloc", 1));
        else
            instructions.add(new Instruction("stackalloc", 0));

        if(!check(TokenType.RParen)){
            analyseCallParamList(s);
        }
        expect(TokenType.RParen);
        operator.pop();
        instructions.add(instruction);
        return s.getReturns();
    }

    private String analyseIdentExpr(SymbolEntry s, Token id) throws CompileError{
        if(!s.getType().equals("int") && !s.getType().equals("double"))
            throw new Error("");

        if (s.getParamlocation() != -1) {
            SymbolEntry func = s.getFunc();
            if (func.getReturns().equals("int"))
                instructions.add(new Instruction("arga", 1 + s.getParamlocation()));
            else if(func.getReturns().equals("double"))
                instructions.add(new Instruction("arga", 2 + s.getParamlocation()));
            else
                instructions.add(new Instruction("arga", s.getParamlocation()));
        }
        else if(s.getParamlocation() == -1 && s.getLevel() != 1) {
            instructions.add(new Instruction("loca", s.getPart()));
        }
        else {
            instructions.add(new Instruction("globa", s.getGlobal()));
        }
        instructions.add(new Instruction("load.64", null));
        return s.getType();
    }

    private String analyseAssignExpr(SymbolEntry s, Token id) throws CompileError{
        if (s.getParamlocation() != -1) {
            SymbolEntry func = s.getFunc();

            if (func.getReturns().equals("int"))
                instructions.add(new Instruction("arga", 1 + s.getParamlocation()));
            else if(func.getReturns().equals("double"))
                instructions.add(new Instruction("arga", 2 + s.getParamlocation()));
            else
                instructions.add(new Instruction("arga", s.getParamlocation()));
        }
        else if(s.getParamlocation() == -1 && s.getLevel() != 1) {
            instructions.add(new Instruction("loca", s.getPart()));
        }
        else {
            instructions.add(new Instruction("globa", s.getGlobal()));
        }

        expect(TokenType.Assign);
        String ety = analyseExpr();
        while (!operator.empty())
            opinstr(operator.pop(), instructions, ety);

        if (s.isConstant)
            throw new Error("");

        else if(s.getType().equals(ety) && (s.getType().equals("int") || s.getType().equals("double"))){

            initializeSymbol(s.getName(), peekedToken.getStartPos());
            instructions.add(new Instruction("store.64", null));
            return "void";
        }
        else
            throw new Error("");

    }

    private String analyseNegateExpr() throws CompileError{
        expect(TokenType.Minus);
        String ety = analyseExpr();
        if(!ety.equals("int") && !ety.equals("double"))
            throw new Error("");
        instructions.add(new Instruction("neg.i", null));
        return ety;
    }

    private String analyseLiteralExpr() throws CompileError{
        if(check(TokenType.Uint)){
            Token token = next();
            instructions.add(new Instruction("push", (Integer) token.getValue()));
            return "int";
        }
//        else if(check(TokenType.Double)){
//            Token token = next();
//            String b = Long.toBinaryString(Double.doubleToRawLongBits((Double) token.getValue()));
//            instructions.add(new Instruction("push", changeString(b)));
//            return "double";
//        }
        else if(check(TokenType.String)){
            Token token = next();
            String name = (String) token.getValue();
            decls.add(new SymbolDecl(name,1, name.length()));

            instructions.add(new Instruction("push", declsnum));
            declsnum++;
            return "string";
        }
//        else if(check(TokenType.Char)){
//            Token token = next();
//            instructions.add(new Instruction("push", (Integer) token.getValue()));
//            return "int";
//        }
        else
            throw new Error("");
    }

    private Long changeString(String a){
        Long lstr = 0L;
        Long l = 1L;
        for(int i=a.length()-1; i>=0; i--){
            if(a.charAt(i) == '1')
                lstr += l;
            l *=2;
        }
        return lstr;
    }

    private String analyseGroupExpr() throws CompileError{
        expect(TokenType.LParen);

        operator.push(TokenType.LParen);
        String ety = analyseExpr();
        expect(TokenType.RParen);

        while (operator.peek() != TokenType.LParen)
            opinstr(operator.pop(), instructions, ety);
        operator.pop();
        return ety;
    }

    private String analyseAsExpr(String ety) throws CompileError{
        expect(TokenType.As);
        String ty =  analyseTy();
        if(ety.equals("int") && ty.equals("double")){
            instructions.add(new Instruction("itof", null));
            return "double";
        }
        else if(ety.equals("double") && ty.equals("int")){
            instructions.add(new Instruction("ftoi", null));
            return "int";
        }
        else if(ety.equals(ty)){
            return ety;
        }
        else
            throw new Error("");

    }

    private String analyseOperatorExpr(String ety) throws CompileError{
        Token t = analyseBinaryOperator();

        if (!operator.empty()) {
            int in = Operator.getOrder(operator.peek());
            int out = Operator.getOrder(t.getTokenType());
            if (Operator.priority[in][out] > 0)
                opinstr(operator.pop(), instructions, ety);
        }
        operator.push(t.getTokenType());

        String ty =  analyseExpr();
        if(ety.equals(ty) && (ety.equals("int") || ety.equals("double")))
            return ty;
        else
            throw new Error("");
    }

    private Token analyseBinaryOperator() throws CompileError{
        if(check(TokenType.As) ||
                check(TokenType.Plus)||
                check(TokenType.Minus)||
                check(TokenType.Mul)||
                check(TokenType.Div)||
                check(TokenType.Eq)||
                check(TokenType.Neq)||
                check(TokenType.Lt)||
                check(TokenType.Gt)||
                check(TokenType.Le)||
                check(TokenType.Ge)){
            return next();
        }
        //不是以上类型
        else
            throw new Error("");
    }



    private SymbolEntry getlibrary(String name) throws CompileError{
        ArrayList<SymbolEntry> params = new ArrayList<>();
        SymbolEntry add = new SymbolEntry();
        String returns;

        if(name.equals("getint")){
            returns = "int";
        }
        else if(name.equals("getdouble")){
            returns = "double";
        }
        else if(name.equals("getchar")){
            returns = "int";
        }
        else if(name.equals("putint")){
            returns = "void";
            add.setType("int");
            params.add(add);
        }
        else if(name.equals("putdouble")){
            returns = "void";
            add.setType("double");
            params.add(add);
        }
        else if(name.equals("putchar")){
            returns = "void";
            add.setType("int");
            params.add(add);
        }
        else if(name.equals("putstr")){
            returns = "void";
            add.setType("string");
            params.add(add);
        }
        else if(name.equals("putln")){
            returns = "void";
        }
        else
            return null;
        return new SymbolEntry(name, false, "function", true, 0, level, params, returns, -1,null,  -1, -1);
    }

    private boolean checkreturn(String name) {

        if (name.equals("getint") || name.equals("getdouble") || name.equals("getchar"))
            return true;

        for (int i=0;i<funcs.size();i++) {
            if (funcs.get(i).getName().equals(name)) {
                if (funcs.get(i).getReturnstr().equals("int") || funcs.get(i).getReturnstr().equals("double")) return true;
            }
        }
        return false;
    }

    private int getFunc(String name){
        for (int i=0 ; i<funcs.size(); i++) {
            if (funcs.get(i).getName().equals(name)) return i;
        }
        return -1;
    }

    private void opinstr(TokenType a, List<Instruction> instructions, String ty) throws CompileError{
        switch (a) {
            case Plus:
                if(ty.equals("int"))
                    instructions.add(new Instruction("add.i", null));
                else if(ty.equals("double"))
                    instructions.add(new Instruction("add.f", null));
                else
                    throw new Error("wrong");
                break;
            case Minus:
                if(ty.equals("int"))
                    instructions.add(new Instruction("sub.i", null));
                else if(ty.equals("double"))
                    instructions.add(new Instruction("sub.f", null));
                else
                    throw new Error("wrong");
                break;
            case Mul:
                if(ty.equals("int"))
                    instructions.add(new Instruction("mul.i", null));
                else if(ty.equals("double"))
                    instructions.add(new Instruction("mul.f", null));
                else
                    throw new Error("wrong");
                break;
            case Div:
                if(ty.equals("int"))
                    instructions.add(new Instruction("div.i", null));
                else if(ty.equals("double"))
                    instructions.add(new Instruction("div.f", null));
                else
                    throw new Error("wrong");
                break;
            case Eq:
                if(ty.equals("int"))
                    instructions.add(new Instruction("cmp.i", null));
                else if(ty.equals("double"))
                    instructions.add(new Instruction("cmp.f", null));
                else
                    throw new Error("wrong");
                instructions.add(new Instruction("not", null));
                break;
            case Neq:
                if(ty.equals("int"))
                    instructions.add(new Instruction("cmp.i", null));
                else if(ty.equals("double"))
                    instructions.add(new Instruction("cmp.f", null));
                else
                    throw new Error("wrong");
                break;
            case Lt:
                if(ty.equals("int"))
                    instructions.add(new Instruction("cmp.i", null));
                else if(ty.equals("double"))
                    instructions.add(new Instruction("cmp.f", null));
                else
                    throw new Error("wrong");
                instructions.add(new Instruction("set.lt", null));
                break;
            case Gt:
                if(ty.equals("int"))
                    instructions.add(new Instruction("cmp.i", null));
                else if(ty.equals("double"))
                    instructions.add(new Instruction("cmp.f", null));
                else
                    throw new Error("wrong");
                instructions.add(new Instruction("set.gt", null));
                break;
            case Le:
                if(ty.equals("int"))
                    instructions.add(new Instruction("cmp.i", null));
                else if(ty.equals("double"))
                    instructions.add(new Instruction("cmp.f", null));
                else
                    throw new Error("wrong");
                instructions.add(new Instruction("set.gt", null));
                instructions.add(new Instruction("not", null));
                break;
            case Ge:
                if(ty.equals("int"))
                    instructions.add(new Instruction("cmp.i", null));
                else if(ty.equals("double"))
                    instructions.add(new Instruction("cmp.f", null));
                else
                    throw new Error("wrong");
                instructions.add(new Instruction("set.lt", null));
                instructions.add(new Instruction("not", null));
                break;
            default:
                break;
        }

    }

    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    public void setTokenizer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public ArrayList<Instruction> getInitInstructions() {
        return initInstructions;
    }

    public void setInitInstructions(ArrayList<Instruction> initInstructions) {
        this.initInstructions = initInstructions;
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(ArrayList<Instruction> instructions) {
        this.instructions = instructions;
    }

    public Token getPeekedToken() {
        return peekedToken;
    }

    public void setPeekedToken(Token peekedToken) {
        this.peekedToken = peekedToken;
    }

    public ArrayList<SymbolEntry> getSymbols() {
        return symbols;
    }

    public void setSymbols(ArrayList<SymbolEntry> symbols) {
        this.symbols = symbols;
    }

    public ArrayList<SymbolDecl> getDecls() {
        return decls;
    }

    public void setDecls(ArrayList<SymbolDecl> decls) {
        this.decls = decls;
    }

    public ArrayList<Func> getFuncs() {
        return funcs;
    }

    public void setFuncs(ArrayList<Func> funcs) {
        this.funcs = funcs;
    }

    public Func getStart() {
        return start;
    }

    public void setStart(Func start) {
        this.start = start;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(int nextOffset) {
        this.nextOffset = nextOffset;
    }

    public int getDeclsnum() {
        return declsnum;
    }

    public void setDeclsnum(int declsnum) {
        this.declsnum = declsnum;
    }

    public int getLocalsnum() {
        return localsnum;
    }

    public void setLocalsnum(int localsnum) {
        this.localsnum = localsnum;
    }

    public int getFuncsnum() {
        return funcsnum;
    }

    public void setFuncsnum(int funcsnum) {
        this.funcsnum = funcsnum;
    }

    public Stack<TokenType> getOperator() {
        return operator;
    }

    public void setOperator(Stack<TokenType> operator) {
        this.operator = operator;
    }
}
