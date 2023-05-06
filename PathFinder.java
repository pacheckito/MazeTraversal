import java.io.*;
import java.util.*;

/*
* self-referential class to represent a position in a path
*/
class Position {
    public int i; // row
    public int j; // column
    public char val; // 1, 0, or 'X'
    // reference to the previous position (parent) that leads to this position on a
    // path
    Position parent;

    Position(int x, int y, char v) {
        i = x;
        j = y;
        val = v;
    }

    Position(int x, int y, char v, Position p) {
        i = x;
        j = y;
        val = v;
        parent = p;
    }
}

// main method: reads in maze file and finds path using both stackSearch and
// queueSearch
public class PathFinder {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("***Usage: java PathFinder maze_file");
            System.exit(-1);
        }
        char[][] maze;
        maze = readMaze(args[0]);
        printMaze(maze);
        Position[] path = stackSearch(maze);
        if (path == null) {
            System.out.println("Maze is NOT solvable (no valid path identified in stackSearch).");
        } else {
            System.out.println("stackSearch Solution:");
            printPath(path);
            printMaze(maze);
        }
        char[][] maze2 = readMaze(args[0]);
        path = queueSearch(maze2);
        if (path == null) {
            System.out.println("Maze is NOT solvable (no valid path identified in queueSearch).");
        } else {
            System.out.println("queueSearch Solution:");
            printPath(path);
            printMaze(maze2);
        }
    }

    public static Position[] queueSearch(char[][] maze) {
        int n = maze.length;
        Queue<Position> queue = new LinkedList<>();
        queue.add(new Position(0, 0, maze[0][0]));
        boolean[][] visited = new boolean[n][n];
        visited[0][0] = true;
        while (!queue.isEmpty()) { // continue searching while the queue is not empty
            Position current = queue.remove(); // remove the next position from the queue
            if (current.i == n - 1 && current.j == n - 1) { // check if the current position is the exit
                Position temp = current;
                while (temp != null) {
                    maze[temp.i][temp.j] = 'X';
                    temp = temp.parent;
                }
                List<Position> path = new ArrayList<>(); // build and return the path to the exit
                while (current != null) {
                    path.add(current);
                    current = current.parent;
                }
                Collections.reverse(path);
                return path.toArray(new Position[path.size()]);
            }
            int[][] neighbors = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } }; // check all neighbors of the current position
            for (int[] offset : neighbors) {
                int newi = current.i + offset[0];
                int newj = current.j + offset[1];
                if (newi < 0 || newi >= n || newj < 0 || newj >= n) {
                    continue; // out of bounds
                }
                if (visited[newi][newj] || maze[newi][newj] == '1') {
                    continue; // already visited or wall
                }
                visited[newi][newj] = true;
                queue.add(new Position(newi, newj, maze[newi][newj], current)); // add the new position to the queue
            }
        }
        return null;
    }

    public static Position[] stackSearch(char[][] maze) {
        Stack<Position> stack = new Stack<>();              
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        char[][] newMaze = new char[maze.length][maze[0].length]; // Create a new maze array to store the path
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                newMaze[i][j] = maze[i][j]; // Copy the original maze to the new maze array
            }
        }
        stack.push(new Position(0, 0, '0', null)); // Add the starting position to the stack
        while (!stack.isEmpty()) {                                                                                                      
            Position current = stack.pop(); // Pop the next position from the stack
            if (current.i == maze.length - 1 && current.j == maze[0].length - 1) {
                Position temp = current;
                while (temp != null) {
                    maze[temp.i][temp.j] = 'X'; // Mark the path in the original maze
                    temp = temp.parent;
                }
                List<Position> path = new ArrayList<>();
                while (current != null) {
                    path.add(current);
                    current = current.parent;
                }
                Collections.reverse(path);
                return path.toArray(new Position[path.size()]);
            }
            visited[current.i][current.j] = true; // mark the current position as visited
            int[][] neighbors = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } }; // explore the neighboring positions
            for (int[] offset : neighbors) {
                int newi = current.i + offset[0];
                int newj = current.j + offset[1];
                if (newi < 0 || newi >= maze.length || newj < 0 || newj >= maze[0].length) { // Out of bounds
                    continue;
                }
                if (visited[newi][newj] || maze[newi][newj] == '1') { // Already visited or wall
                    continue;
                }
                stack.push(new Position(newi, newj, maze[newi][newj], current)); // pish into stack
            }
        }
        return null;
    }






    
    public static void printPath(Position[] path) { // prints path through maze
        System.out.print("Path: ");
        for (Position p : path) {
            System.out.print("(" + p.i + "," + p.j + ") ");
        }
        System.out.println();
    }

    public static char[][] readMaze(String filename) throws IOException { // reads in maze from file
        char[][] maze;
        Scanner scanner;
        try {
            scanner = new Scanner(new FileInputStream(filename));
        } catch (IOException ex) {
            System.err.println("*** Invalid filename: " + filename);
            return null;
        }
        int N = scanner.nextInt();
        scanner.nextLine();
        maze = new char[N][N];
        int i = 0;
        while (i < N && scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] tokens = line.split("\\s+");
            int j = 0;
            for (; j < tokens.length; j++) {
                maze[i][j] = tokens[j].charAt(0);
            }
            if (j != N) {
                System.err.println("*** Invalid line: " + i + " has wrong # columns: " + j);
                return null;
            }
            i++;
        }
        if (i != N) {
            System.err.println("*** Invalid file: has wrong number of rows: " + i);
            return null;
        }
        return maze;
    }

    public static void printMaze(char[][] maze) { // prints maze array
        System.out.println("Maze: ");
        if (maze == null || maze[0] == null) {
            System.err.println("*** Invalid maze array");
            return;
        }
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                System.out.print(maze[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}