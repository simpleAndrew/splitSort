## How to use the solution

Application is started through main method of Starter.java.
It expects 2 parameters to be passed: 
- input path to read unsorted data from
- output path to write sorted output to

I tested it with absolute file names only - 
don't know if it will work with relative names - 
solution is not perfect and it was not the most interesting part.

In case you don't have 1Gb file of unsorted int with you - 
use main of IntegerRandomnessGenerator.java.
It requires one argument - output file to generate int values to;

I verified the app on my laptop: it manages to sort 1Gb file in approx 15 minutes.

##What can be improved
- algorithm of file merging - I think it can be optimised to consume less time, 
as it never remove empty streams and iterates over them every time it looks for next int.
- slicing algoryhm - I went by path of reading AND immediate sorting of data.
It can be re-done with first reading String File and converting it into binary file.
It would allow to reduce number of slices done and speed-up merging.
- memory consumption - I feel that I could improve it and thus reduce number of slices made before merge
- overall naming - would be nice to name everything after map-reduce terms.
- test coverage - despite some tests are present, SplitSort, which is the central conductor, have no tests. It seems to be simple, but still.         
