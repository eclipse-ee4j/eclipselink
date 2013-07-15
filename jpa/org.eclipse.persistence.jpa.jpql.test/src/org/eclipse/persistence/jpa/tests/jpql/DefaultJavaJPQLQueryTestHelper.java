/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql;

import java.lang.reflect.Member;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMappingBuilder;
import org.eclipse.persistence.jpa.jpql.tools.spi.java.JavaMappingBuilder;

/**
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public final class DefaultJavaJPQLQueryTestHelper extends JavaJPQLQueryTestHelper {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IMappingBuilder<Member> buildMappingBuilder() {
		return new JavaMappingBuilder();
	}
}