'use strict';

angular.module('podconApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


