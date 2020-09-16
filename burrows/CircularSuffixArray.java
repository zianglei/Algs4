import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class CircularSuffixArray {

    private int length;
    private Integer[] indices;


    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();

        length = s.length();
        indices = new Integer[length];

        for (int i = 0; i < length; i++) {
            indices[i] = i;
        }
        Arrays.sort(indices, (p1, p2) -> {
            for (int i = 0; i < length; i++) {
                int a1 = (p1 + i) % length;
                int a2 = (p2 + i) % length;
                if (s.charAt(a1) == s.charAt(a2))
                    continue;
                return Character.compare(s.charAt(a1), s.charAt(a2));
            }
            return 0;
        });
    }

    // length of s
    public int length() {
        return length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i >= length || i < 0) throw new IllegalArgumentException();
        return indices[i];
    }

    private void printResult(String s) {
        StdOut.println(" i       Original Suffixes           Sorted Suffixes         index[i]");
        StdOut.println("--    -----------------------     -----------------------    --------");

        char[] arr = s.toCharArray();
        for (int i = 0; i < s.length(); i++) {
            StringBuilder b = new StringBuilder();
            if (i < 10)
                b.append(" " + i);
            else
                b.append(i);
            b.append("    ");

            int index = 0;
            while (index < s.length()) {
                b.append(arr[(i + index) % s.length()]);
                if (index != s.length() - 1)
                    b.append(" ");
                index++;
            }
            b.append("     ");
            index = 0;
            int start = indices[i];
            while (index < s.length()) {
                b.append(arr[(start + index) % s.length()]);
                if (index != s.length() - 1)
                    b.append(" ");
                index++;
            }

            b.append("    ");
            b.append(indices[i]);
            StdOut.println(b.toString());
        }
    }



    // unit testing
    public static void main(String[] args) {
        String s = args[0];
        CircularSuffixArray csa = new CircularSuffixArray(s);
        csa.printResult(s);
    }
}
