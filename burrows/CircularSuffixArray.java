import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {

    private int length;
    private Integer[] indices;
    private static final int CUTOFF = 15;


    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();

        length = s.length();
        indices = new Integer[length];

        for (int i = 0; i < length; i++) {
            indices[i] = i;
        }
        sort(s, 0, length - 1, 0);
    }

    private void sort(String s, int lo, int hi, int d) {
        if (hi <= lo + CUTOFF) {
            insertion(s, lo, hi, d);
            return;
        }
        int lt = lo, gt = hi;
        int v = charAt(s, indices[lo], d);
        int i = lo + 1;
        while (i <= gt) {
            int t = charAt(s, indices[i], d);
            if (t < v) exch(lt++, i++);
            else if (t > v) exch(i, gt--);
            else i++;
        }

        sort(s, lo, lt - 1, d);
        if (v >= 0) sort(s, lt, gt, d + 1);
        sort(s, gt + 1, hi, d);
    }

    private void insertion(String s, int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++) {
            for (int j = i; j > lo && less(s, indices[j], indices[j - 1], d); j--) {
                exch(j, j - 1);
            }
        }
    }

    private int charAt(String s, int start, int offset) {
        if (offset == length) return -1;
        return s.charAt((start + offset) % length);
    }

    private boolean less(String s, int v, int w, int d) {
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (s.charAt((v + i) % len) < s.charAt((w + i) % len)) return true;
            if (s.charAt((v + i) % len) > s.charAt((w + i) % len)) return false;
        }
        return false;
    }

    private void exch(int i, int j) {
        int temp = indices[i];
        indices[i] = indices[j];
        indices[j] = temp;
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
