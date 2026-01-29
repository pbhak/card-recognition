Roadmap 

1. Color Masking
	Pros: 
		- functional
		- clearly identifies cards and boundaries
	Cons:
		- has some background noise

	Solution: 
		After applying flood fill (described below), remove any white blobs that are too small to be considered a card

2. Flood Fill (card localization - main part)
	General Idea: Utilize an algorithm to explore boundaries.

	More detailed breakdown: 
		Iterate over the entire masked image, once encounter a white pixel, start the flood fill:
			1. create a boolean visited[height][width], this will store visited pixels, just true and false values. 
			2. create an array that will keep track of last entered pixel - you add a white pixel. Once you explore all neighbours around it and find another white pixel - remove the previous point from the array. When array is empty - you are done
			3. initialize helper functions: checkWhite, isInBoundary, checkImageBoundaries
    
			Little detail: do not store every single white pixel inside the card but only the boundaries (more efficient) 

				Breakdown of the functions: 
					boolean checkWhite(r, c) - if statement return true if white
					boolean isInBoundary(r, c) - 
