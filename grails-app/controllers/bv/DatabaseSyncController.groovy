package bv

import com.bv.util.DBSyncUtil
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import groovy.sql.GroovyRowResult

class DatabaseSyncController {

    DBSyncUtil dBSyncUtil
    DatabaseSynchService databaseSynchService
    SpringSecurityService springSecurityService;
    def index() {
        def userId = springSecurityService.principal.id;
        if(userId == 1){
            redirect(action: "list");
        }else{
            redirect(action: "accessDenied");
        }

    }
    def accessDenied(){}


    def list(){
        def numberOfDb = databaseSynchService.numberOfDb()
        [dbCount: numberOfDb]
    }

    def showDataList(){
        def gridOutput
        String pageNumber = "1"

        def resultArr
        resultArr = databaseSynchService.dbVersionInfoList();
        List dbSyncList = new DatabaseSynchService().wrapDbVersionList(resultArr);

        def count = databaseSynchService.countDbVersionList()
        LinkedHashMap result = [draw: 1, recordsTotal: count, recordsFiltered:  count,data:dbSyncList.cell]

        gridOutput = result as JSON
        render gridOutput
     }

    def updateDatabase(){
        def dbName = params.dbName
        int updateAllDBs = Integer.parseInt(params.updateAllDBs)
        def returnStr

        if(updateAllDBs == 0)
            returnStr = databaseSynchService.updateSingleDB(dbName)
        else
            returnStr = databaseSynchService.updateAllDatabase()

        render returnStr
    }

    def updateAllDBs() {

        def returnStr = databaseSynchService.updateAllDatabase()
        render returnStr
    }

    def dummyResponse() {

        render "This is to test newly Added Controller"
    }
}
