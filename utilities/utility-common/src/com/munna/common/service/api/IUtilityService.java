package com.munna.common.service.api;

/*
 * @author Mohammed Fathauddin
 * @since 2018
 */
public interface IUtilityService extends Runnable {
	
	public void init();
	
	public void process();
	
	public void finish();

}
