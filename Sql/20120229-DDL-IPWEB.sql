-- Rajout de l'email dans la vue listant les inscriptions
CREATE OR REPLACE FORCE VIEW "IP_WEB"."V_ETUD_INSC_SEMESTRE_RES_EMAIL" ("IDIPL_NUMERO", "ETUD_NUMERO", "ADR_NOM", "ADR_PRENOM", "IDIPL_TYPE_INSCRIPTION", "RES_CODE", "FSPN_KEY", "FANN_KEY", "MSEM_ORDRE", "LIB_DISPENSE", "IMRSEM_ETAT", "LIB_RES_SEM", "IMRSEM_POINTS1", "IMRSEM_SESSION1", "MENTION1", "IMRSEM_POINTS2", "IMRSEM_SESSION2", "MENTION2", "MSEM_KEY", "IDIPL_REDOUBLANT", "ETAT_IP", "EMAIL")
AS
  SELECT
  	v.idipl_numero,
    v.etud_numero,
    v.adr_nom,
    v.adr_prenom,
    v.idipl_type_inscription,
    v.res_code,
    v.fspn_key,
    v.fann_key,
    v.msem_ordre,
    v.lib_dispense,
    v.imrsem_etat,
    v.lib_res_sem,
    v.imrsem_points1,
    v.imrsem_session1,
    v.mention1,
    v.imrsem_points2,
    v.imrsem_session2,
    v.mention2,
    v.msem_key,
    v.idipl_redoublant,
    v.etat_ip,
    c.cpt_email || '@' || c.cpt_domaine
  FROM IP_WEB.v_etud_insc_semestre_res v,
    grhum.etudiant et,
    grhum.individu_ulr ind,
    grhum.repart_compte rc,
    grhum.compte c
  WHERE v.ETUD_NUMERO = et.ETUD_NUMERO
  AND et.NO_INDIVIDU  = ind.NO_INDIVIDU
  AND ind.PERS_ID     = rc.PERS_ID(+)
  AND rc.CPT_ORDRE    = c.CPT_ORDRE(+)
  AND c.CPT_VLAN      = 'E';
