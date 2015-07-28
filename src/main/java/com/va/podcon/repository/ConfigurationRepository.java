package com.va.podcon.repository;

import com.va.podcon.domain.Configuration;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Configuration entity.
 */
public interface ConfigurationRepository extends JpaRepository<Configuration,Long> {

}
