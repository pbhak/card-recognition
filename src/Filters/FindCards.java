package Filters;

import Interfaces.PixelFilter;
import core.DImage;

import java.awt.*;
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
                    reds[r][c] = 255;
                    greens[r][c] = 255;
                    blues[r][c] = 255;
                }
            }
        }

        createCards(img, reds, greens, blues);

        img.setColorChannels(reds, greens, blues);
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

}
