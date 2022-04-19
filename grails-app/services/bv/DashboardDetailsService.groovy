package bv
import org.springframework.transaction.annotation.Transactional

@Transactional
class DashboardDetailsService {

    def serviceMethod() {

    }

    def ArrayList getBudgetCustomerData(fiscalYearInfo){

        LinkedHashMap gridResultCustomerArr
        String select = "bii.customerId AS customer_id,cm.customerName As customer_name,bii.id AS bookInvoiceId,SUM(bii.total_gl_amount) as totalBudAmt"
        String selectIndex = "customer_id,customer_name,bookInvoiceId,totalBudAmt"
        String from = "BudgetItemIncome AS bii,CustomerMaster AS cm"
        String where = "bii.bookingPeriodStartMonth >= '" + fiscalYearInfo[0][3] +"' AND bii.bookingPeriodStartYear >= '" + fiscalYearInfo[0][4] +
                "' AND bii.bookingPeriodStartMonth <= '" + fiscalYearInfo[0][5] + "' AND bii.bookingPeriodStartYear <= '" + fiscalYearInfo[0][6] +
                "' AND  bii.customerId = cm.id AND bii.customerId != '' AND bii.status=1";

        String groupBy = "bii.customer_id"
        String orderBy = "cm.customerName ASC"
        gridResultCustomerArr = new BudgetViewDatabaseService().select(select,from,where,orderBy,groupBy,'false',selectIndex)

        ArrayList budgetCustomerArr = gridResultCustomerArr['dataGridList']

        return budgetCustomerArr;
    }

    def ArrayList getCustomerDataAHWise(fiscalYearInfo){
        LinkedHashMap gridResultCustomerArr

        String select = "DISTINCT(a.glAccount) AS gl_account,cm.accountName As account_name,SUM(a.total_price_without_vat) as totalBudAmt"
        String selectIndex = "gl_account,account_name,totalBudAmt"
        String from = " BudgetItemIncomeDetails AS a,BudgetItemIncome AS b,ChartMaster AS cm"
        String where = "a.glAccount = cm.accountCode  AND a.budgetItemIncomeId = b.id AND b.bookingPeriodStartMonth >= '" + fiscalYearInfo[0][3] +"' AND b.bookingPeriodStartYear >= '" + fiscalYearInfo[0][4] +
        "' AND b.bookingPeriodEndMonth <= '" + fiscalYearInfo[0][5] + "' AND b.bookingPeriodEndYear <= '" + fiscalYearInfo[0][6] + "'"
        String groupBy="a.glAccount"
        String orderBy = "a.glAccount"

        gridResultCustomerArr = new BudgetViewDatabaseService().select(select,from,where,orderBy,groupBy,'false',selectIndex)
        ArrayList budgetCustomerArr = gridResultCustomerArr['dataGridList']

        return budgetCustomerArr;
    }

    def ArrayList getBudgetVendorData(fiscalYearInfo){

        LinkedHashMap gridResultVendorArr

        String select="a.vendorId AS vendor_id,v.vendorName As vendor_name,a.id AS bookInvoiceId,SUM(a.total_gl_amount) as totalBudAmt"
        String selectIndex="vendor_id,vendor_name,bookInvoiceId,totalBudAmt"
        String from="BudgetItemExpense AS a,VendorMaster AS v "
        String where="a.bookingPeriodStartMonth >='" + fiscalYearInfo[0][3] +"' AND a.bookingPeriodStartYear >='" + fiscalYearInfo[0][4] + "' AND a.bookingPeriodStartMonth <='" + fiscalYearInfo[0][5] + "' AND a.bookingPeriodStartYear<='" + fiscalYearInfo[0][6] + "' AND a.vendorId=v.id AND a.vendorId!='' AND a.status=1"
        String groupBy="a.vendor_id"
        String orderBy="v.vendorName ASC"
        gridResultVendorArr = new BudgetViewDatabaseService().select(select,from,where,orderBy,groupBy,'false',selectIndex)

        ArrayList budgetVendorArr = gridResultVendorArr['dataGridList']

        return budgetVendorArr;
    }

    def ArrayList getVendorDataAHWise(fiscalYearInfo){

        LinkedHashMap gridResultVendorArr

        String select = "DISTINCT(a.glAccount) AS gl_account,v.accountName As account_name,SUM(a.total_price_without_vat) as totalBudAmt"
        String selectIndex = "gl_account,account_name,totalBudAmt"
        String from = "BudgetItemExpenseDetails AS a,BudgetItemExpense AS b,ChartMaster AS v"
        String where = "a.glAccount = v.accountCode AND a.glAccount != '' AND a.budgetItemExpenseId = b.id AND b.bookingPeriodStartMonth >='" + fiscalYearInfo[0][3] +"' AND b.bookingPeriodStartYear >='" + fiscalYearInfo[0][4] + "' AND b.bookingPeriodEndMonth <='" + fiscalYearInfo[0][5] + "' AND b.bookingPeriodEndYear<='" + fiscalYearInfo[0][6]+"'"
        String groupBy = "a.glAccount"
        String orderBy = "a.glAccount"
        gridResultVendorArr = new BudgetViewDatabaseService().select(select,from,where,orderBy,groupBy,'false',selectIndex)

        ArrayList budgetVendorArr = gridResultVendorArr['dataGridList']

        return budgetVendorArr;
    }

    def ArrayList getCustomerAccountData(fiscalYearInfo,budgetCustomerArr){

        ArrayList customerAccount = new ArrayList()

        for (int j = 0; j < budgetCustomerArr.size(); j++) {
            String customerId = budgetCustomerArr[j][0]

            LinkedHashMap gridResultCustomerViewArr
            String selectVendorViewArr = "a.customerId As temp_c_id,b.glAccount As temp_gl_account,c.accountName As temp_account_name," +
                                        "a.bookingPeriodStartMonth As temp_b_p_s_m,SUM(b.totalPriceWithoutVat) As temp_t_p_w_v,COUNT(DISTINCT b.budgetItemIncomeId) " +
                                        "As temp_b_i_id,b.budgetItemIncomeId AS bii_id,b.id AS biid_id";
            String selectIndexVendorViewArr = "temp_c_id,temp_gl_account,temp_account_name,temp_b_p_s_m,temp_t_p_w_v,temp_b_i_id,bii_id,biid_id"
            String fromVendorViewArr = "BudgetItemIncome AS a, BudgetItemIncomeDetails AS b,ChartMaster AS c "
            String whereVendorViewArr = "a.id=b.budgetItemIncomeId AND b.glAccount=c.accountCode AND a.bookingPeriodStartMonth >='" + fiscalYearInfo[0][3] +
                    "' AND a.bookingPeriodStartYear >='" + fiscalYearInfo[0][4] + "' AND a.bookingPeriodStartMonth <='" + fiscalYearInfo[0][5] +
                    "' AND a.bookingPeriodStartYear<='" + fiscalYearInfo[0][6] + "' AND a.customerId='" + customerId + "'";

            String orderByVendorViewArr = "b.glAccount"
            String groupByVendorViewArr = "b.budgetItemIncomeId,b.glAccount"
            gridResultCustomerViewArr = new BudgetViewDatabaseService().select(selectVendorViewArr,fromVendorViewArr,whereVendorViewArr,orderByVendorViewArr,groupByVendorViewArr,'true',selectIndexVendorViewArr)

            def customerAccountDetailsArr = gridResultCustomerViewArr['dataGridList']
            if (customerAccountDetailsArr.size() > 0) {
                customerAccount << customerAccountDetailsArr
            }
        }

        return customerAccount;
    }

    def ArrayList getVendorAccountData(fiscalYearInfo,budgetVendorArr){

        ArrayList vendorAccountData = new ArrayList()

        for (int j = 0; j < budgetVendorArr.size(); j++) {
            def vendorId = budgetVendorArr[j][0]

            LinkedHashMap gridResultVendorViewArr;
            String selectVendorViewArr = "a.vendorId AS vi,b.glAccount AS ga,c.accountName AS an,a.bookingPeriodStartMonth AS bpsm,SUM(b.totalPriceWithoutVat) AS tpwv,COUNT(DISTINCT b.budgetItemExpenseId) AS biei,b.budgetItemExpenseId AS bie_id,b.id AS bied_id"
            String selectIndexVendorViewArr = "vi,ga,an,bpsm,tpwv,biei,bie_id,bied_id"
            String fromVendorViewArr = "BudgetItemExpense AS a,BudgetItemExpenseDetails AS b , ChartMaster AS c"
            String whereVendorViewArr = "a.id=b.budgetItemExpenseId AND b.glAccount=c.accountCode AND a.vendorId='" + vendorId +
                                        "' AND a.bookingPeriodStartMonth >='" + fiscalYearInfo[0][3] +"' AND a.bookingPeriodStartYear >='" +
                                        fiscalYearInfo[0][4] + "' AND a.bookingPeriodEndMonth <='" + fiscalYearInfo[0][5] +
                                        "' AND a.bookingPeriodEndYear <='" + fiscalYearInfo[0][6] +"'"
            String orderByVendorViewArr = "b.glAccount"
            String groupByVendorViewArr = "b.budgetItemExpenseId,b.glAccount"
            gridResultVendorViewArr = new BudgetViewDatabaseService().select(selectVendorViewArr,fromVendorViewArr,whereVendorViewArr,orderByVendorViewArr,groupByVendorViewArr,'true',selectIndexVendorViewArr)

            def vendorAccountDetailsArr = gridResultVendorViewArr['dataGridList']

            if (vendorAccountDetailsArr.size() > 0){
                vendorAccountData << vendorAccountDetailsArr
            }
        }

        return vendorAccountData;
    }

    def ArrayList getVendorBudgetAHWiseData(fiscalYearInfo,budgetVendorArr){

        ArrayList vendorAccountData = new ArrayList()

        for (int j = 0; j < budgetVendorArr.size(); j++) {
            def glAccount = budgetVendorArr[j][0]

            LinkedHashMap gridResultVendorViewArr
            String selectVendorViewArr = "b.glAccount AS ga,a.vendorId AS vi,c.vendorName AS vn,a.bookingPeriodStartMonth AS bpsm,SUM(b.totalPriceWithoutVat) AS stpwv,COUNT(DISTINCT b.budgetItemExpenseId) AS cbiei,b.budgetItemExpenseId AS biedi,b.id AS biei"
            String selectIndexVendorViewArr = "ga,vi,vn,bpsm,stpwv,cbiei,biedi,biei"
            String fromVendorViewArr = "BudgetItemExpense AS a,BudgetItemExpenseDetails AS b , VendorMaster AS c "
            String whereVendorViewArr = "a.id=b.budgetItemExpenseId AND a.vendorId = c.id AND b.glAccount='" + glAccount +
                                        "' AND a.bookingPeriodStartMonth >= '" + fiscalYearInfo[0][3] +"' AND a.bookingPeriodStartYear >=' " + fiscalYearInfo[0][4] +
                                        "' AND a.bookingPeriodEndMonth <= '" + fiscalYearInfo[0][5] + "' AND a.bookingPeriodEndYear <=' " + fiscalYearInfo[0][6] +"'"

            String orderByVendorViewArr = "b.glAccount,a.vendorId"
            String groupByVendorViewArr = "b.budgetItemExpenseId"

            gridResultVendorViewArr = new BudgetViewDatabaseService().select(selectVendorViewArr,fromVendorViewArr,whereVendorViewArr,orderByVendorViewArr,groupByVendorViewArr,'true',selectIndexVendorViewArr)

            def vendorAccountDetailsArr = gridResultVendorViewArr['dataGridList']

            if (vendorAccountDetailsArr.size() > 0){
                vendorAccountData << vendorAccountDetailsArr
            }
        }

        return vendorAccountData;
    }

