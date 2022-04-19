package bv

import org.springframework.dao.DataIntegrityViolationException

class SoulsController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [soulsInstanceList: Souls.list(params), soulsInstanceTotal: Souls.count()]
    }

    def create() {
        [soulsInstance: new Souls(params)]
    }

    def save() {
        def soulsInstance = new Souls(params)
        if (!soulsInstance.save(flush: true)) {
            render(view: "create", model: [soulsInstance: soulsInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'souls.label', default: 'Souls'), soulsInstance.id])
        redirect(action: "show", id: soulsInstance.id)
    }

    def show(Long id) {
        def soulsInstance = Souls.get(id)
        if (!soulsInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'souls.label', default: 'Souls'), id])
            redirect(action: "list")
            return
        }

        [soulsInstance: soulsInstance]
    }

    def edit(Long id) {
        def soulsInstance = Souls.get(id)
        if (!soulsInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'souls.label', default: 'Souls'), id])
            redirect(action: "list")
            return
        }

        [soulsInstance: soulsInstance]
    }

    def update(Long id, Long version) {
        def soulsInstance = Souls.get(id)
        if (!soulsInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'souls.label', default: 'Souls'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (soulsInstance.version > version) {
                soulsInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'souls.label', default: 'Souls')] as Object[],
                        "Another user has updated this Souls while you were editing")
                render(view: "edit", model: [soulsInstance: soulsInstance])
                return
            }
        }

        soulsInstance.properties = params

        if (!soulsInstance.save(flush: true)) {
            render(view: "edit", model: [soulsInstance: soulsInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'souls.label', default: 'Souls'), soulsInstance.id])
        redirect(action: "show", id: soulsInstance.id)
    }

    def delete(Long id) {
        def soulsInstance = Souls.get(id)
        if (!soulsInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'souls.label', default: 'Souls'), id])
            redirect(action: "list")
            return
        }

        try {
            soulsInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'souls.label', default: 'Souls'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'souls.label', default: 'Souls'), id])
            redirect(action: "show", id: id)
        }
    }
}
