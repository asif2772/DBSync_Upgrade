/**
 * Created by Abdullah on 4/27/2016.
 */
function startTimer(){
    setTimeout(function () {$("#msgdivAnother").hide('blind', {}, 1000);
        //alert("setTimeout");
    }, 5000);
}

function saveDataToSession(){
    var customerId = $('#customerId').val();
    var paymentRef = $('#paymentRef').val();
    var editId = '${params.editId}';
    //alert("customerId " + customerId +" PaymentRef " + paymentRef);

    $.ajax({type:'POST',
        data:'customerId=' + customerId +'&paymentRef=' + paymentRef +'&editId=' + editId,
        url:'${contextPath}/invoiceIncome/saveDataToSession',
        success:function(data,textStatus){
            if(data != ""){
                $(msgdivAnother).show();
                $(msgdivAnother).html(data);
                startTimer();
                document.getElementById("paymentRef").value = "";
            }
            //alert("data " + data + "status "+ textStatus);
        },
        error: function(XMLHttpRequest,textStatus,errorThrown) {
            //alert("textStatus "+textStatus);
        }
    });
}

function changeBooking(incomeInvoiceId,customerId,debtorId){
    var redirectUrl="list?editId="+incomeInvoiceId+"&customerId="+customerId+"&debtorId="+debtorId;
    window.location.replace(redirectUrl);
}

function generateDebtorDropList(){

    $.ajax({
        type:'POST',
        dataType: 'json',
        url:'${contextPath}/invoiceIncome/generateDebtorNameList',
        data:{
            customerId: $('#customerId').val(),
            debtorId:'${params.debtorId}'
        },
        success: function(ajax)
        {
            $('#debtorList').html(ajax.result);
        }
    });
}

function selectVATDropDown(){
    $.ajax({
        type:'POST',
        dataType: 'json',
        url:'${contextPath}/invoiceIncome/selectVATFromCustomerId',
        data:{
            customerId: $('#customerId').val()
        },
        success: function(data)
        {
            var vatRate = parseFloat(data);
            var varRateFormat = number_format(vatRate, 1, '.', '${session.companyInfo?.thousandSeperator[0]}');
            //alert(varRateFormat);
            $("#vatRate option[value='" + varRateFormat + "']").attr('selected', 'selected');

        }
    });
}

function selectDebtorTerms(){
    $.ajax({
        type:'POST',
        dataType: 'json',
        url:'${contextPath}/invoiceIncome/selectDebtorTermsDropDown',
        data:{
            customerId: $('#customerId').val(),
            debtorId: $('#debtorId').val()
        },
        success: function(data)
        {
            //alert(data)
            var termsId = parseInt(data);
            $("#termsId option[value='" + termsId + "']").attr('selected', 'selected');
        }
    });
}

function checkDebtorCustomerCombination(){
    $.ajax({
        type:'POST',
        dataType: 'json',
        url:'${contextPath}/invoiceIncome/selectDebtorTermsDropDown',
        data:{
            customerId: $('#customerId').val(),
            debtorId: $('#debtorId').val()
        },
        success: function(data)
        {
            //alert(data)
            var termsId = parseInt(data);
            $("#termsId option[value='" + termsId + "']").attr('selected', 'selected');
        }
    });
}