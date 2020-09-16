package com.genhub.service.blog;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.genhub.domain.Role;

public interface RoleService {

	Page<Role> paging(Pageable pageable, String name);

	List<Role> list();

	Map<Long, Role> findByIds(Set<Long> ids);

	Role get(long id);

	Role update(Role r);

	boolean delete(long id);

	void activate(long id, boolean active);

}
