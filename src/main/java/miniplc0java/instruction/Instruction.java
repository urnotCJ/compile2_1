package miniplc0java.instruction;

import java.util.Objects;

public class Instruction {
    String opt;
    Object x;


    public Instruction(String opt, Object x) {
        this.opt = opt;
        this.x = x;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public void setX(Object x) {
        this.x = x;
    }

    public String getOpt() {
        return opt;
    }

    public Object getX() {
        return x;
    }
}
