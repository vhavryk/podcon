package com.va.podcon.web.rest;

import com.va.podcon.Application;
import com.va.podcon.domain.Answer_question;
import com.va.podcon.repository.Answer_questionRepository;

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
import org.joda.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the Answer_questionResource REST controller.
 *
 * @see Answer_questionResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class Answer_questionResourceTest {

    private static final String DEFAULT_QUESTION = "SAMPLE_TEXT";
    private static final String UPDATED_QUESTION = "UPDATED_TEXT";
    private static final String DEFAULT_ANSWER = "SAMPLE_TEXT";
    private static final String UPDATED_ANSWER = "UPDATED_TEXT";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Integer DEFAULT_ACTUAL_ORDER = 0;
    private static final Integer UPDATED_ACTUAL_ORDER = 1;
    private static final String DEFAULT_USER_EMAIL = "SAMPLE_TEXT";
    private static final String UPDATED_USER_EMAIL = "UPDATED_TEXT";

    private static final LocalDate DEFAULT_UPDATE_DATE = new LocalDate(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = new LocalDate();

    @Inject
    private Answer_questionRepository answer_questionRepository;

    private MockMvc restAnswer_questionMockMvc;

    private Answer_question answer_question;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Answer_questionResource answer_questionResource = new Answer_questionResource();
        ReflectionTestUtils.setField(answer_questionResource, "answer_questionRepository", answer_questionRepository);
        this.restAnswer_questionMockMvc = MockMvcBuilders.standaloneSetup(answer_questionResource).build();
    }

    @Before
    public void initTest() {
        answer_question = new Answer_question();
        answer_question.setQuestion(DEFAULT_QUESTION);
        answer_question.setAnswer(DEFAULT_ANSWER);
        answer_question.setActive(DEFAULT_ACTIVE);
        answer_question.setActual_order(DEFAULT_ACTUAL_ORDER);
        answer_question.setUser_email(DEFAULT_USER_EMAIL);
        answer_question.setUpdateDate(DEFAULT_UPDATE_DATE);
    }

    @Test
    @Transactional
    public void createAnswer_question() throws Exception {
        int databaseSizeBeforeCreate = answer_questionRepository.findAll().size();

        // Create the Answer_question
        restAnswer_questionMockMvc.perform(post("/api/answer_questions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(answer_question)))
                .andExpect(status().isCreated());

        // Validate the Answer_question in the database
        List<Answer_question> answer_questions = answer_questionRepository.findAll();
        assertThat(answer_questions).hasSize(databaseSizeBeforeCreate + 1);
        Answer_question testAnswer_question = answer_questions.get(answer_questions.size() - 1);
        assertThat(testAnswer_question.getQuestion()).isEqualTo(DEFAULT_QUESTION);
        assertThat(testAnswer_question.getAnswer()).isEqualTo(DEFAULT_ANSWER);
        assertThat(testAnswer_question.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testAnswer_question.getActual_order()).isEqualTo(DEFAULT_ACTUAL_ORDER);
        assertThat(testAnswer_question.getUser_email()).isEqualTo(DEFAULT_USER_EMAIL);
        assertThat(testAnswer_question.getUpdateDate()).isEqualTo(DEFAULT_UPDATE_DATE);
    }

    @Test
    @Transactional
    public void checkQuestionIsRequired() throws Exception {
        int databaseSizeBeforeTest = answer_questionRepository.findAll().size();
        // set the field null
        answer_question.setQuestion(null);

        // Create the Answer_question, which fails.
        restAnswer_questionMockMvc.perform(post("/api/answer_questions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(answer_question)))
                .andExpect(status().isBadRequest());

        List<Answer_question> answer_questions = answer_questionRepository.findAll();
        assertThat(answer_questions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAnswerIsRequired() throws Exception {
        int databaseSizeBeforeTest = answer_questionRepository.findAll().size();
        // set the field null
        answer_question.setAnswer(null);

        // Create the Answer_question, which fails.
        restAnswer_questionMockMvc.perform(post("/api/answer_questions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(answer_question)))
                .andExpect(status().isBadRequest());

        List<Answer_question> answer_questions = answer_questionRepository.findAll();
        assertThat(answer_questions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAnswer_questions() throws Exception {
        // Initialize the database
        answer_questionRepository.saveAndFlush(answer_question);

        // Get all the answer_questions
        restAnswer_questionMockMvc.perform(get("/api/answer_questions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(answer_question.getId().intValue())))
                .andExpect(jsonPath("$.[*].question").value(hasItem(DEFAULT_QUESTION.toString())))
                .andExpect(jsonPath("$.[*].answer").value(hasItem(DEFAULT_ANSWER.toString())))
                .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
                .andExpect(jsonPath("$.[*].actual_order").value(hasItem(DEFAULT_ACTUAL_ORDER)))
                .andExpect(jsonPath("$.[*].user_email").value(hasItem(DEFAULT_USER_EMAIL.toString())))
                .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())));
    }

    @Test
    @Transactional
    public void getAnswer_question() throws Exception {
        // Initialize the database
        answer_questionRepository.saveAndFlush(answer_question);

        // Get the answer_question
        restAnswer_questionMockMvc.perform(get("/api/answer_questions/{id}", answer_question.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(answer_question.getId().intValue()))
            .andExpect(jsonPath("$.question").value(DEFAULT_QUESTION.toString()))
            .andExpect(jsonPath("$.answer").value(DEFAULT_ANSWER.toString()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.actual_order").value(DEFAULT_ACTUAL_ORDER))
            .andExpect(jsonPath("$.user_email").value(DEFAULT_USER_EMAIL.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAnswer_question() throws Exception {
        // Get the answer_question
        restAnswer_questionMockMvc.perform(get("/api/answer_questions/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAnswer_question() throws Exception {
        // Initialize the database
        answer_questionRepository.saveAndFlush(answer_question);

		int databaseSizeBeforeUpdate = answer_questionRepository.findAll().size();

        // Update the answer_question
        answer_question.setQuestion(UPDATED_QUESTION);
        answer_question.setAnswer(UPDATED_ANSWER);
        answer_question.setActive(UPDATED_ACTIVE);
        answer_question.setActual_order(UPDATED_ACTUAL_ORDER);
        answer_question.setUser_email(UPDATED_USER_EMAIL);
        answer_question.setUpdateDate(UPDATED_UPDATE_DATE);
        restAnswer_questionMockMvc.perform(put("/api/answer_questions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(answer_question)))
                .andExpect(status().isOk());

        // Validate the Answer_question in the database
        List<Answer_question> answer_questions = answer_questionRepository.findAll();
        assertThat(answer_questions).hasSize(databaseSizeBeforeUpdate);
        Answer_question testAnswer_question = answer_questions.get(answer_questions.size() - 1);
        assertThat(testAnswer_question.getQuestion()).isEqualTo(UPDATED_QUESTION);
        assertThat(testAnswer_question.getAnswer()).isEqualTo(UPDATED_ANSWER);
        assertThat(testAnswer_question.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testAnswer_question.getActual_order()).isEqualTo(UPDATED_ACTUAL_ORDER);
        assertThat(testAnswer_question.getUser_email()).isEqualTo(UPDATED_USER_EMAIL);
        assertThat(testAnswer_question.getUpdateDate()).isEqualTo(UPDATED_UPDATE_DATE);
    }

    @Test
    @Transactional
    public void deleteAnswer_question() throws Exception {
        // Initialize the database
        answer_questionRepository.saveAndFlush(answer_question);

		int databaseSizeBeforeDelete = answer_questionRepository.findAll().size();

        // Get the answer_question
        restAnswer_questionMockMvc.perform(delete("/api/answer_questions/{id}", answer_question.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Answer_question> answer_questions = answer_questionRepository.findAll();
        assertThat(answer_questions).hasSize(databaseSizeBeforeDelete - 1);
    }
}
