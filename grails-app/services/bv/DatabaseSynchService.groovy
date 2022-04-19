package bv

import com.bv.util.GridEntity
import grails.web.context.ServletContextHolder
import groovy.sql.GroovyRowResult
import groovy.sql.Sql

import javax.sql.DataSource
import java.sql.SQLException


class DatabaseSynchService {

    DataSource dataSource

    //Please update this vesion if you added new folder in sql folder.
    static final DB_FILE_MAX_VERSION = 46
    def finalReturn = ""


    def numberOfDb() {
        def number
        def db = new Sql(dataSource);
        List<GroovyRowResult> rows = db.rows("select count(*) as number from business_company");
        number = rows[0].number

        return number
    }

    def updateSingleDB(def selectedDbName) {

        //long startTime = System.currentTimeMillis()

        def db = new Sql(dataSource)
        def row

        List<GroovyRowResult> rows = db.rows("""select db_name as dbName,db_user as dbUser,db_password as dbPassword, server_url as serverUrl from business_company WHERE db_name = ${
            selectedDbName
        }""")
        row = rows[0]

        db.close()

        def dbName = row.get("dbName")
        def serverUrl = row.get("serverUrl")
        def username = row.get("dbUser")
        def password = row.get("dbPassword")
        def dbExist = false
        Sql userDb
        def finalReturn = ""

        try {
            userDb = Sql.newInstance(serverUrl + dbName, username, password, "com.mysql.jdbc.Driver")
            dbExist = true
            println('successfully connected to db:  ' + dbName + ' during update')
        } catch (SQLException sqlE) {
            println('error during UpdateDatabase: failed connecting db:  ' + dbName + "---> error: " + sqlE.toString())
        } catch (Exception e) {
            println('error during UpdateDatabase: failed connecting db:  ' + dbName + "---> GenericError: " + e.toString())
        }

        if (dbExist) {
            def queryResult = userDb.rows("SELECT COUNT(*)as numberTable FROM information_schema.tables WHERE table_schema = '" + dbName + "' ")
            def tableCount = queryResult[0].numberTable
            if (tableCount >= 85) {

                String maxVersionQuery = "SELECT max(version) FROM db_version"
                def resultMaxVersion = userDb.rows(maxVersionQuery)

                if(resultMaxVersion[0][0] < DB_FILE_MAX_VERSION) {
                    def dbVersions = userDb.rows("show tables like 'db_version'")
                    if (dbVersions.size() > 0) {
                        finalReturn = applyDBPatch(userDb, dbName)
                    } else {
                        finalReturn = applyDBPatchForVersion(userDb, "0", dbName)
                        finalReturn = applyDBPatch(userDb, dbName)
                    }
                } else {
                    finalReturn = resultMessage("alreadyUpdated", "", dbName, "", "", '')
                }
            } else {
                finalReturn = resultMessage("corrupted", "", dbName, "", "", tableCount)
            }

            userDb.close()
        } else {
            finalReturn = resultMessage("notConnected", "", dbName, "", "", "")
        }

        /*long endTime = System.currentTimeMillis()
        float elapsedTime = (endTime - startTime) / 1000F
        println("for db: " + dbName + " elapsedTime: " +elapsedTime)*/

        return finalReturn
    }

    def updateAllDatabase() {

        def db = new Sql(dataSource)

        List<GroovyRowResult> rows = db.rows("select db_name as dbName,db_user as dbUser,db_password as dbPassword, server_url as serverUrl from business_company")
        int businessCompanySize = rows.size()
        db.close()

        StringBuilder sbFinalReturn = new StringBuilder()
        int successfulUpdates = 0

        for (int i= 0 ; i <businessCompanySize ; i++ ){
            //long startTime = System.currentTimeMillis()

            def row  = rows [i]
            def dbName = row.get("dbName")
            def serverUrl = row.get("serverUrl")
            def username = row.get("dbUser")
            def password = row.get("dbPassword")
            def dbExist = false
            Sql userDb
            def finalReturn = ""

            if (dbName != "tomcatbu_budgetview") { // escaping auth DB, this is redundant
                try {
                    userDb = Sql.newInstance(serverUrl + dbName, username, password, "com.mysql.jdbc.Driver")
                    dbExist = true
                    println('successfully connected to db:  ' + dbName + ' during update')
                } catch (SQLException sqlE) {
                    println('error during UpdateDatabase: failed connecting db:  ' + dbName + "---> error: " + sqlE.toString())
                } catch (Exception e) {
                    println('error during UpdateDatabase: failed connecting db:  ' + dbName + "---> GenericError: " + e.toString())
                }
            }

            if (dbExist) {
                def queryResult = userDb.rows("SELECT COUNT(*)as numberTable FROM information_schema.tables WHERE table_schema = '" + dbName + "' ")
                def tableCount = queryResult[0].numberTable
                if (tableCount >= 85) {

                    String maxVersionQuery = "SELECT max(version) FROM db_version"
                    def resultMaxVersion = userDb.rows(maxVersionQuery)

                    if(resultMaxVersion[0][0] < DB_FILE_MAX_VERSION) {
                        def dbVersions = userDb.rows("show tables like 'db_version'")
                        if (dbVersions.size() > 0) {
                            finalReturn = applyDBPatch(userDb, dbName)
                        } else {
                            finalReturn = applyDBPatchForVersion(userDb, "0", dbName)
                            finalReturn = applyDBPatch(userDb, dbName)
                        }
                        successfulUpdates++
                    } else {
                        finalReturn = finalReturn = resultMessage("alreadyUpdated", "", dbName, "", "", '')
                    }
                } else {
                    finalReturn = resultMessage("corrupted", "", dbName, "", "", tableCount)
                }

                userDb.close()
            } else {
                finalReturn = resultMessage("notConnected", "", dbName, "", "", "")
            }

            /*long endTime = System.currentTimeMillis()
            float elapsedTime = (endTime - startTime) / 1000F
            println("for db: " + dbName + " elapsedTime: " +elapsedTime)*/

            sbFinalReturn.append(finalReturn)
        }


        println('number of successful updates: ' + successfulUpdates)

        return sbFinalReturn.toString()
    }

    def deleteVersionRow(def userDb, def rowNo) {
        userDb.execute("delete from db_version WHERE version = " + rowNo);
    }

    def insertVersionRow(def userDb, def rowNo) {
        userDb.execute("INSERT INTO db_version(version,date) values('" + rowNo + "', NOW());");
    }

