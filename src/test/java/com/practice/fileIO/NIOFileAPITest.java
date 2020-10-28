package com.practice.fileIO;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import org.junit.Test;

public class NIOFileAPITest {

	private static String HOME = System.getProperty("user.home");
	private static String PLAY_WITH_NIO = "temp_play_ground";
	
	@Test
	public void givenPathWhenCheckedThenConfirmUC2() throws IOException {
		
		//check if file exists
		Path path = Paths.get(HOME);
		assertTrue(Files.exists(path));
		
		//Delete file and check if file exist or not
		Path playPath = Paths.get(HOME+"/"+PLAY_WITH_NIO);
		if(Files.exists(playPath))
			FileUtils.deleteFiles(playPath.toFile());
		assertTrue(Files.notExists(playPath));
		
		//create directory
		Files.createDirectory(playPath);
		assertTrue(Files.exists(playPath));
		
		//create file
		IntStream.range(1,10).forEach(n->{
			Path tempFile = Paths.get(playPath+"/temp"+n);
			assertTrue(Files.notExists(tempFile));
			try {
				Files.createFile(tempFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			assertTrue(Files.exists(tempFile));
		});
		
		//List Files, Directories as well as files with extension
		Files.list(playPath).filter(Files::isRegularFile).forEach(System.out::println);
		Files.newDirectoryStream(playPath).forEach(System.out::println);
		Files.newDirectoryStream(playPath, n->n.toFile().isFile()&&n.toString().startsWith("temp")).forEach(System.out::println);
	}

}
