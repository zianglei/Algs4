import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.Arrays;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(s);
        StringBuilder b = new StringBuilder();
        int first = -1;
        for (int i = 0; i < csa.length(); i++) {
            int j = csa.index(i);
            if (j == 0) {
                first = i;
                b.append(s.charAt(s.length() - 1));
            } else {
                b.append(s.charAt(j - 1));
            }
        }
        BinaryStdOut.write(first, 32);
        BinaryStdOut.write(b.toString());
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt(32);

        String s = BinaryStdIn.readString();
        String sortedS = sortString(s);
        int[] next = buildNext(s);

        StringBuilder b = new StringBuilder();
        int index = first;
        int cnt = 0;
        while (cnt < s.length()) {
            b.append(sortedS.charAt(index));
            index = next[index];
            cnt++;
        }

        BinaryStdOut.write(b.toString());
        BinaryStdOut.close();
    }

    private static String sortString(String s) {
        char[] arr = s.toCharArray();
        Arrays.sort(arr);
        return String.valueOf(arr);
    }

    private static int[] buildNext(String s) {
        char[] arr = s.toCharArray();
        int[] next = new int[s.length()];
        for (int i = 0; i < next.length; i++) {
            next[i] = i;
        }

        next = Arrays.stream(next).boxed().sorted((a1, a2) -> {
            if (arr[a1] != arr[a2]) return arr[a1] - arr[a2];
            else return a1 - a2;
        }).mapToInt(Integer::intValue).toArray();

        return next;
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args.length < 1) return;
        if (args[0].equals("-"))
            transform();
        else if (args[0].equals("+"))
            inverseTransform();
    }
}
