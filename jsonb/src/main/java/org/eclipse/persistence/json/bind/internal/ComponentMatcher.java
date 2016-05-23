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

import org.eclipse.persistence.json.bind.internal.adapter.AbstractComponentBinding;
import org.eclipse.persistence.json.bind.internal.adapter.AdapterBinding;
import org.eclipse.persistence.json.bind.internal.adapter.ComponentBindings;
import org.eclipse.persistence.json.bind.internal.adapter.DeserializerBinding;
import org.eclipse.persistence.json.bind.internal.adapter.SerializerBinding;
import org.eclipse.persistence.json.bind.model.PropertyModel;
import org.eclipse.persistence.json.bind.model.TypeWrapper;

import javax.json.bind.JsonbConfig;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.bind.serializer.JsonbSerializer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * Searches for a registered adapter or Serializer for a given type.
 *
 * @author Roman Grigoriadi
 */
public class ComponentMatcher {

    /**
     * Supplier for component binging.
     * @param <T> component binding class
     */
    private interface ComponentSupplier<T extends AbstractComponentBinding> {

        T getComponent(ComponentBindings componentBindings);
    }

    private final ConcurrentMap<Type, ComponentBindings> userComponents;

    ComponentMatcher() {
        userComponents = new ConcurrentHashMap<>();
    }

    /**
     * Called during context creation, introspecting user components provided with JsonbConfig.
     * @param context context
     */
    void init(JsonbContext context) {
        final JsonbSerializer<?>[] serializers = (JsonbSerializer<?>[])context.getConfig().getProperty(JsonbConfig.SERIALIZERS).orElse(new JsonbSerializer<?>[]{});
        for (JsonbSerializer serializer : serializers) {
            introspectSerialzierBinding(serializer.getClass(), () -> serializer);
        }
        final JsonbDeserializer<?>[] deserializers = (JsonbDeserializer<?>[])context.getConfig().getProperty(JsonbConfig.DESERIALIZERS).orElse(new JsonbDeserializer<?>[]{});
        for (JsonbDeserializer deserializer : deserializers) {
            introspectDeserializerBinding(deserializer.getClass(), () -> deserializer);
        }

        final JsonbAdapter<?, ?>[] adapters = (JsonbAdapter<?, ?>[]) context.getConfig().getProperty(JsonbConfig.ADAPTERS).orElse(new JsonbAdapter<?, ?>[]{});
        for (JsonbAdapter<?, ?> adapter : adapters) {
            introspectAdapterBinding(adapter.getClass(), () -> adapter);
        }
    }

    private ComponentBindings getBindingInfo(Type type) {
        return userComponents.compute(type, (type1, bindingInfo) -> bindingInfo != null ? bindingInfo : new ComponentBindings(type1));
    }

    private void addSeserializer(Type bindingType, SerializerBinding serializer) {
        userComponents.computeIfPresent(bindingType, (type, bindings) -> bindings.getSerializer() != null ?
                bindings :
                new ComponentBindings(bindingType, serializer, bindings.getDeserializer(), bindings.getAdapterInfo()));
    }

    private void addDeserializer(Type bindingType, DeserializerBinding deserializer) {
        userComponents.computeIfPresent(bindingType, (type, bindings) -> bindings.getDeserializer() != null ?
                bindings :
                new ComponentBindings(bindingType, bindings.getSerializer(), deserializer, bindings.getAdapterInfo()));
    }

    private void addApapter(Type bindingType, AdapterBinding adapter) {
        userComponents.computeIfPresent(bindingType, (type, bindings) -> bindings.getAdapterInfo() != null ?
                bindings :
                new ComponentBindings(bindingType, bindings.getSerializer(), bindings.getDeserializer(), adapter));
    }

