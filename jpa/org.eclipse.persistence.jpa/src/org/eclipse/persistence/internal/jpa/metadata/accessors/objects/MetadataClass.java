/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.libraries.asm.Constants;

/**
 * INTERNAL:
 * An object to hold onto a valid EJB 3.0 decorated field.
 * 
 * @author Guy Pelletier
 * @since TopLink 10.1.3/EJB 3.0 Preview
 */
public class MetadataClass extends MetadataAnnotatedElement {
    protected String superclass;
    protected List<String> interfaces;
    protected int modifiers;
    protected boolean isPrimitive;
    
    /** Store the classes field metadata, keyed by the field's name. */
    protected Map<String, MetadataField> fields = new HashMap<String, MetadataField>();

    /**
     * Store the classes method metadata, keyed by the method's name.
     * Method's next is used if multiple method with the same name.
     */
    protected Map<String, MetadataMethod> methods = new HashMap<String, MetadataMethod>();

    /**
     * Create the metadata class with the class name.
     */
    public MetadataClass(String name) {
        super(MetadataFactory.logger);
        setName(name);
    }
    
    /**
     * Create the metadata class based on the class.
     * Mainly used for primitive defaults.
     */
    public MetadataClass(Class cls) {
        this(cls.getName());
        this.isPrimitive = cls.isPrimitive();
    }

    public Map<String, MetadataField> getFields() {
        return fields;
    }

    /**
     * Return the field with the name.
     * Search for any declared or inherited field.
     */
    public MetadataField getField(String name) {
        MetadataField field = this.fields.get(name);
        if ((field == null) && (getSuperclassName() != null)) {
            return getSuperclass().getField(name);
        }
        return field;
    }

    public void setFields(Map<String, MetadataField> fields) {
        this.fields = fields;
    }

    public Map<String, MetadataMethod> getMethods() {
        return methods;
    }

    /**
     * Return the method with the name and no arguments.
     */
    public MetadataMethod getMethod(String name) {
        return this.methods.get(name);
    }

    /**
     * Return the method with the name and argument types .
     */
    public MetadataMethod getMethod(String name, Class[] arguments) {
        List<String> argumentNames = new ArrayList<String>(arguments.length);
        for (int index = 0; index < arguments.length; index++) {
            argumentNames.add(arguments[index].getName());
        }
        MetadataMethod method = getMethod(name, argumentNames);
        if (method != null) {
            return method;
        }
        if (getSuperclassName() != null) {
            return getSuperclass().getMethod(name, arguments);
        }
        return null;
    }

    /**
     * Return the method with the name and argument types (class names).
     */
    public MetadataMethod getMethod(String name, List<String> arguments) {
        MetadataMethod method = this.methods.get(name);
        if (method == null) {
            return null;
        }
        while (!method.getParameters().equals(arguments)) {
            method = method.getNext();
            if (method == null) {
                return null;
            }
        }
        return method;
    }
    
    /**
     * Return the method for the given property name.
     */
    public MetadataMethod getMethodForPropertyName(String propertyName) {
        MetadataMethod method;
        
        String leadingChar = String.valueOf(propertyName.charAt(0)).toUpperCase();
        String restOfName = propertyName.substring(1);
        
        // Look for a getPropertyName() method
        method = getMethod(MetadataMethod.GET_PROPERTY_METHOD_PREFIX.concat(leadingChar).concat(restOfName));
        
        if (method == null) {
            // Look for an isPropertyName() method
            method = getMethod(MetadataMethod.IS_PROPERTY_METHOD_PREFIX.concat(leadingChar).concat(restOfName));
        }
        
        if (method != null) {
            method.setSetMethod(method.getSetMethod(this));
        }
        
        return method;
    }

    public void setMethods(Map<String, MetadataMethod> methods) {
        this.methods = methods;
    }
    
    public MetadataClass getSuperclass() {
        return MetadataFactory.getClassMetadata(this.superclass);
    }

    public String getSuperclassName() {
        return superclass;
    }

    public void setSuperclassName(String superclass) {
        this.superclass = superclass;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<String> interfaces) {
        this.interfaces = interfaces;
    }
    
    /**
     * Return if this class is or extends, or super class extends the class.
     */
    public boolean extendsClass(Class javaClass) {
        return extendsClass(javaClass.getName());
    }
    
    /**
     * Return if this class is or extends, or super class extends the class.
     */
    public boolean extendsClass(String className) {
        if (getName() == null) {
            return className == null;
        }
        if (getName().equals(className)) {
            return true;
        }
        if (getSuperclassName() == null) {
            return false;
        }
        if (getSuperclassName().equals(className)) {
            return true;
        }
        return getSuperclass().extendsClass(className);
    }
    
    /**
     * Return if this class is an array type.
     */
    public boolean isArray() {
        return (getName() != null) && (getName().charAt(0) == '[');
    }
    
    /**
     * Return if this class extends Serializable or is an array type.
     */
    public boolean isSerializable() {
        if (isArray()) {
            return true;
        }
        return extendsInterface(Serializable.class);
    }
    
    /**
     * Return if this class is or extends, or super class extends the interface.
     */
    public boolean extendsInterface(Class javaClass) {
        return extendsInterface(javaClass.getName());
    }
    
    /**
     * Return if this class is or extends, or super class extends the interface.
     */
    public boolean extendsInterface(String className) {
        if (getName() == null) {
            return false;
        }
        if (getName().equals(className)) {
            return true;
        }
        if (getInterfaces() == null) {
            return false;
        }
        if (getInterfaces().contains(className)) {
            return true;
        }
        for (String interfaceName : getInterfaces()) {
            if (MetadataFactory.getClassMetadata(interfaceName).extendsInterface(className)) {
                return true;
            }
        }
        if (getSuperclassName() == null) {
            return false;
        }
        return getSuperclass().extendsInterface(className);
    }
    
    /**
     * Return if this is the void class.
     */
    public boolean isVoid() {
        return getName().equals(void.class.getName());
    }
    
    /**
     * Return if this is Object class.
     */
    public boolean isObject() {
        return getName().equals(Object.class.getName());
    }
    
    /**
     * Return if this is a primitive.
     */
    public boolean isPrimitive() {
        return isPrimitive;
    }
    
    /**
     * Return if this is extends Enum.
     */
    public boolean isEnum() {
        return extendsClass(Enum.class);
    }
    
    /**
     * Return if this is extends Map.
     */
    public boolean isMap() {
        return extendsInterface(Map.class);
    }
    
    /**
     * Return if this is extends Collection.
     */
    public boolean isCollection() {
        return extendsInterface(Collection.class);
    }
    
    /**
     * Return if this is an interface (super is null).
     */
    public boolean isInterface() {
        return (Constants.ACC_INTERFACE & this.modifiers) != 0;
    }
    
    /**
     * Return if this is extends List.
     */
    public boolean isList() {
        return extendsInterface(List.class);
    }

    
    /**
     * Allow comparison to Java classes and Metadata classes.
     */
    public boolean equals(Object object) {
        if (object instanceof Class) {
            if (getName() == null) {
                // Void's name is null.
                return ((Class)object).getName() == null;
            }
            return getName().equals(((Class)object).getName());
        }
        return super.equals(object);
    }

    public int getModifiers() {
        return modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }
}