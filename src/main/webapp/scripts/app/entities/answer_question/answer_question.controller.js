'use strict';

angular.module('podconApp')
    .controller('Answer_questionController', function ($scope, Answer_question, ParseLinks) {
        $scope.answer_questions = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Answer_question.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.answer_questions = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Answer_question.get({id: id}, function(result) {
                $scope.answer_question = result;
                $('#deleteAnswer_questionConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Answer_question.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteAnswer_questionConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.answer_question = {question: null, answer: null, active: null, actual_order: null, user_email: null, updateDate: null, id: null};
        };
    });