    /**
     * Lookup serializer binding for a given property runtime type.
     * @param propertyRuntimeType runtime type of a property
     * @param propertyModel model of a property
     * @param <T> Type which is serializer bound to
     * @return serializer optional
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<SerializerBinding<T>> getSerialzierBinding(Type propertyRuntimeType, PropertyModel propertyModel) {
        if (propertyModel == null || propertyModel.getCustomization().getSerializerBinding() == null) {
            return getComponentInfo(propertyRuntimeType, ComponentBindings::getSerializer);
        }
        return getComponentBinding(propertyRuntimeType, propertyModel.getCustomization().getSerializerBinding());
    }

    /**
     * Lookup deserializer binding for a given property runtime type.
     * @param propertyRuntimeType runtime type of a property
     * @param propertyModel model of a property
     * @return serializer optional
     */
    @SuppressWarnings("unchecked")
    public Optional<DeserializerBinding<?>> getDeserialzierBinding(Type propertyRuntimeType, PropertyModel propertyModel) {
        if (propertyModel == null || propertyModel.getCustomization().getSerializerBinding() == null) {
            return getComponentInfo(propertyRuntimeType, ComponentBindings::getDeserializer);
        }
        return getComponentBinding(propertyRuntimeType, propertyModel.getCustomization().getDeserializerBinding());
    }

    /**
     * Get adapter from property model (if declared by annotation and runtime type matches),
     * or return adapter searched by runtime type
     *
     * @param propertyRuntimeType runtime type not null
     * @param propertyModel model nullable
     * @return adapter info if present
     */
    public Optional<AdapterBinding> getAdapterBinding(Type propertyRuntimeType, PropertyModel propertyModel) {
        if (propertyModel != null && propertyModel.getClassModel().getRawType() == TypeWrapper.class) {
            return Optional.empty();
        }
        if (propertyModel == null || propertyModel.getCustomization().getAdapterBinding() == null) {
            return getComponentInfo(propertyRuntimeType, ComponentBindings::getAdapterInfo);
        }
        return getComponentBinding(propertyRuntimeType, propertyModel.getCustomization().getAdapterBinding());
    }

    private <T extends AbstractComponentBinding> Optional<T> getComponentBinding(Type propertyRuntimeType, T componentBinding) {
        //need runtime check, ParameterizedType property may have generic adapter assigned which is not compatible
        //for given runtime type
        if (matches(propertyRuntimeType, componentBinding.getBindingType())) {
            return Optional.of(componentBinding);
        }
        return Optional.empty();
    }

    private <T extends AbstractComponentBinding> Optional<T> getComponentInfo(Type runtimeType, ComponentSupplier<T> supplier) {
        for (ComponentBindings componentBindings : userComponents.values()) {
            final T component = supplier.getComponent(componentBindings);
            if (component != null && matches(runtimeType, componentBindings.getBindingType())) {
                return Optional.of(component);
            }
        }
        return Optional.empty();
    }

    private boolean matches(Type runtimeType, Type componentBindingType) {
        if (componentBindingType.equals(runtimeType)) {
            return true;
        }
        if (componentBindingType instanceof Class && runtimeType instanceof Class) {
            //for polymorphic adapters
            return ((Class<?>) componentBindingType).isAssignableFrom((Class) runtimeType);
        }
        return ReflectionUtils.getRawType(runtimeType) == ReflectionUtils.getRawType(componentBindingType) &&
                runtimeType instanceof ParameterizedType && componentBindingType instanceof ParameterizedType &&
                matchTypeArguments((ParameterizedType) runtimeType, (ParameterizedType) componentBindingType);
    }

