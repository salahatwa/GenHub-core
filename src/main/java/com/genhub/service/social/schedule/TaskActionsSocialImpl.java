package com.genhub.service.social.schedule;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genhub.domain.social.Provider;
import com.genhub.domain.social.Task;
import com.genhub.service.social.SocialPuplisher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TaskActionsSocialImpl implements TaskAction {

	@Autowired
	private SocialPuplisher publisher;

	public TaskActionsSocialImpl(SocialPuplisher publisher) {
		this.publisher = publisher;
	}

	@Transactional
	@Override
	public long execute(List<Provider> providers, Task myTask) {

		log.info("Begin Excuting Task:{}" + myTask.getId());

		try {

			log.info("Number of providers attached to Task:{}" + providers.size());

			for (Provider provider : providers) {

				switch (provider.getProviderType()) {
				case LINKEDIN:
					publisher.publishToLinkedIn(myTask, provider);
					break;

				case TWITTER:
					publisher.publishToTwitter(myTask, provider);
					break;

				default:
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		}

		log.info("Finished Excuted Task Successfuly");
		return myTask.getId();
	}

}
