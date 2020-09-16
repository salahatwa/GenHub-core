package com.genhub.storage.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.genhub.utils.FileKit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ImgBBStorageImpl extends AbstractStorage {

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

		String url = "https://api.imgbb.com/1/upload?key=a70a592df33f6445fadef89c9b8f366b";
		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add("image", Base64.getEncoder().encode(bytes));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<ImgBBRes> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ImgBBRes.class);

		return response.getBody().getData().getDisplay_url();
	}

	private String getStoragePath() {
		return options.getLocation();
	}

	public static void main(String[] args) throws IOException {
		String url = "https://api.imgbb.com/1/upload?key=a70a592df33f6445fadef89c9b8f366b";
		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add("image", Base64.getEncoder().encode(Files.readAllBytes(Paths.get(
				"C:\\salahatwa\\java\\workspace-backend\\hadrx\\src\\main\\resources\\static\\dist\\images\\logo\\logo.png"))));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<ImgBBRes> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ImgBBRes.class);

		System.out.println(response.getBody().getData().getDisplay_url());

	}

	public static Resource getUserFileResource(byte[] bytes) throws IOException {
		return new ByteArrayResource(bytes);
	}

	@lombok.Data
	public static class ImgBBRes {
		private Detail data;

		private boolean success;

		private String status;

		@lombok.Data
		public class Detail {
			private String display_url;

			private String size;

			private String delete_url;

			private String expiration;

			private String id;

			private String time;

			private String title;

			private String url_viewer;

			private String url;

		}
	}

}
