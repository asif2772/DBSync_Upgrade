package bv

import org.springframework.dao.DataIntegrityViolationException

class SoulBlockController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [soulBlockInstanceList: SoulBlock.list(params), soulBlockInstanceTotal: SoulBlock.count()]
    }

    def create() {
        [soulBlockInstance: new SoulBlock(params)]
    }

    def save() {
        def soulBlockInstance = new SoulBlock(params)
        if (!soulBlockInstance.save(flush: true)) {
            render(view: "create", model: [soulBlockInstance: soulBlockInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'soulBlock.label', default: 'SoulBlock'), soulBlockInstance.id])
        redirect(action: "show", id: soulBlockInstance.id)
    }

    def show(Long id) {
        def soulBlockInstance = SoulBlock.get(id)
        if (!soulBlockInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'soulBlock.label', default: 'SoulBlock'), id])
            redirect(action: "list")
            return
        }

        [soulBlockInstance: soulBlockInstance]
    }

    def edit(Long id) {
        def soulBlockInstance = SoulBlock.get(id)
        if (!soulBlockInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'soulBlock.label', default: 'SoulBlock'), id])
            redirect(action: "list")
            return
        }

        [soulBlockInstance: soulBlockInstance]
    }

    def update(Long id, Long version) {
        def soulBlockInstance = SoulBlock.get(id)
        if (!soulBlockInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'soulBlock.label', default: 'SoulBlock'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (soulBlockInstance.version > version) {
                soulBlockInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'soulBlock.label', default: 'SoulBlock')] as Object[],
                        "Another user has updated this SoulBlock while you were editing")
                render(view: "edit", model: [soulBlockInstance: soulBlockInstance])
                return
            }
        }

        soulBlockInstance.properties = params

        if (!soulBlockInstance.save(flush: true)) {
            render(view: "edit", model: [soulBlockInstance: soulBlockInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'soulBlock.label', default: 'SoulBlock'), soulBlockInstance.id])
        redirect(action: "show", id: soulBlockInstance.id)
    }

    def delete(Long id) {
        def soulBlockInstance = SoulBlock.get(id)
        if (!soulBlockInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'soulBlock.label', default: 'SoulBlock'), id])
            redirect(action: "list")
            return
        }

        try {
            soulBlockInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'soulBlock.label', default: 'SoulBlock'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'soulBlock.label', default: 'SoulBlock'), id])
            redirect(action: "show", id: id)
        }
    }
}
