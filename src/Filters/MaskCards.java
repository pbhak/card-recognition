package Filters;

import Interfaces.PixelFilter;
import core.DImage;

public class MaskCards implements PixelFilter {
    short targetR;
    short targetG;
    short targetB;
    double threshold;

    public MaskCards() {
        targetR = 215;
        targetG = 215;
        targetB = 215;
        threshold = 85;
    }

    @Override
    public DImage processImage(DImage img) {
        short[][] reds = img.getRedChannel();
        short[][] greens = img.getGreenChannel();
        short[][] blues = img.getBlueChannel();

        maskCards(reds, greens, blues);

        img.setColorChannels(reds, greens, blues);
        return img;
    }

    private void maskCards(short[][] reds, short[][] greens, short[][] blues) {
        for (int r = 0; r < reds.length; r++) {
            for (int c = 0; c < reds[r].length; c++) {
                if (distance(reds[r][c], greens[r][c], blues[r][c], targetR, targetG, targetB) > threshold) {
                    reds[r][c] = 0;
                    greens[r][c] = 0;
                    blues[r][c] = 0;
                }
            }
        }
    }

    private double distance(short r1, short g1, short b1, short r2, short g2, short b2) {
        return Math.sqrt(((r2 - r1) * (r2 - r1)) + ((g2 - g1) * (g2 - g1)) + ((b2 - b1) * (b2 - b1)));
    }
}
