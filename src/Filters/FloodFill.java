package Filters;

import core.DImage;
import java.util.ArrayList;


public class FloodFill {
	short[][] img;
	public FloodFill(DImage maskedImage) {
		img = maskedImage.getRedChannel();
	}

	public boolean checkImageBoundaries(int r, int c) {
        return false;
    }

	public boolean isInBoundary(int r, int c) {
		int[] dirR = {0, 0, 1, -1};
		int[] dirC = {1, -1, 0, 0};

		boolean hasBlack = false;

		for (int i = 0; i < dirR.length; i++) {
			r += dirR[i];
			c += dirC[i];

			if (img[r][c] == 0) {
				hasBlack = true;
			}

		}
		return hasBlack;
	}

	public static boolean checkWhite(int r, int c) {
		return false;
	}

}