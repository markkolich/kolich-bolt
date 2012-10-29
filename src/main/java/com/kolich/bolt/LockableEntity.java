package com.kolich.bolt;

import java.util.concurrent.locks.ReadWriteLock;

public interface LockableEntity {
	
	public ReadWriteLock getLock();

}
