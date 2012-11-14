/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql;

/**
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public interface EclipseLinkSemanticValidatorExtension {

	EclipseLinkSemanticValidatorExtension NULL_EXTENSION = new EclipseLinkSemanticValidatorExtension() {
		public boolean columnExists(String tableName, String columnName) {
			return false;
		}
		public String getEntityTable(String entityName) {
			return null;
		}
		public boolean tableExists(String tableName) {
			return false;
		}
	};

	boolean columnExists(String tableName, String columnName);

	String getEntityTable(String entityName);

	boolean tableExists(String tableName);
}