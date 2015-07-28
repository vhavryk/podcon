'use strict';

angular.module('podconApp').controller('TagDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Tag', 'Answer_question',
        function($scope, $stateParams, $modalInstance, entity, Tag, Answer_question) {

        $scope.tag = entity;
        $scope.answer_questions = Answer_question.query();
        $scope.load = function(id) {
            Tag.get({id : id}, function(result) {
                $scope.tag = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('podconApp:tagUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.tag.id != null) {
                Tag.update($scope.tag, onSaveFinished);
            } else {
                Tag.save($scope.tag, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
