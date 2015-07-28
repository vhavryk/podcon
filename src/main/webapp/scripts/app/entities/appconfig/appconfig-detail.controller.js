'use strict';

angular.module('podconApp')
    .controller('AppconfigDetailController', function ($scope, $rootScope, $stateParams, entity, Appconfig) {
        $scope.appconfig = entity;
        $scope.load = function (id) {
            Appconfig.get({id: id}, function(result) {
                $scope.appconfig = result;
            });
        };
        $rootScope.$on('podconApp:appconfigUpdate', function(event, result) {
            $scope.appconfig = result;
        });
    });
