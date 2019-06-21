import java.io.*;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import structure5.*;
/**
 * @author Nate Pappalardo
 * @version 1.4
 * Class to parse worm data table into a more readable format
 *
 */
public class WormMathData {
	
	private static BinaryTree<CellNode> wormTree;
	private static ArrayList<CellNode> wormCells;
	
	//constructor to initialize arraylist and binary tree
	public WormMathData() {
		wormCells =  new ArrayList<CellNode>();
		wormTree = new BinaryTree<CellNode>(new CellNode());
	}
	
	/**
	 * 
	 * @param file
	 * @throws IOException
	 * @post fills the wormCells list with BinaryCellNames
	 */
	private void parseExcelData(File file) throws IOException {
		
		//opens the file, throws IOException if file cannot be opened
		FileInputStream info;
		if(file.exists() && file.isFile()) {
			info = new FileInputStream(file);
		}else {
			throw new IOException();
		}
		
		//opens the file as an xlsx worksheet
		Workbook workbook = WorkbookFactory.create(info);
		Sheet spreadsheet = workbook.getSheetAt(0);
		
		//iterator that includes all the rows
		Iterator<Row> rowIterator = spreadsheet.iterator();
		
		//skips the header row
		rowIterator.next();
		
		while(rowIterator.hasNext()) {
			
			//breaks down a row into individual cells
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			ArrayList<Object> curCell = new ArrayList<Object>(7);
			
			while(cellIterator.hasNext()) {
				
				//adds params corresponding to the columns to a list
				Cell cell = cellIterator.next();
				int col = cell.getColumnIndex();
				
				switch(cell.getCellType()) {
					
				case NUMERIC:
					curCell.add(col, ((Double)cell.getNumericCellValue()));;
					break;
				
				case STRING:
					curCell.add(col, cell.getStringCellValue());
					break;
					
				default:
					break;
				}
				
			}
			
			//creates a cell node with the info from the columns passed in as parameters and adds to the arrayList
			CellNode newCell = new CellNode(curCell.get(0).toString(), curCell.get(1).toString(), curCell.get(2).toString(), 
					(double) curCell.get(3), (double) curCell.get(4), curCell.get(5).toString(), curCell.get(6).toString());
			
			wormCells.add(newCell);
		}
		
	}
	
	/**
	 * @pre cell data is parsed
	 * @post creates a tree of cellNodes based on their parents
	 * @return returns true if everything has been sorted
	 */
	private boolean sortData() {
		
		//finds the zygote first to set the root of the tree
		for(CellNode cell: wormCells) {
			if(cell.getParentName().equals("NONE")) {
				wormTree.root().setValue(cell);
				break;
			}
		}
		
		//recursive call
		sortDataHelper(wormTree.root());
		
		//returns true if all elements in wormCells have been put into the tree
		return wormTree.size() == wormCells.size();
	}
	
	/**
	 * recursive method to build a tree of CellNodes
	 * @param curPos is what we are finding the child of
	 * @return returns true if wormCells is empty (meaning every CellNode in it
	 * has been sorted)
	 * 
	 * TODO once we have specific reasons for setting cells as left/right childs, use that
	 * to assign them
	 */
	private void sortDataHelper(BinaryTree<CellNode> curPos) {
		
		//find the first instance of a child of the current tree
		for(int i = 0; i < wormCells.size(); i++) {
			if(wormCells.get(i).getParentName() == curPos.value().getName()) {
				curPos.setLeft(new BinaryTree<CellNode>(wormCells.get(i)));
				wormCells.get(i).setParent(curPos.value());
				curPos.value().setLeftChild(wormCells.get(i));
				
				//finds the second instance of a child of the current tree
				for(int j = i + 1; j < wormCells.size(); j++) {
					if(wormCells.get(j).getParentName() == curPos.value().getName()) {
						curPos.setRight(new BinaryTree<CellNode>(wormCells.get(j)));
						wormCells.get(j).setParent(curPos.value());
						curPos.value().setRightChild(wormCells.get(j));
						
						//recursive call
						sortDataHelper(curPos.right());
						
						//once we've found the second child, there are no more so we can exit
						//the current sub-loop
						continue;
					}
				}
				
				//recursive call
				sortDataHelper(curPos.left());
				
				//exits loop entirely
				break;
			}
		}
	}
	
	/**
	 * @pre wormTree has been built
	 * @post generates the binaryCellNames of all CellNodes based on their position in the tree
	 */
	private void binaryCellNameGeneration() {
		wormTree.root().value().setBinaryCellName(1);
		binaryCellNameGenerationHelper(wormTree.root(), (long) 1);
	}
	
	/**
	 * Recursive method to assign binaryCellNames
	 * @param curTree current tree being operated in
	 */
	private void binaryCellNameGenerationHelper(BinaryTree<CellNode> curTree, Long parentName) {
		BinaryTree<CellNode> leftChild = curTree.left();
		BinaryTree<CellNode> rightChild = curTree.right();
		
		//if the current node actually has children, assign them names
		if(!leftChild.isEmpty()) {
			leftChild.value().setBinaryCellName(Long.parseLong(parentName.toString().concat("0")));
			binaryCellNameGenerationHelper(leftChild, leftChild.value().getBinaryCellName());
		}
		
		if(!rightChild.isEmpty()) {
			rightChild.value().setBinaryCellName(Long.parseLong(parentName.toString().concat("1")));
			binaryCellNameGenerationHelper(rightChild, rightChild.value().getBinaryCellName());
		}
		
	}
	
