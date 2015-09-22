package org.eclipse.persistence.json.bind.model;

import org.eclipse.persistence.json.bind.internal.ReflectionUtils;

import javax.json.bind.JsonbException;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

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
    private Type type;

    /**
     * Model of the class this field belongs to.
     */
    private ClassModel classModel;

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
     * Cached setter method reference. Null if there is no getter.
     */
    private Method setter;

    /**
     * Indicates that getter method for this field is present.
     * Null value means that getter check has not been done yet.
     */
    private Boolean getterPresent = null;

    /**
     * Indicates that setter method for this field is present.
     * Null value means that setter check has not been done yet.
     */
    private Boolean setterPresent = null;

    public FieldModel(ClassModel classModel, Field field) {
        this.type = field.getGenericType();
        this.classModel = classModel;
        this.name = field.getName();
    }

    public Object getValue(Object object) {
        //TODO interchange property descriptors with own implementation
        if (getterPresent == null) {
            // This method is called first time. Try to find a getter and cache it.
            try {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(name, classModel.getRawType());
                getter = propertyDescriptor.getReadMethod();
                getter.setAccessible(true);
                getterPresent = true;
            } catch (IntrospectionException e) {
                getterPresent = false;
            }
        }

        if (getterPresent) {
            // Getter is present -> invoke it.
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

    /**
     * TODO interchange property descriptors with own implementation
     * Set a value to object.
     * @param value Value to set.
     * @param object Object to set in.
     */
    public void setValue(Object value, Object object) {
        if (setterPresent == null) {
            try {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(name, classModel.getRawType());
                setter = propertyDescriptor.getWriteMethod();
                setter.setAccessible(true);
                setterPresent = true;
            } catch (IntrospectionException e) {
                setterPresent = false;
            }
        }

        if (setterPresent) {
            try {
                setter.invoke(object, value);
            } catch (InvocationTargetException | IllegalAccessException e) {
                // TODO logging, more detailed text
                throw new JsonbException("Error setting field value.", e);
            }
        } else {
            try {
                final Field field = object.getClass().getField(name);
                field.setAccessible(true);
                field.set(object, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // TODO logging, more detailed text
                throw   new JsonbException("Error setting field value.", e);
            }
        }
    }

    public String getName() {
        return name;
    }

    public Type getType() {
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
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(name, ReflectionUtils.getRawType(type));
            return propertyDescriptor.getReadMethod();
        } catch (IntrospectionException e) {
            throw new JsonbException("", e);
        }
    }

    public ClassModel getClassModel() {
        return classModel;
    }

    @Override
    public int compareTo(FieldModel o) {
        return name.compareTo(o.getName());
    }
}
