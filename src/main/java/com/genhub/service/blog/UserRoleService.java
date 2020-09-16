package com.genhub.service.blog;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.genhub.domain.Role;

public interface UserRoleService {
	List<Long> listRoleIds(long userId);

	List<Role> listRoles(long userId);

	Map<Long, List<Role>> findMapByUserIds(List<Long> userIds);

	void updateRole(long userId, Set<Long> roleIds);

	Role findRole(long id);
}
