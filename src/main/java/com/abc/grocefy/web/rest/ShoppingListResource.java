package com.abc.grocefy.web.rest;
import com.abc.grocefy.domain.ShoppingList;
import com.abc.grocefy.repository.ShoppingListRepository;
import com.abc.grocefy.repository.search.ShoppingListSearchRepository;
import com.abc.grocefy.web.rest.errors.BadRequestAlertException;
import com.abc.grocefy.web.rest.util.HeaderUtil;
import com.abc.grocefy.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing ShoppingList.
 */
@RestController
@RequestMapping("/api")
public class ShoppingListResource {

    private final Logger log = LoggerFactory.getLogger(ShoppingListResource.class);

    private static final String ENTITY_NAME = "shoppingList";

    private final ShoppingListRepository shoppingListRepository;

    private final ShoppingListSearchRepository shoppingListSearchRepository;

    public ShoppingListResource(ShoppingListRepository shoppingListRepository, ShoppingListSearchRepository shoppingListSearchRepository) {
        this.shoppingListRepository = shoppingListRepository;
        this.shoppingListSearchRepository = shoppingListSearchRepository;
    }

    /**
     * POST  /shopping-lists : Create a new shoppingList.
     *
     * @param shoppingList the shoppingList to create
     * @return the ResponseEntity with status 201 (Created) and with body the new shoppingList, or with status 400 (Bad Request) if the shoppingList has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/shopping-lists")
    public ResponseEntity<ShoppingList> createShoppingList(@Valid @RequestBody ShoppingList shoppingList) throws URISyntaxException {
        log.debug("REST request to save ShoppingList : {}", shoppingList);
        if (shoppingList.getId() != null) {
            throw new BadRequestAlertException("A new shoppingList cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ShoppingList result = shoppingListRepository.save(shoppingList);
        shoppingListSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/shopping-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /shopping-lists : Updates an existing shoppingList.
     *
     * @param shoppingList the shoppingList to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated shoppingList,
     * or with status 400 (Bad Request) if the shoppingList is not valid,
     * or with status 500 (Internal Server Error) if the shoppingList couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/shopping-lists")
    public ResponseEntity<ShoppingList> updateShoppingList(@Valid @RequestBody ShoppingList shoppingList) throws URISyntaxException {
        log.debug("REST request to update ShoppingList : {}", shoppingList);
        if (shoppingList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ShoppingList result = shoppingListRepository.save(shoppingList);
        shoppingListSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, shoppingList.getId().toString()))
            .body(result);
    }

    /**
     * GET  /shopping-lists : get all the shoppingLists.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of shoppingLists in body
     */
    @GetMapping("/shopping-lists")
    public ResponseEntity<List<ShoppingList>> getAllShoppingLists(Pageable pageable) {
        log.debug("REST request to get a page of ShoppingLists");
        Page<ShoppingList> page = shoppingListRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/shopping-lists");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /shopping-lists/:id : get the "id" shoppingList.
     *
     * @param id the id of the shoppingList to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the shoppingList, or with status 404 (Not Found)
     */
    @GetMapping("/shopping-lists/{id}")
    public ResponseEntity<ShoppingList> getShoppingList(@PathVariable Long id) {
        log.debug("REST request to get ShoppingList : {}", id);
        Optional<ShoppingList> shoppingList = shoppingListRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(shoppingList);
    }

    /**
     * DELETE  /shopping-lists/:id : delete the "id" shoppingList.
     *
     * @param id the id of the shoppingList to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/shopping-lists/{id}")
    public ResponseEntity<Void> deleteShoppingList(@PathVariable Long id) {
        log.debug("REST request to delete ShoppingList : {}", id);
        shoppingListRepository.deleteById(id);
        shoppingListSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/shopping-lists?query=:query : search for the shoppingList corresponding
     * to the query.
     *
     * @param query the query of the shoppingList search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/shopping-lists")
    public ResponseEntity<List<ShoppingList>> searchShoppingLists(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of ShoppingLists for query {}", query);
        Page<ShoppingList> page = shoppingListSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/shopping-lists");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
