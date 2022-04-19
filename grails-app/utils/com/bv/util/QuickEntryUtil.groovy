package com.bv.util

import bv.CoreParamsHelper
import grails.util.Holders
import groovy.sql.GroovyRowResult
import org.grails.plugins.web.taglib.ValidationTagLib

import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class QuickEntryUtil {

    public DateFormat formatter;
    public Date date;
    public static final String STR_DATE_FORMAT = "yyyy-MM-dd";
    public static final String GRID_DATE_FORMAT = "dd-MM-yyyy";
    public String STR_DATE_RETURN = "";
    private String contextPath = Holders.applicationContext.servletContext.contextPath

    public Date getDateFormInput(String str) {
        try {
            formatter = new SimpleDateFormat(GRID_DATE_FORMAT);
            date = (Date) formatter.parse(str);
        } catch (Exception e) {
            STR_DATE_RETURN = "";
        }
        return date;
    }

    public String getStrTransDate(Date transDate) {
        try {
            formatter = new SimpleDateFormat(GRID_DATE_FORMAT);
            STR_DATE_RETURN = formatter.format(transDate);
        } catch (Exception e) {
            STR_DATE_RETURN = "";
        }
        return STR_DATE_RETURN;
    }
    //method to get UI grid str date from rest api date str

    public String getStrDateForGrid(String str) {
        try {
            formatter = new SimpleDateFormat(STR_DATE_FORMAT);
            date = (Date) formatter.parse(str);
            formatter = new SimpleDateFormat(GRID_DATE_FORMAT);
            STR_DATE_RETURN = formatter.format(date);
        } catch (Exception e) {
            STR_DATE_RETURN = "";
        }
        return STR_DATE_RETURN;
    }

    public String getStrDateForGrid(Date transDate) {
        try {
            formatter = new SimpleDateFormat(GRID_DATE_FORMAT);
            STR_DATE_RETURN = formatter.format(transDate);
        } catch (Exception e) {
            STR_DATE_RETURN = "";
        }
        return STR_DATE_RETURN;
    }

    public List wrapListInGrid(List<GroovyRowResult> quickEntries, int start) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i];
                obj = new GridEntity();
                obj.id = expenseEntry.id
                changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\")'>Edit</a>"
                obj.cell = [counter, expenseEntry.invoice_number, expenseEntry.vendor_id, "", getStrDateForGrid(expenseEntry.created_date), getStrDateForGrid(expenseEntry.trans_date), expenseEntry.total_gl_amount, expenseEntry.total_vat, changeBooking]
                quickExpenseList.add(obj)
                counter++;
            }
            return quickExpenseList;
        } catch (Exception ex) {
//            log.error(ex.getMessage());
            quickExpenseList = [];
            return quickExpenseList;
        }
    }

    /*
    * Grid for List budget Item page which comes from dashboard
    *
    * */

    public List wrapBudgetItemInGrid(List<GroovyRowResult> quickEntries, int start, journalId, vendorId, bookingPeriod) {
        //public List wrapBudgetItemInGrid(List<GroovyRowResult> quickEntries, int start) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i];

                DecimalFormat twoDForm = new DecimalFormat("#.00");

                def aa = expenseEntry.totalPriceWithoutVat
                Double bb = Double.parseDouble(aa)
                def totalPriceWithoutVatMd = twoDForm.format(bb)

                def cc = expenseEntry.totalPriceWithVat
                Double dd = Double.parseDouble(cc)
                def totalPriceWithVatMd = twoDForm.format(dd)

                obj = new GridEntity();
                obj.id = expenseEntry.invoiceExpenseId + "::" + expenseEntry.detailsID
                //changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.invoiceExpenseId}\",\"${journalId}\",\"${vendorId}\",\"${bookingPeriod}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"../../images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:deleteBooking(\"${journalId}\",\"${journalId}\",\"${journalId}\",\"${journalId}\")'><img width=\"16\" height=\"15\" alt=\"Delete\" src=\"../../images/delete.png\"></a>"
                changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.invoiceExpenseId}\",\"${journalId}\",\"${vendorId}\",\"${bookingPeriod}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                obj.cell = [counter, expenseEntry.budgetItemID, expenseEntry.glAccountName, expenseEntry.createdDate, totalPriceWithoutVatMd, totalPriceWithVatMd, expenseEntry.total, changeBooking]
                quickExpenseList.add(obj)
                counter++;
            }
            return quickExpenseList;
        } catch (Exception ex) {
//            log.error(ex.getMessage());
            quickExpenseList = [];
            return quickExpenseList;
        }
    }

    public List wrapEntryBudgetItemInGrid(List<GroovyRowResult> quickEntries, int start, vendorId, journalId, bookingPeriod, invoiceBudgetExpenseData) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj
        String changeBooking

        try {
            def notDelete = 0
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i];
                notDelete = 0
                invoiceBudgetExpenseData.each { phn ->
                    if (expenseEntry.id == phn) {
                        notDelete = 1
                    }
                }

                def bookStartPeriod = ""
                if (expenseEntry.booking_period_start_month == 12) {
                    bookStartPeriod = "Dec " + " - " + expenseEntry.booking_period_start_year
                } else {
                    bookStartPeriod = new CoreParamsHelper().monthNameShow(expenseEntry.booking_period_start_month) + " - " + expenseEntry.booking_period_start_year
                }
                def bookEndPeriod = ""
                if (expenseEntry.booking_period_end_month == 12) {
                    bookEndPeriod = "Dec " + " - " + expenseEntry.booking_period_end_year
                } else {
                    bookEndPeriod = new CoreParamsHelper().monthNameShow(expenseEntry.booking_period_end_month) + " - " + expenseEntry.booking_period_end_year
                }

                def showtotalGlAmount = expenseEntry.totalGlAmount
                def showtotalVat = expenseEntry.totalVat

                Double showtotalGlAmountc = Double.parseDouble(showtotalGlAmount)
                Double showtotalVatc = Double.parseDouble(showtotalVat)

                def showtotalGlAmounta = String.format("%.2f", showtotalGlAmountc)
                def showtotalVata = String.format("%.2f", showtotalVatc)

                obj = new GridEntity();
                obj.id = expenseEntry.id

                if(bookingPeriod){

                }


                if (notDelete == 0) {
                    changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${expenseEntry.vendor_id}\",\"${journalId}\",\"${bookingPeriod}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:showDeleteDialog(\"${expenseEntry.id}\",\"${expenseEntry.vendor_id}\")'><img width=\"16\" height=\"15\" alt=\"Delete\" src=\"${contextPath}/images/delete.png\"></a>"
                } else {
                    changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${expenseEntry.vendor_id}\",\"${journalId}\",\"${bookingPeriod}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                }


                //obj.cell = [counter, expenseEntry.budgetItemName, expenseEntry.budgetCode, showtotalGlAmounta, showtotalVata, bookStartPeriod, bookEndPeriod, changeBooking]
                obj.cell = ["budgetItemName": expenseEntry.budgetItemName,"budgetCode": expenseEntry.budgetCode,"totalGlAmount": showtotalGlAmounta,"totalVat": showtotalVata,"bookStartPeriod":  bookStartPeriod, "action" :changeBooking]
                quickExpenseList.add(obj)
                counter++;
            }
            return quickExpenseList;
        } catch (Exception ex) {
//            log.error(ex.getMessage());
            quickExpenseList = [];
            return quickExpenseList;
        }
    }
