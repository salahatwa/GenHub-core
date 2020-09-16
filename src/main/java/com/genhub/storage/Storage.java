package com.genhub.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author satwa
 *
 */
public interface Storage {

	/**
	 * Store pictures
	 * @param file
	 * @param basePath
	 * @return
	 * @throws IOException
	 */
	String store(MultipartFile file, String basePath) throws Exception;

	/**
	 * Store compressed pictures
	 * @param file
	 * @param basePath
	 * @return
	 * @throws IOException
	 */
	String storeScale(MultipartFile file, String basePath, int maxWidth) throws Exception;

	/**
	 * Store compressed pictures
	 * @param file
	 * @param basePath
	 * @return
	 * @throws IOException
	 */
	String storeScale(MultipartFile file, String basePath, int width, int height) throws Exception;

	/**
	 * Storage path
	 * @param storePath
	 */
	void deleteFile(String storePath);

	String writeToStore(byte[] bytes, String pathAndFileName) throws Exception;
}
