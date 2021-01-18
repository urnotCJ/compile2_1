package miniplc0java.analyser;

public class SymbolDecl {
    String name;
    int isconstant;
    int position;

    public SymbolDecl(String name, int isconstant, int position) {
        this.name = name;
        this.isconstant = isconstant;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsconstant() {
        return isconstant;
    }

    public void setIsconstant(int isconstant) {
        this.isconstant = isconstant;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
