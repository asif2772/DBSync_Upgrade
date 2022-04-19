import com.bv.util.GridEntity
import groovy.sql.GroovyRowResult

class test {
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
