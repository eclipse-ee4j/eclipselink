package org.eclipse.persistence.testing.models.collections.map;

import org.eclipse.persistence.tools.schemaframework.PopulationManager;

public class MapPopulator {

    protected PopulationManager populationManager;
    
    public MapPopulator(){
        this.populationManager = PopulationManager.getDefaultManager();
    }
    
    /**
     * Call all of the example methods in this system to guarantee that all our objects
     * are registered in the population manager
     */
    public void buildExamples() {
        // First ensure that no preivous examples are hanging around.
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(EntityEntity1MMapHolder.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(EntityDirectMapHolder.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(EntityAggregateMapHolder.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(DirectEntityU1MMapHolder.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(DirectEntityMapHolder.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(DirectEntity1MMapHolder.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(DirectDirectMapHolder.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(DirectAggregateMapHolder.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(AggregateEntityU1MMapHolder.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(AggregateEntityMapHolder.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(AggregateEntity1MMapHolder.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(AggregateDirectMapHolder.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(AggregateAggregateMapHolder.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(EntityEntityMapHolder.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(EntityEntityU1MMapHolder.class);

        registerObject(getEntityEntity1MMapHolder(), "0001");
        registerObject(getEntityDirectMapHolder(), "0002");
        registerObject(getEntityAggregateMapHolder(), "0003");
        registerObject(getDirectEntityU1MMapHolder(), "0004");
        registerObject(getDirectEntityMapHolder(), "0005");
        registerObject(getDirectEntity1MMapHolder(), "0006");
        registerObject(getDirectDirectMapHolder(), "0007");
        registerObject(getDirectAggregateMapHolder(), "0008");
        registerObject(getAggregateEntityU1MMapHolder(), "0009");
        registerObject(getAggregateEntityMapHolder(), "0010");
        registerObject(getAggregateEntity1MMapHolder(), "0011");
        registerObject(getAggregateDirectMapHolder(), "0012");
        registerObject(getAggregateAggregateMapHolder(), "0013");
        registerObject(getEntityEntityMapHolder(), "00014");
        registerObject(getEntityEntityU1MMapHolder(), "0015");
    }
    
    protected boolean containsObject(Class domainClass, String identifier) {
        return populationManager.containsObject(domainClass, identifier);
    }
    
    protected Object getObject(Class domainClass, String identifier) {
        return populationManager.getObject(domainClass, identifier);
    }
    
    public Object registerObject(Object object, String identifier){
        if (containsObject(object.getClass(), identifier)) {
            return getObject(object.getClass(), identifier);
        }
        return populationManager.registerObject(object, identifier);
    }
    
    public EntityEntityU1MMapHolder getEntityEntityU1MMapHolder(){
        EntityEntityU1MMapHolder holder = new EntityEntityU1MMapHolder();
        EntityMapValue value = getEntityMapValue11();
        EntityMapKey key = getEntityMapKey9();
        holder.addEntityToEntityMapItem(key, value);
        
        EntityMapValue value2 = getEntityMapValue12();
        key = getEntityMapKey10();
        holder.addEntityToEntityMapItem(key, value2);
        return holder;
    }
    
    public EntityEntityMapHolder getEntityEntityMapHolder(){
        EntityEntityMapHolder holder = new EntityEntityMapHolder();
        EntityMapValue value = getEntityMapValue5();
        EntityMapKey key = getEntityMapKey7();
        holder.addEntityToEntityMapItem(key, value);
        
        EntityMapValue value2 = getEntityMapValue6();
        key = getEntityMapKey8();
        holder.addEntityToEntityMapItem(key, value2);
        return holder;
    }
    
    public AggregateAggregateMapHolder getAggregateAggregateMapHolder(){
        AggregateAggregateMapHolder holder = new AggregateAggregateMapHolder();
        AggregateMapKey value = getAggregateMapKey1();
        AggregateMapKey key = getAggregateMapKey1();
        holder.addAggregateToAggregateMapItem(key, value);
        AggregateMapKey value2 = getAggregateMapKey2();
        key = getAggregateMapKey2();
        holder.addAggregateToAggregateMapItem(key, value2);
        return holder;
    }
    
    public AggregateDirectMapHolder getAggregateDirectMapHolder(){
        AggregateDirectMapHolder holder = new AggregateDirectMapHolder();
        AggregateMapKey mapKey = getAggregateMapKey1();
        holder.addAggregateToDirectMapItem(mapKey, new Integer(1));
        AggregateMapKey mapKey2 = getAggregateMapKey2();
        holder.addAggregateToDirectMapItem(mapKey2, new Integer(2));
        return holder;
    }
    
    public AggregateEntity1MMapHolder getAggregateEntity1MMapHolder(){
        AggregateEntity1MMapHolder holder = new AggregateEntity1MMapHolder();
        AEOTMMapValue value = getAEOTMMapValue1();
        value.getHolder().setValue(holder);
        AggregateMapKey key = getAggregateMapKey1();
        holder.addAggregateToEntityMapItem(key, value);

        AEOTMMapValue value2 = getAEOTMMapValue2();
        value2.getHolder().setValue(holder);
        key = getAggregateMapKey2();
        holder.addAggregateToEntityMapItem(key, value2);
        return holder;
    }
    
    public AggregateEntityMapHolder getAggregateEntityMapHolder(){
        AggregateEntityMapHolder holder = new AggregateEntityMapHolder();
        EntityMapValue value = getEntityMapValue9();
        AggregateMapKey key = getAggregateMapKey1();
        holder.addAggregateToEntityMapItem(key, value);

        EntityMapValue value2 = getEntityMapValue10();
        key = getAggregateMapKey2();
        holder.addAggregateToEntityMapItem(key, value2);
        return holder;
    }
    
    public AggregateEntityU1MMapHolder getAggregateEntityU1MMapHolder(){
        AggregateEntityU1MMapHolder holder = new AggregateEntityU1MMapHolder();
        EntityMapValue value = getEntityMapValue7();
        AggregateMapKey key = getAggregateMapKey1();
        holder.addAggregateToEntityMapItem(key, value);

        EntityMapValue value2 = getEntityMapValue8();
        key = getAggregateMapKey2();
        holder.addAggregateToEntityMapItem(key, value2);
        return holder;
    }
    
    public DirectAggregateMapHolder getDirectAggregateMapHolder(){
        DirectAggregateMapHolder holder = new DirectAggregateMapHolder();
        AggregateMapValue value = getAggregateMapValue1();
        holder.addDirectToAggregateMapItem(new Integer(1), value);
        value = getAggregateMapValue2();
        holder.addDirectToAggregateMapItem(new Integer(2), value);
        return holder;
    }
    
    public DirectDirectMapHolder getDirectDirectMapHolder(){
        DirectDirectMapHolder holder = new DirectDirectMapHolder();
        holder.addDirectToDirectMapItem(new Integer(1), new Integer(1));
        holder.addDirectToDirectMapItem(new Integer(2), new Integer(2));
        return holder;
    }
    
    public DirectEntity1MMapHolder getDirectEntity1MMapHolder(){
        DirectEntity1MMapHolder initialHolder = new DirectEntity1MMapHolder();
        DEOTMMapValue value = getDEOTMMapValue1();
        value.getHolder().setValue(initialHolder);
        initialHolder.addDirectToEntityMapItem(new Integer(11), value);
        
        DEOTMMapValue value2 = getDEOTMMapValue2();
        value2.getHolder().setValue(initialHolder);
        initialHolder.addDirectToEntityMapItem(new Integer(22), value2);
        return initialHolder;
    }
    
    public DirectEntityMapHolder getDirectEntityMapHolder(){
        DirectEntityMapHolder holder = new DirectEntityMapHolder();
        EntityMapValue value = getEntityMapValue1();
        holder.addDirectToEntityMapItem(new Integer(11), value);

        EntityMapValue value2 = getEntityMapValue2();
        holder.addDirectToEntityMapItem(new Integer(22), value2);
        return holder;
    }
    
    public DirectEntityU1MMapHolder getDirectEntityU1MMapHolder(){
        DirectEntityU1MMapHolder holder = new DirectEntityU1MMapHolder();
        EntityMapValue value = getEntityMapValue3();
        holder.addDirectToEntityMapItem(new Integer(11), value);

        EntityMapValue value2 = getEntityMapValue4();
        holder.addDirectToEntityMapItem(new Integer(22), value2);
        return holder;
    }

    public EntityAggregateMapHolder getEntityAggregateMapHolder(){
        EntityAggregateMapHolder holder = new EntityAggregateMapHolder();
        AggregateMapValue value = getAggregateMapValue1();
        EntityMapKey key = getEntityMapKey1();
        holder.addEntityToAggregateMapItem(key, value);
        AggregateMapValue value2 = getAggregateMapValue2();
        key = getEntityMapKey2();
        holder.addEntityToAggregateMapItem(key, value2);
        return holder;
    }
    
    public EntityDirectMapHolder getEntityDirectMapHolder(){
        EntityDirectMapHolder holder = new EntityDirectMapHolder();
        EntityMapKey mapKey = getEntityMapKey3();
        holder.addEntityDirectMapItem(mapKey, new Integer(1));
        EntityMapKey mapKey2 = getEntityMapKey4();
        holder.addEntityDirectMapItem(mapKey2, new Integer(2));
        return holder;
    }
    
    public EntityEntity1MMapHolder getEntityEntity1MMapHolder(){
        EntityEntity1MMapHolder holder = new EntityEntity1MMapHolder();
        EEOTMMapValue value = getEEOTMMapValue1();
        value.getHolder().setValue(holder);
        EntityMapKey key = getEntityMapKey5();
        holder.addEntityToEntityMapItem(key, value);
        
        EEOTMMapValue value2 = getEEOTMMapValue2();
        value2.getHolder().setValue(holder);
        key = getEntityMapKey6();
        holder.addEntityToEntityMapItem(key, value2);
        return holder;
    }

    public AEOTMMapValue getAEOTMMapValue1(){
        AEOTMMapValue value = new AEOTMMapValue();
        value.setId(11);
        return value;
    }
    
    public AEOTMMapValue getAEOTMMapValue2(){
        AEOTMMapValue value = new AEOTMMapValue();
        value.setId(22);
        return value;
    }
    
    public AggregateMapKey getAggregateMapKey1(){
        AggregateMapKey key = new AggregateMapKey();
        key.setKey(11);
        return key;
    }
    
    public AggregateMapKey getAggregateMapKey11(){
        AggregateMapKey key = new AggregateMapKey();
        key.setKey(11);
        return key;
    }
    
    public AggregateMapKey getAggregateMapKey2(){
        AggregateMapKey key = new AggregateMapKey();
        key.setKey(22);
        return key;
    }
    
    public AggregateMapKey getAggregateMapKey22(){
        AggregateMapKey key = new AggregateMapKey();
        key.setKey(22);
        return key;
    }
    
    public AggregateMapValue getAggregateMapValue1(){
        AggregateMapValue value = new AggregateMapValue();
        value.setValue(1);
        return value;
    }
    
    public AggregateMapValue getAggregateMapValue2(){
        AggregateMapValue value = new AggregateMapValue();
        value.setValue(2);
        return value;
    }
    
    public DEOTMMapValue getDEOTMMapValue1(){
        DEOTMMapValue value = new DEOTMMapValue();
        value.setId(11);
        return value;
    }
    
    public DEOTMMapValue getDEOTMMapValue2(){
        DEOTMMapValue value = new DEOTMMapValue();
        value.setId(22);
        return value;
    }
    
    public EEOTMMapValue getEEOTMMapValue1(){
        EEOTMMapValue value2 = new EEOTMMapValue();
        value2.setId(11);
        return value2;
    }
    
    public EEOTMMapValue getEEOTMMapValue2(){
        EEOTMMapValue value2 = new EEOTMMapValue();
        value2.setId(22);
        return value2;
    }
    
    public EntityMapKey getEntityMapKey1(){
        EntityMapKey key = new EntityMapKey();
        key.setId(111);
        key.setData("111");
        return key;
    }
    
    public EntityMapKey getEntityMapKey2(){
        EntityMapKey key = new EntityMapKey();
        key.setId(222);
        key.setData("222");
        return key;
    }
    
    public EntityMapKey getEntityMapKey3(){
        EntityMapKey key = new EntityMapKey();
        key.setId(333);
        key.setData("data3");
        return key;
    }
    
    public EntityMapKey getEntityMapKey4(){
        EntityMapKey key = new EntityMapKey();
        key.setId(444);
        key.setData("data4");
        return key;
    }
    
    public EntityMapKey getEntityMapKey5(){
        EntityMapKey key = new EntityMapKey();
        key.setId(555);
        key.setData("data5");
        return key;
    }
    
    public EntityMapKey getEntityMapKey6(){
        EntityMapKey key = new EntityMapKey();
        key.setId(666);
        key.setData("data6");
        return key;
    }
    
    public EntityMapKey getEntityMapKey7(){
        EntityMapKey key = new EntityMapKey();
        key.setId(777);
        key.setData("data7");
        return key;
    }
    public EntityMapKey getEntityMapKey8(){
        EntityMapKey key = new EntityMapKey();
        key.setId(888);
        key.setData("data8");
        return key;
    }
    public EntityMapKey getEntityMapKey9(){
        EntityMapKey key = new EntityMapKey();
        key.setId(999);
        key.setData("data9");
        return key;
    }
    public EntityMapKey getEntityMapKey10(){
        EntityMapKey key = new EntityMapKey();
        key.setId(1000);
        key.setData("data10");
        return key;
    }
    
    public EntityMapValue getEntityMapValue1(){
        EntityMapValue value = new EntityMapValue();
        value.setId(111);
        return value;
    }
    
    public EntityMapValue getEntityMapValue2(){
        EntityMapValue value = new EntityMapValue();
        value.setId(222);
        return value;
    }
    
    public EntityMapValue getEntityMapValue3(){
        EntityMapValue value = new EntityMapValue();
        value.setId(333);
        return value;
    }
    
    public EntityMapValue getEntityMapValue4(){
        EntityMapValue value = new EntityMapValue();
        value.setId(444);
        return value;
    }
    
    public EntityMapValue getEntityMapValue5(){
        EntityMapValue value = new EntityMapValue();
        value.setId(555);
        return value;
    }
    
    public EntityMapValue getEntityMapValue6(){
        EntityMapValue value = new EntityMapValue();
        value.setId(666);
        return value;
    }
    
    public EntityMapValue getEntityMapValue7(){
        EntityMapValue value = new EntityMapValue();
        value.setId(777);
        return value;
    }
    
    public EntityMapValue getEntityMapValue8(){
        EntityMapValue value = new EntityMapValue();
        value.setId(888);
        return value;
    }
    
    public EntityMapValue getEntityMapValue9(){
        EntityMapValue value = new EntityMapValue();
        value.setId(999);
        return value;
    }
    
    public EntityMapValue getEntityMapValue10(){
        EntityMapValue value = new EntityMapValue();
        value.setId(1000);
        return value;
    }
    
    public EntityMapValue getEntityMapValue11(){
        EntityMapValue value = new EntityMapValue();
        value.setId(1001);
        return value;
    }
    
    public EntityMapValue getEntityMapValue12(){
        EntityMapValue value = new EntityMapValue();
        value.setId(1002);
        return value;
    }
}