    def ArrayList getCustomerBudgetAHWiseData(fiscalYearInfo,budgetCustomerArr){

        ArrayList customerAccountData = new ArrayList()

        for (int j = 0; j < budgetCustomerArr.size(); j++) {
            def glAccount = budgetCustomerArr[j][0]

            LinkedHashMap gridResultCustomerViewArr

            String selectCustomerViewArr = "b.glAccount AS ga,a.customerId AS ci,c.customerName AS cn,a.bookingPeriodStartMonth AS bpsm,SUM(b.totalPriceWithoutVat) AS tpwv,COUNT(DISTINCT b.budgetItemIncomeId) AS cbiii,b.budgetItemIncomeId AS biidi,b.id AS biii"
            String selectIndexCustomerViewArr = "ga,ci,cn,bpsm,tpwv,cbiii,biidi,biii"
            String fromCustomerViewArr = "BudgetItemIncome AS a,BudgetItemIncomeDetails AS b ,CustomerMaster AS c"

            String whereCustomerViewArr = "a.id = b.budgetItemIncomeId AND a.customerId = c.id AND b.glAccount='" + glAccount +
                    "' AND a.bookingPeriodStartMonth >= '" + fiscalYearInfo[0][3] +"' AND a.bookingPeriodStartYear >=' " + fiscalYearInfo[0][4] +
                    "' AND a.bookingPeriodEndMonth <= '" + fiscalYearInfo[0][5] + "' AND a.bookingPeriodEndYear <=' " + fiscalYearInfo[0][6] +"'"

            String orderByCustomerViewArr = "b.glAccount,a.customerId"
            String groupByCustomerViewArr = "b.budgetItemIncomeId"
            gridResultCustomerViewArr = new BudgetViewDatabaseService().select(selectCustomerViewArr,fromCustomerViewArr,whereCustomerViewArr,orderByCustomerViewArr,groupByCustomerViewArr,'true',selectIndexCustomerViewArr)

            def customerAccountDataArr = gridResultCustomerViewArr['dataGridList']

            if (customerAccountDataArr.size() > 0){
                customerAccountData << customerAccountDataArr
            }
        }

        return customerAccountData;
    }

    def ArrayList  getIncomeInvoiceData(fiscalYearInfo,budgetCustomerArr){

        ArrayList invoiceIncomeDetails = new ArrayList()

        for (int j = 0; j < budgetCustomerArr.size(); j++) {
            Integer customerId = budgetCustomerArr[j][0]
            LinkedHashMap gridResultInvoiceIncomeDetailsArr;

            String selectVendorInvoiceViewArr = "a.budgetCustomerId As temp_b_c_id,b.accountCode As temp_a_code,c.accountName As temp_a_name,a.bookingPeriod As temp_b_period,SUM(b.totalAmountWithoutVat) As temp_tawv,COUNT(DISTINCT b.invoiceId)  As temp_i_id"
            String selectIndexVendorInvoiceViewArr = "temp_b_c_id,temp_a_code,temp_a_name,temp_b_period,temp_tawv,temp_i_id"
            String fromVendorInvoiceViewArr = "InvoiceIncome AS a,InvoiceIncomeDetails AS b,ChartMaster AS c "
            String whereVendorInvoiceViewArr = "a.budgetCustomerId='" + customerId + "'  AND a.id = b.invoiceId AND b.accountCode = c.accountCode AND a.bookingYear='" + fiscalYearInfo[0][4] + "'"
            String orderByVendorInvoiceViewArr = "b.invoiceId,b.accountCode,a.budgetCustomerId"
            String groupByVendorInvoiceViewArr = "b.invoiceId,b.accountCode"

            gridResultInvoiceIncomeDetailsArr = new BudgetViewDatabaseService().select(selectVendorInvoiceViewArr,fromVendorInvoiceViewArr,whereVendorInvoiceViewArr,orderByVendorInvoiceViewArr,groupByVendorInvoiceViewArr,'true',selectIndexVendorInvoiceViewArr)
            def invoiceIncomeDetailsArr = gridResultInvoiceIncomeDetailsArr['dataGridList']

            if (invoiceIncomeDetailsArr.size() > 0){
                invoiceIncomeDetails << invoiceIncomeDetailsArr
            }
        }

        return invoiceIncomeDetails;
    }

    def setTotalIncomeInvoiceAmount(fiscalYearInfo,budgetCustomerArr){

        int nSize = budgetCustomerArr.size();
        for (int j = 0; j < nSize; j++) {
            Integer customerId = budgetCustomerArr[j][0]
            //Total invoice amount.
            String strQuery = "SELECT SUM(ii.total_gl_amount) as totalInvAmt from invoice_income as ii WHERE ii.booking_year = " +
                    " '" + fiscalYearInfo[0][4] + "' AND ii.budget_customer_id = " + customerId +
                    " AND status=1 AND isReverse=0 AND reverseInvoiceId=0 GROUP BY ii.budget_customer_id;"

            def gridResultInvoiceIncomeTotalArr = new BudgetViewDatabaseService().executeQueryAtSingle(strQuery);
            if (gridResultInvoiceIncomeTotalArr.size() > 0) {
                budgetCustomerArr[j][4] = gridResultInvoiceIncomeTotalArr[0];
            } else {
                budgetCustomerArr[j][4] = 0.0;
            }
        }
    }

    def ArrayList getExpenseInvoiceData(fiscalYearInfo,budgetVendorArr){

        ArrayList invoiceExpenseDetails = new ArrayList()

        for (int j = 0; j < budgetVendorArr.size(); j++) {
            def vendorId = budgetVendorArr[j][0]

            LinkedHashMap gridResultVendorInvoiceViewArr
            String selectVendorInvoiceViewArr = "a.budgetVendorId AS bvi,b.accountCode AS ac,c.accountName AS an,a.bookingPeriod AS bp,SUM(b.totalAmountWithoutVat) AS stawov,COUNT(DISTINCT b.invoiceId) AS cii"
            String selectIndexVendorInvoiceViewArr = "bvi,ac,an,bp,stawov,cii"
            String fromVendorInvoiceViewArr = "InvoiceExpense AS a,InvoiceExpenseDetails AS b,ChartMaster AS c"
            String whereVendorInvoiceViewArr = "a.budgetVendorId='" + vendorId + "'  AND a.id=b.invoiceId AND b.accountCode=c.accountCode AND a.bookingYear=" + fiscalYearInfo[0][4]
            String orderByVendorInvoiceViewArr = "b.invoiceId,b.accountCode,a.vendorId"
            String groupByVendorInvoiceViewArr = "b.invoiceId,b.accountCode"
            gridResultVendorInvoiceViewArr = new BudgetViewDatabaseService().select(selectVendorInvoiceViewArr,fromVendorInvoiceViewArr,whereVendorInvoiceViewArr,orderByVendorInvoiceViewArr,groupByVendorInvoiceViewArr,'true',selectIndexVendorInvoiceViewArr)

            def vendorInvoiceViewArr = gridResultVendorInvoiceViewArr['dataGridList']

            if (vendorInvoiceViewArr.size() > 0) {
                invoiceExpenseDetails << vendorInvoiceViewArr
            }

        }

        return invoiceExpenseDetails;
    }

    def ArrayList getIncomeInvoiceDataAHWise(fiscalYearInfo,budgetCustomerArr){

        ArrayList invoiceIncomeDetails = new ArrayList()

        for (int j = 0; j < budgetCustomerArr.size(); j++) {
            def glAccount = budgetCustomerArr[j][0]

            LinkedHashMap gridResultCustomerInvoiceViewArr
            String selectCustomerInvoiceViewArr = "b.accountCode AS ac, a.budgetCustomerId AS bvi, c.accountName AS an,a.bookingPeriod AS bp,SUM(b.totalAmountWithoutVat) AS stawv,COUNT(DISTINCT b.invoiceId) AS cii"
            String selectIndexCustomerInvoiceViewArr = "ac,bvi,an,bp,stawv,cii"
            String fromCustomerInvoiceViewArr = "InvoiceIncome AS a,InvoiceIncomeDetails AS b,ChartMaster AS c"
            String whereCustomerInvoiceViewArr = "b.accountCode='" + glAccount + "' AND a.id=b.invoiceId AND b.accountCode=c.accountCode AND a.bookingYear='"+fiscalYearInfo[0][4]+"'"
            String orderByCustomerInvoiceViewArr = "a.budgetCustomerId,b.accountCode,b.invoiceId"
            String groupByCustomerInvoiceViewArr = "b.invoiceId"

            gridResultCustomerInvoiceViewArr = new BudgetViewDatabaseService().select(selectCustomerInvoiceViewArr,fromCustomerInvoiceViewArr,whereCustomerInvoiceViewArr,orderByCustomerInvoiceViewArr,groupByCustomerInvoiceViewArr,'true',selectIndexCustomerInvoiceViewArr)

            def customerInvoiceViewArr = gridResultCustomerInvoiceViewArr['dataGridList']

            if (customerInvoiceViewArr.size() > 0) {
                invoiceIncomeDetails << customerInvoiceViewArr
            }
        }

        return invoiceIncomeDetails;
    }

    def ArrayList getExpenseInvoiceDataAHWise(fiscalYearInfo,budgetVendorArr){

        ArrayList invoiceExpenseDetails = new ArrayList()

        for (int j = 0; j < budgetVendorArr.size(); j++) {
            def glAccount = budgetVendorArr[j][0]

            LinkedHashMap gridResultVendorInvoiceViewArr
            String selectVendorInvoiceViewArr = "b.accountCode AS ac, a.budgetVendorId AS bvi, c.accountName AS an,a.bookingPeriod AS bp,SUM(b.totalAmountWithoutVat) AS stawv,COUNT(DISTINCT b.invoiceId) AS cii"
            String selectIndexVendorInvoiceViewArr = "ac,bvi,an,bp,stawv,cii"
            String fromVendorInvoiceViewArr = "InvoiceExpense AS a,InvoiceExpenseDetails AS b,ChartMaster AS c"
            String whereVendorInvoiceViewArr = "b.accountCode='" + glAccount + "' AND a.id=b.invoiceId AND b.accountCode=c.accountCode AND a.bookingYear='"+fiscalYearInfo[0][4]+"'"
            String orderByVendorInvoiceViewArr = "a.budgetVendorId,b.accountCode,b.invoiceId"
            String groupByVendorInvoiceViewArr = "b.invoiceId"
            gridResultVendorInvoiceViewArr = new BudgetViewDatabaseService().select(selectVendorInvoiceViewArr,fromVendorInvoiceViewArr,whereVendorInvoiceViewArr,orderByVendorInvoiceViewArr,groupByVendorInvoiceViewArr,'true',selectIndexVendorInvoiceViewArr)

            def vendorInvoiceViewArr = gridResultVendorInvoiceViewArr['dataGridList']

            if (vendorInvoiceViewArr.size() > 0) {
                invoiceExpenseDetails << vendorInvoiceViewArr
            }
        }

        return invoiceExpenseDetails;
    }

