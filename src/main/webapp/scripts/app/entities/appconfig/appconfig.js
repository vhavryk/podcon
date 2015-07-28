'use strict';

angular.module('podconApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('appconfig', {
                parent: 'entity',
                url: '/appconfigs',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'podconApp.appconfig.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/appconfig/appconfigs.html',
                        controller: 'AppconfigController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('appconfig');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('appconfig.detail', {
                parent: 'entity',
                url: '/appconfig/{id}',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'podconApp.appconfig.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/appconfig/appconfig-detail.html',
                        controller: 'AppconfigDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('appconfig');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Appconfig', function($stateParams, Appconfig) {
                        return Appconfig.get({id : $stateParams.id});
                    }]
                }
            })
            .state('appconfig.new', {
                parent: 'appconfig',
                url: '/new',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/appconfig/appconfig-dialog.html',
                        controller: 'AppconfigDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {name: null, value: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('appconfig', null, { reload: true });
                    }, function() {
                        $state.go('appconfig');
                    })
                }]
            })
            .state('appconfig.edit', {
                parent: 'appconfig',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/appconfig/appconfig-dialog.html',
                        controller: 'AppconfigDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Appconfig', function(Appconfig) {
                                return Appconfig.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('appconfig', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
