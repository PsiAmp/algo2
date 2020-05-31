package week2;

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    private final double HIGH_ENERGY_PIXEL = 1000;

    private Picture picture;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException();
        this.picture = new Picture(picture);
    }

    // current picture
    public Picture picture() {
        return new Picture(picture);
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || y < 0 || x > width() - 1 || y > height() - 1) throw new IllegalArgumentException();

        if (x == 0 || y == 0 || x == width() - 1 || y == height() - 1)
            return HIGH_ENERGY_PIXEL;

        int rgb1 = picture.getRGB(x - 1, y);
        int rgb2 = picture.getRGB(x + 1, y);
        int dR = ((rgb2 & 0xFF0000) >> 16) - ((rgb1 & 0xFF0000) >> 16);
        int dG = ((rgb2 & 0xFF00) >> 8) - ((rgb1 & 0xFF00) >> 8);
        int dB = (rgb2 & 0xFF) - (rgb1 & 0xFF);
        int dX2 = dR * dR + dG * dG + dB * dB;

        rgb1 = picture.getRGB(x, y - 1);
        rgb2 = picture.getRGB(x, y + 1);
        dR = ((rgb2 & 0xFF0000) >> 16) - ((rgb1 & 0xFF0000) >> 16);
        dG = ((rgb2 & 0xFF00) >> 8) - ((rgb1 & 0xFF00) >> 8);
        dB = (rgb2 & 0xFF) - (rgb1 & 0xFF);
        int dY2 = dR * dR + dG * dG + dB * dB;

        return Math.sqrt(dX2 + dY2);
    }

    private double[][] createEnergyMatrix() {
        double g[][] = new double[width()][height()];
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                g[i][j] = energy(i, j);
            }
        }
        return g;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (height() < 3 || width() < 3) return new int[width()];

        double g[][] = createEnergyMatrix();

        // Calc top/bot paths
        for (int x = 1; x < width(); x++) {
            g[x][0] += g[x - 1][0];
            g[x][height() - 1] += g[x - 1][height() - 1];
        }

        for (int x = 1; x < width(); x++) {
            for (int y = 1; y < height() - 1; y++) {
                g[x][y] += Math.min(Math.min(g[x - 1][y - 1], g[x - 1][y]), g[x - 1][y + 1]);
            }
        }

        double minWeight = g[width() - 1][0];
        int minY = 0;
        for (int y = 1; y < height(); y++) {
            if (g[width() - 1][y] < minWeight) {
                minY = y;
                minWeight = g[width() - 1][y];
            }
        }

        int horizontalSeam[] = new int[width()];
        horizontalSeam[width() - 1] = minY;
        for (int x = width() - 2; x >= 0; x--) {
            int min = minY;
            if (g[x][minY - 1] < g[x][minY]) {
                min = minY - 1;
            }
            if (g[x][minY + 1] < g[x][min]) {
                min = minY + 1;
            }
            horizontalSeam[x] = min;
            minY = min;
        }

        return horizontalSeam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (width() < 3 || height() < 3) return new int[height()];

        double g[][] = createEnergyMatrix();

        // Calc left/right paths
        for (int y = 1; y < height(); y++) {
            g[0][y] += g[0][y - 1];
            g[width() - 1][y] += g[width() - 1][y - 1];
        }

        for (int y = 1; y < height(); y++) {
            for (int x = 1; x < width() - 1; x++) {
                g[x][y] += Math.min(Math.min(g[x - 1][y - 1], g[x][y - 1]), g[x + 1][y - 1]);
            }
        }

        double minWeight = g[0][height() - 1];
        int minx = 0;
        for (int x = 1; x < width(); x++) {
            if (g[x][height() - 1] < minWeight) {
                minx = x;
                minWeight = g[x][height() - 1];
            }
        }

        int verticalSeam[] = new int[height()];
        verticalSeam[height() - 1] = minx;
        for (int y = height() - 2; y >= 0; y--) {
            int min = minx;
            if (g[minx - 1][y] < g[minx][y]) {
                min = minx - 1;
            }
            if (g[minx + 1][y] < g[min][y]) {
                min = minx + 1;
            }
            verticalSeam[y] = min;
            minx = min;
        }

        return verticalSeam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null || seam.length != width()) throw new IllegalArgumentException();
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= height()) throw new IllegalArgumentException();
        }
        for (int i = 1; i < seam.length; i++) {
            if (Math.abs(seam[i - 1] - seam[i]) > 1) throw new IllegalArgumentException();
        }

        Picture p = new Picture(width(), height() - 1);
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < seam[x]; y++) {
                p.set(x, y, picture.get(x, y));
            }
            for (int y = seam[x] + 1; y < height(); y++) {
                p.set(x, y - 1, picture.get(x, y));
            }
        }
        picture = p;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null || seam.length != height()) throw new IllegalArgumentException();
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= width()) throw new IllegalArgumentException();
        }
        for (int i = 1; i < seam.length; i++) {
            if (Math.abs(seam[i - 1] - seam[i]) > 1) throw new IllegalArgumentException();
        }

        Picture p = new Picture(width() - 1, height());
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < seam[y]; x++) {
                p.set(x, y, picture.get(x, y));
            }
            for (int x = seam[y] + 1; x < width(); x++) {
                p.set(x - 1, y, picture.get(x, y));
            }
        }

        picture = p;
    }

    //  unit testing (optional)
    public static void main(String[] args) {
    }

}
