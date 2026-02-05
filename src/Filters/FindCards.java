package Filters;

import Interfaces.PixelFilter;
import core.DImage;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class FindCards implements PixelFilter {
    short targetR;
    short targetG;
    short targetB;
    double threshold;
    ArrayList<Card> cards;

    public FindCards() {
        targetR = 215;
        targetG = 215;
        targetB = 215;
        threshold = 85;
        cards = new ArrayList<>();
    }

    @Override
    public DImage processImage(DImage img) {
        short[][] reds = img.getRedChannel();
        short[][] greens = img.getGreenChannel();
        short[][] blues = img.getBlueChannel();

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

    private void createCards(DImage img, short[][] reds, short[][] greens, short[][] blues) {
        int currentCard = 0;
        /*
            iterate over black and white masked image
            if a row of white is found, add it as the first row of cards[currentCard]
            then go to the next row from the beginning and repeat until all rows for cards[currentCard] are filled
            repeat for all 12 cards
        */
        for (int y = 0; y < img.getHeight(); y++) {
            int whiteCount = 0;
            for (int x = 0; x < img.getWidth(); x++) {
                if (img.getColorPixelGrid()[y][x] == Color.white.getRGB()) {
                    whiteCount++;
                } else if (whiteCount > 20) {
                    // TODO
                }
            }
        }
    }

    private double distance(short r1, short g1, short b1, short r2, short g2, short b2) {
        return Math.sqrt(((r2 - r1) * (r2 - r1)) + ((g2 - g1) * (g2 - g1)) + ((b2 - b1) * (b2 - b1)));
    }
}

class Card {
    // TODO: hook this to flood fill
}

class FloodFill {
    DImage img;
    short[][] reds;
    short[][] greens;
    short[][] blues;
    ArrayList<Integer[]> queue;

    public FloodFill(DImage img) {
        this.img = img;
        reds = img.getRedChannel();
        greens = img.getGreenChannel();
        blues = img.getBlueChannel();
        this.queue =  new ArrayList<>();
    }


    public void processQueue(int r, int c) {
        String result = "";

        queue.add(new Integer[]{r, c});

        System.out.println("queue size: " + queue.size());
        for (Integer[] i : queue) {
            System.out.println(i[0] + " " + i[1]);
        }

        while (!queue.isEmpty()) {
            System.out.println("checking queue");
            System.out.println(reds[queue.get(0)[0]][queue.get(0)[1]]);
            System.out.println(queue.get(0)[0] +  " " + queue.get(0)[1]);
            if (isInBoundary(queue.get(0)[0], queue.get(0)[1])) {
                System.out.println("inside if statement");
                reds[queue.get(0)[0]][queue.get(0)[1]] = 255;
                greens[queue.get(0)[0]][queue.get(0)[1]] = 0;
                blues[queue.get(0)[0]][queue.get(0)[1]] = 0;
                result += String.format("[%d,%d]\n", queue.get(0)[0], queue.get(0)[1]);
            }
            queue.remove(0);
        }
    }

    public String processImage() {
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                if (reds[i][j] == 255) {
                    processQueue(i, j);
                }
            }
        }
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                if (hasRedNeighbor(i, j)) {
                    reds[i][j] = 254;
                    greens[i][j] = 0;
                    blues[i][j] = 0;
                }
            }
        }

        img.setColorChannels(reds, greens, blues);
        return "";
    }

    public boolean checkImageBoundaries(int r, int c) {
        return !(r <= 0 || r >= img.getHeight() - 1 || c <= 0 || c >= img.getWidth() - 1);
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
                } else if (reds[value[0]][value[1]] == 255) {
                    queue.add(new Integer[]{value[0], value[1]});
                    whiteCount++;
                }
            }
        }
        return blackCount == 1 && whiteCount == 3;
    }

    public boolean hasRedNeighbor(int r, int c) {
        int[][] pointValues = new int[8][2];
        int[] dirR = {-1, -1, 1, 0, 0, 1, 1, 1};
        int[] dirC = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < dirR.length; i++) {
            pointValues[i][0] = r + dirR[i];
            pointValues[i][1] = c + dirC[i];
        }

        for (int[] pointValue : pointValues) {
            if (checkImageBoundaries(pointValue[0], pointValue[1]))
                if (isRed(pointValue[0], pointValue[1])) return true;
        }
        return false;
    }

    public boolean isRed(int r, int c) {
        return reds[r][c] == 255 && greens[r][c] == 0 && blues[r][c] == 0;
    }
}
