'use strict';

angular.module('podconApp')
    .controller('AppconfigController', function ($scope, Appconfig, ParseLinks) {
        $scope.appconfigs = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Appconfig.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.appconfigs = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Appconfig.get({id: id}, function(result) {
                $scope.appconfig = result;
                $('#deleteAppconfigConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Appconfig.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteAppconfigConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.appconfig = {name: null, value: null, id: null};
        };
    });