// *****************************************************

    public List wrapEntryBudgetItemSearchInGrid(List<GroovyRowResult> quickEntries, int start, vendorId, journalId, bookingPeriod, invoiceBudgetExpenseData) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj
        String changeBooking

        try {
            def notDelete = 0
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i];
                notDelete = 0
                invoiceBudgetExpenseData.each { phn ->
                    if (expenseEntry.id == phn) {
                        notDelete = 1
                    }
                }

                def bookStartPeriod = ""
                if (expenseEntry.booking_period_start_month == 12) {
                    bookStartPeriod = "Dec " + " - " + expenseEntry.booking_period_start_year
                } else {
                    bookStartPeriod = new CoreParamsHelper().monthNameShow(expenseEntry.booking_period_start_month) + " - " + expenseEntry.booking_period_start_year
                }
                def bookEndPeriod = ""
                if (expenseEntry.booking_period_end_month == 12) {
                    bookEndPeriod = "Dec " + " - " + expenseEntry.booking_period_end_year
                } else {
                    bookEndPeriod = new CoreParamsHelper().monthNameShow(expenseEntry.booking_period_end_month) + " - " + expenseEntry.booking_period_end_year
                }

                def showtotalGlAmount = expenseEntry.totalGlAmount
                def showtotalVat = expenseEntry.totalVat

