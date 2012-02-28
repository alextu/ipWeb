-- Modif de la vue listant les inscrits aux ec pour prendre en compte le IMREC_DISPENSE
  
CREATE OR REPLACE FORCE VIEW "IP_WEB"."V_INSC_EC_RES" ("FSPN_KEY", "IDIPL_ANNEE_SUIVIE", "ETUD_NUMERO", "ADR_NOM", "ADR_PRENOM", "FANN_KEY", "MEC_KEY", "IDIPL_NUMERO", "IMREC_ETAT", "ETAT_RN", "IMREC_SESSION1", "IMREC_SESSION2", "MRSEM_KEY", "MREC_KEY", "MSEM_KEY", "IMREC_DISPENSE")
AS
  SELECT et.FSPN_KEY,
    et.IDIPL_ANNEE_SUIVIE,
    et.ETUD_NUMERO,
    et.ADR_NOM,
    initcap(et.ADR_PRENOM) ADR_PRENOM,
    et.FANN_KEY,
    rec.MEC_KEY,
    iec.idipl_numero,
    iec.imrec_etat,
    bc.etat_rn,
    iec.imrec_session1,
    iec.imrec_session2,
    rsem.mrsem_key,
    iec.mrec_key,
    rsem.MSEM_KEY,
    iec.imrec_dispense
  FROM scolarite.scol_inscription_ec iec,
    scolarite.scol_maquette_repartition_ec rec,
    scolarite.scol_maquette_repartition_ue rue,
    scolarite.scol_maquette_repartition_sem rsem,
    scolarite.scol_maquette_parcours par,
    scolarite.scol_inscription_etudiant et,
    (SELECT bc.mrsem_key,
      DECODE (NVL (bo.rnaff_etat, 0), 0, bc.bcal_etat, GREATEST (NVL (bc.bcal_etat, 0), bo.rnaff_etat) ) etat_rn,
      bc.fann_key
    FROM scolarite.scol_bilan_calcul bc,
      ip_web.ip_bilanrn_ok bo
    WHERE bc.mrsem_key = bo.mrsem_key(+)
    ) bc
  WHERE iec.mrec_key         = rec.mrec_key
  AND rec.MUE_KEY            = rue.MUE_KEY
  AND rue.MSEM_KEY           = rsem.MSEM_KEY
  AND rsem.MPAR_KEY          = par.MPAR_KEY
  AND (par.FSPN_KEY          = et.FSPN_KEY
  AND et.IDIPL_NUMERO        = iec.IDIPL_NUMERO)
  AND rsem.mrsem_key         = bc.mrsem_key
  AND (iec.imrec_dispense   IS NULL
  OR iec.imrec_dispense NOT IN (1, 3, 4, 5, 6, 7, 8, 99))
  AND (et.res_code          IS NULL
  OR et.res_code NOT        IN ('D', 'E', 'Z', 'H', '1')) ;
  

