/* *****************************************************************************
 *  Name: Adam Kinsey
 *  Date: 26 January 2022
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Board {

    private int[][] tiles;
    private int n;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {

        this.n = tiles.length;
        this.tiles = deepCopyTiles(tiles);

    }


    // string representation of this board
    public String toString() {

        StringBuilder s = new StringBuilder();
        s.append("  " + String.format("%3d", n) + "\n  ");
        for (int i = 0; i < n; i++) {

            for (int j = 0; j < n; j++) {
                s.append(String.format("%3d ", tiles[i][j]));
            }
            s.append("\n  ");
        }
        return s.toString();
    }


    // board dimension n
    public int dimension() {
        return n;
    }


    // number of tiles out of place
    public int hamming() {

        int num = 1, count = 0;
        // Count out-of-place tiles in rows 1 to n-1
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] != num++) count++;
            }
        }
        // Count out-of-place tiles in row n
        for (int i = 0; i < n - 1; i++) {
            if (tiles[n - 1][i] != num++) count++;
        }
        if (tiles[n - 1][n - 1] != 0) count++;

        return count;
    }


    // sum of Manhattan distances between tiles and goal
    public int manhattan() {

        int goalI, goalJ, num, manhat = 0;
        // Calculate manhattan sum for rows 1 to n
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                num = tiles[i][j];
                if (num == 0) {
                    goalI = n - 1;
                    goalJ = n - 1;
                }
                else {
                    goalI = (num - 1) / n;
                    goalJ = (num - 1) % n;
                }

                manhat += Math.abs(i - goalI) + Math.abs(j - goalJ);
            }
        }
        return manhat;
    }


    // is this board the goal board?
    public boolean isGoal() {

        boolean finish = true;

        int count = 0;
        // Check rows 1 to n-1
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] != ++count) {
                    finish = false;
                    break;
                }
            }
            if (!finish) break;
        }
        // Check row n
        for (int i = 0; i < n - 1; i++) {
            if (tiles[n - 1][i] != ++count) {
                finish = false;
                break;
            }
        }
        if (tiles[n - 1][n - 1] != 0) finish = false;

        return finish;
    }

    // does this board equal y?
    public boolean equals(Object y) {

        // Follow Java "equals()" conventions
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;

        String thisBoard = this.toString();
        String thatBoard = y.toString();

        return thisBoard.equals(thatBoard);
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        return new Neighbors(this.tiles);
    }

    private class Neighbors implements Iterable<Board> {

        private int[][] tilesCopy;
        private int zeroRow, zeroCol;
        private boolean[] neighborExists;
        private int numNeighbors;

        // Constructor
        public Neighbors(int[][] currentTiles) {

            // Copy board and Find location of zero
            int n1 = currentTiles.length;
            tilesCopy = new int[n1][n1];

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    tilesCopy[i][j] = currentTiles[i][j];
                    if (currentTiles[i][j] == 0) {
                        zeroRow = i;
                        zeroCol = j;
                    }
                }
            }

            // Calculate "neighbors"
            numNeighbors = 0;
            neighborExists = new boolean[4];
            if (zeroRow > 0) {              // If zero not in top row
                numNeighbors++;
                neighborExists[0] = true;
            }
            if (zeroCol < n1 - 1) {         // If zero not in right column
                numNeighbors++;
                neighborExists[1] = true;
            }
            if (zeroRow < n1 - 1) {         // If zero not in bottom row
                numNeighbors++;
                neighborExists[2] = true;
            }
            if (zeroCol > 0) {              // If zero not in left column
                numNeighbors++;
                neighborExists[3] = true;
            }
        }

        public Iterator<Board> iterator() {
            return new NeighborsIter();
        }

        private class NeighborsIter implements Iterator<Board> {

            private int returned = 0;
            private int neighIndex = 0;

            public boolean hasNext() {
                return returned < numNeighbors;
            }

            public Board next() {

                if (returned >= numNeighbors)
                    throw new NoSuchElementException("There are no neighbors left in iterator.");

                // Find next neighbor
                while (!neighborExists[neighIndex]) neighIndex++;

                Board b;

                switch (neighIndex) {
                    case 0:     // Shift-down neighbor
                        tilesCopy[zeroRow][zeroCol] = tilesCopy[zeroRow - 1][zeroCol];
                        tilesCopy[zeroRow - 1][zeroCol] = 0;
                        b = new Board(tilesCopy);
                        tilesCopy[zeroRow - 1][zeroCol] = tilesCopy[zeroRow][zeroCol];
                        tilesCopy[zeroRow][zeroCol] = 0;
                        break;

                    case 1:     // Shift-left neighbor
                        tilesCopy[zeroRow][zeroCol] = tilesCopy[zeroRow][zeroCol + 1];
                        tilesCopy[zeroRow][zeroCol + 1] = 0;
                        b = new Board(tilesCopy);
                        tilesCopy[zeroRow][zeroCol + 1] = tilesCopy[zeroRow][zeroCol];
                        tilesCopy[zeroRow][zeroCol] = 0;
                        break;

                    case 2:     // Shift-up neighbor
                        tilesCopy[zeroRow][zeroCol] = tilesCopy[zeroRow + 1][zeroCol];
                        tilesCopy[zeroRow + 1][zeroCol] = 0;
                        b = new Board(tilesCopy);
                        tilesCopy[zeroRow + 1][zeroCol] = tilesCopy[zeroRow][zeroCol];
                        tilesCopy[zeroRow][zeroCol] = 0;
                        break;

                    case 3:     // Shift-right neighbor
                        tilesCopy[zeroRow][zeroCol] = tilesCopy[zeroRow][zeroCol - 1];
                        tilesCopy[zeroRow][zeroCol - 1] = 0;
                        b = new Board(tilesCopy);
                        tilesCopy[zeroRow][zeroCol - 1] = tilesCopy[zeroRow][zeroCol];
                        tilesCopy[zeroRow][zeroCol] = 0;
                        break;

                    default:
                        throw new IndexOutOfBoundsException(
                                "Invalid Index: Cannot generate neighbor board.");


                }
                neighIndex++;
                returned++;
                return b;
            }


        }

        public void remove() {
            throw new UnsupportedOperationException("Iterator remove() is not supported)");
        }
    }


    private int[][] deepCopyTiles(int[][] tiles1) {

        int n1 = tiles1.length;
        int[][] tilesCopy = new int[n1][n1];
        for (int i = 0; i < n1; i++) System.arraycopy(tiles1[i], 0, tilesCopy[i], 0, n1);
        return tilesCopy;
    }


    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {

        int row = 0;
        if (tiles[row][0] == 0 || tiles[row][1] == 0) row++;

        int[][] twinTiles = deepCopyTiles(tiles);
        int swap = twinTiles[row][0];
        twinTiles[row][0] = twinTiles[row][1];
        twinTiles[row][1] = swap;
        Board twin = new Board(twinTiles);
        return twin;
    }

    private static int[][] readPuzzleFile(String file) {

        In input = new In(file);
        if (!input.exists()) throw new IllegalArgumentException("file is invalid.");

        int n1 = Integer.parseInt(input.readLine());
        int[][] board = new int[n1][n1];

        int[] nums = input.readAllInts();

        int k = 0;
        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < n1; j++) board[i][j] = nums[k++];
        }

        return board;
    }

    private static boolean testHamMan(String filename, int hamm, int manh) {

        Board b = new Board(readPuzzleFile(filename));
        boolean passed = false;
        int hamm00 = b.hamming(), manh00 = b.manhattan();
        if (hamm00 == hamm && manh00 == manh) passed = true;
        StdOut.print("\n  " + filename + " : Hamming = " + b.hamming()
                             + " Manhattan = " + b.manhattan());
        if (passed) StdOut.print(" PASSED");
        else StdOut.print(" FAILED");
        return passed;
    }

    private int[][] getTiles() {
        return this.deepCopyTiles(this.tiles);
    }

    //
    private int[] testNeighbors(Board that) {

        int[][] thatTiles = that.getTiles();

        if (this.tiles.length != thatTiles.length) {
            throw new InputMismatchException("Array sizes mismatched in testNeighbors()");
        }

        int oldZeroRow = 0, oldZeroCol = 0, newZeroRow = 0, newZeroCol = 0, mismatchCount = 0;
        // int prevSwapRow, prevSwapCol, newSwapRow, newSwapCol;
        int n1 = this.tiles.length;

        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < n1; j++) {

                if (this.tiles[i][j] != thatTiles[i][j]) {
                    mismatchCount++;

                    if (this.tiles[i][j] == 0) {
                        oldZeroRow = i;
                        oldZeroCol = j;
                        // StdOut.printf("\n  Old Zero Pos = (%d, %d)", i, j);
                    }
                    if (thatTiles[i][j] == 0) {
                        newZeroRow = i;
                        newZeroCol = j;
                        // StdOut.printf("\n  New Zero Pos = (%d, %d)", i, j);
                    }
                }
            }
        }


        int delX = newZeroCol - oldZeroCol;
        // StdOut.printf("\n  ZeroCol new - old = delX : %d - %d = %d", newZeroCol, oldZeroCol, delX);
        int delY = newZeroRow - oldZeroRow;
        // StdOut.printf("\n  ZeroRow new - old = delY : %d - %d = %d", newZeroRow, oldZeroRow, delY);
        int[] toReturn = new int[3];
        toReturn[0] = delX;
        toReturn[1] = delY;
        toReturn[2] = mismatchCount;

        return toReturn;
    }


    // unit testing (not graded)
    public static void main(String[] args) {

        String filename;

        if (args.length == 1) filename = args[0];
        else filename = "puzzle00.txt";

        boolean allPassed = true;

        // Test constructor, dimension(), and toString()
        StdOut.println("\nTest 1: Constructor, dimension(), and toString()");
        StdOut.println("  Reading" + filename + "...");
        Board b = new Board(readPuzzleFile(filename));
        StdOut.println("  dimension() = " + b.dimension());
        StdOut.println("Input Board: ");
        StdOut.print(b);

        // Test Hamming and Manhattan
        StdOut.println("\nTest 2: Hamming and Manhattan calculations");

        filename = "puzzle00.txt";
        if (!testHamMan(filename, 0, 0)) allPassed = false;
        filename = "puzzle01.txt";
        if (!testHamMan(filename, 2, 2)) allPassed = false;
        filename = "puzzle02.txt";
        if (!testHamMan(filename, 3, 4)) allPassed = false;
        filename = "puzzle03.txt";
        if (!testHamMan(filename, 4, 4)) allPassed = false;
        filename = "puzzle04.txt";
        if (!testHamMan(filename, 5, 8)) allPassed = false;
        filename = "puzzle05.txt";
        if (!testHamMan(filename, 6, 8)) allPassed = false;
        filename = "puzzle06.txt";
        if (!testHamMan(filename, 7, 12)) allPassed = false;
        filename = "puzzle09.txt";
        if (!testHamMan(filename, 10, 14)) allPassed = false;


        // Test neighbors
        StdOut.println("\n\nTest 3: Generating Neighbors");

        filename = "puzzle05.txt";
        Board b05 = new Board(readPuzzleFile(filename));
        StdOut.println("\n  Original Board: ");
        StdOut.print(b05);
        StdOut.println("\n  Neighbors: ");

        int[] testRes;
        for (Board i : b05.neighbors()) {
            testRes = b05.testNeighbors(i);

            StdOut.printf("\n  Mismatches = %d , delX = %d , delY = %d\n", testRes[2], testRes[0],
                          testRes[1]);
            StdOut.print(i);
        }

        filename = "puzzle08.txt";
        Board b08 = new Board(readPuzzleFile(filename));
        StdOut.println("\n  Original Board: ");
        StdOut.print(b08);
        StdOut.println("\n  Neighbors: ");

        for (Board i : b08.neighbors()) {
            testRes = b08.testNeighbors(i);

            StdOut.printf("\n  Mismatches = %d , delX = %d , delY = %d\n", testRes[2], testRes[0],
                          testRes[1]);
            StdOut.print(i);
        }

        // Test twin
        filename = "puzzle06.txt";
        Board b06 = new Board(readPuzzleFile(filename));
        StdOut.println("\n  Original Board: " + filename);
        StdOut.print(b06);
        StdOut.println("\n  Twin: ");
        StdOut.print(b06.twin());

        filename = "puzzle08.txt";
        Board b08b = new Board(readPuzzleFile(filename));
        StdOut.println("\n  Original Board: " + filename);
        StdOut.print(b08b);
        StdOut.println("\n  Twin: ");
        StdOut.print(b08b.twin());

        if (allPassed) StdOut.print("\n\n  ALL PASSED\n");
        else StdOut.print("\n\n  Some Failures.\n");


    }
}
