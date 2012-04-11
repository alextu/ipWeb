-- Création de la table pour le versionning
CREATE TABLE IP_WEB.DB_VERSION
  (
    DBV_ID      NUMBER(4,0) NOT NULL ENABLE,
    DBV_LIBELLE VARCHAR2(15) NOT NULL ENABLE,
    DBV_DATE DATE NOT NULL ENABLE,
    DBV_INSTALL DATE,
    DBV_COMMENT VARCHAR2(2000),
    CONSTRAINT PK_DB_VERSION PRIMARY KEY (DBV_ID) USING INDEX TABLESPACE SCOL_INDX ENABLE
  )
  TABLESPACE "SCOL" ;
COMMENT ON COLUMN IP_WEB.DB_VERSION.DBV_ID
IS
  'Identifiant de la version';
COMMENT ON COLUMN GRHUM.DB_VERSION.DBV_LIBELLE
IS
  'Libelle de la version';
COMMENT ON COLUMN GRHUM.DB_VERSION.DBV_DATE
IS
  'Date de release de la version';
COMMENT ON COLUMN GRHUM.DB_VERSION.DBV_INSTALL
IS
  'Date d''installation de la version. Si non renseigne, la version n''est pas completement installee.';
COMMENT ON COLUMN GRHUM.DB_VERSION.DBV_COMMENT
IS
  'Le commentaire : une courte description de cette version de la base de donnees.';
COMMENT ON TABLE GRHUM.DB_VERSION
IS
  'Historique des versions du schema du user GRHUM';
  
CREATE SEQUENCE IP_WEB.DB_VERSION_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 53 NOCACHE NOORDER NOCYCLE ;


-- Modification des vues pour virer le semestre et ainsi éviter les duplications à l'affichage des inscrits à un EC
-- (on affiche tous les inscrits indépendemment du semestre...)
CREATE OR REPLACE FORCE VIEW "IP_WEB"."V_LISTE_INSC_EC" ("MREC_KEY", "FORMATION_ABREVIATION", "FSPN_KEY", "IDIPL_ANNEE_SUIVIE", "ETUD_NUMERO", "ADR_NOM", "ADR_PRENOM", "CHOIX_INTEGRE", "FANN_KEY", "MEC_KEY", "IMREC_ETAT", "IMREC_SESSION1", "IMREC_SESSION2", "ETAT_RN", "IMREC_DISPENSE")
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
    --v.MSEM_KEY,
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
  AND dh.ANNEE_UNIV   = v.FANN_KEY);
   
  
  CREATE OR REPLACE FORCE VIEW "IP_WEB"."V_LISTE_INSC_EC_MAIL" ("MREC_KEY", "FORMATION_ABREVIATION", "FSPN_KEY", "IDIPL_ANNEE_SUIVIE", "ETUD_NUMERO", "ADR_NOM", "ADR_PRENOM", "CHOIX_INTEGRE", "FANN_KEY", "MEC_KEY", "IMREC_ETAT", "IMREC_SESSION1", "IMREC_SESSION2", "ETAT_RN", "MAIL_COMPLET", "IMREC_DISPENSE")
AS
  SELECT DISTINCT v.mrec_key,
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
    --v.msem_key,
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
  AND c.CPT_VLAN      = 'E';