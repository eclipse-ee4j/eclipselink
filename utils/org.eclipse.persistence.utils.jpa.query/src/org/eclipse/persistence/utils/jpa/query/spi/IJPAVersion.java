/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query.spi;

/**
 * An enumeration listing the various released versions of the Java Persistence
 * specification.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public enum IJPAVersion
{
	/**
	 * The constant for the Java Persistence version 1.0.
	 */
	VERSION_1_0,

	/**
	 * The constant for the Java Persistence version 2.0.
	 */
	VERSION_2_0;

	/**
	 * Determines whether this constant represents a version that is newer than
	 * the given version.
	 *
	 * @param version The constant to verify if it's representing a version that
	 * is older than this one
	 * @return <code>true</code> if this constant represents a newer version and
	 * the given constant represents a version that is older;
	 * <code>false</code> if the given constant represents a newer and this
	 * constant represents an older version
	 */
	public boolean isNewerThan(IJPAVersion version)
	{
		return !isOlderThan(version);
	}

	/**
	 * Determines whether this constant represents a version that is newer than
	 * the given version or if it's the same version.
	 *
	 * @param version The constant to verify if it's representing a version that
	 * is older than this one or if it's the same than this one
	 * @return <code>true</code> if this constant represents a newer version and
	 * the given constant represents a version that is older or if it's the same
	 * constant; <code>false</code> if the given constant represents a newer and
	 * this constant represents an older version
	 */
	public boolean isNewerThanOrEqual(IJPAVersion version)
	{
		return (this == version) || isNewerThan(version);
	}

	/**
	 * Determines whether this constant represents a version that is older than
	 * the given version.
	 *
	 * @param version The constant to verify if it's representing a version that
	 * is more recent than this one
	 * @return <code>true</code> if this constant represents an earlier version
	 * and the given constant represents a version that is more recent;
	 * <code>false</code> if the given constant represents an earlier version and
	 * this constant represents a more recent version
	 */
	public boolean isOlderThan(IJPAVersion version)
	{
		return ordinal() < version.ordinal();
	}

	/**
	 * Determines whether this constant represents a version that is older than
	 * the given version or if it's the same version.
	 *
	 * @param version The constant to verify if it's representing a version that
	 * is more recent than this one or if it's the same than this one
	 * @return <code>true</code> if this constant represents an earlier version
	 * and the given constant represents a version that is more recent or if it's
	 * the same constant; <code>false</code> if the given constant represents an
	 * earlier version and this constant represents a more recent version
	 */
	public boolean isOlderThanOrEqual(IJPAVersion version)
	{
		return (this == version) || isOlderThan(version);
	}
}