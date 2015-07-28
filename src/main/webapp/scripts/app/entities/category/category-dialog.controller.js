'use strict';

angular.module('podconApp').controller('CategoryDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Category', 'Answer_question',
        function($scope, $stateParams, $modalInstance, entity, Category, Answer_question) {

        $scope.category = entity;
        $scope.answer_questions = Answer_question.query();
        $scope.load = function(id) {
            Category.get({id : id}, function(result) {
                $scope.category = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('podconApp:categoryUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.category.id != null) {
                Category.update($scope.category, onSaveFinished);
            } else {
                Category.save($scope.category, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
