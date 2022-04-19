package bv

import auth.User
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional

import java.text.DateFormat
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat


@Transactional
class AuditReportService {

    def grailsApplication

    def getTagNameWithValue(String strStartTag,String srtValue,String strEndTag,String isMandatory )
    {
        String strTagWithValue = ""

        if(srtValue.isEmpty() || srtValue == "null")
        {
            if(isMandatory == "V"){
                strTagWithValue = strStartTag + " " + strEndTag
                return strTagWithValue;
            }
            else{
                return strTagWithValue;
            }

        }

        strTagWithValue = strStartTag + srtValue + strEndTag

        return strTagWithValue;
    }

    def getAuditFileHeaderInfo(def activeFiscalYear) {

        //Application History
        def appVersion = grailsApplication.metadata['app.version']
        def appProfile = grailsApplication.metadata['app.buildProfile']
        def appGrailsVersion = grailsApplication.metadata['app.grails.version']
//        def appBuildDate = grailsApplication.metadata['app.buildDate']

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String strDateCreated = dateFormat.format(date); //2013/10/15

        // Currecies
        def currCode = "EUR";
        def softwareVersion=0;
        def queryCurrecies = "SELECT * FROM currencies ORDER BY id DESC LIMIT 1"
        def resultCurrencies = new BudgetViewDatabaseService().executeQuery(queryCurrecies)
        if (resultCurrencies) {
            currCode = resultCurrencies[0][3]
        }

        //DB version
        def queryVersion = "SELECT max(version) FROM db_version;"
        def resultVersion = new BudgetViewDatabaseService().executeQuery(queryVersion)
        if (resultVersion) {
            softwareVersion = resultVersion[0][0]
        }

        String strContent = " <header>\n" +
                " <fiscalYear>" + activeFiscalYear.ST_DT_YY + "</fiscalYear>\n" +
                " <startDate>" + activeFiscalYear.ST_DT_yy_mm_dd + "</startDate>\n" +
                " <endDate>" + activeFiscalYear.ED_DT_yy_mm_dd + "</endDate>\n" +
                " <curCode>" + currCode + "</curCode>\n" +
                " <dateCreated>" + strDateCreated + "</dateCreated>\n" +
                getTagNameWithValue("<softwareDesc>","Budgetview","</softwareDesc>\n","F") +
                getTagNameWithValue("<softwareVersion>",softwareVersion.toString(),"</softwareVersion>\n","F") +
                " </header>\n";

        return strContent;
    }

    def getCompanyGeneralInformation() {

        SecurityContext ctx = SecurityContextHolder.getContext();
        Authentication auth = ctx.getAuthentication();
        String username = auth.getName();

        User user = User.findByUsername(username)
        Integer authUserCompanyId = user.getAt('businessCompanyId')

        def queryCompanySetup = "SELECT * FROM company_setup ORDER BY id DESC LIMIT 1"
        def resultCompanySetup = new BudgetViewDatabaseService().executeQuery(queryCompanySetup)

        def resultCompanyDbName = BusinessCompany.findById(authUserCompanyId);
        def companyFullName
        def companyCountry
        if (resultCompanySetup) {
            companyFullName = resultCompanySetup[0][9]
        }

        //Street Address.
        String strStreetName = resultCompanySetup[0][3];//address_line1
        String strHouseNo = resultCompanySetup[0][4];//address_line2

        strStreetName = strStreetName + " " + strHouseNo;

        String strCity = resultCompanySetup[0][7];//city
        String strPostalCode = resultCompanySetup[0][18];//general_postal_code;
        String strCountry = "NL";
        String strStreetAddress = "";

        if((strStreetName.isEmpty()&& strCity.isEmpty()&& strPostalCode.isEmpty())||
                (strStreetName == "null" && strCity == "null" && strPostalCode == "null")){

        }
        else{

            strStreetAddress =" <streetAddress>\n" +
                    getTagNameWithValue("<streetname>",strStreetName,"</streetname>\n","F") +
                    getTagNameWithValue("<city>",strCity,"</city>\n","F") +
                    getTagNameWithValue("<postalCode>",strPostalCode,"</postalCode>\n","F") +
                    getTagNameWithValue("<country>",strCountry,"</country>\n","F") +
                    " </streetAddress>\n"

        }

        //Postal Address
        strStreetName = resultCompanySetup[0][28]//postal_address_line1;
        strCity = resultCompanySetup[0][29]//postal_city;
        strPostalCode = resultCompanySetup[0][32]//postal_zip_code;
        strCountry = resultCompanySetup[0][30]//postal_country;

        String strPostalAddress =   " <postalAddress>\n" +
                getTagNameWithValue("<streetname>",strStreetName,"</streetname>\n","F") +
                getTagNameWithValue("<city>",strCity,"</city>\n","F") +
                getTagNameWithValue("<postalCode>",strPostalCode,"</postalCode>\n","F") +
                getTagNameWithValue("<country>",strCountry,"</country>\n","F") +
                " </postalAddress>\n";

        //Final Company Info
        String strTaxRegistrationCountry = "NL";
        String strContent =" <taxRegIdent>\n" + getTagNameWithValue(" <companyIdent> ",resultCompanyDbName.dbName ,"</companyIdent>\n","F") +
                getTagNameWithValue(" <companyName> ",companyFullName,"</companyName>\n","F") +
                getTagNameWithValue(" <taxRegistrationCountry> ",strTaxRegistrationCountry,"</taxRegistrationCountry>\n","F") +
                " </taxRegIdent>\n" +
                strStreetAddress +
                strPostalAddress;

        return strContent
    }

    def getCustomerSupplierInformation() {

        String customerSupplierContent="";
        def queryCustomer = "SELECT  CONCAT( UPPER('CU'), cm.id) , cm.customer_name, cga.mobile_no, cga.phone_no, cga.fax, cm.email, cga.website_address, cm.cham_of_commerce,co.iso3,\n" +
                "cm.customer_type, cga.address_line1, cga.city, cga.postal_code, cga.state, cpa.postal_address_line1,cpa.postal_address_line2, \n" +
                "cpa.postal_city, cpa.postal_postcode, cpa.postal_state, cba.bank_account_no, cba.iban_prefix   \n" +
                "FROM customer_master AS cm LEFT JOIN customer_general_address AS cga ON cga.customer_id=cm.id " +
                "LEFT JOIN countries AS co ON cga.country_id=co.id \n" +
                "LEFT JOIN customer_postal_address AS cpa ON cpa.customer_id=cm.id " +
                "LEFT JOIN customer_bank_account AS cba ON cba.customer_id=cm.id " +
                "WHERE cm.customer_type='cn' "
                "GROUP BY cm.id"


        def resultCustomer = new BudgetViewDatabaseService().executeQuery(queryCustomer)

        String customerContent = getCustomerSupplierInformationString(resultCustomer);

        def querySupplier="SELECT  CONCAT(UPPER('SU'), vm.id) , vm.vendor_name, vga.mobile_no, vga.phone_no, vga.fax, vm.email, vga.website_address, vm.cham_of_commerce,co.iso3,\n" +
                "vm.vendor_type,CONCAT(vga.address_line1,vga.address_line1), vga.city, vga.postal_code, vga.state, vpa.postal_address_line1,vpa.postal_address_line2, \n" +
                "vpa.postal_city, vpa.postal_postcode, vpa.postal_state, vba.bank_account_no, vba.iban_prefix   \n" +
                "FROM vendor_master AS vm " +
                "LEFT JOIN vendor_general_address AS vga ON vga.vendor_id=vm.id \n" +
                "LEFT JOIN countries AS co ON vga.country_id=co.id \n" +
                "LEFT JOIN vendor_postal_address AS vpa ON vpa.vendor_id=vm.id \n" +
                "LEFT JOIN vendor_bank_account AS vba ON vba.vendor_id=vm.id \n" +
                " where (vm.vendor_type ='vn' or vm.vendor_type ='rp' )" +
                " GROUP BY vm.id"

        def resultSupplier = new BudgetViewDatabaseService().executeQuery(querySupplier)
        String supplierContent = getCustomerSupplierInformationString(resultSupplier);
        customerSupplierContent = customerContent + supplierContent

        return customerSupplierContent;
    }