    /**
     * If runtimeType to adapt is a ParametrizedType, check all type args to match against adapter args.
     */
    private boolean matchTypeArguments(ParameterizedType requiredType, ParameterizedType componentBound) {
        final Type[] requiredTypeArguments = requiredType.getActualTypeArguments();
        final Type[] adapterBoundTypeArguments = componentBound.getActualTypeArguments();
        if (requiredTypeArguments.length != adapterBoundTypeArguments.length) {
            return false;
        }
        for(int i = 0; i< requiredTypeArguments.length; i++) {
            Type adapterTypeArgument = adapterBoundTypeArguments[i];
            if (!requiredTypeArguments[i].equals(adapterTypeArgument)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Introspect adapter generic information and put resolved types into metadata wrapper.
     *
     * @param adapterClass class of an adapter
     * @param instanceSupplier adapter instance
     * @return introspected info with resolved typevar types.
     */
    AdapterBinding introspectAdapterBinding(Class<? extends JsonbAdapter> adapterClass, Supplier<JsonbAdapter<?,?>> instanceSupplier) {
        final ParameterizedType adapterRuntimeType = ReflectionUtils.findParameterizedType(adapterClass, JsonbAdapter.class);
        final Type[] adapterTypeArguments = adapterRuntimeType.getActualTypeArguments();
        Type adaptFromType = resolveTypeArg(adapterTypeArguments[0], adapterClass);
        Type adaptToType = resolveTypeArg(adapterTypeArguments[1], adapterClass);
        final ComponentBindings componentBindings = getBindingInfo(adaptFromType);
        if (componentBindings.getAdapterInfo() != null && componentBindings.getAdapterInfo().getAdapter().getClass().equals(adapterClass)) {
            return componentBindings.getAdapterInfo();
        }
        JsonbAdapter<?,?> instance = instanceSupplier.get();
        final AdapterBinding adapterInfo = new AdapterBinding(adaptFromType, adaptToType, instance);
        addApapter(adaptFromType, adapterInfo);
        return adapterInfo;
    }

    /**
     * If an instance of deserializerClass is present in context and is bound for same type, return that instance.
     * Otherwise create new instance and set it to context.
     *
     * @param deserializerClass class of deserialzier
     * @param instanceSupplier create or get existing instance
     * @return wrapper used in property models
     */
    DeserializerBinding introspectDeserializerBinding(Class<? extends JsonbDeserializer> deserializerClass, Supplier<JsonbDeserializer> instanceSupplier) {
        final ParameterizedType deserializerRuntimeType = ReflectionUtils.findParameterizedType(deserializerClass, JsonbDeserializer.class);
        Type deserBindingType = resolveTypeArg(deserializerRuntimeType.getActualTypeArguments()[0], deserializerClass.getClass());
        final ComponentBindings componentBindings = getBindingInfo(deserBindingType);
        if (componentBindings.getDeserializer() != null && componentBindings.getDeserializer().getClass().equals(deserializerClass)) {
            return componentBindings.getDeserializer();
        } else {
            final DeserializerBinding deserializerBinding = new DeserializerBinding(deserBindingType, instanceSupplier.get());
            addDeserializer(deserBindingType, deserializerBinding);
            return deserializerBinding;
        }
    }

    /**
     * If an instance of dserializerClass is present in context and is bound for same type, return that instance.
     * Otherwise create new instance and set it to context.
     *
     * @param serializerClass class of deserialzier
     * @param instanceSupplier create or get existing instance
     * @return wrapper used in property models
     */
    SerializerBinding introspectSerialzierBinding(Class<? extends JsonbSerializer> serializerClass, Supplier<JsonbSerializer> instanceSupplier) {
        final ParameterizedType serializerRuntimeType = ReflectionUtils.findParameterizedType(serializerClass, JsonbSerializer.class);
        Type serBindingType = resolveTypeArg(serializerRuntimeType.getActualTypeArguments()[0], serializerClass.getClass());
        final ComponentBindings componentBindings = getBindingInfo(serBindingType);
        if (componentBindings.getSerializer() != null && componentBindings.getSerializer().getClass().equals(serializerClass)) {
            return componentBindings.getSerializer();
        } else {
            final SerializerBinding serializerBinding = new SerializerBinding(serBindingType, instanceSupplier.get());
            addSeserializer(serBindingType, serializerBinding);
            return serializerBinding;
        }

    }


    private Type resolveTypeArg(Type adapterTypeArg, Type adapterType) {
        if(adapterTypeArg instanceof ParameterizedType) {
            return ReflectionUtils.resolveTypeArguments((ParameterizedType) adapterTypeArg, adapterType);
        } else if (adapterTypeArg instanceof TypeVariable) {
            return ReflectionUtils.resolveItemVariableType(new RuntimeTypeHolder(null, adapterType), (TypeVariable<?>) adapterTypeArg);
        } else {
            return adapterTypeArg;
        }
    }
}
