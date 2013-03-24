package com.nanuvem.lom.model;

//import org.aspectj.lang.annotation.Before;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import javax.validation.ValidationException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.roo.addon.test.RooIntegrationTest;

@RooIntegrationTest(entity = Entity.class)
public class EntityIntegrationTest {
	private Entity entity;

	private Property property;

	private Entity createEntity(String name, String namespace) {
		Entity entity = new Entity();
		entity.setName(name);
		entity.setNamespace(namespace);
		return entity;
	}

	private Property createProperty(String name, String configuration,
			PropertyType type, Entity entity) {
		Property property = new Property();
		property.setName(name);
		property.setConfiguration(configuration);
		property.setType(type);
		property.setEntity(entity);
		return property;
	}

	@Before
	public void init() {
		entity = new Entity();
	}

	/* CREATE ENTITY */

	@Test
	public void validNameAndNamespace() {
		entity = this.createEntity("name", "namespace");
		entity.persist();
	}

	@Test
	public void withoutNamespace() {
		entity.setName("name");
		entity.persist();
	}

	@Test
	public void twoEntitiesSameNameDifferentNamespaces() {
		entity = this.createEntity("samename", "namespace1");
		entity.persist();

		Entity entity2 = this.createEntity("samename", "namespace2");
		entity2.persist();

		Entity entity_return = Entity.findEntity(entity.getId());
		Assert.assertEquals(entity, entity_return);
	}

	@Test
	public void nameAndNamespaceWithSpaces() {
		entity = this.createEntity("name with spaces", "namespace with spaces");
		entity.persist();
		Assert.assertEquals(entity, Entity.findEntity(entity.getId()));
	}

	@Test(expected = ValidationException.class)
	public void withoutName() {
		entity.setNamespace("has only namespace");
		entity.persist();
	}

	@Test(expected = ValidationException.class)
	public void twoEntitiesWithSameNameInDefaultPackage() {
		entity = this.createEntity("samename", "");
		entity.persist();

		Entity entity2 = this.createEntity("samename", "");
		entity2.persist();
	}

	@Test(expected = ValidationException.class)
	public void twoEntitiesWithSameNameInNonDefaultPackage() {
		entity = this.createEntity("sameName", "sameNamespace");
		entity.persist();

		Entity entity2 = this.createEntity("sameName", "sameNamespace");
		entity2.persist();
	}

	@Test
	public void nameOrNamespaceWithInvalidChar() throws Exception {
		entity.setName("wrongname@#$#@");
		entity.setNamespace("wrongnamespace@@#$%");
		try {
			entity.persist();
			Assert.fail();
		} catch (ValidationException e) {
			List<Entity> allEntitiesList = Entity.findAllEntitys();
			Assert.assertFalse(allEntitiesList.contains(entity));
		}
	}

	@Test
	// (expected=ValidationException.class)
	public void caseInsensitiveName() {
		entity = this.createEntity("NAME", "namespace");
		entity.persist();

		Entity entity2 = this.createEntity("name", "namespace");
		try {
			entity2.persist();
			Assert.fail();
		} catch (ValidationException e) {
			List<Entity> allEntitiesList = Entity.findAllEntitys();
			Assert.assertFalse(allEntitiesList.contains(entity2));
		}
	}

	@Test
	// (expected=ValidationException.class)
	public void caseInsensitiveNamespace() {
		Entity entity_1 = new Entity();
		entity_1.setName("name");
		entity_1.setNamespace("NAMESPACE");
		entity_1.persist();

		Entity entity_2 = new Entity();
		entity_2.setName("name");
		entity_2.setNamespace("namespace");
		try {
			entity_2.persist();
		} catch (ValidationException e) {
			Assert.assertEquals(e.getMessage(),
					"Entity with same name already exists in this namespace!");

		}
	}

	/* LIST ENTITIES */
	@Test
	public void listAllEntities() {
		Entity entity_1 = new Entity();
		entity_1.setName("entity_1");
		entity_1.setNamespace("namespace_entity_1");
		entity_1.persist();

		Entity entity_2 = new Entity();
		entity_2.setName("entity_2");
		entity_2.setNamespace("namespace_entity_2");
		entity_2.persist();

		List<Entity> allEntitiesList = Entity.findAllEntitys();

		Assert.assertEquals(allEntitiesList.get(0).getName(),
				entity_1.getName());
		Assert.assertEquals(allEntitiesList.get(1).getName(),
				entity_2.getName());
		Assert.assertTrue(allEntitiesList.contains(entity_1));
		Assert.assertTrue(allEntitiesList.contains(entity_2));
	}

