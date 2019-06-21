# worm-math
Java library for handling cellular allometric growth analysis in MatLab

v 1.4 is built to parse data from an Excel file, organize and assign BinaryCellNames and perform some cellular allometric growth analysis
i.e. count the number of concurrently living cells and the number of concurrently living cells in descended from a common ancestor

in future versions, it will be more optimized to work with a microscope running MatLab analysis, and will be used as an additional library
to provide more functionality


6/21/19
	So far, I have completed version 1.4 of the WormMathData program. Its current functionality is based on interpreting data from an excel file, but this can be modified as needed when the specific parameters of the microscope program are discovered.

Functionality:
1.)	Interprets data from an excel file, that is, creates “CellNodes” which are representations of the cell that contain the name, parent, lineage, birth and death time, type, and description, as well as the BinaryCellName
2.)	Sorts the CellNodes into a lineage tree (currently there is no reason for a cell to be the left or right child, but parameters can be introduced later that make this significant)
3.)	Assigns BinaryCellNames to the CellNodes based on the CellNode’s position in the lineage tree
4.)	Counts the number of total cells alive at a specified time
5.)	Counts the number of cells with a common ancestor alive at a specified time

What’s the difference between a CellNode and the BinaryCellName outlined in the paper?
	There is no difference. The CellNode contains all the 4-dimensional information that the BinaryCellName contains as outlined in the paper. I chose the name CellNode as opposed to BinaryCellName, because I wanted to reserve the name BinaryCellName for the literal string of 1s and 0s that the cell’s BinaryCellName would look like. However, the CellNode still contains all of the information that it has to, and can be expanded easily to include any new information we want to include, such as position, yaw/pitch/roll, plane of division, etc.

Binary Tree explanation:
	The Binary Tree I have used to sort the data is no different than a lineage tree that takes into account the time of division of cells. Both maintain the binary structure of cell division, and the Binary Tree holds all the CellNodes, which contain all the information of the cell. As such, all the CellNode information is easily organized and accessible through the Binary Tree. All that the tree does is to provide an internal data structure to hold the CellNodes in an organized way. It doesn’t impact the functionality of the program except to make it easier for me, as a programmer, to scale the program to be modular and able to keep track of larger organisms. It is NOT a replacement or deviation from the BinaryCellName system, but is just a tool I am using for myself to be able to access BinaryCellNames efficiently within the actual program. In terms of the BinaryCellName lineage tree, the Binary Tree is the lineage tree holding the BinaryCellNames represented by the CellNode.

Why use a Binary Tree at all?
	It’s true that I could have just assigned the BinaryCellNames differently, but the Binary Tree is the simplest way to do that. A sort by birth time, for example, would not produce the cells in the correct order to be named, and while it is definitely possible to write an algorithm to assign names, having BinaryCellNames without any further organization will make it difficult to do more analysis. On top of the time-consuming and difficult process of just assigning names without any further data structures is that there is no efficient way to access these BinaryCellNames. Any other functionality we would want to add would be very difficult to undertake and would be very error-prone and not as adaptable. Finally, while a BinaryCellName might be more readable to us and give us a lot more information than a traditional cell name, it doesn’t help a compute to be able to answer queries or find information. In short, a Binary Tree is essential to making this program more efficient and functional.

Can we do cellular allometric growth analysis using this program?
	Yes! The program so far can: count the number of cells in the organism that are alive at a time specified by the user; count the number of cells in a body part that are alive at a time specified by the user. Up next on the todo list are: comparing these two numbers; deeper analysis using positional data to be determined. Currently, it’s not set up to do any actual mathematical analysis of the data, but since we already have the formulae we are working with, we can probably plug these into MatLab for the analysis.

Will this work with MatLab?
	According to the MatLab website, you can in fact use Java libraries with MatLab. That means I won’t have to rewrite the code in another language, just rework it to be able to interface with the microscope which will be done once I have the actual information on how the microscope communicates and delivers the data. As of right now, I believe that it should be able to be used in MatLab if the data has been put into an excel file first.
