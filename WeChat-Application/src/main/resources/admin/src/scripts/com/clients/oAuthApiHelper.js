admin.app.factory('oAuthApiHelper', ['$q', 'apiHelper', 'oAuthClient', function ($q, apiHelper, OAuthClient) {
    var addAccessToken = function (url, access_token) {
        return apiHelper.addParameterToURL(url, "access_token", access_token);
    };

    var OAuthApiHelper = {};

    OAuthApiHelper.get = function (access_token, refresh_token, url) {
        var deferred = $q.defer();

        var urlWithAccessToken = addAccessToken(url, access_token);
        apiHelper.get(urlWithAccessToken).then(
            function (data) {
                deferred.resolve({
                    data: data,
                    access_token: ''
                });
            },
            function (ex) {
                if (ex === 403) {
                    OAuthClient.refreshAccessToken(access_token, refresh_token).then(
                        function (accessToken) {
                            var urlWithNewAccessToken = addAccessToken(url, accessToken);
                            apiHelper.get(urlWithNewAccessToken).then(
                                function (data) {
                                    deferred.resolve({
                                        data: data,
                                        access_token: accessToken
                                    });
                                },
                                function (error) {
                                    deferred.reject({
                                        error: error,
                                        access_token: accessToken
                                    });
                                }
                            );
                        },
                        function (error) {
                            deferred.reject(error);
                        }
                    )
                }
                else {
                    deferred.reject(ex)
                }
            }
        );

        return deferred.promise;
    };

    OAuthApiHelper.post = function (access_token, refresh_token, url, data) {
        var deferred = $q.defer();

        var urlWithAccessToken = addAccessToken(url, access_token);
        apiHelper.post(urlWithAccessToken, data).then(
            function (data, status, headers, config) {
                deferred.resolve({
                    data: data,
                    access_token: ''
                });
            },
            function (ex) {
                if (ex === 403) {
                    OAuthClient.refreshAccessToken(access_token, refresh_token).then(
                        function (accessToken) {
                            var urlWithNewAccessToken = addAccessToken(url, accessToken);
                            apiHelper.post(urlWithNewAccessToken, data).then(
                                function (data, status, headers, config) {
                                    deferred.resolve({
                                        data: data,
                                        access_token: accessToken
                                    });
                                },
                                function (error) {
                                    deferred.reject({
                                        error: error,
                                        access_token: accessToken
                                    });

                                }
                            );
                        },
                        function (error) {
                            deferred.reject(error);
                        }
                    )
                }
                else {
                    deferred.reject(ex)
                }
            }
        );

        return deferred.promise;
    };

    return OAuthApiHelper;
}]);