CREATE OR REPLACE FORCE VIEW "IP_WEB"."V_LISTE_INSC_EC" ("MREC_KEY", "FORMATION_ABREVIATION", "FSPN_KEY", "IDIPL_ANNEE_SUIVIE", "ETUD_NUMERO", "ADR_NOM", "ADR_PRENOM", "CHOIX_INTEGRE", "FANN_KEY", "MEC_KEY", "IMREC_ETAT", "IMREC_SESSION1", "IMREC_SESSION2", "ETAT_RN", "MSEM_KEY", "IMREC_DISPENSE")
AS
  SELECT v.MREC_KEY,
    dh.FORMATION_ABREVIATION,
    v.FSPN_KEY,
    v.IDIPL_ANNEE_SUIVIE,
    v.ETUD_NUMERO,
    v.ADR_NOM,
    v.ADR_PRENOM,
    v.CHOIX_INTEGRE,
    v.FANN_KEY,
    v.MEC_KEY,
    v.IMREC_ETAT,
    v.IMREC_SESSION1,
    v.IMREC_SESSION2,
    v.ETAT_RN,
    v.MSEM_KEY,
    v.IMREC_DISPENSE
  FROM
    (SELECT et.FSPN_KEY,
      et.IDIPL_ANNEE_SUIVIE,
      et.ETUD_NUMERO,
      et.ADR_NOM,
      initcap(et.ADR_PRENOM) ADR_PRENOM,
      et.FANN_KEY,
      rec.MEC_KEY,
      ipc.IDIPL_NUMERO,
      'N' CHOIX_INTEGRE,
      NULL IMREC_ETAT,
      NULL IMREC_SESSION1,
      NULL IMREC_SESSION2,
      NULL ETAT_RN,
      ipc.MREC_KEY,
      rsem.msem_key,
      NULL IMREC_DISPENSE
    FROM ip_choix_ec ipc,
      scolarite.scol_inscription_etudiant et,
      scolarite.scol_maquette_repartition_ec rec,
      scolarite.scol_maquette_repartition_ue rue,
      scolarite.scol_maquette_repartition_sem rsem,
      scolarite.scol_maquette_semestre sem,
      scolarite.scol_maquette_parcours par
    WHERE et.IDIPL_NUMERO     = ipc.idipl_numero
    AND rec.MREC_KEY          = ipc.mrec_key
    AND rec.MUE_KEY           = rue.MUE_KEY
    AND rsem.MSEM_KEY         = rue.MSEM_KEY
    AND rsem.MSEM_KEY         = sem.MSEM_KEY
    AND rsem.MPAR_KEY         = par.MPAR_KEY
    AND (par.FSPN_KEY         = et.FSPN_KEY
    AND et.IDIPL_ANNEE_SUIVIE = TRUNC((sem.MSEM_ORDRE+1)/2))
    AND ipc.CHOIX_INTEGRE     = 'N'
    AND (et.res_code         IS NULL
    OR et.res_code NOT       IN ('D', 'E', 'Z', 'H', '1'))
    UNION
    SELECT siec.FSPN_KEY,
      siec.IDIPL_ANNEE_SUIVIE,
      siec.ETUD_NUMERO,
      siec.ADR_NOM,
      siec.ADR_PRENOM,
      siec.FANN_KEY,
      siec.MEC_KEY,
      siec.IDIPL_NUMERO,
      'O' CHOIX_INTEGRE,
      siec.IMREC_ETAT,
      siec.IMREC_SESSION1,
      siec.IMREC_SESSION2,
      siec.ETAT_RN,
      siec.MREC_KEY,
      siec.MSEM_KEY,
      siec.IMREC_DISPENSE
    FROM ip_web.v_insc_ec_res siec
    ) v,
    ip_web.v_liste_diplome_habilite dh
  WHERE (dh.FSPN_KEY  = v.FSPN_KEY
  AND dh.ANNEE_SUIVIE = v.IDIPL_ANNEE_SUIVIE
  AND dh.ANNEE_UNIV   = v.FANN_KEY) ;
  
  
  CREATE OR REPLACE FORCE VIEW "IP_WEB"."V_LISTE_INSC_EC_MAIL" ("MREC_KEY", "FORMATION_ABREVIATION", "FSPN_KEY", "IDIPL_ANNEE_SUIVIE", "ETUD_NUMERO", "ADR_NOM", "ADR_PRENOM", "CHOIX_INTEGRE", "FANN_KEY", "MEC_KEY", "IMREC_ETAT", "IMREC_SESSION1", "IMREC_SESSION2", "ETAT_RN", "MSEM_KEY", "MAIL_COMPLET", "IMREC_DISPENSE")
AS
  SELECT v.mrec_key,
    v.formation_abreviation,
    v.fspn_key,
    v.idipl_annee_suivie,
    v.etud_numero,
    v.adr_nom,
    v.adr_prenom,
    v.choix_integre,
    v.fann_key,
    v.mec_key,
    v.imrec_etat,
    v.imrec_session1,
    v.imrec_session2,
    v.etat_rn,
    v.msem_key,
    c.CPT_EMAIL
    || '@'
    || c.CPT_DOMAINE mail_complet,
    v.imrec_dispense
  FROM ip_web.v_liste_insc_ec v,
    grhum.etudiant et,
    grhum.individu_ulr ind,
    grhum.repart_compte rc,
    grhum.compte c
  WHERE v.ETUD_NUMERO = et.ETUD_NUMERO
  AND et.NO_INDIVIDU  = ind.NO_INDIVIDU
  AND ind.PERS_ID     = rc.PERS_ID(+)
  AND rc.CPT_ORDRE    = c.CPT_ORDRE(+)
  AND c.CPT_VLAN      = 'E' ;
  
