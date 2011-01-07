/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.utility.node;

import org.eclipse.persistence.tools.workbench.utility.SynchronizedBoolean;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This implementation of the Validator interface implements the
 * pause/resume portion of the protocol, but delegates the actual
 * validation to a "pluggable" delegate.
 */
public class PluggableValidator implements Node.Validator {
	private boolean pause;
	private Delegate delegate;


	/**
	 * Convenience factory method.
	 */
	public static Node.Validator buildAsynchronousValidator(SynchronizedBoolean validateFlag) {
		return new PluggableValidator(new AsynchronousValidator(validateFlag));
	}

	/**
	 * Convenience factory method.
	 */
	public static Node.Validator buildSynchronousValidator(Node node) {
		return new PluggableValidator(new SynchronousValidator(node));
	}

	/**
	 * Construct a validator with the specified delegate.
	 */
	public PluggableValidator(Delegate delegate) {
		super();
		this.pause = false;
		this.delegate = delegate;
	}

	/**
	 * @see Node.Validator#validate()
	 */
	public void validate() {
		if ( ! this.pause) {
			this.delegate.validate();
		}
	}

	/**
	 * @see Node.Validator#pause()
	 */
	public void pause() {
		if (this.pause) {
			throw new IllegalStateException("already paused");
		}
		this.pause = true;
	}

	/**
	 * @see Node.Validator#resume()
	 */
	public void resume() {
		if ( ! this.pause) {
			throw new IllegalStateException("not paused");
		}
		this.pause = false;
		// validate all the changes that occurred while the validation was paused
		this.delegate.validate();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, this.delegate);
	}


	// ********** member interface **********

	/**
	 * Interface implemented by any delegates of a pluggable validator.
	 */
	public interface Delegate {

		/**
		 * The validator is not "paused" - perform the appropriate validation.
		 */
		void validate();


		/**
		 * This delegate does nothing.
		 */
		Delegate NULL_DELEGATE =
			new PluggableValidator.Delegate() {
				public void validate() {
					// do nothing
				}
				public String toString() {
					return "NULL_DELEGATE";
				}
			};

	}

}
