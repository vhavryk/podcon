package com.va.podcon.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.va.podcon.domain.Tag;
import com.va.podcon.repository.TagRepository;
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
 * REST controller for managing Tag.
 */
@RestController
@RequestMapping("/api")
public class TagResource {

    private final Logger log = LoggerFactory.getLogger(TagResource.class);

    @Inject
    private TagRepository tagRepository;

    /**
     * POST  /tags -> Create a new tag.
     */
    @RequestMapping(value = "/tags",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Tag> create(@Valid @RequestBody Tag tag) throws URISyntaxException {
        log.debug("REST request to save Tag : {}", tag);
        if (tag.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new tag cannot already have an ID").body(null);
        }
        Tag result = tagRepository.save(tag);
        return ResponseEntity.created(new URI("/api/tags/" + tag.getId())).body(result);
    }

    /**
     * PUT  /tags -> Updates an existing tag.
     */
    @RequestMapping(value = "/tags",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Tag> update(@Valid @RequestBody Tag tag) throws URISyntaxException {
        log.debug("REST request to update Tag : {}", tag);
        if (tag.getId() == null) {
            return create(tag);
        }
        Tag result = tagRepository.save(tag);
        return ResponseEntity.ok().body(result);
    }

    /**
     * GET  /tags -> get all the tags.
     */
    @RequestMapping(value = "/tags",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Tag>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Tag> page = tagRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tags", offset, limit);
        return new ResponseEntity<List<Tag>>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /tags/:id -> get the "id" tag.
     */
    @RequestMapping(value = "/tags/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Tag> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Tag : {}", id);
        Tag tag = tagRepository.findOne(id);
        if (tag == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(tag, HttpStatus.OK);
    }

    /**
     * DELETE  /tags/:id -> delete the "id" tag.
     */
    @RequestMapping(value = "/tags/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Tag : {}", id);
        tagRepository.delete(id);
    }
}
