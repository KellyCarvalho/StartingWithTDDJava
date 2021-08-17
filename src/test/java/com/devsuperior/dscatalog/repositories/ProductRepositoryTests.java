package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {
	@Autowired
	private ProductRepository repository;
	private long existingId;
	private long nonExistingId;
	private long countTotalProducts;

	@BeforeEach
	void setup() throws Exception {
		existingId = 1L;
		nonExistingId = 63L;
		countTotalProducts=25;

	}
	
	@Test
	public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
		Product product = Factory.createProduct();
		product.setId(null);
		product = repository.save(product);
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts+1, product.getId());
		
	}

	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {

		repository.deleteById(existingId);

		Optional<Product> result = repository.findById(1L);

		Assertions.assertFalse(result.isPresent());
	}

	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionIdDoesNotExist() {

		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {

			repository.deleteById(nonExistingId);

		});

	}
	
	@Test
	public void findByIdShouldReturnOptionalProductNoVoidWhenIdExist() {
		Optional<Product> product = repository.findById(existingId);
		
		Assertions.assertFalse(product.isEmpty());;
		
	}
	@Test
	public void findByIdShouldReturnVoidWhenIdNotExist(){
		Optional<Product> product = repository.findById(60L);
		Assertions.assertTrue(product.isEmpty());
		
		
	}
}