//                Double showtotalGlAmountc = Double.parseDouble(showtotalGlAmount)
//                Double showtotalVatc = Double.parseDouble(showtotalVat)

                def showtotalGlAmounta = String.format("%.2f", showtotalGlAmount)
                def showtotalVata = String.format("%.2f", showtotalVat)

                obj = new GridEntity();
                obj.id = expenseEntry.id

                if(bookingPeriod){

                }


                if (notDelete == 0) {
                    changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${expenseEntry.vendor_id}\",\"${journalId}\",\"${bookingPeriod}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:showDeleteDialog(\"${expenseEntry.id}\",\"${expenseEntry.vendor_id}\")'><img width=\"16\" height=\"15\" alt=\"Delete\" src=\"${contextPath}/images/delete.png\"></a>"
                } else {
                    changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${expenseEntry.vendor_id}\",\"${journalId}\",\"${bookingPeriod}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                }


                //obj.cell = [counter, expenseEntry.budgetItemName, expenseEntry.budgetCode, showtotalGlAmounta, showtotalVata, bookStartPeriod, bookEndPeriod, changeBooking]
                obj.cell = [ expenseEntry.budgetItemName, expenseEntry.budgetCode, showtotalGlAmounta, showtotalVata, bookStartPeriod, changeBooking]
                quickExpenseList.add(obj)
                counter++;
            }
            return quickExpenseList;
        } catch (Exception ex) {
//            log.error(ex.getMessage());
            quickExpenseList = [];
            return quickExpenseList;
        }
    }

