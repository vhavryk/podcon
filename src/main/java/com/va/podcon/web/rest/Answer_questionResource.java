package com.va.podcon.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.va.podcon.domain.Answer_question;
import com.va.podcon.domain.Category;
import com.va.podcon.domain.Tag;
import com.va.podcon.repository.Answer_questionRepository;
import com.va.podcon.repository.CategoryRepository;
import com.va.podcon.repository.TagRepository;
import com.va.podcon.web.rest.util.AnswerQuestionDownloader;
import com.va.podcon.web.rest.util.AnswerQuestionHandler;
import com.va.podcon.web.rest.util.PaginationUtil;
import org.hibernate.Hibernate;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * REST controller for managing Answer_question.
 */
@RestController
@RequestMapping("/api")
public class Answer_questionResource {

    private final Logger log = LoggerFactory.getLogger(Answer_questionResource.class);

    @Inject
    private Answer_questionRepository answer_questionRepository;

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private TagRepository tagRepository;

    /**
     * POST  /answer_questions -> Create a new answer_question.
     */
    @RequestMapping(value = "/answer_questions",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Answer_question> create(@Valid @RequestBody Answer_question answer_question) throws URISyntaxException {
        log.debug("REST request to save Answer_question : {}", answer_question);
        if (answer_question.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new answer_question cannot already have an ID").body(null);
        }
        Answer_question result = answer_questionRepository.save(answer_question);
        return ResponseEntity.created(new URI("/api/answer_questions/" + answer_question.getId())).body(result);
    }

    /**
     * PUT  /answer_questions -> Updates an existing answer_question.
     */
    @RequestMapping(value = "/answer_questions",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Answer_question> update(@Valid @RequestBody Answer_question answer_question) throws URISyntaxException {
        log.debug("REST request to update Answer_question : {}", answer_question);
        if (answer_question.getId() == null) {
            return create(answer_question);
        }
        Answer_question result = answer_questionRepository.save(answer_question);
        return ResponseEntity.ok().body(result);
    }

    /**
     * GET  /answer_questions -> get all the answer_questions.
     */
    @RequestMapping(value = "/answer_questions",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Answer_question>> getAll(@RequestParam(value = "page", required = false) Integer offset,
                                                        @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Answer_question> page = answer_questionRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/answer_questions", offset, limit);
        return new ResponseEntity<List<Answer_question>>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /answer_questions/:id -> get the "id" answer_question.
     */
    @RequestMapping(value = "/answer_questions/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Answer_question> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Answer_question : {}", id);
        Answer_question answer_question = answer_questionRepository.findOneWithEagerRelationships(id);
        if (answer_question == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(answer_question, HttpStatus.OK);
    }

    /**
     * DELETE  /answer_questions/:id -> delete the "id" answer_question.
     */
    @RequestMapping(value = "/answer_questions/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Answer_question : {}", id);
        answer_questionRepository.delete(id);
    }

    @RequestMapping(value = "/dowanload_answer_questions",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void download() {
        log.debug("REST request to download Answer_question");
        List<Tag> tags = tagRepository.findAll();

        AnswerQuestionDownloader downloader = new AnswerQuestionDownloader(new AnswerQuestionHandler() {

            private Category cat;

            @Override
            public void category(String text) {
                log.debug("New category :" + text);
                cat = categoryRepository.findByName(text);
                if (cat == null) {
                    cat = new Category();
                    cat.setName(text);
                    cat.setActive(Boolean.TRUE);
                    categoryRepository.save(cat);
                }
            }

            @Override
            public void question(String questionId, String title, String answer) {
                Answer_question answerQuestion = answer_questionRepository.findByQuestion(title);
                if (answerQuestion == null) {
                    answerQuestion = new Answer_question();
                    answerQuestion.setActive(Boolean.FALSE);
                    answerQuestion.setCategory(cat);
                    answerQuestion.setQuestion(title);
                    answerQuestion.setAnswer(answer);
                    answerQuestion.setUpdateDate(LocalDate.now());

                }

                answer_questionRepository.save(answerQuestion);
            }
        });
        try {
            downloader.download();
        } catch (IOException e) {
            log.error("REST request to download Answer_question errror", e);
        }
    }


    @RequestMapping(value = "/assigne_tags_to_answer_questions",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void assigneTags() {
        log.debug("REST request assign tags");
        List<Tag> tags = tagRepository.findAll();
        List<Answer_question> answer_questions = answer_questionRepository.findAll();
        for (Answer_question answer_question : answer_questions) {
            for (Tag tag : tags) {
                String[] tagWord = tag.getName().split(" ");
                int count = 0;
                for (String s : tagWord) {
                    if (answer_question.getAnswer().toLowerCase().contains(s.toLowerCase())
                        || answer_question.getQuestion().toLowerCase().contains(s.toLowerCase())) {
                        count++;
                    }
                }
                if (count == tagWord.length) {
                    //Hibernate.initialize(answer_question.getTags());
                    Set<Tag> tagSet = answer_question.getTags();
                    if (tagSet == null) {
                        tagSet = new HashSet<Tag>();
                    }
                    tagSet.add(tag);
                    answer_question.setTags(tagSet);
                }
            }

        }

        for (Answer_question answer_question : answer_questions) {
            answer_questionRepository.save(answer_question);
        }

    }

}
