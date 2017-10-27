package handy.fileexplorer;

public class HData extends HFile {

	private String hash;
	
	public HData(String name, String hash){
		this.name = name;
		this.hash = hash;
	}

	public String getHash() {
		return hash;
	}
	
	public String printTree(int depth){
		return name + " ~ " + hash;
	}
}
