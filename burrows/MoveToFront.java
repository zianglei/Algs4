import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    private static class Node {
        char c = '\0';
        Node next = null;

        public Node() { }

        public Node(char c) {
            this.c = c;
            this.next = null;
        }
    }

    /**
     * apply move-to-front encoding, reading from standard input and
     * writing to standard output
     */
    public static void encode() {
        Node head = initLinkedList(256);
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int t = findPos(head, c);
            if (t == -1) continue;
            BinaryStdOut.write((char) t);
        }
        BinaryStdOut.close();
    }


    /**
     * apply move-to-front decoding, reading from standard input and
     * writing to standard output
     */
    public static void decode() {
        Node head = initLinkedList(256);
        while (!BinaryStdIn.isEmpty()) {
            int d = BinaryStdIn.readInt(8);
            char c = findChar(head, d);
            BinaryStdOut.write(c);
        }
        BinaryStdOut.close();
    }

    private static Node initLinkedList(int r) {
        Node head = new Node();
        Node temp = head;
        for (int i = 0; i < r; i++) {
            Node t = new Node((char) i);
            temp.next = t;
            temp = t;
        }
        return head;
    }

    /**
     * Find the position of the specific char. Return -1 if not found.
     */
    private static int findPos(Node head, char c) {
        int cnt = 0;
        if (head == null) return -1;
        Node temp = head.next;
        Node prev = head;
        while (temp != null && temp.c != c) {
            cnt++;
            prev = temp;
            temp = temp.next;
        }
        if (temp != null) {
            // move this node to the front;
            prev.next = temp.next;
            temp.next = head.next;
            head.next = temp;
        }
        return temp == null ? -1 : cnt;
    }

    /**
     * Find the character in the specific position. Return '\0' if not found
     */
    private static char findChar(Node head, int pos) {
        if (head == null) return '\0';
        Node temp = head.next;
        Node prev = head;
        int cnt = 0;
        while (temp != null && cnt != pos) {
            cnt++;
            prev = temp;
            temp = temp.next;
        }
        char c = '\0';
        if (temp != null) {
            c = temp.c;
            prev.next = temp.next;
            temp.next = head.next;
            head.next = temp;
        }
        return temp == null ? '\0' : c;
    }


    /**
     * if args[0] is "-", apply Burrows-Wheeler transform
     * if args[0] is "+", apply Burrows-Wheeler inverse transform
     */
    public static void main(String[] args) {
        if (args.length < 1) return;
        if (args[0].equals("-")) {
            // encode
            encode();
        } else if (args[0].equals("+")) {
            // encode
            decode();
        }
    }
}
