// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.nanuvem.lom.model;

import com.nanuvem.lom.model.Entity;
import com.nanuvem.lom.model.Property;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

privileged aspect Property_Roo_Finder {
    
    /*public static TypedQuery<Property> Property.findPropertysByEntity(Entity entity) {
        if (entity == null) throw new IllegalArgumentException("The entity argument is required");
        EntityManager em = Property.entityManager();
        TypedQuery<Property> q = em.createQuery("SELECT o FROM Property AS o WHERE o.entity = :entity", Property.class);
        q.setParameter("entity", entity);
        return q;
    }*/
    
}