    def getCustomerSupplierInformationString(def resultCustomer){
        String customerSupplierContent = "";

        resultCustomer.each { resultLine ->

            String streetAddress = getCusSupStreetAddress(resultLine);
            String postalAddress = getCusSupPostalAddress(resultLine);
            String bankAccount = getCusSupBankAccount(resultLine);

            customerSupplierContent =  customerSupplierContent + "\t\t\t<customerSupplier>\n" +
                    "\t\t\t\t"+getTagNameWithValue("<custSupID>",resultLine[0],"</custSupID>\n","F") +
                    "\t\t\t\t"+getTagNameWithValue("<custSupName>",resultLine[1],"</custSupName>\n","F") +
                    "\t\t\t\t"+getTagNameWithValue("<contact>",resultLine[2].toString(),"</contact>\n","F") +
                    "\t\t\t\t"+getTagNameWithValue("<telephone>",resultLine[3].toString(),"</telephone>\n","F") +
                    "\t\t\t\t"+getTagNameWithValue("<fax>",resultLine[4].toString(),"</fax>\n","F") +
                    "\t\t\t\t"+getTagNameWithValue("<eMail>",resultLine[5].toString(),"</eMail>\n","F") +
                    "\t\t\t\t"+getTagNameWithValue("<website>",resultLine[6].toString(),"</website>\n","F") +
                    "\t\t\t\t"+getTagNameWithValue("<commerceNr>",resultLine[7],"</commerceNr>\n","F") +
//                    "\t\t\t\t"+getTagNameWithValue("<taxRegistrationCountry>",resultLine[8],"</taxRegistrationCountry>\n") +
//                    "\t\t\t\t"+getTagNameWithValue("<taxRegIdent>","String","</taxRegIdent>\n") +
//                    "\t\t\t\t"+getTagNameWithValue("<relationshipID>","String","</relationshipID>\n") +
//                    "\t\t\t\t"+getTagNameWithValue("<custSupTp>",resultLine[9],"</custSupTp>\n") +
//                    "\t\t\t\t"+getTagNameWithValue("<custSupGrpID>","String","</custSupGrpID>\n") +
                    streetAddress +
                    postalAddress +
                    bankAccount +
                    "\t\t\t</customerSupplier>\n"
        }

        return customerSupplierContent;

    }

    def getCusSupStreetAddress(def resultLine) {
        String strContent="";
        if(resultLine[10] == null && resultLine[11] == null && resultLine[12] == null && resultLine[13] == null && resultLine[8] == null ){}
        else{
            strContent = "\t\t\t\t<streetAddress>\n" +
                    "\t\t\t\t\t"+getTagNameWithValue("<streetname>",resultLine[10].toString(),"</streetname>\n","F") +
//                "\t\t\t\t\t<number>String</number>\n" +
//                "\t\t\t\t\t<numberExtension>String</numberExtension>\n" +
//                "\t\t\t\t\t<property>String</property>\n" +
                    "\t\t\t\t\t"+getTagNameWithValue("<city>",resultLine[11].toString(),"</city>\n","F") +
                    "\t\t\t\t\t"+getTagNameWithValue("<postalCode>",resultLine[12].toString(),"</postalCode>\n","F") +
                    "\t\t\t\t\t"+getTagNameWithValue("<region>",resultLine[13].toString(),"</region>\n","F") +
                    "\t\t\t\t\t"+getTagNameWithValue("<country>",resultLine[8].toString(),"</country>\n","F") +
                    "\t\t\t\t</streetAddress>\n";
        }


        return strContent;
    }

    def getCusSupPostalAddress(def resultLine) {
        String strContent="";
        if(resultLine[14] == null && resultLine[15] == null && resultLine[16] == null && resultLine[17] == null && resultLine[18] == null && resultLine[8] == null ){}
        else{
            strContent = "\t\t\t\t<postalAddress>\n" +
                    "\t\t\t\t\t"+getTagNameWithValue("<streetname>",resultLine[14].toString(),"</streetname>\n","F") +
                    "\t\t\t\t\t"+getTagNameWithValue("<number>",resultLine[15].toString(),"</number>\n","F") +
//                "\t\t\t\t\t<number>String</number>\n" +
//                "\t\t\t\t\t<numberExtension>String</numberExtension>\n" +
//                "\t\t\t\t\t<property>String</property>\n" +
                    "\t\t\t\t\t"+getTagNameWithValue("<city>",resultLine[16].toString(),"</city>\n","F") +
                    "\t\t\t\t\t"+getTagNameWithValue("<postalCode>",resultLine[17].toString(),"</postalCode>\n","F") +
                    "\t\t\t\t\t"+getTagNameWithValue("<region>",resultLine[18].toString(),"</region>\n","F") +
                    "\t\t\t\t\t"+getTagNameWithValue("<country>",resultLine[8].toString(),"</country>\n","F") +
                    "\t\t\t\t</postalAddress>\n";

        }

        return strContent;
    }

    def getCusSupBankAccount(def resultLine) {
        String strContent='';
        if((resultLine[19]==null && resultLine[20]==null) ||
                (resultLine[19].toString().isEmpty() && resultLine[20].toString().isEmpty())){}
        else{
            String bankAccountNumber=getCustomerSupplierBankAccountNo(resultLine[19].toString(),resultLine[20].toString() )
            strContent = "\t\t\t\t<bankAccount>\n" +
                    "\t\t\t\t\t"+getTagNameWithValue("<bankAccNr>",bankAccountNumber,"</bankAccNr>\n","F") +
                    //                "\t\t\t\t\t<bankIdCd>${resultLine[21]}</bankIdCd>\n" +
                    "\t\t\t\t</bankAccount>\n";
        }


        return strContent;
    }

    def getCustomerSupplierBankAccountNo(String accountNumber,String IBAN ){

        int numberOfZero=10-accountNumber.length();
        String zeros="";
        String strContent="";
        String fAccountNumber="";
        String fIBAN="";

        if(accountNumber=="null"){
            strContent= "null"
        }
        else{
            if(numberOfZero>0 && numberOfZero!=10){
                for(int i=0;i<numberOfZero;i++){
                    zeros=zeros+"0"
                }
            }
            if(accountNumber.trim().length() > 10){
                fAccountNumber=accountNumber.substring(0,10)
            }
            else{
                fAccountNumber = accountNumber
            }

            if(IBAN.trim().length() > 8){
                fIBAN=IBAN.substring(0,8)
            }
            else{
                fIBAN= IBAN
            }
            strContent=fIBAN+zeros+fAccountNumber

        }

        /*int lenghtOfStrContent=strContent.trim().length();
        if(lenghtOfStrContent > 18){
            return strContent.substring(0,18)
        }*/

        return strContent
    }

