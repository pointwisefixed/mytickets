package com.mytickets.service;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;

public class PersistServiceInitializer {

	@Inject
	private PersistServiceInitializer(PersistService service){
		service.start();
	}
}