// ******************************************************
    /*
   * Grid for List Expense Invoice List
   *
   * */

    public List wrapInvoiceExpenseInGrid(List<GroovyRowResult> quickEntries, int start, budgetVendorId, bookingPeriod, bookInvoiceId,fiscalYearId) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i];
                obj = new GridEntity();
                obj.id = expenseEntry.id

                DecimalFormat twoDForm = new DecimalFormat("#.00");

                BigDecimal showtotalGlAmounta = new BigDecimal(expenseEntry.totalAmountIncVat)
                def showtotalGlAmount = twoDForm.format(showtotalGlAmounta)
                BigDecimal showtotalVata = new BigDecimal(expenseEntry.totalVat)
                def showtotalVat = twoDForm.format(showtotalVata)
                def vendorId = expenseEntry.vendorId
                //println(expenseEntry.bookingPeriod)

                def bookingPeriodFormat = (new CoreParamsHelper().monthNameShow(Integer.parseInt(expenseEntry.bookingPeriod))) + '-' + fiscalYearId;
                def budgetItemDetailsId = new CoreParamsHelper().getBudgetItemExpenseDetailsIdFromInvoiceExpense(expenseEntry.id)

                //changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${vendorId}\",\"${bookingPeriod}\",\"${bookInvoiceId}\",\"${budgetItemDetailsId}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:deleteBooking(\"${expenseEntry.id}\",\"${vendorId}\",\"${bookingPeriod}\",\"${bookInvoiceId}\")'><img width=\"16\" height=\"15\" alt=\"Delete\" src=\"${contextPath}/images/delete.png\"></a>"
                changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${vendorId}\",\"${bookingPeriod}\",\"${bookInvoiceId}\",\"${budgetItemDetailsId}\",\"${budgetVendorId}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                //obj.cell = [counter, expenseEntry.invoiceNumber, expenseEntry.budgetItemID, expenseEntry.budgetItemName, expenseEntry.InvoiceVendorName, expenseEntry.invoiceDate, expenseEntry.dueDate, showtotalGlAmount, showtotalVat, changeBooking]
                obj.cell = ["invoiceNumber":expenseEntry.invoiceNumber,"bookingPeriod": bookingPeriodFormat,"vendorName": expenseEntry.vendorName,"invoiceDate": expenseEntry.invoiceDate,"paymentReference": expenseEntry.paymentRef,"totalGlAmount": showtotalGlAmount, "totalVat":showtotalVat,"action": changeBooking]

                quickExpenseList.add(obj)
                counter++;
            }
            return quickExpenseList;
        } catch (Exception ex) {

            quickExpenseList = [];
            return quickExpenseList;
        }
    }


    public List wrapSearchInvoiceExpenseInGrid(List<GroovyRowResult> quickEntries, int start, fiscalYearId) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i];
                obj = new GridEntity();
                obj.id = expenseEntry.id

                DecimalFormat twoDForm = new DecimalFormat("#.00");

                BigDecimal showtotalGlAmounta = new BigDecimal(expenseEntry.totalAmountIncVat)
                def showtotalGlAmount = twoDForm.format(showtotalGlAmounta)
                BigDecimal showtotalVata = new BigDecimal(expenseEntry.totalVat)
                def showtotalVat = twoDForm.format(showtotalVata)
                def vendorId = expenseEntry.vendorId
                //println(expenseEntry.bookingPeriod)

                def bookingPeriodFormat = (new CoreParamsHelper().monthNameShow(Integer.parseInt(expenseEntry.bookingPeriod))) + '-' + fiscalYearId;
                def budgetItemDetailsId = new CoreParamsHelper().getBudgetItemExpenseDetailsIdFromInvoiceExpense(expenseEntry.id)

                //changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${vendorId}\",\"${bookingPeriod}\",\"${bookInvoiceId}\",\"${budgetItemDetailsId}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:deleteBooking(\"${expenseEntry.id}\",\"${vendorId}\",\"${bookingPeriod}\",\"${bookInvoiceId}\")'><img width=\"16\" height=\"15\" alt=\"Delete\" src=\"${contextPath}/images/delete.png\"></a>"
                changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${vendorId}\",\"${expenseEntry.bookingPeriod}\",\"${expenseEntry.budget_item_expense_id}\",\"${budgetItemDetailsId}\",\"${expenseEntry.budget_vendor_id}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                //obj.cell = [counter, expenseEntry.invoiceNumber, expenseEntry.budgetItemID, expenseEntry.budgetItemName, expenseEntry.InvoiceVendorName, expenseEntry.invoiceDate, expenseEntry.dueDate, showtotalGlAmount, showtotalVat, changeBooking]
                obj.cell = [expenseEntry.invoiceNumber, bookingPeriodFormat, expenseEntry.vendorName, expenseEntry.invoiceDate, expenseEntry.paymentRef, showtotalGlAmount, showtotalVat, changeBooking]

                quickExpenseList.add(obj)
                counter++;
            }
            return quickExpenseList;
        } catch (Exception ex) {
//            log.error(ex.getMessage());
            quickExpenseList = [];
            return quickExpenseList;
        }
    }


    public List wrapReceiptInGrid(List<GroovyRowResult> quickEntries, int start, budgetVendorId, bookingPeriod, bookInvoiceId,fiscalYearId) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i];
                obj = new GridEntity();
                obj.id = expenseEntry.id

                DecimalFormat twoDForm = new DecimalFormat("#0.00");

                BigDecimal showtotalGlAmounta = new BigDecimal(expenseEntry.totalAmountIncVat)
                def showtotalGlAmount = twoDForm.format(showtotalGlAmounta)
                BigDecimal showtotalVata = new BigDecimal(expenseEntry.totalVat)
                def showtotalVat = twoDForm.format(showtotalVata)

                def bookingPeriodFormat = (new CoreParamsHelper().monthNameShow(Integer.parseInt(expenseEntry.bookingPeriod))) + '-' + fiscalYearId;
                def budgetItemDetailsId = new CoreParamsHelper().getBudgetItemExpenseDetailsIdFromInvoiceExpense(expenseEntry.id)
                def vendorId = expenseEntry.vendorId
                def vendorShopId = expenseEntry.shopId

                changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${vendorId}\",\"${bookingPeriod}\",\"${bookInvoiceId}\",\"${budgetItemDetailsId}\",\"${budgetVendorId}\",\"${vendorShopId}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                obj.cell = ["invoiceNumber":expenseEntry.invoiceNumber,"bookingPeriod": bookingPeriodFormat,"vendorName": expenseEntry.vendorName,"invoiceDate": expenseEntry.invoiceDate,"paymentReference":expenseEntry.paymentRef,"totalGlAmount": showtotalGlAmount,"totalVat": showtotalVat,"action": changeBooking]
                quickExpenseList.add(obj)
                counter++;
            }

            return quickExpenseList;
        } catch (Exception ex) {

            quickExpenseList = [];
            return quickExpenseList;
        }
    }



    public List wrapSearchReceiptInGrid(List<GroovyRowResult> quickEntries, int start,fiscalYearId) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i];
                obj = new GridEntity();
                obj.id = expenseEntry.id

                DecimalFormat twoDForm = new DecimalFormat("#0.00");

                BigDecimal showtotalGlAmounta = new BigDecimal(expenseEntry.totalAmountIncVat)
                def showtotalGlAmount = twoDForm.format(showtotalGlAmounta)
                BigDecimal showtotalVata = new BigDecimal(expenseEntry.totalVat)
                def showtotalVat = twoDForm.format(showtotalVata)

                def bookingPeriodFormat = (new CoreParamsHelper().monthNameShow(Integer.parseInt(expenseEntry.bookingPeriod))) + '-' + fiscalYearId;
                def budgetItemDetailsId = new CoreParamsHelper().getBudgetItemExpenseDetailsIdFromInvoiceExpense(expenseEntry.id)
                def vendorId = expenseEntry.vendorId
                def vendorShopId = expenseEntry.shopId


                //changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${vendorId}\",\"${bookingPeriod}\",\"${bookInvoiceId}\",\"${budgetItemDetailsId}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:deleteBooking(\"${expenseEntry.id}\",\"${vendorId}\",\"${bookingPeriod}\",\"${bookInvoiceId}\")'><img width=\"16\" height=\"15\" alt=\"Delete\" src=\"${contextPath}/images/delete.png\"></a>"
