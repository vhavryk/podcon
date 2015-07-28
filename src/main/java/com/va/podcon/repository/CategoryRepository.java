package com.va.podcon.repository;

import com.va.podcon.domain.Category;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Category entity.
 */
public interface CategoryRepository extends JpaRepository<Category,Long> {

    Category findByName(String name);
}
