package bv

class ReceiptEntryDetails {
    Long    receiptEntryId;
    String  accountCode; //acount head code
    Double  amount;
    String  currCode;
    Long    vatCategoryId;
    Integer shopId;     //vendorId
    Double  vatRate;
    Double  vatAmount;
    String  note;

    static constraints = {
        note(nullable: true)
        vatAmount(scale: 2)
    }
}
