package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServicesTests {

	@InjectMocks
	private ProductService service;
	@Mock
	private ProductRepository repository;
	@Mock 
	CategoryRepository categoryRepository;
	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private Product product;
	private Category category;
	private ProductDTO productDTO;
	
	private PageImpl<Product> page;

	@BeforeEach
	void SetUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 4L;
		product = Factory.createProduct();
		productDTO = Factory.createProductDTO();
		category = new Category(1L,"Eletronics");
		page = new PageImpl<>(List.of(product));
		
		

		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		Mockito.when(repository.getOne(existingId)).thenReturn(product);
		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
		
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}

	@Test
	public void deleteShouldDONothingWhenIdExists() {

		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);

		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);

	}

	@Test
	public void deleteSholdThrowResourceNotFoundExceptionWhenIdDoesNotExisits() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);

	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}


	@Test
	public void findAllPagedShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<ProductDTO> result = service.findAllPaged(pageable);
		Assertions.assertNotNull(result);
		Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
		
		
		
	}
	@Test
	public void findByIdShoulReturnIdWhenExists() {
		ProductDTO productDto = service.findById(existingId);
		Assertions.assertNotNull(productDto);
		Mockito.verify(repository, Mockito.times(1)).findById(existingId);
	}
	@Test
	public void findByIDShouldThrowResourceNotFoundExceptionWhenIdNonExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			service.findById(nonExistingId);
			Mockito.verify(repository,Mockito.times(1)).findById(nonExistingId);
		});
		Mockito.verify(repository,Mockito.times(1)).findById(nonExistingId);
	}
	
	@Test
	public void updateShouldReturProductDTOWhenIDExists() {
		ProductDTO result = service.update(existingId, productDTO);
		Assertions.assertNotNull(result);
		Mockito.verify(repository,Mockito.times(1)).getOne(existingId);
		Mockito.verify(categoryRepository,Mockito.times(1)).getOne(existingId);
	}
	/*@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdExistis() {
		
		ProductDTO result = service.update(existingId, productDTO);
		
		
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			service.update(nonExistingId, productDTO);
			
		});
		Mockito.verify(repository,Mockito.times(1)).getOne(nonExistingId);
		Mockito.verify(categoryRepository,Mockito.times(1)).getOne(existingId);
		
		
		
	}*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
