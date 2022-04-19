package bv

import auth.User
import auth.UserPermission
import auth.UserRole
import grails.gorm.transactions.Transactional

@Transactional
class UserService {

    def save(params) {
        def userInstance = new User(params)
        if (!userInstance.save(flush: true))
        return
    }

    def updateInstance (id, confirmPassword) {
        def userInstance = User.get(id)
        userInstance.password = confirmPassword
        userInstance.save(flush: true)
        return
    }

    def deleteUserRole (userInstance) {
        def userRole = UserRole.findByUser(userInstance)
        userRole.delete(flush: true)
        return
    }
    def saveUserPermission (userPermissionId, permissionStatus) {

            def userPermissionInstance = UserPermission.get(userPermissionId)
            if (permissionStatus == 0) {
                //Do Inactive To Active
                userPermissionInstance.permissionStatus = 1
                userPermissionInstance.save(flush: true)
                return
            } else if (permissionStatus == 1)  {
                //Do Active to Inactive
                userPermissionInstance.permissionStatus = 0
                userPermissionInstance.save(flush: true)
                return
            }
        }

    def saveNewUserPermission (status, businessCompanyId, mainUserId, permittedUser) {

        def userPermission = new UserPermission()
        userPermission.permissionStatus = status
        userPermission.businessCompanyId = businessCompanyId
        userPermission.mainUserId = mainUserId
        userPermission.permittedUserId = permittedUser
        userPermission.save();
        return
    }

    def update (id, params) {
        def userInstance = User.get(id)
        userInstance.properties = params
        userInstance.save(flush: true)
        return
    }

    def addPermittedUser (status,busnessCompanyId,authUserId,permittedUserId) {
        //insert new row
        def userPermission = new UserPermission()
        userPermission.permissionStatus = status
        userPermission.businessCompanyId = busnessCompanyId
        userPermission.mainUserId = authUserId
        userPermission.permittedUserId = permittedUserId
        return
    }

    def removePermittedUser (obj) {
        UserPermission userPermission
        userPermission.getAt(obj.id)
        userPermission.delete(flush: true)
        return
    }

    def saveUserLog (def authIpAddress,def authLogStr, def now, def authUserId) {

        def userLog = new UserLog()
        userLog.ipAddress = authIpAddress
        userLog.logInfo = authLogStr
        userLog.logTime = now
        userLog.userId = authUserId
        userLog.save(flush: true)
        return
    }

}
