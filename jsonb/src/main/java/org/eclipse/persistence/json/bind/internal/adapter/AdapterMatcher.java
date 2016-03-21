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

package org.eclipse.persistence.json.bind.internal.adapter;

import org.eclipse.persistence.json.bind.internal.JsonbContext;
import org.eclipse.persistence.json.bind.internal.ReflectionUtils;
import org.eclipse.persistence.json.bind.internal.VariableTypeInheritanceSearch;
import org.eclipse.persistence.json.bind.model.PropertyModel;

import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Searches for a registered adapter for a given type.
 *
 * @author Roman Grigoriadi
 */
public class AdapterMatcher {

    private static final AdapterMatcher instance = new AdapterMatcher();

    private AdapterMatcher() {
    }

    public static AdapterMatcher getInstance() {
        return instance;
    }

    /**
     * Find an adapter for a given runtime type.
     *
     * @param runtimeType type to adapt
     * @return adapter info with adapter
     */
    public Optional<JsonbAdapterInfo> getAdapterInfo(Type runtimeType) {
        for (JsonbAdapterInfo info : JsonbContext.getInstance().getAdapters()) {
            if (matches(runtimeType, info)) {
                return Optional.of(info);
            }
        }

        return Optional.empty();
    }

    /**
     * Get adapter from property model (if declared by annotation and runtime type matches),
     * or return adapter searched by runtime type
     *
     * @param propertyRuntimeType runtime type not null
     * @param propertyModel model nullable
     * @return adapter info if present
     */
    public Optional<JsonbAdapterInfo> getAdapterInfo(Type propertyRuntimeType, PropertyModel propertyModel) {
        Optional<JsonbAdapterInfo> propertyAdapterInfo = propertyModel != null ?
                propertyModel.getCustomization().getAdapterInfo() : Optional.empty();
        if (propertyAdapterInfo.filter(info -> matches(propertyRuntimeType, info)).isPresent()) {
            return propertyAdapterInfo;
        }
        return getAdapterInfo(propertyRuntimeType);
    }

    private boolean matches(Type runtimeType, JsonbAdapterInfo info) {
        if (info.getFromType().equals(runtimeType)) {
            return true;
        }
        return ReflectionUtils.getRawType(runtimeType) == ReflectionUtils.getRawType(info.getFromType()) &&
                runtimeType instanceof ParameterizedType && info.getFromType() instanceof ParameterizedType &&
                matchTypeArguments((ParameterizedType) runtimeType, (ParameterizedType) info.getFromType(), info.getAdapter().getClass());
    }

    /**
     * If runtimeType to adapt is a ParametrizedType, check all type args to match against adapter args.
     */
    private boolean matchTypeArguments(ParameterizedType requiredType, ParameterizedType adapterBound, Class<? extends JsonbAdapter> adapterClass) {
        final Type[] requiredTypeArguments = requiredType.getActualTypeArguments();
        final Type[] adapterBoundTypeArguments = adapterBound.getActualTypeArguments();
        if (requiredTypeArguments.length != adapterBoundTypeArguments.length) {
            return false;
        }
        for(int i = 0; i< requiredTypeArguments.length; i++) {
            Type adapterTypeArgument = adapterBoundTypeArguments[i];
            if (adapterTypeArgument instanceof TypeVariable) {
                adapterTypeArgument = new VariableTypeInheritanceSearch().searchParametrizedType(adapterClass, (TypeVariable<?>) adapterTypeArgument);
            }
            if (!requiredTypeArguments[i].equals(adapterTypeArgument)) {
                return false;
            }
        }
        return true;
    }


    /**
     * For generic adapters like:
     * <p>
     *     {@code
     *     interface ContainerAdapter<T> extends JsonbAdapter<Box<T>, Crate<T>>...;
     *     class IntegerBoxToCrateAdapter implements ContainerAdapter<Integer>...;
     *     }
     * </p>
     * We need to find a JsonbAdapter class which will hold basic generic type arguments,
     * and resolve them if they are TypeVariables from there.
     *
     * @param adapter adapter to resolve runtime type from
     * @return type of JsonbAdapter
     */
    private ParameterizedType getAdapterRuntimeType(JsonbAdapter<?, ?> adapter) {
        Class adapterClass = adapter.getClass();
        while (adapterClass != Object.class) {
            for (Type adapterInterface : adapterClass.getGenericInterfaces()) {
                if (adapterInterface instanceof ParameterizedType &&
                        ((ParameterizedType) adapterInterface).getRawType().equals(JsonbAdapter.class)) {
                    return (ParameterizedType) adapterInterface;
                }
            }
            adapterClass = adapterClass.getSuperclass();
        }
        throw new JsonbException(String.format("Adapter: %s is not a type of JsonbAdapter", adapter.getClass()));
    }

    /**
     * Introspects generic type information for adapters for "adapt from" and "adapt to" types, and store resolved
     * into JsonbAdapterInfo container.
     *
     * @param adapters adapter to introspect
     * @return List of introspected info objects
     */
    public List<JsonbAdapterInfo> parseRegisteredAddapters(JsonbAdapter<?,?>[] adapters) {
        if (adapters == null || adapters.length == 0) {
            return new ArrayList<>();
        }
        List<JsonbAdapterInfo> adapterInfos = new ArrayList<>();
        for (JsonbAdapter<?, ?> adapter : adapters) {
            final JsonbAdapterInfo adapterInfo = introspectAdapterInfo(adapter);
            adapterInfos.add(adapterInfo);
        }
        return adapterInfos;
    }

    /**
     * Introspect adapter generic information and put resolved types into metadata wrapper.
     *
     * @param adapter adapter to introspect not null
     * @return introspected info with resolved typevar types.
     */
    public JsonbAdapterInfo introspectAdapterInfo(JsonbAdapter<?, ?> adapter) {
        Objects.requireNonNull(adapter);
        final ParameterizedType adapterRuntimeType = getAdapterRuntimeType(adapter);
        final Type[] adapterTypeArguments = adapterRuntimeType.getActualTypeArguments();
        Type adaptFromType = resolveAdapterTypeArg(adapterTypeArguments[0], adapter.getClass());
        Type adaptToType = resolveAdapterTypeArg(adapterTypeArguments[1], adapter.getClass());
        return new JsonbAdapterInfo(adaptFromType, adaptToType, adapter);
    }

    private Type resolveAdapterTypeArg(Type adapterTypeArg, Type adapterType) {
        return adapterTypeArg instanceof ParameterizedType ?
                ReflectionUtils.resolveTypeArguments((ParameterizedType) adapterTypeArg, adapterType) :
                adapterTypeArg;
    }
}
