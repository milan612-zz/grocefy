package com.abc.grocefy.web.rest;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.abc.grocefy.domain.Groups;
import com.abc.grocefy.repository.GroupsRepository;
import com.abc.grocefy.repository.search.GroupsSearchRepository;
import com.abc.grocefy.web.rest.errors.BadRequestAlertException;
import com.abc.grocefy.web.rest.util.HeaderUtil;
import com.abc.grocefy.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing Groups.
 */
@RestController
@RequestMapping("/api")
public class GroupsResource {

    private final Logger log = LoggerFactory.getLogger(GroupsResource.class);

    private static final String ENTITY_NAME = "groups";

    private final GroupsRepository groupsRepository;

    private final GroupsSearchRepository groupsSearchRepository;

    public GroupsResource(GroupsRepository groupsRepository, GroupsSearchRepository groupsSearchRepository) {
        this.groupsRepository = groupsRepository;
        this.groupsSearchRepository = groupsSearchRepository;
    }

    /**
     * POST  /groups : Create a new groups.
     *
     * @param groups the groups to create
     * @return the ResponseEntity with status 201 (Created) and with body the new groups, or with status 400 (Bad Request) if the groups has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/groups")
    public ResponseEntity<Groups> createGroups(@Valid @RequestBody Groups groups) throws URISyntaxException {
        log.debug("REST request to save Groups : {}", groups);
        if (groups.getId() != null) {
            throw new BadRequestAlertException("A new groups cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Groups result = groupsRepository.save(groups);
        groupsSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/groups/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /groups : Updates an existing groups.
     *
     * @param groups the groups to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated groups,
     * or with status 400 (Bad Request) if the groups is not valid,
     * or with status 500 (Internal Server Error) if the groups couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/groups")
    public ResponseEntity<Groups> updateGroups(@Valid @RequestBody Groups groups) throws URISyntaxException {
        log.debug("REST request to update Groups : {}", groups);
        if (groups.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Groups result = groupsRepository.save(groups);
        groupsSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, groups.getId().toString()))
            .body(result);
    }

    /**
     * GET  /groups : get all the groups.
     *
     * @param pageable the pagination information
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many)
     * @return the ResponseEntity with status 200 (OK) and the list of groups in body
     */
    @GetMapping("/groups")
    public ResponseEntity<List<Groups>> getAllGroups(Pageable pageable, @RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get a page of Groups");
        Page<Groups> page;
        if (eagerload) {
            page = groupsRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = groupsRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, String.format("/api/groups?eagerload=%b", eagerload));
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /groups/:id : get the "id" groups.
     *
     * @param id the id of the groups to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the groups, or with status 404 (Not Found)
     */
    @GetMapping("/groups/{id}")
    public ResponseEntity<Groups> getGroups(@PathVariable Long id) {
        log.debug("REST request to get Groups : {}", id);
        Optional<Groups> groups = groupsRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(groups);
    }

    /**
     * DELETE  /groups/:id : delete the "id" groups.
     *
     * @param id the id of the groups to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/groups/{id}")
    public ResponseEntity<Void> deleteGroups(@PathVariable Long id) {
        log.debug("REST request to delete Groups : {}", id);
        groupsRepository.deleteById(id);
        groupsSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/groups?query=:query : search for the groups corresponding
     * to the query.
     *
     * @param query the query of the groups search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/groups")
    public ResponseEntity<List<Groups>> searchGroups(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Groups for query {}", query);
        Page<Groups> page = groupsSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/groups");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
