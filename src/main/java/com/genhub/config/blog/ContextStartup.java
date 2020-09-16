package com.genhub.config.blog;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.ServletContextAware;

import com.genhub.domain.blog.Options;
import com.genhub.service.blog.ChannelService;
import com.genhub.service.blog.MailService;
import com.genhub.service.blog.OptionsService;
import com.genhub.utils.BlogConsts;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(2)
@Component
public class ContextStartup implements ApplicationRunner, ServletContextAware {
	@Autowired
	private OptionsService optionsService;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private MailService mailService;
	@Autowired
	private SiteOptions siteOptions;

	private ServletContext servletContext;

	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
		log.info("initialization ...");
		reloadOptions(true);
		resetChannels();
		log.info("OK, completed");
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void reloadOptions(boolean startup) {
		List<Options> options = optionsService.findAll();

		log.info("find options ({})...", options.size());

		if (startup && CollectionUtils.isEmpty(options)) {
			try {
				log.info("init options...");
				Resource resource = new ClassPathResource("/scripts/schema.sql");
				optionsService.initSettings(resource);
				options = optionsService.findAll();
			} catch (Exception e) {
				log.error("------------------------------------------------------------");
				log.error("-          ERROR: The database is not initialized          -");
				log.error("------------------------------------------------------------");
				log.error(e.getMessage(), e);
				System.exit(1);
			}
		}

		Map<String, String> map = siteOptions.getOptions();
		options.forEach(opt -> {
			if (StringUtils.isNoneBlank(opt.getKey(), opt.getValue())) {
				map.put(opt.getKey(), opt.getValue());
			}
		});
		servletContext.setAttribute("options", map);
		servletContext.setAttribute("site", siteOptions);
		mailService.config();

		System.setProperty("site.location", siteOptions.getLocation());
	}

	public void resetChannels() {
		servletContext.setAttribute("channels", channelService.findAll(BlogConsts.STATUS_NORMAL));
	}

}
