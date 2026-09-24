package com.example.LibraryManagementSystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.LibraryManagementSystem.dto.category.CategoryRequest;
import com.example.LibraryManagementSystem.entity.Category;
import com.example.LibraryManagementSystem.exception.CategoryAlreadyExistsException;
import com.example.LibraryManagementSystem.exception.CategoryNotFoundException;
import com.example.LibraryManagementSystem.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void save_shouldCreateCategory_whenNameIsAvailable() {
        // Arrange
        CategoryRequest request =
            new CategoryRequest("  science fiction  ");

        when(categoryRepository.existsByNameIgnoreCase(
            "SCIENCE FICTION"
        )).thenReturn(false);

        when(categoryRepository.save(any(Category.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Category result = categoryService.save(request);

        // Assert
        assertEquals("SCIENCE FICTION", result.getName());

        verify(categoryRepository)
            .save(any(Category.class));
    }

    @Test
    void save_shouldThrowException_whenCategoryAlreadyExists() {
        // Arrange
        CategoryRequest request =
            new CategoryRequest("  horror  ");

        when(categoryRepository.existsByNameIgnoreCase("HORROR"))
            .thenReturn(true);

        // Act & Assert
        assertThrows(
            CategoryAlreadyExistsException.class,
            () -> categoryService.save(request)
        );

        verify(categoryRepository, never())
            .save(any(Category.class));
    }

    @Test
    void update_shouldUpdateCategory_whenRequestIsValid() {
        // Arrange
        Long categoryId = 1L;

        CategoryRequest request =
            new CategoryRequest("  philosophy  ");

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("OLD NAME");

        when(categoryRepository.findById(categoryId))
            .thenReturn(Optional.of(existingCategory));

        when(categoryRepository.existsByNameIgnoreCaseAndIdNot(
            "PHILOSOPHY",
            categoryId
        )).thenReturn(false);

        when(categoryRepository.save(any(Category.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Category result =
            categoryService.update(categoryId, request);

        // Assert
        assertSame(existingCategory, result);
        assertEquals("PHILOSOPHY", result.getName());

        verify(categoryRepository).save(existingCategory);
    }

    @Test
    void update_shouldThrowException_whenCategoryDoesNotExist() {
        // Arrange
        Long categoryId = 999L;

        CategoryRequest request =
            new CategoryRequest("Horror");

        when(categoryRepository.findById(categoryId))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            CategoryNotFoundException.class,
            () -> categoryService.update(categoryId, request)
        );

        verify(categoryRepository, never())
            .save(any(Category.class));
    }

    @Test
    void update_shouldThrowException_whenNameBelongsToAnotherCategory() {
        // Arrange
        Long categoryId = 1L;

        CategoryRequest request =
            new CategoryRequest(" horror ");

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("FANTASY");

        when(categoryRepository.findById(categoryId))
            .thenReturn(Optional.of(existingCategory));

        when(categoryRepository.existsByNameIgnoreCaseAndIdNot(
            "HORROR",
            categoryId
        )).thenReturn(true);

        // Act & Assert
        assertThrows(
            CategoryAlreadyExistsException.class,
            () -> categoryService.update(categoryId, request)
        );

        verify(categoryRepository, never())
            .save(any(Category.class));
    }

    @Test
    void findAll_shouldReturnRequestedPage() {
        Pageable pageable = PageRequest.of(0, 2);

        Category firstCategory = new Category();
        firstCategory.setId(1L);
        firstCategory.setName("FANTASY");

        Category secondCategory = new Category();
        secondCategory.setId(2L);
        secondCategory.setName("HORROR");

        Page<Category> repositoryPage = new PageImpl<>(
            List.of(firstCategory, secondCategory),
            pageable,
            4
        );

        when(categoryRepository.findAll(pageable))
            .thenReturn(repositoryPage);

        Page<Category> result = categoryService.findAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(4, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertSame(firstCategory, result.getContent().get(0));

        verify(categoryRepository).findAll(pageable);
    }
}
