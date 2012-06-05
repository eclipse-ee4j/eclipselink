/*******************************************************************************
 * Copyright (c) 2008 - 2012 Oracle Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Linda DeMichiel - Java Persistence 2.1
 *     Linda DeMichiel - Java Persistence 2.0
 *
 ******************************************************************************/ 
package javax.persistence;

import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Declares one or more {@link PersistenceContext} annotations.
 * It is used to express a dependency on container-managed
 * entity manager persistence contexts.
 *
 *@see PersistenceContext
 *
 * @since Java Persistence 1.0
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface PersistenceContexts {

    /** (Required) One or more <code>PersistenceContext</code> annotations. */
    PersistenceContext[] value();

}
