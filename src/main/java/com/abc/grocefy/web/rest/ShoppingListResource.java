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
import com.abc.grocefy.domain.ShoppingList;
import com.abc.grocefy.domain.User;
import com.abc.grocefy.domain.enumeration.State;
import com.abc.grocefy.repository.ShoppingListRepository;
import com.abc.grocefy.repository.search.ShoppingListSearchRepository;
import com.abc.grocefy.security.SecurityUtils;
import com.abc.grocefy.service.UserService;
import com.abc.grocefy.web.rest.errors.BadRequestAlertException;
import com.abc.grocefy.web.rest.util.HeaderUtil;
import com.abc.grocefy.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;

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

    private final UserService userService;

    public ShoppingListResource(ShoppingListRepository shoppingListRepository,
        ShoppingListSearchRepository shoppingListSearchRepository,UserService userService) {
        this.shoppingListRepository = shoppingListRepository;
        this.shoppingListSearchRepository = shoppingListSearchRepository;
        this.userService = userService;
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
        // Override user to logged-in user
        shoppingList.setOwner(userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin().get()).get());
        // Override state to OPEN
        shoppingList.setState(State.OPEN);
        ShoppingList result = shoppingListRepository.save(shoppingList);
        shoppingListSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/shopping-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * POST  /shopping-lists/assign : Set shopper to loggedIn user
     *
     * @param id of the shoppingList to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated shoppingList,
     * or with status 400 (Bad Request) if the shoppingList is not valid,
     * or with status 500 (Internal Server Error) if the shoppingList couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/shopping-lists/assignshopper/{id}")
    public ResponseEntity<ShoppingList> assignShopper(@PathVariable Long id) throws URISyntaxException {
        log.debug("REST request to update ShoppingList : {}", id);
        final User user = userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        ShoppingList shoppingList = shoppingListRepository.findById(id).get();
        shoppingList.setShopper(user);
        ShoppingList result = shoppingListRepository.save(shoppingList);
        shoppingListSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, shoppingList.getId().toString()))
            .body(result);
}

    /**
     * POST  /shopping-lists/removeshopper/{id} : Set shopper to null
     *
     * @param id of the shoppingList to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated shoppingList,
     * or with status 400 (Bad Request) if the shoppingList is not valid,
     * or with status 500 (Internal Server Error) if the shoppingList couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/shopping-lists/removeshopper/{id}")
    public ResponseEntity<ShoppingList> unAssignShopper(@PathVariable Long id) throws URISyntaxException {
        log.debug("REST request to update ShoppingList : {}", id);
        ShoppingList shoppingList = shoppingListRepository.findById(id).orElse(new ShoppingList());
        validateRequest(shoppingList,shoppingList);
        shoppingList.setShopper(null);
        ShoppingList result = shoppingListRepository.save(shoppingList);
        shoppingListSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, shoppingList.getId().toString()))
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
        ShoppingList shoppingListFromDB =
            shoppingListRepository.findById(shoppingList.getId()).orElse(new ShoppingList());
        validateRequest(shoppingList,shoppingListFromDB);
        ShoppingList result = shoppingListRepository.save(shoppingList);
        shoppingListSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, shoppingList.getId().toString()))
            .body(result);
    }

    private void validateRequest(final ShoppingList shoppingListFromRequest,final ShoppingList shoppingListFromDB) {
        if (shoppingListFromRequest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        final User user = userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin().get()).get();

        if (!compareUsers(shoppingListFromRequest.getOwner(),shoppingListFromDB.getOwner())){
            throw new BadRequestAlertException ("Owner can't be edited", ENTITY_NAME, "invalidrequest");
        }

        if (!compareUsers(shoppingListFromRequest.getShopper(),shoppingListFromDB.getShopper())){
            throw new BadRequestAlertException ("Shopper can't be edited, please use "
                + "/shopping-lists/assignshopper/{id} or /shopping-lists/removeshopper/{id} endpoints",
                ENTITY_NAME, "invalidrequest");
        }

        if (!compareUsers(shoppingListFromRequest.getOwner(),user) && !compareUsers(shoppingListFromRequest.getShopper(),user) ){
            throw new BadRequestAlertException (user.getLogin() + " is not allowed to make changes in this list",
                ENTITY_NAME, "invaliduser");
        }
    }


    /**
     * Assuming ID will never be 0
     */
    private boolean compareUsers(User x, User y){
        long xID = x !=null ? x.getId() : 0;
        long yID = y !=null ? y.getId() : 0;
        return xID == yID;
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
        Page<ShoppingList> page = shoppingListRepository.findByCurrentUser(pageable);
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
