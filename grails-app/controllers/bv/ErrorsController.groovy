package bv

import org.springframework.security.access.annotation.Secured

@Secured(['permitAll'])
class ErrorsController {
    def error403 = {}

    def error404 = {}

    def error500 = {
        render view: '/error'
    }
}