	@Test
	public void listEntitiesByValidFragmentOfName() {
		String fragmentOfName = "Ca";

		Entity entity_1 = this.createEntity("Carro", "namespace_entity_1");
		entity_1.persist();

		Entity entity_2 = this.createEntity("Car", "namespace_entity_2");
		entity_2.persist();

		Entity entity_3 = this.createEntity("Bike", "namespace_entity_3");
		entity_3.persist();

		TypedQuery<Entity> allEntities = Entity
				.findEntitysByNameLike(fragmentOfName);

		List<Entity> allEntitiesList = allEntities.getResultList();
		Assert.assertTrue(allEntitiesList.contains(entity_1));
		Assert.assertTrue(allEntitiesList.contains(entity_2));
		Assert.assertFalse(allEntitiesList.contains(entity_3));
		Assert.assertEquals(2, allEntitiesList.size());
	}

	@Test
	public void listEntitiesByValidFragmentOfNamespace() {
		String fragmentOfNamespace = "pack";

		Entity entity_1 = this.createEntity("Carro", "packet");
		entity_1.persist();

		Entity entity_2 = this.createEntity("Car", "packet");
		entity_2.persist();

		Entity entity_3 = this.createEntity("Bike", "namespace");
		entity_3.persist();

		TypedQuery<Entity> allEntities = Entity
				.findEntitysByNamespaceLike(fragmentOfNamespace);

		List<Entity> allEntitiesList = allEntities.getResultList();
		Assert.assertTrue(allEntitiesList.contains(entity_1));
		Assert.assertTrue(allEntitiesList.contains(entity_2));
		Assert.assertFalse(allEntitiesList.contains(entity_3));
		Assert.assertEquals(2, allEntitiesList.size());
	}

	@Test
	public void listEntitiesByEmptyName() {
		Entity entity_1 = this.createEntity(" ", "namespace_entity_1");
		entity_1.persist();

		Entity entity_2 = this.createEntity("Entity", "namespace_entity_2");
		entity_2.persist();

		List<Entity> entities = Entity.findEntitiesByEmptyName();

		Assert.assertTrue(entities.contains(entity_1));
		Assert.assertFalse(entities.contains(entity_2));
	}

	@Test
	public void listEntitiesByEmptyNamespace() {
		Entity entity_1 = this.createEntity("name", " ");
		entity_1.persist();

		Entity entity_2 = this.createEntity("Entity", "namespace_entity_2");
		entity_2.persist();

		List<Entity> entities = Entity.findEntitiesByEmptyNamespace();

		Assert.assertTrue(entities.contains(entity_1));
		Assert.assertFalse(entities.contains(entity_2));
	}

	@Test
	public void listEntitiesByNameWithSpace() {
		Entity entity_1 = this.createEntity("Name with spaces", "");
		entity_1.persist();

		Entity entity_2 = this.createEntity("Entity", "");
		entity_2.persist();

		List<Entity> entities = Entity.findEntitiesByNameWithSpace();

		Assert.assertTrue(entities.contains(entity_1));
		Assert.assertFalse(entities.contains(entity_2));
	}

	@Test
	public void listEntitiesByNamespaceWithSpace() {
		Entity entity_1 = this.createEntity("Name", "Namespace with spaces");
		entity_1.persist();

		Entity entity_2 = this.createEntity("Entity", "");
		entity_2.persist();

		List<Entity> entities = Entity.findEntitiesByNamespaceWithSpace();

		Assert.assertTrue(entities.contains(entity_1));
		Assert.assertFalse(entities.contains(entity_2));
	}

	@Test
	public void listEntitiesForcingCaseInsensitiveName() {
		Entity entity_1 = this.createEntity("NAME", "Namespace");
		entity_1.persist();

		Entity entity_2 = this.createEntity("Entity", "Namespace");
		entity_2.persist();

		List<Entity> entitiesList = Entity.findEntitysByNameLike("name")
				.getResultList();
		Assert.assertTrue(entitiesList.contains(entity_1));
		Assert.assertFalse(entitiesList.contains(entity_2));
	}

