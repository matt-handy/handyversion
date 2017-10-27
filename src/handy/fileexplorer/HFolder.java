package handy.fileexplorer;

import java.util.List;

public class HFolder extends HFile {

	private List<HFile> files;
	
	public HFolder(String name, List<HFile> files){
		this.name = name;
		this.files = files;
	}

	public List<HFile> getFiles() {
		return files;
	}
	
	public String printTree(int depth) {
		StringBuilder sb = new StringBuilder(name);
		for(HFile file : files){
			sb.append(System.lineSeparator());
			for(int idx = 0; idx < depth; idx++){
				sb.append("-");
			}
			sb.append("- ~ ");
			sb.append(file.printTree(depth + 1));
		}
		return sb.toString();
	}
}
