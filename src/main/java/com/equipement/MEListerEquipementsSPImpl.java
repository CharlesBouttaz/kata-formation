package com.equipement;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.management.monitor.Monitor;


public class MEListerEquipementsSPImpl extends AbstractGenericServiceSP implements IMEListerEquipementsSP {

    private static final Logger LOGGER = Logger.getLogger(MEListerEquipementsSPImpl.class);

    public EchListerEquipements listerEPEquipements(final EnteteTechnique pEnteteTechnique, final String pIdPersonne, final String pCdCategorieFoyer,
                                                    final Boolean pIndicEquipementActif, final Boolean pIndicEpTransmisBO)
            throws FunctionalException, TechnicalException, InternalFunctionalException {

        final Monitor lMonitor = monitorStart(pEnteteTechnique);

        EchListerEquipements lEchListerEquipements = null;

        try {
            // Démarrer la transaction
            JTAUtils.initialiserTransaction(pEnteteTechnique);

            // Contrôler l'entête technique et les paramètres d'entrée
            CoucheSPUtils.controlerParametresSP(pEnteteTechnique, pIdPersonne, pCdCategorieFoyer);

            // si foyer non familial la categorie n'est pas sauvé en BDD Smart donc mettre null
            String lCatFoyer = null;
            if (SgmcConstantes.CATEGORIE_FOYER_FAMILIALE.equals(pCdCategorieFoyer)) {
                lCatFoyer = pCdCategorieFoyer;
            }

            // Appel du SO
            final IListerEquipementsSOC lListerEquipementsSOC = new ListerEquipementsSOCImpl();
            final boolean lIsCodeEntrepriseGass = SgmcConstantes.CODE_ENTREP_GASS.equals(pEnteteTechnique.getCodeEntreprise());

            // Pour GASS, l'identifiant passé est celui de la GRC, il faut le transformer en identifiant SMART
            Integer lIdPersonneSmart = null;

            final IMetierPersonnesSOC lMetierPersonnesSOC = new MetierPersonnesSOCImpl();

            if (lIsCodeEntrepriseGass) {
                final Map<String, String> lListeIdentifiants = lMetierPersonnesSOC.getIdsPersonne(pEnteteTechnique, null, pIdPersonne, null);
                final String lId = lListeIdentifiants.get(SgmcConstantes.ORIGINE_IDENTIFIANT_SMART);
                if (lId != null) {
                    lIdPersonneSmart = Integer.valueOf(lId);
                }
            } else {
                lIdPersonneSmart = Integer.parseInt(pIdPersonne);
            }

            // Récupération des personnes du foyer
            if (lIdPersonneSmart != null) {
                final List<Personne> lPersonnesFoyer = lListerEquipementsSOC.listerPersonneFoyer(pEnteteTechnique, lIdPersonneSmart, lCatFoyer);
                final Set<ListePersonneEP> lEquipementsFoyer = lListerEquipementsSOC.listerEquipementsFoyer(pEnteteTechnique, lIdPersonneSmart, lCatFoyer, lPersonnesFoyer, pIndicEquipementActif, pIndicEpTransmisBO, lIsCodeEntrepriseGass);
                final Map<Integer, Map<String, Set<ContratSigma>>> lListeContratFoyer = lListerEquipementsSOC.listerContratFoyer(pEnteteTechnique, lPersonnesFoyer);
                final Map<Integer, Set<EquipementGR>> lListeEquipementGRCFoyer = lListerEquipementsSOC.listerEquipementsGrcFoyer(pEnteteTechnique, lEquipementsFoyer);

                // Mapping vers modele Echange
                // Dans le cas GASS on veut tous les BO + les contrats collectivités
                lEchListerEquipements = MediationEchListerEquipements.mapperModFos(pEnteteTechnique, lEquipementsFoyer, lListeContratFoyer, lListeEquipementGRCFoyer,
                        lPersonnesFoyer, lIsCodeEntrepriseGass, lIsCodeEntrepriseGass);

                // OHU - amélioration de la TDN - filtrer les équipements en fonction de leur état : équipements uniquement actifs ou tous les équipements
                if (pIndicEquipementActif) {
                    for (final EchEquipementsPersonne lEchangeEquipementsPersonne : lEchListerEquipements.getEchangeEquipementPersonnes()) {
                        final EchEquipement[] lTabEchangeEquipement = this.filtrerEchangeEquipement(lEchangeEquipementsPersonne.getEchangeEquipements(),
                                SgmcConstantes.LISTE_ETATS_CONTRAT_INCLUS_DEDOUBLEMENT);
                        lEchangeEquipementsPersonne.setEchangeEquipements(lTabEchangeEquipement);
                    }
                }
            } else {
                throw new InternalFunctionalException("Personne " + pIdPersonne + " non trouvée !");
            }

            // Valider la transaction
            JTAUtils.commit();

        } catch (final Exception lException) {
            try {
                JTAUtils.rollback();
            } catch (final Exception lException2) {
                LOGGER.error(lException2);
            } finally {
                super.gestionException(lException, pEnteteTechnique);
            }
        } finally {
            super.monitorEnd(lMonitor);
            LOGGER.debug("MEListerEquipementsSPImpl.listerEPEquipements - end");
        }

        return lEchListerEquipements;
    }

    /**
     * Méthode permettant de filtrer les équipements en fonction de leur état
     *
     * @param pTabEchangeEquipement tableau des {@link EchangeEquipement}
     * @param pListEtatContrat {@link List} les états de contrat autorisés
     * @return tableau des {@link EchangeEquipement} filtré
     */
    private EchEquipement[] filtrerEchangeEquipement(final EchEquipement[] pTabEchangeEquipement, final List<String> pListEtatContrat) {
        EchEquipement lEchangeEquipement = null;

        // Transformer le tableau en List sans utiliser Arrays.asList pour éviter UnsupportedOperationException lors de la méthode lIteratorEchangeEquipement.remove();
        final List<EchEquipement> lListEchangeEquipement = new ArrayList<EchEquipement>();
        for (final EchEquipement lEchEquipement : pTabEchangeEquipement) {
            lListEchangeEquipement.add(lEchEquipement);
        }

        // Créer un Iterator pour pouvoir supprimer dynamiquement les éléments
        final Iterator<EchEquipement> lIteratorEchangeEquipement = lListEchangeEquipement.iterator();

        while (lIteratorEchangeEquipement.hasNext()) {
            lEchangeEquipement = lIteratorEchangeEquipement.next();
            // NBE et OHU anomalie 4460 : supprimer les equipements inactif et conserver les équipements non conformes
            if (pListEtatContrat != null && !pListEtatContrat.contains(lEchangeEquipement.getEtat())
                    && (lEchangeEquipement.getIndicTransmisBo() == null || !lEchangeEquipement.getIndicTransmisBo())
                    && (lEchangeEquipement.getEpNonConforme() == null || !lEchangeEquipement.getEpNonConforme())) {
                lIteratorEchangeEquipement.remove();
            }
        }

        // Transformer la liste en tableau
        final EchEquipement[] lTabEchangeEquipement = new EchEquipement[lListEchangeEquipement.size()];

        return lListEchangeEquipement.toArray(lTabEchangeEquipement);
    }
}
