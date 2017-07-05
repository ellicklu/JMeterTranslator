package ecd.perf.utilities.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class SourceFileLoadUtil {
	public final static char[] loadLRFileSource(String filename) {
		File file = new File(filename);
		String className = file.getName().substring(0, file.getName().indexOf('.'));
		char[] head = ("public class "+className+" {\n").toCharArray();
		char[] rawContent= null;
		try {
			rawContent= internalLoadFileSouce(filename);
		} catch (IOException e) {
			System.out.println("ERROR: failed to load content -" + filename);
			e.printStackTrace();
			return null;
		}
		char[] tail = "\n}\n".toCharArray();
		char[] finalContent = new char[head.length+rawContent.length+tail.length];
		System.arraycopy(head, 0, finalContent, 0, head.length);
		System.arraycopy(rawContent, 0, finalContent, head.length, rawContent.length);
		System.arraycopy(tail, 0, finalContent, head.length+rawContent.length, tail.length);
		return finalContent;
	}
	
	public final static char[] loadFileSouce(String filename) {
		
		try {
			return internalLoadFileSouce(filename);
		} catch (IOException e) {
			System.out.println("ERROR: failed to load content -" + filename);
			e.printStackTrace();
			return null;
		}
	}
	
	private final static char[] internalLoadFileSouce(String filename) throws IOException {
		File inputFile = new File(filename);
		FileReader fr = new FileReader(inputFile);
		int bufferSize = 1024;
		char[] bufferredChars = new char[bufferSize];
		ArrayList<char[]> allBuffers = new ArrayList<char[]>();
		int readCount = 0;
		int totalCount = 0;
		while((readCount = fr.read(bufferredChars,0,bufferSize)) > 0) {
			allBuffers.add(bufferredChars);
			bufferredChars = new char[bufferSize];
			totalCount += readCount;
		}
		
		char[] allChars = new char[(int) totalCount];
		for(int i = 0; i < allBuffers.size(); i++) {
			if(i < allBuffers.size() - 1) {
				System.arraycopy(allBuffers.get(i), 0, allChars, i*bufferSize, bufferSize);
			} else {
				int finalSize = (int) (totalCount%bufferSize);
				System.arraycopy(allBuffers.get(i), 0, allChars, i*bufferSize, finalSize);
			}
			
		}
		return allChars;
	}
}
