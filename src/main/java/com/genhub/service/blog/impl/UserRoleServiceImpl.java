package com.genhub.service.blog.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genhub.domain.Role;
import com.genhub.domain.UserRole;
import com.genhub.repository.blog.UserRoleRepository;
import com.genhub.service.blog.RoleService;
import com.genhub.service.blog.UserRoleService;

@Service
@Transactional(readOnly = true)
public class UserRoleServiceImpl implements UserRoleService {
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private RoleService roleService;

	@Override
	public List<Long> listRoleIds(long userId) {
		List<UserRole> list = userRoleRepository.findAllByUserId(userId);
		List<Long> roleIds = new ArrayList<>();
		if (null != list) {
			list.forEach(po -> roleIds.add(po.getRoleId()));
		}
		return roleIds;
	}

	@Override
	public List<Role> listRoles(long userId) {
		List<Long> roleIds = listRoleIds(userId);
		return new ArrayList<>(roleService.findByIds(new HashSet<>(roleIds)).values());
	}
	
	@Override
	public Role findRole(long id) {
		return  roleService.get(id);
	}

	@Override
	public Map<Long, List<Role>> findMapByUserIds(List<Long> userIds) {
		List<UserRole> list = userRoleRepository.findAllByUserIdIn(userIds);
		Map<Long, Set<Long>> map = new HashMap<>();

		list.forEach(po -> {
			Set<Long> roleIds = map.computeIfAbsent(po.getUserId(), k -> new HashSet<>());
			roleIds.add(po.getRoleId());
		});

		Map<Long, List<Role>> ret = new HashMap<>();
		map.forEach((k, v) -> {
			ret.put(k, new ArrayList<>(roleService.findByIds(v).values()));
		});
		return ret;
	}

	@Override
	@Transactional
	public void updateRole(long userId, Set<Long> roleIds) {
		if (null == roleIds || roleIds.isEmpty()) {
			userRoleRepository.deleteByUserId(userId);
		} else {
			List<UserRole> list = userRoleRepository.findAllByUserId(userId);
			List<Long> exitIds = new ArrayList<>();

			if (null != list) {
				list.forEach(po -> {
					if (!roleIds.contains(po.getRoleId())) {
						userRoleRepository.delete(po);
					} else {
						exitIds.add(po.getRoleId());
					}
				});
			}

			// Save new roles that are not in existing roles ID
			roleIds.stream().filter(id -> !exitIds.contains(id)).forEach(roleId -> {
				UserRole po = new UserRole();
				po.setUserId(userId);
				po.setRoleId(roleId);

				userRoleRepository.save(po);
			});
		}

	}
}
