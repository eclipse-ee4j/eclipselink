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
 *     tware, ssmith = 1.0 - weaver wrapper for Equinox
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.deployment.osgi.equinox;

import java.lang.instrument.IllegalClassFormatException;

import javax.persistence.spi.ClassTransformer;

import org.eclipse.persistence.jpa.equinox.weaving.IWeaver;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;


/**
 * Provides a weaving wrapper for JPA on OSGi
 * 
 * @author tware
 *
 */
@SuppressWarnings("restriction")
final class EquinoxWeaver implements IWeaver {
	private final ClassTransformer transformer;

	EquinoxWeaver(final ClassTransformer transformer) {
		this.transformer = transformer;
	}

	public byte[] transform(String className, byte[] classfileBuffer) {
		try {
			byte[] transformedBytes = transformer.transform(null, className, null, null, classfileBuffer);
			if (transformedBytes != null) {
				AbstractSessionLog.getLog().log(SessionLog.FINER, className + " woven successfully");  // TODO NON-NLS 
			}
			return transformedBytes;
		} catch (IllegalClassFormatException e) {
			// TODO log appropriate warning
			e.printStackTrace();
			return null;
		}
	}
}
