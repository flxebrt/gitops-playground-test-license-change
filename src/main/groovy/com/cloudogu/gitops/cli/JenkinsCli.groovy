package com.cloudogu.gitops.cli

import com.cloudogu.gitops.dependencyinjection.Factory
import com.cloudogu.gitops.jenkins.UserManager
import groovy.util.logging.Slf4j
import io.micronaut.context.ApplicationContext
import picocli.CommandLine

@CommandLine.Command(name = 'jenkins', description = 'CLI-tool to configure jenkins.',
        mixinStandardHelpOptions = true)
@Slf4j
class JenkinsCli {
    @CommandLine.Command(name = "add-user", description = "adds user to jenkins", mixinStandardHelpOptions = true)
    void addUser(
            @CommandLine.Parameters(paramLabel = "username", description = "The username to create") String username,
            @CommandLine.Parameters(paramLabel = "password", description = "The user's password") String password,
            @CommandLine.Mixin OptionsMixin options
    ) {
        def userManager = createApplicationContext(options).getBean(UserManager)
        userManager.createUser(username, password)
    }

    @CommandLine.Command(name = "set-permission", description = "adds user to jenkins", mixinStandardHelpOptions = true)
    void grantPermission(
            @CommandLine.Parameters(paramLabel = "username", description = "The username to grant the permission to") String username,
            @CommandLine.Parameters(paramLabel = "permission", description = "The permission to grant") String permission,
            @CommandLine.Mixin OptionsMixin options
    ) {
        def userManager = createApplicationContext(options).getBean(UserManager)
        if (userManager.isUsingMatrixBasedPermissions()) {
            userManager.grantPermission(username, UserManager.Permissions.valueOf(permission))
        } else {
            log.debug("Is not using matrix based permission. Does not need to add permission.")
        }
    }

    private ApplicationContext createApplicationContext(OptionsMixin options) {
        ApplicationContext.run()
                .registerSingleton(new Factory(options))
    }

    static class OptionsMixin {
        // args group jenkins
        @CommandLine.Option(names = ['--jenkins-url'], required = true, description = 'The url of your external jenkins')
        public String jenkinsUrl
        @CommandLine.Option(names = ['--jenkins-username'], required = true, description = 'Mandatory when --jenkins-url is set')
        public String jenkinsUsername
        @CommandLine.Option(names = ['--jenkins-password'], required = true, description = 'Mandatory when --jenkins-url is set')
        public String jenkinsPassword
    }
}
