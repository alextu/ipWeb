create or replace force view IP_WEB.V_SCOL_INSCRIPTION_AP (IDIPL_NUMERO, NOM_PRENOM, FILIERE, FANN_KEY, MAP_KEY, MEC_KEY)
as
select distinct a.idipl_numero, v.adr_nom || ' ' || v.adr_prenom NOM_PRENOM, v.formation_abreviation as FILIERE,
				a.fann_key, rap.map_key, rap.mec_key
         from SCOLARITE.scol_inscription_ap a
         join SCOLARITE.scol_maquette_repartition_ap rap
              on a.mrap_key = rap.mrap_key
         join IP_WEB.v_liste_insc_ec v
              on rap.mec_key = v.mec_key
         join SCOLARITE.scol_inscription_etudiant insc
              on a.idipl_numero = insc.idipl_numero and 
                 v.etud_numero = insc.etud_numero
         where a.imrap_dispense <> 14 and
               v.imrec_dispense <> 14;