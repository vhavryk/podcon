package com.va.podcon.repository;

import com.va.podcon.domain.Tag;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Tag entity.
 */
public interface TagRepository extends JpaRepository<Tag,Long> {

}
