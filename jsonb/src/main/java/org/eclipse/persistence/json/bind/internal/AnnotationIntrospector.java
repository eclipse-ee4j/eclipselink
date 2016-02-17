/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.internal.adapter.AdapterMatcher;
import org.eclipse.persistence.json.bind.internal.adapter.JsonbAdapterInfo;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.model.Property;

import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.*;
import javax.json.bind.config.PropertyVisibilityStrategy;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Introspects configuration on classes and their properties by reading annotations.
 *
 * @author Roman Grigoriadi
 */
public class AnnotationIntrospector {

    private static final AnnotationIntrospector instance = new AnnotationIntrospector();

    /**
     * Gets a singleton instance to use
     * @return instance
     */
    public static AnnotationIntrospector getInstance() {
        return instance;
    }

    private AnnotationIntrospector() {}

    /**
     * Gets a name of property for JSON marshalling.
     * Can be different writeName for same property.
     * @param property property representation - field, getter, setter (not null)
     * @return read name
     */
    public String getJsonbPropertyJsonWriteName(Property property) {
        Objects.requireNonNull(property);
        return getJsonbPropertyCustomizedName(property, property.getGetter());
    }

    /**
     * Gets a name of property for JSON unmarshalling.
     * Can be different from readName for same property.
     * @param property property representation - field, getter, setter (not null)
     * @return write name
     */
    public String getJsonbPropertyJsonReadName(Property property) {
        Objects.requireNonNull(property);
        return getJsonbPropertyCustomizedName(property, property.getSetter());
    }

    private String getJsonbPropertyCustomizedName(Property property, Method method) {
        JsonbProperty methodAnnotation = getMethodAnnotation(JsonbProperty.class, method);
        if (methodAnnotation != null && !methodAnnotation.value().isEmpty()) {
            return methodAnnotation.value();
        }
        //in case of property name getter/setter override field value
        JsonbProperty fieldAnnotation = getFieldAnnotation(JsonbProperty.class, property.getField());
        if (fieldAnnotation != null && !fieldAnnotation.value().isEmpty()) {
            return fieldAnnotation.value();
        }

        return property.getName();
    }

    /**
     * Checks for {@link JsonbAdapter} on a property.
     * @param property property not null
     * @return adapter info
     */
    public JsonbAdapterInfo getAdapter(Property property) {
        Objects.requireNonNull(property);
        Optional<JsonbTypeAdapter> annotation = getAnnotationFromProperty(JsonbTypeAdapter.class, property);
        final Optional<JsonbAdapterInfo> adapterInfoOptional = annotation.map(annot -> {
            final JsonbAdapter adapterInstance = ReflectionUtils.createNoArgConstructorInstance(annot.value());
            final JsonbAdapterInfo adapterInfo = AdapterMatcher.getInstance().introspectAdapterInfo(adapterInstance);
            if ((property.getPropertyType() instanceof Class<?>) &&
                    ReflectionUtils.getRawType(property.getPropertyType()) != ReflectionUtils.getRawType(adapterInfo.getFromType())) {
                throw new JsonbException(Messages.getMessage(MessageKeys.ADAPTER_INCOMPATIBLE, adapterInfo.getFromType(), property.getPropertyType()));
            }
            return adapterInfo;
        });
        return adapterInfoOptional.orElse(null);
    }
    /**
     * Checks if property is nillable.
     * Looks for {@link JsonbProperty} nillable attribute only.
     * JsonbNillable is checked only for ClassModels.
     *
     * @param property property to search in, not null
     * @return True if property should be serialized when null.
     */
    public boolean isPropertyNillable(Property property) {
        Objects.requireNonNull(property);

        final Optional<JsonbProperty> jsonbProperty = getAnnotationFromProperty(JsonbProperty.class, property);
        return jsonbProperty.isPresent() && jsonbProperty.get().nillable();

    }

    /**
     * Checks for JsonbNillable annotation on a class, its superclasses and interfaces.
     *
     * @param clazz class to search JsonbNillable in.
     * @return true if found
     */
    public boolean isClassNillable(Class<?> clazz) {
        //TODO potential unnecessary duplicate search for inheritance. Consider a cache.
        JsonbNillable classLevel = searchAnnotationInClassHierarchy(JsonbNillable.class, clazz);
        if(classLevel != null) {
            return classLevel.value();
        }
        JsonbNillable interfaceLevel = searchInInterfaceHierarchy(JsonbNillable.class, clazz);
        if (interfaceLevel != null) {
            return interfaceLevel.value();
        }
        JsonbNillable packageLevel = clazz.getPackage().getAnnotation(JsonbNillable.class);
        return packageLevel != null && packageLevel.value();
    }

