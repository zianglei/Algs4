/* *****************************************************************************
 *  Name: BoggleSolver.java
 *  Date: 08/26/2020
 *  Description: Find all valid words in a given Boggle board, using a given
 *               directory.
 *****************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BoggleSolver {

    private class TSTNode {
        public int value = 0;
        public char c;
        public TSTNode left, middle, right;
    }

    private static final char ESCAPE_QU_CHAR = '~';
    private final HashMap<Character, List<Integer>> tileLetters;
    private final List<Map<Character, List<Integer>>> tileNeighbors;
    private TSTNode root = null;

    /**
     * Initializes the data structure using the given array of strings as the
     * directory.
     *
     * @param directory Each word in the directory contains only the uppercase
     *                  letters A through Z.
     */
    public BoggleSolver(String[] directory) {
        tileLetters = new HashMap<>();
        tileNeighbors = new ArrayList<>();
        root = null;
        buildTST(directory);
    }

    /**
     * Returns the set of all valid words in the given Boggle board, as
     * an Iterable.
     *
     * @param board a Boggle board.
     * @return an Iterable including all valid words
     */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        tileLetters.clear();
        tileNeighbors.clear();
        scanBoard(board);
        Set<String> words = searchTST(board.rows() * board.cols());
        return words;
    }

    /**
     * Returns the score of the given word if it is in the directory, zero
     * otherwise.
     *
     * @param word The word contains only the uppercase letters A through Z.
     * @return The score of the given word.
     */
    public int scoreOf(String word) {
        TSTNode node = get(word, root, 0);
        if (node == null) return 0;
        int length = node.value;
        if (length < 3) return 0;
        if (length <= 4) return 1;
        if (length == 5) return 2;
        if (length == 6) return 3;
        if (length == 7) return 5;
        else return 11;
    }

    private TSTNode get(String key, TSTNode node, int d) {
        if (node == null) return null;
        char c = key.charAt(d);
        boolean isQu = (c == 'Q' && d < key.length() - 1 && key.charAt(d + 1) == 'U');
        if (isQu) c = ESCAPE_QU_CHAR;
        if (c < node.c) {
            return get(key, node.left, d);
        } else if (c > node.c) {
            return get(key, node.right, d);
        } else if (isQu ? (d < key.length() - 2) : (d < key.length() - 1)) {
            return get(key, node.middle, d + (isQu ? 2 : 1));
        }
        return node;
    }


    /**
     * Builds a Ternary Search Tree corresponding to the directory.
     *
     * @param directory
     */
    private void buildTST(String[] directory) {
        for (String s : directory) {
            put(s);
        }
    }

    /**
     * Puts a string into the TST.
     *
     * @param key
     */
    private void put(String key) {
        root = put(root, key, key.length(), 0);
    }

    private TSTNode put(TSTNode x, String key, int val, int d) {
        char c = key.charAt(d);
        boolean isQu = (c == 'Q' && d < key.length() - 1 && key.charAt(d + 1) == 'U');
        c = isQu ? ESCAPE_QU_CHAR : c;
        if (x == null) {
            x = new TSTNode();
            // 'QU' is represented by '~'
            x.c = isQu ? ESCAPE_QU_CHAR : c;
        }
        if (c < x.c)
            x.left = put(x.left, key, val, d);
        else if (c > x.c)
            x.right = put(x.right, key, val, d);
        else if (isQu ? d < key.length() - 2 : d < key.length() - 1)
            x.middle = put(x.middle, key, val, d + (isQu ? 2 : 1));
        else
            x.value = val;
        return x;
    }

    private void scanBoard(BoggleBoard board) {
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                char c = board.getLetter(i, j);
                if (c == 'Q') c = ESCAPE_QU_CHAR;
                if (!tileLetters.containsKey(c)) {
                    tileLetters.put(c, new ArrayList<>());
                }
                tileLetters.get(c).add(i * board.cols() + j);
                tileNeighbors.add(getNeighbors(i, j, board));
            }
        }
    }

    private Set<String> searchTST(int boardSize) {
        Set<String> res = new HashSet<>();
        StringBuilder b = new StringBuilder();
        ArrayList<Boolean> visited = new ArrayList<Boolean>(
                Collections.nCopies(boardSize, Boolean.FALSE));

        dfsTST(res, b, root, visited, -1);
        return res;
    }


    /**
     * @param res
     * @param builder
     * @param node
     * @param visited
     * @param parentTileIndex 父节点的骰子索引
     */
    private void dfsTST(Set<String> res, StringBuilder builder, TSTNode node, ArrayList<Boolean> visited,
                int parentTileIndex) {
        if (node == null) return;
        Map<Character, List<Integer>> neighbors = null;
        // Get neighbor tiles of the last character if it isn't the first character of a string.
        if (parentTileIndex != -1) {
            neighbors = tileNeighbors.get(parentTileIndex);
        }

        if (parentTileIndex == -1 || neighbors.containsKey(node.c)) {
            List<Integer> list = null;

            if (parentTileIndex == -1) {
                list = tileLetters.get(node.c);
            } else {
                list = neighbors.get(node.c);
            }
            // The char of the TST node is in the neighbors list of the parent TST node tile in the board
            if (list != null) {
                builder.append(node.c == ESCAPE_QU_CHAR ? "QU" : node.c);
                // Maybe there is more than one tile including "node.c" character.
                for (int i : list) {
                    if (visited.get(i)) continue;
                    if (node.value >= 3) {
                        res.add(builder.toString());
                        // node.value = 0; // The word has been put in the list, clear the value.
                    }
                    visited.set(i, Boolean.TRUE);
                    dfsTST(res, builder, node.middle, visited, i);
                    visited.set(i, Boolean.FALSE);
                }
                if (node.c == ESCAPE_QU_CHAR) {
                    builder.deleteCharAt(builder.length() - 1);
                }
                builder.deleteCharAt(builder.length() - 1);
            }
        }
        // 扫描左节点和右节点
        if (node.right != null) {
            dfsTST(res, builder, node.right, visited, parentTileIndex);
        }
        if (node.left != null) {
            dfsTST(res, builder, node.left, visited, parentTileIndex);
        }


    }

    private Map<Character, List<Integer>> getNeighbors(int row, int col, BoggleBoard board) {
        Map<Character, List<Integer>> neighbors = new HashMap<>();
        int brow = board.rows();
        int bcol = board.cols();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                int tempRow = row + i;
                int tempCol = col + j;
                if (tempRow < 0 || tempRow >= brow || tempCol < 0 || tempCol >= bcol) continue;
                char c = board.getLetter(tempRow, tempCol);
                if (c == 'Q') c = ESCAPE_QU_CHAR;
                if (!neighbors.containsKey(c)) {
                    neighbors.put(c, new ArrayList<>());
                }
                neighbors.get(c).add(tempRow * bcol + tempCol);
            }
        }
        return neighbors;
    }

    public static void main(String[] argv) {
        In in = new In(argv[0]);
        BoggleSolver solver = new BoggleSolver(in.readAllStrings());
        StdOut.println(solver.scoreOf("BEAR"));
    }
}
