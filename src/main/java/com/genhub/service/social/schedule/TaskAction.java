package com.genhub.service.social.schedule;

import java.util.List;

import com.genhub.domain.social.Provider;
import com.genhub.domain.social.Task;

public interface TaskAction {
	public long execute(List<Provider> providers,Task task);
}
