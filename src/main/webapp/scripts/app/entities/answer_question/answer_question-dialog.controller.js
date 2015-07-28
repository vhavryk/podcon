'use strict';

angular.module('podconApp').controller('Answer_questionDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Answer_question', 'Category', 'Tag',
        function($scope, $stateParams, $modalInstance, entity, Answer_question, Category, Tag) {

        $scope.answer_question = entity;
        $scope.categorys = Category.query();
        $scope.tags = Tag.query();
        $scope.load = function(id) {
            Answer_question.get({id : id}, function(result) {
                $scope.answer_question = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('podconApp:answer_questionUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.answer_question.id != null) {
                Answer_question.update($scope.answer_question, onSaveFinished);
            } else {
                Answer_question.save($scope.answer_question, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
