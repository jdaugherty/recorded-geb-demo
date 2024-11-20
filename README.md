Demonstration of Test Recording use ContainerGebSpec
---
To enable video recording of tests, `afterTest()` must be called after each test finish.  However, the state of the test (fail/success) can only be known by using a SpockExtension.  Thus, this project adds a spock extension - `ContainerGebRecordingExtension` - to add the associated listeners to determine if a test is successful or a failure.  This information is then passed to the webDriverContainer's `afterTest()` method so recordings can be saved.

The downside of this approach is each test creates a test container instead of using 1 test container for the life of the test run.  If we were to move the container bootstrapping to the extension, we could significantly speed up the runtime of these tests.  This would require moving configuration values from instance methods to annotations because of how spock is set up.  i.e. if you want a different hostname, you'd add an annotation to set the host name on the test and then when processing that spec you could start a different container if the hostname is different from the running one.

After running tests, look in `app1/build/recordings` for the failed recordings.

Attribution
===
Tests based on the [Grails Functional Test Repository](https://github.com/grails/grails-functional-tests).

Test Container functionality with Geb based on the [Grails Geb Plugin](https://github.com/grails/geb).

