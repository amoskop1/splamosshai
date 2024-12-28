package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * retrieving the result once it is available.
 *
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {

	private T result;
	private boolean isResolved;
	private final Object lock;

	/**
	 * This should be the only public constructor in this class.
	 */
	public Future() {
		this.result = null;
		this.isResolved = false;
		this.lock = new Object();
	}

	/**
	 * Retrieves the result the Future object holds if it has been resolved.
	 * This is a blocking method! It waits for the computation in case it has
	 * not been completed.
	 *
	 * @return the result of type T if it is available, waits until it is available.
	 */
	public T get() {
		synchronized (lock) {
			while (!isResolved) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return null;
				}
			}
			return result;
		}
	}

	/**
	 * Resolves the result of this Future object.
	 *
	 * @param result the result to set
	 */
	public void resolve(T result) {
		synchronized (lock) {
			if (!isResolved) {
				this.result = result;
				this.isResolved = true;
				lock.notifyAll();
			}
		}
	}

	/**
	 * @return true if this object has been resolved, false otherwise
	 */
	public boolean isDone() {
		synchronized (lock) {
			return isResolved;
		}
	}

	/**
	 * Retrieves the result the Future object holds if it has been resolved.
	 * This method is non-blocking, it has a limited amount of time determined
	 * by {@code timeout}.
	 *
	 * @param timeout the maximal amount of time units to wait for the result.
	 * @param unit    the {@link TimeUnit} time units to wait.
	 * @return the result of type T if it is available, null if the timeout elapsed.
	 */
	public T get(long timeout, TimeUnit unit) {
		synchronized (lock) {
			if (!isResolved) {
				try {
					lock.wait(unit.toMillis(timeout));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return null;
				}
			}
			if (isResolved) {
				return result;
			} else {
				return null;
			}
		}
	}
}
