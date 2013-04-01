package com.nanuvem.lom.model;

import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;

import junit.framework.Assert;

import org.hibernate.PropertyNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.test.RooIntegrationTest;

@RooIntegrationTest(entity = Property.class)
public class PropertyIntegrationTest {
	private Property property;
	private Entity entity;

	@Autowired
	private PropertyDataOnDemand dod;

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
		property = new Property();

	}

	@Test
	public void testCountPropertys() {
		entity = createEntity("entity", "namespace");
		Assert.assertNotNull(entity);
		entity.persist();

		property = createProperty("property", null, PropertyType._BINARY,
				entity);
		property.persist();
		long count = Property.countPropertys();
		Assert.assertTrue(count == 1);
	}

	@Test
	public void testFindProperty() {
		entity = createEntity("entity", "namespace");
		Assert.assertNotNull(entity);
		entity.persist();

		property = createProperty("property", null, PropertyType._BINARY,
				entity);
		property.persist();

		Property found = Property.findProperty(property.getId());
		Assert.assertEquals(entity.getId(), found.getId());
	}

	@Test
	public void testFlush() {
		entity = createEntity("entity", "namespace");
		entity.persist();

		property = createProperty("property", null, PropertyType._BINARY,
				entity);
		property.persist();
		Long id = property.getId();
		Assert.assertNotNull(
				"Data on demand for 'Property' failed to provide an identifier",
				id);
		property = Property.findProperty(id);
		Assert.assertNotNull(
				"Find method for 'Property' illegally returned null for id '"
						+ id + "'", property);

		boolean modified = dod.modifyProperty(property);

		Integer currentVersion = property.getVersion();
		property.flush();
		Assert.assertTrue(
				"Version for 'Property' failed to increment on flush directive",
				(currentVersion != null && property.getVersion() > currentVersion)
						|| !modified);
	}

	@Test
	public void testFindPropertyEntries() {
		entity = createEntity("entity", "namespace");
		entity.persist();

		property = createProperty("property", null, PropertyType._BINARY,
				entity);
		property.persist();

		long count = Property.countPropertys();
		if (count > 20)
			count = 20;
		int firstResult = 0;
		int maxResults = (int) count;
		List<Property> result = Property.findPropertyEntries(firstResult,
				maxResults);
		Assert.assertNotNull(
				"Find entries method for 'Property' illegally returned null",
				result);
		Assert.assertEquals(
				"Find entries method for 'Property' returned an incorrect number of entries",
				count, result.size());
	}

	@Test
	public void testMergeUpdate() {
		entity = createEntity("entity", "namespace");
		entity.persist();

		property = createProperty("property", null, PropertyType._BINARY,
				entity);
		property.persist();

		Long id = property.getId();
		Assert.assertNotNull(
				"Data on demand for 'Property' failed to provide an identifier",
				id);
		property = Property.findProperty(id);
		boolean modified = dod.modifyProperty(property);
		Integer currentVersion = property.getVersion();
		Property merged = property.merge();
		property.flush();
		Assert.assertEquals(
				"Identifier of merged object not the same as identifier of original object",
				merged.getId(), id);
		Assert.assertTrue(
				"Version for 'Property' failed to increment on merge and flush directive",
				(currentVersion != null && property.getVersion() > currentVersion)
						|| !modified);
	}

	@Test
	public void testPersist() {
		entity = createEntity("EntityName", "EntityNamespace");
		entity.persist();
		property = createProperty("PropertyName", null, PropertyType.TEXT,
				entity);
		property.persist();
		property.flush();
		Assert.assertNotNull(
				"Expected 'Property' identifier to no longer be null",
				property.getId());
	}

	@Test
	public void testRemove() {
		entity = createEntity("EntityName", "EntityNamespace");
		entity.persist();
		property = createProperty("PropertyName", null, PropertyType.TEXT,
				entity);
		property.persist();
		long id = property.getId();
		property.remove();
		property.flush();
		Property.findProperty(id);
	}

	/* CREATE PROPERTY */

	@Test
	public void validEntityPropertyTypeAndName() {
		entity = createEntity("EntityName", "EntityNamespace");
		entity.persist();
		property = createProperty("PropertyName", null, PropertyType.TEXT,
				entity);
		property.persist();
		Assert.assertEquals(Property.findProperty(property.getId()), property);
	}

	@Test
	public void twoPropertiesWithSameNameInDiferentEntities() {
		entity = createEntity("Entity_1", "EntityNamespace");
		entity.persist();
		property = createProperty("SameName", null, PropertyType.TEXT, entity);
		property.persist();
		Entity entity_2 = createEntity("Entity_2", "EntityNamespace");
		entity_2.persist();
		Property property_2 = createProperty("SameName", null,
				PropertyType.TEXT, entity_2);
		property_2.persist();

		Assert.assertEquals(Property.findProperty(property_2.getId()),
				property_2);
		Assert.assertEquals(entity_2, property_2.getEntity());
	}

	@Test
	public void propertyNameWithSpaces() {
		entity = createEntity("Entity_1", "EntityNamespace");
		entity.persist();
		property = createProperty("Name with spaces", null, PropertyType.TEXT,
				entity);
		property.persist();
		Assert.assertEquals(Property.findProperty(property.getId()), property);
	}

	@Test
	public void configureMandatoryProperty() {
		entity = createEntity("Entity_1", "EntityNamespace");
		entity.persist();
		property = createProperty("Name with spaces", "{\"mandatory\":true}", PropertyType.TEXT,
				entity);
		
		property.persist();
		
	}

	
	 @Test 
	 public void configurePropertyWithDefaultValue() { 
		 entity = createEntity("Entity_1", "EntityNamespace");
			entity.persist();
			property = createProperty("Name with spaces", "{\"default\":\"abc\"}", PropertyType.TEXT,
					entity);
			
			property.persist();
	 }
	 

	@Test(expected = ValidationException.class)
	public void propertyWithoutName() {
		entity = createEntity("Entity_1", "EntityNamespace");
		entity.persist();
		property = createProperty("", null, PropertyType.TEXT, entity);
		property.persist();
	}

	@Test(expected = ValidationException.class)
	public void propertyWithoutEntity() {
		Entity nullEntity = null;
		property = createProperty("", null, PropertyType.TEXT, nullEntity);
		property.persist();
	}

	@Test(expected = ValidationException.class)
	public void twoDifferentTypePropertiesWithSameNameInSameEntity() {
		entity = createEntity("Entity_1", "EntityNamespace");
		entity.persist();
		property = createProperty("SameName", null, PropertyType.TEXT, entity);
		property.persist();
		Property property_2 = createProperty("SameName", null,
				PropertyType.PASSWORD, entity);
		property_2.persist();
	}

	@Test(expected = ValidationException.class)
	public void twoSameTypePropertiesWithSameNameInSameEntity() {
		entity = createEntity("Entity_1", "EntityNamespace");
		entity.persist();
		property = createProperty("SameName", null, PropertyType.TEXT, entity);
		property.persist();
		Property property_2 = createProperty("SameName", null,
				PropertyType.TEXT, entity);
		property_2.persist();
	}

	@Test(expected = ValidationException.class)
	public void caseInsensitiveNames() {
		entity = createEntity("Entity_1", "EntityNamespace");
		entity.persist();
		property = createProperty("SameName", null, PropertyType.TEXT, entity);
		property.persist();
		Property property_2 = createProperty("samename", null,
				PropertyType.TEXT, entity);
		property_2.persist();
	}

	/*
	 * @Test//(expected = ValidationException.class) public void
	 * invalidConfigurationForEveryPropertyType(){ //TODO }
	 */

	/* READ PROPERTIES */

	@Test
	public void testFindAllPropertys() {
		entity = createEntity("Entity_1", "EntityNamespace");
		entity.persist();
		property = createProperty("Property_1", null, PropertyType.TEXT, entity);
		property.persist();

		Property property_2 = createProperty("Property_2", null,
				PropertyType.TEXT, entity);
		property_2.persist();
		long count = Property.countPropertys();
		List<Property> result = Property.findAllPropertys();
		Assert.assertNotNull(
				"Find all method for 'Property' illegally returned null",
				result);
		Assert.assertTrue(
				"Find all method for 'Property' failed to return any data",
				result.size() == count);
	}

	@Test
	public void listAllPropertiesOfAnEntity() {
		entity = createEntity("Entity_1", "EntityNamespace");
		entity.persist();
		property = createProperty("Property_1", null, PropertyType.TEXT, entity);
		property.persist();

		Property property_2 = createProperty("Property_2", null,
				PropertyType.TEXT, entity);
		property_2.persist();

		List<Property> properties = Property.findPropertysByEntity(entity)
				.getResultList();

		Assert.assertTrue(properties.contains(property));
		Assert.assertTrue(properties.contains(property_2));
		Assert.assertTrue(properties.size() == 2);
	}

	@Test
	public void listAllPropertiesOfAnEntityByValidFragmentOfName() {
		entity = createEntity("Entity_1", "EntityNamespace");
		entity.persist();
		property = createProperty("Property_1", null, PropertyType.TEXT, entity);
		property.persist();

		Property property_2 = createProperty("Property_2", null,
				PropertyType.TEXT, entity);
		property_2.persist();

		List<Property> propertiesByFragment = entity.findPropertiesByName("_2");

		Assert.assertTrue(propertiesByFragment.contains(property_2));
		Assert.assertEquals(propertiesByFragment.size(), 1);

	}

	@Test
	public void listAllPropertiesOfAnEntityByEmptyName() {
		// TODO
		entity = createEntity("Entity_1", "EntityNamespace");
		entity.persist();
		property = createProperty("Property 1", null, PropertyType.TEXT, entity);
		property.persist();

		Property property_2 = createProperty("Property_2", null,
				PropertyType.TEXT, entity);
		property_2.persist();

		List<Property> propertiesByFragment = entity.findPropertiesByName("");

		Assert.assertTrue(propertiesByFragment.contains(property));
		Assert.assertTrue(propertiesByFragment.contains(property_2));
		Assert.assertEquals(propertiesByFragment.size(), 2);

	}

	@Test
	public void listAllPropertiesOfAnEntityByFragmentOfNameWithSpaces() {
		entity = createEntity("Entity_1", "EntityNamespace");
		entity.persist();
		property = createProperty("Property 1", null, PropertyType.TEXT, entity);
		property.persist();

		Property property_2 = createProperty("Property_2", null,
				PropertyType.TEXT, entity);
		property_2.persist();

		List<Property> propertiesByFragment = entity
				.findPropertiesByName("y 1");

		Assert.assertTrue(propertiesByFragment.contains(property));
		Assert.assertEquals(propertiesByFragment.size(), 1);
	}

	@Test
	public void listPropertiesWhenThereIsNoProperties() {
		List<Property> properties = Property.findAllPropertys();
		Assert.assertEquals(properties.size(), 0);
	}

	/*
	 * @Test public void listPropertiesForcingCaseInsensitiveNames(){
	 * 
	 * }
	 */

	@Test
	public void getPropertyByID() {
		entity = createEntity("Entity_1", "EntityNamespace");
		entity.persist();
		property = createProperty("Property 1", null, PropertyType.TEXT, entity);
		property.persist();
		Assert.assertEquals(property, Property.findProperty(property.getId()));
	}

	@Test(expected = PropertyNotFoundException.class)
	public void getPropertyWithUnknownId() {
		long id = 10;
		Assert.assertNull(Property.findProperty(id));
	}

	@Test(expected = EntityNotFoundException.class)
	public void listPropertiesOfAnUnknownEntity() {
		Property.findPropertysByEntity(entity);
	}

	/* REMOVE PROPERTIES */

}
