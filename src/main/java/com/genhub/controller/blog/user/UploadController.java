package com.genhub.controller.blog.user;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.genhub.config.blog.SiteOptions;
import com.genhub.storage.StorageFactory;
import com.genhub.utils.BlogConsts;
import com.genhub.utils.FileKit;

import lombok.Data;

@RestController
@RequestMapping("/user/post")
public class UploadController {

	@Autowired
	private SiteOptions siteOptions;

	@Autowired
	protected StorageFactory storageFactory;

	public static HashMap<String, String> errorInfo = new HashMap<>();

	static {
		errorInfo.put("SUCCESS", "SUCCESS");
		errorInfo.put("NOFILE", "File upload domain not included");
		errorInfo.put("TYPE", "Disallowed file format");
		errorInfo.put("SIZE", "File size exceeds the limit, the maximum support 2Mb");
		errorInfo.put("ENTYPE", "Request type ENTYPE error");
		errorInfo.put("REQUEST", "Upload request exception");
		errorInfo.put("IO", "IO exception");
		errorInfo.put("DIR", "Directory creation failed");
		errorInfo.put("UNKNOWN", "Unknown Error");
	}

	@PostMapping("/upload")
	public UploadResult upload(@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(required = false) String crop, @RequestParam(required = false) int size,
			@RequestParam(required = false) int width, @RequestParam(required = false) int height) throws IOException {
		UploadResult result = new UploadResult();

		size = (size > 0) ? size : siteOptions.getIntegerValue(BlogConsts.STORAGE_MAX_WIDTH);
		if (null == file || file.isEmpty()) {
			return result.error(errorInfo.get("NOFILE"));
		}

		String fileName = file.getOriginalFilename();

		if (!FileKit.checkFileType(fileName)) {
			return result.error(errorInfo.get("TYPE"));
		}

		String limitSize = siteOptions.getValue(BlogConsts.STORAGE_LIMIT_SIZE);
		if (StringUtils.isBlank(limitSize)) {
			limitSize = "2";
		}
		if (file.getSize() > (Long.parseLong(limitSize) * 1024 * 1024)) {
			return result.error(errorInfo.get("SIZE"));
		}

		try {
			String path;
			if (StringUtils.isNotBlank(crop)) {
				Integer[] imageSize = siteOptions.getIntegerArrayValue(crop, BlogConsts.SEPARATOR_X);
				width = (width > 0) ? width : imageSize[0];
				height = (height > 0) ? height : imageSize[1];

				path = storageFactory.get().storeScale(file, BlogConsts.thumbnailPath, width, height);
			} else {
				path = storageFactory.get().storeScale(file, BlogConsts.thumbnailPath, size);
			}
			result.ok(errorInfo.get("SUCCESS"));
			result.setName(fileName);
			result.setPath(path);
			result.setSize(file.getSize());

		} catch (Exception e) {
			result.error(errorInfo.get("UNKNOWN"));
			e.printStackTrace();
		}

		return result;
	}

	@Data
	public static class UploadResult {
		public static int OK = 200;
		public static int ERROR = 400;

		private int status;

		private String message;

		private String name;

		private long size;

		private String path;

		public UploadResult ok(String message) {
			this.status = OK;
			this.message = message;
			return this;
		}

		public UploadResult error(String message) {
			this.status = ERROR;
			this.message = message;
			return this;
		}

	}
}
