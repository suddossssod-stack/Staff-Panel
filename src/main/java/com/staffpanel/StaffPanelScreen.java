    private void initNotes(int left, int top) {
        input = new TextFieldWidget(textRenderer, left + 18, top + 91, 390, 22, Text.literal("Private note"));
        input.setMaxLength(200);
        input.setPlaceholder(Text.literal("Write a private staff note..."));
        addDrawableChild(input);
        addDrawableChild(ButtonWidget.builder(Text.literal("Save note"), b -> {
            String note = input.getText().trim();
            if (!note.isEmpty()) { data.notes.add(note); data.save(); init(); }
        }).dimensions(left + 416, top + 91, 124, 22).build());
        for (int i = 0; i < Math.min(7, data.notes.size()); i++) {
            final int index = data.notes.size() - 1 - i;
            String note = data.notes.get(index);
            String label = note.length() > 38 ? note.substring(0, 35) + "..." : note;
            addDrawableChild(ButtonWidget.builder(Text.literal("Delete  " + label), b -> {
                data.notes.remove(index); data.save(); init();
            }).dimensions(left + 18, top + 126 + i * 24, 522, 20).build());
        }
    }

    private void initPunishments(int left, int top) {
        input = new TextFieldWidget(textRenderer, left + 18, top + 91, 330, 22, Text.literal("Player name"));
        input.setMaxLength(32);
        input.setPlaceholder(Text.literal("Enter the player's exact username..."));
        addDrawableChild(input);
        addDrawableChild(ButtonWidget.builder(Text.literal(punishmentType == PunishmentType.MUTE ? "Type: MUTE" : "Type: BAN"), b -> {
            punishmentType = punishmentType == PunishmentType.MUTE ? PunishmentType.BAN : PunishmentType.MUTE;
            offenceIndex = 0; init();
        }).dimensions(left + 356, top + 91, 184, 22).build());

        String[][] offences = currentOffences();
        if (offenceIndex >= offences.length) offenceIndex = 0;
        addDrawableChild(ButtonWidget.builder(Text.literal("‹ Previous"), b -> { offenceIndex = (offenceIndex - 1 + currentOffences().length) % currentOffences().length; init(); })
                .dimensions(left + 18, top + 126, 100, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Next offence ›"), b -> { offenceIndex = (offenceIndex + 1) % currentOffences().length; init(); })
                .dimensions(left + 124, top + 126, 120, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("MARK FOR " + punishmentType), b -> {
            String player = input.getText().trim();
            if (player.isEmpty()) { status = "Enter a player name first."; return; }
            String[] selected = currentOffences()[offenceIndex];
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            data.punishments.add(0, new StaffPanelData.PunishmentEntry(player, punishmentType.name(), selected[0], selected[1], time));
            data.save(); status = player + " marked for " + punishmentType + ": " + selected[0]; init();
        }).dimensions(left + 356, top + 126, 184, 22).build());
    }

    private String[][] currentOffences() { return punishmentType == PunishmentType.MUTE ? MUTE_OFFENCES : BAN_OFFENCES; }

    private void changeTab(Tab next) { tab = next; status = ""; init(); }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        int left = this.width / 2 - 280;
        int top = this.height / 2 - 175;
        context.fill(0, 0, width, height, 0x88050912);
        context.fill(left, top, left + 560, top + 350, 0xF0141B2B);
        context.fill(left, top, left + 560, top + 5, 0xFF35B8FF);
        context.fill(left + 12, top + 68, left + 548, top + 69, 0xFF34435E);
        context.drawTextWithShadow(textRenderer, "STAFF PANEL", left + 18, top + 14, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, "MODERATION TOOLKIT  /  LOCAL PROFILE", left + 18, top + 25, 0xFF8FA8C9);
        super.render(context, mouseX, mouseY, delta);

        if (tab == Tab.HOME) {
            context.drawTextWithShadow(textRenderer, "Welcome back, staff member.", left + 20, top + 112, 0xFFFFFFFF);
            drawStat(context, left + 20, top + 145, "WATCHLIST", String.valueOf(data.watchlist.size()));
            drawStat(context, left + 200, top + 145, "PRIVATE NOTES", String.valueOf(data.notes.size()));
            drawStat(context, left + 380, top + 145, "PUNISHMENT MARKS", String.valueOf(data.punishments.size()));
            context.drawTextWithShadow(textRenderer, "Use the tabs above to manage players and staff records.", left + 20, top + 220, 0xFFB8C9E2);
            context.drawTextWithShadow(textRenderer, "Press K or use /staffpanel to open this dashboard.", left + 20, top + 242, 0xFF8498B7);
        } else if (tab == Tab.WATCHLIST) {
            context.drawTextWithShadow(textRenderer, "WATCHLIST  /  Players to keep an eye on", left + 18, top + 78, 0xFFB9DFFF);
            if (data.watchlist.isEmpty()) context.drawTextWithShadow(textRenderer, "No players added yet.", left + 18, top + 132, 0xFF9AAAC2);
        } else if (tab == Tab.NOTES) {
            context.drawTextWithShadow(textRenderer, "PRIVATE NOTES  /  Saved on this computer", left + 18, top + 78, 0xFFB9DFFF);
            if (data.notes.isEmpty()) context.drawTextWithShadow(textRenderer, "No notes saved yet.", left + 18, top + 132, 0xFF9AAAC2);
        } else {
            renderPunishments(context, left, top);
        }
        if (!status.isEmpty()) context.drawTextWithShadow(textRenderer, status, left + 18, top + 326, 0xFFFFD36E);
        context.drawTextWithShadow(textRenderer, "CLIENT-SIDE ONLY  •  Records are not sent to the server", left + 18, top + 332, 0xFF73839D);
    }

    private void renderPunishments(DrawContext context, int left, int top) {
        String[][] offences = currentOffences();
        String[] selected = offences[Math.min(offenceIndex, offences.length - 1)];
        context.drawTextWithShadow(textRenderer, "PUNISHMENT QUEUE  /  Select an offence, enter a player, then mark it.", left + 18, top + 78, 0xFFB9DFFF);
        int colour = punishmentType == PunishmentType.MUTE ? 0xFF75D9FF : 0xFFFF8D9A;
        context.drawTextWithShadow(textRenderer, punishmentType + " OFFENCE " + (offenceIndex + 1) + " OF " + offences.length, left + 260, top + 132, colour);
        context.drawTextWithShadow(textRenderer, selected[0], left + 18, top + 160, 0xFFFFFFFF);
        drawWrapped(context, selected[1], left + 18, top + 178, 510, 0xFFB4C3D8);
        if (punishmentType == PunishmentType.BAN && (offenceIndex == 6 || offenceIndex == 8)) {
            context.fill(left + 18, top + 211, left + 542, top + 232, 0xFF67313B);
            context.drawTextWithShadow(textRenderer, offenceIndex == 6 ? "URGENT: Contact Admin+ immediately." : "PERMANENT BAN: Report to Management immediately.", left + 26, top + 218, 0xFFFFC3C9);
        }
        context.drawTextWithShadow(textRenderer, "RECENT MARKS", left + 18, top + 238, 0xFFB9DFFF);
        List<StaffPanelData.PunishmentEntry> marks = data.punishments;
        if (marks.isEmpty()) context.drawTextWithShadow(textRenderer, "No punishment marks yet.", left + 18, top + 258, 0xFF9AAAC2);
        for (int i = 0; i < Math.min(3, marks.size()); i++) {
            StaffPanelData.PunishmentEntry entry = marks.get(i);
            String line = entry.player + "  •  " + entry.category + "  •  " + entry.offence;
            if (line.length() > 82) line = line.substring(0, 79) + "...";
            context.drawTextWithShadow(textRenderer, line, left + 18, top + 258 + i * 17, entry.category.equals("BAN") ? 0xFFFFA6AE : 0xFF9DDEFF);
        }
        if (marks.size() > 3) context.drawTextWithShadow(textRenderer, "+ " + (marks.size() - 3) + " more saved marks in your local file.", left + 18, top + 310, 0xFF8498B7);
    }

    private void drawStat(DrawContext context, int x, int y, String label, String value) {
        context.fill(x, y, x + 160, y + 52, 0xFF202C43);
        context.drawTextWithShadow(textRenderer, label, x + 10, y + 9, 0xFF91A9C9);
        context.drawTextWithShadow(textRenderer, value, x + 10, y + 27, 0xFFFFFFFF);
    }

    private void drawWrapped(DrawContext context, String text, int x, int y, int maxWidth, int color) {
        StringBuilder line = new StringBuilder();
        int lineY = y;
        for (String word : text.split(" ")) {
            String candidate = line.isEmpty() ? word : line + " " + word;
            if (textRenderer.getWidth(candidate) > maxWidth && !line.isEmpty()) {
                context.drawTextWithShadow(textRenderer, line.toString(), x, lineY, color);
                lineY += 12; line = new StringBuilder(word);
            } else line = new StringBuilder(candidate);
        }
        if (!line.isEmpty()) context.drawTextWithShadow(textRenderer, line.toString(), x, lineY, color);
    }

    @Override
    public void close() { if (client != null) client.setScreen(parent); }
}