    /**
     * Checks if property is annotated transient.
     * If JsonbTransient annotation is present on field getter or setter, and other annotation is present on either
     * of it, JsonbException is thrown with message describing collision.
     *
     * @param property not null
     * @return JsonbTransient annotation is found on field, getter of setter of a property
     */
    public boolean isTransient(Property property) {
        Objects.requireNonNull(property);
        final Optional<JsonbTransient> jsonbTransient = getAnnotationFromProperty(JsonbTransient.class, property);
        if (jsonbTransient.isPresent()) {
            final Class[] FORBIDDEN_ANNOTATIONS = new Class[]{JsonbProperty.class, JsonbNillable.class, JsonbCreator.class, JsonbDateFormat.class, JsonbNumberFormat.class, JsonbPropertyOrder.class, JsonbVisibility.class};
            for (Class annotClass : FORBIDDEN_ANNOTATIONS) {
                if (getAnnotationFromProperty(annotClass, property).isPresent()) {
                    throw new JsonbException(String.format("JsonbTransient annotation collides with %s for property %s", annotClass, property.getName()));
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Get a @JsonbVisibility annotation from a class or its package.
     * @param clazz Class to lookup annotation
     * @return Instantiated PropertyVisibilityStrategy if annotation is present
     */
    public Optional<PropertyVisibilityStrategy> getPropertyVisibilityStrategy(Class<?> clazz) {
        JsonbVisibility visibilityAnnotation = clazz.getAnnotation(JsonbVisibility.class);
        if (visibilityAnnotation == null) {
            visibilityAnnotation = clazz.getPackage().getAnnotation(JsonbVisibility.class);
        }
        final Optional<JsonbVisibility> visibilityOptional = Optional.ofNullable(visibilityAnnotation);
        return visibilityOptional.map(jsonbVisibility -> ReflectionUtils.createNoArgConstructorInstance(jsonbVisibility.value()));
    }

    /**
     * Gets an annotation from first resolved annotation in a property in this order:
     * <p>1. Field, 2. Getter, 3 Setter.</p>
     * First found overrides other.
     *
     * @param annotationClass Annotation class to search for
     * @param property property to search in
     * @param <T> Annotation type
     * @return Annotation if found, null otherwise
     */
    private <T extends Annotation> Optional<T> getAnnotationFromProperty(Class<T> annotationClass, Property property) {
        T fieldAnnotation = getFieldAnnotation(annotationClass, property.getField());
        if (fieldAnnotation != null) {
            return Optional.of(fieldAnnotation);
        }

        T getterAnnotation = getMethodAnnotation(annotationClass, property.getGetter());
        if (getterAnnotation != null) {
            return Optional.of(getterAnnotation);
        }

        T setterAnnotation = getMethodAnnotation(annotationClass, property.getSetter());
        if (setterAnnotation != null) {
            return Optional.of(setterAnnotation);
        }

        return Optional.empty();
    }



    private <T extends Annotation> T getFieldAnnotation(Class<T> annotationClass, Field field ) {
        if (field == null) {
            return null;
        }
        return field.getAnnotation(annotationClass);
    }

    private <T extends Annotation> T getMethodAnnotation(Class<T> annotationClass, Method method) {
        if (method == null) {
            return null;
        }
        return method.getAnnotation(annotationClass);
    }

    private <T extends Annotation> T searchAnnotationInClassHierarchy(Class<T> annotationClass, Class<?> declaringClass) {
        if (declaringClass == null || declaringClass == Object.class) {
            return null;
        }
        T annotation = declaringClass.getAnnotation(annotationClass);
        if (annotation != null) {
            return annotation;
        }

        return searchAnnotationInClassHierarchy(annotationClass, declaringClass.getSuperclass());
    }

    private <T extends Annotation> T searchInInterfaceHierarchy(Class<T> annotationClass, Class<?> clazz) {
        if (clazz == Object.class) {
            return null;
        }
        final Map<Class<?>, T> annotationMapping = new HashMap<>();
        collectFromInterfaces(annotationClass, clazz, annotationMapping);
        if (annotationMapping.size() == 1) {
            return annotationMapping.values().iterator().next();
        } else if (annotationMapping.size() > 1) {
            StringBuilder message = new StringBuilder("Duplicate mapping found for ").append(annotationClass).append(" on interfaces:");
            for (Map.Entry<Class<?>, T> entry : annotationMapping.entrySet()) {
                message.append("[").append(entry.getKey()).append("],");
            }
            throw new JsonbException(message.toString());
        }
        //TODO GR (after "ThreadLocal" refactoring) it would be better to scan interfaces only of current class without superclasses here and check superclasses in ClassModel during customization initialization
        return searchInInterfaceHierarchy(annotationClass, clazz.getSuperclass());
    }

    private <T extends Annotation> void collectFromInterfaces(Class<T> annotationClass, Class clazz, Map<Class<?>, T> collectedAnnotations) {

        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            T annotation = interfaceClass.getAnnotation(annotationClass);
            if (annotation != null) {
                collectedAnnotations.put(interfaceClass, annotation);
            }
            collectFromInterfaces(annotationClass, interfaceClass, collectedAnnotations);
        }
    }

    /**
     * Returns JsonbPropertyOrder annotation if class has one
     * @param clazz Class to be checked
     * @return
     */
    public Optional<JsonbPropertyOrder> getJsonbPropertyOrderAnnotation(Class<?> clazz) {
        return Optional.ofNullable(clazz.getAnnotation(JsonbPropertyOrder.class));
    }
}
