package bv

import org.springframework.dao.DataIntegrityViolationException

class SoulLogsController {
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        params.sort="id"
        params.order="desc"
        [soulLogsInstanceList: SoulLogs.list(params), soulLogsInstanceTotal: SoulLogs.count()]
    }

    def create() {
        [soulLogsInstance: new SoulLogs(params)]
    }

    def save() {
        def soulLogsInstance = new SoulLogs(params)
        if (!soulLogsInstance.save(flush: true)) {
            render(view: "create", model: [soulLogsInstance: soulLogsInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'soulLogs.label', default: 'SoulLogs'), soulLogsInstance.id])
        redirect(action: "show", id: soulLogsInstance.id)
    }

    def show(Long id) {
        def soulLogsInstance = SoulLogs.get(id)
        if (!soulLogsInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'soulLogs.label', default: 'SoulLogs'), id])
            redirect(action: "list")
            return
        }

        [soulLogsInstance: soulLogsInstance]
    }

    def edit(Long id) {
        def soulLogsInstance = SoulLogs.get(id)
        if (!soulLogsInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'soulLogs.label', default: 'SoulLogs'), id])
            redirect(action: "list")
            return
        }

        [soulLogsInstance: soulLogsInstance]
    }

    def update(Long id, Long version) {
        def soulLogsInstance = SoulLogs.get(id)
        if (!soulLogsInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'soulLogs.label', default: 'SoulLogs'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (soulLogsInstance.version > version) {
                soulLogsInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'soulLogs.label', default: 'SoulLogs')] as Object[],
                        "Another user has updated this SoulLogs while you were editing")
                render(view: "edit", model: [soulLogsInstance: soulLogsInstance])
                return
            }
        }

        soulLogsInstance.properties = params

        if (!soulLogsInstance.save(flush: true)) {
            render(view: "edit", model: [soulLogsInstance: soulLogsInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'soulLogs.label', default: 'SoulLogs'), soulLogsInstance.id])
        redirect(action: "show", id: soulLogsInstance.id)
    }

    def delete(Long id) {
        def soulLogsInstance = SoulLogs.get(id)
        if (!soulLogsInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'soulLogs.label', default: 'SoulLogs'), id])
            redirect(action: "list")
            return
        }

        try {
            soulLogsInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'soulLogs.label', default: 'SoulLogs'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'soulLogs.label', default: 'SoulLogs'), id])
            redirect(action: "show", id: id)
        }
    }
}
