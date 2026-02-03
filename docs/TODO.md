Roadmap 



02/03:
* flood fill works but slow 
    - create a queue for processing white pixels only since it currently processes every pixel which goes through 2x loop neighbour search -- redundancy is insane
    - create boolean visited[][] for efficiency 
    - implement Card class entirely - store cards and filter color noise
    

1. Color Masking
	Pros: 
		- functional
		- clearly identifies cards and boundaries
	Cons:
		- has some background noise

	Solution: 
		After applying flood fill (described below), remove any white blobs that are too small to be considered a card

2. Flood Fill (card localization - main part)
	General Idea: Create an algorithm utilizing BFS or DFS to explore boundaries.

	More detailed breakdown: 
		Iterate over the entire masked image, once encounter a white pixel, start the flood fill:
			1. create a boolean visited[height][width], this will store visited pixels, just true and false values according to the current row and col. 
			2. create an array that will keep track of last entered pixel: you add a white pixel and once you explore all neighbours around it and find another white pixel - remove the previous point from the array. When array is empty - you are done
			3. initialize helper functions: checkWhite, isInBoundary, checkImageBoundaries
    
			Little detail: do not store every single white pixel inside the card class but only the boundaries (more efficient) 

				Breakdown of the functions: 
					boolean checkWhite(r, c) - if statement return true if white
					boolean isInBoundary(r, c) - checks in four directions whether there is a black pixel
					checkImageBoundaries(r, c) - simple function to check whether index is in image boundaries

			4. create a main loop:
				create array to store white pixels in order of discovery
				while pixels in array:
					get first pixel 
					write it to visited, remove from the array
					if isInBoundary = write to the card class
					explore 4 directions from the current pixel, add each to the array
					use helper functions to check if pixel is valid - if valid, then add to the array
					repeat the process until array is empty

				check lengths of each card class object - if too small then it is background noise 
