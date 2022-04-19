package com.bv

import auth.Role
import auth.User
import auth.UserRole
import com.bv.util.GridEntity
import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

class UserController {

    def springSecurityService
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    SecurityContext ctx = SecurityContextHolder.getContext();
    Authentication auth = ctx.getAuthentication();

    String username = auth.getName()


    def index() {
        redirect(action: "list", params: params)
    }

    @Secured(['ROLE_ADMIN'])
    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)


        [userInstance: new User(params), userInstanceList: User.list(params), userInstanceTotal: User.count()]


    }

    @Secured(['ROLE_ADMIN'])
    def userGrid(){

        User user = User.findByUsername(username)
        Integer authUserId = user.getAt('id')
        int aInt = 0

        def userList
        def busComID
        String gridOutput
        String businessCompanyId=""
        String strQuery = ""
        if(authUserId==1){

            strQuery = "SELECT usr.id, usr.username,bc.name as companyName,usr.accountExpired,usr.accountLocked,usr.enabled,usr.passwordExpired from User as usr, BusinessCompany as bc where bc.id = usr.businessCompanyId "

        }else{

            businessCompanyId="select usr.businessCompanyId from User as usr where id="+authUserId
            busComID=User.executeQuery(businessCompanyId)
            strQuery = "SELECT usr.id, usr.username,bc.name as companyName,usr.accountExpired,usr.accountLocked,usr.enabled,usr.passwordExpired from User as usr, BusinessCompany as bc where bc.id = usr.businessCompanyId AND usr.businessCompanyId="+busComID[0]

        }

        userList = User.executeQuery(strQuery)
        List quickExpenseList = new ArrayList()
        GridEntity obj
        String userEdit=""

        def protocol = request.isSecure() ? "https://" : "http://"
        def host = request.getServerName()
        def port = request.getServerPort()
        def context = request.getServletContext().getContextPath()

        def liveUrl = ""
        liveUrl = protocol + host + ":" + port + context
        userList.each { phn ->
            obj = new GridEntity();
            aInt = aInt+1
            obj.id = aInt

            def businessCompanyName = phn[2];
            userEdit = "<a href='javascript:editUser(\"${phn[0]}\",\"${liveUrl}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${liveUrl}/images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:editPassword(\"${phn[0]}\",\"${liveUrl}\")'><img width=\"16\" height=\"15\" alt=\"EditPass\" src=\"${liveUrl}/images/reset.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href='javascript:deleteUser(\"${phn[0]}\",\"${phn[1]}\",\"${liveUrl}\")'> <img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${liveUrl}/images/delete.png\"></a>"
            obj.cell = ["userName": phn[1], "comapanyName":businessCompanyName, "accountExpire":phn[3], "acountLocked":phn[4],"enable": phn[5],"passwordExpire": phn[6],"action": userEdit]
            quickExpenseList.add(obj)

        }
        LinkedHashMap result = [draw: 1, recordsTotal: userList.size(), recordsFiltered:  userList.size(),data : quickExpenseList.cell]
        gridOutput = result as JSON
        render gridOutput;
    }


    @Secured(['ROLE_ADMIN'])
    def create() {
        [userInstance: new User(params)]
    }

    @Secured(['ROLE_ADMIN'])
    def save() {
        def userInstance = new User(params)
        if (!userInstance.save(flush: true)) {
            render(view: "list", model: [userInstance: userInstance, userInstanceList: User.list(params), userInstanceTotal: User.count()])
            return
        }

        def role = Role.get(params.role)
        UserRole.create(userInstance, role,true)

        flash.message = message(code: 'com.created.message', args: [message(code: 'user.label', default: 'User'), userInstance.id])
        redirect(action: "list")
    }

    @Secured(['ROLE_ADMIN'])
    def show(Long id) {
        def userInstance = User.get(id)
        if (!userInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "list")
            return
        }

        [userInstance: userInstance]
    }

    @Secured(['ROLE_ADMIN','ROLE_ACCOUNTANT'])
    def updatePassword(Long id) {
        def userInstance = User.get(id)
        String username = userInstance.username
        Integer userid = userInstance.id

        String newPassword = params.newPassword
        String confirmPassword = params.confirmPassword

        [userInstance: userInstance, username: username, userId:userid]

        String st = params.st
        if (st){
            render view: 'updatePassword', model: [userInstance: userInstance, username: username, userId:userid]
        }else{
            if (confirmPassword){
                if (newPassword.equals(confirmPassword)){

                    userInstance.password = confirmPassword
                    userInstance.save(flush: true)
                    flash.message = 'Password Updated Successfully!'

                    User curUser = springSecurityService.getCurrentUser();
                    def adminRole = Role.findByAuthority('ROLE_ADMIN');
                    if (curUser.authorities.contains(adminRole)) {
                        redirect(controller: 'user', action: 'list')
                    }
                    else{
                        render view: 'updatePassword', model: [userInstance: userInstance, username: username, userId:userid]
                    }
                }else{
                    flash.message = 'Your password and repeat password is not same'
                    redirect controller: 'user', action: 'updatePassword', id: userid
                }
            }
            else{
                //flash.message = 'Password and repeat password is not same'
                render view: 'updatePassword', model: [userInstance: userInstance, username: username, userId:userid]
            }
        }
    }

    @Secured(['ROLE_ADMIN'])
    def edit(Long id) {
        def userInstance = User.get(id)
        def  userRole = UserRole.findByUser(userInstance)
        if(!userRole){
            flash.message = "Edit not possible! " + message(code: 'default.not.found.message', args: ['Role', id])
            redirect(action: "list")
            return
        }
        userInstance.role = userRole.role.id

        if (!userInstance) {
            flash.message = "Edit not possible! " + message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "list")
            return
        }
        render(view: "list", model: [userInstance: userInstance, userInstanceList: User.list(params), userInstanceTotal: User.count()])
    }

    @Secured(['ROLE_ADMIN'])
    def update(Long id, Long version) {
        def userInstance = User.get(id)
        if (!userInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (userInstance.version > version) {
                userInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'user.label', default: 'User')] as Object[],
                        "Another user has updated this User while you were editing")
                render(view: "edit", model: [userInstance: userInstance])
                return
            }
        }

        userInstance.properties = params
        //First delete old role
        def  userRole = UserRole.findByUser(userInstance)
        if(userRole){
            userRole.delete(flush: true);
        }

        //Create new role
        def role = Role.get(params.role)
        UserRole.create(userInstance, role,true)

        if (!userInstance.save(flush: true)) {
            render(view: "edit", model: [userInstance: userInstance])
            return
        }

        flash.message = message(code: 'com.updated.message', args: [message(code: 'user.label', default: 'User'), userInstance.id])
        redirect(action: "list")
    }

    @Secured(['ROLE_ADMIN'])
    def remove(Long id) {

        def userInstance = User.get(id)
        def userName = userInstance.username
        if (!userInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), userName])
            redirect(action: "list")
            return
        }

        try {
            def  userRole = UserRole.findByUser(userInstance)
            if(userRole){
                userRole.delete(flush: true);
            }

            userInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'user.label', default: 'User'), userName])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'user.label', default: 'User'), userName])
            redirect(action: "show", id: id)
        }
    }
}
