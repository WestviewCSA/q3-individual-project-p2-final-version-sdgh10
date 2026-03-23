public class Position{
	
	int row;
	int col;
	int layer;
	Position prev;
	
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
	
	public String toString() {
		return row + "," + col + "," + layer;
	}
	
	
}







//   // Position class with layer information
//
//
//public class Position {
//        int row, col, layer;
//        Position parent;
//        
//        Position(int row, int col, int layer) {
//            this(row, col, layer, null);
//        }
//        
//        Position(int row, int col, int layer, Position parent) {
//            this.row = row;
//            this.col = col;
//            this.layer = layer;
//            this.parent = parent;
//        }
//        
//        @Override
//        public boolean equals(Object obj) {
//            if (this == obj) return true;
//            if (obj == null || getClass() != obj.getClass()) return false;
//            Position pos = (Position) obj;
//            return row == pos.row && col == pos.col && layer == pos.layer;
//        }
//    }