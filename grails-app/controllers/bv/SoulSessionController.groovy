package bv

import org.springframework.dao.DataIntegrityViolationException

class SoulSessionController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [soulSessionInstanceList: SoulSession.list(params), soulSessionInstanceTotal: SoulSession.count()]
    }

    def create() {
        [soulSessionInstance: new SoulSession(params)]
    }

    def save() {
        def soulSessionInstance = new SoulSession(params)
        if (!soulSessionInstance.save(flush: true)) {
            render(view: "create", model: [soulSessionInstance: soulSessionInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'soulSession.label', default: 'SoulSession'), soulSessionInstance.id])
        redirect(action: "show", id: soulSessionInstance.id)
    }

    def show(Long id) {
        def soulSessionInstance = SoulSession.get(id)
        if (!soulSessionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'soulSession.label', default: 'SoulSession'), id])
            redirect(action: "list")
            return
        }

        [soulSessionInstance: soulSessionInstance]
    }

    def edit(Long id) {
        def soulSessionInstance = SoulSession.get(id)
        if (!soulSessionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'soulSession.label', default: 'SoulSession'), id])
            redirect(action: "list")
            return
        }

        [soulSessionInstance: soulSessionInstance]
    }

    def update(Long id, Long version) {
        def soulSessionInstance = SoulSession.get(id)
        if (!soulSessionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'soulSession.label', default: 'SoulSession'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (soulSessionInstance.version > version) {
                soulSessionInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'soulSession.label', default: 'SoulSession')] as Object[],
                        "Another user has updated this SoulSession while you were editing")
                render(view: "edit", model: [soulSessionInstance: soulSessionInstance])
                return
            }
        }

        soulSessionInstance.properties = params

        if (!soulSessionInstance.save(flush: true)) {
            render(view: "edit", model: [soulSessionInstance: soulSessionInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'soulSession.label', default: 'SoulSession'), soulSessionInstance.id])
        redirect(action: "show", id: soulSessionInstance.id)
    }

    def delete(Long id) {
        def soulSessionInstance = SoulSession.get(id)
        if (!soulSessionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'soulSession.label', default: 'SoulSession'), id])
            redirect(action: "list")
            return
        }

        try {
            soulSessionInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'soulSession.label', default: 'SoulSession'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'soulSession.label', default: 'SoulSession'), id])
            redirect(action: "show", id: id)
        }
    }
}
