package bgu.spl.mics;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl instance = null;
	private final ConcurrentHashMap <Class<?extends Message>, List<MicroService>> messages ;
	private final ConcurrentHashMap <MicroService, BlockingQueue<Message>> queues ;
	private final ConcurrentHashMap<Event<?>, Future<?>> futureEvents;
	// Private constructor to prevent instantiation
	private MessageBusImpl() {
		messages = new ConcurrentHashMap<>();
		queues = new ConcurrentHashMap<>();
		futureEvents = new ConcurrentHashMap<>();
	}

	// Public method to provide the single instance
	public static MessageBusImpl getInstance() {
		if (instance == null) {
			synchronized (MessageBusImpl.class) { // Thread-safe initialization
				 if (instance == null) { // Double-checked locking
					instance = new MessageBusImpl();
				}
			}
		}
		return instance;
	}
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (messages) {
			if (!messages.containsKey(type)) {
				messages.put(type, new CopyOnWriteArrayList<>());
			}
			messages.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (messages) {
			if (!messages.containsKey(type)) {
				messages.put(type, new CopyOnWriteArrayList<>());
			}
			messages.get(type).add(m);
		}

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		if (futureEvents.get(e) != null) {
			futureEvents.get(e).resolve(result);
		}
	}


	@Override
	public void sendBroadcast(Broadcast b) {
		List<MicroService> subscribers;
		synchronized (messages) {
			subscribers = messages.get(b.getClass());
		}
		if (subscribers != null) {
			for (int i = 0; i < subscribers.size(); i++) {
				MicroService m = subscribers.get(i);
				queues.putIfAbsent(m, new LinkedBlockingQueue<>());
				try {
					queues.get(m).put(b);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		List<MicroService> subscribers;
		synchronized (messages) {
			subscribers = messages.get(e.getClass());
		}
		if (subscribers == null || subscribers.isEmpty()) {
			return null;
		}

		Future<T> future = new Future<>();
		futureEvents.put(e, future); // Associate the event with the Future

		synchronized (messages) {
			List<MicroService> eventSubscribers = messages.get(e.getClass());
			if (eventSubscribers != null && !eventSubscribers.isEmpty()) {
				MicroService target = eventSubscribers.remove(0);
				eventSubscribers.add(target); // Rotate for round-robin
				queues.putIfAbsent(target, new LinkedBlockingQueue<>());
				try {
					queues.get(target).add(e); // Use add instead of put
				} catch (IllegalStateException ex) {
					System.err.println("Queue full: " + ex.getMessage());
				}
			}
		}

		return future;
	}

	@Override
	public void register(MicroService m) {
		synchronized (queues) {
			if (!queues.containsKey(m)) {
				queues.put(m, new LinkedBlockingQueue<>());
			}
		}
	}


	@Override
	public void unregister(MicroService m) {
		queues.remove(m);
		synchronized (messages) {
			List<Class<? extends Message>> keys = new ArrayList<>(messages.keySet());
			for (int i = 0; i < keys.size(); i++) {
				List<MicroService> subscribers = messages.get(keys.get(i));
				if (subscribers != null) {
					subscribers.remove(m);
				}
			}
		}
	}


	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		BlockingQueue<Message> queue = queues.get(m);
		if (queue == null) {
			throw new IllegalStateException("MicroService is not registered");
		}

		while (true) {
			synchronized (queue) {
				if (!queue.isEmpty()) {
					return queue.poll(); // Retrieve and remove the head of the queue
				}
			}
			Thread.sleep(10); // Small wait before re-checking
		}
	}

	

}
