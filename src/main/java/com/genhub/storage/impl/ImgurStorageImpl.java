package com.genhub.storage.impl;

import java.io.File;
import java.io.IOException;

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
public class ImgurStorageImpl extends AbstractStorage {

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

		final String uri = "https://api.imgur.com/3/image";

		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add("image", getUserFileResource(bytes));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.add("Authorization", "Client-ID c900a9acad45b7f");
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<ImgurRes> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, ImgurRes.class);
		System.out.println("response status: " + response.getStatusCode());
		System.out.println("response body: " + response.getBody().getData().getLink());

		return response.getBody().getData().getLink();

	}

	private String getStoragePath() {
		return options.getLocation();
	}

	public static void main(String[] args) throws IOException {
		final String uri = "https://api.imgur.com/3/image";

		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add("image", getUserFileResource(null));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.add("Authorization", "Client-ID c900a9acad45b7f");
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<ImgurRes> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, ImgurRes.class);
		System.out.println("response status: " + response.getStatusCode());
		System.out.println("response body: " + response.getBody().getData().getLink());
	}

	public static Resource getUserFileResource(byte[] bytes) throws IOException {

		return new ByteArrayResource(bytes);
	}

	public static class ImgurRes {
		private Data data;

		private String success;

		private String status;

		public ImgurRes() {

		}

		public Data getData() {
			return data;
		}

		public void setData(Data data) {
			this.data = data;
		}

		public String getSuccess() {
			return success;
		}

		public void setSuccess(String success) {
			this.success = success;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public static class Data {

			private String link;

			public Data() {
			}

			public String getLink() {
				return link;
			}

			public void setLink(String link) {
				this.link = link;
			}

		}
	}
}
