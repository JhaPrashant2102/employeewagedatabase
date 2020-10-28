package com.practice.fileIO;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

// goal is to watch a particular directory and record what's happening in a directory
public class WatchServiceExample {
	private final WatchService watcher;
	private final Map<WatchKey, Path> directoryWatchers;

	public WatchServiceExample(Path directoryPath) throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.directoryWatchers = new HashMap<WatchKey,Path>();
		//scanAndRegisterDirectories(directoryPath);
	}

	/*
	  private void scanAndRegisterDirectories(final Path Start) throws IOException {
		Files.walkFileTree(Start, (simpleFileVisitor)preVisitDirectory(directoryWatchers,attrs)->{
			registerDirWatchers(directoryWatchers);
			return FileVisitResult.CONTINUE;
		});
	}
	*/

}
