'use strict';

angular.module('podconApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('answer_question', {
                parent: 'entity',
                url: '/answer_questions',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'podconApp.answer_question.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/answer_question/answer_questions.html',
                        controller: 'Answer_questionController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('answer_question');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('answer_question.detail', {
                parent: 'entity',
                url: '/answer_question/{id}',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'podconApp.answer_question.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/answer_question/answer_question-detail.html',
                        controller: 'Answer_questionDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('answer_question');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Answer_question', function($stateParams, Answer_question) {
                        return Answer_question.get({id : $stateParams.id});
                    }]
                }
            })
            .state('answer_question.new', {
                parent: 'answer_question',
                url: '/new',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/answer_question/answer_question-dialog.html',
                        controller: 'Answer_questionDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {question: null, answer: null, active: null, actual_order: null, user_email: null, updateDate: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('answer_question', null, { reload: true });
                    }, function() {
                        $state.go('answer_question');
                    })
                }]
            })
            .state('answer_question.edit', {
                parent: 'answer_question',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/answer_question/answer_question-dialog.html',
                        controller: 'Answer_questionDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Answer_question', function(Answer_question) {
                                return Answer_question.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('answer_question', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
