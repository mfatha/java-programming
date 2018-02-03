package com.munna.common.executor.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorServiceFactory {
	private static Map<String, ExecutorServiceFactory> executorServiceFactory = new HashMap<String, ExecutorServiceFactory>();

	private ExecutorService executorService = null;

	private ExecutorServiceFactory(String executorType, int noOfThreads) {
		executorService = Executors.newFixedThreadPool(noOfThreads);
	}

	public static void initialize(String executorType, int noOfThreads) {
		if (!executorServiceFactory.containsKey(executorType)) {
			synchronized (ExecutorServiceFactory.class) {
				if (!executorServiceFactory.containsKey(executorType)) {
					executorServiceFactory.put(executorType, new ExecutorServiceFactory(executorType, noOfThreads));
				}
			}
		}
	}

	public static ExecutorServiceFactory getInstance(String executorType) {
		return executorServiceFactory.get(executorType);
	}

	public Future<?> addService(Runnable runnable) {
		return executorService.submit(runnable);
	}

	public void shutdown() {
		executorService.shutdown();
	}
}
