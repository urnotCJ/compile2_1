package miniplc0java.analyser;

import java.util.ArrayList;

public class SymbolEntry {
    String name;
    boolean isConstant;//常量
    String type; //1 int 2 double 3void function
    boolean isInitialized;
    int stackOffset;//栈偏移
    int level;//层数
    ArrayList<SymbolEntry> params = new ArrayList<>();//函数参数列表
    String returns;
    int paramlocation;//参数位置
    SymbolEntry func;//所属函数
    int part;//局部变量
    int global;//全局

    public SymbolEntry(){}

    public SymbolEntry(String name,boolean isConstant, String type, boolean isInitialized, int stackOffset, int level, ArrayList<SymbolEntry> params, String returns, int paramlocation, SymbolEntry func, int part, int global){
        this.name=name;
        this.isConstant=isConstant;
        this.type=type;
        this.isInitialized=isInitialized;
        this.stackOffset=stackOffset;
        this.level=level;
        this.params=params;
        this.returns=returns;
        this.paramlocation=paramlocation;
        this.func=func;
        this.part=part;
        this.global=global;
    }

    public String getName() {
        return name;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public String getType() {
        return type;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public int getStackOffset() {
        return stackOffset;
    }

    public int getLevel() {
        return level;
    }

    public ArrayList<SymbolEntry> getParams() {
        return params;
    }

    public String getReturns() {
        return returns;
    }

    public int getParamlocation() {
        return paramlocation;
    }

    public SymbolEntry getFunc() {
        return func;
    }

    public int getPart() {
        return part;
    }

    public int getGlobal() {
        return global;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConstant(boolean constant) {
        isConstant = constant;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    public void setStackOffset(int stackOffset) {
        this.stackOffset = stackOffset;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setParams(ArrayList<SymbolEntry> params) {
        this.params = params;
    }

    public void setReturns(String returns) {
        this.returns = returns;
    }

    public void setParamlocation(int paramlocation) {
        this.paramlocation = paramlocation;
    }

    public void setFunc(SymbolEntry func) {
        this.func = func;
    }

    public void setPart(int part) {
        this.part = part;
    }

    public void setGlobal(int global) {
        this.global = global;
    }
}
