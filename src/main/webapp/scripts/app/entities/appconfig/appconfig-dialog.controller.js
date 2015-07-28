'use strict';

angular.module('podconApp').controller('AppconfigDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Appconfig',
        function($scope, $stateParams, $modalInstance, entity, Appconfig) {

        $scope.appconfig = entity;
        $scope.load = function(id) {
            Appconfig.get({id : id}, function(result) {
                $scope.appconfig = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('podconApp:appconfigUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.appconfig.id != null) {
                Appconfig.update($scope.appconfig, onSaveFinished);
            } else {
                Appconfig.save($scope.appconfig, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
