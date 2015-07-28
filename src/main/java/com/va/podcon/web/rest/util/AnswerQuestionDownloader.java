package com.va.podcon.web.rest.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Created by vitalij on 7/26/2015.
 */
public class AnswerQuestionDownloader {

    private AnswerQuestionHandler handler;
    private Map<String, String> coockies;

    public AnswerQuestionDownloader(AnswerQuestionHandler answerQuestionHandler) {
        handler = answerQuestionHandler;
    }

    public static void main1(String[] args) throws IOException {
        AnswerQuestionDownloader downloader = new AnswerQuestionDownloader(new AnswerQuestionHandler() {

            @Override
            public void category(String text) {
                System.out.println(text);
                System.out.println();
            }

            @Override
            public void question(String questionId, String title, String answerText) {
                System.out.println("questionId = [" + questionId + "], title = [" + title + "], answerText = [" + answerText + "]");
            }
        });

        downloader.download();
    }

    public void download() throws IOException {
        Connection connect = getConnection();
        connect.data("t", "getCategoryPath");
        connect.data("catId", "1");
        Document doc = connect.post();

        Elements selects = doc.select("select");
        Element select = selects.get(0);
        Elements options = select.select("option");
        for (int i = 1; i < options.size(); i++) {
            Element option = options.get(i);
            handler.category(option.text());
            coockies = null;
            selectAnswers(option);
            break;
        }
    }

    private Connection getConnection() {
        Connection connect = Jsoup.connect("http://zir.minrd.gov.ua/bz/view");
        connect.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        connect.header("X-Requested-With", "XMLHttpRequest");
        connect.header("Accept-Encoding", "gzip, deflate");
        connect.header("Origin", "http://zir.minrd.gov.ua");
        connect.header("Referer", "http://zir.minrd.gov.ua/main/bz/view/?src=ques");
        connect.header("Accept", "application/xml, text/xml, */*; q=0.01");
        connect.header("Connection", "keep-alive");
        return connect;
    }

    private void selectAnswers(Element option) throws IOException {
        Connection connect = getConnection();
        connect.data("t", "getResultList");
        connect.data("srchWords", "");
        connect.data("wordsVal", "");
        connect.data("catVal", option.val());
        connect.data("hrenVal", "ques");
        connect.data("srcVal", "ques");
        connect.data("statusVal", "1");
        int count = loadQuestions(connect);
        if (count > 0) {
            addQuestions();
        }
    }

    private void addQuestions() throws IOException {
        Connection connect = getConnection();
        connect.data("t", "addToResultList");
        connect.data("srchWords", "");
        int count = loadQuestions(connect);
        if (count > 0) {
            addQuestions();
        }
    }

    private int loadQuestions(Connection connect) throws IOException {
        connect.method(Connection.Method.POST);
        if (coockies != null) {
            connect.cookies(coockies);
            Collection<String> keys = coockies.keySet();
            for (String key : keys) {
                connect.header(key, coockies.get(key));
            }
        }
        Connection.Response response = connect.execute();
        if (response.cookies().size() > 0) {
            coockies = response.cookies();
        }
        Document doc = response.parse();

        Elements content = doc.select("content");
        int count = 0;
        if (content.size() > 0) {
            String text = content.get(0).text();
            String htmlQuestion = StringEscapeUtils.unescapeHtml4(text);

            Document questionsDoc = Jsoup.parse(htmlQuestion);
            Elements questionsBlock = questionsDoc.select("div");
            count = questionsBlock.size();
            for (Element question : questionsBlock) {
                String questionId = question.attr("quesid");
                Elements div = question.select("div");
                if (div.size() > 2) {
                    Element quesdtionTextElement = div.get(3);
                    String title = quesdtionTextElement.attr("title");

                    String answerText = getAnswerText(questionId);
                    handler.question(questionId, title,answerText);
                }
            }

        }

        return count;
    }

    private String getAnswerText(String questionId) throws IOException {
        String answer = null;
        Connection connection = getConnection();
        connection.data("t","getAnswerContent");
        connection.data("id",questionId);
        connection.data("srchWords","");
        connection.data("type","ques");

        Document document = connection.post();
        return document.text();
    }
}
