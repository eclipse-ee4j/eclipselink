/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/26/2008-1.0M6 Guy Pelletier 
 *       - 211302: Add variable 1-1 mapping support to the EclipseLink-ORM.XML Schema
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes 
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.FetchType;

import static javax.persistence.FetchType.EAGER;

/** 
 * Variable one to one mappings are used to represent a pointer references
 * between a java object and an implementer of an interface. This mapping is 
 * usually represented by a single pointer (stored in an instance variable) 
 * between the source and target objects. In the relational database tables, 
 * these mappings are normally implemented using a foreign key and a type code.
 * 
 * A VariableOneToOne can be specified within an Entity, MappedSuperclass 
 * and Embeddable class.
 * 
 * @author Guy Pelletier
 * @since Eclipselink 1.0 
 */ 
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)
public @interface VariableOneToOne {
    /**
     * (Optional) The interface class that is the target of the association. If
     * not specified it will be inferred from the type of the object being 
     * referenced.
     */
    Class targetInterface() default void.class;
    
    /**
     * (Optional) The operations that must be cascaded to the target of the 
     * association.
     */
    CascadeType[] cascade() default {};
    
    /**
     * (Optional) Defines whether the value of the field or property should
     * be lazily loaded or must be eagerly fetched. The EAGER strategy is a 
     * requirement on the persistence provider runtime that the value must be 
     * eagerly fetched. The LAZY strategy is a hint to the persistence provider 
     * runtime. If not specified, defaults to EAGER.
     */
    FetchType fetch() default EAGER;
    
    /**
     * (Optional) Whether the association is optional. If set to false then a 
     * non-null relationship must always exist.
     */
    boolean optional() default true;
    
    /**
     * (Optional) Whether to apply the remove operation to entities that have
     * been removed from the relationship and to cascade the remove operation to
     * those entities.
     */
    boolean orphanRemoval() default false;
  
    /**
     * (Optional) The discriminator column will hold the type indicators. If the 
     * DiscriminatorColumn is not specified, the name of the discriminator 
     * column defaults to "DTYPE" and the discriminator type to STRING.
     */
    DiscriminatorColumn discriminatorColumn() default @DiscriminatorColumn;
    
    /**
     * (Optional) The list of discriminator types that can be used with this
     * VariableOneToOne. If none are specified then those entities within the
     * persistence unit that implement the target interface will be added to 
     * the list of types. The discriminator type will default as follows:
     *  - If DiscriminatorColumn type is STRING: Entity.name()
     *  - If DiscriminatorColumn type is CHAR: First letter of the Entity class
     *  - If DiscriminatorColumn type is INTEGER: The next integer after the
     *    highest integer explicitly added.
     */
    DiscriminatorClass[] discriminatorClasses() default {};
}
