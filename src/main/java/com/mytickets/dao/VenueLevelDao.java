package com.mytickets.dao;

import com.google.inject.ImplementedBy;
import com.mytickets.dao.impl.VenueLevelDaoImpl;

@ImplementedBy(VenueLevelDaoImpl.class)
public interface VenueLevelDao {

	Integer getSeatsInLevel(int levelId);
}