    def getGeneralLedger() {

        String queryGeneralLedger = "SELECT cm.account_code, REPLACE(cm.account_name, '&', 'and') AS account_name,cct.id AS chartClassTypeId " +
                "FROM chart_master AS cm INNER JOIN chart_group cg ON cg.id = cm.chart_group_id " +
                "LEFT JOIN chart_class AS cc ON cc.id = cg.chart_class_id " +
                "LEFT JOIN chart_class_type AS cct ON cct.id = cc.chart_class_type_id ORDER BY cm.account_code"

        def resultGeneralLedger = new BudgetViewDatabaseService().executeQuery(queryGeneralLedger)
        String XmlGeneralLedger = ''
        resultGeneralLedger.each { resultLedgerLine ->

            if(resultLedgerLine[0]== null && resultLedgerLine[1] == null && strAccType == null ){}
            else{
                String strAccType = "B"
                if(Integer.parseInt(resultLedgerLine[2].toString()) >= 4){
                    strAccType = "P"
                }
                XmlGeneralLedger = XmlGeneralLedger + "\t\t\t<ledgerAccount>\n" +
                        "\t\t\t\t"+getTagNameWithValue("<accID>",resultLedgerLine[0].toString(),"</accID>\n","F") +
                        "\t\t\t\t"+getTagNameWithValue("<accDesc>",resultLedgerLine[1].toString(),"</accDesc>\n","F") +
                        "\t\t\t\t"+getTagNameWithValue("<accTp>",strAccType,"</accTp>\n","F") +
                        "\t\t\t</ledgerAccount>\n"
            }

        }

        return XmlGeneralLedger
    }

    def getTaxonomy() {

        String taxonomyContent = "                <taxonomy>\n" +

                "                    <taxoRef>String</taxoRef>\n" +
                "                    <taxoElement>\n" +
                "                        <txCd>String</txCd>\n" +
                "                        <txClusCd>String</txClusCd>\n" +
                "                        <txClusCtxtID>String</txClusCtxtID>\n" +
                "                        <glAccID>String</glAccID>\n" +
                "                    </taxoElement>\n" +
                "                </taxonomy>\n"

        return taxonomyContent
    }

    def getBasics() {

        String basicContent =   "                <basic>\n" +
                "                    <basicType>02</basicType>\n" +
                "                    <basicID>1</basicID>\n" +
                "                    <basicDesc>String</basicDesc>\n" +
                "                </basic>\n" +
                "                <basic>\n" +
                "                    <basicType>02</basicType>\n" +
                "                    <basicID>2</basicID>\n" +
                "                    <basicDesc>String</basicDesc>\n" +
                "                </basic>\n"

        return basicContent
    }

    def getVatCode() {

        String vatCodesQuery = "SELECT * FROM vat_category AS vc  WHERE vc.status = 1 ORDER BY vc.id ASC"
        def resultVatCodes = new BudgetViewDatabaseService().executeQuery(vatCodesQuery)

        String xmlVatCodes = ''
        resultVatCodes.each { resultLedgerLine ->

            if(resultLedgerLine[0] == null && resultLedgerLine[2] == null &&
                    resultLedgerLine[7] == null && resultLedgerLine[6] == null ){
            }
            else{
                xmlVatCodes = xmlVatCodes + "\t\t\t<vatCode>\n" +
                        "\t\t\t\t"+getTagNameWithValue("<vatID>",resultLedgerLine[0].toString(),"</vatID>\n","F") +
                        "\t\t\t\t"+getTagNameWithValue("<vatDesc>",resultLedgerLine[2].toString(),"</vatDesc>\n","F") +
                        "\t\t\t\t"+getTagNameWithValue("<vatToPayAccID>",resultLedgerLine[7].toString(),"</vatToPayAccID>\n","F") +
                        "\t\t\t\t"+getTagNameWithValue("<vatToClaimAccID>",resultLedgerLine[6].toString(),"</vatToClaimAccID>\n","F") +
                        "\t\t\t</vatCode>\n"
            }
        }
        return xmlVatCodes
    }

    def getPeriodTime(def activeFiscalYear) {

        Date startDateDt = new SimpleDateFormat("yyyy-MM-dd").parse(activeFiscalYear.ST_DT_yy_mm_dd)
        Date endDateDt = new SimpleDateFormat("yyyy-MM-dd").parse(activeFiscalYear.ED_DT_yy_mm_dd)

        Calendar endcalst = Calendar.getInstance()
        endcalst.setTime(startDateDt)
        int yearst = endcalst.get(Calendar.YEAR)
        int monthst = endcalst.get(Calendar.MONTH)

        Calendar calend = Calendar.getInstance()
        calend.setTime(endDateDt)
        int yearend = calend.get(Calendar.YEAR)
        int monthend = calend.get(Calendar.MONTH)

        int stTotalMonth = yearst * 12 + monthst
        int endTotalMonth = yearend * 12 + monthend
        int monthDuration = endTotalMonth - stTotalMonth

        Date dateYrBeg
        Date dateYrEnd
        Calendar calYrBeg = Calendar.getInstance()
        Calendar calYrEnd = Calendar.getInstance()

        String startDatePeriod = ''
        String startTimePeriod = ''
        String endDatePeriod = ''
        String endTimePeriod = ''
        String xmlStr = ''

        int periodNumb = 0
        int y = monthst
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        int nNoOfMonth = 12;
        for (int x = 0; x < nNoOfMonth; x++) {
            periodNumb = periodNumb + 1

            // Create a calendar object and set year and month
            Calendar mycal = new GregorianCalendar(yearst, y, 1);
            // Get the number of days in that month
            int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

            if (y == 11) {
                calYrBeg.set(yearst, y, 01)
                calYrEnd.set(yearst, y , daysInMonth)
                dateYrBeg = calYrBeg.getTime()
                dateYrEnd = calYrEnd.getTime()
                startDatePeriod = "" + sdf.format(dateYrBeg)
                endDatePeriod = "" + sdf.format(dateYrEnd)
                startTimePeriod = "00:00:00"
                endTimePeriod = "23:59:59"
                y = 0
                yearst = yearst + 1
            } else {
                calYrBeg.set(yearst, y, 01)
                calYrEnd.set(yearst, y, daysInMonth)
                dateYrBeg = calYrBeg.getTime()
                dateYrEnd = calYrEnd.getTime()
                startDatePeriod = "" + sdf.format(dateYrBeg)
                endDatePeriod = "" + sdf.format(dateYrEnd)
                startTimePeriod = "00:00:00"
                endTimePeriod = "23:59:59"
            }
//            System.out.print("\n"+y + " Month "+ x);
            y++
            String strMonthString = new DateFormatSymbols().getMonths()[periodNumb-1];
            String periodDesc = "Periode " + periodNumb + "-"+strMonthString
            xmlStr = xmlStr + "\t\t\t<period>\n" +
                    "\t\t\t\t" + getTagNameWithValue("<periodNumber>",periodNumb.toString(),"</periodNumber>\n","F") +
                    "\t\t\t\t" + getTagNameWithValue("<periodDesc>",periodDesc.toString(),"</periodDesc>\n","F") +
                    "\t\t\t\t" + getTagNameWithValue("<startDatePeriod>",startDatePeriod.toString(),"</startDatePeriod>\n","F") +
                    "\t\t\t\t" + getTagNameWithValue("<startTimePeriod>",startTimePeriod.toString(),"</startTimePeriod>\n","F") +
                    "\t\t\t\t" + getTagNameWithValue("<endDatePeriod>",endDatePeriod.toString(),"</endDatePeriod>\n","F") +
                    "\t\t\t\t" + getTagNameWithValue("<endTimePeriod>",endTimePeriod.toString(),"</endTimePeriod>\n","F") +
                    "\t\t\t</period>\n"
        }

        return xmlStr
    }

