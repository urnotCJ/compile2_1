package miniplc0java.analyser;

import miniplc0java.instruction.Instruction;

import java.util.ArrayList;

public class Func {
    String name;
    int paramsize;
    int letnum;
    int position;
    int returns;//int 1;double 2,void 3
    ArrayList<Instruction> funcinstructions= new ArrayList<>();
    int level;
    String returnstr;

    public Func(String name, int position, int returns, int paramsize, int letnum, ArrayList<Instruction> funcinstructions, int level,String returnstr) {
        this.name = name;
        this.position = position;
        this.returns = returns;
        this.paramsize = paramsize;
        this.letnum = letnum;
        this.funcinstructions = funcinstructions;
        this.level = level;
        this.returnstr = returnstr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getReturns() {
        return returns;
    }

    public void setReturns(int returns) {
        this.returns = returns;
    }

    public int getParamsize() {
        return paramsize;
    }

    public void setParamsize(int paramsize) {
        this.paramsize = paramsize;
    }

    public int getLetnum() {
        return letnum;
    }

    public void setLetnum(int letnum) {
        this.letnum = letnum;
    }

    public ArrayList<Instruction> getFuncinstructions() {
        return funcinstructions;
    }

    public void setFuncinstructions(ArrayList<Instruction> funcinstructions) {
        this.funcinstructions = funcinstructions;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getReturnstr() {
        return returnstr;
    }

    public void setReturnstr(String returnstr) {
        this.returnstr = returnstr;
    }
}