    def applyDBPatch(def userDb, def dbName) {
        def missingVersionList = []
        def multipleVersionList = []
        def tempResult = ""

        def rowDbVersion = userDb.rows("select version as dbVersion from db_version ORDER BY version");
        int maxDbVersion = rowDbVersion.get(rowDbVersion.size() - 1).get("dbVersion");

        //Prepare missing and duplicate version list.
        for (int i = 1; i <= maxDbVersion; i++) {
            if (!findArrayElement(rowDbVersion, i)) {
                missingVersionList.add(i)
            }

            def countElement = countArrayElement(rowDbVersion, i)
            if (countElement > 1) {
                multipleVersionList.add(i)
            }
        }

        //Appy sql execute for missing version.
        for (int j = 0; j < missingVersionList.size(); j++) {
            tempResult = applyDBPatchForVersion(userDb, missingVersionList[j], dbName)
        }

        //Appy sql execute for duplicate version list.
        for (int x = 0; x < multipleVersionList.size(); x++) {
            deleteVersionRow(userDb, multipleVersionList[x])
            insertVersionRow(userDb, multipleVersionList[x])
        }

        //Apply sql execute for new version file.
        for (int j = maxDbVersion + 1; j <= DB_FILE_MAX_VERSION; j++) {
            tempResult = applyDBPatchForVersion(userDb, j, dbName)
        }

        return tempResult
    }

    def findArrayElement(def arr, def element) {
        def flag = false
        for (int i = 0; i < arr.size(); i++) {

            if (element == arr[i][0]) {
                flag = true;
            }
        }
        return flag;
    }

    def countArrayElement(def arr, def element) {
        def count = 0
        for (int i = 0; i < arr.size(); i++) {

            if (element == arr[i][0]) {
                count++
            }

        }
        return count;
    }

    def applyDBPatchForVersion(def userDb, def dbVersion, def dbName) {

        def tempResult = ""
        def currentInfo = parseDDL(userDb, dbVersion, dbName)

        if (currentInfo.bSuccess) {
            tempResult = tempResult + parseDML(userDb, dbVersion, dbName);
            tempResult = tempResult + resultMessage("successful", "", dbName, currentInfo.cFile, currentInfo.cSql, "")
        } else {
            tempResult = currentInfo.tempResult
        }

        return tempResult
    }

    def parseDDL(def userDb, def dbVersion, def dbName) {

        def tempResult = ""
        String templine = '';
        Map currentInfo = ["cFile": "", "cSql": "", "bSuccess": false, "tempResult": ""]

        String path = ServletContextHolder.getServletContext().getRealPath('sql')
        def bSuccess = false;
        //DDL parsing
        def file = new File(path + File.separator + dbVersion + File.separator + 'ddl.sql');
        currentInfo.cFile = file
        try {
            file.text.eachLine { line ->
                if (line.length() > 0) {
                    if (line.substring(0, 2) == '--' || line == '') {
                    } else {
                        templine = templine + line;
                        if (line.trim().endsWith(";")) {
                            currentInfo.cSql = templine
                            if (templine.contains("TriggerName")) {
                                def values = templine.split(',')
                                templine = values[1].replace(";", "").trim();//+ "_" + dbVersion;
                                String sql = getTriggerQuery(templine);
                                userDb.execute(sql);
                                templine = '';
                            } else {
                                userDb.execute(templine);
                                templine = '';
                            }
                        }
                    }
                }
            }

            bSuccess = true;
        } catch (SQLException ex) {
            String sqlState = ex.getSQLState();
            int nErrorCode = ex.getErrorCode();

            //Duplicate column exist
            if (nErrorCode == 1060 && sqlState == "42S21") {
                bSuccess = true;
                tempResult += resultMessage("ignore_statement", ex, dbName, file, templine, "")
            } else {
                tempResult += resultMessage("sqlError", ex, dbName, file, templine, "")
            }

        }
        catch (Exception e) {
            tempResult += resultMessage("sqlError", e, dbName, file, templine, "")
        }

        currentInfo.bSuccess = bSuccess
        currentInfo.tempResult = tempResult

        return currentInfo;
    }

    def parseDML(def userDb, def dbVersion, def dbName) {

        def tempResult = ""
        String templine = '';
        String path = ServletContextHolder.getServletContext().getRealPath('sql')

        //DML parsing
        def fileDML = new File(path + File.separator + dbVersion + File.separator + 'dml.sql');

        templine = '';
        fileDML.text.eachLine { line ->
            if (line.length() > 0) {
                if (line.substring(0, 2) == '--' || line == '') {

                } else {
                    templine = templine + line;
                    if (line.trim().endsWith(";")) {
                        tempResult += executeDMLQuery(userDb, templine)
                        templine = "";
                    }
                }
            }
        }

        return tempResult
    }


    def executeDMLQuery(def userDb, def templine) {
        def tempResult = ""

        try {
            userDb.execute(templine);
            templine = '';
        } catch (SQLException ex) {
            String sqlState = ex.getSQLState();
            int nErrorCode = ex.getErrorCode();

            //Duplicate entry
            if (nErrorCode == 1062 && sqlState == "23000") {
                tempResult += resultMessage("ignore_statement", ex, dbName, file, templine, "")
            } else {
                tempResult += resultMessage("sqlError", ex, dbName, file, templine, "")
            }

        } catch (Exception e) {
            tempResult = resultMessage("sqlError", e, dbName, fileDML, templine, "")
        }

        return tempResult;
    }

    def resultMessage(def type, def ex, def dbName, def cFile, def cSql, def numberTable) {

        StringBuilder finalReturn =  new StringBuilder()

        if (type == "sqlError") {
            finalReturn.append("<p style=\"color:red\">Database Name: " + dbName + "<br>")
                        .append("File Path: " + cFile + "<br>")
                        .append("Sql String: " + cSql + "<br>")
                        .append(dbName + " " + "Is is failed. " + "<br>")
                        .append("ERROR: " + ex + "</p><br>***********************************************\n\n<br><br>")
        } else if (type == "corrupted") {
            finalReturn.append("<p style=\"color:red\">Database Name: " + dbName + "<br>")
                        .append("Tabel no:" + numberTable + "<br>")
                        .append(dbName + " " + "Is corrupted." + "<br>")
                        .append("</p><br>***********************************************\n\n<br><br>")
        } else if (type == "notConnected") {
            finalReturn.append("<p style=\"color:red\">Database Name: " + dbName + "<br>")
                        .append(dbName + " " + "failed to connect." + "<br>")
                        .append("</p><br>***********************************************\n\n<br><br>")
        } else if (type == "successful") {
            finalReturn.append("<p style=\"color:Green\">Database Name: " + dbName + "<br>")
                        .append("File Path: " + cFile + "<br>")
                        .append(dbName + " " + "Is successfully executed. " + "</p><br> ***********************************************<br><br>")
        } else if (type == "ignore_statement") {
            finalReturn.append("<p style=\"color:Green\">Database Name: " + dbName + "<br>")
                        .append(dbName + " " + ex.getMessage() + "</p><br> ***********************************************<br><br>")
        } else if (type == "alreadyUpdated") {
            finalReturn.append("<p style=\"color:#b27300\">Database Name: ${dbName} <br>")
                    .append(dbName + " Already Updated </p><br> ***********************************************<br><br>")
        }

        return finalReturn.toString()
    }

