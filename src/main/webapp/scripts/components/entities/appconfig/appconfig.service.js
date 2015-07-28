'use strict';

angular.module('podconApp')
    .factory('Appconfig', function ($resource, DateUtils) {
        return $resource('api/appconfigs/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
