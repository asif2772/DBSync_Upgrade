package security

import auth.User
import bv.UserService
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.security.authentication.*
import org.springframework.security.web.WebAttributes

@Secured('permitAll')
class LoginController {
    AuthenticationTrustResolver authenticationTrustResolver
    SpringSecurityService springSecurityService

    def utilityService
    UserService userService
    def mailService

    def index() {
        if (springSecurityService.isLoggedIn()) {
            redirect uri: conf.successHandler.defaultTargetUrl
        }
        else {
            redirect action: 'auth', params: params
        }

    }
    def auth () {

        def conf = getConf()
        if (springSecurityService.isLoggedIn()) {
            redirect uri: conf.successHandler.defaultTargetUrl
            return
        }

        String postUrl = request.contextPath + conf.apf.filterProcessesUrl
        render view: 'auth2', model: [postUrl: postUrl,
                                     usernameParameter: conf.apf.usernameParameter,
                                     passwordParameter: conf.apf.passwordParameter]
    }

    def loginSuccess(){

        redirect(controller: 'databaseSync', action: 'index')
       /* if (SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')) {
            redirect(controller: 'registerUser', action: 'admin')
        } else {
            redirect(controller: 'registerUser', action: 'index')
        }*/

    }

    /**
     * Callback after a failed login. Redirects to the auth page with a warning message.
     */
    def authfail = {
        def username = session[SpringSecurityUtils.SPRING_SECURITY_LAST_USERNAME_KEY]
        String msg = ''
        def exception = session[WebAttributes.AUTHENTICATION_EXCEPTION]
        if (exception) {
            if (exception instanceof AccountExpiredException) {
                msg = g.message(code: "springSecurity.errors.login.expired")
            } else if (exception instanceof CredentialsExpiredException) {
                msg = g.message(code: "springSecurity.errors.login.passwordExpired")
            } else if (exception instanceof DisabledException) {
                msg = g.message(code: "springSecurity.errors.login.disabled")
            } else if (exception instanceof LockedException) {
                msg = g.message(code: "springSecurity.errors.login.locked")
            } else {
//                msg = g.message(code: "springSecurity.errors.login.fail")
                msg = "Incorrect username or password!"
            }
        }

        if (springSecurityService.isAjax(request)) {
            render([error: msg] as JSON)
        } else {
            flash.message = msg
            redirect action: 'auth', params: params
        }
    }

    def forgotPassword = {
        if (params.userName) {
            User user = User.findByUsername(params.userName)
            if (user && user.email) {
                def password = utilityService.generateRandomString()
                userService.updateInstance(user.id, password)
                try {
                    mailService.sendMail {
                        async false
                        to user.email
                        subject message(code: "msg.forgotPassword.mail.subject")
                        body(view: "forgotPasswordEmail", model: [user: user, password: password])
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    flash.message = "Mail send failed. Please try again."
                }
                render(view: 'forgotPasswordSuccess', model: [user: user, password: password])
                return

            } else {
                flash.message = message(code: "msg.forgotPassword.unknown", args: [params.userName])
            }
        }
        render view: 'forgotPassword'
    }

    protected ConfigObject getConf() {
        SpringSecurityUtils.securityConfig
    }
}