    /// database maintance start
    def List<Map> databaseMaintainceList() {

        List dbMaintainceArr = new ArrayList();
        def db = new Sql(dataSource);
        List<GroovyRowResult> rows = db.rows("select db_name as bdName,db_user as dbUser,db_password as dbPassword, server_url as serverUrl,DATE_FORMAT(date_created,'%m-%d-%Y') as creationDate,name as companyName,IF(status='1', 'Active', 'Inactive') as user_status from business_company");

        for (def row in rows) {

            Map dbMaintainceMap = ["companyName"                : '',
                                   "databaseName"               : '',
                                   "creationDate"               : '',
                                   "firstLogin"                 : '',
                                   "lastLogin"                  : '',
                                   "numberOfIncomeBudgets"      : '',
                                   "numberOfExpenseBudgets"     : '',
                                   "numberOfLinesInTrans_master": '',
                                   "firstTransactionDate"       : '',
                                   "lastCreatedDate"            : '',
                                   "bookingYearWithZero"        : '',
                                   "accountCodeWithNull"        : '',
                                   "status"                     : ''
            ];

            def dbName = row.get("bdName");
            def serverUrl = row.get("serverUrl");
            def dbExist = true;

//            def username = "root"
//            def password = ""

            def username = row.get("dbUser");
            def password = row.get("dbPassword");

            //Code for local server
            //if(username != "root") continue;

            Sql userDb;
            try {
                userDb = Sql.newInstance(serverUrl + dbName, username, password, "com.mysql.jdbc.Driver");
            } catch (Exception e) {
                e.printStackTrace();
                dbExist = false;
            }

            if (dbExist) {
                // println(row.get("bdName"));

                try {

                    String numberOfIncomeBudgetsQuery = "SELECT COUNT(id) FROM budget_item_income"
                    String numberOfExpenseBudgetsQuery = "SELECT COUNT(id) FROM budget_item_expense"
                    String numberOfLineTransMasterQuery = "SELECT COUNT(id) FROM trans_master"
                    String firstTransactionDateQuery = "SELECT DATE_FORMAT(trans_date,'%m-%d-%Y') FROM trans_master where id =(SELECT  MIN(id) from trans_master)"
                    String lastCreatedDateQuery = "SELECT DATE_FORMAT(create_date,'%m-%d-%Y')  FROM trans_master order by create_date DESC"
                    String bookingYearWithZeroQuery = "SELECT COUNT(*) FROM trans_master where booking_year = 0"
                    String accountCodeWithNullQuery = "SELECT COUNT(*) FROM trans_master where account_code = 'null' or account_code = '' "

                    def resultnumberOfIncomeBudgets = userDb.rows(numberOfIncomeBudgetsQuery)
                    def resultnumberOfExpenseBudgets = userDb.rows(numberOfExpenseBudgetsQuery)
                    def resultnumberOfLineTransMaster = userDb.rows(numberOfLineTransMasterQuery)
                    def resultFirstTransactionDate = userDb.rows(firstTransactionDateQuery)
                    def resultLastCreatedDate = userDb.rows(lastCreatedDateQuery)
                    def resultBookingYearWithZero = userDb.rows(bookingYearWithZeroQuery)
                    def resultAccountCodeWithNull = userDb.rows(accountCodeWithNullQuery)

                    dbMaintainceMap.companyName = row.get("companyName");
                    dbMaintainceMap.databaseName = row.get("bdName");
                    dbMaintainceMap.creationDate = row.get("creationDate");
                    dbMaintainceMap.numberOfIncomeBudgets = resultnumberOfIncomeBudgets[0][0];
                    dbMaintainceMap.numberOfExpenseBudgets = resultnumberOfExpenseBudgets[0][0];
                    dbMaintainceMap.numberOfLinesInTrans_master = resultnumberOfLineTransMaster[0][0];
                    dbMaintainceMap.firstTransactionDate = resultFirstTransactionDate[0][0];
                    dbMaintainceMap.lastCreatedDate = resultLastCreatedDate[0][0];
                    dbMaintainceMap.bookingYearWithZero = resultBookingYearWithZero[0][0];
                    dbMaintainceMap.accountCodeWithNull = resultAccountCodeWithNull[0][0];
                    dbMaintainceMap.status = row.get("user_status");

                } catch (Exception e) {
                    e.printStackTrace();
                }

                userDb.close();
                dbMaintainceArr.add(dbMaintainceMap);
            }

        }

        return dbMaintainceArr;
    }