    def setTotalExpenseInvoiceAmount(fiscalYearInfo,budgetVendorArr){

        for (int j = 0; j < budgetVendorArr.size(); j++) {
            def vendorId = budgetVendorArr[j][0]
            //Total invoice amount.
            String strQuery = "SELECT SUM(ie.total_gl_amount) as totalInvAmt from invoice_expense as ie WHERE ie.booking_year = " +
                    " '" + fiscalYearInfo[0][4] + "' AND ie.budget_vendor_id = " + vendorId +
                    " AND status=1 AND isReverse=0 AND reverseInvoiceId=0 GROUP BY ie.budget_vendor_id;"

            def gridResultTotalInvoiceArr = new BudgetViewDatabaseService().executeQueryAtSingle(strQuery);
            if (gridResultTotalInvoiceArr.size() > 0) {
                budgetVendorArr[j][4] = gridResultTotalInvoiceArr[0];
            } else {
                budgetVendorArr[j][4] = 0.0;
            }
        }
    }

    def setTotalExpenseInvoiceAmountAHWise(fiscalYearInfo,expenseAHWiseArr){

        for (int j = 0; j < expenseAHWiseArr.size(); j++) {
            def accountCode = expenseAHWiseArr[j][0]
            //Total invoice amount.
            String strQuery = "SELECT SUM(ied.total_amount_without_vat) as totalInvAmt from invoice_expense_details as ied " +
                              "INNER JOIN invoice_expense as ie ON ied.invoice_id = ie.id  " +
                              "WHERE ie.booking_year = '"+ fiscalYearInfo[0][4]  +"' AND ied.account_code = '" + accountCode +
                              "' AND ie.status=1 AND ie.is_reverse=0 AND ie.reverse_invoice_id=0 GROUP BY ied.account_code;"

            def gridResultTotalInvoiceArr = new BudgetViewDatabaseService().executeQueryAtSingle(strQuery);
            if (gridResultTotalInvoiceArr.size() > 0) {
                expenseAHWiseArr[j][3] = gridResultTotalInvoiceArr[0];
            } else {
                expenseAHWiseArr[j][3] = 0.0;
            }
        }
    }

    def setTotalIncomeInvoiceAmountAHWise(fiscalYearInfo,incomeAHWiseArr){

        for (int j = 0; j < incomeAHWiseArr.size(); j++) {
            def accountCode = incomeAHWiseArr[j][0]
            //Total invoice amount.
            String strQuery = "SELECT SUM(iid.total_amount_without_vat) as totalInvAmt from invoice_income_details as iid " +
                            "INNER JOIN invoice_income as ii ON iid.invoice_id = ii.id  " +
                            "WHERE ii.booking_year = '"+ fiscalYearInfo[0][4]  +"' AND iid.account_code = '" + accountCode +
                            "' AND ii.status=1 AND ii.is_reverse=0 AND ii.reverse_invoice_id=0 GROUP BY iid.account_code;"

            def gridResultTotalInvoiceArr = new BudgetViewDatabaseService().executeQueryAtSingle(strQuery);
            if (gridResultTotalInvoiceArr.size() > 0) {
                incomeAHWiseArr[j][3] = gridResultTotalInvoiceArr[0];
            } else {
                incomeAHWiseArr[j][3] = 0.0;
            }
        }
    }

    def getBudgetIncomeForecastData(fiscalYearInfo){

        LinkedHashMap gridResultBudgetForecastArr

        String selectBudgetForecastArr = "id,SUM(totalGlAmount) AS total_amount,bookingPeriodStartMonth As bpsm"
        String selectIndexBudgetForecastArr = "id,total_amount,bpsm"
        String fromBudgetForecastArr = "BudgetItemIncome"
        String whereBudgetForecastArr = "bookingPeriodStartYear='"+fiscalYearInfo[0][4] +"' AND status=1"
        String orderByBudgetForecastArr = "bookingPeriodStartMonth,id"
        String groupByBudgetForecastArr = "bookingPeriodStartMonth,id"

        gridResultBudgetForecastArr = new BudgetViewDatabaseService().select(selectBudgetForecastArr,fromBudgetForecastArr,whereBudgetForecastArr,orderByBudgetForecastArr,groupByBudgetForecastArr,'true',selectIndexBudgetForecastArr)

        def budgetForecastArr =  gridResultBudgetForecastArr['dataGridList']

        return budgetForecastArr;
    }

    def getInvoiceIncomeForecastData(fiscalYearInfo){

        LinkedHashMap gridResultInvoiceForecastArr
        String selectInvoiceForecastArr = "budgetItemIncomeId AS bi_id,SUM(totalGlAmount) AS total_amount,bookingPeriod As bp"
        String selectIndexInvoiceForecastArr = "bi_id,total_amount,bp"
        String fromInvoiceForecastArr = "InvoiceIncome"
        String whereInvoiceForecastArr = "bookingYear='" + fiscalYearInfo[0][4] + "'  AND status=1 AND isReverse=0 AND reverseInvoiceId=0"
        String orderByInvoiceForecastArr = "bookingPeriod,budgetItemIncomeId"
        String groupByInvoiceForecastArr = "bookingPeriod,budgetItemIncomeId"
        gridResultInvoiceForecastArr = new BudgetViewDatabaseService().select(selectInvoiceForecastArr,fromInvoiceForecastArr,whereInvoiceForecastArr,orderByInvoiceForecastArr,groupByInvoiceForecastArr,'true',selectIndexInvoiceForecastArr)

        def InvoiceForecastArr = gridResultInvoiceForecastArr['dataGridList']

        return InvoiceForecastArr;
    }

    def Map prepareCurrentForecastForIncomeNameWise(budgetForecastArr,invoiceForecastArr){

        Map currentForecastArr = ["janTotal": 0.00, "febTotal": 0.00, "marTotal": 0.00, "aprTotal": 0.00, "mayTotal": 0.00, "junTotal": 0.00,
                                   "julTotal": 0.00, "augTotal": 0.00, "sepTotal": 0.00, "octTotal": 0.00, "novTotal": 0.00,"decTotal":0.00]


        budgetForecastArr.eachWithIndex {BudgetForecastIndex, BudgetForecastKey ->
            def tempBudetId =  BudgetForecastIndex[0]
            def tempBudetAmount =  BudgetForecastIndex[1]
            def tempBudetPeriod =  BudgetForecastIndex[2]

            Integer flagFound = 0

            invoiceForecastArr.eachWithIndex {InvoiceForecastIndex, InvoiceForecastKey ->

                def tempInvoiceId =  InvoiceForecastIndex[0]
                def tempInvoiceAmount =  InvoiceForecastIndex[1]
                def tempInvoicePeriod =  InvoiceForecastIndex[2]

                if (tempBudetId == tempInvoiceId){

                    if (Integer.parseInt(tempInvoicePeriod)==1){
                        currentForecastArr.janTotal= currentForecastArr.janTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==2){
                        currentForecastArr.febTotal=  currentForecastArr.febTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==3){
                        currentForecastArr.marTotal= currentForecastArr.marTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==4){
                        currentForecastArr.aprTotal= currentForecastArr.aprTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==5){
                        currentForecastArr.mayTotal=  currentForecastArr.mayTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==6){
                        currentForecastArr.junTotal= currentForecastArr.junTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==7){
                        currentForecastArr.julTotal= currentForecastArr.julTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==8){
                        currentForecastArr.augTotal= currentForecastArr.augTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==9){
                        currentForecastArr.sepTotal= currentForecastArr.sepTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==10){
                        currentForecastArr.octTotal= currentForecastArr.octTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==11){
                        currentForecastArr.novTotal= currentForecastArr.novTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==12){
                        currentForecastArr.decTotal= currentForecastArr.decTotal+tempInvoiceAmount
                        flagFound=1
                    }
                }
            }

            if ( flagFound==0){
                if (tempBudetPeriod==1){
                    currentForecastArr.janTotal= currentForecastArr.janTotal+tempBudetAmount
                }else if (tempBudetPeriod==2){
                    currentForecastArr.febTotal=  currentForecastArr.febTotal+tempBudetAmount
                }else if (tempBudetPeriod==3){
                    currentForecastArr.marTotal= currentForecastArr.marTotal+tempBudetAmount
                }else if (tempBudetPeriod==4){
                    currentForecastArr.aprTotal= currentForecastArr.aprTotal+tempBudetAmount
                }else if (tempBudetPeriod==5){
                    currentForecastArr.mayTotal=  currentForecastArr.mayTotal+tempBudetAmount
                }else if (tempBudetPeriod==6){
                    currentForecastArr.junTotal= currentForecastArr.junTotal+tempBudetAmount
                }else if (tempBudetPeriod==7){
                    currentForecastArr.julTotal= currentForecastArr.julTotal+tempBudetAmount
                }else if (tempBudetPeriod==8){
                    currentForecastArr.augTotal= currentForecastArr.augTotal+tempBudetAmount
                }else if (tempBudetPeriod==9){
                    currentForecastArr.sepTotal= currentForecastArr.sepTotal+tempBudetAmount
                }else if (tempBudetPeriod==10){
                    currentForecastArr.octTotal= currentForecastArr.octTotal+tempBudetAmount
                }else if (tempBudetPeriod==11){
                    currentForecastArr.novTotal= currentForecastArr.novTotal+tempBudetAmount
                }else if (tempBudetPeriod==12){
                    currentForecastArr.decTotal= currentForecastArr.decTotal+tempBudetAmount
                }

            }
        }

        return currentForecastArr;
    }

    def getInvoiceExpenseForecastData(fiscalYearInfo){

        LinkedHashMap gridResultInvoiceForecastArr
        String selectInvoiceForecastArr="budgetItemExpenseId AS bi_id,SUM(totalGlAmount) AS total_amount,bookingPeriod AS bp"
        String selectIndexInvoiceForecastArr="bi_id,total_amount,bp"
        String fromInvoiceForecastArr="InvoiceExpense"
        String whereInvoiceForecastArr="bookingYear='"+fiscalYearInfo[0][4]+"'  AND status=1 AND isReverse=0 AND reverseInvoiceId=0"
        String orderByInvoiceForecastArr="bookingPeriod,budgetItemExpenseId"
        String groupByInvoiceForecastArr="bookingPeriod,budgetItemExpenseId"
        gridResultInvoiceForecastArr=new BudgetViewDatabaseService().select(selectInvoiceForecastArr,fromInvoiceForecastArr,whereInvoiceForecastArr,orderByInvoiceForecastArr,groupByInvoiceForecastArr,'true',selectIndexInvoiceForecastArr)

        def invoiceForecastArr =gridResultInvoiceForecastArr['dataGridList']

        return invoiceForecastArr;
    }

    def getBudgetExpenseForecastData(fiscalYearInfo){

        LinkedHashMap gridResultBudgetForecastArr
        String selectBudgetForecastArr="id,SUM(totalGlAmount) AS total_amount,bookingPeriodStartMonth AS bpsm"
        String selectIndexBudgetForecastArr="id,total_amount,bpsm"
        String fromBudgetForecastArr="BudgetItemExpense"
        String whereBudgetForecastArr="bookingPeriodStartYear='"+fiscalYearInfo[0][4] +"' AND status=1"
        String orderByBudgetForecastArr="bookingPeriodStartMonth,id"
        String groupByBudgetForecastArr="bookingPeriodStartMonth,id"
        gridResultBudgetForecastArr=new BudgetViewDatabaseService().select(selectBudgetForecastArr,fromBudgetForecastArr,whereBudgetForecastArr,orderByBudgetForecastArr,groupByBudgetForecastArr,'true',selectIndexBudgetForecastArr)

        def budgetForecastArr =  gridResultBudgetForecastArr['dataGridList']

        return budgetForecastArr;
    }

