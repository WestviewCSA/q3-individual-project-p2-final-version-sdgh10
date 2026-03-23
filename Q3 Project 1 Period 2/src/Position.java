public class Position {
    
    private int row;
    private int col;
    private int layer;
    private Position prev;
    private static int[] goal;
    
    public Position(int r, int c, int l) {
        row = r;
        col = c;
        layer = l;
        prev = null;
    }
    
    public Position(int r, int c, int l, Position prev) {
        row = r;
        col = c;
        layer = l;
        this.prev = prev;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
    
    public int getLayer() {
        return layer;
    }
    
    public static void setGoal(int[] goalCoords) {
        goal = goalCoords;
    }
    
    public String getKey() {
        return layer + "," + row + "," + col;
    }
    
    public Position getPrev() {
        return prev;
    }
    
    public boolean isGoal() {
        if (goal == null) return false;  // FIXED: Added null check
        return row == goal[0] && col == goal[1] && layer == goal[2];
    }
}