    def getOpeningBalanceGeneralInfo() {

        String openBalanceGeneralContent =  "            "+getTagNameWithValue("<opBalDate>","2009-01-01","</opBalDate>\n","F")  +
                "            "+getTagNameWithValue("<opBalDesc>","String","</opBalDesc>\n","F")  +
                "            "+getTagNameWithValue("<linesCount>","0","</linesCount>\n","F")  +
                "            "+getTagNameWithValue("<totalDebit>","-0.00","</totalDebit>\n","F")  +
                "            "+getTagNameWithValue("<totalCredit>","-0.00","</totalCredit>\n","F")


        return openBalanceGeneralContent
    }

    def getOpeningBalanceLineInfo() {

        String openBalanceLineContent = "            <obLine>\n" +
                "                "+getTagNameWithValue("<nr>","String","</nr>\n","F")+
                "                "+getTagNameWithValue("<accID>","0","</accID>\n","F")+
                "                "+getTagNameWithValue("<amnt>","0","</amnt>\n","F")+
                "                "+getTagNameWithValue("<amntTp>","C","</amntTp>\n","F")+
                "            </obLine>\n"

        return openBalanceLineContent
    }

    def getOpenBalanceSubLedgers() {

        String obSubledgerGeneralInfo = getOBSubledgerGeneralInfo()
        String obSubLedgerline = getOBSubLedgerLine()

        String opSubledgerContent = "                <obSubledger>\n" +
                obSubledgerGeneralInfo +
                obSubLedgerline +
                "                </obSubledger>\n";

        return opSubledgerContent
    }

    def getOBSubledgerGeneralInfo() {

        String ObSubledgerGeneralContent =  "                    "+getTagNameWithValue("<sbType>","String","</sbType>\n","F")+
                "                    "+getTagNameWithValue("<sbDesc>","String","</sbDesc>\n","F")+
                "                    "+getTagNameWithValue("<linesCount>","0","</linesCount>\n","F")+
                "                    "+getTagNameWithValue("<totalDebit>","0","</totalDebit>\n","F")+
                "                    "+getTagNameWithValue("<totalCredit>","0","</totalCredit>\n","F")



        return ObSubledgerGeneralContent
    }

    def getOBSubLedgerLine() {

        String obSubledgetLineContent = "                    <obSbLine>\n" +
                "                        "+getTagNameWithValue("<nr>","String","</nr>\n","F")+
                "                        "+getTagNameWithValue("<obLineNr>","String","</obLineNr>\n","F")+
                "                        "+getTagNameWithValue("<desc>","String","</desc>\n","F")+
                "                        "+getTagNameWithValue("<amnt>","0","</amnt>\n","F")+
                "                        "+getTagNameWithValue("<amntTp>","String","</amntTp>\n","F")+
                "                        "+getTagNameWithValue("<docRef>","String","</docRef>\n","F")+
//                                        "                        "+getTagNameWithValue("<recRef>","String","</recRef>\n","F")+
                "                        "+getTagNameWithValue("<custSupID>","0","</custSupID>\n","F")+
                "                        "+getTagNameWithValue("<invRef>","String","</invRef>\n","F")+
                "                        "+getTagNameWithValue("<invPurSalTp>","String","</invPurSalTp>\n","F")+
                "                        "+getTagNameWithValue("<invTp>","String","</invTp>\n","F")+
                "                        "+getTagNameWithValue("<invDt>","2009-01-01","</invDt>\n","F")+
                "                        "+getTagNameWithValue("<invDueDt>","2009-01-01","</invDueDt>\n","F")+
                "                        "+getTagNameWithValue("<mutTp>","String","</mutTp>\n","F")+
                "                    </obSbLine>\n"

        return obSubledgetLineContent
    }

    def getTransactionGeneralInfo() {

        String linesCountQuery = "SELECT COUNT(tm.id) AS rowNumber FROM trans_master AS tm"
        def resultLinesCount = new BudgetViewDatabaseService().executeQuery(linesCountQuery)

        String debitAmountQuery = "SELECT ROUND(SUM(tm.amount),2) AS totalDebitAmount FROM trans_master AS tm WHERE tm.amount > 0.0"
        def resultDebitAmount = new BudgetViewDatabaseService().executeQuery(debitAmountQuery)

        String creditAmountQuery = "SELECT ROUND(SUM(tm.amount)*-1,2) AS totalCreditAmount FROM trans_master AS tm WHERE tm.amount < 0.0"
        def resultCreditAmount = new BudgetViewDatabaseService().executeQuery(creditAmountQuery)

//        BigDecimal totalDebit= new BigDecimal(resultDebitAmount[0][0])
//        String FtotalDebit=totalDebit.setScale(2, BigDecimal.ROUND_HALF_UP).toString();


        String strContent ="            "+getTagNameWithValue("<linesCount>",resultLinesCount[0][0].toString(),"</linesCount>\n","F")+
                "            "+getTagNameWithValue("<totalDebit>",getTwoDecimalNumber(resultDebitAmount[0][0]).toString(),"</totalDebit>\n","F")+
                "            "+getTagNameWithValue("<totalCredit>",getTwoDecimalNumber(resultCreditAmount[0][0]).toString(),"</totalCredit>\n","F")



        return strContent;
    }

    def String getFormattedDate(value){

        String dateString = "";
        String year ='20' + value.substring(0,2);
        String month = value.substring(2,4);
        String date = value.substring(4,6);
        dateString = year + "-" + month + "-" + date;

        return dateString;
    }

