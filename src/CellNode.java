/**
 * 
 * @author Nate Pappalardo
 * @version 1.1
 * 
 * Class to quantify cells as BinaryCellNames
 */
public class CellNode {
	
	private String name = null;
	private double birthTime = 0;
	private double deathTime = 0;
	private String parentName = null;
	private String lineage = null;
	private String type = null;
	private String description = null;
	private long binaryCellName = 0;
	private CellNode parent = null;
	private CellNode leftChild = null;
	private CellNode rightChild = null;
	
	
	public CellNode(String name, String parentName, String lineage, double birthTime, double deathTime, String type, String description) {
		this.name = name;
		this.parentName = parentName;
		this.type = type;
		this.birthTime = birthTime;
		this.deathTime = deathTime;
		this.lineage = lineage;
		this.description = description;
		new CellNode();
	}
	
	public CellNode() {}
	
	public String getName() {
		return name;
	}
	public double getBirthTime() {
		return birthTime;
	}
	public double getDeathTime() {
		return deathTime;
	}
	public String getParentName() {
		return parentName;
	}
	public String getLineage() {
		return lineage;
	}
	public String getType() {
		return type;
	}
	public String getDescription() {
		return description;
	}
	public CellNode getParent() {
		return parent;
	}
	
	protected void setParent(CellNode parent) {
		this.parent = parent;
	}
	
	public long getBinaryCellName() {
		return binaryCellName;
	}
	
	protected void setBinaryCellName(long l) {
		this.binaryCellName = l;
	}
	
	public CellNode getLeftChild() {
		return leftChild;
	}

	protected void setLeftChild(CellNode leftChild) {
		this.leftChild = leftChild;
	}

	public CellNode getRightChild() {
		return rightChild;
	}

	protected void setRightChild(CellNode rightChild) {
		this.rightChild = rightChild;
	}

}
