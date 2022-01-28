/* *****************************************************************************
 *  Name: Adam Kinsey
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.io.FileWriter;
import java.io.IOException;

public class BoardMaker {

    private int[][] tiles;

    public BoardMaker(int n) {
        tiles = this.makeNew(n);
    }

    public int[][] makeNew(int n) {

        int num = n * n;
        int[] tilesArr = new int[num];

        for (int i = 0; i < num; i++) tilesArr[i] = i;
        StdRandom.shuffle(tilesArr);

        tiles = new int[n][n];
        for (int i = 0; i < num; i++) tiles[i / n][i % n] = tilesArr[i];

        return deepCopyTiles(tiles);
    }

    public int[][] getTiles() {
        return deepCopyTiles(tiles);
    }

    private int[][] deepCopyTiles(int[][] tiles1) {
        int n = tiles1.length;
        int[][] tilesCopy = new int[n][n];
        for (int i = 0; i < n; i++) System.arraycopy(tiles1[i], 0, tilesCopy[i], 0, n);
        return tilesCopy;
    }

    public String toString() {

        int n = tiles.length;
        StringBuilder s = new StringBuilder();
        s.append(n + "\n");

        if (n < 4) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    s.append(String.format("%1d", tiles[i][j]) + " ");
                }
                s.append("\n");
            }
        }
        else if (n < 10) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    s.append(String.format("%2d", tiles[i][j]) + " ");
                }
                s.append("\n");
            }
        }
        else if (n < 32) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    s.append(String.format("%3d", tiles[i][j]) + " ");
                }
                s.append("\n");
            }
        }

        return s.toString();


    }

    public void toFile(String filename) {

        String s = this.toString();

        try {
            FileWriter f = new FileWriter(filename);
            f.write(s, 0, s.length());
            f.close();
        }
        catch (IOException e) {
            StdOut.println("Could not write to file " + filename);
        }
        StdOut.println("File written successfully!");
        StdOut.print(this.toString());

    }

    public static void main(String[] args) {

        // Test make()
        BoardMaker b = new BoardMaker(5);
        b.toFile("test5.txt");
    }
}
