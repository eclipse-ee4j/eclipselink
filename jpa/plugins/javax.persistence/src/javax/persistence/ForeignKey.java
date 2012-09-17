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

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used in schema generation to override the persistence provider's 
 * default foreign key strategy.  
 * It is used to define a foreign key constraint or to otherwise
 * override or disable the persistence provider's default foreign key 
 * definition.
 * <p>
 * The syntax used in the <code>foreignKeyDefinition</code> element 
 * should follow the SQL syntax used by the target database for foreign
 * key constraints.  For example, this may be similar the following:
 * <p>
 * <pre>
 * FOREIGN KEY ( &#060;COLUMN expression&#062; {, &#060;COLUMN expression&#062;}... )
 * REFERENCES &#060;TABLE identifier&#062; [
 *     (&#060;COLUMN expression&#062; {, &#060;COLUMN expression&#062;}... ) ]
 * [ ON UPDATE &#060;referential action&#062; ]
 * [ ON DELETE &#060;referential action&#062; ]
 * </pre>
 *
 * If <code>disableForeignKey</code> is <code>true</code>, the
 * persistence provider must not generate a foreign key constraint.
 *
 * @see JoinColumn
 * @see JoinColumns
 * @see MapKeyJoinColumn
 * @see MapKeyJoinColumns
 * @see PrimaryKeyJoinColumn
 * @see PrimaryKeyJoinColumns
 *
 * @since Java Persistence 2.1
 */
@Target({})
@Retention(RUNTIME)
public @interface ForeignKey {

    /**
     * (Optional) The name of the foreign key constraint.  If this
     * is not specified, it defaults to a provider-generated name.
     */
    String name() default "";

    /**
     * (Optional) The foreign key constraint definition.  If this
     * is not specified, and disableForeignKey is false, the 
     * persistence provider's default foreign key strategy will apply.
     */
    String foreignKeyDefinition() default "";

    /**
     * (Optional) Used to specify that the persistence provider should not
     *  generate a foreign key constraint.
     */
    boolean disableForeignKey() default false;
}
