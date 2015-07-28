package com.va.podcon.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.va.podcon.domain.Appconfig;
import com.va.podcon.repository.AppconfigRepository;
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
 * REST controller for managing Appconfig.
 */
@RestController
@RequestMapping("/api")
public class AppconfigResource {

    private final Logger log = LoggerFactory.getLogger(AppconfigResource.class);

    @Inject
    private AppconfigRepository appconfigRepository;

    /**
     * POST  /appconfigs -> Create a new appconfig.
     */
    @RequestMapping(value = "/appconfigs",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Appconfig> create(@Valid @RequestBody Appconfig appconfig) throws URISyntaxException {
        log.debug("REST request to save Appconfig : {}", appconfig);
        if (appconfig.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new appconfig cannot already have an ID").body(null);
        }
        Appconfig result = appconfigRepository.save(appconfig);
        return ResponseEntity.created(new URI("/api/appconfigs/" + appconfig.getId())).body(result);
    }

    /**
     * PUT  /appconfigs -> Updates an existing appconfig.
     */
    @RequestMapping(value = "/appconfigs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Appconfig> update(@Valid @RequestBody Appconfig appconfig) throws URISyntaxException {
        log.debug("REST request to update Appconfig : {}", appconfig);
        if (appconfig.getId() == null) {
            return create(appconfig);
        }
        Appconfig result = appconfigRepository.save(appconfig);
        return ResponseEntity.ok().body(result);
    }

    /**
     * GET  /appconfigs -> get all the appconfigs.
     */
    @RequestMapping(value = "/appconfigs",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Appconfig>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Appconfig> page = appconfigRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/appconfigs", offset, limit);
        return new ResponseEntity<List<Appconfig>>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /appconfigs/:id -> get the "id" appconfig.
     */
    @RequestMapping(value = "/appconfigs/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Appconfig> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Appconfig : {}", id);
        Appconfig appconfig = appconfigRepository.findOne(id);
        if (appconfig == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(appconfig, HttpStatus.OK);
    }

    /**
     * DELETE  /appconfigs/:id -> delete the "id" appconfig.
     */
    @RequestMapping(value = "/appconfigs/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Appconfig : {}", id);
        appconfigRepository.delete(id);
    }
}
