package com.genhub.storage.impl;

import java.io.File;

import org.springframework.stereotype.Component;

import com.genhub.utils.FileKit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NativeStorageImpl extends AbstractStorage {

	@Override
	public void deleteFile(String storePath) {
		File file = new File(getStoragePath() + storePath);

		if (file.exists() && !file.isDirectory()) {
			file.delete();
			log.info("fileRepo delete " + storePath);
		}
	}

	@Override
	public String writeToStore(byte[] bytes, String pathAndFileName) throws Exception {
		String dest = getStoragePath() + pathAndFileName;
		FileKit.writeByteArrayToFile(bytes, dest);
		return pathAndFileName;
	}

	private String getStoragePath() {
		return options.getLocation();
	}

}
