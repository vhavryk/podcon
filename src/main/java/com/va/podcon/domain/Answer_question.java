package com.va.podcon.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.va.podcon.domain.util.CustomLocalDateSerializer;
import com.va.podcon.domain.util.ISO8601LocalDateDeserializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A Answer_question.
 */
@Entity
@Table(name = "ANSWER_QUESTION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Answer_question implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "question", nullable = false)
    private String question;

    @NotNull
    @Column(name = "answer", nullable = false)
    private String answer;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "actual_order")
    private Integer actual_order;

    @Size(max = 255)
    @Column(name = "user_email", length = 255)
    private String user_email;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "update_date")
    private LocalDate updateDate;

    @ManyToOne
    private Category category;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "ANSWER_QUESTION_TAG",
               joinColumns = @JoinColumn(name="answer_questions_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="tags_id", referencedColumnName="ID"))
    private Set<Tag> tags = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getActual_order() {
        return actual_order;
    }

    public void setActual_order(Integer actual_order) {
        this.actual_order = actual_order;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Answer_question answer_question = (Answer_question) o;

        if ( ! Objects.equals(id, answer_question.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Answer_question{" +
                "id=" + id +
                ", question='" + question + "'" +
                ", answer='" + answer + "'" +
                ", active='" + active + "'" +
                ", actual_order='" + actual_order + "'" +
                ", user_email='" + user_email + "'" +
                ", updateDate='" + updateDate + "'" +
                '}';
    }
}
