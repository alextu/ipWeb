create or replace force view IP_WEB.V_SCOL_NB_INSCRIPTION_EC_FAC (FANN_KEY, IDIPL_NUMERO, NB_EC_FACULTATIFS)
as
select fann_key, idipl_numero, count(mrec_key) from (
  select distinct iec.idipl_numero,
    iec.mrec_key,
    rec.mtec_code,    
    iec.fann_key
  from scolarite.scol_inscription_ec iec,
    scolarite.scol_maquette_repartition_ec rec,
    scolarite.scol_maquette_repartition_ue rue
  where iec.mrec_key = rec.mrec_key
  and rec.mue_key    = rue.mue_key
  and rec.mtec_code = 'F')
group by fann_key, idipl_numero;