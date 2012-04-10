// Ce script permet de switcher entre une DIV visible et non visible 
// but : présenter à la demande un résumé ou une zone complete sans aller 
//       faire un round-trip au serveur !

function switcherZoneDetail(divResume,divDetail,divMasqueDetail,chpHide) {
  var monChpHide = document.forms["formIpEtud"].elements[chpHide]; 
  if (monChpHide.value=="O") {
  	cacherZoneDetail(divResume,divDetail,divMasqueDetail,chpHide);
  }
  else {
  	montrerZoneDetail(divResume,divDetail,divMasqueDetail,chpHide);
  }
}

function cacherZoneDetail(divResume,divDetail,divMasqueDetail,chpHide) {
  var monChpHide = document.forms["formIpEtud"].elements[chpHide]; 
  monChpHide.value="N";

  var maDivResume = document.getElementById(divResume);
  var maDivDetail = document.getElementById(divDetail);
  var maDivMasqueDetail = document.getElementById(divMasqueDetail);

  maDivResume.style.display="block";
  maDivDetail.style.display="none";
  maDivMasqueDetail.style.display="none";

}

function montrerZoneDetail(divResume,divDetail,divMasqueDetail,chpHide) {
  var monChpHide = document.forms["formIpEtud"].elements[chpHide];
  monChpHide.value="O";

  var maDivResume = document.getElementById(divResume);
  var maDivDetail = document.getElementById(divDetail);
  var maDivMasqueDetail = document.getElementById(divMasqueDetail);
  
  maDivResume.style.display="none";
  maDivDetail.style.display="block";
  maDivMasqueDetail.style.display="block";
}

// appel du JavaScript qui va cocher ou décocher... en cliquant n'importe ou sur la ligne !
function cocheLigne(refCaseACocher) {
  var maCaC = document.forms["formIpEtud"].elements[refCaseACocher];
  maCaC.checked=!maCaC.checked;
}
