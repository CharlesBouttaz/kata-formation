package com.equipement;

import org.junit.Test;

import static org.junit.Assert.*;

public class MEListerEquipementsSPImplTest {

    @Test
    public void listerEPEquipements() throws TechnicalException, FunctionalException, InternalFunctionalException {
        MEListerEquipementsSPImpl meListerEquipementsSPImpl = new MEListerEquipementsSPImpl();

        EchListerEquipements echListerEquipements = meListerEquipementsSPImpl.listerEPEquipements(null, null, null, false, false);


    }
}