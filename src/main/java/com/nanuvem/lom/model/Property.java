package com.nanuvem.lom.model;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Property {

    @NotNull
    private String name = "";

    private String configuration = "";

    @ManyToOne
    private Entity entity;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PropertyType type;
        
    @Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        
        if (this.isValidName() == false) {
            throw new ValidationException("Invalid characters in name");
        }
        
        if (this.isValidConfiguration() == false) {
            throw new ValidationException("Invalid configuration");
        }
        
        Entity entity = Entity.findEntity(this.entity.getId());
        Set<Property> properties = entity.getProperties();
        for(Property p : properties){
        	if (p.getName().equalsIgnoreCase(this.name)){
        		throw new ValidationException("Property with same name and same entity already exists!");
        	}
        }
        properties.add(this);
        
        this.entityManager.persist(this);
    }
    
    public boolean validateNameAndNamespace() {
        if (this.isValidName() && this.isValidConfiguration()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isValidName() {
        if (Pattern.matches("[a-zA-Z0-9 _]+", this.name)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isValidConfiguration() {
        if (Pattern.matches("[a-zA-Z0-9 _]+", this.configuration) 
        		|| this.configuration.equals("")) {
            return true;
        } else {
            return false;
        }
    }
    
}
