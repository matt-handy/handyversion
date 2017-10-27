package handy.fileexplorer;

import java.util.ArrayList;
import java.util.List;

public abstract class HFile {
	
	protected String name;

	public abstract String printTree(int depth);
	
	private static int currentLine = 0;
	
	public static HFile buildFromString(String heirarchy){
		String[] lines = heirarchy.split(System.lineSeparator());
		if(lines.length == 1){
			String[] elements = lines[0].split(" ~ ");
			return new HData(elements[0], elements[1]);
		}else{
			String name = lines[0];
			List<HFile> files = new ArrayList<HFile>();
			int currentDepth = 1;
			currentLine = 1;
			while(currentLine < lines.length){
				HFile file = getFiles(lines, currentDepth); 
				files.add(file);
			}
			return new HFolder(name, files);
		}
		
	}
	
	public static HFile getFiles(String[] lines, int currentDepth){
		//System.out.println(currentDepth + " " + lines[currentLine]);
		//if(lines[currentLine].equals("-- dashboard") && currentDepth == 1){
		//	System.exit(1);
		//}
		if(currentLine == lines.length){
			return null;
		}
		String[] elements = lines[currentLine].split(" ~ ");
		if(elements.length == 3){
			if(elements[0].length() == currentDepth){
				currentLine++;
				//System.out.println("New element");
				return new HData(elements[1], elements[2]);
			}else{
				//System.out.println("New element - wrong depth");
				return null;
			}
		}else{ //Assume length = 2
			if(elements[0].length() == currentDepth){
				String name = elements[1];
				List<HFile> files = new ArrayList<HFile>();
				currentLine++;
				//System.out.println("New folder");
				HFile next = getFiles(lines, currentDepth + 1);
				while(next != null){
					files.add(next);
					//System.out.println("Loopering");
					next = getFiles(lines, currentDepth + 1);
				}
				return new HFolder(name, files);
			}else{
				//System.out.println("Folder - wrong depth");
				return null;
			}
		}
	}
	
	public String getName(){
		return name;
	}
	
	public String compareTrees(HFile baseline, String root){
		StringBuilder diffTable = new StringBuilder();
		
		if(this instanceof HData && baseline instanceof HData){
			HData myData = (HData) this;
			HData theirData = (HData) baseline;
			if(myData.name.equals(theirData.name)){
				if(!myData.getHash().equals(theirData.getHash())){
					diffTable.append(root + " is different" + System.lineSeparator());
				}
			}else{
				diffTable.append(root + " compared to " + theirData.name + System.lineSeparator());
			}
		}else if(this instanceof HFolder && baseline instanceof HFolder){
			HFolder myData = (HFolder) this;
			HFolder theirData = (HFolder) baseline;
			if(myData.name.equals(theirData.name)){
				//diffTable.append(depthOffset + myData.name + " comparing..." + System.lineSeparator());
				if(theirData.getFiles().size() != 0){
					int nonBaselineIdx = 0;
					for(int baselineIdx = 0; baselineIdx < theirData.getFiles().size(); baselineIdx++){
						String baselineName = theirData.getFiles().get(baselineIdx).name;
						String myName = myData.getFiles().get(nonBaselineIdx).name; 
						if(nonBaselineIdx >= myData.getFiles().size()){
							diffTable.append(root + "/" + baselineName +
									" not in current tree.");
						}else{
							if(baselineName.equals(myName)){
								diffTable.append(myData.getFiles().get(nonBaselineIdx).compareTrees(
										theirData.getFiles().get(baselineIdx), root + "/" + baselineName));
								nonBaselineIdx++;
							}else{
								if(baselineName.compareTo(myName) < 0){
									//case new baseline ahead
									diffTable.append(root + "/" + baselineName +
											" missing from current tree." + System.lineSeparator());
								}else if(baselineName.compareTo(myName) > 0){
									//case baseline ahead
									diffTable.append(root + "/" + myName +
											" new in current tree." + System.lineSeparator());
									nonBaselineIdx++;
									baselineIdx--;//This is a control flow hack
								}else{
									System.out.println("Reporting identical files in difference comparator");
								}
							}
						}
					}
					while(nonBaselineIdx < myData.getFiles().size()){
						diffTable.append(root + "/" + myData.getFiles().get(nonBaselineIdx).name +
								" not in baseline tree.");
						nonBaselineIdx++;
					}
				}
			}else{
				diffTable.append(root + "/" + myData.name + " compared to " + theirData.name + System.lineSeparator());
			}
		}else{
			diffTable.append(root + "/" + "Mismatch of file/folder type" + System.lineSeparator());
		}
		
		return diffTable.toString();
	}
	
}