    def Map prepareCurrentForecastForExpenseNameWise(budgetForecastArr,invoiceForecastArr){

        Map currentForecastArr = ["janTotal": 0, "febTotal": 0, "marTotal": 0, "aprTotal": 0, "mayTotal": 0, "junTotal": 0,
                                  "julTotal": 0, "augTotal": 0, "sepTotal": 0, "octTotal": 0, "novTotal": 0,"decTotal": 0]

        budgetForecastArr.eachWithIndex {BudgetForecastIndex, BudgetForecastKey ->
            def tempBudetId =  BudgetForecastIndex[0]
            def tempBudetAmount =  BudgetForecastIndex[1]
            def tempBudetPeriod =  BudgetForecastIndex[2]

            Integer flagFound = 0

            invoiceForecastArr.eachWithIndex {InvoiceForecastIndex, InvoiceForecastKey ->

                def tempInvoiceId=  InvoiceForecastIndex[0]
                def tempInvoiceAmount=  InvoiceForecastIndex[1]
                def tempInvoicePeriod=  InvoiceForecastIndex[2]

                if (tempBudetId == tempInvoiceId){

                    if (Integer.parseInt(tempInvoicePeriod)==1){
                        currentForecastArr.janTotal= currentForecastArr.janTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==2){
                        currentForecastArr.febTotal=  currentForecastArr.febTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==3){
                        currentForecastArr.marTotal= currentForecastArr.marTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==4){
                        currentForecastArr.aprTotal= currentForecastArr.aprTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==5){
                        currentForecastArr.mayTotal=  currentForecastArr.mayTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==6){
                        currentForecastArr.junTotal= currentForecastArr.junTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==7){
                        currentForecastArr.julTotal= currentForecastArr.julTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==8){
                        currentForecastArr.augTotal= currentForecastArr.augTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==9){
                        currentForecastArr.sepTotal= currentForecastArr.sepTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==10){
                        currentForecastArr.octTotal= currentForecastArr.octTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==11){
                        currentForecastArr.novTotal= currentForecastArr.novTotal+tempInvoiceAmount
                        flagFound=1
                    }else if (Integer.parseInt(tempInvoicePeriod)==12){
                        currentForecastArr.decTotal= currentForecastArr.decTotal+tempInvoiceAmount
                        flagFound=1
                    }
                }
            }

            if ( flagFound==0){
                if (tempBudetPeriod==1){
                    currentForecastArr.janTotal = currentForecastArr.janTotal+tempBudetAmount
                }else if (tempBudetPeriod == 2){
                    currentForecastArr.febTotal=  currentForecastArr.febTotal+tempBudetAmount
                }else if (tempBudetPeriod == 3){
                    currentForecastArr.marTotal= currentForecastArr.marTotal+tempBudetAmount
                }else if (tempBudetPeriod == 4){
                    currentForecastArr.aprTotal= currentForecastArr.aprTotal+tempBudetAmount
                }else if (tempBudetPeriod == 5){
                    currentForecastArr.mayTotal=  currentForecastArr.mayTotal+tempBudetAmount
                }else if (tempBudetPeriod == 6){
                    currentForecastArr.junTotal = currentForecastArr.junTotal+tempBudetAmount
                }else if (tempBudetPeriod ==7){
                    currentForecastArr.julTotal = currentForecastArr.julTotal+tempBudetAmount
                }else if (tempBudetPeriod == 8){
                    currentForecastArr.augTotal = currentForecastArr.augTotal+tempBudetAmount
                }else if (tempBudetPeriod == 9){
                    currentForecastArr.sepTotal = currentForecastArr.sepTotal+tempBudetAmount
                }else if (tempBudetPeriod == 10){
                    currentForecastArr.octTotal= currentForecastArr.octTotal+tempBudetAmount
                }else if (tempBudetPeriod == 11){
                    currentForecastArr.novTotal = currentForecastArr.novTotal+tempBudetAmount
                }else if (tempBudetPeriod == 12){
                    currentForecastArr.decTotal = currentForecastArr.decTotal+tempBudetAmount
                }

            }
        }

        return currentForecastArr;
    }

    def Map getInvoiceIncomeTotalMonthWise(fiscalYearInfo){

        Map totalInvoiceIncome = ["janTotal": 0.00, "febTotal": 0.00, "marTotal": 0.00, "aprTotal": 0.00, "mayTotal": 0.00, "junTotal": 0.00,
                                  "julTotal": 0.00, "augTotal": 0.00, "sepTotal": 0.00, "octTotal": 0.00, "novTotal": 0.00,"decTotal": 0.00,
                                  "allTotal": 0.00]


        LinkedHashMap gridResultTotalInvoiceIncomeArr
        String selectTotalInvoiceIncomeArr="bookingPeriod AS bp,SUM(totalGlAmount) AS total_income"
        String selectIndexTotalInvoiceIncomeArr="bp,total_income"
        String fromTotalInvoiceIncomeArr="InvoiceIncome "
        String whereTotalInvoiceIncomeArr="bookingYear ='" + fiscalYearInfo[0][4] + "' AND status=1 AND isReverse=0 AND reverseInvoiceId=0"
        String orderByTotalInvoiceIncomeArr=""
        String groupByTotalInvoiceIncomeArr="bookingPeriod"
        gridResultTotalInvoiceIncomeArr=new BudgetViewDatabaseService().select(selectTotalInvoiceIncomeArr,fromTotalInvoiceIncomeArr,whereTotalInvoiceIncomeArr,orderByTotalInvoiceIncomeArr,groupByTotalInvoiceIncomeArr,'true',selectIndexTotalInvoiceIncomeArr)

        ArrayList totalInvoiceIncomeArr = gridResultTotalInvoiceIncomeArr['dataGridList']

        def allTotal = 0.00;
        totalInvoiceIncomeArr.eachWithIndex {incomeTotalIndex, incomeTotalKey ->
            if (Integer.parseInt(incomeTotalIndex[0])==1){
                totalInvoiceIncome.janTotal = incomeTotalIndex[1]
                allTotal += totalInvoiceIncome.janTotal;
            }else if (Integer.parseInt(incomeTotalIndex[0])==2){
                totalInvoiceIncome.febTotal = incomeTotalIndex[1]
                allTotal += totalInvoiceIncome.febTotal;
            }else if (Integer.parseInt(incomeTotalIndex[0])==3){
                totalInvoiceIncome.marTotal = incomeTotalIndex[1]
                allTotal += totalInvoiceIncome.marTotal;
            }else if (Integer.parseInt(incomeTotalIndex[0])==4){
                totalInvoiceIncome.aprTotal = incomeTotalIndex[1]
                allTotal += totalInvoiceIncome.aprTotal;
            }else if (Integer.parseInt(incomeTotalIndex[0])==5){
                totalInvoiceIncome.mayTotal =incomeTotalIndex[1]
                allTotal += totalInvoiceIncome.mayTotal;
            }else if (Integer.parseInt(incomeTotalIndex[0])==6){
                totalInvoiceIncome.junTotal =incomeTotalIndex[1]
                allTotal += totalInvoiceIncome.junTotal;
            }else if (Integer.parseInt(incomeTotalIndex[0])==7){
                totalInvoiceIncome.julTotal =incomeTotalIndex[1]
                allTotal += totalInvoiceIncome.julTotal;
            }else if (Integer.parseInt(incomeTotalIndex[0])==8){
                totalInvoiceIncome.augTotal = incomeTotalIndex[1]
                allTotal += totalInvoiceIncome.augTotal;
            }else if (Integer.parseInt(incomeTotalIndex[0])==9){
                totalInvoiceIncome.sepTotal = incomeTotalIndex[1]
                allTotal += totalInvoiceIncome.sepTotal;
            }else if (Integer.parseInt(incomeTotalIndex[0])==10){
                totalInvoiceIncome.octTotal = incomeTotalIndex[1]
                allTotal += totalInvoiceIncome.octTotal;
            }else if (Integer.parseInt(incomeTotalIndex[0])==11){
                totalInvoiceIncome.novTotal = incomeTotalIndex[1]
                allTotal += totalInvoiceIncome.novTotal;
            }else if (Integer.parseInt(incomeTotalIndex[0])==12){
                totalInvoiceIncome.decTotal = incomeTotalIndex[1]
                allTotal += totalInvoiceIncome.decTotal;
            }
        }

        totalInvoiceIncome.allTotal = allTotal;

        return totalInvoiceIncome;
    }

    def Map getInvoiceExpenseTotalMonthWise(fiscalYearInfo) {

        Map totalInvoiceExpense = ["janTotal": 0.00, "febTotal": 0.00, "marTotal": 0.00, "aprTotal": 0.00, "mayTotal": 0.00, "junTotal": 0.00,
                                   "julTotal": 0.00, "augTotal": 0.00, "sepTotal": 0.00, "octTotal": 0.00, "novTotal": 0.00, "decTotal": 0.00,
                                    "allTotal":0.00]


        String selectTotalInvoiceExpenseArr = "bookingPeriod AS bp,SUM(totalGlAmount) AS total_expense"
        String selectIndexTotalInvoiceExpenseArr = "bp,total_expense"
        String fromTotalInvoiceExpenseArr = "InvoiceExpense "
        String whereTotalInvoiceExpenseArr = "bookingYear ='" + fiscalYearInfo[0][4] + "' AND status=1 AND isReverse=0 AND reverseInvoiceId=0"
        String orderByTotalInvoiceExpenseArr = ""
        String groupByTotalInvoiceExpenseArr = "bookingPeriod"

        LinkedHashMap gridResultTotalInvoiceExpenseArr
        gridResultTotalInvoiceExpenseArr = new BudgetViewDatabaseService().select(selectTotalInvoiceExpenseArr,fromTotalInvoiceExpenseArr,whereTotalInvoiceExpenseArr,orderByTotalInvoiceExpenseArr,groupByTotalInvoiceExpenseArr,'true',selectIndexTotalInvoiceExpenseArr)

        ArrayList totalInvoiceExpenseArr = gridResultTotalInvoiceExpenseArr['dataGridList']
        def allTotal = 0.00;
        totalInvoiceExpenseArr.eachWithIndex {expenseTotalIndex, expenseGrossKey ->
            if (Integer.parseInt(expenseTotalIndex[0]) == 1){
                totalInvoiceExpense.janTotal = expenseTotalIndex[1]
                allTotal += totalInvoiceExpense.janTotal;
            }else if (Integer.parseInt(expenseTotalIndex[0]) == 2){
                totalInvoiceExpense.febTotal = expenseTotalIndex[1]
                allTotal += totalInvoiceExpense.febTotal;
            }else if (Integer.parseInt(expenseTotalIndex[0]) == 3){
                totalInvoiceExpense.marTotal = expenseTotalIndex[1]
                allTotal += totalInvoiceExpense.marTotal;
            }else if (Integer.parseInt(expenseTotalIndex[0]) == 4){
                totalInvoiceExpense.aprTotal = expenseTotalIndex[1]
                allTotal += totalInvoiceExpense.aprTotal;
            }else if (Integer.parseInt(expenseTotalIndex[0]) == 5){
                totalInvoiceExpense.mayTotal = expenseTotalIndex[1]
                allTotal += totalInvoiceExpense.mayTotal;
            }else if (Integer.parseInt(expenseTotalIndex[0]) == 6){
                totalInvoiceExpense.junTotal = expenseTotalIndex[1]
                allTotal += totalInvoiceExpense.junTotal;
            }else if (Integer.parseInt(expenseTotalIndex[0]) == 7){
                totalInvoiceExpense.julTotal = expenseTotalIndex[1]
                allTotal += totalInvoiceExpense.julTotal;
            }else if (Integer.parseInt(expenseTotalIndex[0]) == 8){
                totalInvoiceExpense.augTotal = expenseTotalIndex[1]
                allTotal += totalInvoiceExpense.augTotal;
            }else if (Integer.parseInt(expenseTotalIndex[0]) == 9){
                totalInvoiceExpense.sepTotal = expenseTotalIndex[1]
                allTotal += totalInvoiceExpense.sepTotal;
            }else if (Integer.parseInt(expenseTotalIndex[0]) == 10){
                totalInvoiceExpense.octTotal = expenseTotalIndex[1]
                allTotal += totalInvoiceExpense.octTotal;
            }else if (Integer.parseInt(expenseTotalIndex[0]) == 11){
                totalInvoiceExpense.novTotal = expenseTotalIndex[1]
                allTotal += totalInvoiceExpense.novTotal;
            }else if (Integer.parseInt(expenseTotalIndex[0]) == 12){
                totalInvoiceExpense.decTotal =  expenseTotalIndex[1]
                allTotal += totalInvoiceExpense.decTotal;
            }
        }

        totalInvoiceExpense.allTotal = allTotal;

        return totalInvoiceExpense;
    }

