package com.claymus;

import java.util.logging.Level;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Builder;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.google.apphosting.api.ApiProxy.OverQuotaException;

/*
 * Quota Details: http://code.google.com/appengine/docs/quotas.html#Task_Queue
 * .----------------------.-------------.------------------------.
 * | Resource			  | Daily(Free) | Daily(Billing Enabled) |
 * |----------------------|-------------|------------------------|
 * | Task Queue API Calls |		100,000 |			  20,000,000 |
 * '----------------------'-------------'------------------------'
 * Queue Execution Rate: 100 task invocations per second per queue
 * Maximum Number of Tasks that can be Added in a Batch: 100 tasks
 */

/*
 * Thread-Safe: No Global Data
 */
public class Task {

	private static final Queue queue = QueueFactory.getDefaultQueue();


	// QUEUING TASK TO "DEFAULT QUEUE"

	public static boolean add(TaskOptions url, long countdownMillis) {
		try {
			if(countdownMillis <= 0)
				Task.queue.add(url);
			else
				Task.queue.add(url.countdownMillis(countdownMillis));
		} catch(TransientFailureException ex) {
			Logger.get().log(Level.WARNING, null, ex);
			return false;
		} catch(OverQuotaException ex) {
			Logger.get().log(Level.SEVERE, null, ex);
			return false;
		}
		return true;
	}

	public static boolean add(TaskOptions url) {
		return Task.add(url, 0);
	}

	public static boolean add(String urlStr) {
		return Task.add(Builder.withUrl(urlStr));
	}

	public static boolean add(String urlStr, Method method) {
		return Task.add(Builder.withUrl(urlStr).method(method));
	}

	public static boolean add(String urlStr, long countdownMillis, Method method) {
		return Task.add(Builder.withUrl(urlStr).method(method), countdownMillis);
	}

}
