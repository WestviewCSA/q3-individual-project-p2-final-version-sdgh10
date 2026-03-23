//import java.io.File;
//import java.io.FileNotFoundException;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.LinkedList;
//import java.util.Queue;
//import java.util.Scanner;
//import java.util.Stack;
//
//public class Main {
//    
//    private int rows;
//    private int cols;
//    private int layerCount;
//    private int[] start;
//    private int[] goal;
//    private ArrayList<String[][]> layers;
//    private boolean outputCoordinate;
//    
//
//    public static void main(String[] args) {
//        boolean useStack = false;
//        boolean useQueue = false;
//        boolean useOpt = false;
//        boolean showTime = false;
//        boolean inputCoordinate = false;
//        boolean outputCoordinate = false;
//        String filename = null;
//        int solverFlags = 0;
//        
//        for (int i = 0; i < args.length; i++) {
//            if (args[i].equals("--Stack")) {
//                useStack = true;
//                solverFlags++;
//            } else if (args[i].equals("--Queue")) {
//                useQueue = true;
//                solverFlags++;
//            } else if (args[i].equals("--Opt")) {
//                useOpt = true;
//                solverFlags++;
//            } else if (args[i].equals("--Time")) {
//                showTime = true;
//            } else if (args[i].equals("--Incoordinate")) {
//                inputCoordinate = true;
//            } else if (args[i].equals("--Outcoordinate")) {
//                outputCoordinate = true;
//            } else if (args[i].equals("--Help")) {
//                printHelp();
//                System.exit(0);
//            } else if (!args[i].startsWith("--")) {
//                filename = args[i];
//            }
//        }
//        
//        // Check that exactly one solver flag was specified
//        if (solverFlags != 1) {
//            System.err.println("Error: Must specify exactly one of --Stack, --Queue, or --Opt");
//            System.exit(-1);
//        }
//        
//        // Check that a filename was provided
//        if (filename == null) {
//            System.err.println("Error: No input file specified");
//            System.err.println("Usage: java Main [--Stack|--Queue|--Opt] [--Time] [--Incoordinate] [--Outcoordinate] <filename>");
//            System.exit(-1);
//        }
//        
//        try {
//            Main solver = new Main(filename, inputCoordinate);
//            solver.setOutputCoordinate(outputCoordinate);
//            
//            long startTime = System.nanoTime();
//            ArrayList<Position> path = null;
//            
//            if (useStack) {
//                path = solver.solveWithStack();
//            } else if (useQueue) {
//                path = solver.solveWithQueue();
//            } else if (useOpt) {
//                path = solver.solveOptimal();
//            }
//            
//            long endTime = System.nanoTime();
//            double runtimeSeconds = (endTime - startTime) / 1_000_000_000.0;
//            
//            if (path != null) {
//                if (outputCoordinate) {
//                    solver.printPathCoordinates(path);
//                } else {
//                    solver.markAndPrintPath(path);
//                }
//                if (showTime) {
//                    System.out.printf("Total Runtime: %.9f seconds\n", runtimeSeconds);
//                }
//            } else {
//                System.out.println("The Wolverine Store is closed.");
//            }
//            
//        } catch (Exception e) {
//            System.err.println("Error: " + e.getMessage());
//            System.exit(-1);
//        }
//    }
//    
//    private static void printHelp() {
//        System.out.println("Wolverine's Quest Maze Solver");
//        System.out.println("Finds a path from W to $ through multiple maze layers with teleporters (|)");
//        System.out.println();
//        System.out.println("Command Line Switches:");
//        System.out.println("  --Stack          Use stack-based approach (DFS)");
//        System.out.println("  --Queue          Use queue-based approach (BFS)");
//        System.out.println("  --Opt            Use optimal approach (shortest path)");
//        System.out.println("  --Time           Print runtime after solving");
//        System.out.println("  --Incoordinate   Input file is in coordinate format");
//        System.out.println("  --Outcoordinate  Output in coordinate format (path coordinates)");
//        System.out.println("  --Help           Display this help message");
//        System.out.println();
//        System.out.println("Usage: java Main [--Stack|--Queue|--Opt] [--Time] [--Incoordinate] [--Outcoordinate] <filename>");
//    }
//    
//    public Main(String filename, boolean isCoordinate) throws IncorrectMapFormatException, 
//                                        IncompleteMapException, 
//                                        IllegalMapCharacterException {
//        this.outputCoordinate = false;
//        
//        if (isCoordinate) {
//            readCoordinateMazeFile(filename);
//        } else {
//            layers = readTextMazeFile(filename);
//        }
//        findStartAndGoal();
//    }
//    
//    public void setOutputCoordinate(boolean outputCoordinate) {
//        this.outputCoordinate = outputCoordinate;
//    }
//    
//    public ArrayList<String[][]> readTextMazeFile(String filename) throws IncorrectMapFormatException, 
//    IncompleteMapException, 
//    IllegalMapCharacterException {
//        try (Scanner scanner = new Scanner(new File(filename))) {
//            if (!scanner.hasNextInt()) {
//                throw new IncorrectMapFormatException("First line must start with row count");
//            }
//            rows = scanner.nextInt();
//            
//            if (!scanner.hasNextInt()) {
//                throw new IncorrectMapFormatException("Missing column count");
//            }
//            cols = scanner.nextInt();
//            
//            if (!scanner.hasNextInt()) {
//                throw new IncorrectMapFormatException("Missing layer count");
//            }
//            layerCount = scanner.nextInt();
//            
//            if (rows <= 0 || cols <= 0 || layerCount <= 0) {
//                throw new IncorrectMapFormatException("Rows, columns, and layers must be positive");
//            }
//            
//            ArrayList<String[][]> layers = new ArrayList<>();
//            
//            for (int l = 0; l < layerCount; l++) {
//                String[][] grid = new String[rows][cols];
//                
//                for (int r = 0; r < rows; r++) {
//                    if (!scanner.hasNext()) {
//                        throw new IncompleteMapException("Missing row " + r + " in layer " + l);
//                    }
//                    
//                    String line = scanner.next();
//                    
//                    if (line.length() < cols) {
//                        throw new IncompleteMapException("Row " + r + " in layer " + l + 
//                                                         " has only " + line.length() + 
//                                                         " characters, needs " + cols);
//                    }
//                    
//                    for (int c = 0; c < cols; c++) {
//                        String ch = line.substring(c, c + 1);
//                        if (!ch.equals(".") && !ch.equals("@") && !ch.equals("W") && 
//                            !ch.equals("$") && !ch.equals("|")) {
//                            throw new IllegalMapCharacterException("Illegal character '" + ch + 
//                                                                   "' at layer " + l + 
//                                                                   ", row " + r + ", col " + c);
//                        }
//                        grid[r][c] = ch;
//                    }
//                }
//                layers.add(grid);
//            }
//            
//            return layers;
//            
//        } catch (FileNotFoundException e) {
//            throw new IncorrectMapFormatException("File not found: " + filename);
//        }
//    }
//    
//    public void readCoordinateMazeFile(String filename) throws IncorrectMapFormatException, 
//                                        IncompleteMapException, 
//                                        IllegalMapCharacterException {
//        try (Scanner scanner = new Scanner(new File(filename))) {
//            if (!scanner.hasNextInt()) {
//                throw new IncorrectMapFormatException("First line must start with row count");
//            }
//            rows = scanner.nextInt();
//            
//            if (!scanner.hasNextInt()) {
//                throw new IncorrectMapFormatException("Missing column count");
//            }
//            cols = scanner.nextInt();
//            
//            if (!scanner.hasNextInt()) {
//                throw new IncorrectMapFormatException("Missing layer count");
//            }
//            layerCount = scanner.nextInt();
//            
//            if (rows <= 0 || cols <= 0 || layerCount <= 0) {
//                throw new IncorrectMapFormatException("Rows, columns, and layers must be positive");
//            }
//            
//            layers = new ArrayList<>();
//            for (int l = 0; l < layerCount; l++) {
//                String[][] grid = new String[rows][cols];
//                for (int r = 0; r < rows; r++) {
//                    for (int c = 0; c < cols; c++) {
//                        grid[r][c] = ".";
//                    }
//                }
//                layers.add(grid);
//            }
//            
//            while (scanner.hasNext()) {
//                if (!scanner.hasNext()) break;
//                String type = scanner.next();
//                if (!scanner.hasNextInt()) break;
//                int row = scanner.nextInt();
//                if (!scanner.hasNextInt()) break;
//                int col = scanner.nextInt();
//                if (!scanner.hasNextInt()) break;
//                int layer = scanner.nextInt();
//                
//                if (row < 0 || row >= rows || col < 0 || col >= cols || layer < 0 || layer >= layerCount) {
//                    throw new IllegalMapCharacterException("Coordinate (" + row + "," + col + ") in layer " + 
//                                                           layer + " is out of bounds");
//                }
//                
//                if (type.equals("W") || type.equals("$") || type.equals("|") || type.equals("@") || type.equals(".")) {
//                    layers.get(layer)[row][col] = type;
//                } else {
//                    throw new IllegalMapCharacterException("Illegal character '" + type + "' in coordinate input");
//                }
//            }
//        } catch (FileNotFoundException e) {
//            throw new IncorrectMapFormatException("File not found: " + filename);
//        }
//    }
//    
//    public void findStartAndGoal() {
//        for (int l = 0; l < layerCount; l++) {
//            String[][] grid = layers.get(l);
//            for (int r = 0; r < rows; r++) {
//                for (int c = 0; c < cols; c++) {
//                    if (grid[r][c].equals("W")) {
//                        start = new int[]{r, c, l};
//                    } else if (grid[r][c].equals("$")) {
//                        goal = new int[]{r, c, l};
//                    }
//                }
//            }
//        }
//        
//        Position.setGoal(goal);
//        
//        if (start == null || goal == null) {
//            System.out.println("The Wolverine Store is closed.");
//            System.exit(0);
//        }
//    }
//    
//    public boolean isValid(int row, int col, int layer) {
//        if (row < 0 || row >= rows || col < 0 || col >= cols) {
//            return false;
//        }
//        String cell = layers.get(layer)[row][col];
//        return !cell.equals("@");
//    }
//    
//    public int teleport(int currentLayer) {
//        return (currentLayer + 1) % layerCount;
//    }
//    
//    public ArrayList<Position> reconstructPath(Position end) {
//        ArrayList<Position> path = new ArrayList<>();
//        Position current = end;
//        
//        while (current != null) {
//            path.add(0, current);
//            current = current.getPrev();
//        }
//        
//        return path;
//    }
//    
//    public ArrayList<Position> solveWithStack() {
//        HashSet<String> visited = new HashSet<>();
//        Stack<Position> stack = new Stack<>();
//        
//        Position startPos = new Position(start[0], start[1], start[2]);
//        stack.push(startPos);
//        visited.add(startPos.getKey());
//        
//        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
//        
//        while (!stack.isEmpty()) {
//            Position current = stack.pop();
//            
//            if (current.isGoal()) {
//                return reconstructPath(current);
//            }
//            
//            for (int[] dir : directions) {
//                int newRow = current.getRow() + dir[0];
//                int newCol = current.getCol() + dir[1];
//                int newLayer = current.getLayer();
//                
//                if (isValid(newRow, newCol, newLayer)) {
//                    String cell = layers.get(newLayer)[newRow][newCol];
//                    
//                    if (cell.equals(".") || cell.equals("W") || cell.equals("$")) {
//                        Position next = new Position(newRow, newCol, newLayer, current);
//                        if (!visited.contains(next.getKey())) {
//                            visited.add(next.getKey());
//                            stack.push(next);
//                        }
//                    }
//                    else if (cell.equals("|")) {
//                        int teleportedLayer = teleport(newLayer);
//                        
//                        if (isValid(newRow, newCol, teleportedLayer)) {
//                            Position teleported = new Position(newRow, newCol, teleportedLayer, current);
//                            if (!visited.contains(teleported.getKey())) {
//                                visited.add(teleported.getKey());
//                                stack.push(teleported);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        
//        return null;
//    }
//    
//    public ArrayList<Position> solveWithQueue() {
//        HashSet<String> visited = new HashSet<>();
//        Queue<Position> queue = new LinkedList<>();
//        
//        Position startPos = new Position(start[0], start[1], start[2]);
//        queue.add(startPos);
//        visited.add(startPos.getKey());
//        
//        int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
//        
//        while (!queue.isEmpty()) {
//            Position current = queue.poll();
//            
//            for (int[] dir : directions) {
//                int newRow = current.getRow() + dir[0];
//                int newCol = current.getCol() + dir[1];
//                int newLayer = current.getLayer();
//                
//                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
//                    String cell = layers.get(newLayer)[newRow][newCol];
//                    
//                    if (cell.equals("@")) {
//                        continue;
//                    }
//                    
//                    if (cell.equals(".") || cell.equals("W") || cell.equals("$")) {
//                        Position next = new Position(newRow, newCol, newLayer, current);
//                        if (!visited.contains(next.getKey())) {
//                            visited.add(next.getKey());
//                            queue.add(next);
//                            if (next.isGoal()) {
//                                return reconstructPath(next);
//                            }
//                        }
//                    }
//                    else if (cell.equals("|")) {
//                        int teleportedLayer = teleport(newLayer);
//                        if (teleportedLayer >= 0 && teleportedLayer < layerCount) {
//                            String destCell = layers.get(teleportedLayer)[newRow][newCol];
//                            if (!destCell.equals("@")) {
//                                Position teleported = new Position(newRow, newCol, teleportedLayer, current);
//                                if (!visited.contains(teleported.getKey())) {
//                                    visited.add(teleported.getKey());
//                                    queue.add(teleported);
//                                    if (teleported.isGoal()) {
//                                        return reconstructPath(teleported);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        
//        return null;
//    }
//    
//    public ArrayList<Position> solveOptimal() {
//        if (start[0] == goal[0] && start[1] == goal[1] && start[2] == goal[2]) {
//            ArrayList<Position> path = new ArrayList<>();
//            path.add(new Position(start[0], start[1], start[2]));
//            return path;
//        }
//        
//        int totalCells = layerCount * rows * cols;
//        boolean[] visited = new boolean[totalCells];
//        
//        int rowMultiplier = cols;
//        int layerMultiplier = rows * cols;
//        
//        int maxSize = totalCells;
//        int[] queueRows = new int[maxSize];
//        int[] queueCols = new int[maxSize];
//        int[] queueLayers = new int[maxSize];
//        int[] pIndex = new int[maxSize];
//        int front = 0;
//        int rear = 0;
//        
//        int[] teleportMap = new int[layerCount];
//        for (int i = 0; i < layerCount; i++) {
//            teleportMap[i] = (i + 1) % layerCount;
//        }
//        
//        queueRows[rear] = start[0];
//        queueCols[rear] = start[1];
//        queueLayers[rear] = start[2];
//        int startIndex = start[2] * layerMultiplier + start[0] * rowMultiplier + start[1];
//        visited[startIndex] = true;
//        pIndex[rear] = -1;
//        rear++;
//        
//        int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
//        
//        while (front < rear) {
//            int currentRow = queueRows[front];
//            int currentCol = queueCols[front];
//            int currentLayer = queueLayers[front];
//            
//            if (currentRow == goal[0] && currentCol == goal[1] && currentLayer == goal[2]) {
//                return reconstructPath2(queueRows, queueCols, queueLayers, pIndex, front);
//            }
//            
//            for (int[] dir : directions) {
//                int newRow = currentRow + dir[0];
//                int newCol = currentCol + dir[1];
//                
//                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
//                    String cell = layers.get(currentLayer)[newRow][newCol];
//                    
//                    if (cell.equals("@")) {
//                        continue;
//                    }
//                    
//                    if (!cell.equals("|")) {
//                        int newIndex = currentLayer * layerMultiplier + newRow * rowMultiplier + newCol;
//                        if (!visited[newIndex]) {
//                            visited[newIndex] = true;
//                            queueRows[rear] = newRow;
//                            queueCols[rear] = newCol;
//                            queueLayers[rear] = currentLayer;
//                            pIndex[rear] = front;
//                            rear++;
//                        }
//                    } else {
//                        int newLayer = teleportMap[currentLayer];
//                        int newIndex = newLayer * layerMultiplier + newRow * rowMultiplier + newCol;
//                        if (!visited[newIndex]) {
//                            String destCell = layers.get(newLayer)[newRow][newCol];
//                            if (!destCell.equals("@")) {
//                                visited[newIndex] = true;
//                                queueRows[rear] = newRow;
//                                queueCols[rear] = newCol;
//                                queueLayers[rear] = newLayer;
//                                pIndex[rear] = front;
//                                rear++;
//                            }
//                        }
//                    }
//                }
//            }
//            front++;
//        }
//        
//        return null;
//    }
//    
//    public ArrayList<Position> reconstructPath2(int[] rows, int[] cols, int[] layers, int[] pIndex, int endIndex) {
//        ArrayList<Position> path = new ArrayList<>();
//        
//        int length = 0;
//        int tempIndex = endIndex;
//        while (tempIndex != -1) {
//            length++;
//            tempIndex = pIndex[tempIndex];
//        }
//        
//        Position[] tempPath = new Position[length];
//        int pos = length - 1;
//        int currentIndex = endIndex;
//        while (currentIndex != -1) {
//            tempPath[pos--] = new Position(rows[currentIndex], cols[currentIndex], layers[currentIndex]);
//            currentIndex = pIndex[currentIndex];
//        }
//        
//        for (int i = 0; i < length; i++) {
//            path.add(tempPath[i]);
//        }
//        
//        return path;
//    }
//    
//    public void printPathCoordinates(ArrayList<Position> path) {
//        if (path == null) return;
//        
//        for (Position pos : path) {
//            if (pos.getRow() == start[0] && pos.getCol() == start[1] && pos.getLayer() == start[2]) {
//                continue;
//            }
//            if (pos.isGoal()) {
//                continue;
//            }
//            System.out.println("+ " + pos.getRow() + " " + pos.getCol() + " " + pos.getLayer());
//        }
//    }
//    
//    public void markAndPrintPath(ArrayList<Position> path) {
//        if (path == null) return;
//        
//        ArrayList<String[][]> solutionLayers = new ArrayList<>();
//        for (String[][] original : layers) {
//            String[][] copy = new String[rows][cols];
//            for (int i = 0; i < rows; i++) {
//                System.arraycopy(original[i], 0, copy[i], 0, cols);
//            }
//            solutionLayers.add(copy);
//        }
//        
//        for (Position pos : path) {
//            if (pos.getRow() == start[0] && pos.getCol() == start[1] && pos.getLayer() == start[2]) {
//                continue;
//            }
//            if (pos.isGoal()) {
//                continue;
//            }
//            
//            String[][] layer = solutionLayers.get(pos.getLayer());
//            layer[pos.getRow()][pos.getCol()] = "+";
//        }
//        
//        for (int l = 0; l < layerCount; l++) {
//            String[][] grid = solutionLayers.get(l);
//            for (int r = 0; r < rows; r++) {
//                for (int c = 0; c < cols; c++) {
//                    System.out.print(grid[r][c]);
//                }
//                System.out.println();
//            }
//            if (l < layerCount - 1) {
//                System.out.println();
//            }
//        }
//    }
//}










