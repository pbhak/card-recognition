package Filters;

import java.util.ArrayList;

class Card {
    private ArrayList<Integer[]> boundaryPixels;

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

        return new int[][]{ {minX, minY}, {maxX, maxY}, {minX, maxY}, {maxX, minY} };
    }

    public int getSize() {
        return boundaryPixels.size();
    }
}