//                ${expenseEntry.id}\",\"${vendorId}\",\"${bookingPeriod}\",\"${bookInvoiceId}\",\"${budgetItemDetailsId}\",\"${budgetVendorId}\",\"${vendorShopId}\
                changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${vendorId}\",\"${expenseEntry.bookingPeriod}\",\"${expenseEntry.budget_item_expense_id}\",\"${budgetItemDetailsId}\",\"${expenseEntry.budget_vendor_id}\",\"${vendorShopId}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                //obj.cell = [counter, expenseEntry.invoiceNumber, expenseEntry.budgetItemID, expenseEntry.budgetItemName, expenseEntry.InvoiceVendorName, expenseEntry.invoiceDate, expenseEntry.dueDate, showtotalGlAmount, showtotalVat, changeBooking]
                obj.cell = [expenseEntry.invoiceNumber, bookingPeriodFormat, expenseEntry.vendorName, expenseEntry.invoiceDate,expenseEntry.paymentRef, showtotalGlAmount, showtotalVat, changeBooking]
                quickExpenseList.add(obj)
                counter++;
            }
            return quickExpenseList;
        } catch (Exception ex) {
//            log.error(ex.getMessage());
            quickExpenseList = [];
            return quickExpenseList;
        }
    }



    /*
    * function list for Journal Entry Util
    * */

    public List wrapJournalEntryInGrid(List<GroovyRowResult> journalEntries, int start,liveUrl) {
        List journalEntriesList = new ArrayList()
        def journalEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < journalEntries.size(); i++) {
                journalEntry = journalEntries[i];
                obj = new GridEntity();
                obj.id = journalEntry.id

                DecimalFormat twoDForm = new DecimalFormat("#.00");
                BigDecimal showtotalGlAmounta = new BigDecimal(journalEntry.totlaAmount)
                def showtotalGlAmount = twoDForm.format(showtotalGlAmounta)
                changeBooking = "<a href='javascript:changeJournalEntry(\"${journalEntry.id}\",\"${journalEntry.invoiceNumber}\",\"${liveUrl}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                obj.cell = ["journalNumber":journalEntry.invoiceNumber,"transactionDate": journalEntry.transactionDate,"totalAmount": showtotalGlAmount,"action": changeBooking]
                journalEntriesList.add(obj)
                counter++;
            }
            return journalEntriesList;
        } catch (Exception ex) {
//            log.error(ex.getMessage());
            journalEntriesList = [];
            return journalEntriesList;
        }
    }

    public List wrapChartMasterEntryInGrid(List<GroovyRowResult> chartMasterEntries, int start, liveUrl, fromAction) {
        List chartMasterEntriesList = new ArrayList()
        def chartMasterEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < chartMasterEntries.size(); i++) {
                chartMasterEntry = chartMasterEntries[i];
                obj = new GridEntity();
                obj.id = chartMasterEntry.id

                if (fromAction == "list") {
                    changeBooking = "<a href='javascript:changeBooking(\"${chartMasterEntry.id}\",\"${liveUrl}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                } else {
                    changeBooking = "<a href='javascript:changeBooking(\"${chartMasterEntry.id}\",\"${liveUrl}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                }

                obj.cell = ["accountCode": chartMasterEntry.accountCode,"accountName": chartMasterEntry.accountName,"accountantName": chartMasterEntry.accountantName, "chartGroup":chartMasterEntry.groupName, "status":chartMasterEntry.showStatus,"action": changeBooking]
                chartMasterEntriesList.add(obj)

                counter++;
            }
            return chartMasterEntriesList;
        } catch (Exception ex) {
            chartMasterEntriesList = [];
            return chartMasterEntriesList;
        }
    }

    /*
   * function list for Manual Reconciliation Entry Util
   * */

    public List wrapManualReconcileEntryInGrid(List<GroovyRowResult> manualEntries, int start) {
        List manuallEntriesList = new ArrayList()
        def manualEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < manualEntries.size(); i++) {
                manualEntry = manualEntries[i];
                obj = new GridEntity();
                obj.id = manualEntry.id
                DecimalFormat twoDForm = new DecimalFormat("#.00");

                BigDecimal showTotalGlAmounta = new BigDecimal(manualEntry.totalAmnt)
                def showTotalGlAmount = twoDForm.format(showTotalGlAmounta)
                BigDecimal showTotalPaidAmounta = new BigDecimal(manualEntry.paid_amount)
                def showTotalPaidAmount = twoDForm.format(showTotalPaidAmounta)

                changeBooking = "<a href='javascript:changeBooking(\"${manualEntry.id}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:deleteBooking(\"${manualEntry.id}\")'><img width=\"16\" height=\"15\" alt=\"Delete\" src=\"${contextPath}/images/delete.png\"></a>"
                obj.cell = [counter, manualEntry.invoiceNumber, showTotalGlAmount, showTotalPaidAmount, manualEntry.transactionDate, changeBooking]
                manuallEntriesList.add(obj)
                counter++;
            }
            return manuallEntriesList;
        } catch (Exception ex) {
//            log.error(ex.getMessage());
            manuallEntriesList = [];
            return manuallEntriesList;
        }
    }

    public List wrapUndoReconciliateListInGrid(List<GroovyRowResult>undoReconciliateList,int start){

        List undoReconciliateItem = new ArrayList()
        def undoReconciliate
        GridEntity obj
        def g = new ValidationTagLib()
        try{
            int counter = start + 1
            for (int i = 0; i <undoReconciliateList.size() ; i++){
                undoReconciliate= undoReconciliateList[i]
                obj = new GridEntity()
                obj.id=undoReconciliate.id
                DecimalFormat twoDForm = new DecimalFormat("#.00");
                BigDecimal showAmounta = new BigDecimal(undoReconciliate.amount)
                def amount = twoDForm.format(showAmounta)

                String firstCol = "<input type=\"checkbox\" value=\"${undoReconciliate.bank_payment_id}\"  onchange=\"jQuery.ajax({type:'POST',data:'id=' + this.value, url:'/undoReconciliation/bankStmtImpFinalss',success:function(data,textStatus){jQuery('#updateTableC').html(data);},error:function(XMLHttpRequest,textStatus,errorThrown){}});\"  id=\"UndoRecon\" name=\"UndoRecon\">"

                obj.cell=["firstCol":firstCol,"date":undoReconciliate.date,"relationalBankAccount":undoReconciliate.by_bank_account_no,"description":undoReconciliate.description,"bankPaymentId":undoReconciliate.bank_payment_id,"amount":amount]
                undoReconciliateItem.add(obj)
                counter++
            }
            return undoReconciliateItem

        } catch (Exception ex){

            undoReconciliateItem = []
            return undoReconciliateItem
        }

    }

    public List wrapEntryReservationBudgetItemInGrid(List<GroovyRowResult> quickEntries, int start,journalId,
                                                     bookingPeriod,isForDelete=false) {
        List quickRerservationList = new ArrayList()
        def reservationEntry
        GridEntity obj
        String changeBooking
        String tempBookingPeriod = ""
        try {
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                reservationEntry = quickEntries[i];

                tempBookingPeriod = ""
                def bookStartPeriod = ""
                if (reservationEntry.booking_period_month == 12) {
                    bookStartPeriod = "Dec " + " - " + reservationEntry.booking_period_year
                } else {
                    bookStartPeriod = new CoreParamsHelper().monthNameShow(reservationEntry.booking_period_month) + " - " + reservationEntry.booking_period_year
                }
                def bookEndPeriod = ""
                if (reservationEntry.booking_period_month == 12) {
                    bookEndPeriod = "Dec " + " - " + reservationEntry.booking_period_year
                } else {
                    bookEndPeriod = new CoreParamsHelper().monthNameShow(reservationEntry.booking_period_month) + " - " + reservationEntry.booking_period_year
                }

                def showtotalGlAmount = reservationEntry.totalGlAmount
                def showtotalVat = reservationEntry.totalVat

                Double showtotalGlAmountc = Double.parseDouble(showtotalGlAmount)
                Double showtotalVatc = Double.parseDouble(showtotalVat)

                def showtotalGlAmounta = String.format("%.2f", showtotalGlAmountc)
                def showtotalVata = String.format("%.2f", showtotalVatc)

                obj = new GridEntity();
                obj.id = reservationEntry.id

                if(bookingPeriod == ""){
                    tempBookingPeriod = reservationEntry.booking_period_month
                }else{
                    tempBookingPeriod = bookingPeriod;
                }
                if(isForDelete){
                    String firstCol =   "<input type=\"checkbox\" name=\"budgetItemId\" value=\"${reservationEntry.id}\">";
                    obj.cell = ["firstCol":firstCol,"budgetItemName": reservationEntry.budgetItemName,"budgetCode": reservationEntry.budgetCode,"totalGlAmount": showtotalGlAmounta,"totalVat": showtotalVata,"bookStartPeriod":  bookStartPeriod]
                }
                else{

                    changeBooking = "<a href='javascript:changeReservationBudget(\"${reservationEntry.id}\",\"${reservationEntry.budget_name_id}\",\"${journalId}\",\"${tempBookingPeriod}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:showReservationDeleteDialog(\"${reservationEntry.id}\",\"${reservationEntry.budget_name_id}\")'><img width=\"16\" height=\"15\" alt=\"Delete\" src=\"${contextPath}/images/delete.png\"></a>"
                    obj.cell = ["budgetItemName": reservationEntry.budgetItemName,"budgetCode": reservationEntry.budgetCode,"totalGlAmount": showtotalGlAmounta,"totalVat": showtotalVata,"bookStartPeriod":  bookStartPeriod, "action" :changeBooking]
                }



                quickRerservationList.add(obj)
                counter++;
            }

            return quickRerservationList;
        } catch (Exception ex) {
            quickRerservationList = [];
            return quickRerservationList;
        }
    }
}