import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

public class Main {
    
    private int rows;
    private int cols;
    private int layerCount;
    private int[] start;
    private int[] goal;
    private ArrayList<String[][]> layers;
    private boolean outputCoordinate;
    

    public static void main(String[] args) {
        boolean useStack = false;
        boolean useQueue = false;
        boolean useOpt = false;
        boolean showTime = false;
        boolean inputCoordinate = false;
        boolean outputCoordinate = false;
        String filename = null;
        int solverFlags = 0;
        
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--Stack")) {
                useStack = true;
                solverFlags++;
            } else if (args[i].equals("--Queue")) {
                useQueue = true;
                solverFlags++;
            } else if (args[i].equals("--Opt")) {
                useOpt = true;
                solverFlags++;
            } else if (args[i].equals("--Time")) {
                showTime = true;
            } else if (args[i].equals("--Incoordinate")) {
                inputCoordinate = true;
            } else if (args[i].equals("--Outcoordinate")) {
                outputCoordinate = true;
            } else if (args[i].equals("--Help")) {
                printHelp();
                System.exit(0);
            } else if (!args[i].startsWith("--")) {
                filename = args[i];
            }
        }
        
        if (solverFlags != 1) {
            System.err.println("Error: Must specify exactly one of --Stack, --Queue, or --Opt");
            System.exit(-1);
        }
        
        if (filename == null) {
            filename = "src/coordEasyMap2";
        }
        
        try {
            Main solver = new Main(filename, inputCoordinate);
            solver.setOutputCoordinate(outputCoordinate);
            
            long startTime = System.nanoTime();
            ArrayList<Position> path = null;
            
            if (useStack) {
                path = solver.solveWithStack();
            } else if (useQueue) {
                path = solver.solveWithQueue();
            } else if (useOpt) {
                path = solver.solveOptimal();
            }
            
            long endTime = System.nanoTime();
            double runtimeSeconds = (endTime - startTime) / 1_000_000_000.0;
            
            if (path != null) {
                if (outputCoordinate) {
                    solver.printPathCoordinates(path);
                } else {
                    solver.markAndPrintPath(path);
                }
                if (showTime) {
                    System.out.printf("Total Runtime: %.9f seconds\n", runtimeSeconds);
                }
            } else {
                System.out.println("The Wolverine Store is closed.");
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(-1);
        }
    }
    
    private static void printHelp() {
        System.out.println("Wolverine's Quest Maze Solver");
        System.out.println("Finds a path from W to $ through multiple maze layers with teleporters (|)");
        System.out.println();
        System.out.println("Command Line Switches:");
        System.out.println("  --Stack          Use stack-based approach (DFS)");
        System.out.println("  --Queue          Use queue-based approach (BFS)");
        System.out.println("  --Opt            Use optimal approach (shortest path)");
        System.out.println("  --Time           Print runtime after solving");
        System.out.println("  --Incoordinate   Input file is in coordinate format");
        System.out.println("  --Outcoordinate  Output in coordinate format (path coordinates)");
        System.out.println("  --Help           Display this help message");
        System.out.println();
        System.out.println("Usage: java Main [--Stack|--Queue|--Opt] [--Time] [--Incoordinate] [--Outcoordinate] <filename>");
    }
    
    public Main(String filename, boolean isCoordinate) throws IncorrectMapFormatException, 
                                        IncompleteMapException, 
                                        IllegalMapCharacterException {
        this.outputCoordinate = false;
        
        if (isCoordinate) {
            readCoordinateMazeFile(filename);
        } else {
            layers = readTextMazeFile(filename);
        }
        findStartAndGoal();
    }
    
    public void setOutputCoordinate(boolean outputCoordinate) {
        this.outputCoordinate = outputCoordinate;
    }
    
    public ArrayList<String[][]> readTextMazeFile(String filename) throws IncorrectMapFormatException, 
    IncompleteMapException, 
    IllegalMapCharacterException {
        try (Scanner scanner = new Scanner(new File(filename))) {
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
            
            return layers;
            
        } catch (FileNotFoundException e) {
            throw new IncorrectMapFormatException("File not found: " + filename);
        }
    }
    
    public void readCoordinateMazeFile(String filename) throws IncorrectMapFormatException, 
                                        IncompleteMapException, 
                                        IllegalMapCharacterException {
        try (Scanner scanner = new Scanner(new File(filename))) {
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
            
            layers = new ArrayList<>();
            for (int l = 0; l < layerCount; l++) {
                String[][] grid = new String[rows][cols];
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        grid[r][c] = ".";
                    }
                }
                layers.add(grid);
            }
            
            while (scanner.hasNext()) {
                if (!scanner.hasNext()) break;
                String type = scanner.next();
                if (!scanner.hasNextInt()) break;
                int row = scanner.nextInt();
                if (!scanner.hasNextInt()) break;
                int col = scanner.nextInt();
                if (!scanner.hasNextInt()) break;
                int layer = scanner.nextInt();
                
                if (row < 0 || row >= rows || col < 0 || col >= cols || layer < 0 || layer >= layerCount) {
                    throw new IllegalMapCharacterException("Coordinate (" + row + "," + col + ") in layer " + 
                                                           layer + " is out of bounds");
                }
                
                if (type.equals("W") || type.equals("$") || type.equals("|") || type.equals("@") || type.equals(".")) {
                    layers.get(layer)[row][col] = type;
                } else {
                    throw new IllegalMapCharacterException("Illegal character '" + type + "' in coordinate input");
                }
            }
        } catch (FileNotFoundException e) {
            throw new IncorrectMapFormatException("File not found: " + filename);
        }
    }
    
    public void findStartAndGoal() {
        for (int l = 0; l < layerCount; l++) {
            String[][] grid = layers.get(l);
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (grid[r][c].equals("W")) {
                        start = new int[]{r, c, l};
                    } else if (grid[r][c].equals("$")) {
                        goal = new int[]{r, c, l};
                    }
                }
            }
        }
        
        Position.setGoal(goal);
        
        if (start == null || goal == null) {
            System.out.println("The Wolverine Store is closed.");
            System.exit(0);
        }
    }
    
    public boolean isValid(int row, int col, int layer) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }
        String cell = layers.get(layer)[row][col];
        return !cell.equals("@");
    }
    
    public int teleport(int currentLayer) {
        return (currentLayer + 1) % layerCount;
    }
    
    public ArrayList<Position> reconstructPath(Position end) {
        ArrayList<Position> path = new ArrayList<>();
        Position current = end;
        
        while (current != null) {
            path.add(0, current);
            current = current.getPrev();
        }
        
        return path;
    }
    
    public ArrayList<Position> solveWithStack() {
        HashSet<String> visited = new HashSet<>();
        Stack<Position> stack = new Stack<>();
        
        Position startPos = new Position(start[0], start[1], start[2]);
        stack.push(startPos);
        visited.add(startPos.getKey());
        
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        while (!stack.isEmpty()) {
            Position current = stack.pop();
            
            if (current.isGoal()) {
                return reconstructPath(current);
            }
            
            for (int[] dir : directions) {
                int newRow = current.getRow() + dir[0];
                int newCol = current.getCol() + dir[1];
                int newLayer = current.getLayer();
                
                if (isValid(newRow, newCol, newLayer)) {
                    String cell = layers.get(newLayer)[newRow][newCol];
                    
                    if (cell.equals(".") || cell.equals("W") || cell.equals("$")) {
                        Position next = new Position(newRow, newCol, newLayer, current);
                        if (!visited.contains(next.getKey())) {
                            visited.add(next.getKey());
                            stack.push(next);
                        }
                    }
                    else if (cell.equals("|")) {
                        int teleportedLayer = teleport(newLayer);
                        
                        if (isValid(newRow, newCol, teleportedLayer)) {
                            Position teleported = new Position(newRow, newCol, teleportedLayer, current);
                            if (!visited.contains(teleported.getKey())) {
                                visited.add(teleported.getKey());
                                stack.push(teleported);
                            }
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    public ArrayList<Position> solveWithQueue() {
        HashSet<String> visited = new HashSet<>();
        Queue<Position> queue = new LinkedList<>();
        
        Position startPos = new Position(start[0], start[1], start[2]);
        queue.add(startPos);
        visited.add(startPos.getKey());
        
        int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        
        while (!queue.isEmpty()) {
            Position current = queue.poll();
            
            for (int[] dir : directions) {
                int newRow = current.getRow() + dir[0];
                int newCol = current.getCol() + dir[1];
                int newLayer = current.getLayer();
                
                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                    String cell = layers.get(newLayer)[newRow][newCol];
                    
                    if (cell.equals("@")) {
                        continue;
                    }
                    
                    if (cell.equals(".") || cell.equals("W") || cell.equals("$")) {
                        Position next = new Position(newRow, newCol, newLayer, current);
                        if (!visited.contains(next.getKey())) {
                            visited.add(next.getKey());
                            queue.add(next);
                            if (next.isGoal()) {
                                return reconstructPath(next);
                            }
                        }
                    }
                    else if (cell.equals("|")) {
                        int teleportedLayer = teleport(newLayer);
                        if (teleportedLayer >= 0 && teleportedLayer < layerCount) {
                            String destCell = layers.get(teleportedLayer)[newRow][newCol];
                            if (!destCell.equals("@")) {
                                Position teleported = new Position(newRow, newCol, teleportedLayer, current);
                                if (!visited.contains(teleported.getKey())) {
                                    visited.add(teleported.getKey());
                                    queue.add(teleported);
                                    if (teleported.isGoal()) {
                                        return reconstructPath(teleported);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    public ArrayList<Position> solveOptimal() {
        if (start[0] == goal[0] && start[1] == goal[1] && start[2] == goal[2]) {
            ArrayList<Position> path = new ArrayList<>();
            path.add(new Position(start[0], start[1], start[2]));
            return path;
        }
        
        int totalCells = layerCount * rows * cols;
        boolean[] visited = new boolean[totalCells];
        
        int rowMultiplier = cols;
        int layerMultiplier = rows * cols;
        
        int maxSize = totalCells;
        int[] queueRows = new int[maxSize];
        int[] queueCols = new int[maxSize];
        int[] queueLayers = new int[maxSize];
        int[] pIndex = new int[maxSize];
        int front = 0;
        int rear = 0;
        
        int[] teleportMap = new int[layerCount];
        for (int i = 0; i < layerCount; i++) {
            teleportMap[i] = (i + 1) % layerCount;
        }
        
        queueRows[rear] = start[0];
        queueCols[rear] = start[1];
        queueLayers[rear] = start[2];
        int startIndex = start[2] * layerMultiplier + start[0] * rowMultiplier + start[1];
        visited[startIndex] = true;
        pIndex[rear] = -1;
        rear++;
        
        int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        
        while (front < rear) {
            int currentRow = queueRows[front];
            int currentCol = queueCols[front];
            int currentLayer = queueLayers[front];
            
            if (currentRow == goal[0] && currentCol == goal[1] && currentLayer == goal[2]) {
                return reconstructPath2(queueRows, queueCols, queueLayers, pIndex, front);
            }
            
            for (int[] dir : directions) {
                int newRow = currentRow + dir[0];
                int newCol = currentCol + dir[1];
                
                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                    String cell = layers.get(currentLayer)[newRow][newCol];
                    
                    if (cell.equals("@")) {
                        continue;
                    }
                    
                    if (!cell.equals("|")) {
                        int newIndex = currentLayer * layerMultiplier + newRow * rowMultiplier + newCol;
                        if (!visited[newIndex]) {
                            visited[newIndex] = true;
                            queueRows[rear] = newRow;
                            queueCols[rear] = newCol;
                            queueLayers[rear] = currentLayer;
                            pIndex[rear] = front;
                            rear++;
                        }
                    } else {
                        int newLayer = teleportMap[currentLayer];
                        int newIndex = newLayer * layerMultiplier + newRow * rowMultiplier + newCol;
                        if (!visited[newIndex]) {
                            String destCell = layers.get(newLayer)[newRow][newCol];
                            if (!destCell.equals("@")) {
                                visited[newIndex] = true;
                                queueRows[rear] = newRow;
                                queueCols[rear] = newCol;
                                queueLayers[rear] = newLayer;
                                pIndex[rear] = front;
                                rear++;
                            }
                        }
                    }
                }
            }
            front++;
        }
        
        return null;
    }
    
    public ArrayList<Position> reconstructPath2(int[] rows, int[] cols, int[] layers, int[] pIndex, int endIndex) {
        ArrayList<Position> path = new ArrayList<>();
        
        int length = 0;
        int tempIndex = endIndex;
        while (tempIndex != -1) {
            length++;
            tempIndex = pIndex[tempIndex];
        }
        
        Position[] tempPath = new Position[length];
        int pos = length - 1;
        int currentIndex = endIndex;
        while (currentIndex != -1) {
            tempPath[pos--] = new Position(rows[currentIndex], cols[currentIndex], layers[currentIndex]);
            currentIndex = pIndex[currentIndex];
        }
        
        for (int i = 0; i < length; i++) {
            path.add(tempPath[i]);
        }
        
        return path;
    }
    
    public void printPathCoordinates(ArrayList<Position> path) {
        if (path == null) return;
        
        for (Position pos : path) {
            if (pos.getRow() == start[0] && pos.getCol() == start[1] && pos.getLayer() == start[2]) {
                continue;
            }
            if (pos.isGoal()) {
                continue;
            }
            System.out.println("+ " + pos.getRow() + " " + pos.getCol() + " " + pos.getLayer());
        }
    }
    
    public void markAndPrintPath(ArrayList<Position> path) {
        if (path == null) return;
        
        ArrayList<String[][]> solutionLayers = new ArrayList<>();
        for (String[][] original : layers) {
            String[][] copy = new String[rows][cols];
            for (int i = 0; i < rows; i++) {
                System.arraycopy(original[i], 0, copy[i], 0, cols);
            }
            solutionLayers.add(copy);
        }
        
        for (Position pos : path) {
            if (pos.getRow() == start[0] && pos.getCol() == start[1] && pos.getLayer() == start[2]) {
                continue;
            }
            if (pos.isGoal()) {
                continue;
            }
            
            String[][] layer = solutionLayers.get(pos.getLayer());
            layer[pos.getRow()][pos.getCol()] = "+";
        }
        
        for (int l = 0; l < layerCount; l++) {
            String[][] grid = solutionLayers.get(l);
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    System.out.print(grid[r][c]);
                }
                System.out.println();
            }
            if (l < layerCount - 1) {
                System.out.println();
            }
        }
    }
}