	/**
	 * 
	 * @param time, time at which you want to count how many cells are alive that are descended from a 
	 * @return the number of cells that are alive concurrently at a specific time
	 */
	public static int findLivingLineageCells(long name, int time) {
		
		Iterator<CellNode> cells = null;
		ArrayList<BinaryTree<CellNode>> treeList = new ArrayList<BinaryTree<CellNode>>();
		
		if(wormTree.root().value().getBinaryCellName() == name) {
			cells = wormTree.iterator();
		}else {
			findLivingLineageCellsHelper(name, wormTree.root(), treeList);
			cells = treeList.get(0).iterator();
		}
				
		int count = 0;
		
		while(cells.hasNext()) {
			CellNode curCell = cells.next();
			
			/**
			 * the requirements for a cell to be currently alive are:
			 * 1.) its birth time is before the specified time
			 * 2.) its death time is after the specified time (if it exists - i.e. isn't 0)
			 * 3.) its children's birth times are after the specified time (it hasn't divided yet)
			 */
			if(curCell.getBirthTime() < time && (curCell.getDeathTime() == 0.0 || curCell.getDeathTime() > time)) {
				if((curCell.getLeftChild() == null || curCell.getLeftChild().getBirthTime() > time) && (curCell.getRightChild() == null ||
						curCell.getRightChild().getBirthTime() > time)) {
					count++;
				}
			}
		}
		
		return count;
	}
	
	/**
	 * 
	 * @param name BinaryCellName to look for
	 * @param curTree current position
	 * @param list a way to return the sub-tree with the BinaryCellName as the root
	 * 
	 * Recursive method that looks for a binary sub-tree with name as the BinaryCellName
	 * of the root and returns that as the only element in an arrayList
	 */
	private static void findLivingLineageCellsHelper(long name, BinaryTree<CellNode> curTree, ArrayList<BinaryTree<CellNode>> list) {
		 
		BinaryTree<CellNode> left = curTree.left();
		BinaryTree<CellNode> right = curTree.right();
		
		if(curTree.value().getBinaryCellName() == name) {
			list.add(curTree);
		}else {
			if(!left.isEmpty() && !right.isEmpty()) {
				findLivingLineageCellsHelper(name, left, list);
				findLivingLineageCellsHelper(name, right, list);
			}
		}
	}
	
	
	
	/**
	 * main method to test functionality
	 * REMOVE WHEN USING AS A LIBRARY
	 */
	public static void main(String args[]) throws IOException {
		long startTime = System.currentTimeMillis();
		File wormFile = new File(args[0]);
		WormMathData worm = new WormMathData();
		try {
			worm.parseExcelData(wormFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(worm.sortData());
		worm.binaryCellNameGeneration();
		
		Workbook sortTest = WorkbookFactory.create(true);
		Sheet sortTestSheet = sortTest.createSheet();
		Iterator<CellNode> wormIterator = wormTree.levelorderIterator();
		Row curRow = sortTestSheet.createRow(0);
		curRow.createCell(0).setCellValue("Name");
		curRow.createCell(1).setCellValue("Parent");
		curRow.createCell(2).setCellValue("Lineage");
		curRow.createCell(3).setCellValue("Birth Time");
		curRow.createCell(4).setCellValue("Death Time");
		curRow.createCell(5).setCellValue("Type");
		curRow.createCell(6).setCellValue("Description");
		curRow.createCell(7).setCellValue("Binary Cell Name");
		int i = 0;
		while(wormIterator.hasNext()) {
			i++;
			CellNode curCell = wormIterator.next();
			sortTestSheet.createRow(i);
			curRow = sortTestSheet.getRow(i);
			ArrayList<Cell> cellList = new ArrayList<Cell>();
			for(int j = 0; j < 8; j++) {
				Cell cell = curRow.createCell(j);
				cellList.add(cell);
			}
			cellList.get(0).setCellValue(curCell.getName());
			cellList.get(1).setCellValue(curCell.getParentName());
			cellList.get(2).setCellValue(curCell.getLineage());
			cellList.get(3).setCellValue(curCell.getBirthTime());
			cellList.get(4).setCellValue(curCell.getDeathTime());
			cellList.get(5).setCellValue(curCell.getType());
			cellList.get(6).setCellValue(curCell.getDescription());
			cellList.get(7).setCellValue(curCell.getBinaryCellName());
		}
		
		FileOutputStream fos = new FileOutputStream("C:/Users/njpap/OneDrive/Documents/WHOI/test.xlsx");
		sortTest.write(fos);
		fos.close();
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
		System.out.println(findLivingLineageCells(1, 300));
		System.out.println(findLivingLineageCells(10001, 300));
	}
}