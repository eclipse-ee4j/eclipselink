/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * 		dclarke/tware - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs;

import static org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller.mediaType;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.MapEntryExpression;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jpa.rs.metadata.model.Attribute;
import org.eclipse.persistence.jpa.rs.metadata.model.Descriptor;
import org.eclipse.persistence.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.jpa.rs.metadata.model.LinkTemplate;
import org.eclipse.persistence.jpa.rs.metadata.model.Parameter;
import org.eclipse.persistence.jpa.rs.metadata.model.PersistenceUnit;
import org.eclipse.persistence.jpa.rs.metadata.model.Query;
import org.eclipse.persistence.jpa.rs.metadata.model.SessionBeanCall;
import org.eclipse.persistence.jpa.rs.util.IdHelper;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * JAX-RS application interface JPA-RS
 * 
 * @author dclarke
 * @since EclipseLink 2.4.0
 */
@Singleton
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class Service {

	public static final String RELATIONSHIP_PARTNER = "partner";
    private PersistenceFactory factory;

    public PersistenceFactory getPersistenceFactory() {
        return factory;
    }

   @EJB
    public void setPersistenceFactory(PersistenceFactory factory) {
        this.factory = factory;
    }
   
   @GET
   public Response getContexts(@Context HttpHeaders hh, @Context UriInfo uriInfo) throws JAXBException {
       Set<String> contexts = factory.getPersistenceContextNames();
       Iterator<String> contextIterator = contexts.iterator();
       List<Link> links = new ArrayList<Link>();
       String mediaType = StreamingOutputMarshaller.mediaType(hh.getAcceptableMediaTypes()).toString();
       while (contextIterator.hasNext()){
           String context = contextIterator.next();
           links.add(new Link(context, mediaType, "\"href\": \"" + uriInfo.getBaseUri() + context + "/metadata\""));
       }
       String result = null;
       result = marshallMetadata(links, mediaType);
       return Response.ok(new StreamingOutputMarshaller(null, result, hh.getAcceptableMediaTypes())).build();
   }
   
   
   @POST
   @Produces(MediaType.WILDCARD)
   public Response callSessionBean(@Context HttpHeaders hh, @Context UriInfo ui, InputStream is) throws JAXBException, ClassNotFoundException, NamingException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
       SessionBeanCall call = null;
       call = unmarshallSessionBeanCall(is);

       String jndiName = call.getJndiName();
       javax.naming.Context ctx = new InitialContext();
       Object ans = ctx.lookup(jndiName);  
       if (ans == null){
           JPARSLogger.fine("jpars_could_not_find_session_bean", new Object[]{jndiName});
           return Response.status(Status.NOT_FOUND).build();
       }
           
       PersistenceContext context = null;
       if (call.getContext() != null){
           context = factory.getPersistenceContext(call.getContext());
           if (context == null){
               JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[]{call.getContext()});
               return Response.status(Status.NOT_FOUND).build();
           }
       }
           
       Class[] parameters = new Class[call.getParameters().size()];
       Object[] args = new Object[call.getParameters().size()];
       int i = 0;
       for (Parameter param: call.getParameters()){
           Class parameterClass = null;
           Object parameterValue = null;
           if (context != null){
               parameterClass = context.getClass(param.getTypeName());
           }
           if (parameterClass != null){
               parameterValue = context.unmarshalEntity(param.getTypeName(),  hh.getMediaType(), is);
           } else {
               parameterClass = Thread.currentThread().getContextClassLoader().loadClass(param.getTypeName());
               parameterValue = ConversionManager.getDefaultManager().convertObject(param.getValue(), parameterClass);
           }
           parameters[i] = parameterClass;
           args[i] = parameterValue;
           i++;
       }
       Method method = ans.getClass().getMethod(call.getMethodName(), parameters);
       Object returnValue = method.invoke(ans, args);
       return Response.ok(new StreamingOutputMarshaller(null, returnValue, hh.getAcceptableMediaTypes())).build();
   }
   
    @PUT
    @Path("{context}")
    public Response bootstrap(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @Context HttpHeaders hh, @Context UriInfo uriInfo, InputStream in) throws IOException, MalformedURLException{
        String urlString = getSingleHeader("persistenceXmlURL", hh);

        PersistenceContext persistenceContext = null;
        boolean replace = false;
        List<String> replaceValues = hh.getRequestHeader("replace");
        if (replaceValues != null && replaceValues.size() > 0){
            replace = Boolean.getBoolean(replaceValues.get(0));
        }
        Map<String, Object> properties = new HashMap<String, Object>();
        if (urlString != null){
            URL url = new URL(urlString);
            persistenceContext = factory.bootstrapPersistenceContext(persistenceUnit, url, properties, replace);
        } else {
            persistenceContext = factory.bootstrapPersistenceContext(persistenceUnit, in, properties, replace);
        }
        if (persistenceContext != null){
            persistenceContext.setBaseURI(uriInfo.getBaseUri());
            return Response.status(Status.CREATED).build();
        }
        JPARSLogger.fine("jpars_could_bootstrap_persistence_context", new Object[]{persistenceUnit});
        return Response.status(Status.BAD_REQUEST).build();
    }
    
    @GET
    @Path("{context}/metadata")
    public Response getTypes(@PathParam("context") String persistenceUnit, @Context HttpHeaders hh, @Context UriInfo uriInfo) {
        PersistenceContext app = get(persistenceUnit, uriInfo.getBaseUri());
        if (app == null){
            JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[]{persistenceUnit});
            return Response.status(Status.NOT_FOUND).build();
        } else {
            PersistenceUnit pu = new PersistenceUnit();
            pu.setPersistenceUnitName(persistenceUnit);
            Map<Class, ClassDescriptor> descriptors = app.getJpaSession().getDescriptors();
            String mediaType = StreamingOutputMarshaller.mediaType(hh.getAcceptableMediaTypes()).toString();
            Iterator<Class> contextIterator = descriptors.keySet().iterator();
            while (contextIterator.hasNext()){
                ClassDescriptor descriptor = descriptors.get(contextIterator.next());
                pu.getTypes().add(new Link(descriptor.getAlias(), mediaType, uriInfo.getBaseUri() + persistenceUnit + "/metadata/entity/" + descriptor.getAlias()));
            }           
            String result = null;
            try {
                result = marshallMetadata(pu, mediaType);
            } catch (JAXBException e){
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
            ResponseBuilder rb = Response.ok(new StreamingOutputMarshaller(null , result, hh.getAcceptableMediaTypes()));
            rb.header("Content-Type", MediaType.APPLICATION_JSON);
            return rb.build();
        }
    }
    
    @DELETE
    @Path("{context}")
    public Response removeContext(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @Context HttpHeaders hh, InputStream in){
        factory.closePersistenceContext(persistenceUnit);
        return Response.ok().build();
    }

    @PUT
    @Path("{context}/subscribe/{name}")
    public Response subscribe(@PathParam("context") String persistenceUnit, @PathParam("name") String name, @Context UriInfo ui) {
        PersistenceContext app = get(persistenceUnit, ui.getBaseUri());
        if (app == null){
            JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[]{persistenceUnit});
            return Response.status(Status.NOT_FOUND).build();
        }
        app.subscribeToEventNotification(name);

        return Response.ok().build();
    }
    
    @GET
    @Path("{context}/metadata/entity/{descriptorAlias}")
    public Response getDescriptorMetadata(@PathParam("context") String persistenceUnit, @PathParam("descriptorAlias") String descriptorAlias, @Context HttpHeaders hh, @Context UriInfo uriInfo) {
        PersistenceContext app = get(persistenceUnit, uriInfo.getBaseUri());
        if (app == null){
            JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[]{persistenceUnit});
            return Response.status(Status.NOT_FOUND).build();
        } else {
            ClassDescriptor descriptor = app.getJpaSession().getDescriptorForAlias(descriptorAlias);
            if (descriptor == null){
                return Response.status(Status.NOT_FOUND).build();
            } else {
                String mediaType = StreamingOutputMarshaller.mediaType(hh.getAcceptableMediaTypes()).toString();
                Descriptor returnDescriptor = buildDescriptor(app, persistenceUnit, descriptor, uriInfo.getBaseUri().toString());
                String result = null;
                try {
                    result = marshallMetadata(returnDescriptor, mediaType);
                } catch (JAXBException e){
                    return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                }
                return Response.ok(new StreamingOutputMarshaller(null , result, hh.getAcceptableMediaTypes())).build();
            }
        }
    }
    
    @GET
    @Path("{context}/metadata/query/")
    public Response getQueriesMetadata(@PathParam("context") String persistenceUnit, @Context HttpHeaders hh, @Context UriInfo uriInfo) {
        PersistenceContext app = get(persistenceUnit, uriInfo.getBaseUri());
        if (app == null){
            JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[]{persistenceUnit});
            return Response.status(Status.NOT_FOUND).build();
        } else {
            List<Query> queries = new ArrayList<Query>();
            addQueries(queries, app, null);
            String mediaType = StreamingOutputMarshaller.mediaType(hh.getAcceptableMediaTypes()).toString();
            String result = null;
            try {
                result = marshallMetadata(queries, mediaType);
            } catch (JAXBException e){
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
            return Response.ok(new StreamingOutputMarshaller(null , result, hh.getAcceptableMediaTypes())).build();
        }
    }
    
    @GET
    @Path("{context}/metadata/query/{queryName}")
    public Response getQueryMetadata(@PathParam("context") String persistenceUnit, @PathParam("queryName") String queryName, @Context HttpHeaders hh, @Context UriInfo uriInfo) {
        PersistenceContext app = get(persistenceUnit, uriInfo.getBaseUri());
        if (app == null){
            JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[]{persistenceUnit});
            return Response.status(Status.NOT_FOUND).build();
        } else {
            List<Query> returnQueries = new ArrayList<Query>();
            Map<String, List<DatabaseQuery>> queries = app.getJpaSession().getQueries();
            if (queries.get(queryName) != null){
                for (DatabaseQuery query :queries.get(queryName)){
                    returnQueries.add(getQuery(query, app));
                }
            }
            String mediaType = StreamingOutputMarshaller.mediaType(hh.getAcceptableMediaTypes()).toString();
            String result = null;
            try {
                result = marshallMetadata(returnQueries, mediaType);
            } catch (JAXBException e){
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
            return Response.ok(new StreamingOutputMarshaller(null , result, hh.getAcceptableMediaTypes())).build();
        }
    }
    
    @GET
    @Path("{context}/entity/{type}/{key}")
    public Response find(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("key") String key, @Context HttpHeaders hh, @Context UriInfo ui) {
        PersistenceContext app = get(persistenceUnit, ui.getBaseUri());
        if (app == null || app.getClass(type) == null){
            if (app == null){
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[]{persistenceUnit});
            } else {
                JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[]{type, persistenceUnit});
            }
            return Response.status(Status.NOT_FOUND).build();
        }
        Map<String, String> discriminators = getParameterMap(ui, persistenceUnit);
        
        Object id = IdHelper.buildId(app, type, key, discriminators);

        Object entity = app.find(discriminators, type, id, Service.getHintMap(ui));

        if (entity == null) {
            return Response.status(Status.NOT_FOUND).build();
        } else {
            return Response.ok(new StreamingOutputMarshaller(app, entity, hh.getAcceptableMediaTypes())).build();
        }        
    }
    
    @GET
    @Path("{context}/entity/{type}/{key}/{attribute}")
    public Response findAttribute(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("key") String key, @PathParam("attribute") String attribute, @Context HttpHeaders hh, @Context UriInfo ui) {
        PersistenceContext app = get(persistenceUnit, ui.getBaseUri());
        if (app == null || app.getClass(type) == null){
            if (app == null){
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[]{persistenceUnit});
            } else {
                JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[]{type, persistenceUnit});
            }
            return Response.status(Status.NOT_FOUND).build();
        }
        Map<String, String> discriminators = getParameterMap(ui, persistenceUnit);
        Object id = IdHelper.buildId(app, type, key, discriminators);

        Object entity = app.findAttribute(discriminators, type, id, Service.getHintMap(ui), attribute);

        if (entity == null) {
            return Response.status(Status.NOT_FOUND).build();
        } else {
            return Response.ok(new StreamingOutputMarshaller(app, entity, hh.getAcceptableMediaTypes())).build();
        }        
    }
    
    @POST
    @Path("{context}/entity/{type}/{key}/{attribute}")
    public Response setOrAddAttribute(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("key") String key, @PathParam("attribute") String attribute, @Context HttpHeaders hh, @Context UriInfo ui, InputStream in) {
        PersistenceContext app = get(persistenceUnit, ui.getBaseUri());
        if (app == null || app.getClass(type) == null){
            if (app == null){
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[]{persistenceUnit});
            } else {
                JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[]{type, persistenceUnit});
            }
            return Response.status(Status.NOT_FOUND).build();
        }
        
        Map<String, String> discriminators = getParameterMap(ui, persistenceUnit);
        Object id = IdHelper.buildId(app, type, key, discriminators);

        Object entity = null;
        String partner = (String)Service.getParameterMap(ui, attribute).get(RELATIONSHIP_PARTNER);
        try{
            ClassDescriptor descriptor = app.getDescriptor(type);
            DatabaseMapping mapping = (DatabaseMapping)descriptor.getMappingForAttributeName(attribute);
            if (!mapping.isForeignReferenceMapping()){
                return Response.status(Status.NOT_FOUND).build();
            }
            entity = app.unmarshalEntity(((ForeignReferenceMapping)mapping).getReferenceDescriptor().getAlias(), mediaType(hh.getAcceptableMediaTypes()), in);
        } catch (JAXBException e){
            return Response.status(Status.BAD_REQUEST).build();
        }
        
        Object result = app.updateOrAddAttribute(getParameterMap(ui, persistenceUnit), type, id, Service.getHintMap(ui), attribute, entity, partner);

        if (result == null) {
            return Response.status(Status.NOT_FOUND).build();
        } else {
            return Response.ok(new StreamingOutputMarshaller(app, result, hh.getAcceptableMediaTypes())).build();
        }
    }
    
    @DELETE
    @Path("{context}/entity/{type}/{key}/{attribute}")
    public Response removeAttribute(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("key") String key, @PathParam("attribute") String attribute, @Context HttpHeaders hh, @Context UriInfo ui, InputStream in) {
        PersistenceContext app = get(persistenceUnit, ui.getBaseUri());
        if (app == null || app.getClass(type) == null){
            if (app == null){
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[]{persistenceUnit});
            } else {
                JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[]{type, persistenceUnit});
            }
            return Response.status(Status.NOT_FOUND).build();
        }
        
        Map<String, String> discriminators = getParameterMap(ui, persistenceUnit);
        Object id = IdHelper.buildId(app, type, key, discriminators);

        Object entity = null;
        String partner = (String)Service.getParameterMap(ui, attribute).get(RELATIONSHIP_PARTNER);
        try{
            ClassDescriptor descriptor = app.getDescriptor(type);
            DatabaseMapping mapping = (DatabaseMapping)descriptor.getMappingForAttributeName(attribute);
            if (!mapping.isForeignReferenceMapping()){
                return Response.status(Status.NOT_FOUND).build();
            }
            entity = app.unmarshalEntity(((ForeignReferenceMapping)mapping).getReferenceDescriptor().getAlias(), mediaType(hh.getAcceptableMediaTypes()), in);
        } catch (JAXBException e){
            return Response.status(Status.BAD_REQUEST).build();
        }
        
        Object result = app.removeAttribute(getParameterMap(ui, persistenceUnit), type, id, Service.getHintMap(ui), attribute, entity, partner);
        if (result == null) {
            return Response.status(Status.NOT_FOUND).build();
        } else {
            return Response.ok(new StreamingOutputMarshaller(app, result, hh.getAcceptableMediaTypes())).build();
        }     
    }

    @PUT
    @Path("{context}/entity/{type}")
    public Response create(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @Context HttpHeaders hh, @Context UriInfo uriInfo, InputStream in) throws JAXBException {
        PersistenceContext app = get(persistenceUnit, uriInfo.getBaseUri());
        ClassDescriptor descriptor = app.getDescriptor(type);
        if (app == null || descriptor == null){
            if (app == null){
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[]{persistenceUnit});
            } else {
                JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[]{type, persistenceUnit});
            }
            return Response.status(Status.NOT_FOUND).build();
        }
        Object entity = null;
        try{
            entity = app.unmarshalEntity(type, mediaType(hh.getAcceptableMediaTypes()), in);
        } catch (JAXBException e){
            e.printStackTrace();
            return Response.status(Status.BAD_REQUEST).build();
        }

        // maintain itempotence on PUT by disallowing sequencing
        AbstractDirectMapping sequenceMapping = descriptor.getObjectBuilder().getSequenceMapping();
        if (sequenceMapping != null){
            Object value = sequenceMapping.getAttributeAccessor().getAttributeValueFromObject(entity);

            if (descriptor.getObjectBuilder().isPrimaryKeyComponentInvalid(value, descriptor.getPrimaryKeyFields().indexOf(descriptor.getSequenceNumberField())) || descriptor.getSequence().shouldAlwaysOverrideExistingValue()){
                return Response.status(Status.BAD_REQUEST).build();
            }
        }

        app.create(getParameterMap(uriInfo, persistenceUnit), entity);
        ResponseBuilder rb = Response.status(Status.CREATED);
        rb.entity(new StreamingOutputMarshaller(app, entity, hh.getAcceptableMediaTypes()));
        return rb.build();
    }

    @POST
    @Path("{context}/entity/{type}")
    public Response update(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @Context HttpHeaders hh, @Context UriInfo uriInfo, InputStream in) {
        PersistenceContext app = get(persistenceUnit, uriInfo.getBaseUri());
        if (app == null || app.getClass(type) == null){
            if (app == null){
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[]{persistenceUnit});
            } else {
                JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[]{type, persistenceUnit});
            }
            return Response.status(Status.NOT_FOUND).build();
         }
        MediaType contentType = mediaType(hh.getRequestHeader(HttpHeaders.CONTENT_TYPE)); 
        Object entity = null;
        try {
            entity = app.unmarshalEntity(type, contentType, in);
        } catch (JAXBException e){
            return Response.status(Status.BAD_REQUEST).build();
        }
        entity = app.merge(getParameterMap(uriInfo, persistenceUnit), entity);
        return Response.ok(new StreamingOutputMarshaller(app, entity, hh.getAcceptableMediaTypes())).build();
    }

    @DELETE
    @Path("{context}/entity/{type}/{key}")
    public Response delete(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("key") String key, @Context HttpHeaders hh, @Context UriInfo ui) {
        PersistenceContext app = get(persistenceUnit, ui.getBaseUri());
        if (app == null || app.getClass(type) == null){
            if (app == null){
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[]{persistenceUnit});
            } else {
                JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[]{type, persistenceUnit});
            }
            return Response.status(Status.NOT_FOUND).build();
        }
        Map<String, String> discriminators = getParameterMap(ui, persistenceUnit);
        Object id = IdHelper.buildId(app, type, key, discriminators);
        app.delete(discriminators, type, id);
        return Response.ok().build();
    }
    
    @GET
    @Path("{context}/query/{name}")
    public Response namedQuery(@PathParam("context") String persistenceUnit, @PathParam("name") String name, @Context HttpHeaders hh, @Context UriInfo ui) {
        PersistenceContext app = get(persistenceUnit, ui.getBaseUri());
        if (app == null){
            JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[]{persistenceUnit});
            return Response.status(Status.NOT_FOUND).build();
        }
        Object result = app.query(getParameterMap(ui, persistenceUnit), name, Service.getParameterMap(ui, name), Service.getHintMap(ui), false, false);
        return Response.ok(new StreamingOutputMarshaller(app, result, hh.getAcceptableMediaTypes())).build();
    }
    
    @POST
    @Path("{context}/query/{name}")
    @Produces({ MediaType.APPLICATION_OCTET_STREAM})
    public Response namedQueryUpdate(@PathParam("context") String persistenceUnit, @PathParam("name") String name, @Context HttpHeaders hh, @Context UriInfo ui) {
        PersistenceContext app = get(persistenceUnit, ui.getBaseUri());
        if (app == null){
            JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[]{persistenceUnit});
            return Response.status(Status.NOT_FOUND).build();
        }
        Object result = app.query(getParameterMap(ui, persistenceUnit), name, Service.getParameterMap(ui, name), Service.getHintMap(ui), false, true);
        return Response.ok(new StreamingOutputMarshaller(app, result.toString(), hh.getAcceptableMediaTypes())).build();
    }
    
    @GET
    @Path("{context}/singleResultQuery/{name}")
    @Produces(MediaType.WILDCARD)
    public Response namedQuerySingleResult(@PathParam("context") String persistenceUnit, @PathParam("name") String name, @Context HttpHeaders hh, @Context UriInfo ui) {
        PersistenceContext app = get(persistenceUnit, ui.getBaseUri());
        if (app == null){
            JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[]{persistenceUnit});
            return Response.status(Status.NOT_FOUND).build();
        }
        Object result = app.query(getParameterMap(ui, persistenceUnit), name, Service.getParameterMap(ui, name), Service.getHintMap(ui), true, false);
        return Response.ok(new StreamingOutputMarshaller(app, result, hh.getAcceptableMediaTypes())).build();
    }
    
    protected Descriptor buildDescriptor(PersistenceContext app, String persistenceUnit, ClassDescriptor descriptor, String baseUri){
        Descriptor returnDescriptor = new Descriptor();
        returnDescriptor.setName(descriptor.getAlias());
        returnDescriptor.setType(descriptor.getJavaClassName());
        returnDescriptor.getLinkTemplates().add(new LinkTemplate("find", "get", baseUri + persistenceUnit + "/entity/" + descriptor.getAlias() + "/{primaryKey}"));
        returnDescriptor.getLinkTemplates().add(new LinkTemplate("persist", "put", baseUri + persistenceUnit + "/entity/" + descriptor.getAlias()));
        returnDescriptor.getLinkTemplates().add(new LinkTemplate("update", "post", baseUri + persistenceUnit + "/entity/" + descriptor.getAlias()));
        returnDescriptor.getLinkTemplates().add(new LinkTemplate("delete", "delete", baseUri + persistenceUnit + "/entity/" + descriptor.getAlias() + "/{primaryKey}"));
        
        if (!descriptor.getMappings().isEmpty()){
            Iterator<DatabaseMapping> mappingIterator = descriptor.getMappings().iterator();
            while (mappingIterator.hasNext()){
                DatabaseMapping mapping = mappingIterator.next();
                addMapping(returnDescriptor, mapping);
            }
        }
        addQueries(returnDescriptor.getQueries(), app, descriptor.getJavaClassName());
        return returnDescriptor;
    }
    
    protected void addMapping(Descriptor descriptor, DatabaseMapping mapping){
        String target = null;
        if (mapping.isCollectionMapping()){
            CollectionMapping collectionMapping = (CollectionMapping)mapping;
            String collectionType = collectionMapping.getContainerPolicy().getContainerClassName();
            if (collectionMapping.getContainerPolicy().isMapPolicy()){
                String mapKeyType = ((MapContainerPolicy)collectionMapping.getContainerPolicy()).getKeyType().toString();
                target = collectionType + "<" +  mapKeyType + ", " + collectionMapping.getReferenceClassName() + ">";
            } else {
                target = collectionType + "<" + collectionMapping.getReferenceClassName() + ">";
            }
        } else if (mapping.isForeignReferenceMapping()){
            target = ((ForeignReferenceMapping)mapping).getReferenceClass().getName();
        } else {
            target = mapping.getAttributeClassification().getName();
        }
        descriptor.getAttributes().add(new Attribute(mapping.getAttributeName(), target));
    }
    
    protected void addQueries(List<Query>  queryList, PersistenceContext app, String javaClassName){
        Map<String, List<DatabaseQuery>> queries = app.getJpaSession().getQueries();
        List<DatabaseQuery> returnQueries = new ArrayList<DatabaseQuery>();
        for (String key: queries.keySet()){
            List<DatabaseQuery> keyQueries = queries.get(key);
            Iterator<DatabaseQuery> queryIterator = keyQueries.iterator();
            while (queryIterator.hasNext()){
                DatabaseQuery query= queryIterator.next();
                if (javaClassName == null || (query.getReferenceClassName() != null && query.getReferenceClassName().equals(javaClassName))){
                    returnQueries.add(query);
                }
            }
         }
        Iterator<DatabaseQuery> queryIterator = returnQueries.iterator();
        while(queryIterator.hasNext()){
            queryList.add(getQuery(queryIterator.next(), app));            
        }
    }
    
    protected Query getQuery(DatabaseQuery query, PersistenceContext app){
        String method = query.isReadQuery() ? "get" : "post";
        String jpql = query.getJPQLString() == null? "" : query.getJPQLString();
        StringBuffer parameterString = new StringBuffer();
        Iterator<String> argumentsIterator = query.getArguments().iterator();
        while (argumentsIterator.hasNext()){
            String argument = argumentsIterator.next();
            parameterString.append(";");
            parameterString.append(argument + "={" + argument + "}");
        }
        Query returnQuery = new Query(query.getName(), jpql, new LinkTemplate("execute", method, app.getBaseURI() + app.getName() + "/query/" + query.getName() + parameterString));
        if (query.isReportQuery()){
            query.checkPrepare(app.getJpaSession(), new DatabaseRecord());
            for (ReportItem item: ((ReportQuery)query).getItems()){
                if (item.getMapping() != null) {
                     if (item.getAttributeExpression() != null && item.getAttributeExpression().isMapEntryExpression()){
                        if (((MapEntryExpression)item.getAttributeExpression()).shouldReturnMapEntry()){
                            returnQuery.getReturnTypes().add(Map.Entry.class.getName());
                        } else {
                            returnQuery.getReturnTypes().add(((Class)((CollectionMapping)item.getMapping()).getContainerPolicy().getKeyType()).getName());
                        }
                    } else {
                       returnQuery.getReturnTypes().add(item.getMapping().getAttributeClassification().getName());
                    }
                } else if (item.getResultType() != null) {
                    returnQuery.getReturnTypes().add(item.getResultType().getName());
                } else if (item.getDescriptor() != null) {
                    returnQuery.getReturnTypes().add(item.getDescriptor().getJavaClass().getName());
                } else if (item.getAttributeExpression() != null && item.getAttributeExpression().isConstantExpression()){
                    returnQuery.getReturnTypes().add(((ConstantExpression)item.getAttributeExpression()).getValue().getClass().getName());
                } else {
                    // Use Object.class by default.
                    returnQuery.getReturnTypes().add(ClassConstants.OBJECT.getName());
                }
            }
        } else {
            returnQuery.getReturnTypes().add(query.getReferenceClassName() == null ? "" : query.getReferenceClassName());
        }
        return  returnQuery;
    }

    
    @PreDestroy
    public void close() {
        factory.close();
    }

    private PersistenceContext get(String persistenceUnit, URI defaultURI) {
        PersistenceContext app = getPersistenceFactory().getPersistenceContext(persistenceUnit);
        if (app == null){
            try{
                DynamicClassLoader dcl = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
                Map<String, Object> properties = new HashMap<String, Object>();
                properties.put(PersistenceUnitProperties.CLASSLOADER, dcl);
                
                EntityManagerFactory factory = null;//(EntityManagerFactory) Naming.getInitialContext().lookup("java:comp/env/persistence/pu1")
                if (factory == null){
                    factory = Persistence.createEntityManagerFactory(persistenceUnit, properties);
                }
                if (factory != null){
                    app = getPersistenceFactory().bootstrapPersistenceContext(persistenceUnit, factory, defaultURI, true);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            if (app.getBaseURI() == null){
                app.setBaseURI(defaultURI);
            }
        }
        
        if (app == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return app;
    }
    
    private static Map<String, Object> getHintMap(UriInfo info){
        Map<String, Object> hints = new HashMap<String, Object>();
         for(String key :  info.getQueryParameters().keySet()) { 
            hints.put(key, info.getQueryParameters().getFirst(key));  
        }
        return hints;
    }

    /**
     * This method has been temporarily added to allow processing of either query or matrix parameters
     * When the final protocol is worked out, it should be removed or altered.
     * 
     * Here we check for query parameters and if they don't exist, we get the matrix parameters.
     * @param info
     * @return
     */
    private static Map<String, String> getParameterMap(UriInfo info, String segment){
        Map<String, String> parameters = new HashMap<String, String>();
        for (PathSegment pathSegment: info.getPathSegments()){
            if (pathSegment.getPath() != null && pathSegment.getPath().equals(segment)){
                for(Entry<String, List<String>> entry : pathSegment.getMatrixParameters().entrySet()) { 
                    parameters.put(entry.getKey(), entry.getValue().get(0)); 
                }
                return parameters;
            }
        }
        return parameters;
    }
    
    private String getSingleHeader(String parameterName, HttpHeaders hh){
        List<String> params = hh.getRequestHeader(parameterName);
        if (params == null || params.isEmpty()) {
            return null;
        }
        if (params.size() != 1) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        return params.get(0);
    }

    
    protected String marshallMetadata(Object metadata, String mediaType) throws JAXBException {
        Class[] jaxbClasses = new Class[]{Link.class, Attribute.class, Descriptor.class, LinkTemplate.class, PersistenceUnit.class, Query.class};
        JAXBContext context = (JAXBContext)JAXBContextFactory.createContext(jaxbClasses, null);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, Boolean.FALSE);
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, mediaType);
        StringWriter writer = new StringWriter();
        marshaller.marshal(metadata, writer);
        return writer.toString();
    }
    
    protected SessionBeanCall unmarshallSessionBeanCall(InputStream data) throws JAXBException {
        Class[] jaxbClasses = new Class[]{SessionBeanCall.class};
        JAXBContext context = (JAXBContext)JAXBContextFactory.createContext(jaxbClasses, null);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, Boolean.FALSE);
        unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        StreamSource ss = new StreamSource(data);
        return unmarshaller.unmarshal(ss, SessionBeanCall.class).getValue();
    }

}
