/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.uitools;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * This handler will forward any exceptions thrown during AWT event processing
 * to a static "global" handler. The "global" handler can be set via the
 * #setGlobalHandler(GlobalHandler) static method.
 * 
 * To use this class, you must install it by setting a System property,
 * either in code:
 * 	AWTExceptionHandler.register();
 * 		- or -
 * 	System.setProperty("sun.awt.exception.handler", AWTExceptionHandler.class.getName());
 * or via a JVM command-line argument:
 * 	java -Dsun.awt.exception.handler=org.eclipse.persistence.tools.workbench.uitools.AWTExceptionHandler
 * 
 * @see java.awt.EventDispatchThread#handleException(Throwable)
 * 
 * I'm not quite sure what Sun was thinking when the put this "temporary hack"
 * in place. The handler is instantiated with every exception and has no context....
 * A better solution would probably be API on EventDispatchThread for setting
 * the exception handler.... As a result, we have to route everything through a
 * configurable Singleton.
 * 
 * NB: Support for this type of handler may go away in future
 * releases of the JDK. Currently it works with JDK 1.4.2.
 */
public class AWTExceptionHandler {

	/**
	 * The "global" handler that will be forwarded any exceptions that
	 * occur during AWT event processing.
	 */
	private static GlobalHandler globalHandler = GlobalHandler.DEFAULT_INSTANCE;

	/**
	 * The name of the system property that must be set to the
	 * class name an AWT "exception handler".
	 */
	public static final String AWT_EXCEPTION_HANDLER_PROPERTY_NAME = "sun.awt.exception.handler";


	/**
	 * Register this class as the AWT "exception handler".
	 */
	public static void register() {
		System.setProperty(AWT_EXCEPTION_HANDLER_PROPERTY_NAME, AWTExceptionHandler.class.getName());
	}


	/**
	 * Set the "global" handler that will handle all exceptions that occur
	 * during AWT event processing.
	 */
	public static synchronized void setGlobalHandler(GlobalHandler handler) {
		globalHandler = ((handler == null) ? GlobalHandler.DEFAULT_INSTANCE : handler);
	}


	// ********** "AWT exception handler" implementation **********

	/**
	 * We must provide a public default constructor, which will be invoked
	 * reflectively by EventDispatchThread.
	 */
	public AWTExceptionHandler() {
		super();
	}

	/**
	 * We must provide a public method named "handle" that takes a single
	 * parameter of type Throwable, which will be invoked reflectively by
	 * EventDispatchThread.
	 */
	public void handle(Throwable t) {
		try {
			globalHandler.handle(t);
		} catch (Throwable t2) {
			this.handle(t, t2);
		}
	}

	/**
	 * Don't allow any exceptions to slip through or the AWT
	 * EventDispatchThread will not use us again.
	 */
	private void handle(Throwable original, Throwable subsequent) {
		try {
			System.err.println("Problem occurred while handling AWT Event Problem:");
			subsequent.printStackTrace();
			System.err.println();
			System.err.println("Original exception:");
			original.printStackTrace();
		} catch (Throwable t3) {
			// if we have problems printing to System.err, just eat the exception;
			// there is no other easy way to communicate the problem...
		}
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this);
	}


	// ********** nested interface **********

	/**
	 * Define a simple interface for the "global" handler that will be
	 * passed any exception that occurs during AWT event processing.
	 */
	public interface GlobalHandler {

		/**
		 * The specified exception occurred during AWT event processing.
		 * Do something reasonable with it.
		 */
		void handle(Throwable t);


		GlobalHandler DEFAULT_INSTANCE =
			new GlobalHandler() {
				public void handle(Throwable t) {
					t.printStackTrace();
				}
				public String toString() {
					return "DefaultGlobalHandler";
				}
			};

	}

}
