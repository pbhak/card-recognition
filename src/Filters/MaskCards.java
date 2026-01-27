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
        threshold = 50;
    }

    @Override
    public DImage processImage(DImage img) {
        short[][] reds = img.getRedChannel();
        short[][] greens = img.getGreenChannel();
        short[][] blues = img.getBlueChannel();

        for (int i = 0; i < reds.length; i++) {
            for (int j = 0; j < reds[i].length; j++) {
                if (distance(reds[i][j], greens[i][j], blues[i][j], targetR, targetG, targetB) > threshold) {
                    reds[i][j] = 0;
                    greens[i][j] = 0;
                    blues[i][j] = 0;
                }
            }
        }

        img.setColorChannels(reds, greens, blues);
        return img;
    }

    private double distance(short r1, short g1, short b1, short r2, short g2, short b2) {
        return Math.sqrt(((r2 - r1) * (r2 - r1)) + ((g2 - g1) * (g2 - g1)) + ((b2 - b1) * (b2 - b1)));
    }
}
