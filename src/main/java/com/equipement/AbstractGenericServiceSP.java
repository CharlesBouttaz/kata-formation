package com.equipement;

import javax.management.monitor.Monitor;

public class AbstractGenericServiceSP {
    protected Monitor monitorStart(EnteteTechnique pEnteteTechnique) {
        return null;
    }

    protected void gestionException(Exception lException, EnteteTechnique pEnteteTechnique) {
    }

    protected void monitorEnd(Monitor lMonitor) {

    }
}
