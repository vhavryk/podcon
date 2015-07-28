'use strict';

angular.module('podconApp')
    .factory('Answer_question', function ($resource, DateUtils) {
        return $resource('api/answer_questions/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.updateDate = DateUtils.convertLocaleDateFromServer(data.updateDate);
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.updateDate = DateUtils.convertLocaleDateToServer(data.updateDate);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.updateDate = DateUtils.convertLocaleDateToServer(data.updateDate);
                    return angular.toJson(data);
                }
            }
        });
    });
