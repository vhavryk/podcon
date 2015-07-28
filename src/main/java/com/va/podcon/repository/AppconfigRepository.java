package com.va.podcon.repository;

import com.va.podcon.domain.Appconfig;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Appconfig entity.
 */
public interface AppconfigRepository extends JpaRepository<Appconfig,Long> {

}
