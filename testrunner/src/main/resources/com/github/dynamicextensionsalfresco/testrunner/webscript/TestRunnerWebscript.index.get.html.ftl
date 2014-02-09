<!DOCTYPE html>
<html lang="en" ng-app="testrunner">
<head>
	<#assign resources = url.serviceContext + "/testrunner/resources">
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Integration test results</title>
    <link rel="stylesheet" href="${resources}/css/bootstrap.min.css">
    <style type="text/css">
		body {
			padding: 10px;
		}
        span.failures {
            border: 1px solid red;
            padding: 3px;
        }
        ul.failures li p {
            color: gray;
        }
        ul.methodlist {
            display: inline-block;
            margin: 0;
        }
        ul.methodlist li {
            list-style: none;
            padding: 0 5px;
            border-bottom: 1px solid gray;
        }
        ul.methodlist li:last-child {
            border-bottom: none;
        }
		table.table {
			width: auto;
		}
		form {
			margin: 10px;
		}
		.className {
			font-size: 15px;
			margin-bottom: 10px;
			display: inline-block;
		}
    </style>
    <script type="text/javascript" src="${resources}/js/angular.min.js"></script>
    <script type="text/javascript">
        angular.module('testrunner', [])
                .controller('tests', function($scope,$http) {
					$scope.testFilter = {};
                    $http.get('/alfresco/service/testrunner/tests').success(function(tests) {
                        $scope.tests = tests;
                    });
                    $scope.runTests = function() {
                        $scope.runningTests = true;
                        $scope.testResults = null;
                        $scope.error = null;

                        $http.post('/alfresco/service/testrunner/run', $scope.testFilter).success(function(testResults) {
                            $scope.testResults = testResults;
                            $scope.runningTests = false;
                        }).error(function(error) {
							$scope.error = error;
							$scope.runningTests = false;
						});
                    };
                })
                .filter('checkmark', function() {
                    return function(input) {
                        return input ? '\u2713' : '\u2718';
                    };
                });
    </script>
</head>
<body ng-controller="tests">
<fieldset>
	<legend>Available tests</legend>
    <table class="table striped">
        <thead>
        <tr>
            <th>Bundle</th><th>Test class</th>
        </tr>
        </thead>
        <tbody>
        <tr ng-repeat="test in tests | filter:testFilter">
            <td>{{ test.bundleName }}</td><td>{{ test.className }}</td>
        </tr>
        </tbody>
    </table>

    <form ng-submit="runTests()">
        Only run tests matching <input ng-model="testFilter.className" type="search"/>
        from bundle <input ng-model="testFilter.bundleName" type="search"/>
        <input class="btn btn-primary" type="submit" value="run" ng-disabled="runningTests">
    </form>
</fieldset>

<fieldset class="results">
	<legend>Execution</legend>

    <p ng-show="runningTests">running integration tests</p>

    <pre ng-show="error" class="failure">{{error | json}}</pre>
    <div class="test" ng-repeat="test in testResults">
        <span class="label label-info className">{{test.bundleName}}: {{test.className}}</span>
        <p>Methods:</p>
        <ul class="methodlist">
            <li ng-repeat="method in test.methods">
                <p>{{method.method}} {{method.failures.length == 0 | checkmark}}</p>
                <p class="failures" ng-show="method.failures">Failures:</p>
                <ul class="failurelist">
                    <li ng-repeat="failure in method.failures">
                        <h2>{{failure.description}}</h2>
                        <p>{{failure.message}}</p>
                    <pre>
                        {{failure.trace}}
                    </pre>
                    </li>
                </ul>
            </li>
        </ul>
        <p>Executed
            <ng-pluralize count="test.summary.runcount"
                          when="{'0': 'no tests',
                     'one': 'one test',
                     'other': '{} tests'}">
            </ng-pluralize>
            <span class="failures" ng-show="test.failurecount">: {{test.summary.failurecount}} failures</span>
            <span class="ignored" ng-show="test.ignorecount">(ignored {{test.summary.ignorecount}} tests)</span>
            in {{test.summary.runtime}} milliseconds.
        </p>
    </div>
</fieldset>
</body>
</html>