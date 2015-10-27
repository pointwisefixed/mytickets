package com.mytickets.dao.impl;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.mytickets.dao.VenueLevelDao;

public class VenueLevelDaoImpl implements VenueLevelDao {

	@Inject
	private Provider<EntityManager> entityManagerProvider;

	@Override
	@Transactional
	public Integer getSeatsInLevel(int levelId) {
		EntityManager em = entityManagerProvider.get();		
		return null;
	}

}
