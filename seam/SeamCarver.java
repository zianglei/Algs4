/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;

public class SeamCarver {


    private static final int DIR_VERTICAL = 0;
    private static final int DIR_HORIZONTAL = 1;


    private int[][] pixelColorArray;
    private double[][] pixelEnergyArray;


    private boolean isTransposed;

    private int width, height;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException();
        isTransposed = false;

        width = picture.width();
        height = picture.height();

        pixelColorArray = new int[height][width];
        pixelEnergyArray = new double[height][width];

        for (int y = 0; y < picture.height(); y++) {
            for (int x = 0; x < picture.width(); x++) {
                pixelColorArray[y][x] = picture.getRGB(x, y);
            }
        }

        calcEnergy();
    }

    // current picture
    public Picture picture() {
        if (isTransposed) {
            transpose();
        }

        Picture picture = new Picture(width, height);
        for (int row = 0; row < height; row++)
            for (int col = 0; col < width; col++)
                picture.setRGB(col, row, pixelColorArray[row][col]);
        return picture;
    }

    // width of current picture
    public int width() {
        if (isTransposed) return height;
        return width;
    }

    // height of current picture
    public int height() {
        // 当发生转置的时候，width=原图片的高度
        if (isTransposed) return width;
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height()) throw new IllegalArgumentException();
        if (isTransposed) return pixelEnergyArray[x][y];
        return pixelEnergyArray[y][x];
    }

    private int color(int x, int y) {
        return pixelColorArray[y][x];
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (!isTransposed) transpose();
        int[] seam = getSeam();
        return seam;
    }

    private void transpose() {
        // transpose the data
        int[][] newColors = new int[width][height];
        double[][] newEnergies = new double[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                newColors[x][y] = pixelColorArray[y][x];
                newEnergies[x][y] = pixelEnergyArray[y][x];
            }
        }

        pixelColorArray = newColors;
        pixelEnergyArray = newEnergies;

        int temp = height;
        height = width;
        width = temp;

        isTransposed = !isTransposed;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (isTransposed) transpose();
        return getSeam();
    }

    private int[] getSeam() {
        int[] edgeTo = dijkstra();
        int size = height;
        int[] res = new int[size];

        int x = width * height;
        Stack<Integer> s = new Stack<>();
        for (x = edgeTo[x]; x != -1; x = edgeTo[x]) {
            s.push(x / height);
        }
        for (int i = 0; i < res.length; i++) {
            res[i] = s.pop();
        }
        return res;
    }


    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException();
        if (seam.length != width()) throw new IllegalArgumentException();

        if (!isTransposed) transpose();
        realRemoveVerticalSeam(seam);
    }


    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException();
        if (seam.length != height()) throw new IllegalArgumentException();
        if (isTransposed) transpose();

        realRemoveVerticalSeam(seam);
    }

    private void realRemoveVerticalSeam(int[] seam) {

        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= width) throw new IllegalArgumentException();
            if (i < seam.length - 1 &&
                    ((seam[i] > seam[i+1] + 1) || (seam[i] < seam[i + 1] - 1)))
                throw new IllegalArgumentException();
        }

        for (int i = 0; i < seam.length; i++) {
            if (seam[i] > 0) pixelEnergyArray[i][seam[i] - 1] = -1;
            if (seam[i] < width - 1) pixelEnergyArray[i][seam[i] + 1] = -1;
            System.arraycopy(pixelColorArray[i], seam[i] + 1, pixelColorArray[i], seam[i], width - seam[i] - 1);
            System.arraycopy(pixelEnergyArray[i], seam[i] + 1, pixelEnergyArray[i], seam[i], width - seam[i] - 1);
        }

        width -= 1;

        recalcEnergy();
    }


    private int getOffset(int x, int y) {
        return x * height + y;
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private int[] dijkstra() {
        int n = height * width + 1;
        double[] distance = new double[height * width + 1];
        int[] edgeTo = new int[height * width + 1];
        for (int i = 0; i < n; i++) {
            distance[i] = Double.POSITIVE_INFINITY;
        }

        // very fast !!!
        IndexMinPQ<Double> pq = new IndexMinPQ<Double>(n);

        for (int x = 0; x < width; x++) {
            int loc = getOffset(x, 0);
            distance[loc] = pixelEnergyArray[0][x];
            edgeTo[loc] = -1;
            pq.insert(loc, distance[loc]);
        }

        while (!pq.isEmpty()) {
            int u = pq.delMin();
            if (u == height * width) break;

            int x = u / height, y = u % height;
            // if the vertex is the last column or row, directly link it to the virtual end vertex.
            if (y == height - 1)
            {
                int lastVertex = width * height;
                if (distance[lastVertex] > distance[u]) {
                    distance[lastVertex] = distance[u];
                    edgeTo[lastVertex] = u;
                    if (pq.contains(lastVertex)) pq.changeKey(lastVertex, distance[lastVertex]);
                    else pq.insert(lastVertex, distance[lastVertex]);
                }
            } else {
                // else
                for (int j = -1; j < 2; j++) {
                    int vx = 0, vy = 0;
                        // vertical
                    vx = x + j;
                    vy = y + 1;
                    if (!isValid(vx, vy)) continue;
                    int v = vx * height + vy;

                    if (distance[v] > distance[u] + pixelEnergyArray[vy][vx]) {
                        distance[v] = distance[u] + pixelEnergyArray[vy][vx];
                        // update path;
                        edgeTo[v] = u;
                        if (pq.contains(v)) pq.changeKey(v, distance[v]);
                        else pq.insert(v, distance[v]);

                    }
                }
            }

        }

        return edgeTo;
    }

    private double calcPixelEnergy(int x, int y) {
        if (x == 0 || x == width - 1 || y == height - 1 || y == 0) {
            return 1000.0;
        } else {
            int rgbXLeft = color(x - 1, y);
            int rgbXRight = color(x + 1, y);
            int rgbYUp = color(x, y - 1);
            int rgbYDown = color(x, y + 1);
            double deltaRx =  Math.pow((rgbXRight & 0xff) - (rgbXLeft & 0xff), 2)
                    + Math.pow((rgbXRight >> 8 & 0xff) - (rgbXLeft >> 8 & 0xff), 2)
                    + Math.pow((rgbXRight >> 16 & 0xff) - (rgbXLeft >> 16 & 0xff), 2);
            double deltaRy = Math.pow((rgbYUp & 0xff) - (rgbYDown & 0xff), 2)
                    + Math.pow((rgbYUp >> 8 & 0xff) - (rgbYDown >> 8 & 0xff), 2)
                    + Math.pow((rgbYUp >> 16 & 0xff) - (rgbYDown >> 16 & 0xff), 2);
            return Math.sqrt(deltaRx + deltaRy);
        }
    }


    private void recalcEnergy() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (pixelEnergyArray[y][x] == -1) {
                    pixelEnergyArray[y][x] = calcPixelEnergy(x, y);
                }
            }
        }
    }

    private void calcEnergy() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixelEnergyArray[y][x] = calcPixelEnergy(x, y);
            }
        }
    }

}
