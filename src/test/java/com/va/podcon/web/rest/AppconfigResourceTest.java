package com.va.podcon.web.rest;

import com.va.podcon.Application;
import com.va.podcon.domain.Appconfig;
import com.va.podcon.repository.AppconfigRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the AppconfigResource REST controller.
 *
 * @see AppconfigResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class AppconfigResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_VALUE = "SAMPLE_TEXT";
    private static final String UPDATED_VALUE = "UPDATED_TEXT";

    @Inject
    private AppconfigRepository appconfigRepository;

    private MockMvc restAppconfigMockMvc;

    private Appconfig appconfig;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AppconfigResource appconfigResource = new AppconfigResource();
        ReflectionTestUtils.setField(appconfigResource, "appconfigRepository", appconfigRepository);
        this.restAppconfigMockMvc = MockMvcBuilders.standaloneSetup(appconfigResource).build();
    }

    @Before
    public void initTest() {
        appconfig = new Appconfig();
        appconfig.setName(DEFAULT_NAME);
        appconfig.setValue(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void createAppconfig() throws Exception {
        int databaseSizeBeforeCreate = appconfigRepository.findAll().size();

        // Create the Appconfig
        restAppconfigMockMvc.perform(post("/api/appconfigs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(appconfig)))
                .andExpect(status().isCreated());

        // Validate the Appconfig in the database
        List<Appconfig> appconfigs = appconfigRepository.findAll();
        assertThat(appconfigs).hasSize(databaseSizeBeforeCreate + 1);
        Appconfig testAppconfig = appconfigs.get(appconfigs.size() - 1);
        assertThat(testAppconfig.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAppconfig.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = appconfigRepository.findAll().size();
        // set the field null
        appconfig.setName(null);

        // Create the Appconfig, which fails.
        restAppconfigMockMvc.perform(post("/api/appconfigs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(appconfig)))
                .andExpect(status().isBadRequest());

        List<Appconfig> appconfigs = appconfigRepository.findAll();
        assertThat(appconfigs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAppconfigs() throws Exception {
        // Initialize the database
        appconfigRepository.saveAndFlush(appconfig);

        // Get all the appconfigs
        restAppconfigMockMvc.perform(get("/api/appconfigs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(appconfig.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())));
    }

    @Test
    @Transactional
    public void getAppconfig() throws Exception {
        // Initialize the database
        appconfigRepository.saveAndFlush(appconfig);

        // Get the appconfig
        restAppconfigMockMvc.perform(get("/api/appconfigs/{id}", appconfig.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(appconfig.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAppconfig() throws Exception {
        // Get the appconfig
        restAppconfigMockMvc.perform(get("/api/appconfigs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAppconfig() throws Exception {
        // Initialize the database
        appconfigRepository.saveAndFlush(appconfig);

		int databaseSizeBeforeUpdate = appconfigRepository.findAll().size();

        // Update the appconfig
        appconfig.setName(UPDATED_NAME);
        appconfig.setValue(UPDATED_VALUE);
        restAppconfigMockMvc.perform(put("/api/appconfigs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(appconfig)))
                .andExpect(status().isOk());

        // Validate the Appconfig in the database
        List<Appconfig> appconfigs = appconfigRepository.findAll();
        assertThat(appconfigs).hasSize(databaseSizeBeforeUpdate);
        Appconfig testAppconfig = appconfigs.get(appconfigs.size() - 1);
        assertThat(testAppconfig.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAppconfig.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void deleteAppconfig() throws Exception {
        // Initialize the database
        appconfigRepository.saveAndFlush(appconfig);

		int databaseSizeBeforeDelete = appconfigRepository.findAll().size();

        // Get the appconfig
        restAppconfigMockMvc.perform(delete("/api/appconfigs/{id}", appconfig.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Appconfig> appconfigs = appconfigRepository.findAll();
        assertThat(appconfigs).hasSize(databaseSizeBeforeDelete - 1);
    }
}
