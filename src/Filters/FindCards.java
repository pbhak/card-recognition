package Filters;

import Interfaces.PixelFilter;
import core.DImage;
import org.w3c.dom.ls.LSOutput;

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
    boolean[][] visited;

    public FloodFill(DImage img) {
        this.img = img;
        reds = img.getRedChannel();
        greens = img.getGreenChannel();
        blues = img.getBlueChannel();
        this.queue =  new ArrayList<>();
        this.visited = new boolean[img.getHeight()][img.getWidth()];
    }


    public void processQueue(int r, int c) {
        queue.add(new Integer[]{r, c});

        while (!queue.isEmpty()) {
            Integer[] current = queue.get(0);
            queue.remove(0);
            if (visited[current[0]][current[1]]) continue;
            visited[current[0]][current[1]] = true;

            if (isInBoundary(current[0], current[1])) {
                reds[current[0]][current[1]] = 255;
                greens[current[0]][current[1]] = 0;
                blues[current[0]][current[1]] = 0;
            }
        }
    }


    public void processImage() {
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                if (reds[i][j] == 255 && !visited[i][j]) {
                    processQueue(i, j);
                }
            }
        }

        img.setColorChannels(reds, greens, blues);
    }

    public boolean checkImageBoundaries(int r, int c) {
        return (r <= 0 || r >= img.getHeight() - 1 || c <= 0 || c >= img.getWidth() - 1);
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

    public static boolean checkWhite(int r, int c) {
        return false;
    }

}

