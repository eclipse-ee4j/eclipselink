/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility.events;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

/**
 * AWT-aware implementation of ChangeNotifier interface:
 * If we are executing on the AWT event-dispatch thread,
 * simply forward the change notification directly to the listener.
 * If we are executing on some other thread, queue up the
 * notification on the AWT event queue so it can be executed
 * on the event-dispatch thread (after the pending events have
 * been dispatched).
 */
public final class AWTChangeNotifier
	implements ChangeNotifier, Serializable
{
	// singleton
	private static ChangeNotifier INSTANCE;

	private static final long serialVersionUID = 1L;


	/**
	 * Return the singleton.
	 */
	public synchronized static ChangeNotifier instance() {
		if (INSTANCE == null) {
			INSTANCE = new AWTChangeNotifier();
		}
		return INSTANCE;
	}

	/**
	 * Ensure non-instantiability.
	 */
	private AWTChangeNotifier() {
		super();
	}

	/**
	 * @see ChangeNotifier#stateChanged(StateChangeListener, StateChangeEvent)
	 */
	public void stateChanged(final StateChangeListener listener, final StateChangeEvent event) {
		if (EventQueue.isDispatchThread()) {
			listener.stateChanged(event);
		} else {
			this.invoke(
				new Runnable() {
					public void run() {
						listener.stateChanged(event);
					}
					public String toString() {
						return "stateChanged";
					}
				}
			);
		}
	}

	/**
	 * @see ChangeSupport.Notifier#propertyChange(java.beans.PropertyChangeListener, java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(final PropertyChangeListener listener, final PropertyChangeEvent event) {
		if (EventQueue.isDispatchThread()) {
			listener.propertyChange(event);
		} else {
			this.invoke(
				new Runnable() {
					public void run() {
						listener.propertyChange(event);
					}
					public String toString() {
						return "propertyChange";
					}
				}
			);
		}
	}

	/**
	 * @see ChangeSupport.Notifier#itemsAdded(CollectionChangeListener, CollectionChangeEvent)
	 */
	public void itemsAdded(final CollectionChangeListener listener, final CollectionChangeEvent event) {
		if (EventQueue.isDispatchThread()) {
			listener.itemsAdded(event);
		} else {
			this.invoke(
				new Runnable() {
					public void run() {
						listener.itemsAdded(event);
					}
					public String toString() {
						return "itemsAdded (Collection)";
					}
				}
			);
		}
	}

	/**
	 * @see ChangeSupport.Notifier#itemsRemoved(CollectionChangeListener, CollectionChangeEvent)
	 */
	public void itemsRemoved(final CollectionChangeListener listener, final CollectionChangeEvent event) {
		if (EventQueue.isDispatchThread()) {
			listener.itemsRemoved(event);
		} else {
			this.invoke(
				new Runnable() {
					public void run() {
						listener.itemsRemoved(event);
					}
					public String toString() {
						return "itemsRemoved (Collection)";
					}
				}
			);
		}
	}

	/**
	 * @see ChangeSupport.Notifier#collectionChanged(CollectionChangeListener, CollectionChangeEvent)
	 */
	public void collectionChanged(final CollectionChangeListener listener, final CollectionChangeEvent event) {
		if (EventQueue.isDispatchThread()) {
			listener.collectionChanged(event);
		} else {
			this.invoke(
				new Runnable() {
					public void run() {
						listener.collectionChanged(event);
					}
					public String toString() {
						return "collectionChanged";
					}
				}
			);
		}
	}

	/**
	 * @see ChangeSupport.Notifier#itemsAdded(ListChangeListener, ListChangeEvent)
	 */
	public void itemsAdded(final ListChangeListener listener, final ListChangeEvent event) {
		if (EventQueue.isDispatchThread()) {
			listener.itemsAdded(event);
		} else {
			this.invoke(
				new Runnable() {
					public void run() {
						listener.itemsAdded(event);
					}
					public String toString() {
						return "itemsAdded (List)";
					}
				}
			);
		}
	}

	/**
	 * @see ChangeSupport.Notifier#itemsRemoved(ListChangeListener, ListChangeEvent)
	 */
	public void itemsRemoved(final ListChangeListener listener, final ListChangeEvent event) {
		if (EventQueue.isDispatchThread()) {
			listener.itemsRemoved(event);
		} else {
			this.invoke(
				new Runnable() {
					public void run() {
						listener.itemsRemoved(event);
					}
					public String toString() {
						return "itemsRemoved (List)";
					}
				}
			);
		}
	}

	/**
	 * @see ChangeSupport.Notifier#itemsReplaced(ListChangeListener, ListChangeEvent)
	 */
	public void itemsReplaced(final ListChangeListener listener, final ListChangeEvent event) {
		if (EventQueue.isDispatchThread()) {
			listener.itemsReplaced(event);
		} else {
			this.invoke(
				new Runnable() {
					public void run() {
						listener.itemsReplaced(event);
					}
					public String toString() {
						return "itemsReplaced (List)";
					}
				}
			);
		}
	}

	/**
	 * @see ChangeSupport.Notifier#listChanged(ListChangeListener, ListChangeEvent)
	 */
	public void listChanged(final ListChangeListener listener, final ListChangeEvent event) {
		if (EventQueue.isDispatchThread()) {
			listener.listChanged(event);
		} else {
			this.invoke(
				new Runnable() {
					public void run() {
						listener.listChanged(event);
					}
					public String toString() {
						return "listChanged";
					}
				}
			);
		}
	}

	/**
	 * @see ChangeSupport.Notifier#nodeAdded(TreeChangeListener, TreeChangeEvent)
	 */
	public void nodeAdded(final TreeChangeListener listener, final TreeChangeEvent event) {
		if (EventQueue.isDispatchThread()) {
			listener.nodeAdded(event);
		} else {
			this.invoke(
				new Runnable() {
					public void run() {
						listener.nodeAdded(event);
					}
					public String toString() {
						return "nodeAdded";
					}
				}
			);
		}
	}

	/**
	 * @see ChangeSupport.Notifier#nodeRemoved(TreeChangeListener, TreeChangeEvent)
	 */
	public void nodeRemoved(final TreeChangeListener listener, final TreeChangeEvent event) {
		if (EventQueue.isDispatchThread()) {
			listener.nodeRemoved(event);
		} else {
			this.invoke(
				new Runnable() {
					public void run() {
						listener.nodeRemoved(event);
					}
					public String toString() {
						return "nodeRemoved";
					}
				}
			);
		}
	}

	/**
	 * @see ChangeSupport.Notifier#treeChanged(TreeChangeListener, TreeChangeEvent)
	 */
	public void treeChanged(final TreeChangeListener listener, final TreeChangeEvent event) {
		if (EventQueue.isDispatchThread()) {
			listener.treeChanged(event);
		} else {
			this.invoke(
				new Runnable() {
					public void run() {
						listener.treeChanged(event);
					}
					public String toString() {
						return "treeChanged";
					}
				}
			);
		}
	}

	/**
	 * EventQueue.invokeLater(Runnable) seems to work OK;
	 * but using #invokeAndWait() can somtimes make things
	 * more predictable when debugging.
	 */
	private void invoke(Runnable r) {
		EventQueue.invokeLater(r);
//		try {
//			EventQueue.invokeAndWait(r);
//		} catch (InterruptedException ex) {
//			throw new RuntimeException(ex);
//		} catch (java.lang.reflect.InvocationTargetException ex) {
//			throw new RuntimeException(ex);
//		}
	}

	/**
	 * Serializable singleton support
	 */
	private Object readResolve() {
		return instance();
	}

}
