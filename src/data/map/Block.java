package data.map;

public class Block {
    private int line;
    private int column;
    private boolean isOccupied;

    public Block(int line, int column) {
        this.line = line;
        this.column = column;
        this.isOccupied = false;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    @Override
    public String toString() {
        return "Block [line=" + line + ", column=" + column + "]";
    }
}

