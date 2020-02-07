package de.domjos.myarchivemobile.settings;

import de.domjos.myarchivelibrary.database.Database;

public final class Globals {
    private Database database;
    private Settings settings;
    private boolean network;

    public Globals() {
        this.database = null;
        this.settings = null;
        this.network = true;
    }

    public Database getDatabase() {
        return this.database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public Settings getSettings() {
        return this.settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public boolean isNetwork() {
        return this.network;
    }

    public void setNetwork(boolean network) {
        this.network = network;
    }
}
