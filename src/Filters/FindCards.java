package Filters;

import Interfaces.PixelFilter;
import core.DImage;

import java.util.ArrayList;

public class FindCards implements PixelFilter {
    short targetR;
    short targetG;
    short targetB;
    double threshold;
    ArrayList<Card> cards;
    short[][] reds, greens, blues, originalReds, originalGreens, originalBlues;

    public FindCards() {
        targetR = 215;
        targetG = 215;
        targetB = 215;
        threshold = 85;
        cards = new ArrayList<>();
    }

    private short[][] copy(short[][] arr) {
        short[][] newArr = new short[arr.length][];
        for (int i = 0; i < arr.length; i++) newArr[i] = arr[i].clone();
        return newArr;
    }

    @Override
    public DImage processImage(DImage img) {
        reds = img.getRedChannel();
        greens = img.getGreenChannel();
        blues = img.getBlueChannel();

        if (originalReds == null) {
            originalReds = copy(reds);
            originalGreens = copy(greens);
            originalBlues = copy(blues);
        }

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
        CardProcessing f = new CardProcessing(img, originalReds, originalGreens, originalBlues);
        img.setColorChannels(originalReds, originalGreens, originalBlues);
        return f.processImage();
    }

    private double distance(short r1, short g1, short b1, short r2, short g2, short b2) {
        return Math.sqrt(((r2 - r1) * (r2 - r1)) + ((g2 - g1) * (g2 - g1)) + ((b2 - b1) * (b2 - b1)));
    }
}