    ///database maintance end
    def String getTriggerQuery(String triggerName) {
        String sql = '';
        if (triggerName == "after_invoice_expense_insert") {

            sql = "CREATE TRIGGER after_invoice_expense_insert\n" +
                    "  AFTER INSERT ON invoice_expense\n" +
                    "  FOR EACH ROW\n" +
                    "  BEGIN\n" +
                    "  INSERT INTO audit_trail SET date_time = NOW(),\n" +
                    "       action = 'create',\n" +
                    "       process = 'invoice_expense',\n" +
                    "       invoice_number = new.id,\n" +
                    "       type_of_booking = 'expense',\n" +
                    "       user_id = new.user_id,\n" +
                    "       table_name = 'invoice_expense',\n" +
                    "       amount = new.total_gl_amount,\n" +
                    "       values_in_string =  concat('id=',new.id,',budget_item_expense_id=',new.budget_item_expense_id,',budget_vendor_id=',new.budget_vendor_id,',vendor_id=' , new.vendor_id , ',terms_id=' , new.terms_id);\n" +
                    "  END;";

        } else if (triggerName == "after_invoice_expense_update") {

            sql = "CREATE TRIGGER after_invoice_expense_update\n" +
                    "    AFTER UPDATE ON invoice_expense\n" +
                    "    FOR EACH ROW \n" +
                    "    BEGIN\n" +
                    "    INSERT INTO audit_trail\n" +
                    "    SET date_time = NOW(),\n" +
                    "        action = 'update',\n" +
                    "        process = 'invoice_expense',\n" +
                    "        invoice_number = new.id,\n" +
                    "        type_of_booking = 'expense',\n" +
                    "        user_id = new.user_id,\n" +
                    "        table_name = 'invoice_expense',\n" +
                    "        amount = new.total_gl_amount,\n" +
                    "        values_in_string =  concat('id=',new.id,',budget_item_expense_id=',new.budget_item_expense_id,',budget_vendor_id=',new.budget_vendor_id,',vendor_id=' , new.vendor_id , ',terms_id=' , new.terms_id);\n" +
                    "   END;";
        } else if (triggerName == "after_invoice_expense_delete") {
            sql = "CREATE TRIGGER after_invoice_expense_delete\n" +
                    "    AFTER DELETE ON invoice_expense\n" +
                    "    FOR EACH ROW BEGIN\n" +
                    "    INSERT INTO audit_trail\n" +
                    "    SET date_time = NOW(),\n" +
                    "        action = 'delete',\n" +
                    "        process = 'invoice_expense',\n" +
                    "        invoice_number = old.id,\n" +
                    "        type_of_booking = 'expense',\n" +
                    "        user_id = old.user_id,\n" +
                    "        table_name = 'invoice_expense',\n" +
                    "        amount = old.total_gl_amount,\n" +
                    "        values_in_string =  concat('id=',old.id,',budget_item_expense_id=',old.budget_item_expense_id,',budget_vendor_id=',old.budget_vendor_id,',vendor_id=' , old.vendor_id , ',terms_id=' , old.terms_id);\n" +
                    "   END;";
        } else if (triggerName == "after_invoice_income_insert") {

            sql = "CREATE TRIGGER after_invoice_income_insert\n" +
                    "    AFTER INSERT ON invoice_income\n" +
                    "    FOR EACH ROW BEGIN\n" +
                    "    INSERT INTO audit_trail\n" +
                    "    SET date_time = NOW(),\n" +
                    "        action = 'create',\n" +
                    "        process = 'invoice_income',\n" +
                    "        invoice_number = new.id,\n" +
                    "        type_of_booking = 'income',\n" +
                    "        user_id = new.user_id,\n" +
                    "        amount = new.total_gl_amount,\n" +
                    "        table_name = 'invoice_income',\n" +
                    "        values_in_string =  concat('id=',new.id,',budget_item_income_id=',new.budget_item_income_id,',budget_customer_id=',new.budget_customer_id,',customer_id=' , new.customer_id , ',terms_id=' , new.terms_id);\n" +
                    "   END;";
        } else if (triggerName == "after_invoice_income_update") {
            sql = "CREATE TRIGGER after_invoice_income_update\n" +
                    "    AFTER UPDATE ON invoice_income\n" +
                    "    FOR EACH ROW BEGIN\n" +
                    "    INSERT INTO audit_trail\n" +
                    "    SET date_time = NOW(),\n" +
                    "        action = 'update',\n" +
                    "        process = 'invoice_income',\n" +
                    "        invoice_number = new.id,\n" +
                    "        type_of_booking = 'income',\n" +
                    "        user_id = new.user_id,\n" +
                    "        amount = new.total_gl_amount,\n" +
                    "        table_name = 'invoice_income',\n" +
                    "        values_in_string =  concat('id=',new.id,',budget_item_income_id=',new.budget_item_income_id,',budget_customer_id=',new.budget_customer_id,',customer_id=' , new.customer_id , ',terms_id=' , new.terms_id);\n" +
                    "   END;";

        } else if (triggerName == "after_invoice_income_delete") {
            sql = "CREATE TRIGGER after_invoice_income_delete\n" +
                    "    AFTER DELETE ON invoice_income\n" +
                    "    FOR EACH ROW BEGIN\n" +
                    "    INSERT INTO audit_trail\n" +
                    "    SET date_time = NOW(),\n" +
                    "        action = 'delete',\n" +
                    "        process = 'invoice_income',\n" +
                    "        invoice_number = old.id,\n" +
                    "        type_of_booking = 'income',\n" +
                    "        user_id = old.user_id,\n" +
                    "        amount = old.total_gl_amount,\n" +
                    "        table_name = 'invoice_income',\n" +
                    "        values_in_string =  concat('id=',old.id,',budget_item_income_id=',old.budget_item_income_id,',budget_customer_id=',old.budget_customer_id,',customer_id=' , old.customer_id , ',terms_id=' , old.terms_id);\n" +
                    "   END;"
        } else if (triggerName == "after_trans_master_insert") {
            sql = "CREATE TRIGGER after_trans_master_insert\n" +
                    "    AFTER INSERT ON trans_master\n" +
                    "    FOR EACH ROW BEGIN\n" +
                    "    INSERT INTO audit_trail\n" +
                    "    SET date_time = NOW(),\n" +
                    "         action = 'create',\n" +
                    "         user_id = new.user_id,\n" +
                    "         process = new.process,\n" +
                    "         invoice_number = new.invoice_no,\n" +
                    "         type_of_booking = '',\n" +
                    "         amount = new.amount,\n" +
                    "         table_name = 'trans_master',\n" +
//                    "         values_in_string =  concat('id=',new.id,';account_code=',new.account_code,';trans_type=',new.trans_type,';booking_period=' , new.booking_period , ';booking_year=' , new.booking_year);\n" +
                    "         values_in_string = new.recenciliation_code;\n" +
                    "   END;"
        } else if (triggerName == "after_trans_master_update") {

            sql = "CREATE TRIGGER after_trans_master_update\n" +
                    "    AFTER UPDATE ON trans_master\n" +
                    "    FOR EACH ROW BEGIN\n" +
                    "    INSERT INTO audit_trail\n" +
                    "    SET date_time = NOW(),\n" +
                    "         action = 'update',\n" +
                    "         user_id = new.user_id,\n" +
                    "         process = new.process,\n" +
                    "         invoice_number = new.invoice_no,\n" +
                    "         type_of_booking = '',\n" +
                    "         amount = new.amount,\n" +
                    "         table_name = 'trans_master',\n" +
//                    "         values_in_string =  concat('id=',new.id,';account_code=',new.account_code,';trans_type=',new.trans_type,';booking_period=' , new.booking_period , ';booking_year=' , new.booking_year);\n" +
                    "         values_in_string = new.recenciliation_code;\n" +
                    "   END;";
        } else if (triggerName == "after_trans_master_delete") {
            sql = "CREATE TRIGGER after_trans_master_delete\n" +
                    "    AFTER DELETE ON trans_master\n" +
                    "    FOR EACH ROW BEGIN\n" +
                    "    INSERT INTO audit_trail\n" +
                    "    SET date_time = NOW(),\n" +
                    "         action = 'delete',\n" +
                    "         user_id = old.user_id,\n" +
                    "         process = old.process,\n" +
                    "         invoice_number = old.invoice_no,\n" +
                    "         type_of_booking = '',\n" +
                    "         amount = old.amount,\n" +
                    "         table_name = 'trans_master',\n" +
//                    "         values_in_string =  concat('id=',old.id,';account_code=',old.account_code,';trans_type=',old.trans_type,';booking_period=' , old.booking_period , ';booking_year=' , old.booking_year);\n" +
                    "         values_in_string = old.recenciliation_code;\n" +
                    "   END;"
        }
        //Remain total debit, total credit, number of transaction
        //Bank statement import
        else if (triggerName == "after_bank_statement_import_final_insert") {
            sql = "CREATE TRIGGER after_bank_statement_import_final_insert\n" +
                    "    AFTER INSERT ON bank_statement_import_final\n" +
                    "    FOR EACH ROW BEGIN\n" +
                    "    INSERT INTO audit_trail\n" +
                    "    SET date_time = NOW(),\n" +
                    "         action = 'create',\n" +
                    "         user_id = 0,\n" +
                    "         process = 'bank_statement_import_final',\n" +
                    "         invoice_number = '',\n" +
                    "         type_of_booking = '',\n" +
                    "         amount = 0,\n" +
                    "         table_name = 'bank_statement_import_final',\n" +
                    "         values_in_string =  concat('id=',new.id,',opening_balance=',new.starting_balance,',closing_balance=',new.ending_balance,',first_date=',new.start_trans_date,',last_date=',new.end_trans_date,',debit=',new.total_debit,',credit=',new.total_credit,',total_transaction=',new.number_of_transaction);\n" +
                    "   END;"
        } else if (triggerName == "after_bank_statement_import_final_update") {

            sql = "CREATE TRIGGER after_bank_statement_import_final_update\n" +
                    "    AFTER UPDATE ON bank_statement_import_final\n" +
                    "    FOR EACH ROW BEGIN\n" +
                    "    INSERT INTO audit_trail\n" +
                    "    SET date_time = NOW(),\n" +
                    "         action = 'update',\n" +
                    "         user_id = 0,\n" +
                    "         process = 'bank_statement_import_final',\n" +
                    "         invoice_number = '',\n" +
                    "         type_of_booking = '',\n" +
                    "         amount = 0,\n" +
                    "         table_name = 'bank_statement_import_final',\n" +
                    "         values_in_string =  concat('id=',new.id,',opening_balance=',new.starting_balance,',closing_balance=',new.ending_balance,',first_date=',new.start_trans_date,',last_date=',new.end_trans_date,',debit=',new.total_debit,',credit=',new.total_credit,',total_transaction=',new.number_of_transaction);\n" +
                    "   END;"
        } else if (triggerName == "after_bank_statement_import_final_delete") {

            sql = "CREATE TRIGGER after_bank_statement_import_final_delete\n" +
                    "    AFTER DELETE ON bank_statement_import_final\n" +
                    "    FOR EACH ROW BEGIN\n" +
                    "    INSERT INTO audit_trail\n" +
                    "    SET date_time = NOW(),\n" +
                    "         action = 'delete',\n" +
                    "         user_id = 0,\n" +
                    "         process = 'bank_statement_import_final',\n" +
                    "         invoice_number = '',\n" +
                    "         type_of_booking = '',\n" +
                    "         amount = 0,\n" +
                    "         table_name = 'bank_statement_import_final',\n" +
                    "         values_in_string =  concat('id=',old.id,',opening_balance=',old.starting_balance,',closing_balance=',old.ending_balance,',first_date=',old.start_trans_date,',last_date=',old.end_trans_date,',debit=',old.total_debit,',credit=',old.total_credit,',total_transaction=',old.number_of_transaction);\n" +
                    "   END;"
        }
        //Bank statement import details
        else if (triggerName == "after_bank_statement_import_details_final_insert") {
            sql = "CREATE TRIGGER after_bank_statement_import_details_final_insert\n" +
                    "    AFTER INSERT ON bank_statement_import_details_final\n" +
                    "    FOR EACH ROW BEGIN\n" +
                    "    INSERT INTO audit_trail\n" +
                    "    SET date_time = NOW(),\n" +
                    "         action = 'create',\n" +
                    "         user_id = 0,\n" +
                    "         process = 'bank_statement_import_details',\n" +
                    "         invoice_number = new.bank_import_id,\n" +
                    "         type_of_booking = new.debit_credit,\n" +
                    "         amount = new.amount,\n" +
                    "         table_name = 'bank_statement_import_details_final',\n" +
                    "         values_in_string =  concat('id=',new.id,',company_bank_acc=',new.trans_bank_account_no,',trans_date=',new.trans_date_time,',by_bank_account=',new.by_bank_account_no , ',desc=',new.description);\n" +
                    "   END;"
        } else if (triggerName == "after_bank_statement_import_details_final_update") {

            sql = "CREATE TRIGGER after_bank_statement_import_details_final_update\n" +
                    "    AFTER UPDATE ON bank_statement_import_details_final\n" +
                    "    FOR EACH ROW BEGIN\n" +
                    "    INSERT INTO audit_trail\n" +
                    "    SET date_time = NOW(),\n" +
                    "         action = 'update',\n" +
                    "         user_id = 0,\n" +
                    "         process = 'bank_statement_import_details',\n" +
                    "         invoice_number = new.bank_import_id,\n" +
                    "         type_of_booking = new.debit_credit,\n" +
                    "         amount = new.amount,\n" +
                    "         table_name = 'bank_statement_import_details_final',\n" +
                    "         values_in_string =  concat('id=',new.id,',company_bank_acc=',new.trans_bank_account_no,',trans_date=',new.trans_date_time,',by_bank_account=',new.by_bank_account_no , ',desc=',new.description);\n" +
                    "   END;"
        } else if (triggerName == "after_bank_statement_import_details_final_delete") {

            sql = "CREATE TRIGGER after_bank_statement_import_details_final_delete\n" +
                    "    AFTER DELETE ON bank_statement_import_details_final\n" +
                    "    FOR EACH ROW BEGIN\n" +
                    "    INSERT INTO audit_trail\n" +
                    "    SET date_time = NOW(),\n" +
                    "         action = 'delete',\n" +
                    "         user_id = 0,\n" +
                    "         process = 'bank_statement_import_details',\n" +
                    "         invoice_number = old.bank_import_id,\n" +
                    "         type_of_booking = old.debit_credit,\n" +
                    "         amount = old.amount,\n" +
                    "         table_name = 'bank_statement_import_details_final',\n" +
                    "         values_in_string =  concat('id=',old.id,',company_bank_acc=',old.trans_bank_account_no,',trans_date=',old.trans_date_time,',by_bank_account=',old.by_bank_account_no , ',desc=',old.description);\n" +
                    "   END;"
        }

        return sql;
    }


