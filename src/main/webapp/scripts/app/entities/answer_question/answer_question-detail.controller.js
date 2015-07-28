'use strict';

angular.module('podconApp')
    .controller('Answer_questionDetailController', function ($scope, $rootScope, $stateParams, entity, Answer_question, Category, Tag) {
        $scope.answer_question = entity;
        $scope.load = function (id) {
            Answer_question.get({id: id}, function(result) {
                $scope.answer_question = result;
            });
        };
        $rootScope.$on('podconApp:answer_questionUpdate', function(event, result) {
            $scope.answer_question = result;
        });
    });
