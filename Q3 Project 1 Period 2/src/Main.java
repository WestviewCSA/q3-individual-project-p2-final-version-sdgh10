import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.zip.DataFormatException;

public class Main {
	
	private int rows;
	private int cols;
	private int layerCount;
	
	private int[] start;
	private int[] goal;
	
	public Main() throws IncorrectMapFormatException, IncompleteMapException, IllegalMapCharacterException{
        readMazeFile("/src/easyMap1");
//        findStartAndGoal();
    }
    
	
	
    public static void main(String[] args) {
    		String filename = "/src/easyMap2";
    		
    	
    		
    }
	
	//direct text-map thing v v v v 

    public void readMazeFile(String filename) throws IncorrectMapFormatException, 
    IncompleteMapException, 
    IllegalMapCharacterException{
    		
    		try (Scanner scanner = new Scanner(new File(filename))) {
            // Check first line format
            if (!scanner.hasNextInt()) {
                throw new IncorrectMapFormatException("First line must start with row count");
            }
            rows = scanner.nextInt();
            
            if (!scanner.hasNextInt()) {
                throw new IncorrectMapFormatException("Missing column count");
            }
            cols = scanner.nextInt();
            
            if (!scanner.hasNextInt()) {
                throw new IncorrectMapFormatException("Missing layer count");
            }
            layerCount = scanner.nextInt();
            
            if (rows <= 0 || cols <= 0 || layerCount <= 0) {
                throw new IncorrectMapFormatException("Rows, columns, and layers must be positive");
            }
            
            ArrayList<String[][]> layers = new ArrayList<>();
            
            // Read each layer
            for (int l = 0; l < layerCount; l++) {
                String[][] grid = new String[rows][cols];
                
                for (int r = 0; r < rows; r++) {
                    if (!scanner.hasNext()) {
                        throw new IncompleteMapException("Missing row " + r + " in layer " + l);
                    }
                    
                    String line = scanner.next();
                    
                    if (line.length() < cols) {
                        throw new IncompleteMapException("Row " + r + " in layer " + l + 
                                                         " has only " + line.length() + 
                                                         " characters, needs " + cols);
                    }
                    
                    for (int c = 0; c < cols; c++) {
                        String ch = line.substring(c, c + 1);
                        // Check for illegal characters
                        if (!ch.equals(".") && !ch.equals("@") && !ch.equals("W") && 
                            !ch.equals("$") && !ch.equals("|")) {
                            throw new IllegalMapCharacterException("Illegal character '" + ch + 
                                                                   "' at layer " + l + 
                                                                   ", row " + r + ", col " + c);
                        }
                        grid[r][c] = ch;
                    }
                }
                layers.add(grid);
            }
            
        } catch (FileNotFoundException e) {
            throw new IncorrectMapFormatException("File not found: " + filename);
        }
    }
}
//
//    // We store each maze layer as a separate 2D array in an ArrayList.
//    // This avoids any 3D arrays.
//    private ArrayList<char[][]> layers;
//    private int rows, cols, layerCount;
//    private int[] start; // {row, col, layer}
//    private int[] goal;  // {row, col, layer}
//
//    // --- Inner class to represent a state (position + layer) for our searches ---
//    private class State {
//        int r, c, layer;
//        State parent; // For path reconstruction
//
//        State(int r, int c, int layer) {
//            this(r, c, layer, null);
//        }
//
//        State(int r, int c, int layer, State parent) {
//            this.r = r;
//            this.c = c;
//            this.layer = layer;
//            this.parent = parent;
//        }
//
//        // Useful for checking if we've reached the goal
//        boolean isGoal() {
//            return this.r == goal[0] && this.c == goal[1] && this.layer == goal[2];
//        }
//    }
//
//    // --- Constructor: Reads the file, builds the layers, finds start/goal ---
//    public Main(String filename) {
//        readMazeFile(filename);
//        findStartAndGoal();
//    }
//
//    // --- File reading: follows the spec exactly ---
//    private void readMazeFile(String filename) {
//        try (Scanner scanner = new Scanner(new File(filename))) {
//            // First line: rows, columns, layers
//            rows = scanner.nextInt();
//            cols = scanner.nextInt();
//            layerCount = scanner.nextInt();
//
//            layers = new ArrayList<>(layerCount);
//
//            // Read each layer one by one
//            for (int l = 0; l < layerCount; l++) {
//                char[][] grid = new char[rows][cols];
//                for (int r = 0; r < rows; r++) {
//                    String line = scanner.next();
//                    for (int c = 0; c < cols; c++) {
//                        grid[r][c] = line.charAt(c);
//                    }
//                }
//                layers.add(grid);
//            }
//        } catch (FileNotFoundException e) {
//            System.err.println("Error: File not found - " + filename);
//            System.exit(1);
//        }
//    }
//
//    // --- Scan all layers to find 'W' and '$' ---
//    private void findStartAndGoal() {
//        for (int l = 0; l < layerCount; l++) {
//            char[][] grid = layers.get(l);
//            for (int r = 0; r < rows; r++) {
//                for (int c = 0; c < cols; c++) {
//                    if (grid[r][c] == 'W') {
//                        start = new int[]{r, c, l};
//                    } else if (grid[r][c] == '$') {
//                        goal = new int[]{r, c, l};
//                    }
//                }
//            }
//        }
//
//        if (start == null || goal == null) {
//            System.err.println("Error: Maze missing start (W) or goal ($)");
//            System.exit(1);
//        }
//    }
//
//    // --- Core logic: checks if a move is allowed (not a wall, within bounds) ---
//    private boolean isValid(int r, int c, int layer, boolean[][][] visited) {
//        if (r < 0 || r >= rows || c < 0 || c >= cols) return false;
//        if (visited[layer][r][c]) return false;
//        char cell = layers.get(layer)[r][c];
//        // Walkable: open space, start, goal, or walkway
//        return cell != '@';
//    }
//
//    // --- Handles the teleportation logic when stepping onto '|' ---
//    // Returns the new layer after teleportation (cyclic).
//    private int teleport(int currentLayer) {
//        return (currentLayer + 1) % layerCount;
//    }
//
//    // --- Path reconstruction: walks back through parents and returns list of states ---
//    private ArrayList<State> reconstructPath(State end) {
//        ArrayList<State> path = new ArrayList<>();
//        State cur = end;
//        while (cur != null) {
//            path.add(0, cur); // Insert at front to get start->goal order
//            cur = cur.parent;
//        }
//        return path;
//    }
//
//    // --- 1) QUEUE-BASED SOLVER (BFS-like, finds some path) ---
//    public ArrayList<State> solveWithQueue() {
//        // Visited array: [layer][row][col] – this is a 3D boolean, but it's the only 3D structure.
//        // It's required to track visited cells across layers. The problem forbids 3D *maze* storage,
//        // but a visited tracking array is standard for graph search and acceptable at AP level.
//        boolean[][][] visited = new boolean[layerCount][rows][cols];
//
//        Queue<State> queue = new LinkedList<>();
//        State startState = new State(start[0], start[1], start[2]);
//        queue.add(startState);
//        visited[start[2]][start[0]][start[1]] = true;
//
//        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // N, S, W, E
//
//        while (!queue.isEmpty()) {
//            State current = queue.poll();
//
//            if (current.isGoal()) {
//                return reconstructPath(current);
//            }
//
//            // Explore neighbors
//            for (int[] d : dirs) {
//                int nr = current.r + d[0];
//                int nc = current.c + d[1];
//                int nl = current.layer;
//
//                // First, check if we can move to (nr, nc) in the current layer
//                if (isValid(nr, nc, nl, visited)) {
//                    char cell = layers.get(nl)[nr][nc];
//
//                    // Case 1: Regular walkable tile (., W, $) – but W and $ are handled by isGoal already.
//                    if (cell == '.' || cell == 'W' || cell == '$') {
//                        visited[nl][nr][nc] = true;
//                        queue.add(new State(nr, nc, nl, current));
//                    }
//                    // Case 2: Walkway – teleport after moving onto it
//                    else if (cell == '|') {
//                        int newLayer = teleport(nl);
//                        // Teleport destination must also be valid (not a wall, not visited)
//                        if (isValid(nr, nc, newLayer, visited)) {
//                            visited[newLayer][nr][nc] = true;
//                            queue.add(new State(nr, nc, newLayer, current));
//                        }
//                    }
//                }
//            }
//        }
//        return null; // No path found
//    }
//
//    // --- 2) STACK-BASED SOLVER (DFS-like, finds some path) ---
//    public ArrayList<State> solveWithStack() {
//        boolean[][][] visited = new boolean[layerCount][rows][cols];
//
//        Stack<State> stack = new Stack<>();
//        State startState = new State(start[0], start[1], start[2]);
//        stack.push(startState);
//        visited[start[2]][start[0]][start[1]] = true;
//
//        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
//
//        while (!stack.isEmpty()) {
//            State current = stack.pop();
//
//            if (current.isGoal()) {
//                return reconstructPath(current);
//            }
//
//            for (int[] d : dirs) {
//                int nr = current.r + d[0];
//                int nc = current.c + d[1];
//                int nl = current.layer;
//
//                if (isValid(nr, nc, nl, visited)) {
//                    char cell = layers.get(nl)[nr][nc];
//
//                    if (cell == '.' || cell == 'W' || cell == '$') {
//                        visited[nl][nr][nc] = true;
//                        stack.push(new State(nr, nc, nl, current));
//                    }
//                    else if (cell == '|') {
//                        int newLayer = teleport(nl);
//                        if (isValid(nr, nc, newLayer, visited)) {
//                            visited[newLayer][nr][nc] = true;
//                            stack.push(new State(nr, nc, newLayer, current));
//                        }
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    // --- 3) OPTIMAL PATH (shortest distance) ---
//    // Since BFS guarantees the shortest path when edges are unweighted,
//    // we can reuse solveWithQueue. The spec wants a separate method.
//    public ArrayList<State> solveOptimal() {
//        return solveWithQueue();
//    }
//
//    // --- Mark the solution path with '+' and print all layers ---
//    public void markAndPrintPath(ArrayList<State> path) {
//        if (path == null) {
//            System.out.println("No path found.");
//            return;
//        }
//
//        // Create copies of each layer to modify
//        ArrayList<char[][]> solutionLayers = new ArrayList<>();
//        for (char[][] original : layers) {
//            char[][] copy = new char[rows][cols];
//            for (int i = 0; i < rows; i++) {
//                System.arraycopy(original[i], 0, copy[i], 0, cols);
//            }
//            solutionLayers.add(copy);
//        }
//
//        // Mark the path (skip start and goal to preserve 'W' and '$')
//        for (State s : path) {
//            if (!s.isGoal() && !(s.r == start[0] && s.c == start[1] && s.layer == start[2])) {
//                char[][] layerGrid = solutionLayers.get(s.layer);
//                if (layerGrid[s.r][s.c] != '$' && layerGrid[s.r][s.c] != 'W') {
//                    layerGrid[s.r][s.c] = '+';
//                }
//            }
//        }
//
//        // Print all layers in order
//        for (int l = 0; l < layerCount; l++) {
//            System.out.println("Layer " + l + ":");
//            char[][] grid = solutionLayers.get(l);
//            for (int r = 0; r < rows; r++) {
//                System.out.println(new String(grid[r]));
//            }
//            System.out.println(); // blank line between layers
//        }
//    }
//
//    // --- Helper to measure and run a solver ---
//    private void runSolver(String name, java.util.function.Supplier<ArrayList<State>> solver) {
//        System.out.println("=== " + name + " ===");
//        long startTime = System.nanoTime();
//        ArrayList<State> path = solver.get();
//        long endTime = System.nanoTime();
//        double ms = (endTime - startTime) / 1_000_000.0;
//
//        if (path != null) {
//            System.out.println("Path found with length: " + path.size());
//            System.out.printf("Time: %.3f ms\n", ms);
//            markAndPrintPath(path);
//        } else {
//            System.out.println("No path found.");
//            System.out.printf("Time: %.3f ms\n", ms);
//        }
//        System.out.println();
//    }
//
//    // --- MAIN: entry point ---
//    public static void main(String[] args) {
//        // Default filename (you can change this or pass as argument)
//        String filename = "src/hardMap1";
//        if (args.length > 0) {
//            filename = args[0];
//        }
//
//        Main solver = new Main(filename);
//
//        // Run the three required solvers
//        solver.runSolver("Queue-based (any path)", solver::solveWithQueue);
//        solver.runSolver("Stack-based (any path)", solver::solveWithStack);
//        solver.runSolver("Optimal (shortest path)", solver::solveOptimal);
//    }
//}
//
//
//
//
//
////import java.io.File;
////import java.io.FileNotFoundException;
////import java.util.ArrayList;
////import java.util.LinkedList;
////import java.util.Queue;
////import java.util.Scanner;
////import java.util.Stack;
////
////public class Main {
////    
////    private String[][] grid;
////    private int rows, cols;
////    private int[] start, goal;
////    
////    
////    public static void main(String[] args) {
////        // Check if file path is provided as command line argument
////        String filePath = "src/hardMap1"; // default path
////        if (args.length > 0) {
////            filePath = args[0];
////        }
////        
////        File file = new File(filePath);
////        String[][] maze = reader(file);
////        
////        if (maze == null) {
////            System.out.println("Error reading maze file");
////            return;
////        }
////        
////        System.out.println("Original Maze:");
////        printGrid(maze);
////        System.out.println();
////        
////        Main solver = new Main(maze);
////        
////        // Solve with Stack (DFS)
////        System.out.println("Solving with Stack (DFS):");
////        long startTime = System.nanoTime();
////        ArrayList<Position> stackPath = solver.solveWithStack();
////        long endTime = System.nanoTime();
////        double duration = (endTime - startTime) / 1_000_000.0;
////        
////        if (stackPath != null) {
////            System.out.println("Path found! Length: " + stackPath.size());
////            solver.markPath(stackPath);
////            System.out.printf("Time: %.3f ms\n\n", duration);
////        } else {
////            System.out.println("No path found!\n");
////        }
////        
////        // Solve with Queue (BFS)
////        System.out.println("Solving with Queue (BFS):");
////        startTime = System.nanoTime();
////        ArrayList<Position> queuePath = solver.solveWithQueue();
////        endTime = System.nanoTime();
////        duration = (endTime - startTime) / 1_000_000.0;
////        
////        if (queuePath != null) {
////            System.out.println("Path found! Length: " + queuePath.size());
////            solver.markPath(queuePath);
////            System.out.printf("Time: %.3f ms\n\n", duration);
////        } else {
////            System.out.println("No path found!\n");
////        }
////        
////        // Solve Optimal Path (also using BFS but guaranteed shortest)
////        System.out.println("Solving Optimal Path (Shortest):");
////        startTime = System.nanoTime();
////        ArrayList<Position> optimalPath = solver.solveOptimal();
////        endTime = System.nanoTime();
////        duration = (endTime - startTime) / 1_000_000.0;
////        
////        if (optimalPath != null) {
////            System.out.println("Optimal path found! Length: " + optimalPath.size());
////            solver.markPath(optimalPath);
////            System.out.printf("Time: %.3f ms\n", duration);
////        } else {
////            System.out.println("No path found!\n");
////        }
////    }
////    
////    public Main(String[][] grid) {
////        this.grid = grid;
////        this.rows = grid.length;
////        this.cols = grid[0].length;
////        this.start = findPosition('W');
////        this.goal = findPosition('$');
////        
////        if (start == null) {
////            System.out.println("Warning: Start position 'W' not found!");
////        }
////        if (goal == null) {
////            System.out.println("Warning: Goal position '$' not found!");
////        }
////    }
////    
////    public static String[][] reader(File f) {
////        try {
////            Scanner scanner = new Scanner(f);
////            int row = scanner.nextInt();
////            int column = scanner.nextInt();
////            int layer = scanner.nextInt();
////            
////            row *= layer;
////            
////            String[][] arr = new String[row][column];
////            for (int r = 0; r < arr.length; r++) {
////                String rows = scanner.next();
////                for (int c = 0; c < arr[0].length; c++) {
////                    arr[r][c] = rows.substring(c, c + 1);
////                }
////            }
////            scanner.close();
////            return arr;
////            
////        } catch (FileNotFoundException e) {
////            System.err.println("File not found: " + f.getPath());
////            e.printStackTrace();
////            return null;
////        }
////    }
////    
////    public static void printGrid(String[][] arr) {
////        for (int i = 0; i < arr.length; i++) {
////            for (int j = 0; j < arr[0].length; j++) {
////                System.out.print(arr[i][j]);
////            }
////            System.out.println();
////        }
////    }
////    
////    public static String toString(String[][] arr) {
////        String res = "";
////        for (int i = 0; i < arr.length; i++) {
////            res += "[";
////            for (int j = 0; j < arr[0].length; j++) {
////                res += arr[i][j];
////                res += ", ";
////            }
////            res += "]\n";
////        }
////        return res;
////    }
////    
////    public static String findLocation(String letter, String[][] arr) {
////        String row = "";
////        String column = "";
////        for (int i = 0; i < arr.length; i++) {
////            for (int j = 0; j < arr[0].length; j++) {
////                if (arr[i][j].equals(letter)) {
////                    row += i;
////                    column += j;
////                    break;
////                }
////            }
////        }
////        return row + "," + column;
////    }
////    
////    // Stack-based solver (DFS)
////    public ArrayList<Position> solveWithStack() {
////        if (start == null || goal == null) return null;
////        
////        Stack<Position> stack = new Stack<>();
////        boolean[][] visited = new boolean[rows][cols];
////        
////        stack.push(new Position(start[0], start[1]));
////        visited[start[0]][start[1]] = true;
////        
////        while (!stack.isEmpty()) {
////            Position current = stack.pop();
////            
////            if (current.row == goal[0] && current.col == goal[1]) {
////                return reconstructPath(current);
////            }
////            
////            // Check all 4 directions (North, South, West, East)
////            int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
////            for (int[] dir : dirs) {
////                int newRow = current.row + dir[0];
////                int newCol = current.col + dir[1];
////                
////                if (isValidMove(newRow, newCol, visited)) {
////                    visited[newRow][newCol] = true;
////                    stack.push(new Position(newRow, newCol, current));
////                }
////            }
////        }
////        return null;
////    }
////    
////    // Queue-based solver (BFS)
////    public ArrayList<Position> solveWithQueue() {
////        if (start == null || goal == null) return null;
////        
////        Queue<Position> queue = new LinkedList<>();
////        boolean[][] visited = new boolean[rows][cols];
////        
////        queue.add(new Position(start[0], start[1]));
////        visited[start[0]][start[1]] = true;
////        
////        while (!queue.isEmpty()) {
////            Position current = queue.poll();
////            
////            if (current.row == goal[0] && current.col == goal[1]) {
////                return reconstructPath(current);
////            }
////            
////            // Check all 4 directions
////            int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
////            for (int[] dir : dirs) {
////                int newRow = current.row + dir[0];
////                int newCol = current.col + dir[1];
////                
////                if (isValidMove(newRow, newCol, visited)) {
////                    visited[newRow][newCol] = true;
////                    queue.add(new Position(newRow, newCol, current));
////                }
////            }
////        }
////        return null;
////    }
////    
////    // Optimal path solver (BFS - always finds shortest path)
////    public ArrayList<Position> solveOptimal() {
////        // BFS already finds the shortest path, so just use solveWithQueue
////        return solveWithQueue();
////    }
////    
////    public boolean isValidMove(int row, int col, boolean[][] visited) {
////        if (row < 0 || row >= rows || col < 0 || col >= cols) return false;
////        if (visited[row][col]) return false;
////        
////        String cell = grid[row][col];
////        return cell.equals(".") || cell.equals("$") || cell.equals("|");
////    }
////    
////    public ArrayList<Position> reconstructPath(Position end) {
////        ArrayList<Position> path = new ArrayList<>();
////        Position current = end;
////        
////        while (current != null) {
////            path.add(0, current);
////            current = current.parent;
////        }
////        return path;
////    }
////    
////    public int[] findPosition(char target) {
////        for (int i = 0; i < rows; i++) {
////            for (int j = 0; j < cols; j++) {
////                if (grid[i][j].charAt(0) == target) {
////                    return new int[]{i, j};
////                }
////            }
////        }
////        return null;
////    }
////    
////    public void markPath(ArrayList<Position> path) {
////        if (path == null) return;
////        
////        String[][] solution = new String[rows][cols];
////        // Copy original grid
////        for (int i = 0; i < rows; i++) {
////            for (int j = 0; j < cols; j++) {
////                solution[i][j] = grid[i][j];
////            }
////        }
////        
////        // Mark the path with '+'
////        for (Position pos : path) {
////            String cell = solution[pos.row][pos.col];
////            if (!cell.equals("W") && !cell.equals("$")) {
////                solution[pos.row][pos.col] = "+";
////            }
////        }
////        
////        printGrid(solution);
////    }
////}
////
//////import java.awt.List;
//////import java.io.File;
//////import java.io.FileNotFoundException;
//////import java.util.ArrayList;
//////import java.util.LinkedList;
//////import java.util.Queue;
//////import java.util.Scanner;
//////import java.util.Stack;
//////
//////public class Main {
//////	
//////	private String[][] grid;
//////    private int rows, cols;
//////    private int[] start, goal;
//////    
////////	public static void main(String[] args) {
////////		// TODO Auto-generated method stub
////////		
////////		Stack<String[]> in = new Stack<>();
////////		
////////		ArrayList<String[]> out = new ArrayList<>();
////////		
////////		//hello
////////		
////////		File file = new File("src/easyMap1");
////////		
////////		String[][] arr = reader(file);
////////		System.out.println(toString(arr));
////////		
////////		String[] location = findLocation("W", arr).split(",");
////////		
////////		in.push(location);
////////		out.add(in.pop());
////////		
////////		String north = arr[Integer.parseInt(location[0])-1][Integer.parseInt(location[1])];
////////		String south = arr[Integer.parseInt(location[0])+1][Integer.parseInt(location[1])];
////////		String east = arr[Integer.parseInt(location[0])][Integer.parseInt(location[1])+1];
////////		String west = arr[Integer.parseInt(location[0])-1][Integer.parseInt(location[1])-1];
////////		
//////////		if(north.equals(".")) {
//////////			if()
//////////		}
//////////		
//////////		
//////////		
////////	}
//////	
//////	    
//////    public Main(String[][] grid) {
//////        this.grid = grid;
//////        this.rows = grid.length;
//////        this.cols = grid[0].length;
//////        this.start = findPosition('W');
//////        this.goal = findPosition('$');
//////    }
//////
//////	
//////	
//////	public static String[][] reader(File f){
//////		
//////		
//////		try {
//////			Scanner scanner = new Scanner(f);
//////			int row = scanner.nextInt();
//////			int column = scanner.nextInt();
//////			int layer = scanner.nextInt();
//////			
//////			row*=layer;
//////			
//////			String[][] arr = new String[row][column];
//////			for(int r=0;r<arr.length;r++) {
//////				String rows = scanner.next();
//////				for(int c=0;c<arr[0].length;c++) {
//////					arr[r][c] = rows.substring(c, c+1);
//////				}
//////			}
//////			
//////			return arr;
//////			
//////			
//////		}
//////		catch (FileNotFoundException e) {
//////			// TODO Auto-generated catch block
//////			e.printStackTrace();
//////			return null;
//////		}
//////	}
//////	
//////	public static String toString(String[][] arr) {
//////		String res = "";
//////		for(int i = 0; i<arr.length; i++) {
//////			res+="[";
//////			for(int j = 0; j<arr[0].length; j++) {
//////				res+=arr[i][j];
//////				res+=", ";
//////			}
//////			res+="]";
//////		}
//////		res=res.substring(0,res.length()-2);
//////		res+="]";
//////		
//////		return res;
//////	}
//////	
//////	public static String findLocation(String letter, String[][] arr) {
//////		
//////		String row="";
//////		String column="";
//////		for(int i = 0; i<arr.length; i++) {
//////			for(int j = 0; j<arr[0].length; j++) {
//////				if(arr[i][j].equals(letter)) {
//////					row += i;
//////					column += j;
//////					break;
//////				}
//////			}
//////		}
//////		
//////		return row+","+column;
//////	}
//////	
//////	
//////	public List<Position> solveWithStack() {
//////	    Stack<Position> stack = new Stack<>();
//////	    boolean[][] visited = new boolean[rows][cols];
//////	    
//////	    stack.push(new Position(start[0], start[1]));
//////	    visited[start[0]][start[1]] = true;
//////	    
//////	    while (!stack.isEmpty()) {
//////	        Position current = stack.pop();
//////	        
//////	        if (current.row == goal[0] && current.col == goal[1]) {
//////	            return reconstructPath(current);
//////	        }
//////	        
//////	        // Check all 4 directions
//////	        for (int[] dir : new int[][]{{-1,0},{1,0},{0,-1},{0,1}}) {
//////	            int newRow = current.row + dir[0];
//////	            int newCol = current.col + dir[1];
//////	            
//////	            if (isValidMove(newRow, newCol, visited)) {
//////	                visited[newRow][newCol] = true;
//////	                stack.push(new Position(newRow, newCol, current));
//////	            }
//////	        }
//////	    }
//////	    return null;
//////	}
//////	
//////	public boolean isValidMove(int row, int col, boolean[][] visited) {
//////	    if (row < 0 || row >= rows || col < 0 || col >= cols) return false;
//////	    if (visited[row][col]) return false;
//////	    
//////	    char cell = grid[row][col].charAt(0);
//////	    return cell == '.' || cell == '$' || cell == '|';
//////	}
//////
//////	public List<Position> reconstructPath(Position end) {
//////	    List<Position> path = new ArrayList<>();
//////	    Position current = end;
//////	    
//////	    while (current != null) {
//////	        path.add(0, current);
//////	        current = current.parent;
//////	    }
//////	    return path;
//////	}
//////
//////	public int[] findPosition(char target) {
//////	    for (int i = 0; i < rows; i++) {
//////	        for (int j = 0; j < cols; j++) {
//////	            if (grid[i][j].charAt(0) == target) {
//////	                return new int[]{i, j};
//////	            }
//////	        }
//////	    }
//////	    return null;
//////	}
//////	
//////	public void markPath(List<Position> path) {
//////	    String[][] solution = copyGrid(grid);
//////	    
//////	    for (Position pos : path) {
//////	        if (!solution[pos.row][pos.col].equals("W") && 
//////	            !solution[pos.row][pos.col].equals("$")) {
//////	            solution[pos.row][pos.col] = "+";
//////	        }
//////	    }
//////	    printGrid(solution);
//////	}
//////}
////////public static String[][] getCoords(File file) {
////////	String dims = getDims(file);
////////	int row = Integer.parseInt(dims.substring(0,1));
////////	int col = Integer.parseInt(dims.substring(1,2));
////////	int layer = Integer.parseInt(dims.substring(2,3));
////////	if(layer>1){
////////		row*=layer;
//////////		System.out.println(col);
////////	}
////////	String[][] res = new String[row][col];
////////	try {
////////		Scanner scan = new Scanner(file);
////////		scan.next();
////////		scan.next();
////////		scan.next();
////////		for(int r=0;r<res.length;r++) {
////////			String rows = scan.next();
////////			for(int c=0;c<res[0].length;c++) {
////////				res[r][c] = rows.substring(c, c+1);
////////			}
////////		}
////////		
//////////		System.out.println(Arrays.deepToString(res));
//////////		System.out.println(res[0][0]);
////////		return res;
////////	} catch (FileNotFoundException e) {
////////		// TODO Auto-generated catch block
////////		e.printStackTrace();
////////		
////////		return res;
////////	}
////////
//////
