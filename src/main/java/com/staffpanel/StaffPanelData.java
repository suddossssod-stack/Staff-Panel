package com.staffpanel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** All Staff Panel records are stored locally on this computer. */
public final class StaffPanelData {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("staff-panel.json");

    public List<String> watchlist = new ArrayList<>();
    public List<String> notes = new ArrayList<>();
    public List<PunishmentEntry> punishments = new ArrayList<>();

    public static final class PunishmentEntry {
        public String player;
        public String category;
        public String offence;
        public String description;
        public String markedAt;

        public PunishmentEntry(String player, String category, String offence, String description, String markedAt) {
            this.player = player;
            this.category = category;
            this.offence = offence;
            this.description = description;
            this.markedAt = markedAt;
        }
    }

    public static StaffPanelData load() {
        try {
            if (Files.exists(FILE)) {
                StaffPanelData data = GSON.fromJson(Files.readString(FILE), StaffPanelData.class);
                if (data != null) {
                    if (data.watchlist == null) data.watchlist = new ArrayList<>();
                    if (data.notes == null) data.notes = new ArrayList<>();
                    if (data.punishments == null) data.punishments = new ArrayList<>();
                    return data;
                }
            }
        } catch (Exception e) {
            System.err.println("[Staff Panel] Could not load saved data: " + e.getMessage());
        }
        return new StaffPanelData();
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(this));
        } catch (IOException e) {
            System.err.println("[Staff Panel] Could not save data: " + e.getMessage());
        }
    }
}