    def Map getIncomeBudgetTotalMonthWise(fiscalYearInfo) {

        Map totalIncomeBudget = ["janTotal": 0.00, "febTotal": 0.00, "marTotal": 0.00, "aprTotal": 0.00, "mayTotal": 0.00, "junTotal": 0.00,
                                 "julTotal": 0.00, "augTotal": 0.00, "sepTotal": 0.00, "octTotal": 0.00, "novTotal": 0.00, "decTotal": 0.00,
                                 "allTotal":0.00]


        LinkedHashMap gridResultTotalBudgetIncomeArr
        String selectTotalBudgetIncomeArr = "bookingPeriodStartMonth AS bpsm,SUM(totalGlAmount) AS total_income_budget"
        String selectIndexTotalBudgetIncomeArr = "bpsm,total_income_budget"
        String fromTotalBudgetIncomeArr = "BudgetItemIncome "
        String whereTotalBudgetIncomeArr = "bookingPeriodStartYear ='" + fiscalYearInfo[0][4] + "' AND status=1"
        String orderByTotalBudgetIncomeArr = ""
        String groupByTotalBudgetIncomeArr = "bookingPeriodStartMonth"
        gridResultTotalBudgetIncomeArr = new BudgetViewDatabaseService().select(selectTotalBudgetIncomeArr,fromTotalBudgetIncomeArr,whereTotalBudgetIncomeArr,orderByTotalBudgetIncomeArr,groupByTotalBudgetIncomeArr,'true',selectIndexTotalBudgetIncomeArr)

        ArrayList totalBudgetIncomeArr = gridResultTotalBudgetIncomeArr['dataGridList']

        def allTotal = 0.00
        totalBudgetIncomeArr.eachWithIndex {incomeBudgetIndex, incomeBudgetKey ->
            if (incomeBudgetIndex[0] == 1){
                totalIncomeBudget.janTotal = incomeBudgetIndex[1]
                allTotal += totalIncomeBudget.janTotal
            }else if (incomeBudgetIndex[0] == 2){
                totalIncomeBudget.febTotal = incomeBudgetIndex[1]
                allTotal += totalIncomeBudget.febTotal
            }else if (incomeBudgetIndex[0] == 3){
                totalIncomeBudget.marTotal = incomeBudgetIndex[1]
                allTotal += totalIncomeBudget.marTotal
            }else if (incomeBudgetIndex[0] == 4){
                totalIncomeBudget.aprTotal = incomeBudgetIndex[1]
                allTotal += totalIncomeBudget.aprTotal
            }else if (incomeBudgetIndex[0] == 5){
                totalIncomeBudget.mayTotal = incomeBudgetIndex[1]
                allTotal += totalIncomeBudget.mayTotal
            }else if (incomeBudgetIndex[0] == 6){
                totalIncomeBudget.junTotal = incomeBudgetIndex[1]
                allTotal += totalIncomeBudget.junTotal
            }else if (incomeBudgetIndex[0] == 7){
                totalIncomeBudget.julTotal = incomeBudgetIndex[1]
                allTotal += totalIncomeBudget.julTotal
            }else if (incomeBudgetIndex[0] == 8){
                totalIncomeBudget.augTotal = incomeBudgetIndex[1]
                allTotal += totalIncomeBudget.augTotal
            }else if (incomeBudgetIndex[0] == 9){
                totalIncomeBudget.sepTotal = incomeBudgetIndex[1]
                allTotal += totalIncomeBudget.sepTotal
            }else if (incomeBudgetIndex[0] == 10){
                totalIncomeBudget.octTotal = incomeBudgetIndex[1]
                allTotal += totalIncomeBudget.octTotal
            }else if (incomeBudgetIndex[0] == 11){
                totalIncomeBudget.novTotal = incomeBudgetIndex[1]
                allTotal += totalIncomeBudget.novTotal
            }else if (incomeBudgetIndex[0] == 12){
                totalIncomeBudget.decTotal =  incomeBudgetIndex[1]
                allTotal += totalIncomeBudget.decTotal
            }
        }

        totalIncomeBudget.allTotal = allTotal

        return totalIncomeBudget;
    }

    def Map getExpenseBudgetTotalMonthWise(fiscalYearInfo) {

        Map totalExpenseBudget = ["janTotal": 0.00, "febTotal": 0.00, "marTotal": 0.00, "aprTotal": 0.00, "mayTotal": 0.00, "junTotal": 0.00,
                                 "julTotal": 0.00, "augTotal": 0.00, "sepTotal": 0.00, "octTotal": 0.00, "novTotal": 0.00, "decTotal": 0.00,
                                 "allTotal":0.00]


        String selectTotalBudgetExpenseArr="bookingPeriodStartMonth AS bpsm,SUM(totalGlAmount) AS total_expense_budget"
        String selectIndexTotalBudgetExpenseArr="bpsm,total_expense_budget"
        String fromTotalBudgetExpenseArr="BudgetItemExpense "
        String whereTotalBudgetExpenseArr="bookingPeriodStartYear ='" + fiscalYearInfo[0][4] + "' AND status=1"
        String orderByTotalBudgetExpenseArr = ""
        String groupByTotalBudgetExpenseArr = "bookingPeriodStartMonth"

        LinkedHashMap gridResultTotalBudgetExpenseArr
        gridResultTotalBudgetExpenseArr = new BudgetViewDatabaseService().select(selectTotalBudgetExpenseArr,fromTotalBudgetExpenseArr,whereTotalBudgetExpenseArr,orderByTotalBudgetExpenseArr,groupByTotalBudgetExpenseArr,'true',selectIndexTotalBudgetExpenseArr)

        ArrayList totalBudgetExpenseArr = gridResultTotalBudgetExpenseArr['dataGridList']

        def allTotal = 0.00;
        totalBudgetExpenseArr.eachWithIndex {expenseBudgetIndex, incomeBudgetKey ->
            if (expenseBudgetIndex[0] == 1){
                totalExpenseBudget.janTotal = expenseBudgetIndex[1]
                allTotal += totalExpenseBudget.janTotal;
            }else if (expenseBudgetIndex[0] == 2){
                totalExpenseBudget.febTotal = expenseBudgetIndex[1]
                allTotal += totalExpenseBudget.febTotal;
            }else if (expenseBudgetIndex[0] == 3){
                totalExpenseBudget.marTotal = expenseBudgetIndex[1]
                allTotal += totalExpenseBudget.marTotal;
            }else if (expenseBudgetIndex[0] == 4){
                totalExpenseBudget.aprTotal = expenseBudgetIndex[1]
                allTotal += totalExpenseBudget.aprTotal;
            }else if (expenseBudgetIndex[0] == 5){
                totalExpenseBudget.mayTotal = expenseBudgetIndex[1]
                allTotal += totalExpenseBudget.mayTotal;
            }else if (expenseBudgetIndex[0] == 6){
                totalExpenseBudget.junTotal = expenseBudgetIndex[1]
                allTotal += totalExpenseBudget.junTotal;
            }else if (expenseBudgetIndex[0] == 7){
                totalExpenseBudget.julTotal = expenseBudgetIndex[1]
                allTotal += totalExpenseBudget.julTotal;
            }else if (expenseBudgetIndex[0] == 8){
                totalExpenseBudget.augTotal = expenseBudgetIndex[1]
                allTotal += totalExpenseBudget.augTotal;
            }else if (expenseBudgetIndex[0] == 9){
                totalExpenseBudget.sepTotal = expenseBudgetIndex[1]
                allTotal += totalExpenseBudget.sepTotal;
            }else if (expenseBudgetIndex[0] == 10){
                totalExpenseBudget.octTotal = expenseBudgetIndex[1]
                allTotal += totalExpenseBudget.octTotal;
            }else if (expenseBudgetIndex[0] == 11){
                totalExpenseBudget.novTotal = expenseBudgetIndex[1]
                allTotal += totalExpenseBudget.novTotal;
            }else if (expenseBudgetIndex[0] == 12){
                totalExpenseBudget.decTotal =  expenseBudgetIndex[1]
                allTotal += totalExpenseBudget.decTotal;
            }
        }

        return totalExpenseBudget;
    }

    def Map getNetProfitBasedOnInvoice(grossProfitInvoice,incomeTaxPercentage) {
        Map netProfit = ["janTotal": 0.00, "febTotal": 0.00, "marTotal": 0.00, "aprTotal": 0.00, "mayTotal": 0.00, "junTotal": 0.00,
                           "julTotal": 0.00, "augTotal": 0.00, "sepTotal": 0.00, "octTotal": 0.00, "novTotal": 0.00,"decTotal":0.00]


        for(int i=0;i<12;i++){
            if(i == 0){
                netProfit.janTotal = grossProfitInvoice.janTotal - (grossProfitInvoice.janTotal*incomeTaxPercentage)/100;
            }else if(i == 1){
                netProfit.febTotal = grossProfitInvoice.febTotal - (grossProfitInvoice.febTotal*incomeTaxPercentage)/100;
            }else if(i == 2){
                netProfit.marTotal = grossProfitInvoice.marTotal - (grossProfitInvoice.marTotal*incomeTaxPercentage)/100;
            }else if(i == 3){
                netProfit.aprTotal = grossProfitInvoice.aprTotal - (grossProfitInvoice.aprTotal*incomeTaxPercentage)/100;
            }else if(i == 4){
                netProfit.mayTotal = grossProfitInvoice.mayTotal - (grossProfitInvoice.mayTotal*incomeTaxPercentage)/100;
            }else if(i == 5){
                netProfit.junTotal = grossProfitInvoice.junTotal - (grossProfitInvoice.junTotal*incomeTaxPercentage)/100;
            }else if(i == 6){
                netProfit.julTotal = grossProfitInvoice.julTotal - (grossProfitInvoice.julTotal*incomeTaxPercentage)/100;
            }else if(i == 7){
                netProfit.augTotal = grossProfitInvoice.augTotal - (grossProfitInvoice.augTotal*incomeTaxPercentage)/100;
            }else if(i == 8){
                netProfit.sepTotal = grossProfitInvoice.sepTotal - (grossProfitInvoice.sepTotal*incomeTaxPercentage)/100;
            }else if(i == 9){
                netProfit.octTotal = grossProfitInvoice.octTotal - (grossProfitInvoice.octTotal*incomeTaxPercentage)/100;
            }else if(i == 10){
                netProfit.novTotal = grossProfitInvoice.novTotal - (grossProfitInvoice.novTotal*incomeTaxPercentage)/100;
            }else if(i == 11){
                netProfit.decTotal = grossProfitInvoice.decTotal - (grossProfitInvoice.decTotal*incomeTaxPercentage)/100;
            }
        }

        return netProfit;
    }

