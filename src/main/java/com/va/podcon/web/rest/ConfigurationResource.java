package com.va.podcon.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.va.podcon.domain.Configuration;
import com.va.podcon.repository.ConfigurationRepository;
import com.va.podcon.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * REST controller for managing Configuration.
 */
@RestController
@RequestMapping("/api")
public class ConfigurationResource {

    private final Logger log = LoggerFactory.getLogger(ConfigurationResource.class);

    @Inject
    private ConfigurationRepository configurationRepository;

    /**
     * POST  /configurations -> Create a new configuration.
     */
    @RequestMapping(value = "/configurations",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Configuration configuration) throws URISyntaxException {
        log.debug("REST request to save Configuration : {}", configuration);
        if (configuration.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new configuration cannot already have an ID").build();
        }
        configurationRepository.save(configuration);
        return ResponseEntity.created(new URI("/api/configurations/" + configuration.getId())).build();
    }

    /**
     * PUT  /configurations -> Updates an existing configuration.
     */
    @RequestMapping(value = "/configurations",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Configuration configuration) throws URISyntaxException {
        log.debug("REST request to update Configuration : {}", configuration);
        if (configuration.getId() == null) {
            return create(configuration);
        }
        configurationRepository.save(configuration);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /configurations -> get all the configurations.
     */
    @RequestMapping(value = "/configurations",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Configuration>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Configuration> page = configurationRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/configurations", offset, limit);
        return new ResponseEntity<List<Configuration>>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /configurations/:id -> get the "id" configuration.
     */
    @RequestMapping(value = "/configurations/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Configuration> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Configuration : {}", id);
        Configuration configuration = configurationRepository.findOne(id);
        if (configuration == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(configuration, HttpStatus.OK);
    }

    /**
     * DELETE  /configurations/:id -> delete the "id" configuration.
     */
    @RequestMapping(value = "/configurations/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Configuration : {}", id);
        configurationRepository.delete(id);
    }
}
