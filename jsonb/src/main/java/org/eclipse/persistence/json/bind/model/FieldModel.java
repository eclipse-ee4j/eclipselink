package org.eclipse.persistence.json.bind.model;

import javax.json.bind.JsonbException;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A model for class field.
 *
 * @author Dmitry Kornilov
 */
public class FieldModel implements Comparable<FieldModel> {
    /**
     * Field name as in class.
     */
    private final String name;

    /**
     * Field type.
     */
    private final Class type;

    /**
     * Model of the class this field belongs to.
     */
    private final ClassModel classModel;

    /**
     * Field name as it is written in JSON document during marshalling.
     * {@link javax.json.bind.annotation.JsonbProperty} customization on getter. Defaults to {@see name} if not set.
     */
    private String writeName;

    /**
     * Field name to read from JSON document during unmarshalling.
     * {@link javax.json.bind.annotation.JsonbProperty} customization on setter. Defaults to {@see name} if not set.
     */
    private String readName;

    /**
     * Indicates that this field is nillable (@JsonbProperty(nillable=true)).
     */
    private boolean nillable;

    /**
     * Cached getter method reference. Null if there is no getter.
     */
    private Method getter;

    /**
     * Indicates that getter method for this field is present.
     * Null value means that getter check has not been done yet.
     */
    private Boolean getterPresent = null;

    public FieldModel(ClassModel classModel, String name, Class type) {
        this.classModel = classModel;
        this.name = name;
        this.type = type;
    }

    public Object getValue(Object object) {
        if (getterPresent == null) {
            // This method is called first time. Try to find a getter and cache it.
            try {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(name, classModel.getType());
                getter = propertyDescriptor.getReadMethod();
                getterPresent = true;
            } catch (IntrospectionException e) {
                getterPresent = false;
            }
        }

        if (getterPresent) {
            // Getter is present -> invoke it.
            getter.setAccessible(true);
            try {
                return getter.invoke(object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                // TODO logging, more detailed text
                throw new JsonbException("Error invoking getter method.", e);
            }
        } else {
            // Getter is not present -> use reflection.
            try {
                final Field field = object.getClass().getField(name);
                field.setAccessible(true);
                return field.get(object);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // TODO logging, more detailed text
                throw new JsonbException("Error getting field value.", e);
            }
        }
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }

    public String getWriteName() {
        if (writeName == null) {
            return name;
        }
        return writeName;
    }

    public void setWriteName(String writeName) {
        this.writeName = writeName;
    }

    public String getReadName() {
        if (readName == null) {
            return name;
        }
        return readName;
    }

    public void setReadName(String readName) {
        this.readName = readName;
    }

    public boolean isNillable() {
        return nillable;
    }

    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    public Method getGetter() {
        try {
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(name, type);
            return propertyDescriptor.getReadMethod();
        } catch (IntrospectionException e) {
            throw new JsonbException("", e);
        }
    }

    @Override
    public int compareTo(FieldModel o) {
        return name.compareTo(o.getName());
    }
}
