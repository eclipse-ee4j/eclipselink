package org.eclipse.persistence.testing.models.optimisticlocking;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class ListHolder {

    private int id;
    private String description;
    private int version;
    private List<ListItem> items;
    
    public ListHolder(){
        items = new ArrayList<ListItem>();
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
    public List<ListItem> getItems() {
        return items;
    }
    public void setItems(List<ListItem> items) {
        this.items = items;
    }
    
    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(ListHolder.class);
        Vector vector = new Vector();
        vector.addElement("OL_HOLDER");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("OL_HOLDER.ID");

        // SECTION: PROPERTIES
        descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.FullIdentityMap.class);
        descriptor.setSequenceNumberName("OL_HOLDER");
        descriptor.setSequenceNumberFieldName("ID");
        descriptor.setExistenceChecking("Check cache");
        descriptor.setIdentityMapSize(100);
        
        VersionLockingPolicy lockingPolicy = new VersionLockingPolicy();
        lockingPolicy.setWriteLockFieldName("OL_HOLDER.VERSION");
        lockingPolicy.setIsStoredInCache(false);
        descriptor.setOptimisticLockingPolicy(lockingPolicy);

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping.setAttributeName("id");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("OL_HOLDER.ID");
        descriptor.addMapping(directtofieldmapping);
        
        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping1 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("version");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("OL_HOLDER.VERSION");
        descriptor.addMapping(directtofieldmapping1);
        
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping2 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping2.setAttributeName("description");
        directtofieldmapping2.setIsReadOnly(false);
        directtofieldmapping2.setFieldName("OL_HOLDER.DESCR");
        descriptor.addMapping(directtofieldmapping2);
        
        // SECTION: ONETOMANYMAPPING
        org.eclipse.persistence.mappings.OneToManyMapping onetomanymapping = new org.eclipse.persistence.mappings.OneToManyMapping();
        onetomanymapping.setAttributeName("items");
        onetomanymapping.setIsReadOnly(false);
        onetomanymapping.useTransparentList();
        onetomanymapping.setReferenceClass(ListItem.class);
        onetomanymapping.addTargetForeignKeyFieldName("OL_ITEM.HOLDER_ID", "OL_HOLDER.ID");
        descriptor.addMapping(onetomanymapping);

        return descriptor;
    }
    
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("OL_HOLDER");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        
        definition.addField("VERSION", Integer.class, 30);
        definition.addField("DESCR", java.lang.String.class);

        return definition;
    }
}
