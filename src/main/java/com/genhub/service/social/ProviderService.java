package com.genhub.service.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.genhub.domain.social.Provider;
import com.genhub.repository.social.ProviderRepository;

@Service
public class ProviderService {

	@Autowired
	private ProviderRepository providerRepo;

	public Provider save(Provider provider) {
		return providerRepo.save(provider);
	}

	public Page<Provider> getAllAccountsForUser(long userId, Integer pageNo, Integer pageSize, String sortBy) {
		Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

		Page<Provider> pagedResult = providerRepo.findByCreatedBy(userId, paging);

		return pagedResult;
	}

	public void removeAccountById(String id) {
		this.providerRepo.deleteById(id);
	}

}