    def List<Map> getInvoiceWithoutBudgetList() {

        List invoiceArr = new ArrayList();
        def db = new Sql(dataSource);
        List<GroovyRowResult> rows = db.rows("select db_name as bdName,db_user as dbUser,db_password as dbPassword, server_url as serverUrl,date_created as creationDate,name as companyName,IF(status='1', 'Active', 'Inactive') as user_status from business_company");

        for (def row in rows) {

            def dbName = row.get("bdName");
            def serverUrl = row.get("serverUrl");
            def dbExist = true;

//            def username = "root"
//            def password = ""

            def username = row.get("dbUser");
            def password = row.get("dbPassword");

            //Code for local server
            //if(username != "root") continue;

            Sql userDb;
            try {
                userDb = Sql.newInstance(serverUrl + dbName, username, password, "com.mysql.jdbc.Driver");
            } catch (Exception e) {
                e.printStackTrace();
                dbExist = false;
            }

            if (dbExist) {

                try {

                    String incomeInvoiceStr = """   SELECT
                                                    a.id,
                                                    a.booking_period,
                                                    a.booking_year,
                                                    a.budget_item_income_id,
                                                    a.invoice_no,
                                                    a.payment_ref,
                                                    a.total_gl_amount,
                                                    a.total_vat,b.customer_name
                                                    FROM
                                                    invoice_income a INNER JOIN customer_master b
                                                    ON a.budget_customer_id = b.id WHERE  budget_item_income_id not IN (select id FROM budget_item_income )"""

                    String expenseInvoiceStr = """SELECT
                                                    a.booking_period,
                                                    a.booking_year,
                                                    a.budget_item_expense_id,
                                                    a.budget_vendor_id,
                                                    a.invoice_no,
                                                    a.payment_ref,
                                                    a.total_gl_amount,
                                                    a.total_vat, b.vendor_name,a.is_book_receive,a.id
                                                    FROM
                                                    invoice_expense a INNER JOIN vendor_master b
                                                    on a.budget_vendor_id = b.id
                                                    where a.budget_item_expense_id not IN (SELECT id from budget_item_expense) ORDER BY a.is_book_receive """


                    def resultIncomeInvoiceArr = userDb.rows(incomeInvoiceStr)
                    def resultExpenseInvoiceArr = userDb.rows(expenseInvoiceStr)

                    for (int i = 0; i < resultIncomeInvoiceArr.size(); i++) {

                        Map invoiceMap = ["databaseName"      : '',
                                          "type"              : '',
                                          "id"                : '',
                                          "bookingPeriod"     : '',
                                          "bookingYear"       : '',
                                          "budgetCustomerID"  : '',
                                          "budgetItemIncomeId": '',
                                          "invoiceNo"         : '',
                                          "totalVat"          : '',
                                          "totalGlAmount"     : '',
                                          "paymentRef"        : ''

                        ]

                        invoiceMap.databaseName = row.get("bdName");
                        invoiceMap.type = "Income invoice"
                        invoiceMap.id = resultIncomeInvoiceArr[i][0];
                        invoiceMap.bookingPeriod = resultIncomeInvoiceArr[i][1];
                        invoiceMap.bookingYear = resultIncomeInvoiceArr[i][2];
                        invoiceMap.budgetCustomerID = resultIncomeInvoiceArr[i][8];
                        invoiceMap.budgetItemIncomeId = resultIncomeInvoiceArr[i][3];
                        invoiceMap.invoiceNo = resultIncomeInvoiceArr[i][4];
                        invoiceMap.totalVat = resultIncomeInvoiceArr[i][7];
                        invoiceMap.totalGlAmount = resultIncomeInvoiceArr[i][6];
                        invoiceMap.paymentRef = resultIncomeInvoiceArr[i][5];
                        invoiceArr.add(invoiceMap);

                    }

                    for (int i = 0; i < resultExpenseInvoiceArr.size(); i++) {

                        Map invoiceMap = ["databaseName"      : '',
                                          "type"              : '',
                                          "id"                : '',
                                          "bookingPeriod"     : '',
                                          "bookingYear"       : '',
                                          "budgetCustomerID"  : '',
                                          "budgetItemIncomeId": '',
                                          "invoiceNo"         : '',
                                          "totalVat"          : '',
                                          "totalGlAmount"     : '',
                                          "paymentRef"        : ''

                        ]

                        def type = ""
                        if (resultExpenseInvoiceArr[i][9] == 0) {
                            type = "Expense Invoice"
                        } else {
                            type = "Expense Receipt"
                        }

                        invoiceMap.databaseName = row.get("bdName");
                        invoiceMap.type = type
                        invoiceMap.id = resultExpenseInvoiceArr[i][10];
                        invoiceMap.bookingPeriod = resultExpenseInvoiceArr[i][0];
                        invoiceMap.bookingYear = resultExpenseInvoiceArr[i][1];
                        invoiceMap.budgetCustomerID = resultExpenseInvoiceArr[i][8];
                        invoiceMap.budgetItemIncomeId = resultExpenseInvoiceArr[i][2];
                        invoiceMap.invoiceNo = resultExpenseInvoiceArr[i][4];
                        invoiceMap.totalVat = resultExpenseInvoiceArr[i][7];
                        invoiceMap.totalGlAmount = resultExpenseInvoiceArr[i][6];
                        invoiceMap.paymentRef = resultExpenseInvoiceArr[i][5];
                        invoiceArr.add(invoiceMap);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                userDb.close();

            }

        }

        return invoiceArr;
    }

    def Map getDBSecurityInfo(def databaseName) {

        Map dbVersionMap = ["serverUrl": '', "dbUser": '', "dbPassword": ''];

        def db = new Sql(dataSource);
        List<GroovyRowResult> rows = db.rows("select db_name as dbName,db_user as dbUser,db_password as dbPassword, server_url as serverUrl,date_created as creationDate,name as companyName,IF(status='1', 'Active', 'Inactive') as user_status from business_company");
        def countDB = db.rows("select count(id) from business_company");

        for (def row in rows) {
            def dbName = row.get("dbName");
            def serverUrl = row.get("serverUrl");
            def username = row.get("dbUser");
            def password = row.get("dbPassword");

            if (dbName == databaseName) {
                dbVersionMap.serverUrl = serverUrl;
                dbVersionMap.dbUser = username;
                dbVersionMap.dbPassword = password;
            }
        }

        return dbVersionMap;
    }

    def List<Map> dbVersionInfoList() {

        List dbVersionArr = new ArrayList();
        Map finalReturn = ["databaseArr": '', "countDB": '']
        def db = new Sql(dataSource);

        List<GroovyRowResult> rows = db.rows("select db_name as dbName,db_user as dbUser,db_password as dbPassword, " +
                "server_url as serverUrl,date_created as creationDate,name as companyName,IF(status='1', 'Active', 'Inactive') " +
                "as user_status from business_company");
        db.close()

        for (def row in rows) {

            Map dbVersionMap = ["databaseName": '', "version": '', "status": '']

            def dbName = row.get("dbName")
            def serverUrl = row.get("serverUrl")
            def username = row.get("dbUser")
            def password = row.get("dbPassword")
            def dbExist = true

            Sql userDb
            try {
                userDb = Sql.newInstance(serverUrl + dbName, username, password, "com.mysql.jdbc.Driver")
            } catch (SQLException sqlE) {
                println('error during connecting db:  ' + dbName + "---> error: " + sqlE.toString())
                dbExist = false
            } catch (Exception e) {
                println('error during connecting db:  ' + dbName + "---> GenericError: " + e.toString())
                dbExist = false
            }

            if (dbExist) {
                if (dbName != "tomcatbu_budgetview") {
                    try {

                        String maxVersionQuery = "SELECT max(version) FROM db_version"

                        def resultMaxVersion = userDb.rows(maxVersionQuery)

                        def status = ""
                        if (DB_FILE_MAX_VERSION == resultMaxVersion[0][0]) {
                            status = "Up to date"
                        } else if (DB_FILE_MAX_VERSION < resultMaxVersion[0][0]) {
                            status = "Invalid Version"
                        } else {
                            status = "Back Dated"
                        }

                        dbVersionMap.databaseName = row.get("dbName")
                        dbVersionMap.version = resultMaxVersion[0][0]
                        dbVersionMap.status = status

                    } catch (SQLException sqlE) {
                        println('error during showing dbList:  ' + dbName + "---> error: " + sqlE.toString())
                    } catch (Exception e) {
                        println('error during showing dbList:  ' + dbName + "---> GenericError: " + e.toString())
                    }

                    dbVersionArr.add(dbVersionMap)
                }

                userDb.close()
            }

        }
        return dbVersionArr
    }


    def countDbVersionList() {

        def db = new Sql(dataSource);
        //  List<GroovyRowResult> rows = db.rows("select db_name as bdName,db_user as dbUser,db_password as dbPassword, server_url as serverUrl,date_created as creationDate,name as companyName,IF(status='1', 'Active', 'Inactive') as user_status from business_company");
        def countDB = db.rows("select count(id) from business_company");
        def count = countDB[0][0]

        return count
    }


    def List<Map> dbListWithoutCompanyBankAccount() {

        List dbVersionArr = new ArrayList();
        Map finalReturn = ["databaseArr": '', "countDB": '']
        def db = new Sql(dataSource);

        List<GroovyRowResult> rows = db.rows("select db_name as dbName,db_user as dbUser,db_password as dbPassword, " +
                "server_url as serverUrl,date_created as creationDate,name as companyName,IF(status='1', 'Active', 'Inactive') " +
                "as user_status from business_company");
        def countDB = db.rows("select count(id) from business_company");

        for (def row in rows) {

            Map dbVersionMap = ["databaseName": ''];

            def dbName = row.get("dbName");
            def serverUrl = row.get("serverUrl");
            def dbExist = true;

            def username = row.get("dbUser");
            def password = row.get("dbPassword");

            Sql userDb;
            try {
                userDb = Sql.newInstance(serverUrl + dbName, username, password, "com.mysql.jdbc.Driver");
            } catch (Exception e) {
                e.printStackTrace();
                dbExist = false;
            }

            if (dbExist) {
                if (dbName != "tomcatbu_budgetview") {
                    try {
                        String query = "SELECT * from company_bank_accounts WHERE bank_account_category = ' ' " +
                                "OR bank_account_category = 'null' " +
                                "OR bank_account_category IS NULL " +
                                "OR bank_account_category = 'NULL' "
                        def result = userDb.rows(query)
                        if (result.size() > 0) {
                            dbVersionMap.databaseName = row.get("dbName");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dbVersionArr.add(dbVersionMap);
                }
                userDb.close();
            }

        }
        return dbVersionArr;
    }

    def createDatabase(String databaseName, def username, def password) {

        //First drop database.
        def dropQuery = "DROP DATABASE IF EXISTS " + databaseName + ";"
        def createQuery = "CREATE DATABASE " + databaseName + ";"
        def sql = Sql.newInstance("jdbc:mysql://localhost:3306", username, password, "com.mysql.jdbc.Driver")
        sql.executeUpdate(dropQuery)
        sql.executeUpdate(createQuery)
        //Then create database.

    }

    def Sql selectDatabase(def dbName) {

        Map dbVersionMap = getDBSecurityInfo(dbName);

        Sql userDb;
        try {
            userDb = Sql.newInstance(dbVersionMap.serverUrl + dbName, dbVersionMap.dbUser, dbVersionMap.dbPassword, "com.mysql.jdbc.Driver");

        } catch (Exception e) {
            e.printStackTrace();
//            dbExist = false;
        }

        return userDb;
    }

    def parseDBNameFromFile(def file) {
        String dbName = ""

        file.text.eachLine { line ->
            if (line.length() > 1) {
                if (line.substring(0, 2) == '--' || line == " ") {
                    if (dbName == "") {
                        if (line.contains("Database")) {
                            def splitted = line.split(":")
                            String temp = splitted[1].toString()
                            dbName = temp.substring(2, temp.length() - 1)


                        }
                    }
                }
            } else {
                println(line);
            }
        }

        return dbName;
    }

    def excuteUploadedTigger(Sql userDb, String tempLine) {
        def finalResult = ""
        def queryFailed = 0
        def querySuccessful = 0
        Map res = [
                'finalResult'    : "",
                'querySuccessful': 0,
                'queryFailed'    : 0

        ]
        def newTempLine = tempLine.replace("DELIMITER //", "")
        try {
            userDb.execute(newTempLine);
//            finalResult = finalResult + restoreErrorMessage("successful",tempLine,"")
            querySuccessful++
        }
        catch (Exception ex) {
            finalResult = finalResult + restoreErrorMessage("failed", tempLine, ex)
            queryFailed++
        }
        res.finalResult = finalResult
        res.querySuccessful = querySuccessful
        res.queryFailed = queryFailed
        return res
    }


    def excuteUploadedFile(def file, def username, def password) {
        //Delete and Create database.
        def dbName = parseDBNameFromFile(file);
        createDatabase(dbName, username, password);
        //Select database
        Sql userDb = selectDatabase(dbName);
        //Parse file and execute query.
        String templine = ''
        def finalResult = '';
        def totalQuery = 0
        def queryFailed = 0
        def querySuccessful = 0

        file.text.eachLine { line ->
            if (line.length() > 1) {
                if (line.substring(0, 2) == '--' || line == " ") {

                } else {
                    templine = templine + line;

//                    if (templine.contains("DELIMITER")) {
                    if (templine.contains("DELIMITER //")) {
                        if (line.trim().endsWith("END")) {
//                        if (line.trim().endsWith("END ;")) {
                            templine = templine + " ;"
                            totalQuery++
                            def temp = excuteUploadedTigger(userDb, templine)
                            finalResult = finalResult + temp.finalResult
                            queryFailed = queryFailed + temp.queryFailed
                            querySuccessful = querySuccessful + temp.querySuccessful
                            templine = '';
                        }
                    } else if (templine.contains("DELIMITER ;")) {
                        templine = '';

                    } else {
                        if (line.trim().endsWith(";")) {

                            totalQuery++
                            try {
                                userDb.execute(templine);
//                                finalResult = finalResult + restoreErrorMessage("successful", templine, "")
                                querySuccessful++
                                templine = '';
                            }
                            catch (Exception ex) {
                                queryFailed++
                                finalResult = finalResult + restoreErrorMessage("failed", templine, ex)
                            }

                        }
                    }
                }

            }


        }

        finalResult = "<div style=\"color:blue\">Total Number of Query: " + totalQuery + "<br></div>" +
                "<div style=\"color:green\">Successfully Executed: " + querySuccessful + "<br></div>" +
                "<div style=\"color:red\">Execution Failed: " + queryFailed + "<br></div>" + finalResult

        return finalResult
    }

    def restoreErrorMessage(def type, def tempLine, def exception) {
        def finalResult = ""
        if (type == "successful") {
//            finalResult = finalResult + "<div style=\"color:blue\">"+tempLine+"<br></div>"
            finalResult = finalResult + "<div style=\"color:green\">Query executed successfully<br></div>"
        } else {
            finalResult = finalResult + "<div style=\"color:red\">" + tempLine + "<br></div>"
            finalResult = finalResult + "<div style=\"color:red\">" + exception + "<br></div>"
            finalResult = finalResult + "<div style=\"color:red\">Execution Failed<br></div>"
        }

        return finalResult
    }


    def dbDump(def tempDbName) {
        def db = new Sql(dataSource);
        List<GroovyRowResult> rows = db.rows("""select db_name,db_user,db_password  from business_company where db_name= '${
            tempDbName
        }'""");
        def dbName = rows[0].db_name
        def userName = rows[0].db_user
        def password = rows[0].db_password
        Process process
        String path = ServletContextHolder.getServletContext().getRealPath('sql')
        path = path + "/clientDbDump.sh"
        String temp = "sh " + path + " " + userName + " " + password + " " + dbName
        println("temp : " + temp)
        process = temp.execute()
        /*String strCommand = "C:\\Program Files\\MySQL\\MySQL Server 5.6\\bin\\mysqldump -u root tomcatbu_lmtest1 -C --result-file=E:\\${dbName}.sql";
        process = strCommand.execute()*/
        process.waitForOrKill(10000)

        return dbName

    }

    def List<Map> databaseTransAmountList() {

        List dbMaintainceArr = new ArrayList();
        def db = new Sql(dataSource);
        List<GroovyRowResult> rows = db.rows("select db_name as dbName,db_user as dbUser,db_password as dbPassword, server_url as serverUrl,DATE_FORMAT(date_created,'%m-%d-%Y') as creationDate,name as companyName,IF(status='1', 'Active', 'Inactive') as user_status from business_company")

        for (def row in rows) {

            Map dbMaintainceMap = ["companyName"                : '',
                                   "databaseName"               : '',
                                   "transAmount"                : ''
            ]

            def dbName = row.get("dbName")
            def serverUrl = row.get("serverUrl")
            def dbExist = true
            def username = row.get("dbUser")
            def password = row.get("dbPassword")

            Sql userDb;
            try {
                userDb = Sql.newInstance(serverUrl + dbName, username, password, "com.mysql.jdbc.Driver")
            } catch (Exception e) {
                e.printStackTrace()
                dbExist = false
            }

            if (dbExist) {

                try {
                    String totalAmountFromTransMaster = "SELECT ROUND(SUM(amount),2) from trans_master"
                    def resultTransAmount = userDb.rows(totalAmountFromTransMaster)

                    dbMaintainceMap.companyName = row.get("companyName")
                    dbMaintainceMap.databaseName = row.get("dbName")
                    dbMaintainceMap.transAmount = String.valueOf(resultTransAmount[0][0])


                } catch (Exception e) {
                    e.printStackTrace()
                }

                userDb.close()
                dbMaintainceArr.add(dbMaintainceMap)
            }

        }

        return dbMaintainceArr
    }

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

}
