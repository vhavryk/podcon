'use strict';

angular.module('podconApp')
    .controller('CategoryDetailController', function ($scope, $rootScope, $stateParams, entity, Category, Answer_question) {
        $scope.category = entity;
        $scope.load = function (id) {
            Category.get({id: id}, function(result) {
                $scope.category = result;
            });
        };
        $rootScope.$on('podconApp:categoryUpdate', function(event, result) {
            $scope.category = result;
        });
    });