    def getJournalDetails(def resultJournalTrans){
        String jrnID = ''
        String jrnIDNew = ''
        String strTrnsNoCur = "0"

        int rowLength = 0
        int rowCount = 0
        int nTrLineNo = 1;

        boolean bGeneralInfoLoaded = false;
        boolean bTransHeadLoaded = false;
        String strContent = "\t\t\t<journal>\n";

        resultJournalTrans.each { resultJournalRow ->

            String strTransType = resultJournalRow[0];
            String strJrnType = resultJournalRow[2];
            String strInvoiceNo = resultJournalRow[5];
            String strInvRef = resultJournalRow[6];
            String strBookPeriod = resultJournalRow[9];
            String strSourceID = resultJournalRow[8];
            String strTransDate = "" + resultJournalRow[7]
            String strDesc = resultJournalRow[13]
            if(strTransDate.length() > 10){
                strTransDate = strTransDate.substring(0, 10)
            }

            int nChartClassTypeId = Integer.parseInt(resultJournalRow[22].toString());
            String strLineNote = resultJournalRow[23].toString();
            String strOffsetAccId = resultJournalRow[3].toString()

            strJrnType = strJrnType.toUpperCase();
            if(!bGeneralInfoLoaded){
                bGeneralInfoLoaded = true;

                strContent = strContent +
                        "\t\t\t\t"+getTagNameWithValue("<jrnID>",resultJournalRow[0].toString(),"</jrnID>\n","F") +
                        "\t\t\t\t"+getTagNameWithValue("<desc>",resultJournalRow[1].toString(),"</desc>\n","F") +
                        "\t\t\t\t"+getTagNameWithValue("<jrnTp>",strJrnType.toString(),"</jrnTp>\n","F") +
                        "\t\t\t\t"+getTagNameWithValue("<offsetAccID>",strOffsetAccId,"</offsetAccID>\n","F")

                if(strTransType == "7"){
                    String strBankAccountNo = resultJournalRow[21].toString();
                    strContent = strContent +
                            "\t\t\t\t"+getTagNameWithValue("<bankAccNr>",strBankAccountNo,"</bankAccNr>\n","F")
                }
            }

            if(strTrnsNoCur != strInvoiceNo){
                nTrLineNo = 1;

                String strBookYr = resultJournalRow[4]
                int nLen = strBookYr.length();
                if(nLen >= 4){
                    strBookYr = strBookYr.substring(2,4)
                }

                if(strTrnsNoCur == "0"){
                }
                else{
                    strContent = strContent + "\t\t\t\t</transaction>\n";
                }
                strTrnsNoCur = strInvoiceNo;

                String strTransNo = strBookYr + "0" + strTransType + "000" + strInvoiceNo
                strContent = strContent +
                        "\t\t\t\t<transaction>\n" +
                        "\t\t\t\t\t"+getTagNameWithValue("<nr>",strTransNo.toString(),"</nr>\n","F")+
                        "\t\t\t\t\t"+getTagNameWithValue("<desc>",strDesc.toString(),"</desc>\n","F")+
                        "\t\t\t\t\t"+getTagNameWithValue("<periodNumber>",strBookPeriod.toString(),"</periodNumber>\n","F")+
                        "\t\t\t\t\t"+getTagNameWithValue("<trDt>",strTransDate.toString(),"</trDt>\n","F")+
                        "\t\t\t\t\t"+getTagNameWithValue("<sourceID>",strSourceID.toString(),"</sourceID>\n","F")

            }


            String strAccId = resultJournalRow[10].toString();
            String strDocRef = resultJournalRow[11];
            String strEffDate = resultJournalRow[12]
            if(strTransType.toString() == "7") {
                strEffDate = getFormattedDate(strEffDate);
            }

            Double fAmount = resultJournalRow[14]

            if(fAmount < 0.0) fAmount = fAmount * (-1);
            String strAmtType = resultJournalRow[15]
            strAmtType = strAmtType.toUpperCase();
            String strCusSupId = resultJournalRow[16]

            if(strEffDate.length() > 10){
                strEffDate = strEffDate.substring(0, 10)
            }

            String strVatId = resultJournalRow[17];
            String strVatRate = resultJournalRow[18];
            Double fVatAmount = resultJournalRow[19];
            String strVatAmntTp = resultJournalRow[20]
            strVatAmntTp = strVatAmntTp.toString().toUpperCase();

//            BigDecimal amnt= new BigDecimal(fAmount)
//            String famnt=amnt.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            if(strAccId !=  strOffsetAccId){
                String strTrLineNo = "" + nTrLineNo++

                strContent = strContent +
                        "\t\t\t\t\t<trLine>\n" +
                        "\t\t\t\t\t\t" + getTagNameWithValue("<nr>", strTrLineNo.toString(), "</nr>\n", "F") +
                        "\t\t\t\t\t\t" + getTagNameWithValue("<accID>", strAccId, "</accID>\n", "F") +
                        "\t\t\t\t\t\t" + getTagNameWithValue("<docRef>", strDocRef.toString(), "</docRef>\n", "F") +
                        "\t\t\t\t\t\t" + getTagNameWithValue("<effDate>", strEffDate.toString(), "</effDate>\n", "F") +
                        "\t\t\t\t\t\t" + getTagNameWithValue("<desc>", strLineNote.toString(), "</desc>\n", "F") +
                        "\t\t\t\t\t\t" + getTagNameWithValue("<amnt>", getTwoDecimalNumber(fAmount).toString(), "</amnt>\n", "F") +
                        "\t\t\t\t\t\t" + getTagNameWithValue("<amntTp>", strAmtType.toString(), "</amntTp>\n", "F") +
                        "\t\t\t\t\t\t" + getTagNameWithValue("<custSupID>", strCusSupId.toString(), "</custSupID>\n", "F") +
                        "\t\t\t\t\t\t" + getTagNameWithValue("<invRef>", strInvRef.toString(), "</invRef>\n", "F")
    //                    "\t\t\t\t\t\t\t<currency>\n" +
    //                    "\t\t\t\t\t\t\t\t<curCode>EUR</curCode>\n" +
    //                    "\t\t\t\t\t\t\t\t<curAmnt>-0.00</curAmnt>\n" +
    //                    "\t\t\t\t\t\t\t</currency>\n" +

                if((nChartClassTypeId > 3) && (strTransType.toString() == "1" || strTransType.toString() == "2"|| strTransType.toString() == "4")) {
    //                BigDecimal fVatAmountDec= new BigDecimal(fVatAmount)
    //                String finalVatAmountDec=fVatAmountDec.setScale(2, BigDecimal.ROUND_HALF_UP).toString();

                    strContent = strContent +
                            "\t\t\t\t\t\t\t<vat>\n" +
                            "\t\t\t\t\t\t\t\t" + getTagNameWithValue("<vatID>", strVatId.toString(), "</vatID>\n", "F") +
                            "\t\t\t\t\t\t\t\t" + getTagNameWithValue("<vatPerc>", getTwoDecimalNumber(strVatRate).toString(), "</vatPerc>\n", "F") +
                            "\t\t\t\t\t\t\t\t" + getTagNameWithValue("<vatAmnt>", getTwoDecimalNumber(fVatAmount).toString(), "</vatAmnt>\n", "F") +
                            "\t\t\t\t\t\t\t\t" + getTagNameWithValue("<vatAmntTp>", strVatAmntTp.toString(), "</vatAmntTp>\n", "F") +
                            "\t\t\t\t\t\t\t</vat>\n"
                }

                strContent = strContent + "\t\t\t\t\t</trLine>\n"
            }
        }

        //End Tag
        strContent = strContent + "\t\t\t\t</transaction>\n";
        strContent = strContent + "\t\t\t</journal>\n";

