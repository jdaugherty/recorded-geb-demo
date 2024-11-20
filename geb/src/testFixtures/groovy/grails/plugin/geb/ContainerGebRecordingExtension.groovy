package grails.plugin.geb

import groovy.transform.TailRecursive
import groovy.util.logging.Slf4j
import org.spockframework.runtime.AbstractRunListener
import org.spockframework.runtime.extension.IGlobalExtension
import org.spockframework.runtime.model.ErrorInfo
import org.spockframework.runtime.model.IterationInfo
import org.spockframework.runtime.model.SpecInfo
import org.testcontainers.containers.BrowserWebDriverContainer

import java.time.LocalDateTime

@Slf4j
class ContainerGebRecordingExtension implements IGlobalExtension {
    ContainerGebConfiguration configuration

    @Override
    void start() {
        configuration = new ContainerGebConfiguration()
    }

    @Override
    void visitSpec(SpecInfo spec) {
        if (isContainerizedGebSpec(spec)) {
            ContainerGebTestListener listener = new ContainerGebTestListener(spec, LocalDateTime.now())
            // TODO: ideally, we would initialize the web driver container once for all geb tests so we don't have to spin it up & down.
            spec.addSetupInterceptor {
                ContainerGebSpec gebSpec = it.instance as ContainerGebSpec
                gebSpec.initialize()
                if(configuration.recording) {
                    listener.webDriverContainer = gebSpec.webDriverContainer.withRecordingMode(configuration.recordingMode, configuration.recordingDirectory, configuration.recordingFormat)
                }
            }

            spec.addListener(listener)
        }
    }

    @TailRecursive
    boolean isContainerizedGebSpec(SpecInfo spec) {
        if(spec != null) {
            if(spec.filename.startsWith('ContainerGebSpec.')) {
                return true
            }

            return isContainerizedGebSpec(spec.superSpec)
        }
        return false
    }
}

class ContainerGebTestListener extends AbstractRunListener {
    BrowserWebDriverContainer webDriverContainer
    ErrorInfo errorInfo
    SpecInfo spec
    LocalDateTime runDate

    ContainerGebTestListener(SpecInfo spec, LocalDateTime runDate) {
        this.spec = spec
        this.runDate = runDate
    }

    @Override
    void afterIteration(IterationInfo iteration) {
        webDriverContainer.afterTest(new ContainerGebTestDescription(iteration, runDate), Optional.of(errorInfo?.exception))
        errorInfo = null
    }

    @Override
    void error(ErrorInfo error) {
        errorInfo = error
    }
}