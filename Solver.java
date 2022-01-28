/* *****************************************************************************
 *  Name: Adam Kinsey
 *  Date: 26 January 2022
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinearProbingHashST;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Solver {

    private boolean isSolvable;
    private int moves;
    private Node solution;

    private class Node implements Comparable<Node> {

        private String keyString;
        private Board b;
        private int numMoves, hammCost, manhCost;
        private Node prev;

        public Node(Board b, int numMoves, Node prev) {

            this.b = b;
            this.keyString = b.toString().substring(8).replace("[\\t\\n\\x0B\\f\\r]", "");
            this.numMoves = numMoves;
            this.prev = prev;
            this.hammCost = b.hamming() + numMoves;
            this.manhCost = b.manhattan() + numMoves;
        }

        public String toString() {

            String s = ("\n  numMoves = " + numMoves + "  hammCost = " + hammCost
                    + "  manhCost = " + manhCost + "\n" + b);
            return s;
        }

        public int compareTo(Node that) {
            return Integer.compare(this.manhCost, that.manhCost);
        }

        public int hashCode() {
            return this.keyString.hashCode();
        }

        // does this board equal y?
        public boolean equals(Object that) {

            // Follow Java "equals()" conventions
            if (that == this) return true;
            if (that == null) return false;
            if (that.getClass() != this.getClass()) return false;

            return this.b.equals(((Node) that).b);
        }

    }


    // Constructor: finds a solution to the initial or twin boards (using the A* algorithm)
    public Solver(Board initial) {

        LinearProbingHashST<String, Node> inspected, inspected2;
        MinPQ<Node> possible, possible2;

        if (initial == null) throw new IllegalArgumentException("Initial board is null.");

        // Initialize Data Structures
        inspected = new LinearProbingHashST<String, Node>(32);
        inspected2 = new LinearProbingHashST<String, Node>(32);
        possible = new MinPQ<Node>(32);
        possible2 = new MinPQ<Node>(32);
        isSolvable = false;
        moves = -1;
        int move = 0;

        // Insert starting data
        possible.insert(new Node(initial, move, null));
        possible2.insert(new Node(initial.twin(), move,
                                  null)); // Alternate in case "initial" is unsolvable

        // Main solver loop
        Node current, current2;

        while (true) {

            // Remove best next possible move Nodes from both Queues
            current = possible.delMin();
            current2 = possible2.delMin();

            // Save the Nodes being evaluated so that they aren't evaluated again
            inspected.put(current.keyString, current);
            inspected2.put(current2.keyString, current2);

            if (current.b.isGoal()) {
                StdOut.println("\n  Original Board SOLVED!");
                isSolvable = true;
                solution = current;
                break;
            }
            if (current2.b.isGoal()) {
                StdOut.println("\n  Original Board Unsolvable.");
                solution = current2;
                break;
            }

            // DEBUG
            // StdOut.printf("  Attempt %3d , Inspected = %d, Possible = %d \n", move,
            //               inspected.size(), possible.size());
            // StdOut.print(current.b + "\n" + current2.b);
            // StdOut.println("\n Press any key to continue...");
            // StdIn.readLine();

            move++;
            Node temp;

            // Inspect next possible moves in Primary game tree
            for (Board b1 : current.b.neighbors()) {

                // Add all "new" neighbors to possible
                temp = new Node(b1, move, current);
                if (inspected.contains(temp.keyString)) continue;
                possible.insert(temp);
            }

            // Inspect next possible moves in Primary game tree
            for (Board b2 : current2.b.neighbors()) {

                // Add all "new" neighbors to possible
                temp = new Node(b2, move, current);
                if (inspected2.contains(temp.keyString)) continue;
                possible2.insert(temp);
            }
        }

        // After some solution is found, count number of moves.
        if (isSolvable) {
            Node temp = solution;
            StdOut.print("Calculating moves...");
            while (temp != null) {
                moves++;
                StdOut.print(" " + moves);
                temp = temp.prev;
            }
        }

    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return isSolvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        // if (!isSolvable) return -1;
        return moves;
    }

    // sequence of boards in a intest solution; null if unsolvable
    public Iterable<Board> solution() {
        return new Solution();
    }

    private class Solution implements Iterable<Board> {

        public Iterator<Board> iterator() {
            return new Solver.Solution.Sequence();
        }

        private class Sequence implements Iterator<Board> {

            private Node current;

            public Sequence() {
                current = solution;
            }

            public boolean hasNext() {
                return current != null;
            }

            public Board next() {

                if (current == null)
                    throw new NoSuchElementException("There are no more boards in Solution.");
                if (current.prev == null) {
                    Node temp = current;
                    current = null;
                    return temp.b;
                }
                Node temp = current;
                current = current.prev;
                return temp.b;
            }

        }
    }

    // test client (see below)
    public static void main(String[] args) {

        // for each command-line argument
        for (String filename : args) {

            // read in the board specified in the filename
            In in = new In(filename);
            int n = in.readInt();
            int[][] tiles = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    tiles[i][j] = in.readInt();
                }
            }

            // solve the slider puzzle
            Board initial = new Board(tiles);
            Solver solver = new Solver(initial);

            StdOut.println(filename + ": " + solver.moves());

            if (solver.isSolvable())
                StdOut.printf("\n Original Board Solved in %d moves!", solver.moves());
            else
                StdOut.printf("\n Original Board UnSolvable.  Alternate Board solved in %d moves.",
                              solver.moves());


        }
    }

}
