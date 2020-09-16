package com.genhub.storage;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.genhub.config.blog.SiteOptions;
import com.genhub.storage.impl.ImgBBStorageImpl;
import com.genhub.storage.impl.ImgurStorageImpl;
import com.genhub.storage.impl.NativeStorageImpl;

@Component
public class StorageFactory implements InitializingBean {
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private SiteOptions siteOptions;

	private Map<String, Storage> fileRepoMap = new HashMap<>();

	public boolean registry(String key, Storage storage) {
		if (fileRepoMap.containsKey(key)) {
			return false;
		}
		fileRepoMap.put(key, storage);
		return true;
	}

	public Storage get() {
		String scheme = siteOptions.getValue("storage_scheme");
		if (StringUtils.isBlank(scheme)) {
			scheme = "native";
		}
		return fileRepoMap.get(scheme);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		fileRepoMap.put("native", applicationContext.getBean(NativeStorageImpl.class));
		fileRepoMap.put("imgbb", applicationContext.getBean(ImgBBStorageImpl.class));
		fileRepoMap.put("imgur", applicationContext.getBean(ImgurStorageImpl.class));
	}
}
