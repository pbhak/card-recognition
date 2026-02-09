package Filters;

import Interfaces.PixelFilter;
import core.DImage;

import java.util.ArrayList;
import java.util.function.DoubleToIntFunction;

public class FindCards implements PixelFilter {
    short targetR;
    short targetG;
    short targetB;
    double threshold;
    ArrayList<Card> cards;
    short[][] reds, greens, blues;

    public FindCards() {
        targetR = 215;
        targetG = 215;
        targetB = 215;
        threshold = 85;
        cards = new ArrayList<>();
    }

    @Override
    public DImage processImage(DImage img) {
        reds = img.getRedChannel();
        greens = img.getGreenChannel();
        blues = img.getBlueChannel();

        // mask cards
        for (int r = 0; r < reds.length; r++) {
            for (int c = 0; c < reds[r].length; c++) {
                double pixelDistance = distance(reds[r][c], greens[r][c], blues[r][c], targetR, targetG, targetB);
                if (pixelDistance <= threshold) {
                    reds[r][c] = 255;
                    greens[r][c] = 255;
                    blues[r][c] = 255;
                } else if (pixelDistance > threshold) {
                    reds[r][c] = 0;
                    greens[r][c] = 0;
                    blues[r][c] = 0;
                }
            }
        }

        // set image before creating the class instance
        img.setColorChannels(reds, greens, blues);

        // flood fill main
        FloodFill f = new FloodFill(img);
        f.processImage();

        return img;
    }

    private double distance(short r1, short g1, short b1, short r2, short g2, short b2) {
        return Math.sqrt(((r2 - r1) * (r2 - r1)) + ((g2 - g1) * (g2 - g1)) + ((b2 - b1) * (b2 - b1)));
    }
}

class Card {
    ArrayList<Integer[]> boundaryPixels;

    public Card() {
        boundaryPixels = new ArrayList<>();
    }

    public void addElement(Integer[] boundaryPixel) {
        boundaryPixels.add(boundaryPixel);
    }

    public int[][] findCorners() {
        Integer minX = boundaryPixels.get(0)[0];
        Integer minY = boundaryPixels.get(0)[1];
        Integer maxX = boundaryPixels.get(boundaryPixels.size() - 1)[0];
        Integer maxY = boundaryPixels.get(boundaryPixels.size() - 1)[1];

        for (Integer[] i : boundaryPixels) {
            minX = Math.min(minX, i[0]);
            minY = Math.min(minY, i[1]);
            maxX = Math.max(maxX, i[0]);
            maxY = Math.max(maxY, i[1]);
        }

        if (maxY - minY < 150) return new int[][]{{-1, -1}};

        return new int[][]{{minX, minY}, {maxX, maxY}, {minX, maxY}, {maxX, minY}};
    }

    public int getSize() {
        return boundaryPixels.size();
    }
}

class FloodFill {
    DImage img;
    short[][] reds, greens, blues;
    ArrayList<Integer[]> queue;
    boolean[][] visited;

    public FloodFill(DImage img) {
        this.img = img;
        reds = img.getRedChannel();
        greens = img.getGreenChannel();
        blues = img.getBlueChannel();
        this.queue =  new ArrayList<>();
        this.visited = new boolean[img.getHeight()][img.getWidth()];
    }


    public Card processQueue(int r, int c) {
        Card card = new Card();

        queue.add(new Integer[]{r, c});

        while (!queue.isEmpty()) {
            Integer[] current = queue.get(0);
            card.addElement(current);
            queue.remove(0);
            if (visited[current[0]][current[1]]) continue;
            visited[current[0]][current[1]] = true;

            if (isInBoundary(current[0], current[1])) {
                reds[current[0]][current[1]] = 255;
                greens[current[0]][current[1]] = 0;
                blues[current[0]][current[1]] = 0;
            }
        }
        return card;
    }

    public void processImage() {
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                if (reds[i][j] == 255 && !visited[i][j]) {
                    Card currCard = processQueue(i, j);
                    if (currCard.getSize() > 100) {
                        int[][] corners = currCard.findCorners();
                        for (int[] corner : corners) {
                            if (corner[0] == -1 && corner[1] == -1) continue;
                            System.out.printf("Found corner at (%d, %d)\n", corner[1], corner[0]);
                            drawCorner(corner[0], corner[1]);
                        }
                    }
                }
            }
        }

        img.setColorChannels(reds, greens, blues);
    }

    private void drawCorner(int r, int c) {
        // 3x3 grid with its center at (r, c)
        System.out.printf("Drawing corner with center at (%d, %d)\n", r, c);
        int[] dirR = {0, -1, 0, -1, 0, 0, 1, 1, 1};
        int[] dirC = {0, -1, -1, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < dirR.length; i++) {
            int newR = r + dirR[i];
            int newC = c + dirC[i];
            if (checkImageBoundaries(newR, newC)) {
                reds[newR][newC] = 0;
                greens[newR][newC] = 255;
                blues[newR][newC] = 0;
            }
        }
    }

    public boolean checkImageBoundaries(int r, int c) {
        return r > 0 && r < img.getHeight() - 1 && c > 0 && c < img.getWidth() - 1;
    }

    public boolean isInBoundary(int r, int c) {
        int[] dirR = {0, 0, 1, -1};
        int[] dirC = {1, -1, 0, 0};

        int blackCount = 0;

        for (int i = 0; i < dirR.length; i++) {
            int nr = r + dirR[i];
            int nc = c + dirC[i];

            if (checkImageBoundaries(nr, nc)) {
                if (reds[nr][nc] == 0) {
                    blackCount++;
                } else if (!visited[nr][nc]) {
                    queue.add(new Integer[]{nr, nc});
                }
            }
        }
        return blackCount >=1;
    }
}

