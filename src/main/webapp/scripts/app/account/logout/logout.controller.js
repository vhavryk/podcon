'use strict';

angular.module('podconApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
