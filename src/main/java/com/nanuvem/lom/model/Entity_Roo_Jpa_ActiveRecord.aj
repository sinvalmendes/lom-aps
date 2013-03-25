// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.nanuvem.lom.model;

import com.nanuvem.lom.model.Entity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Entity_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager Entity.entityManager;
    
    public static final EntityManager Entity.entityManager() {
        EntityManager em = new Entity().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Entity.countEntitys() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Entity o", Long.class).getSingleResult();
    }
    
    public static List<Entity> Entity.findAllEntitys() {
        return entityManager().createQuery("SELECT o FROM Entity o", Entity.class).getResultList();
    }
    
    /*public static Entity Entity.findEntity(Long id) {
        if (id == null) return null;
        return entityManager().find(Entity.class, id);
    }*/
    
    public static List<Entity> Entity.findEntityEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Entity o", Entity.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void Entity.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Entity attached = Entity.findEntity(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Entity.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Entity.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Entity Entity.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Entity merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
