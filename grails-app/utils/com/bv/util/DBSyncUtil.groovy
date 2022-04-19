package com.bv.util

import groovy.sql.GroovyRowResult

class DBSyncUtil {

    public List wrapDbVersionList(List<GroovyRowResult> quickEntries) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj

        try {

            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i];

                def dbName = expenseEntry.databaseName
                def version = expenseEntry.version
                def status = expenseEntry.status
                def dbUpdate = "<input type='button' class='dbSyncBtn greenBtn' style='float: left;height:25px;' value='Update' onclick='updateDbIndividually(\"${dbName}\")' >"


                if(dbName){
                    obj = new GridEntity();
                    obj.id = expenseEntry.id
                    obj.cell = ["dbName": dbName,"version": version,"status": status,"action":dbUpdate]
                    quickExpenseList.add(obj)
                }

            }

        } catch (Exception ex) {
            quickExpenseList = []
        }

        return quickExpenseList
    }

    public List wrapDbListWithoutCompanyBankAccount(List<GroovyRowResult> quickEntries) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj
        String changeBooking
        def start = 0

        try {
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i];

                def dbName = expenseEntry.databaseName

                if(dbName){
                    obj = new GridEntity();
                    obj.id = expenseEntry.id
                    obj.cell = ["dbName": dbName]
                    quickExpenseList.add(obj)
                    counter++;
                }

            }
            return quickExpenseList;
        } catch (Exception ex) {
            quickExpenseList = [];
            return quickExpenseList;
        }
    }

    public List wrapDbVersionListForDumping(List<GroovyRowResult> quickEntries,context) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj
        String changeBooking
        def start = 0

        try {
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i];

                def dbName = expenseEntry.databaseName

                def dbDump = "<a href='${context}/reports/dbDump?dbName=${dbName}'>Back Up</a>"
//                def dbDump = "<g:link controller='reports' action='dbDump' params='[dbName:${dbName}]'>Back Up</g:link>"
//                def dbDump = "<input type='button' class='dbSyncBtn greenBtn' style='float: left;height:25px;' value='Back Up' onclick='dbBackUp(\"${dbName}\")' >"


                obj = new GridEntity();
                obj.id = expenseEntry.id
                obj.cell = ["dbName": dbName,"action":dbDump]
                quickExpenseList.add(obj)
                counter++;
            }
            return quickExpenseList;
        } catch (Exception ex) {
            quickExpenseList = [];
            return quickExpenseList;
        }
    }
}
