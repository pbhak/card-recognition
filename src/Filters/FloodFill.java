package Filters;

import Interfaces.PixelFilter;
import core.DImage;

import java.awt.*;
import java.util.ArrayList;


public class FloodFill implements PixelFilter {
	public FloodFill(DImage maskedImage) {
		short[][] img = maskedImage.getRedChannel();
	}

	public static ArrayList<Card> processImage() {

	}


	public static boolean checkImageBoundaries(r, c, img) {}

	public static boolean isInBoundary(r, c, img) {
		int[] dirR = {0, 0, 1, -1};
		int[] dirC = {1, -1, 0, 0};

		boolean hasBlack = false;

		for (int i = 0; i < dirR.length(); i++) {
			r += dirR[i];
			c += dirC[i];

			if (img[r][c] == 0) {
				hasBlack = true;
			}

		}
		return hasBlack;
	}

	public static boolean checkWhite(r, c, img) {}

}