package bv

import com.bv.util.DBSyncUtil
import grails.converters.JSON
import grails.io.IOUtils
import grails.plugins.jasper.JasperExportFormat
import grails.plugins.jasper.JasperReportDef
import grails.util.Holders
import jxl.Workbook
import jxl.write.Label
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook
import org.grails.plugins.web.taglib.ValidationTagLib

import java.text.DateFormat
import java.text.SimpleDateFormat

class ReportsController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    def springSecurityService
    def jasperService
    def reportService
    AuditReportService auditReportService
    DashboardDetailsService dashboardDetailsService;
    DatabaseSynchService databaseSynchService
    DBSyncUtil dBSyncUtil = new DBSyncUtil();

//    def grailsApplication
    def g = new ValidationTagLib()

    def glReport() {

        def fileType = params.reportFileType
        if (params.glAccountEndDropDown < params.glAccountStartDropDown) {

            flash.message=message(code: 'report.gl.flashMessage')

        } else {

            if (params.booking_period_start) {

                def company = new BudgetViewDatabaseService().executeQuery("SELECT company_full_name,logo FROM company_setup")
                String companyName = 'Your company setup is not done!'
                String companyLogo = 'images/companylogo/logo-default.png'
                if (company.size() > 0) {
                    companyName = company[0][0];
                    companyLogo = company[0][1]
                }

                //params.chartClassTypeId
                params.company_name = companyName;

                params.booking_period_start = Integer.parseInt(params.booking_period_start);
                params.booking_year_start = Integer.parseInt(params.booking_year_start);
                params.booking_period_end = Integer.parseInt(params.booking_period_end);
                params.booking_year_end = Integer.parseInt(params.booking_year_end);

                params.gl_companyName_label = g.message(code: 'report.gl.companyName.label')
                params.gl_startPeriod_label = g.message(code: 'report.gl.startPeriod.label')
                params.gl_endPeriod_label = g.message(code: 'report.gl.endPeriod.label')
                params.gl_reportDate_label = g.message(code: 'report.gl.reportDate.label')
                params.gl_titleBS_report = g.message(code: 'report.gl.titleBS.label')
                params.gl_titlePL_report = g.message(code: 'report.gl.titlePL.label')
                params.gl_bookNo_label = g.message(code: 'report.gl.bookNo.label')
                params.gl_date_label = g.message(code: 'report.gl.date.label')
                params.gl_period_label = g.message(code: 'report.gl.period.label')
                params.gl_type_label = g.message(code: 'report.gl.type.label')
                params.gl_relation_label = g.message(code: 'report.gl.relation.label')
                params.gl_paymentRef_label = g.message(code: 'report.gl.paymentRef.label')
                params.gl_debit_label = g.message(code: 'report.gl.debit.label')
                params.gl_credit_label = g.message(code: 'report.gl.credit.label')
                params.gl_total_label = g.message(code: 'report.gl.total.label')
                params.gl_balance_label = g.message(code: 'report.gl.balance.label')
                params.gl_report_account_code_wise = g.message(code: 'report.glReportAcountCodeWise.label')

                if (params.chart_class_type_id == "1") {
                    params.chart_class_type_id_start = 1
                    params.chart_class_type_id_end = 3
                } else if (params.chart_class_type_id == "2") {
                    params.chart_class_type_id_start = 4
                    params.chart_class_type_id_end = 7
                } else {
                    params._file = "gl_report_with_glCodeRange"
                    params.gl_account_code_start = params.glAccountStartDropDown
                    params.gl_account_code_end = params.glAccountEndDropDown
                }
                if(fileType=="pdf"){
                    params._format="PDF"
                    String realPath = request.getSession().getServletContext().getRealPath("/");
                    params.SUBREPORT_DIR = realPath + "reports/sub_reports/"

                    JasperReportDef rep = reportService.buildReportDefinition(params, request.locale, null)
                    ByteArrayOutputStream stream = reportService.generateReport(rep)

                    if (params.chart_class_type_id == "1") {
                        response.setHeader("Content-disposition", "attachment; filename=" + 'GL_Report_BS' + ".pdf")
                    } else if (params.chart_class_type_id == "2") {
                        response.setHeader("Content-disposition", "attachment; filename=" + 'GL_Report_PL' + ".pdf")
                    } else {
                        response.setHeader("Content-disposition", "attachment; filename=" + 'GL_Report_GL_CODE_RANGE' + ".pdf")
                    }
                    response.contentType = "application/pdf"
                    response.outputStream << stream.toByteArray()
//                    response.outputStream.flush()

                }else if(fileType=="csv"){

                    params._format="CSV"
                    String realPath = request.getSession().getServletContext().getRealPath("/");
                    params.SUBREPORT_DIR = realPath + "reports/sub_reports/"

                    JasperReportDef rep = reportService.buildReportDefinition(params, request.locale, null)
                    ByteArrayOutputStream stream = reportService.generateReport(rep)

                    if (params.chart_class_type_id == "1") {
                        response.setHeader("Content-disposition", "attachment; filename=" + 'GL_Report_BS' + ".csv")
                    } else if (params.chart_class_type_id == "2") {
                        response.setHeader("Content-disposition", "attachment; filename=" + 'GL_Report_PL' + ".csv")
                    } else {
                        response.setHeader("Content-disposition", "attachment; filename=" + 'GL_Report_GL_CODE_RANGE' + ".csv")
                    }
                    response.contentType = "text/csv"
                    response.outputStream << stream.toByteArray()
//                   response.outputStream.flush()
                }




            } else {
                def fiscalYr = new BudgetViewDatabaseService().executeQuery("SELECT yearBegin,yearEnd FROM FiscalYear WHERE status=1")
                render(view: "glReport", model: [startDate: new Date(fiscalYr[0][0].getTime()), endDate: new Date(fiscalYr[0][1].getTime())])
            }
        }

    }

    def balanceSheetNew() {

        def fileType = params.reportFileType
        if(params.booking_period) {

            def company = new BudgetViewDatabaseService().executeQuery("SELECT company_full_name,logo FROM company_setup")
            String companyName = 'Your company setup is not done!'
            String companyLogo = 'images/companylogo/logo-default.png'
            if (company.size() > 0) {
                companyName = company[0][0];
                companyLogo = company[0][1]
            }

            params.company_name = companyName;
            params.company_logo = getReportDirectory() + File.separator + companyLogo;
            params.booking_period = Integer.parseInt(params.booking_period);
            params.booking_year = Integer.parseInt(params.booking_year);

            //
            params.bs_report_title =g.message(code:'report.balanceSheet.title.label' )
            params.bs_companyName_label=g.message(code:'report.balanceSheet.companyName.label' )
            params.bs_period_label=g.message(code:'report.balanceSheet.period.label' )
            params.bs_reportDate_label=g.message(code:'report.balanceSheet.reportDate.label' )
            params.bs_total_label=g.message(code:'report.balanceSheet.total.label' )
            params.bs_profitReportedYear_label=g.message(code:'report.balanceSheet.profitForReportedYear.label' )
            params.bsd_report_title=g.message(code:'report.balanceSheet.bsdTitle.label' )
            //

            if(params.showDetails) {
                params._file="balance_sheet"
            }else{
                params._file="balance_sheet_summary"
            }

            if(fileType=="pdf"){
                params._format="PDF"
                JasperReportDef rep = reportService.buildReportDefinition(params,request.locale,null)
                ByteArrayOutputStream stream = reportService.generateReport(rep)
                response.setHeader("Content-disposition", "attachment; filename=" + 'BalanceSheet_Report' + ".pdf")
                response.contentType = "application/pdf"
                response.outputStream << stream.toByteArray()
//                response.outputStream.flush()

            }else if(fileType=="csv"){
                params._format="CSV"
                JasperReportDef rep = reportService.buildReportDefinition(params,request.locale,null)
                ByteArrayOutputStream stream = reportService.generateReport(rep)
                response.setHeader("Content-disposition", "attachment; filename=" + 'BalanceSheet_Report' + ".csv")
                response.contentType = "text/csv"
                response.outputStream << stream.toByteArray()
//                response.outputStream.flush()

            }

        }else {
            render(view: "balanceSheet")
        }
    }

    def agingReportNew() {

        def fileType = params.reportFileType
        if(params.aging_date) {

            def company = new BudgetViewDatabaseService().executeQuery("SELECT company_full_name,logo FROM company_setup")
            String companyName = 'Your company setup is not done!'
            String companyLogo = 'images/companylogo/logo-default.png'
            if (company.size() > 0) {
                companyName = company[0][0];
                companyLogo = company[0][1]
            }

            params.company_name = companyName;
            //params.company_logo = getReportDirectory() + File.separator + companyLogo;
            params.aging_title_report=g.message(code:'report.aging.title.label' )
            params.aging_companyName_label=g.message(code:'report.aging.companyName.label' )
            params.aging_agingDate_label=g.message(code:'report.aging.agingDate.label' )
            params.aging_reportDate_label=g.message(code:'report.aging.reportDate.label' )
            params.aging_name_label=g.message(code:'report.aging.name.label' )
            params.aging_id_label=g.message(code:'report.aging.id.label' )
            params.aging_invoiceNo_label=g.message(code:'report.aging.invoiceNo.label' )
            params.aging_paymentRef_label=g.message(code:'report.aging.paymentRef.label' )
            params.aging_invoiceAmount_label=g.message(code:'report.aging.invoiceAmount.label' )
            params.aging_paidAmount_label=g.message(code:'report.aging.paidAmount.label' )
            params.aging_dueAmount_label=g.message(code:'report.aging.dueAmount.label' )
            params.aging_dueDays_label=g.message(code:'report.aging.dueDays.label' )
            params.aging_paidStatus_label=g.message(code:'report.aging.paidStatus.label' )
            params.aging_total_label=g.message(code:'report.aging.total.label' )
            params.aging_totalInvoiceAmount_label=g.message(code:'report.aging.totalInvoiceAmount.label' )
            params.aging_totalPaidAmount_label=g.message(code:'report.aging.totalPaidAmount.label' )
            params.aging_totalDueAmount_label=g.message(code:'report.aging.totalDueAmount.label' )
            params.aging_invoiceIncome_label=g.message(code:'report.aging.incomeInvoice.label' )
            params.aging_expenseInvoice_label = g.message(code:'report.aging.expenseInvoice.label' )
            params.aging_invoiceDate_label = g.message(code:'report.aging.invoiceDate.label')

            String stDate = params.aging_date
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
//            df.setTimeZone(TimeZone.getTimeZone("UTC"))
//
            Date startDate = df.parse(stDate)
            String startDateFmt = startDate.format("yyyy-MM-dd")
            params.aging_date = startDateFmt

            if(params.showInvoiceAmount){
                params._file="aging_without_paid_amt"
            }else{
                params._file="aging"
            }

            if(fileType=="pdf"){
                params._format="PDF"
                JasperReportDef rep = reportService.buildReportDefinition(params,request.locale,null)
                ByteArrayOutputStream stream = reportService.generateReport(rep)
                response.setHeader("Content-disposition", "attachment; filename=" + 'Aging_Report' + ".pdf")
                response.contentType = "application/pdf"
                response.outputStream << stream.toByteArray()
//                response.outputStream.flush()

            }else if(fileType=="csv"){
                params._format="CSV"
                JasperReportDef rep = reportService.buildReportDefinition(params,request.locale,null)
                ByteArrayOutputStream stream = reportService.generateReport(rep)
                response.setHeader("Content-disposition", "attachment; filename=" + 'Aging_Report' + ".csv")
                response.contentType = "text/csv"
                response.outputStream << stream.toByteArray()
//                response.outputStream.flush()

            }

        }else {
            render(view: "agingReport")
        }
    }

    /******************** * Start IncomeStatement REPORT * *****************************/
    def incomeStatement() {

        def fileType = params.reportFileType
        if(params.start_booking_period) {

            def company = new BudgetViewDatabaseService().executeQuery("SELECT company_full_name,logo FROM company_setup")
            String companyName = 'Your company setup is not done!'
            String companyLogo = 'images/companylogo/logo-default.png'
            if (company.size() > 0) {
                companyName = company[0][0];
                companyLogo = company[0][1]
            }

            params.company_name = companyName;

            params.is_title_report=g.message(code:'report.incomeStatement.title.label' )
            params.is_companyName_label=g.message(code:'report.incomeStatement.companyName.label' )
            params.is_startPeriod_label=g.message(code:'report.incomeStatement.startPeriod.label' )
            params.is_endPeriod_label=g.message(code:'report.incomeStatement.endPeriod.label' )
            params.is_reportDate_label=g.message(code:'report.incomeStatement.reportDate.label' )
            params.is_totalIncome_label=g.message(code:'report.incomeStatement.totalIncome.label' )
            params.is_total_label=g.message(code:'report.incomeStatement.total.label' )
            params.is_grossProfit_label=g.message(code:'report.incomeStatement.grossProfit.label' )
            params.is_operatingIncome_label=g.message(code:'report.incomeStatement.operatingIncome.label' )
            params.is_netIncome_label=g.message(code:'report.incomeStatement.netIncome.label' )
            if(params.show_details){
                params._file = "income_statement_details";
            }
            else{
                params._file = "income_statement";
            }
            if(fileType=="pdf"){
                params._format="PDF"
                JasperReportDef rep = reportService.buildReportDefinition(params,request.locale,null)
                ByteArrayOutputStream stream = reportService.generateReport(rep)
                response.setHeader("Content-disposition", "attachment; filename=" + 'Income_Statement' + ".pdf")
                response.contentType = "application/pdf"
                response.outputStream << stream.toByteArray()
//                response.outputStream.flush()

            }else if(fileType=="csv"){
                params._format="CSV"
                JasperReportDef rep = reportService.buildReportDefinition(params,request.locale,null)
                ByteArrayOutputStream stream = reportService.generateReport(rep)
                response.setHeader("Content-disposition", "attachment; filename=" + 'Income_Statement' + ".csv")
                response.contentType = "text/csv"
                response.outputStream << stream.toByteArray()
//                response.outputStream.flush()
            }

        }else {
            render(view: "incomeStatement")
        }
    }

    /******************** * END of IncomeStatement REPORT * *****************************/
    def profitStatement() {
        params.reportName = 'income_statement.jasper';
    }

    def showReport() {

        println(params)
        String stDate = params.startDate
        String enDate = params.endDate
        DateFormat df = new SimpleDateFormat("dd-M-yyyy")
        df.setTimeZone(TimeZone.getTimeZone("UTC"))
        Date startDate = df.parse(stDate);

        Date endDate = df.parse(enDate);

        String reportName = 'balance_sheet.jasper';

        LinkedHashMap reportParam = new LinkedHashMap()
        String HOST_URL = getHostUrl() + "/static"

        int fiscalYear = 1;

        reportParam.put('startDate', startDate)
        reportParam.put('endDate', endDate)
        reportParam.put('stDate', stDate)
        reportParam.put('enDate', enDate)
        reportParam.put('HOST_URL', HOST_URL)
        reportParam.put('fiscalYear', fiscalYear)

        println(reportParam)

        def reportDef = new JasperReportDef(
                name: reportName,
                parameters: reportParam,
                fileFormat: JasperExportFormat.PDF_FORMAT)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType = "application/pdf"

        response.setHeader("Content-disposition", "attachment;filename=balanceSheet.pdf")
        response.outputStream << report.toByteArray()
    }

    /******************** * Start VAT REPORT * *****************************/
    def vatReport() {

        def fileType = params.reportFileType
        if(params.start_date) {
            def company = new BudgetViewDatabaseService().executeQuery("SELECT company_full_name,logo FROM company_setup")
            String companyName = 'Your company setup is not done!'
            String companyLogo = '../images/companylogo/logo-default.png'
            if (company.size() > 0) {
                companyName = company[0][0];
                if(company[0][1]) {
                    companyLogo = company[0][1]
                }
            }
            params.company_name = companyName;
            params.company_logo = getReportDirectory() + File.separator + companyLogo;
            params.start_date = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd-MM-yyyy").parse(params.start_date))
            params.end_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(params.end_date+" 23:59:59"))

            params.vat_title_report=g.message(code:'report.vat.title.label' )
            params.vat_companyName_label=g.message(code:'report.vat.companyName.label' )
            params.vat_startDate_label=g.message(code:'report.vat.startDate.label' )
            params.vat_endDate_label=g.message(code:'report.vat.endDate.label' )
            params.vat_reportDate_label=g.message(code:'report.vat.reportDate.label' )
            params.vat_invoicedAmount_label=g.message(code:'report.vat.invoicedAmount.label' )
            params.vat_vatAmount_label=g.message(code:'report.vat.vatAmount.label' )
            params.vat_total_label=g.message(code:'report.vat.total.label' )
            params.vat_vatToBePaid_label=g.message(code:'report.vat.vatToBePaid.label' )
            params.vat_check_label=g.message(code:'report.vat.check.label' )
            params.vat_vatOnIncome_label=g.message(code:'report.vat.vatOnIncome.label' )
            params.vat_vatOnCostandExpenses_label=g.message(code:'report.vat.vatOnCost&Expense.label' )
            params.vat_vatCorrections_label=g.message(code:'report.vat.vatCorrection.label' )
            if(fileType=="pdf"){
                params._format="PDF"
                JasperReportDef rep = reportService.buildReportDefinition(params,request.locale,null)
                ByteArrayOutputStream stream = reportService.generateReport(rep)
                response.setHeader("Content-disposition", "attachment; filename=" + 'vat' + ".pdf")
                response.contentType = "application/pdf"
                response.outputStream << stream.toByteArray()
//                response.outputStream.flush()
            }else if(fileType=="csv"){
                params._format="CSV"
                JasperReportDef rep = reportService.buildReportDefinition(params,request.locale,null)
                ByteArrayOutputStream stream = reportService.generateReport(rep)
                response.setHeader("Content-disposition", "attachment; filename=" + 'vat' + ".csv")
                response.contentType = "text/csv"
                response.outputStream << stream.toByteArray()
//                response.outputStream.flush()
            }


        }else {
            def fiscalYr = new BudgetViewDatabaseService().executeQuery("SELECT yearBegin,yearEnd FROM FiscalYear WHERE status=1")

            render(view: "vatReport",model: [startDate :new Date(fiscalYr[0][0].getTime()), endDate :new Date(fiscalYr[0][1].getTime())])
        }
    }

    def showVatReport() {

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date startDate = df.parse("01-" + params.startBookingMonth + "-" + params.startBookingYear);
        Date endDate = df.parse("31-" + params.endBokingMonth + "-" + params.endBookingYear);
        String startDateStr = startDate.format("yyyy-MM-dd")
        String endDateStr = endDate.format("yyyy-MM-dd")
        def toDay = new Date()
        String todayD = toDay.format("yyyy-MM-dd")

        LinkedHashMap reportParam = new LinkedHashMap()
        String HOST_URL = getHostUrl() + "/static"

        def company = CompanySetup.findAll()
        String companyName = ''
        String companyAddress = ''
        if (company.size() > 0) {
            companyName += company.get(0).companyFullName;
            companyAddress += company.get(0).addressLine1 + " " + company.get(0).addressLine2
        } else {
            companyName += 'Your company setup is not done!';
            companyAddress += '=========??========'
        }

        String subRDir = HOST_URL + "/web-app/reports"
        String SUBREPORT_DIR = "${servletContext.getRealPath('/reports')}/".replace('/', '\\')
        //println(SUBREPORT_DIR)
        reportParam.put('startDate', startDateStr)
        reportParam.put('endDate', endDateStr)
        reportParam.put('todayD', todayD)
        reportParam.put('HOST_URL', HOST_URL)
        reportParam.put('SUBREPORT_DIR', SUBREPORT_DIR)
        reportParam.put('companyName', companyName)
        reportParam.put('companyAddress', companyAddress)
        String reportName = 'newVatReportMain.jasper';
        reportParam.put('SUBREPORT_DIR', getReportDirectory() + File.separator)
        def reportDef = new JasperReportDef(
                name: reportName,
                parameters: reportParam,
                fileFormat: JasperExportFormat.PDF_FORMAT)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType = "application/pdf"
        response.setHeader("Content-disposition", "attachment;filename=varReport.pdf")
        response.outputStream << report.toByteArray()

    }
    /******************** * END of VAT REPORT * *****************************/

    /******************** * Start AGING REPORT * *****************************/
    def agingReport() {
        render(view: "agingReport")
    }

    def showAgingReport() {

        def company = CompanySetup.findAll()
        String companyName = ''
        String companyAddress = ''
        if (company.size() > 0) {
            companyName += company.get(0).companyFullName;
            companyAddress += company.get(0).addressLine1 + " " + company.get(0).addressLine2
        } else {
            companyName += 'Your company setup is not done!';
            companyAddress += '=========??========'
        }

        def cusPrefix = SystemPrefix.executeQuery("SELECT prefix FROM SystemPrefix WHERE id=1")
        def iniPrefix = SystemPrefix.executeQuery("SELECT prefix FROM SystemPrefix WHERE id=8")

        String stDate = params.startDate
        DateFormat df = new SimpleDateFormat("dd-M-yyyy")
        df.setTimeZone(TimeZone.getTimeZone("UTC"))
        Date startDate = df.parse(stDate);

        def toDay = new Date()
        String todayD = toDay.format("yyyy-MM-dd")
        String startDateSTR = startDate.format("yyyy-MM-dd")

        String reportName = 'agingReport.jasper';

        LinkedHashMap reportParam = new LinkedHashMap()
        String HOST_URL = getHostUrl() + "/static"


        String SUBREPORT_DIR = "${servletContext.getRealPath('/reports')}/".replace('/', '\\')
        println(SUBREPORT_DIR)

        reportParam.put('startDate', startDateSTR)
        reportParam.put('todayD', todayD)
        reportParam.put('HOST_URL', HOST_URL)
        reportParam.put('cusPrefix', (cusPrefix[0]) ? (cusPrefix[0]) : 'CUS')
        reportParam.put('inviPrefix', (iniPrefix[0]) ? (iniPrefix[0]) : 'INVI')
        reportParam.put('companyName', companyName)
        reportParam.put('companyAddress', companyAddress)
        reportParam.put('SUBREPORT_DIR', getReportDirectory() + File.separator)
        println reportParam
        def reportDef = new JasperReportDef(
                name: reportName,
                parameters: reportParam,
                fileFormat: JasperExportFormat.PDF_FORMAT)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType = "application/pdf"
        response.setHeader("Content-disposition", "attachment;filename=agingReport.pdf")
        response.outputStream << report.toByteArray()
    }

    /********************** END GL REPORT************************** */
