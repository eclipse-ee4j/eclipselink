/*
 * Copyright (c) 2008, Oracle. All rights reserved.
 *
 * This software is the proprietary information of Oracle Corporation.
 * Use is subject to license terms.
 */
package org.eclipse.persistence.tools.workbench.scplugin.model;

/**
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public enum SequenceType
{
	DEFAULT,

	/**
	 * Designates the database sequence mechanism is used.
	 */
	NATIVE,

	TABLE,
	UNARY_TABLE,
	XML_FILE
}