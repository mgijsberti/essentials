/*
 * Copyright 2014 Hippo B.V. (http://www.onehippo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

(function () {
    "use strict";
    angular.module('hippo.essentials')
        .controller('CKEditorPluginsCtrl', function ($scope, $filter, $sce, $log, $modal, $rootScope, $http) {
            var endpoint = $rootScope.REST.dynamic + 'ckeditor-plugins';
            $scope.pluginId = "CKEditorPlugins";
            $scope.fieldsAdded = false;
            $scope.addDocs = function () {
                var documents = [];
                angular.forEach($scope.documentTypes, function (value) {
                    if (value.checked) {
                        documents.push(value.name);
                    }
                });
                var payload = Essentials.addPayloadData("documents", documents.join(','), null);
                $http.post(endpoint, payload).success(function (data) {
                    $scope.fieldsAdded = true;
                });

            };

            //############################################
            // INIT
            //############################################
            $http.get($rootScope.REST.root + "/plugins/plugins/" + $scope.pluginId).success(function (plugin) {
                $scope.plugin = plugin;
            });

            $http.get($rootScope.REST.documents).success(function (docTypes) {
                // Filter out basedocument
                $scope.documentTypes = [];
                angular.forEach(docTypes, function(docType) {
                    if (docType.name !== 'basedocument' ) {
                        $scope.documentTypes.push(docType);
                    }
                });
            });
        })
})();

