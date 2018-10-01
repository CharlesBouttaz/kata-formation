package com.equipement;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class IListerEquipementsSOC {
    public List<Personne> listerPersonneFoyer(EnteteTechnique pEnteteTechnique, Integer lIdPersonneSmart, String lCatFoyer) {
        return null;
    }

    public Set<ListePersonneEP> listerEquipementsFoyer(EnteteTechnique pEnteteTechnique, Integer lIdPersonneSmart, String lCatFoyer, List<Personne> lPersonnesFoyer, Boolean pIndicEquipementActif, Boolean pIndicEpTransmisBO, boolean lIsCodeEntrepriseGass) {
        return null;
    }

    public Map<Integer, Map<String, Set<ContratSigma>>> listerContratFoyer(EnteteTechnique pEnteteTechnique, List<Personne> lPersonnesFoyer) {
        return null;
    }

    public Map<Integer, Set<EquipementGR>> listerEquipementsGrcFoyer(EnteteTechnique pEnteteTechnique, Set<ListePersonneEP> lEquipementsFoyer) {
        return null;
    }
}
