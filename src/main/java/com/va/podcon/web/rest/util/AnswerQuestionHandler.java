package com.va.podcon.web.rest.util;

/**
 * Created by vitalij on 7/26/2015.
 */
public interface AnswerQuestionHandler {
    void category(String text);

    void question(String questionId, String title,String answer);
}