        return strContent;
    }

    def getJournalInfo() {
        //Income
        String journalQuery =   "SELECT tm.trans_type,'Verkoopboek' AS typeDesc,'S' AS jrnType,(SELECT debitor_gl_code from debit_credit_gl_setup) AS offsetAccID," +
                "tm.booking_year AS bookYear,tm.invoice_no AS invNo," +
                "CONCAT(tm.invoice_no,'#1') AS invRef,tm.trans_date,tm.process,tm.booking_period," +
                "tm.account_code AS accId,CONCAT(tm.invoice_no,'#1') AS docRef,ii.trans_date AS effDate,ii.payment_ref AS invDesc,tm.amount AS trAmt," +
                "CASE WHEN tm.amount >= 0.0 THEN UPPER('D') WHEN tm.amount < 0.0 THEN UPPER('C') END as amntTp," +
                "CONCAT(Upper('CU'),ii.customer_id) AS custSupID, " +
                "iid.vat_category_id,iid.vat_rate,ROUND((iid.total_amount_with_vat - iid.total_amount_without_vat),2) AS vatAmt,"+
                "CASE WHEN ii.total_vat >= 0.0 THEN UPPER('C') WHEN ii.total_vat < 0.0 THEN UPPER('D') END as vatTp," +
                "'000' AS byBankAccountNo,cc.chart_class_type_id AS classTypeId,REPLACE(iid.note, '&', 'and') AS lineNote " +
                "FROM trans_master AS tm " +
                "INNER JOIN invoice_income AS ii ON tm.invoice_no = ii.id " +
                "INNER JOIN invoice_income_details AS iid ON iid.invoice_id = ii.id " +
//                "LEFT JOIN debit_credit_gl_setup AS dcgs ON dcgs.debitor_gl_code = tm.account_code " +
                "LEFT JOIN chart_master AS cm ON cm.account_code = tm.account_code " +
                "LEFT JOIN chart_group AS cg ON cg.id = cm.chart_group_id " +
                "LEFT JOIN chart_class AS cc ON cc.id = cg.chart_class_id " +
                "WHERE tm.trans_type = 1 " +
                "AND tm.booking_year =(SELECT YEAR(fiscal_year.year_end) from fiscal_year where status=1) \n" +
                " group by ii.id " +
                "ORDER BY tm.invoice_no";

        def resultJournalTrans = new BudgetViewDatabaseService().executeQuery(journalQuery)
        String strIncomeTrans = ""
        if(resultJournalTrans.size() > 0){
            strIncomeTrans = getJournalDetails(resultJournalTrans)
        }

        //Expense
        journalQuery =  "SELECT tm.trans_type,'Inkoopboek' AS typeDesc,'P' AS jrnType,(SELECT creditor_gl_code from debit_credit_gl_setup) AS offsetAccID," +
                "tm.booking_year AS bookYear,tm.invoice_no AS invNo," +
                "CONCAT(tm.invoice_no,'#2') AS invRef,tm.trans_date,tm.process,tm.booking_period," +
                "tm.account_code AS accId,CONCAT(tm.invoice_no,'#2') AS docRef,ie.trans_date AS effDate,ie.payment_ref AS invDesc,tm.amount AS trAmt," +
                "CASE WHEN tm.amount >= 0.0 THEN UPPER('D') WHEN tm.amount < 0.0 THEN UPPER('C') END as amntTp," +
                "CONCAT(Upper('SU'),ie.vendor_id) AS custSupID, " +
                "ied.vat_category_id,ied.vat_rate,ROUND((ied.total_amount_with_vat - ied.total_amount_without_vat),2) AS vatAmt,"+
                "CASE WHEN ie.total_vat >= 0.0 THEN UPPER('D') WHEN ie.total_vat < 0.0 THEN UPPER('C') END as vatTp," +
                "'000' AS byBankAccountNo,cc.chart_class_type_id AS classTypeId,REPLACE(ied.note, '&', 'and') AS lineNote " +
                "FROM trans_master AS tm " +
                "INNER JOIN invoice_expense AS ie ON tm.invoice_no = ie.id " +
                "INNER JOIN invoice_expense_details AS ied ON ied.invoice_id = ie.id " +
                "LEFT JOIN chart_master AS cm ON cm.account_code=tm.account_code " +
                "LEFT JOIN chart_group AS cg ON cg.id = cm.chart_group_id " +
                "LEFT JOIN chart_class AS cc ON cc.id = cg.chart_class_id " +
                "WHERE tm.trans_type = 2 " +
                "AND tm.booking_year =(SELECT YEAR(fiscal_year.year_end) from fiscal_year where status=1) \n" +
                " group by ie.id " +
                "ORDER BY tm.invoice_no"

        resultJournalTrans = new BudgetViewDatabaseService().executeQuery(journalQuery)
        String strExpenseTrans = ""
        if(resultJournalTrans.size() > 0){
            strExpenseTrans = getJournalDetails(resultJournalTrans)
        }

        //Journal
        journalQuery =  "SELECT tm.trans_type,'Memoriaalboek' AS typeDesc,'M' AS jrnType,'' AS offsetAccID, " +
                "tm.booking_year AS bookYear,tm.invoice_no AS invNo, " +
                " CONCAT(je.id,'#3') AS invRef,tm.trans_date,tm.process,tm.booking_period, " +
                "tm.account_code AS accId,CONCAT(je.id,'#3') AS docRef,je.trans_date AS effDate,REPLACE(je.comments, '&', 'and') AS trDesc,tm.amount, " +
                "CASE WHEN tm.amount >= 0.0 THEN UPPER('D') WHEN tm.amount < 0.0 THEN UPPER('C') END as amntTp,'' AS custSupID, " +
                "0 as vatCategoryId,0 AS vatRate,0 AS vatAmt," +
                "UPPER('D') AS vatTp,'000' AS byBankAccountNo,0 AS classTypeId,REPLACE(jed.note, '&', 'and') AS lineNote " +
                "FROM trans_master AS tm  " +
                "INNER JOIN journal_entry AS je ON tm.invoice_no = je.id " +
                "INNER JOIN journal_entry_details AS jed ON jed.journal_entry_id = je.id " +
                "WHERE tm.trans_type = 3 " +
                "AND tm.booking_year =(SELECT YEAR(fiscal_year.year_end) from fiscal_year where status=1) \n" +
                " group by je.id " +
                "ORDER BY tm.invoice_no;"

        resultJournalTrans = new BudgetViewDatabaseService().executeQuery(journalQuery)
        String strJounrnalTrans = ""
        if(resultJournalTrans.size() > 0){
            strJounrnalTrans = getJournalDetails(resultJournalTrans)
        }

        //Receipt
        journalQuery =  "SELECT tm.trans_type,'Inkoopboek' AS typeDesc,'P' AS jrnType,(SELECT creditor_gl_code from debit_credit_gl_setup) AS offsetAccID," +
                "tm.booking_year AS bookYear,tm.invoice_no AS invNo," +
                "CONCAT(tm.invoice_no,'#4') AS invRef,tm.trans_date,tm.process,tm.booking_period," +
                "tm.account_code AS accId,CONCAT(tm.invoice_no,'#4') AS docRef,ie.trans_date AS effDate,ie.payment_ref AS invDesc,tm.amount AS trAmt," +
                "CASE WHEN tm.amount >= 0.0 THEN UPPER('D') WHEN tm.amount < 0.0 THEN UPPER('C') END as amntTp," +
                "CONCAT(Upper('SU'),ie.vendor_id) AS custSupID, " +
                "ied.vat_category_id,ied.vat_rate,ROUND((ied.total_amount_with_vat - ied.total_amount_without_vat),2) AS vatAmt,"+
                "CASE WHEN ie.total_vat >= 0.0 THEN UPPER('D') WHEN ie.total_vat < 0.0 THEN UPPER('C') END as vatTp," +
                "'000' AS byBankAccountNo,cc.chart_class_type_id AS classTypeId,REPLACE(ied.note, '&', 'and') AS lineNote " +
                "FROM trans_master AS tm " +
                "INNER JOIN invoice_expense AS ie ON tm.invoice_no = ie.id " +
                "INNER JOIN invoice_expense_details AS ied ON ied.invoice_id = ie.id " +
                "LEFT JOIN chart_master AS cm ON cm.account_code=tm.account_code " +
                "LEFT JOIN chart_group AS cg ON cg.id = cm.chart_group_id " +
                "LEFT JOIN chart_class AS cc ON cc.id = cg.chart_class_id " +
                "WHERE tm.trans_type = 4 " +
                "AND tm.booking_year =(SELECT YEAR(fiscal_year.year_end) from fiscal_year where status=1) \n" +
                " group by ie.id " +
                "ORDER BY tm.invoice_no"

        resultJournalTrans = new BudgetViewDatabaseService().executeQuery(journalQuery)
        String strReceiptTrans = ""
        if(resultJournalTrans.size() > 0){
            strReceiptTrans = getJournalDetails(resultJournalTrans)
        }

        //Bank Trans
        journalQuery =  "SELECT tm.trans_type,'Bank' AS typeDesc,'B' AS jrnType,cbgr.gl_chart_code AS offsetAccID," +
                "tm.booking_year AS bookYear,tm.invoice_no AS invNo, " +
                "CASE WHEN bsidf.reconcilated = 0 THEN '' WHEN bsidf.reconcilated = 1 THEN tm.recenciliation_code END AS invRef," +
                "tm.trans_date,tm.process,tm.booking_period, " +
                "tm.account_code AS accId,CONCAT(tm.invoice_no,'#',tm.trans_type) AS docRef,bsidf.trans_date_time AS effDate," +
                "'' AS invDesc,tm.amount, " +
                "CASE WHEN tm.amount >= 0.0 THEN UPPER('D') WHEN tm.amount < 0.0 THEN UPPER('C') END as amntTp," +
                "CASE WHEN substring_index(tm.recenciliation_code,'#',-1) = '1' THEN " +
                "CONCAT(UPPER('CU'),(select ii.customer_id from invoice_income as ii where CONCAT(ii.id,'') = substring_index(tm.recenciliation_code,'#',1))) WHEN substring_index(tm.recenciliation_code,'#',-1) = '2' " +
                "THEN CONCAT(UPPER('SU'),(select ie.vendor_id from invoice_expense as ie where CONCAT(ie.id,'') = substring_index(tm.recenciliation_code,'#',1))) END as custSupID, " +
                "0 as vatCategoryId,0 AS vatRate,0 AS vatAmt, " +
                "UPPER('D') AS vatTp,bsidf.trans_bank_account_no AS byBankAccountNo,0 AS classTypeId," +
                "REPLACE(bsidf.description, '&', 'and') AS lineNote " +
                "FROM trans_master AS tm  " +
                "INNER JOIN bank_statement_import_details_final AS bsidf ON tm.invoice_no = bsidf.id " +
                "INNER JOIN bank_statement_import_final AS bsif ON bsif.id = bsidf.bank_import_id " +
                "LEFT JOIN company_bank_gl_relation AS cbgr ON cbgr.gl_chart_code = tm.account_code " +
                "WHERE tm.trans_type = 7 " +
                "AND tm.booking_year =(SELECT YEAR(fiscal_year.year_end) from fiscal_year where status=1) \n" +
                "ORDER BY tm.invoice_no";

        resultJournalTrans = new BudgetViewDatabaseService().executeQuery(journalQuery)
        String strBankTrans = ""
        if(resultJournalTrans.size() > 0){
            strBankTrans = getJournalDetails(resultJournalTrans)
        }

        String strContent = strIncomeTrans + strExpenseTrans + strJounrnalTrans + strReceiptTrans + strBankTrans;
        return strContent
    }

    def getSubLedger() {
        String strContent
        def queryCUSubledger = "SELECT   UPPER('CU') as sbType,'Accounts Receivable' as sbDesc, COUNT(tm.id), ROUND(SUM(tm.amount),2) AS totalDebitAmount,'0' as totalCreditAmount \n" +
                "FROM trans_master AS tm \n" +
                "WHERE tm.account_code= (SELECT debitor_gl_code from debit_credit_gl_setup)" +
                "and tm.booking_year =(SELECT YEAR(fiscal_year.year_end) from fiscal_year where status=1) ;"

        def resultCUSubledger = new BudgetViewDatabaseService().executeQuery(queryCUSubledger)

        def queryCUsubLedgerLine = "SELECT\n" +
                "tm.trans_type,\n" +
                "tm.recenciliation_code,tm.invoice_no,\n" +
                "CASE tm.trans_type\n" +
                "WHEN  3 THEN jo.comments\n" +
                "WHEN  1 THEN inv.payment_ref\n" +
                "WHEN  2 THEN invex.payment_ref \n" +
                "WHEN  4 THEN invex.payment_ref\n" +
                "WHEN  7 THEN REPLACE(bank.description,'&','and')\n" +
                "END  as descrip,\n" +
                "\n" +
                "IF(tm.amount < 0,tm.amount*(-1),tm.amount)as amnt,\n" +
                "IF(tm.amount < 0,UPPER('C'),UPPER('D'))as amntTp,\n" +
                "CONCAT(UPPER('CU'),inv.customer_id) AS custSupID,UPPER('S') AS invPurSalTp,\n" +
                "tm.trans_date ,\n" +
                "\n" +
                "CASE tm.trans_type\n" +
                "WHEN  3 THEN UPPER('Z')\n" +
                "WHEN  1 THEN UPPER('I')\n" +
                "WHEN  2 THEN UPPER('I')\n" +
                "WHEN  4 THEN UPPER('I')\n" +
                "WHEN  7 THEN UPPER('P')\n" +
                "END  as mutTp,\n" +
                "inv.payment_ref as invRef,\n" +
                "\n" +
                "IF(inv.total_gl_amount < 0,UPPER('C'),UPPER('D')) AS invTp,\n" +
                "inv.due_date AS invDueDt," +
                "CONCAT(CONCAT(tm.invoice_no,'#'), tm.trans_type) as trNr " +
                "FROM trans_master AS tm\n" +
                "\n" +
                "LEFT JOIN journal_entry AS jo ON tm.invoice_no =jo.id\n" +
                "LEFT JOIN invoice_income AS inv ON tm.invoice_no =inv.id\n" +
                "LEFT JOIN invoice_expense AS invex ON tm.invoice_no =invex.id\n" +
                "LEFT JOIN bank_statement_import_details_final AS bank ON tm.invoice_no =bank.id\n" +
                "\n" +
                "WHERE\n" +
                "tm.account_code = (SELECT debitor_gl_code from debit_credit_gl_setup)" +
                "and tm.booking_year =(SELECT YEAR(fiscal_year.year_end) from fiscal_year where status=1) \n"

        def resultCUsubLedgerLine = new BudgetViewDatabaseService().executeQuery(queryCUsubLedgerLine)

        def querySUSubledger = "SELECT  UPPER('SU') as sbType, 'Accounts Payable'as sbDesc, COUNT(tm.id), ROUND(SUM(tm.amount),2) AS  totalCreditAmount\n" +
                "FROM trans_master AS tm \n" +
                "WHERE tm.account_code= (SELECT creditor_gl_code from debit_credit_gl_setup);"

        def resultSUSubledger = new BudgetViewDatabaseService().executeQuery(querySUSubledger)

        def querySUsubLedgerLine = "SELECT\n" +
                "tm.trans_type,\n" +
                "tm.recenciliation_code,tm.invoice_no,\n" +
                "CASE tm.trans_type\n" +
                "WHEN  3 THEN jo.comments\n" +
                "WHEN  1 THEN inv.payment_ref\n" +
                "WHEN  2 THEN invex.payment_ref \n" +
                "WHEN  4 THEN invex.payment_ref\n" +
                "WHEN  7 THEN REPLACE(bank.description,'&','and')\n" +
                "END  as descrip,\n" +
                "IF(tm.amount < 0,tm.amount*(-1),tm.amount)as amnt,\n" +
                "IF(tm.amount < 0,UPPER('C'),UPPER('D'))as amntTp,\n" +
                "CONCAT(UPPER('SU'),invex.vendor_id) AS custSupID,UPPER('P') AS invPurSalTp,\n" +
                "tm.trans_date ,\n" +
                "\n" +
                "CASE tm.trans_type\n" +
                "WHEN  3 THEN UPPER('Z')\n" +
                "WHEN  1 THEN UPPER('I')\n" +
                "WHEN  2 THEN UPPER('I')\n" +
                "WHEN  4 THEN UPPER('I')\n" +
                "WHEN  7 THEN UPPER('P')\n" +
                "END  as mutTp,\n" +
                "invex.payment_ref as invRef,\n" +
                "\n" +
                "IF(invex.total_gl_amount < 0,UPPER('C'),UPPER('D')) AS invTp,\n" +
                "invex.due_date AS invDueDt ," +
                "CONCAT(CONCAT(tm.invoice_no,'#'), tm.trans_type) as trNr " +
                "FROM trans_master AS tm\n" +
                "\n" +
                "LEFT JOIN journal_entry AS jo ON tm.invoice_no =jo.id\n" +
                "LEFT JOIN invoice_income AS inv ON tm.invoice_no =inv.id\n" +
                "LEFT JOIN invoice_expense AS invex ON tm.invoice_no =invex.id\n" +
                "LEFT JOIN bank_statement_import_details_final AS bank ON tm.invoice_no =bank.id\n" +
                "\n" +
                "WHERE\n" +
                "tm.account_code = (SELECT creditor_gl_code from debit_credit_gl_setup)" +
                "and tm.booking_year =(SELECT YEAR(fiscal_year.year_end) from fiscal_year where status=1)  "


        def resultSUsubLedgerLine = new BudgetViewDatabaseService().executeQuery(querySUsubLedgerLine)

        String subLedgerGeneralInformationCU = getCUSubLedgerGeneralInfo(resultCUSubledger,resultSUSubledger)
        String subLedgerLineCU = getSubLedgerLineInfo(resultCUsubLedgerLine)
        String strContentCU=getSubLedgerString(subLedgerGeneralInformationCU, subLedgerLineCU)

        String subLedgerGeneralInformationSU = getSUSubLedgerGeneralInfo(resultSUSubledger,resultCUSubledger)
        String subLedgerLineSU = getSubLedgerLineInfo(resultSUsubLedgerLine)
        String strContentSU=getSubLedgerString(subLedgerGeneralInformationSU, subLedgerLineSU)
        strContent=strContentCU+strContentSU

        return strContent;
    }

    def  getSubLedgerString(String subLedgerGeneralInformation, String subLedgerLine) {

        String subLedgerContent =   "          <subledger>\n" +
                subLedgerGeneralInformation +
                subLedgerLine +
                "           </subledger>\n"

        return subLedgerContent;

    }

    def getCUSubLedgerGeneralInfo(def resultCUSubledger,def resultSUSubledger) {

//        BigDecimal totalDebit= new BigDecimal(resultCUSubledger[0][3])
//        String FtotalDebit=totalDebit.setScale(2, BigDecimal.ROUND_HALF_UP).toString();

        String subLedgerGeneralContent =
                "                    "+getTagNameWithValue("<sbType>",resultCUSubledger[0][0].toString(),"</sbType>\n","F")+
                        "                    "+getTagNameWithValue("<sbDesc>",resultCUSubledger[0][1].toString().capitalize(),"</sbDesc>\n","F")+
                        "                    "+getTagNameWithValue("<linesCount>",resultCUSubledger[0][2].toString(),"</linesCount>\n","F")+
                        "                    "+getTagNameWithValue("<totalDebit>",getTwoDecimalNumber(resultCUSubledger[0][3]).toString(),"</totalDebit>\n","F")+
                        "                    "+getTagNameWithValue("<totalCredit>",getTwoDecimalNumber(resultSUSubledger[0][3]).toString(),"</totalCredit>\n","F")

        return subLedgerGeneralContent
    }

    def getSUSubLedgerGeneralInfo(def resultSUSubledger,def resultCUSubledger) {

        String subLedgerGeneralContent =
                "                    "+getTagNameWithValue("<sbType>",resultSUSubledger[0][0].toString(),"</sbType>\n","F")+
                        "                    "+getTagNameWithValue("<sbDesc>",resultSUSubledger[0][1].toString().capitalize(),"</sbDesc>\n","F")+
                        "                    "+getTagNameWithValue("<linesCount>",resultSUSubledger[0][2].toString(),"</linesCount>\n","F")+
                        "                    "+getTagNameWithValue("<totalDebit>",getTwoDecimalNumber(resultCUSubledger[0][3]).toString(),"</totalDebit>\n","F")+
                        "                    "+getTagNameWithValue("<totalCredit>",getTwoDecimalNumber(resultSUSubledger[0][3]).toString(),"</totalCredit>\n","F")

        return subLedgerGeneralContent
    }

    def getSubLedgerGeneralInfo() {

        String subLedgerGeneralContent = "                    <sbType>CS</sbType>\n" +
                "                    <sbDesc>String</sbDesc>\n" +
                "                    <linesCount>1</linesCount>\n" +
                "                    <totalDebit>-0.00</totalDebit>\n" +
                "                    <totalCredit>-0.00</totalCredit>\n"
        return subLedgerGeneralContent

    }

    def getSubLedgerLineInfo(def resultSubLedgerLine) {
        String subLedgerLineContent='';
        int i=1;
        String strTrLineNr = "1:1";
        int nTrLineNrCounter = 1;
        int nTempInvoiceNo = 0;
        resultSubLedgerLine.each { resultLine ->
            String strInvDueDt="";
            String strInvDt="";
            if(resultLine[12].toString().length()>=10){
                strInvDueDt = resultLine[12].toString().substring(0,10)
            }

            if(resultLine[8].toString().length()>=10){
                strInvDt = resultLine[8].toString().substring(0,10)
            }




            int nInvoiceNo = Integer.parseInt(resultLine[2].toString());

            if(nInvoiceNo == nTempInvoiceNo)
            {
                nTrLineNrCounter++;
                strTrLineNr = nTrLineNrCounter + ":1";
            }
            else{
                nTrLineNrCounter = 1;
                strTrLineNr = "1:1";
            }
//            BigDecimal amnt= new BigDecimal(resultLine[4])
//            String famnt=amnt.setScale(2, BigDecimal.ROUND_HALF_UP).toString();

            subLedgerLineContent =subLedgerLineContent+ "        <sbLine>\n" +
                    "                        "+getTagNameWithValue("<nr>",i.toString(),"</nr>\n","F")+
                    "                        "+getTagNameWithValue("<jrnID>",resultLine[0].toString(),"</jrnID>\n","F")+
                    "                        "+getTagNameWithValue("<trNr>",resultLine[13].toString(),"</trNr>\n","F")+
                    "                        "+getTagNameWithValue("<trLineNr>",strTrLineNr,"</trLineNr>\n","F")+
                    "                        "+getTagNameWithValue("<desc>",resultLine[3].toString(),"</desc>\n","F")+
                    "                        "+getTagNameWithValue("<amnt>",getTwoDecimalNumber(resultLine[4]).toString(),"</amnt>\n","F")+
                    "                        "+getTagNameWithValue("<amntTp>",resultLine[5].toString(),"</amntTp>\n","F")+
                    "                        "+getTagNameWithValue("<docRef>",resultLine[1].toString(),"</docRef>\n","F")+
                    "                        "+getTagNameWithValue("<custSupID>",resultLine[6].toString(),"</custSupID>\n","F")+
                    "                        "+getTagNameWithValue("<invRef>",resultLine[1].toString(),"</invRef>\n","F")+
                    "                        "+getTagNameWithValue("<invPurSalTp>",resultLine[7].toString(),"</invPurSalTp>\n","F")+
                    "                        "+getTagNameWithValue("<invTp>",resultLine[11].toString(),"</invTp>\n","F")+
                    "                        "+getTagNameWithValue("<invDt>",strInvDt,"</invDt>\n","F")+
                    "                        "+getTagNameWithValue("<invDueDt>",strInvDueDt,"</invDueDt>\n","F")+
                    "                        "+getTagNameWithValue("<mutTp>",resultLine[9].toString(),"</mutTp>\n","F")+
                    "      </sbLine>\n"

            i++;

            nTempInvoiceNo = nInvoiceNo;
        }

        return subLedgerLineContent
    }

    def getTwoDecimalNumber(value){
        if(value != null){
            BigDecimal decimaleValue= new BigDecimal(value)
            def finalValue=decimaleValue.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            return finalValue;
        }


    }

}
