package com.munna.common.service.api;

public abstract class UtilityService implements IUtilityService {
	
	@Override
	public void run() {
		init();
		process();
		finish();
	}

	
	
}
