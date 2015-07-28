package com.va.podcon.repository;

import com.va.podcon.domain.Answer_question;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Answer_question entity.
 */
public interface Answer_questionRepository extends JpaRepository<Answer_question,Long> {

    @Query("select answer_question from Answer_question answer_question left join fetch answer_question.tags where answer_question.id =:id")
    Answer_question findOneWithEagerRelationships(@Param("id") Long id);

    Answer_question findByQuestion(String question);
}
