package Filters;

import core.DImage;

import java.util.ArrayList;

class CardProcessing {
    DImage img;
    short[][] reds;
    short[][] greens;
    short[][] blues;
    private final short[][] originalReds;
    private final short[][] originalGreens;
    private final short[][] originalBlues;
    ArrayList<Integer[]> queue;
    boolean[][] visited;

    public CardProcessing(DImage img, short[][] originalReds, short[][] originalGreens, short[][] originalBlues) {
        this.img = img;
        reds = img.getRedChannel();
        greens = img.getGreenChannel();
        blues = img.getBlueChannel();
        this.originalReds = originalReds;
        this.originalGreens = originalGreens;
        this.originalBlues = originalBlues;
        this.queue = new ArrayList<>();
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
                originalReds[current[0]][current[1]] = 255;
                originalGreens[current[0]][current[1]] = 0;
                originalBlues[current[0]][current[1]] = 0;
            }
        }
        return card;
    }

    public DImage processImage() {
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

        img.setColorChannels(originalReds, originalGreens, originalBlues);
        return img;
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
                originalReds[newR][newC] = 0;
                originalGreens[newR][newC] = 255;
                originalBlues[newR][newC] = 0;
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
        return blackCount >= 1;
    }
}
