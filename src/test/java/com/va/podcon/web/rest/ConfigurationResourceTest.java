package com.va.podcon.web.rest;

import com.va.podcon.Application;
import com.va.podcon.domain.Configuration;
import com.va.podcon.repository.ConfigurationRepository;

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
 * Test class for the ConfigurationResource REST controller.
 *
 * @see ConfigurationResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ConfigurationResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_VALUE = "SAMPLE_TEXT";
    private static final String UPDATED_VALUE = "UPDATED_TEXT";

    @Inject
    private ConfigurationRepository configurationRepository;

    private MockMvc restConfigurationMockMvc;

    private Configuration configuration;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ConfigurationResource configurationResource = new ConfigurationResource();
        ReflectionTestUtils.setField(configurationResource, "configurationRepository", configurationRepository);
        this.restConfigurationMockMvc = MockMvcBuilders.standaloneSetup(configurationResource).build();
    }

    @Before
    public void initTest() {
        configuration = new Configuration();
        configuration.setName(DEFAULT_NAME);
        configuration.setValue(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void createConfiguration() throws Exception {
        int databaseSizeBeforeCreate = configurationRepository.findAll().size();

        // Create the Configuration
        restConfigurationMockMvc.perform(post("/api/configurations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(configuration)))
                .andExpect(status().isCreated());

        // Validate the Configuration in the database
        List<Configuration> configurations = configurationRepository.findAll();
        assertThat(configurations).hasSize(databaseSizeBeforeCreate + 1);
        Configuration testConfiguration = configurations.get(configurations.size() - 1);
        assertThat(testConfiguration.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testConfiguration.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(configurationRepository.findAll()).hasSize(0);
        // set the field null
        configuration.setName(null);

        // Create the Configuration, which fails.
        restConfigurationMockMvc.perform(post("/api/configurations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(configuration)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Configuration> configurations = configurationRepository.findAll();
        assertThat(configurations).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllConfigurations() throws Exception {
        // Initialize the database
        configurationRepository.saveAndFlush(configuration);

        // Get all the configurations
        restConfigurationMockMvc.perform(get("/api/configurations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(configuration.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())));
    }

    @Test
    @Transactional
    public void getConfiguration() throws Exception {
        // Initialize the database
        configurationRepository.saveAndFlush(configuration);

        // Get the configuration
        restConfigurationMockMvc.perform(get("/api/configurations/{id}", configuration.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(configuration.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingConfiguration() throws Exception {
        // Get the configuration
        restConfigurationMockMvc.perform(get("/api/configurations/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateConfiguration() throws Exception {
        // Initialize the database
        configurationRepository.saveAndFlush(configuration);

		int databaseSizeBeforeUpdate = configurationRepository.findAll().size();

        // Update the configuration
        configuration.setName(UPDATED_NAME);
        configuration.setValue(UPDATED_VALUE);
        restConfigurationMockMvc.perform(put("/api/configurations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(configuration)))
                .andExpect(status().isOk());

        // Validate the Configuration in the database
        List<Configuration> configurations = configurationRepository.findAll();
        assertThat(configurations).hasSize(databaseSizeBeforeUpdate);
        Configuration testConfiguration = configurations.get(configurations.size() - 1);
        assertThat(testConfiguration.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testConfiguration.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void deleteConfiguration() throws Exception {
        // Initialize the database
        configurationRepository.saveAndFlush(configuration);

		int databaseSizeBeforeDelete = configurationRepository.findAll().size();

        // Get the configuration
        restConfigurationMockMvc.perform(delete("/api/configurations/{id}", configuration.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Configuration> configurations = configurationRepository.findAll();
        assertThat(configurations).hasSize(databaseSizeBeforeDelete - 1);
    }
}
