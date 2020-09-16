package com.genhub.service.blog.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.genhub.domain.Role;
import com.genhub.domain.UserRole;
import com.genhub.exceptions.ResourceNotFoundException;
import com.genhub.repository.blog.RoleRepository;
import com.genhub.repository.blog.UserRoleRepository;
import com.genhub.service.blog.RoleService;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserRoleRepository userRoleRepository;

	@Override
	public Page<Role> paging(Pageable pageable, String name) {
		Page<Role> page = roleRepository.findAll((root, query, builder) -> {
			Predicate predicate = builder.conjunction();

			if (StringUtils.isNoneBlank(name)) {
				predicate.getExpressions().add(builder.like(root.get("name"), "%" + name + "%"));
			}

			query.orderBy(builder.desc(root.get("id")));
			return predicate;
		}, pageable);
		return page;
	}

	@Override
	public List<Role> list() {
		List<Role> list = roleRepository.findAllByStatus(Role.STATUS_NORMAL);
		return list;
	}

	@Override
	public Map<Long, Role> findByIds(Set<Long> ids) {
		List<Role> list = roleRepository.findAllById(ids);
		Map<Long, Role> ret = new LinkedHashMap<>();
		list.forEach(po -> {
			Role vo = toVO(po);
			ret.put(vo.getId(), vo);
		});
		return ret;
	}

	@Override
	public Role get(long id) {
		return toVO(roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role", "Id", id)));
	}

	@Override
	public Role update(Role r) {
		Optional<Role> optional = roleRepository.findById(r.getId());
		Role po = optional.orElse(new Role());
		po.setName(r.getName());
		po.setDescription(r.getDescription());
		po.setStatus(r.getStatus());

		roleRepository.save(po);
		return po;
	}

	@Override
	public boolean delete(long id) {
		List<UserRole> urs = userRoleRepository.findAllByRoleId(id);
		Assert.state(urs == null || urs.size() == 0, "The character has already been used and cannot be deleted");
		roleRepository.deleteById(id);
		return true;
	}

	@Override
	public void activate(long id, boolean active) {
		Role po = roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role", "Id", id));
		po.setStatus(active ? Role.STATUS_NORMAL : Role.STATUS_CLOSED);
	}

	private Role toVO(Role po) {
		Role r = new Role();
		r.setId(po.getId());
		r.setName(po.getName());
		r.setDescription(po.getDescription());
		r.setStatus(po.getStatus());

		return r;
	}
}
