package Filters;

import Interfaces.PixelFilter;
import core.DImage;
import org.w3c.dom.ls.LSOutput;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

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
        System.out.println("processing image");
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
    Boolean[][] visitedPoints;

    public FloodFill(DImage img) {
        this.img = img;
        reds = img.getRedChannel();
        greens = img.getGreenChannel();
        blues = img.getBlueChannel();
        visitedPoints = new Boolean[img.getHeight()][img.getWidth()];
    }

    public void processImage() {
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                if (isInBoundary(i, j)) {
                    reds[i][j] = 255;
                    greens[i][j] = 0;
                    blues[i][j] = 0;
                    System.out.printf("ran on point (%d,%d)\n", j, i);
                }
            }
        }
        img.setColorChannels(reds, greens, blues);
    }

    public boolean checkImageBoundaries(int r, int c) {
        return r > 0 && r < img.getHeight() - 1 && c > 0 && c < img.getWidth() - 1;
    }

    public boolean isInBoundary(int r, int c) {
        if (visitedPoints[r][c] != null) return visitedPoints[r][c];
        int[] dirR = {0, 0, 1, -1};
        int[] dirC = {1, -1, 0, 0};

        int[][] values = new int[4][2];

        for (int i = 0; i < dirR.length; i++) {
            values[i][0] = r + dirR[i];
            values[i][1] = c + dirC[i];
        }

        int blackCount = 0;
        int whiteCount = 0;
        for (int[] value : values) {
            if (checkImageBoundaries(value[0], value[1])) {
                if (reds[value[0]][value[1]] == 0) {
                    blackCount++;
                } else if (reds[value[0]][value[1]] == 255) {
                    whiteCount++;
                }
            }
        }

        visitedPoints[r][c] = blackCount == 1 && whiteCount == 3;
        return blackCount == 1 && whiteCount == 3;
    }

    public static boolean checkWhite(int r, int c) {
        return false;
    }

}