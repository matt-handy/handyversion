package handy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

import handy.fileexplorer.FileTreeBuilder;
import handy.fileexplorer.HFile;

public class FileRunner {
	public static void main(String args[]){
		if(args.length != 2){
			System.out.println("Usage:");
			System.out.println("\t -i -> Initial baseline");
			System.out.println("\t -c -> Compare to previous");
			System.out.println("Second argument: root directory");
		}else{
			if(args[0].equals("-i")){
				Date date = new Date();
			    try {
			    	BufferedWriter writer = new BufferedWriter(new FileWriter("latest_tree.log"));
			    	HFile current = FileTreeBuilder.getHeirarchy(args[1]);
					String tree = current.printTree(0);
					
					writer.write(date.toGMTString());
					writer.newLine();
					writer.write(tree);
					writer.close();
			    } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(args[0].equals("-c")){
				try{
					File oldPrevious = new File("previous_tree.log");
					if(oldPrevious.exists()){
						BufferedReader bReader = new BufferedReader(new FileReader(oldPrevious));
						String oldTime = bReader.readLine();
						bReader.close();
						
						oldTime = oldTime.replaceAll(" ", "");
						oldTime = oldTime.replaceAll(":", "");
						System.out.println("Renaming to " + oldTime);
						File newOld = new File(oldTime);
						Files.move(oldPrevious.toPath(), newOld.toPath());
					}
					File previous = new File("latest_tree.log");
					File newPrevious = new File("previous_tree.log");
					previous.renameTo(newPrevious);
					
					BufferedReader bReader = new BufferedReader(new FileReader(newPrevious));
					StringBuilder previousTreeStr = new StringBuilder();
					int idx = 0;
					String next = bReader.readLine();
					while(next != null){
						if(idx != 0){
							previousTreeStr.append(next + System.lineSeparator());
						}
						idx++;
						next = bReader.readLine();
					}
					bReader.close();
					
					HFile previousTree = HFile.buildFromString(previousTreeStr.toString());
					
					BufferedWriter writer = new BufferedWriter(new FileWriter("latest_tree.log"));
			    	HFile currentTree = FileTreeBuilder.getHeirarchy(args[1]);
			    	writer.write(new Date().toGMTString());
					writer.newLine();
					writer.write(currentTree.printTree(0));
					writer.close();
					
					System.out.println(currentTree.compareTrees(previousTree, args[1]));
				}catch (IOException e){
					e.printStackTrace();
				}
			}else if(args[0].equals("-t")){
				try {
					HFile baseline = FileTreeBuilder.getHeirarchy(".");
					BufferedWriter writer = new BufferedWriter(new FileWriter("tree_disruptor.txt"));
					writer.write("HAX");
					writer.close();
					HFile newBaseline = FileTreeBuilder.getHeirarchy(".");
					String compare = newBaseline.compareTrees(baseline, ".");
					System.out.println(compare);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		
	}
}