	@Test
	public void getEntityByID() {
		entity = this.createEntity("name", "namespace");
		entity.persist();
		long id = entity.getId();
		Assert.assertEquals(entity, Entity.findEntity(id));
	}

	@Test
	public void listEntitiesUsingInvalidFragmentOfName() {
		String fragmentOfName = "INVALID_FRAGMENT";

		Entity entity_1 = this.createEntity("Bus", "namespace");
		entity_1.persist();

		Entity entity_2 = this.createEntity("Car", "namespace");
		entity_2.persist();

		List<Entity> entities = Entity.findEntitysByNameLike(fragmentOfName)
				.getResultList();

		Assert.assertFalse(entities.contains(entity_1));
		Assert.assertFalse(entities.contains(entity_2));
		Assert.assertEquals(0, entities.size());
	}

	@Test
	public void getEntityWithAnUnknowId() {
		Entity entity_1 = this.createEntity("Bus", "Namespace");
		entity_1.persist();
		long id = 10;
		Assert.assertNull(Entity.findEntity(id));
	}

	/* CREATE PROPERTY */

	@Test
	public void validEntityPropertyTypeAndName() {
		entity = this.createEntity("EntityName", "EntityNamespace");
		entity.persist();
		property = this.createProperty("PropertyName",
				"configuration", PropertyType.TEXT, entity);

		property.persist();
		Assert.assertEquals(Property.findProperty(property.getId()), property);
	}

	@Test
	public void twoPropertiesWithSameNameInDiferentEntities() {
		entity = this.createEntity("Entity_1", "EntityNamespace");
		entity.persist();
		property = this.createProperty("SameName",
				"configuration", PropertyType.TEXT, entity);
		property.persist();
		Entity entity_2 = this.createEntity("Entity_2", "EntityNamespace");
		entity_2.persist();
		Property property_2 = this.createProperty("SameName",
				"configuration", PropertyType.TEXT, entity_2);
		property_2.persist();

		Assert.assertEquals(Property.findProperty(property_2.getId()),
				property_2);
		Assert.assertEquals(entity_2, property_2.getEntity());
	}

	@Test
	public void propertyNameWithSpaces() {
		entity = this.createEntity("Entity_1", "EntityNamespace");
		entity.persist();
		property = this.createProperty("Name with spaces",
				"configuration", PropertyType.TEXT, entity);
		property.persist();
		Assert.assertEquals(Property.findProperty(property.getId()), property);
	}

	@Test
	public void configureMandatoryProperty() {
		// TODO
	}

	@Test
	public void configurePropertyWithDefaultValue() {
		// TODO
	}

	@Test(expected = ValidationException.class)
	public void propertyWithoutName() {
		entity = this.createEntity("Entity_1", "EntityNamespace");
		entity.persist();
		property = this.createProperty("",
				"configuration", PropertyType.TEXT, entity);
		property.persist();
	}
	
	@Test(expected = ValidationException.class)
	public void twoDifferentTypePropertiesWithSameNameInSameEntity(){
		entity = this.createEntity("Entity_1", "EntityNamespace");
		entity.persist();
		property = this.createProperty("SameName",
				"configuration", PropertyType.TEXT, entity);		
		property.persist();
		Property property_2 = this.createProperty("SameName",
				"configuration", PropertyType.PASSWORD, entity);
		property_2.persist();
	}
	
	@Test(expected = ValidationException.class)
	public void twoSameTypePropertiesWithSameNameInSameEntity(){
		entity = this.createEntity("Entity_1", "EntityNamespace");
		entity.persist();
		property = this.createProperty("SameName",
				"configuration", PropertyType.TEXT, entity);		
		property.persist();
		Property property_2 = this.createProperty("SameName",
				"configuration", PropertyType.TEXT, entity);
		property_2.persist();
	}
	
	@Test(expected = ValidationException.class)
	public void caseInsensitiveNames(){
		entity = this.createEntity("Entity_1", "EntityNamespace");
		entity.persist();
		property = this.createProperty("SameName",
				"configuration", PropertyType.TEXT, entity);		
		property.persist();
		Property property_2 = this.createProperty("samename",
				"configuration", PropertyType.TEXT, entity);
		property_2.persist();
	}
	
	@Test(expected = ValidationException.class)
	public void invalidConfigurationForEveryPropertyType(){
		//TODO
	}
	
	
}
