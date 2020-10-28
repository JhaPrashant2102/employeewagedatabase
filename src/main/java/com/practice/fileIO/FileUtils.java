package com.practice.fileIO;

import java.io.File;

public class FileUtils {
	public static boolean deleteFiles(File contentsToDelete) {
		File[] allContents = contentsToDelete.listFiles();
		if(allContents!=null) {
			for(File file : allContents) {
				//file.delete(); will not be  used beca use we want recursive calls
				deleteFiles(file);
			}
		}
		return contentsToDelete.delete();
	}
}
