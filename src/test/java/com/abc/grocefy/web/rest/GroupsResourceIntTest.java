package com.abc.grocefy.web.rest;

import com.abc.grocefy.GrocefyApp;

import com.abc.grocefy.domain.Groups;
import com.abc.grocefy.repository.GroupsRepository;
import com.abc.grocefy.repository.search.GroupsSearchRepository;
import com.abc.grocefy.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import static com.abc.grocefy.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.abc.grocefy.domain.enumeration.Status;
/**
 * Test class for the GroupsResource REST controller.
 *
 * @see GroupsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrocefyApp.class)
public class GroupsResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.ACTIVE;
    private static final Status UPDATED_STATUS = Status.INACTIVE;

    private static final String DEFAULT_USER_LIST = "AAAAAAAAAA";
    private static final String UPDATED_USER_LIST = "BBBBBBBBBB";

    @Autowired
    private GroupsRepository groupsRepository;

    @Mock
    private GroupsRepository groupsRepositoryMock;

    /**
     * This repository is mocked in the com.abc.grocefy.repository.search test package.
     *
     * @see com.abc.grocefy.repository.search.GroupsSearchRepositoryMockConfiguration
     */
    @Autowired
    private GroupsSearchRepository mockGroupsSearchRepository;

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

    private MockMvc restGroupsMockMvc;

    private Groups groups;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final GroupsResource groupsResource = new GroupsResource(groupsRepository, mockGroupsSearchRepository);
        this.restGroupsMockMvc = MockMvcBuilders.standaloneSetup(groupsResource)
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
    public static Groups createEntity(EntityManager em) {
        Groups groups = new Groups()
            .title(DEFAULT_TITLE)
            .status(DEFAULT_STATUS)
            .userList(DEFAULT_USER_LIST);
        return groups;
    }

    @Before
    public void initTest() {
        groups = createEntity(em);
    }

    @Test
    @Transactional
    public void createGroups() throws Exception {
        int databaseSizeBeforeCreate = groupsRepository.findAll().size();

        // Create the Groups
        restGroupsMockMvc.perform(post("/api/groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(groups)))
            .andExpect(status().isCreated());

        // Validate the Groups in the database
        List<Groups> groupsList = groupsRepository.findAll();
        assertThat(groupsList).hasSize(databaseSizeBeforeCreate + 1);
        Groups testGroups = groupsList.get(groupsList.size() - 1);
        assertThat(testGroups.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testGroups.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testGroups.getUserList()).isEqualTo(DEFAULT_USER_LIST);

        // Validate the Groups in Elasticsearch
        verify(mockGroupsSearchRepository, times(1)).save(testGroups);
    }

    @Test
    @Transactional
    public void createGroupsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = groupsRepository.findAll().size();

        // Create the Groups with an existing ID
        groups.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restGroupsMockMvc.perform(post("/api/groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(groups)))
            .andExpect(status().isBadRequest());

        // Validate the Groups in the database
        List<Groups> groupsList = groupsRepository.findAll();
        assertThat(groupsList).hasSize(databaseSizeBeforeCreate);

        // Validate the Groups in Elasticsearch
        verify(mockGroupsSearchRepository, times(0)).save(groups);
    }

    @Test
    @Transactional
    public void getAllGroups() throws Exception {
        // Initialize the database
        groupsRepository.saveAndFlush(groups);

        // Get all the groupsList
        restGroupsMockMvc.perform(get("/api/groups?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(groups.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].userList").value(hasItem(DEFAULT_USER_LIST.toString())));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllGroupsWithEagerRelationshipsIsEnabled() throws Exception {
        GroupsResource groupsResource = new GroupsResource(groupsRepositoryMock, mockGroupsSearchRepository);
        when(groupsRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restGroupsMockMvc = MockMvcBuilders.standaloneSetup(groupsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restGroupsMockMvc.perform(get("/api/groups?eagerload=true"))
        .andExpect(status().isOk());

        verify(groupsRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllGroupsWithEagerRelationshipsIsNotEnabled() throws Exception {
        GroupsResource groupsResource = new GroupsResource(groupsRepositoryMock, mockGroupsSearchRepository);
            when(groupsRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restGroupsMockMvc = MockMvcBuilders.standaloneSetup(groupsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restGroupsMockMvc.perform(get("/api/groups?eagerload=true"))
        .andExpect(status().isOk());

            verify(groupsRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getGroups() throws Exception {
        // Initialize the database
        groupsRepository.saveAndFlush(groups);

        // Get the groups
        restGroupsMockMvc.perform(get("/api/groups/{id}", groups.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(groups.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.userList").value(DEFAULT_USER_LIST.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingGroups() throws Exception {
        // Get the groups
        restGroupsMockMvc.perform(get("/api/groups/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGroups() throws Exception {
        // Initialize the database
        groupsRepository.saveAndFlush(groups);

        int databaseSizeBeforeUpdate = groupsRepository.findAll().size();

        // Update the groups
        Groups updatedGroups = groupsRepository.findById(groups.getId()).get();
        // Disconnect from session so that the updates on updatedGroups are not directly saved in db
        em.detach(updatedGroups);
        updatedGroups
            .title(UPDATED_TITLE)
            .status(UPDATED_STATUS)
            .userList(UPDATED_USER_LIST);

        restGroupsMockMvc.perform(put("/api/groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedGroups)))
            .andExpect(status().isOk());

        // Validate the Groups in the database
        List<Groups> groupsList = groupsRepository.findAll();
        assertThat(groupsList).hasSize(databaseSizeBeforeUpdate);
        Groups testGroups = groupsList.get(groupsList.size() - 1);
        assertThat(testGroups.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testGroups.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testGroups.getUserList()).isEqualTo(UPDATED_USER_LIST);

        // Validate the Groups in Elasticsearch
        verify(mockGroupsSearchRepository, times(1)).save(testGroups);
    }

    @Test
    @Transactional
    public void updateNonExistingGroups() throws Exception {
        int databaseSizeBeforeUpdate = groupsRepository.findAll().size();

        // Create the Groups

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGroupsMockMvc.perform(put("/api/groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(groups)))
            .andExpect(status().isBadRequest());

        // Validate the Groups in the database
        List<Groups> groupsList = groupsRepository.findAll();
        assertThat(groupsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Groups in Elasticsearch
        verify(mockGroupsSearchRepository, times(0)).save(groups);
    }

    @Test
    @Transactional
    public void deleteGroups() throws Exception {
        // Initialize the database
        groupsRepository.saveAndFlush(groups);

        int databaseSizeBeforeDelete = groupsRepository.findAll().size();

        // Delete the groups
        restGroupsMockMvc.perform(delete("/api/groups/{id}", groups.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Groups> groupsList = groupsRepository.findAll();
        assertThat(groupsList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Groups in Elasticsearch
        verify(mockGroupsSearchRepository, times(1)).deleteById(groups.getId());
    }

    @Test
    @Transactional
    public void searchGroups() throws Exception {
        // Initialize the database
        groupsRepository.saveAndFlush(groups);
        when(mockGroupsSearchRepository.search(queryStringQuery("id:" + groups.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(groups), PageRequest.of(0, 1), 1));
        // Search the groups
        restGroupsMockMvc.perform(get("/api/_search/groups?query=id:" + groups.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(groups.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].userList").value(hasItem(DEFAULT_USER_LIST.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Groups.class);
        Groups groups1 = new Groups();
        groups1.setId(1L);
        Groups groups2 = new Groups();
        groups2.setId(groups1.getId());
        assertThat(groups1).isEqualTo(groups2);
        groups2.setId(2L);
        assertThat(groups1).isNotEqualTo(groups2);
        groups1.setId(null);
        assertThat(groups1).isNotEqualTo(groups2);
    }
}
