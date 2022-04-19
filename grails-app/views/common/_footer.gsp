<div id="footer">
    <div id="footer-inner">
        <div id="footer-inner-left">
            Copyright &copy; 2012 Budgetview
        </div>
        <div id="footer-inner-right">
            <ul>
                <li>
                    <g:link url="[action:'privacy',controller:'common']"><g:message code="bv.footer.privacyPolicy.label" default="Privacy Policy" /></g:link>
                </li>
                <li>
                    <g:link url="[action:'termsOfUse',controller:'common']"><g:message code="bv.footer.termsOfUse.label" default="Terms of Use" /></g:link>
                </li>
                <li>
                    <g:link url="[action:'index',controller:'dashboard']"><g:message code="bv.footer.Sitemap.label" default="Sitemap" /></g:link>
                </li>
                <li>
                    <g:link url="[action:'create',controller:'contactUs']"><g:message code="bv.footer.contactUs.label" default="Contact Us" /></g:link>
                </li>
            </ul>
        </div>
    </div>
</div>

<g:javascript src="jquery.hoverIntent.minified.js" />
<g:javascript src="jquery.dcmegamenu.1.3.3.js" />

<script type="text/javascript">
    $(document).ready(function(){

        //Mega menu init
        $('#mega-menu-1').dcMegaMenu({
            rowItems: '3',
            speed: 'fast',
            effect: 'slide'});

        $('.inner').corner('3px');
        //$('.pagination').corner('bottom');
        $('.content').corner('3px');
        $('.buttons').corner('3px');
        $('.save').corner('3px');
        $('#income').corner('3px');
        $('#footer').corner('bottom');
        $('.allIncomeHead').corner('top');
        $('input').corner('3px');

        //Help dialog init
        $( "#helpinfodlg" ).dialog({ autoOpen: false, show : "scale", hide : "scale",height: 'auto',width: 450,});
        $( "#helpBtn" ).click(function() {
            var position =  $("#helpBtn").position();
            $("#helpinfodlg").dialog("option", "position", [position.left-40, position.top+70]);
            $( "#helpinfodlg" ).dialog("open");
        });

//        $('table.highchart').highchartTable();
    });


</script>