package com.abc.grocefy.web.rest;

import com.abc.grocefy.GrocefyApp;

import com.abc.grocefy.domain.ShoppingList;
import com.abc.grocefy.repository.ShoppingListRepository;
import com.abc.grocefy.repository.search.ShoppingListSearchRepository;
import com.abc.grocefy.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;


import static com.abc.grocefy.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.abc.grocefy.domain.enumeration.State;
/**
 * Test class for the ShoppingListResource REST controller.
 *
 * @see ShoppingListResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrocefyApp.class)
public class ShoppingListResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_LIST = "AAAAAAAAAA";
    private static final String UPDATED_LIST = "BBBBBBBBBB";

    private static final State DEFAULT_STATE = State.OPEN;
    private static final State UPDATED_STATE = State.WAITING;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    /**
     * This repository is mocked in the com.abc.grocefy.repository.search test package.
     *
     * @see com.abc.grocefy.repository.search.ShoppingListSearchRepositoryMockConfiguration
     */
    @Autowired
    private ShoppingListSearchRepository mockShoppingListSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restShoppingListMockMvc;

    private ShoppingList shoppingList;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ShoppingListResource shoppingListResource = new ShoppingListResource(shoppingListRepository, mockShoppingListSearchRepository);
        this.restShoppingListMockMvc = MockMvcBuilders.standaloneSetup(shoppingListResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShoppingList createEntity(EntityManager em) {
        ShoppingList shoppingList = new ShoppingList()
            .title(DEFAULT_TITLE)
            .list(DEFAULT_LIST)
            .state(DEFAULT_STATE);
        return shoppingList;
    }

    @Before
    public void initTest() {
        shoppingList = createEntity(em);
    }

    @Test
    @Transactional
    public void createShoppingList() throws Exception {
        int databaseSizeBeforeCreate = shoppingListRepository.findAll().size();

        // Create the ShoppingList
        restShoppingListMockMvc.perform(post("/api/shopping-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(shoppingList)))
            .andExpect(status().isCreated());

        // Validate the ShoppingList in the database
        List<ShoppingList> shoppingListList = shoppingListRepository.findAll();
        assertThat(shoppingListList).hasSize(databaseSizeBeforeCreate + 1);
        ShoppingList testShoppingList = shoppingListList.get(shoppingListList.size() - 1);
        assertThat(testShoppingList.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testShoppingList.getList()).isEqualTo(DEFAULT_LIST);
        assertThat(testShoppingList.getState()).isEqualTo(DEFAULT_STATE);

        // Validate the ShoppingList in Elasticsearch
        verify(mockShoppingListSearchRepository, times(1)).save(testShoppingList);
    }

    @Test
    @Transactional
    public void createShoppingListWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = shoppingListRepository.findAll().size();

        // Create the ShoppingList with an existing ID
        shoppingList.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restShoppingListMockMvc.perform(post("/api/shopping-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(shoppingList)))
            .andExpect(status().isBadRequest());

        // Validate the ShoppingList in the database
        List<ShoppingList> shoppingListList = shoppingListRepository.findAll();
        assertThat(shoppingListList).hasSize(databaseSizeBeforeCreate);

        // Validate the ShoppingList in Elasticsearch
        verify(mockShoppingListSearchRepository, times(0)).save(shoppingList);
    }

    @Test
    @Transactional
    public void getAllShoppingLists() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        // Get all the shoppingListList
        restShoppingListMockMvc.perform(get("/api/shopping-lists?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shoppingList.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].list").value(hasItem(DEFAULT_LIST.toString())))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())));
    }
    
    @Test
    @Transactional
    public void getShoppingList() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        // Get the shoppingList
        restShoppingListMockMvc.perform(get("/api/shopping-lists/{id}", shoppingList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(shoppingList.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.list").value(DEFAULT_LIST.toString()))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingShoppingList() throws Exception {
        // Get the shoppingList
        restShoppingListMockMvc.perform(get("/api/shopping-lists/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateShoppingList() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        int databaseSizeBeforeUpdate = shoppingListRepository.findAll().size();

        // Update the shoppingList
        ShoppingList updatedShoppingList = shoppingListRepository.findById(shoppingList.getId()).get();
        // Disconnect from session so that the updates on updatedShoppingList are not directly saved in db
        em.detach(updatedShoppingList);
        updatedShoppingList
            .title(UPDATED_TITLE)
            .list(UPDATED_LIST)
            .state(UPDATED_STATE);

        restShoppingListMockMvc.perform(put("/api/shopping-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedShoppingList)))
            .andExpect(status().isOk());

        // Validate the ShoppingList in the database
        List<ShoppingList> shoppingListList = shoppingListRepository.findAll();
        assertThat(shoppingListList).hasSize(databaseSizeBeforeUpdate);
        ShoppingList testShoppingList = shoppingListList.get(shoppingListList.size() - 1);
        assertThat(testShoppingList.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testShoppingList.getList()).isEqualTo(UPDATED_LIST);
        assertThat(testShoppingList.getState()).isEqualTo(UPDATED_STATE);

        // Validate the ShoppingList in Elasticsearch
        verify(mockShoppingListSearchRepository, times(1)).save(testShoppingList);
    }

    @Test
    @Transactional
    public void updateNonExistingShoppingList() throws Exception {
        int databaseSizeBeforeUpdate = shoppingListRepository.findAll().size();

        // Create the ShoppingList

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShoppingListMockMvc.perform(put("/api/shopping-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(shoppingList)))
            .andExpect(status().isBadRequest());

        // Validate the ShoppingList in the database
        List<ShoppingList> shoppingListList = shoppingListRepository.findAll();
        assertThat(shoppingListList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ShoppingList in Elasticsearch
        verify(mockShoppingListSearchRepository, times(0)).save(shoppingList);
    }

    @Test
    @Transactional
    public void deleteShoppingList() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);

        int databaseSizeBeforeDelete = shoppingListRepository.findAll().size();

        // Delete the shoppingList
        restShoppingListMockMvc.perform(delete("/api/shopping-lists/{id}", shoppingList.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ShoppingList> shoppingListList = shoppingListRepository.findAll();
        assertThat(shoppingListList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ShoppingList in Elasticsearch
        verify(mockShoppingListSearchRepository, times(1)).deleteById(shoppingList.getId());
    }

    @Test
    @Transactional
    public void searchShoppingList() throws Exception {
        // Initialize the database
        shoppingListRepository.saveAndFlush(shoppingList);
        when(mockShoppingListSearchRepository.search(queryStringQuery("id:" + shoppingList.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(shoppingList), PageRequest.of(0, 1), 1));
        // Search the shoppingList
        restShoppingListMockMvc.perform(get("/api/_search/shopping-lists?query=id:" + shoppingList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shoppingList.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].list").value(hasItem(DEFAULT_LIST.toString())))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShoppingList.class);
        ShoppingList shoppingList1 = new ShoppingList();
        shoppingList1.setId(1L);
        ShoppingList shoppingList2 = new ShoppingList();
        shoppingList2.setId(shoppingList1.getId());
        assertThat(shoppingList1).isEqualTo(shoppingList2);
        shoppingList2.setId(2L);
        assertThat(shoppingList1).isNotEqualTo(shoppingList2);
        shoppingList1.setId(null);
        assertThat(shoppingList1).isNotEqualTo(shoppingList2);
    }
}