//    public static String getReportDirectory() {
    def getReportDirectory() {
        String REPORT_DIRECTORY = null;
        if (!REPORT_DIRECTORY) {
            File reportFolder = Holders.applicationContext.getResource('/reports').file
            REPORT_DIRECTORY = reportFolder.absolutePath;
        }
//        return REPORT_DIRECTORY;
    }

    def getHostUrl() {
        def protocol = request.isSecure() ? "https://" : "http://"
        def host = request.getServerName()
        def port = request.getServerPort()
        def context = request.getServletContext().getContextPath()

        return protocol + host + ":" + port + context
    }

    def createNewFolder() {

        def user = springSecurityService.principal
        def webRootDir = servletContext.getRealPath("/")
        def reportDir = new File(webRootDir, "/reports/")
        reportDir.mkdirs()
        String fileNameCreate = webRootDir + "/reports/" + user.id;

        File file = new File(fileNameCreate);

        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        }

    }

    def exportAuditFile() {

        Map activeFiscalYear = new CoreParamsHelper().getFiscalYearInformation()
        def user = springSecurityService.principal

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm");
        Date date = new Date();
        String strDateCreated = dateFormat.format(date);

        //Prepare directory
        createNewFolder();
        def webRootDir = servletContext.getRealPath("/")
        def reportDir = new File(webRootDir, "/reports/")
        String strXSD_FilePath = webRootDir + "/reports/" + "XmlAuditfileFinancieel3.2.xsd";

        String fileName = webRootDir + "/reports/" + user.id + "/" + "AuditReport_" + strDateCreated + ".xml"
        File files = new File(fileName)

        String content = ""
        try {
            String strHeaderContent = auditReportService.getAuditFileHeaderInfo(activeFiscalYear)
            String strCompanyInfo = auditReportService.getCompanyGeneralInformation()

            String strCustomerSupplierInfo = auditReportService.getCustomerSupplierInformation()
            String strXmlGeneralLedger = auditReportService.getGeneralLedger()
//            String strTaxonomy = auditReportService.getTaxonomy()
//            String strBasics = auditReportService.getBasics()
            String strVatCode = auditReportService.getVatCode()
            String strPeriodTime = auditReportService.getPeriodTime(activeFiscalYear)
            String strOpenBalanceGeneralInfo = auditReportService.getOpeningBalanceGeneralInfo()
            String strOpenBalanceLineInfo = auditReportService.getOpeningBalanceLineInfo()
            String strOpenBalanceSubLedgers = auditReportService.getOpenBalanceSubLedgers()
            String strTransactionGeneralInfo = auditReportService.getTransactionGeneralInfo()
            String strJournalInfo = auditReportService.getJournalInfo()
            String strSubledger = auditReportService.getSubLedger()

            content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<auditfile xmlns=\"http://www.auditfiles.nl/XAF/3.2\" xmlns:str=\"http://www.ec-design.nl/Abz/BCPP/1.0/structures\"\n" +
                    "           xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.auditfiles.nl/XAF/3.2\n" +
                    "${strXSD_FilePath}\">\n" + strHeaderContent +
                    "    <company>\n" +
                    strCompanyInfo+
                    "<customersSuppliers>\n"+
                    strCustomerSupplierInfo+
                    "</customersSuppliers>\n" +
                    "<generalLedger>\n" +
                    strXmlGeneralLedger +
//                                    "            <taxonomies>\n" +
//                                                    strTaxonomy +
//                                    "            </taxonomies>\n" +
//
//                                    "            <basics>\n" +
//                                                      strBasics +
//                                    "            </basics>\n" +
                    "        </generalLedger>\n" +
                    "        <vatCodes>\n"+
                    strVatCode+
                    "        </vatCodes>\n" +
                    "        <periods>\n"+
                    strPeriodTime+
                    "        </periods>\n" +
//                    "        <openingBalance>\n" +
//                                    strOpenBalanceGeneralInfo+
//                                    strOpenBalanceLineInfo+
//                                       "            <obSubledgers>\n" +
//                                                           strOpenBalanceSubLedgers+
//                                       "            </obSubledgers>\n" +
//                    "        </openingBalance>\n" +
                    "        <transactions>\n" +
                    strTransactionGeneralInfo+
                    strJournalInfo +
                    "            <subledgers>\n" +
                    strSubledger+
                    "            </subledgers>\n" +
                    "        </transactions>\n" +
                    "    </company>\n" +
                    "</auditfile>"

            if (!files.exists()) {
                files.createNewFile()
            }

            FileWriter fw = new FileWriter(files.getAbsoluteFile())
            BufferedWriter bw = new BufferedWriter(fw)
            bw.write(content)
            bw.close()

        } catch (IOException e) {
            e.printStackTrace()
        }

        fileName = "AuditReport_" + strDateCreated + ".xml"

        response.setHeader("Content-Type", "application/octet-stream;")
        response.setHeader("Content-Disposition", "attachment;filename=\"${fileName}\"")
        response.setHeader("Content-Length", "${files.size()}")
        response.outputStream << files.getBytes()
    }

    def exportSummaryView(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm");
        Date date = new Date();
        String strDateCreated = dateFormat.format(date);
        String fileName="Summary_Report_"+strDateCreated

        def ActiveFiscalYear = new CoreParamsHelper().getActiveFiscalYear()
        def fiscalYearInfo = new CoreParamsHelper().getActiveFiscalYearInformation(ActiveFiscalYear)

        //Getting budget customer data.

        response.setContentType('application/vnd.ms-excel')
        response.setHeader('Content-Disposition', 'Attachment;Filename='+fileName+'.xls')
        WritableWorkbook workbook = Workbook.createWorkbook(response.outputStream)


        WritableSheet sheet1 = workbook.createSheet("Summary View", 0)
//       set column width
        sheet1.setColumnView(0,40)
        sheet1.setColumnView(1,20)
        sheet1.setColumnView(2,20)
        sheet1.setColumnView(3,15)
        sheet1.setColumnView(4,15)
        sheet1.setColumnView(5,15)
        sheet1.setColumnView(6,15)
        sheet1.setColumnView(7,15)
        sheet1.setColumnView(8,15)
        sheet1.setColumnView(9,15)
        sheet1.setColumnView(10,15)
        sheet1.setColumnView(11,15)
        sheet1.setColumnView(12,15)
        sheet1.setColumnView(13,15)
        sheet1.setColumnView(14,15)


//      set column name
        sheet1.addCell(new Label(0,0, "Budget name"))
        sheet1.addCell(new Label(1,0, "income/expense"))
        sheet1.addCell(new Label(2,0, "Budget/invoice"))
        sheet1.addCell(new Label(3,0, "January"))
        sheet1.addCell(new Label(4,0, "February"))
        sheet1.addCell(new Label(5,0, "March"))
        sheet1.addCell(new Label(6,0, "April"))
        sheet1.addCell(new Label(7,0, "May"))
        sheet1.addCell(new Label(8,0, "June"))
        sheet1.addCell(new Label(9,0, "July"))
        sheet1.addCell(new Label(10,0, "August"))
        sheet1.addCell(new Label(11,0, "September"))
        sheet1.addCell(new Label(12,0, "October"))
        sheet1.addCell(new Label(13,0, "November"))
        sheet1.addCell(new Label(14,0, "December"))

        int nRowNo = 1;
        //for income budget summary
        ArrayList budgetCustomerArr = dashboardDetailsService.getBudgetCustomerData(fiscalYearInfo);
        //Getting customer account data.
        ArrayList customerAccountData = dashboardDetailsService.getCustomerAccountData(fiscalYearInfo,budgetCustomerArr);

        ArrayList monthwiseIncomeBudgetArr=dashboardDetailsService.getMonthwiseBudgetSummary(budgetCustomerArr,customerAccountData,"Income","Budget")

        for(int i=0;i<monthwiseIncomeBudgetArr.size();i++)
        {
            sheet1.addCell(new Label(0,nRowNo, monthwiseIncomeBudgetArr[i].budgetName))
            sheet1.addCell(new Label(1,nRowNo, monthwiseIncomeBudgetArr[i].incomeOrExpense))
            sheet1.addCell(new Label(2,nRowNo, monthwiseIncomeBudgetArr[i].budgetOrInvoice))
            sheet1.addCell(new Label(3,nRowNo, monthwiseIncomeBudgetArr[i].janAmount.toString()))
            sheet1.addCell(new Label(4,nRowNo, monthwiseIncomeBudgetArr[i].febAmount.toString()))
            sheet1.addCell(new Label(5,nRowNo, monthwiseIncomeBudgetArr[i].marAmount.toString()))
            sheet1.addCell(new Label(6,nRowNo, monthwiseIncomeBudgetArr[i].aprAmount.toString()))
            sheet1.addCell(new Label(7,nRowNo, monthwiseIncomeBudgetArr[i].mayAmount.toString()))
            sheet1.addCell(new Label(8,nRowNo, monthwiseIncomeBudgetArr[i].junAmount.toString()))
            sheet1.addCell(new Label(9,nRowNo, monthwiseIncomeBudgetArr[i].julAmount.toString()))
            sheet1.addCell(new Label(10,nRowNo, monthwiseIncomeBudgetArr[i].augAmount.toString()))
            sheet1.addCell(new Label(11,nRowNo, monthwiseIncomeBudgetArr[i].sepAmount.toString()))
            sheet1.addCell(new Label(12,nRowNo, monthwiseIncomeBudgetArr[i].octAmount.toString()))
            sheet1.addCell(new Label(13,nRowNo, monthwiseIncomeBudgetArr[i].novAmount.toString()))
            sheet1.addCell(new Label(14,nRowNo, monthwiseIncomeBudgetArr[i].decAmount.toString()))
            nRowNo++;
        }

        //Getting income invoice data.
        ArrayList invoiceIncomeDetails = dashboardDetailsService.getIncomeInvoiceData(fiscalYearInfo,budgetCustomerArr);

        println("report\n" +invoiceIncomeDetails);

        ArrayList monthwiseIncomeInvoiceArr=dashboardDetailsService.getMonthwiseInvoiceSummary(budgetCustomerArr,invoiceIncomeDetails,"Income","Invoice");

        for(int i=0;i<monthwiseIncomeInvoiceArr.size();i++)
        {
            sheet1.addCell(new Label(0,nRowNo, monthwiseIncomeInvoiceArr[i].budgetName))
            sheet1.addCell(new Label(1,nRowNo, monthwiseIncomeInvoiceArr[i].incomeOrExpense))
            sheet1.addCell(new Label(2,nRowNo, monthwiseIncomeInvoiceArr[i].budgetOrInvoice))
            sheet1.addCell(new Label(3,nRowNo, monthwiseIncomeInvoiceArr[i].janAmount.toString()))
            sheet1.addCell(new Label(4,nRowNo, monthwiseIncomeInvoiceArr[i].febAmount.toString()))
            sheet1.addCell(new Label(5,nRowNo, monthwiseIncomeInvoiceArr[i].marAmount.toString()))
            sheet1.addCell(new Label(6,nRowNo, monthwiseIncomeInvoiceArr[i].aprAmount.toString()))
            sheet1.addCell(new Label(7,nRowNo, monthwiseIncomeInvoiceArr[i].mayAmount.toString()))
            sheet1.addCell(new Label(8,nRowNo, monthwiseIncomeInvoiceArr[i].junAmount.toString()))
            sheet1.addCell(new Label(9,nRowNo, monthwiseIncomeInvoiceArr[i].julAmount.toString()))
            sheet1.addCell(new Label(10,nRowNo, monthwiseIncomeInvoiceArr[i].augAmount.toString()))
            sheet1.addCell(new Label(11,nRowNo, monthwiseIncomeInvoiceArr[i].sepAmount.toString()))
            sheet1.addCell(new Label(12,nRowNo, monthwiseIncomeInvoiceArr[i].octAmount.toString()))
            sheet1.addCell(new Label(13,nRowNo, monthwiseIncomeInvoiceArr[i].novAmount.toString()))
            sheet1.addCell(new Label(14,nRowNo, monthwiseIncomeInvoiceArr[i].decAmount.toString()))
            nRowNo++;
        }




//for expense budget summary
        //Getting budget vendor data.
        ArrayList budgetVendorArr = dashboardDetailsService.getBudgetVendorData(fiscalYearInfo);

        //Getting vendor account data.
        ArrayList vendorAccountData = dashboardDetailsService.getVendorAccountData(fiscalYearInfo,budgetVendorArr);

        ArrayList monthwiseExpenseBudgetArr=dashboardDetailsService.getMonthwiseBudgetSummary(budgetVendorArr,vendorAccountData,"Expense","Budget");

        for(int i=0;i<monthwiseExpenseBudgetArr.size();i++)
        {
            sheet1.addCell(new Label(0,nRowNo, monthwiseExpenseBudgetArr[i].budgetName))
            sheet1.addCell(new Label(1,nRowNo, monthwiseExpenseBudgetArr[i].incomeOrExpense))
            sheet1.addCell(new Label(2,nRowNo, monthwiseExpenseBudgetArr[i].budgetOrInvoice))
            sheet1.addCell(new Label(3,nRowNo, monthwiseExpenseBudgetArr[i].janAmount.toString()))
            sheet1.addCell(new Label(4,nRowNo, monthwiseExpenseBudgetArr[i].febAmount.toString()))
            sheet1.addCell(new Label(5,nRowNo, monthwiseExpenseBudgetArr[i].marAmount.toString()))
            sheet1.addCell(new Label(6,nRowNo, monthwiseExpenseBudgetArr[i].aprAmount.toString()))
            sheet1.addCell(new Label(7,nRowNo, monthwiseExpenseBudgetArr[i].mayAmount.toString()))
            sheet1.addCell(new Label(8,nRowNo, monthwiseExpenseBudgetArr[i].junAmount.toString()))
            sheet1.addCell(new Label(9,nRowNo, monthwiseExpenseBudgetArr[i].julAmount.toString()))
            sheet1.addCell(new Label(10,nRowNo, monthwiseExpenseBudgetArr[i].augAmount.toString()))
            sheet1.addCell(new Label(11,nRowNo, monthwiseExpenseBudgetArr[i].sepAmount.toString()))
            sheet1.addCell(new Label(12,nRowNo, monthwiseExpenseBudgetArr[i].octAmount.toString()))
            sheet1.addCell(new Label(13,nRowNo, monthwiseExpenseBudgetArr[i].novAmount.toString()))
            sheet1.addCell(new Label(14,nRowNo, monthwiseExpenseBudgetArr[i].decAmount.toString()))
            nRowNo++;
        }

        ArrayList expenseInvoiceData = dashboardDetailsService.getExpenseInvoiceData(fiscalYearInfo,budgetVendorArr);
//       println(expenseInvoiceData);

        ArrayList monthwiseExpenseInvoiceArr=dashboardDetailsService.getMonthwiseInvoiceSummary(budgetVendorArr,expenseInvoiceData,"Expense","Invoice");

        for(int i=0;i<monthwiseExpenseInvoiceArr.size();i++)
        {
            sheet1.addCell(new Label(0,nRowNo, monthwiseExpenseInvoiceArr[i].budgetName))
            sheet1.addCell(new Label(1,nRowNo, monthwiseExpenseInvoiceArr[i].incomeOrExpense))
            sheet1.addCell(new Label(2,nRowNo, monthwiseExpenseInvoiceArr[i].budgetOrInvoice))
            sheet1.addCell(new Label(3,nRowNo, monthwiseExpenseInvoiceArr[i].janAmount.toString()))
            sheet1.addCell(new Label(4,nRowNo, monthwiseExpenseInvoiceArr[i].febAmount.toString()))
            sheet1.addCell(new Label(5,nRowNo, monthwiseExpenseInvoiceArr[i].marAmount.toString()))
            sheet1.addCell(new Label(6,nRowNo, monthwiseExpenseInvoiceArr[i].aprAmount.toString()))
            sheet1.addCell(new Label(7,nRowNo, monthwiseExpenseInvoiceArr[i].mayAmount.toString()))
            sheet1.addCell(new Label(8,nRowNo, monthwiseExpenseInvoiceArr[i].junAmount.toString()))
            sheet1.addCell(new Label(9,nRowNo, monthwiseExpenseInvoiceArr[i].julAmount.toString()))
            sheet1.addCell(new Label(10,nRowNo, monthwiseExpenseInvoiceArr[i].augAmount.toString()))
            sheet1.addCell(new Label(11,nRowNo, monthwiseExpenseInvoiceArr[i].sepAmount.toString()))
            sheet1.addCell(new Label(12,nRowNo, monthwiseExpenseInvoiceArr[i].octAmount.toString()))
            sheet1.addCell(new Label(13,nRowNo, monthwiseExpenseInvoiceArr[i].novAmount.toString()))
            sheet1.addCell(new Label(14,nRowNo, monthwiseExpenseInvoiceArr[i].decAmount.toString()))
            nRowNo++;
        }

        workbook.write();
        workbook.close();


    }

    def databaseMaintenance(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm");
        Date date = new Date();
        String strDateCreated = dateFormat.format(date);
        String fileName="Database_Maintenance_"+strDateCreated

        response.setContentType('application/vnd.ms-excel')
        response.setHeader('Content-Disposition', 'Attachment;Filename ='+fileName+'.xls')
        WritableWorkbook workbook = Workbook.createWorkbook(response.outputStream)
        WritableSheet sheet1 = workbook.createSheet("Database Maintenance", 0)

        //       set column width
        sheet1.setColumnView(0,30)
        sheet1.setColumnView(1,30)
        sheet1.setColumnView(2,30)
        sheet1.setColumnView(3,30)
        sheet1.setColumnView(4,30)
        sheet1.setColumnView(5,30)
        sheet1.setColumnView(6,30)
        sheet1.setColumnView(7,30)
        sheet1.setColumnView(8,30)
        sheet1.setColumnView(9,30)
        sheet1.setColumnView(10,30)

        // ***************Header******************//
        sheet1.addCell(new Label(0,0, "Company name"))
        sheet1.addCell(new Label(1,0, "Database Name"))
        sheet1.addCell(new Label(2,0, "Creation date"))
        sheet1.addCell(new Label(3,0, "Number of Income Budgets"))
        sheet1.addCell(new Label(4,0, "Number of Expense Budgets"))
        sheet1.addCell(new Label(5,0, "Number of Lines in Trans_master"))
        sheet1.addCell(new Label(6,0, "First Transaction Date"))
        sheet1.addCell(new Label(7,0, "Last Created Date"))
        sheet1.addCell(new Label(8,0, "Booking Year With Zero"))
        sheet1.addCell(new Label(9,0, "Account Code With Null"))
        sheet1.addCell(new Label(10,0, "Status"))

        ArrayList dbMaintenanceData = databaseSynchService.databaseMaintainceList();

        for(int i=0;i<dbMaintenanceData.size();i++){
            sheet1.addCell(new Label(0,i+1, dbMaintenanceData[i].companyName))
            sheet1.addCell(new Label(1,i+1, dbMaintenanceData[i].databaseName))
            sheet1.addCell(new Label(2,i+1, dbMaintenanceData[i].creationDate.toString()))
            sheet1.addCell(new Label(3,i+1, dbMaintenanceData[i].numberOfIncomeBudgets.toString()))
            sheet1.addCell(new Label(4,i+1, dbMaintenanceData[i].numberOfExpenseBudgets.toString()))
            sheet1.addCell(new Label(5,i+1, dbMaintenanceData[i].numberOfLinesInTrans_master.toString()))
            sheet1.addCell(new Label(6,i+1, dbMaintenanceData[i].firstTransactionDate.toString()))
            sheet1.addCell(new Label(7,i+1, dbMaintenanceData[i].lastCreatedDate.toString()))
            sheet1.addCell(new Label(8,i+1, dbMaintenanceData[i].bookingYearWithZero.toString()))
            sheet1.addCell(new Label(9,i+1, dbMaintenanceData[i].accountCodeWithNull.toString()))
            sheet1.addCell(new Label(10,i+1, dbMaintenanceData[i].status))


        }

        workbook.write();
        workbook.close();
    }

    def reportInvoiceWithoutBudget(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm");
        Date date = new Date();
        String strDateCreated = dateFormat.format(date);
        String fileName="Invoices_without_Budget"+strDateCreated

        response.setContentType('application/vnd.ms-excel')
        response.setHeader('Content-Disposition', 'Attachment;Filename ='+fileName+'.xls')
        WritableWorkbook workbook = Workbook.createWorkbook(response.outputStream)
        WritableSheet sheet1 = workbook.createSheet("Invoices Without Budget", 0)

        //       set column width
        sheet1.setColumnView(0,30)
        sheet1.setColumnView(1,30)
        sheet1.setColumnView(2,30)
        sheet1.setColumnView(3,30)
        sheet1.setColumnView(4,30)
        sheet1.setColumnView(5,30)
        sheet1.setColumnView(6,30)
        sheet1.setColumnView(7,30)
        sheet1.setColumnView(8,30)
        sheet1.setColumnView(9,30)
        sheet1.setColumnView(10,30)


        // ***************Header*****************/
        sheet1.addCell(new Label(0,0, "Database name"))
        sheet1.addCell(new Label(1,0, "Invoice Type"))
        sheet1.addCell(new Label(2,0, "Id"))
        sheet1.addCell(new Label(3,0, "Booking Period"))
        sheet1.addCell(new Label(4,0, "Booking Year"))
        sheet1.addCell(new Label(5,0, "Budget Customer/Vendor "))
        sheet1.addCell(new Label(6,0, "Budget Item Id"))
        sheet1.addCell(new Label(7,0, "Invoice No"))
        sheet1.addCell(new Label(8,0, "Total Gl Amount"))
        sheet1.addCell(new Label(9,0, "Total vat"))
        sheet1.addCell(new Label(10,0, "Payment Ref"))

        ArrayList invoiceData = databaseSynchService.getInvoiceWithoutBudgetList();

        for(int i=0;i<invoiceData.size();i++){
            sheet1.addCell(new Label(0,i+1, invoiceData[i].databaseName.toString()))
            sheet1.addCell(new Label(1,i+1, invoiceData[i].type.toString()))
            sheet1.addCell(new Label(2,i+1, invoiceData[i].id.toString()))
            sheet1.addCell(new Label(3,i+1, invoiceData[i].bookingPeriod.toString()))
            sheet1.addCell(new Label(4,i+1, invoiceData[i].bookingYear.toString()))
            sheet1.addCell(new Label(5,i+1, invoiceData[i].budgetCustomerID.toString()))
            sheet1.addCell(new Label(6,i+1, invoiceData[i].budgetItemIncomeId.toString()))
            sheet1.addCell(new Label(7,i+1, invoiceData[i].invoiceNo.toString()))
            sheet1.addCell(new Label(8,i+1, invoiceData[i].totalVat.toString()))
            sheet1.addCell(new Label(9,i+1, invoiceData[i].totalGlAmount.toString()))
            sheet1.addCell(new Label(10,i+1,invoiceData[i].paymentRef.toString()))
        }
        workbook.write();
        workbook.close();


    }

    def showDataListForDumping(){
        def gridOutput
        String pageNumber = "1"
        def context = request.getServletContext().getContextPath()
        print(context)
        def resultArr = databaseSynchService.dbVersionInfoList()
        List dbSyncList = dBSyncUtil.wrapDbVersionListForDumping(resultArr,context)

        def count = databaseSynchService.countDbVersionList()
        LinkedHashMap result = [draw: 1, recordsTotal: count, recordsFiltered:  count, data:dbSyncList.cell]

        gridOutput = result as JSON
        render gridOutput;
    }

    def dbDump(){
        def output = databaseSynchService.dbDump(params.dbName)
        Thread thread = new Thread()
//        thread.sleep(60000)
        thread.sleep(20000)
        downloadFile(output)

    }

    def downloadFile(def dbName) {
        InputStream contentStream
        try {
            def file = new File("/home/tomcatbu/defaultDBForDbSync.sql")
//            def file = new File("E:\\${dbName}.sql")
            response.setHeader "Content-disposition", "attachment; filename= "+dbName+".sql"
            response.setHeader("Content-Length", "file-size")
            response.setContentType("file-mime-type")
            contentStream = file.newInputStream()
            response.outputStream << contentStream
            webRequest.renderView = false
        }finally {
            IOUtils.closeQuietly(contentStream)
        }
    }

    def databaseWithoutCompanyBankAccount(){

    }

    def showDatabaseWithoutCompanyBankAccount(){
        def gridOutput
        String pageNumber = "1"

        def resultArr = databaseSynchService.dbListWithoutCompanyBankAccount()
        List dbSyncList = dBSyncUtil.wrapDbListWithoutCompanyBankAccount(resultArr)

        def count = dbSyncList.size()
        LinkedHashMap result = [draw: 1, recordsTotal: count, recordsFiltered:  count,data:dbSyncList.cell]

        gridOutput = result as JSON
        render gridOutput;

    }

    def getTransactionsAmount = {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm")
        Date date = new Date()
        String strDateCreated = dateFormat.format(date)
        String fileName="TransAmount_"+strDateCreated

        response.setContentType('application/vnd.ms-excel')
        response.setHeader('Content-Disposition', 'Attachment;Filename ='+fileName+'.xls')
        WritableWorkbook workbook = Workbook.createWorkbook(response.outputStream)
        WritableSheet sheet1 = workbook.createSheet("Invoices Without Budget", 0)

        //set column width
        sheet1.setColumnView(0,30)
        sheet1.setColumnView(1,30)
        sheet1.setColumnView(2,30)

        // ***************Header******************//
        sheet1.addCell(new Label(0,0, "Company name"))
        sheet1.addCell(new Label(1,0, "Database Name"))
        sheet1.addCell(new Label(2,0, "Trans_Amount"))

        def dbMaintenanceData = databaseSynchService.databaseTransAmountList()

        for(int i=0; i< dbMaintenanceData.size(); i++){
            sheet1.addCell(new Label(0,i+1, dbMaintenanceData[i].companyName))
            sheet1.addCell(new Label(1,i+1, dbMaintenanceData[i].databaseName))
            sheet1.addCell(new Label(2,i+1, dbMaintenanceData[i].transAmount))
        }

        workbook.write()
        workbook.close()
    }
}
