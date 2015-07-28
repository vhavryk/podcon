package com.va.podcon.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A Tag.
 */
@Entity
@Table(name = "TAG")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Tag implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "actual_order")
    private Integer actual_order;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Answer_question> answer_questions = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getActual_order() {
        return actual_order;
    }

    public void setActual_order(Integer actual_order) {
        this.actual_order = actual_order;
    }

    public Set<Answer_question> getAnswer_questions() {
        return answer_questions;
    }

    public void setAnswer_questions(Set<Answer_question> answer_questions) {
        this.answer_questions = answer_questions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Tag tag = (Tag) o;

        if ( ! Objects.equals(id, tag.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", actual_order='" + actual_order + "'" +
                '}';
    }
}
