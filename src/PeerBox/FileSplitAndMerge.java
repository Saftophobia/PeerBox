package PeerBox;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

// Splits files, format independent and merges them back 
public class FileSplitAndMerge {
	
	public static void mergeFiles(String filename, LinkedList<String> paths) throws IOException{
		File out = new File(filename);
		FileOutputStream fos;
		byte[] fileBytes;
	    fos = new FileOutputStream(out,true);             
	    for (String path: paths) {
	        fileBytes = FileManager.readFile(path);
	        fos.write(fileBytes);
	        fos.flush();
	        fileBytes = null;
	    }
	    fos.close();
	    fos = null;
	}
	
	// Returns list of paths to split files
    public static LinkedList<String> splitFile(File f) throws IOException {
    	LinkedList<String> paths = new LinkedList<String>();
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
        String name = f.getName();
        int partCounter = 1;
        
        // sizeOfFiles = 512 KB, can be customized to our needs
        int sizeOfFiles = 512 * 1024;	 // need to be synced with FileManager.PIECE_SIZE
        byte[] buffer = new byte[sizeOfFiles];
        String path;
        while ((bis.read(buffer)) > 0) {
        	path = f.getParent()+File.separator+name+"."+String.format("%1d", partCounter++);
            FileManager.writeToAbsoluteFile(path, buffer);
            paths.add(path);
        }
        
        bis.close();
        return paths;
    }

    public static void main(String[] args) throws IOException {
        LinkedList<String> paths = splitFile(
        		new File("/Users/macbokpro/Music/iERA/Self Discovery.mp3"));
        mergeFiles("/Users/macbokpro/Music/iERA/Self Discovery2.mp3", paths);
    }

}