    def Map getNetProfitBasedOnBooking(grossProfitBooking,incomeTaxPercentage){

        Map netProfit = ["janTotal": 0.00, "febTotal": 0.00, "marTotal": 0.00, "aprTotal": 0.00, "mayTotal": 0.00, "junTotal": 0.00,
                         "julTotal": 0.00, "augTotal": 0.00, "sepTotal": 0.00, "octTotal": 0.00, "novTotal": 0.00,"decTotal":0.00,
                         "allTotal": 0.00]

        def allTotal = 0.00;
        for(int i=0;i<12;i++){
            if(i == 0){
                netProfit.janTotal = grossProfitBooking.janTotal - (grossProfitBooking.janTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.janTotal;
            }else if(i == 1){
                netProfit.febTotal = grossProfitBooking.febTotal - (grossProfitBooking.febTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.febTotal;
            }else if(i == 2){
                netProfit.marTotal = grossProfitBooking.marTotal - (grossProfitBooking.marTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.marTotal;
            }else if(i == 3){
                netProfit.aprTotal = grossProfitBooking.aprTotal - (grossProfitBooking.aprTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.aprTotal;
            }else if(i == 4){
                netProfit.mayTotal = grossProfitBooking.mayTotal - (grossProfitBooking.mayTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.mayTotal;
            }else if(i == 5){
                netProfit.junTotal = grossProfitBooking.junTotal - (grossProfitBooking.junTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.junTotal;
            }else if(i == 6){
                netProfit.julTotal = grossProfitBooking.julTotal - (grossProfitBooking.julTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.julTotal;
            }else if(i == 7){
                netProfit.augTotal = grossProfitBooking.augTotal - (grossProfitBooking.augTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.augTotal;
            }else if(i == 8){
                netProfit.sepTotal = grossProfitBooking.sepTotal - (grossProfitBooking.sepTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.sepTotal;
            }else if(i == 9){
                netProfit.octTotal = grossProfitBooking.octTotal - (grossProfitBooking.octTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.octTotal;
            }else if(i == 10){
                netProfit.novTotal = grossProfitBooking.novTotal - (grossProfitBooking.novTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.novTotal;
            }else if(i == 11){
                netProfit.decTotal = grossProfitBooking.decTotal - (grossProfitBooking.decTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.decTotal;
            }
        }

        netProfit.allTotal = allTotal;

        return netProfit;
    }

    def Map getNetProfitBasedOnBudget(grossProfitBudget,incomeTaxPercentage) {
        Map netProfit = ["janTotal": 0.00, "febTotal": 0.00, "marTotal": 0.00, "aprTotal": 0.00, "mayTotal": 0.00, "junTotal": 0.00,
                         "julTotal": 0.00, "augTotal": 0.00, "sepTotal": 0.00, "octTotal": 0.00, "novTotal": 0.00,"decTotal":0.00,
                         "allTotal": 0.00]

        def allTotal = 0.00;
        for(int i=0;i<12;i++){
            if(i == 0){
                netProfit.janTotal = grossProfitBudget.janTotal - (grossProfitBudget.janTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.janTotal;
            }else if(i == 1){
                netProfit.febTotal = grossProfitBudget.febTotal - (grossProfitBudget.febTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.febTotal;
            }else if(i == 2){
                netProfit.marTotal = grossProfitBudget.marTotal - (grossProfitBudget.marTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.marTotal;
            }else if(i == 3){
                netProfit.aprTotal = grossProfitBudget.aprTotal - (grossProfitBudget.aprTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.aprTotal;
            }else if(i == 4){
                netProfit.mayTotal = grossProfitBudget.mayTotal - (grossProfitBudget.mayTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.mayTotal;
            }else if(i == 5){
                netProfit.junTotal = grossProfitBudget.junTotal - (grossProfitBudget.junTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.junTotal;
            }else if(i == 6){
                netProfit.julTotal = grossProfitBudget.julTotal - (grossProfitBudget.julTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.julTotal;
            }else if(i == 7){
                netProfit.augTotal = grossProfitBudget.augTotal - (grossProfitBudget.augTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.augTotal;
            }else if(i == 8){
                netProfit.sepTotal = grossProfitBudget.sepTotal - (grossProfitBudget.sepTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.sepTotal;
            }else if(i == 9){
                netProfit.octTotal = grossProfitBudget.octTotal - (grossProfitBudget.octTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.octTotal;
            }else if(i == 10){
                netProfit.novTotal = grossProfitBudget.novTotal - (grossProfitBudget.novTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.novTotal;
            }else if(i == 11){
                netProfit.decTotal = grossProfitBudget.decTotal - (grossProfitBudget.decTotal*incomeTaxPercentage)/100;
                allTotal += netProfit.decTotal;
            }
        }

        netProfit.allTotal = allTotal;

        return netProfit;
    }

    def Map getGrossProfitBasedOnInvoice(totalInvoiceIncome,totalInvoiceExpense) {

        Map grossProfit = ["janTotal": 0.00, "febTotal": 0.00, "marTotal": 0.00, "aprTotal": 0.00, "mayTotal": 0.00, "junTotal": 0.00,
                           "julTotal": 0.00, "augTotal": 0.00, "sepTotal": 0.00, "octTotal": 0.00, "novTotal": 0.00,"decTotal":0.00]


        for(int i=0;i<12;i++){
            if(i == 0){
                grossProfit.janTotal = totalInvoiceIncome.janTotal - totalInvoiceExpense.janTotal
            }else if(i == 1){
                grossProfit.febTotal = totalInvoiceIncome.febTotal - totalInvoiceExpense.febTotal
            }else if(i == 2){
                grossProfit.marTotal = totalInvoiceIncome.marTotal - totalInvoiceExpense.marTotal
            }else if(i == 3){
                grossProfit.aprTotal = totalInvoiceIncome.aprTotal - totalInvoiceExpense.aprTotal
            }else if(i == 4){
                grossProfit.mayTotal = totalInvoiceIncome.mayTotal - totalInvoiceExpense.mayTotal
            }else if(i == 5){
                grossProfit.junTotal = totalInvoiceIncome.junTotal - totalInvoiceExpense.junTotal
            }else if(i == 6){
                grossProfit.julTotal = totalInvoiceIncome.julTotal - totalInvoiceExpense.julTotal
            }else if(i == 7){
                grossProfit.augTotal = totalInvoiceIncome.augTotal - totalInvoiceExpense.augTotal
            }else if(i == 8){
                grossProfit.sepTotal = totalInvoiceIncome.sepTotal - totalInvoiceExpense.sepTotal
            }else if(i == 9){
                grossProfit.octTotal = totalInvoiceIncome.octTotal - totalInvoiceExpense.octTotal
            }else if(i == 10){
                grossProfit.novTotal = totalInvoiceIncome.novTotal - totalInvoiceExpense.novTotal
            }else if(i == 11){
                grossProfit.decTotal = totalInvoiceIncome.decTotal - totalInvoiceExpense.decTotal
            }
        }

        return grossProfit;

    }

    def Double getForecastValue(valInvoiceIncome,valInvoiceExpense,valIncomeBudget,valExpenseBudget){

        def forecastProfit = 0.00;
        def forecastExpenseTemp = 0.00;

        def forecastIncomeTemp = 0.00;

//        println("valInvoiceIncome "+ valInvoiceIncome)
//        println("valInvoiceExpense"+ valInvoiceExpense)
//        println("valIncomeBudget "+ valIncomeBudget)
//        println("valExpenseBudget "+ valExpenseBudget)

        //INCOME
        if(valInvoiceIncome == 0.00){
            forecastIncomeTemp = valIncomeBudget
        }
        else{
            forecastIncomeTemp = valInvoiceIncome
        }

//        println("forecastIncomeTemp "+ forecastIncomeTemp)

        //EXPENSE
        if(valInvoiceExpense == 0.00){
            forecastExpenseTemp = valExpenseBudget
        }else{
            forecastExpenseTemp = valInvoiceExpense
        }

//        println("forecastExpenseTemp "+ forecastExpenseTemp)

        forecastProfit = forecastIncomeTemp - forecastExpenseTemp;

        return forecastProfit;
    }

    def Map getForecastOfGrossProfit(incomeForcast,expenseForcast){

        Map forecastGrossProfit = ["janTotal": 0.00, "febTotal": 0.00, "marTotal": 0.00, "aprTotal": 0.00, "mayTotal": 0.00, "junTotal": 0.00,
                                   "julTotal": 0.00, "augTotal": 0.00, "sepTotal": 0.00, "octTotal": 0.00, "novTotal": 0.00,"decTotal":0.00,
                                   "allTotal": 0.00]

        def allTotal = 0.00;
        for(int i=0;i<12;i++){
            if(i == 0){
                forecastGrossProfit.janTotal = incomeForcast.janTotal - expenseForcast.janTotal;
                allTotal += forecastGrossProfit.janTotal;
            }else if(i == 1){
                forecastGrossProfit.febTotal = incomeForcast.febTotal - expenseForcast.febTotal;
                allTotal += forecastGrossProfit.febTotal;
            }else if(i == 2){
                forecastGrossProfit.marTotal = incomeForcast.marTotal - expenseForcast.marTotal;
                allTotal += forecastGrossProfit.marTotal;
            }else if(i == 3){
                forecastGrossProfit.aprTotal = incomeForcast.aprTotal - expenseForcast.aprTotal;
                allTotal += forecastGrossProfit.aprTotal;
            }else if(i == 4){
                forecastGrossProfit.mayTotal = incomeForcast.mayTotal - expenseForcast.mayTotal;
                allTotal += forecastGrossProfit.mayTotal;
            }else if(i == 5){
                forecastGrossProfit.junTotal = incomeForcast.junTotal - expenseForcast.junTotal;
                allTotal += forecastGrossProfit.junTotal;
            }else if(i == 6){
                forecastGrossProfit.julTotal = incomeForcast.julTotal - expenseForcast.julTotal;
                allTotal += forecastGrossProfit.julTotal;
            }else if(i == 7){
                forecastGrossProfit.augTotal = incomeForcast.augTotal - expenseForcast.augTotal;
                allTotal += forecastGrossProfit.augTotal;
            }else if(i == 8){
                forecastGrossProfit.sepTotal = incomeForcast.sepTotal - expenseForcast.sepTotal;
                allTotal += forecastGrossProfit.sepTotal;
            }else if(i == 9){
                forecastGrossProfit.octTotal = incomeForcast.octTotal - expenseForcast.octTotal;
                allTotal += forecastGrossProfit.octTotal;
            }else if(i == 10){
                forecastGrossProfit.novTotal = incomeForcast.novTotal - expenseForcast.novTotal;
                allTotal += forecastGrossProfit.novTotal;
            }else if(i == 11){
                forecastGrossProfit.decTotal = incomeForcast.decTotal - expenseForcast.decTotal;
                allTotal += forecastGrossProfit.decTotal;
            }
        }

        forecastGrossProfit.allTotal = allTotal;

//        println("forecastGrossProfit" + forecastGrossProfit)

        return forecastGrossProfit;
    }

    def Map getForecastOfNetProfit(forecastGrossProfit,incomeTaxPercentage) {

        Map forecastNetProfit = ["janTotal": 0.00, "febTotal": 0.00, "marTotal": 0.00, "aprTotal": 0.00, "mayTotal": 0.00, "junTotal": 0.00,
                                   "julTotal": 0.00, "augTotal": 0.00, "sepTotal": 0.00, "octTotal": 0.00, "novTotal": 0.00,"decTotal":0.00,
                                   "allTotal": 0.00]

        def allTotal = 0.00;
        for(int i=0;i<12;i++){
            if(i == 0){
                forecastNetProfit.janTotal = forecastGrossProfit.janTotal - (forecastGrossProfit.janTotal*incomeTaxPercentage)/100;
                allTotal += forecastNetProfit.janTotal;
            }else if(i == 1){
                forecastNetProfit.febTotal = forecastGrossProfit.febTotal - (forecastGrossProfit.febTotal*incomeTaxPercentage)/100;
                allTotal += forecastNetProfit.febTotal;
            }else if(i == 2){
                forecastNetProfit.marTotal = forecastGrossProfit.marTotal - (forecastGrossProfit.marTotal*incomeTaxPercentage)/100;
                allTotal += forecastNetProfit.marTotal;
            }else if(i == 3){
                forecastNetProfit.aprTotal = forecastGrossProfit.aprTotal - (forecastGrossProfit.aprTotal*incomeTaxPercentage)/100;
                allTotal += forecastNetProfit.aprTotal;
            }else if(i == 4){
                forecastNetProfit.mayTotal = forecastGrossProfit.mayTotal - (forecastGrossProfit.mayTotal*incomeTaxPercentage)/100;
                allTotal += forecastNetProfit.mayTotal;
            }else if(i == 5){
                forecastNetProfit.junTotal = forecastGrossProfit.junTotal - (forecastGrossProfit.junTotal*incomeTaxPercentage)/100;
                allTotal += forecastNetProfit.junTotal;
            }else if(i == 6){
                forecastNetProfit.julTotal = forecastGrossProfit.julTotal - (forecastGrossProfit.julTotal*incomeTaxPercentage)/100;
                allTotal += forecastNetProfit.julTotal;
            }else if(i == 7){
                forecastNetProfit.augTotal = forecastGrossProfit.augTotal - (forecastGrossProfit.augTotal*incomeTaxPercentage)/100;
                allTotal += forecastNetProfit.augTotal;
            }else if(i == 8){
                forecastNetProfit.sepTotal = forecastGrossProfit.sepTotal - (forecastGrossProfit.sepTotal*incomeTaxPercentage)/100;
                allTotal += forecastNetProfit.sepTotal;
            }else if(i == 9){
                forecastNetProfit.octTotal = forecastGrossProfit.octTotal - (forecastGrossProfit.octTotal*incomeTaxPercentage)/100;
                allTotal += forecastNetProfit.octTotal;
            }else if(i == 10){
                forecastNetProfit.novTotal = forecastGrossProfit.novTotal - (forecastGrossProfit.novTotal*incomeTaxPercentage)/100;
                allTotal += forecastNetProfit.novTotal;
            }else if(i == 11){
                forecastNetProfit.decTotal = forecastGrossProfit.decTotal - (forecastGrossProfit.decTotal*incomeTaxPercentage)/100;
                allTotal += forecastNetProfit.decTotal;
            }
        }

        forecastNetProfit.allTotal = allTotal;

        return forecastNetProfit;
    }

    def Map getGrossProfitBasedOnBooking(totalInvoiceIncome,totalInvoiceExpense)
    {
        Map grossProfit = ["janTotal": 0.00, "febTotal": 0.00, "marTotal": 0.00, "aprTotal": 0.00, "mayTotal": 0.00, "junTotal": 0.00,
                           "julTotal": 0.00, "augTotal": 0.00, "sepTotal": 0.00, "octTotal": 0.00, "novTotal": 0.00,"decTotal":0.00,
                           "allTotal": 0.00]

        def allTotal = 0.00;
        for(int i=0;i<12;i++){
            if(i == 0){
                grossProfit.janTotal = totalInvoiceIncome.janTotal - totalInvoiceExpense.janTotal
                allTotal += grossProfit.janTotal;
            }else if(i == 1){
                grossProfit.febTotal = totalInvoiceIncome.febTotal - totalInvoiceExpense.febTotal
                allTotal += grossProfit.febTotal;
            }else if(i == 2){
                grossProfit.marTotal = totalInvoiceIncome.marTotal - totalInvoiceExpense.marTotal
                allTotal += grossProfit.marTotal;
            }else if(i == 3){
                grossProfit.aprTotal = totalInvoiceIncome.aprTotal - totalInvoiceExpense.aprTotal
                allTotal += grossProfit.aprTotal;
            }else if(i == 4){
                grossProfit.mayTotal = totalInvoiceIncome.mayTotal - totalInvoiceExpense.mayTotal
                allTotal += grossProfit.mayTotal;
            }else if(i == 5){
                grossProfit.junTotal = totalInvoiceIncome.junTotal - totalInvoiceExpense.junTotal
                allTotal += grossProfit.junTotal;
            }else if(i == 6){
                grossProfit.julTotal = totalInvoiceIncome.julTotal - totalInvoiceExpense.julTotal
                allTotal += grossProfit.julTotal;
            }else if(i == 7){
                grossProfit.augTotal = totalInvoiceIncome.augTotal - totalInvoiceExpense.augTotal
                allTotal += grossProfit.augTotal;
            }else if(i == 8){
                grossProfit.sepTotal = totalInvoiceIncome.sepTotal - totalInvoiceExpense.sepTotal
                allTotal += grossProfit.sepTotal;
            }else if(i == 9){
                grossProfit.octTotal = totalInvoiceIncome.octTotal - totalInvoiceExpense.octTotal
                allTotal += grossProfit.octTotal;
            }else if(i == 10){
                grossProfit.novTotal = totalInvoiceIncome.novTotal - totalInvoiceExpense.novTotal
                allTotal += grossProfit.novTotal;
            }else if(i == 11){
                grossProfit.decTotal = totalInvoiceIncome.decTotal - totalInvoiceExpense.decTotal
                allTotal += grossProfit.decTotal;
            }
        }

        grossProfit.allTotal = allTotal;

        return grossProfit;
    }

    def Double getTotalGrossProfitFromMonthAmount(grossProfitMonthly){

        Double totalProfit = 0.00;
        for(int i=0;i<12;i++){
            if(i == 0){
                totalProfit = totalProfit + grossProfitMonthly.janTotal;
            }else if(i == 1){
                totalProfit = totalProfit + grossProfitMonthly.febTotal
            }else if(i == 2){
                totalProfit = totalProfit + grossProfitMonthly.marTotal
            }else if(i == 3){
                totalProfit = totalProfit + grossProfitMonthly.aprTotal
            }else if(i == 4){
                totalProfit = totalProfit + grossProfitMonthly.mayTotal
            }else if(i == 5){
                totalProfit = totalProfit + grossProfitMonthly.junTotal
            }else if(i == 6){
                totalProfit = totalProfit + grossProfitMonthly.julTotal
            }else if(i == 7){
                totalProfit = totalProfit + grossProfitMonthly.augTotal
            }else if(i == 8){
                totalProfit = totalProfit + grossProfitMonthly.sepTotal
            }else if(i == 9){
                totalProfit = totalProfit + grossProfitMonthly.octTotal
            }else if(i == 10){
                totalProfit = totalProfit + grossProfitMonthly.novTotal
            }else if(i == 11){
                totalProfit = totalProfit + grossProfitMonthly.decTotal
            }
        }

        return totalProfit;
    }

    def Map getGrossProfitBasedOnBudget(totalIncomeBudget,totalExpenseBudget) {

        Map grossProfit = ["janTotal": 0.00, "febTotal": 0.00, "marTotal": 0.00, "aprTotal": 0.00, "mayTotal": 0.00, "junTotal": 0.00,
                           "julTotal": 0.00, "augTotal": 0.00, "sepTotal": 0.00, "octTotal": 0.00, "novTotal": 0.00,"decTotal":0.00,
                           "allTotal": 0.00]


        def allTotal = 0.00;
        for(int i=0;i<12;i++){
            if(i == 0){
                grossProfit.janTotal = totalIncomeBudget.janTotal - totalExpenseBudget.janTotal
                allTotal += grossProfit.janTotal;
            }else if(i == 1){
                grossProfit.febTotal = totalIncomeBudget.febTotal - totalExpenseBudget.febTotal
                allTotal += grossProfit.febTotal;
            }else if(i == 2){
                grossProfit.marTotal = totalIncomeBudget.marTotal - totalExpenseBudget.marTotal
                allTotal += grossProfit.marTotal;
            }else if(i == 3){
                grossProfit.aprTotal = totalIncomeBudget.aprTotal - totalExpenseBudget.aprTotal
                allTotal += grossProfit.aprTotal;
            }else if(i == 4){
                grossProfit.mayTotal = totalIncomeBudget.mayTotal - totalExpenseBudget.mayTotal
                allTotal += grossProfit.mayTotal;
            }else if(i == 5){
                grossProfit.junTotal = totalIncomeBudget.junTotal - totalExpenseBudget.junTotal
                allTotal += grossProfit.junTotal;
            }else if(i == 6){
                grossProfit.julTotal = totalIncomeBudget.julTotal - totalExpenseBudget.julTotal
                allTotal += grossProfit.julTotal;
            }else if(i == 7){
                grossProfit.augTotal = totalIncomeBudget.augTotal - totalExpenseBudget.augTotal
                allTotal += grossProfit.augTotal;
            }else if(i == 8){
                grossProfit.sepTotal = totalIncomeBudget.sepTotal - totalExpenseBudget.sepTotal
                allTotal += grossProfit.sepTotal;
            }else if(i == 9){
                grossProfit.octTotal = totalIncomeBudget.octTotal - totalExpenseBudget.octTotal
                allTotal += grossProfit.octTotal;
            }else if(i == 10){
                grossProfit.novTotal = totalIncomeBudget.novTotal - totalExpenseBudget.novTotal
                allTotal += grossProfit.novTotal;
            }else if(i == 11){
                grossProfit.decTotal = totalIncomeBudget.decTotal - totalExpenseBudget.decTotal
                allTotal += grossProfit.decTotal;
            }
        }

        grossProfit.allTotal = allTotal;

        return grossProfit;
    }

    def Map getAmountWithoutTax(withTaxData,incomeTaxPercentage) {
        Map withoutTaxData = ["janTotal": 0.00, "febTotal": 0.00, "marTotal": 0.00, "aprTotal": 0.00, "mayTotal": 0.00, "junTotal": 0.00,
                         "julTotal": 0.00, "augTotal": 0.00, "sepTotal": 0.00, "octTotal": 0.00, "novTotal": 0.00,"decTotal":0.00,
                         "allTotal": 0.00]

        def allTotal = 0.00;
        for(int i=0;i<12;i++){
            if(i == 0){
                withoutTaxData.janTotal = withTaxData.janTotal - (withTaxData.janTotal*incomeTaxPercentage)/100;
                allTotal += withoutTaxData.janTotal;
            }else if(i == 1){
                withoutTaxData.febTotal = withTaxData.febTotal - (withTaxData.febTotal*incomeTaxPercentage)/100;
                allTotal += withoutTaxData.febTotal;
            }else if(i == 2){
                withoutTaxData.marTotal = withTaxData.marTotal - (withTaxData.marTotal*incomeTaxPercentage)/100;
                allTotal += withoutTaxData.marTotal;
            }else if(i == 3){
                withoutTaxData.aprTotal = withTaxData.aprTotal - (withTaxData.aprTotal*incomeTaxPercentage)/100;
                allTotal += withoutTaxData.aprTotal;
            }else if(i == 4){
                withoutTaxData.mayTotal = withTaxData.mayTotal - (withTaxData.mayTotal*incomeTaxPercentage)/100;
                allTotal += withoutTaxData.mayTotal;
            }else if(i == 5){
                withoutTaxData.junTotal = withTaxData.junTotal - (withTaxData.junTotal*incomeTaxPercentage)/100;
                allTotal += withoutTaxData.junTotal;
            }else if(i == 6){
                withoutTaxData.julTotal = withTaxData.julTotal - (withTaxData.julTotal*incomeTaxPercentage)/100;
                allTotal += withoutTaxData.julTotal;
            }else if(i == 7){
                withoutTaxData.augTotal = withTaxData.augTotal - (withTaxData.augTotal*incomeTaxPercentage)/100;
                allTotal += withoutTaxData.augTotal;
            }else if(i == 8){
                withoutTaxData.sepTotal = withTaxData.sepTotal - (withTaxData.sepTotal*incomeTaxPercentage)/100;
                allTotal += withoutTaxData.sepTotal;
            }else if(i == 9){
                withoutTaxData.octTotal = withTaxData.octTotal - (withTaxData.octTotal*incomeTaxPercentage)/100;
                allTotal += withoutTaxData.octTotal;
            }else if(i == 10){
                withoutTaxData.novTotal = withTaxData.novTotal - (withTaxData.novTotal*incomeTaxPercentage)/100;
                allTotal += withoutTaxData.novTotal;
            }else if(i == 11){
                withoutTaxData.decTotal = withTaxData.decTotal - (withTaxData.decTotal*incomeTaxPercentage)/100;
                allTotal += withoutTaxData.decTotal;
            }
        }

        withoutTaxData.allTotal = allTotal;

        return withoutTaxData;
    }

    def Map getTaxReservationAmount(incomeBudget,expenseBudget,incomeTaxPercentage) {
        Map taxReservationAmount = ["janTotal": 0.00, "febTotal": 0.00, "marTotal": 0.00, "aprTotal": 0.00, "mayTotal": 0.00, "junTotal": 0.00,
                                    "julTotal": 0.00, "augTotal": 0.00, "sepTotal": 0.00, "octTotal": 0.00, "novTotal": 0.00,"decTotal":0.00]

        for(int i=0;i<12;i++){
            if(i == 0){
                taxReservationAmount.janTotal = ((incomeBudget.janTotal - expenseBudget.janTotal) * incomeTaxPercentage)/100;
            }else if(i == 1){
                taxReservationAmount.febTotal = ((incomeBudget.febTotal - expenseBudget.febTotal) * incomeTaxPercentage)/100;
            }else if(i == 2){
                taxReservationAmount.marTotal = ((incomeBudget.marTotal - expenseBudget.marTotal) * incomeTaxPercentage)/100;
            }else if(i == 3){
                taxReservationAmount.aprTotal = ((incomeBudget.aprTotal - expenseBudget.aprTotal) * incomeTaxPercentage)/100;
            }else if(i == 4){
                taxReservationAmount.mayTotal = ((incomeBudget.mayTotal - expenseBudget.mayTotal) * incomeTaxPercentage)/100;
            }else if(i == 5){
                taxReservationAmount.junTotal = ((incomeBudget.junTotal - expenseBudget.junTotal) * incomeTaxPercentage)/100;
            }else if(i == 6){
                taxReservationAmount.julTotal = ((incomeBudget.julTotal - expenseBudget.julTotal) * incomeTaxPercentage)/100;
            }else if(i == 7){
                taxReservationAmount.augTotal = ((incomeBudget.augTotal - expenseBudget.augTotal) * incomeTaxPercentage)/100;
            }else if(i == 8){
                taxReservationAmount.sepTotal = ((incomeBudget.sepTotal - expenseBudget.sepTotal) * incomeTaxPercentage)/100;
            }else if(i == 9){
                taxReservationAmount.octTotal = ((incomeBudget.octTotal - expenseBudget.octTotal) * incomeTaxPercentage)/100;
            }else if(i == 10){
                taxReservationAmount.novTotal = ((incomeBudget.novTotal - expenseBudget.novTotal) * incomeTaxPercentage)/100;
            }else if(i == 11){
                taxReservationAmount.decTotal = ((incomeBudget.decTotal - expenseBudget.decTotal) * incomeTaxPercentage)/100;
            }
        }

        return taxReservationAmount;
    }

    def List<Map> getMonthwiseBudgetSummary(ArrayList budgetArr,ArrayList accountDataArr,String incomeOrExpense,String budgetOrInvoice){

        List monthlyIncomeBudgetArr = new ArrayList();

        for(int i=0;i<budgetArr.size();i++){
            Map monthlyIncomeBudgetMap = ["budgetName":'',"incomeOrExpense":incomeOrExpense,"budgetOrInvoice":budgetOrInvoice,"janAmount": 0,"febAmount": 0,"marAmount": 0,"aprAmount": 0,"mayAmount": 0,"junAmount": 0,
                                          "julAmount": 0,"augAmount": 0,"sepAmount": 0,"octAmount": 0,"novAmount": 0,"decAmount": 0];

            def customerId=budgetArr[i][0];
            def customerTempId
            if(accountDataArr.size()>0) {
                for (int z = 0; z < accountDataArr.size(); z++) {
                for (int j = 0; j < accountDataArr[z].size(); j++) {
                    customerTempId = accountDataArr[z][j][0];


                    if (customerId == customerTempId) {

                        monthlyIncomeBudgetMap.budgetName = budgetArr[i][1];

                        if (accountDataArr[z][j][3] == 1) {
                            monthlyIncomeBudgetMap.janAmount += accountDataArr[z][j][4]
                        } else if (accountDataArr[z][j][3] == 2) {
                            monthlyIncomeBudgetMap.febAmount += accountDataArr[z][j][4]
                        } else if (accountDataArr[z][j][3] == 3) {
                            monthlyIncomeBudgetMap.marAmount += accountDataArr[z][j][4]
                        } else if (accountDataArr[z][j][3] == 4) {
                            monthlyIncomeBudgetMap.aprAmount += accountDataArr[z][j][4]
                        } else if (accountDataArr[z][j][3] == 5) {
                            monthlyIncomeBudgetMap.mayAmount += accountDataArr[z][j][4]
                        } else if (accountDataArr[z][j][3] == 6) {
                            monthlyIncomeBudgetMap.junAmount += accountDataArr[z][j][4]
                        } else if (accountDataArr[z][j][3] == 7) {
                            monthlyIncomeBudgetMap.julAmount += accountDataArr[z][j][4]
                        } else if (accountDataArr[z][j][3] == 8) {
                            monthlyIncomeBudgetMap.augAmount += accountDataArr[z][j][4]
                        } else if (accountDataArr[z][j][3] == 9) {
                            monthlyIncomeBudgetMap.sepAmount += accountDataArr[z][j][4]
                        } else if (accountDataArr[z][j][3] == 10) {
                            monthlyIncomeBudgetMap.octAmount += accountDataArr[z][j][4]
                        } else if (accountDataArr[z][j][3] == 11) {
                            monthlyIncomeBudgetMap.novAmount += accountDataArr[z][j][4]
                        } else if (accountDataArr[z][j][3] == 12) {
                            monthlyIncomeBudgetMap.decAmount += accountDataArr[z][j][4]
                        }
                    }
                }
                    if (customerId == customerTempId) {
                        monthlyIncomeBudgetArr.add(monthlyIncomeBudgetMap);
                    }else{}

                }

            }


        }
        return monthlyIncomeBudgetArr;

    }

    def List<Map> getMonthwiseInvoiceSummary(ArrayList invoiceArr,ArrayList accountDataArr,String incomeOrExpense,String budgetOrInvoice) {
        List monthlyInvoiceArr = new ArrayList();
        println(accountDataArr)

        for (int i = 0; i < invoiceArr.size(); i++) {
            Map monthlyInvoiceMap = ["budgetName": '', "incomeOrExpense": incomeOrExpense, "budgetOrInvoice": budgetOrInvoice, "janAmount": 0, "febAmount": 0, "marAmount": 0, "aprAmount": 0, "mayAmount": 0, "junAmount": 0,
                                      "julAmount" : 0, "augAmount": 0, "sepAmount": 0, "octAmount": 0, "novAmount": 0, "decAmount": 0];

            def vendorId=invoiceArr[i][0];
            def vendorTempId
//            println(accountDataArr);

            if(accountDataArr.size()>0) {
                for (int z = 0; z < accountDataArr.size(); z++) {
                for (int j = 0; j < accountDataArr[z].size(); j++) {
                        vendorTempId = accountDataArr[z][j][0];

                    if (vendorId.toString() == vendorTempId.toString()) {

                        monthlyInvoiceMap.budgetName = invoiceArr[i][1];
                        if (Integer.parseInt(accountDataArr[z][j][3]) == 1) {
                            monthlyInvoiceMap.janAmount += accountDataArr[z][j][4]
                        } else if (Integer.parseInt(accountDataArr[z][j][3]) == 2) {
                            monthlyInvoiceMap.febAmount += accountDataArr[z][j][4]
                        } else if (Integer.parseInt(accountDataArr[z][j][3]) == 3) {
                            monthlyInvoiceMap.marAmount += accountDataArr[z][j][4]
                        } else if (Integer.parseInt(accountDataArr[z][j][3]) == 4) {
                            monthlyInvoiceMap.aprAmount += accountDataArr[z][j][4]
                        } else if (Integer.parseInt(accountDataArr[z][j][3]) == 5) {
                            monthlyInvoiceMap.mayAmount += accountDataArr[z][j][4]
                        } else if (Integer.parseInt(accountDataArr[z][j][3]) == 6) {
                            monthlyInvoiceMap.junAmount += accountDataArr[z][j][4]
                        } else if (Integer.parseInt(accountDataArr[z][j][3]) == 7) {
                            monthlyInvoiceMap.julAmount += accountDataArr[z][j][4]
                        } else if (Integer.parseInt(accountDataArr[z][j][3]) == 8) {
                            monthlyInvoiceMap.augAmount += accountDataArr[z][j][4]
                        } else if (Integer.parseInt(accountDataArr[z][j][3]) == 9) {
                            monthlyInvoiceMap.sepAmount += accountDataArr[z][j][4]
                        } else if (Integer.parseInt(accountDataArr[z][j][3]) == 10) {
                            monthlyInvoiceMap.octAmount += accountDataArr[z][j][4]
                        } else if (Integer.parseInt(accountDataArr[z][j][3]) == 11) {
                            monthlyInvoiceMap.novAmount += accountDataArr[z][j][4]
                        } else if (Integer.parseInt(accountDataArr[z][j][3]) == 12) {
                            monthlyInvoiceMap.decAmount += accountDataArr[z][j][4]
                        }
                    }
                }
                    if(vendorId.toString() == vendorTempId.toString()){
                        monthlyInvoiceArr.add(monthlyInvoiceMap);
                    }else{}

            }
            }



        }

        return monthlyInvoiceArr;
    }
}
