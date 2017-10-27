package handy.fileexplorer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class FileTreeBuilder {

	public static HFile getHeirarchy(String directory) throws IOException {
		File file = new File(directory);
		if(file.exists()){
			return getFiles(file);
		}else{
			throw new IOException("There's not file here!");
		}
	}

	public static HFile getFiles(File folder) throws IOException {

			if (folder.isDirectory()) {
				List<HFile> files = new ArrayList<HFile>();
				for (File fileEntry : folder.listFiles()) {
					files.add(getFiles(fileEntry));
				}
				return new HFolder(folder.getName(), files);
			} else {
				StringBuffer sb = new StringBuffer("");
				
				try {
					MessageDigest md = MessageDigest.getInstance("SHA-256");
					FileInputStream fis = new FileInputStream(folder);
					byte[] dataBytes = new byte[1024];

					int nread = 0;

					while ((nread = fis.read(dataBytes)) != -1) {
						md.update(dataBytes, 0, nread);
					}

					byte[] mdbytes = md.digest();

					// convert the byte to hex format
					for (int i = 0; i < mdbytes.length; i++) {
						sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
					}

					fis.close();
				} catch (NoSuchAlgorithmException e) {
					//I'm super, duper sure that SHA-1 exists
				} catch (FileNotFoundException e) {
					//Already validated that the file is real...
				}

				return new HData(folder.getName(), sb.toString());
			}
		
	}
}
