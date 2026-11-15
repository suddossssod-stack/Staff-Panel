package com.staffpanel;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.Locale;

public final class StaffPanelScreen extends Screen {
    private enum Tab { HOME, WATCHLIST, NOTES }

    private final Screen parent;
    private final StaffPanelData data;
    private Tab tab = Tab.HOME;
    private TextFieldWidget input;

    public StaffPanelScreen(Screen parent) {
        super(Text.literal("Staff Panel"));
        this.parent = parent;
        this.data = StaffPanelData.load();
    }

    @Override
    protected void init() {
        clearChildren();
        int left = this.width / 2 - 150;
        int top = this.height / 2 - 105;

        addDrawableChild(ButtonWidget.builder(Text.literal("Home"), b -> changeTab(Tab.HOME))
                .dimensions(left, top, 82, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Watchlist"), b -> changeTab(Tab.WATCHLIST))
                .dimensions(left + 88, top, 88, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Notes"), b -> changeTab(Tab.NOTES))
                .dimensions(left + 182, top, 82, 20).build());

        if (tab == Tab.WATCHLIST) {
            input = new TextFieldWidget(textRenderer, left, top + 38, 190, 20, Text.literal("Player name"));
            input.setMaxLength(32);
            addDrawableChild(input);
            addDrawableChild(ButtonWidget.builder(Text.literal("Add"), b -> {
                String name = input.getText().trim();
                if (!name.isEmpty() && data.watchlist.stream().noneMatch(s -> s.equalsIgnoreCase(name))) {
                    data.watchlist.add(name);
                    data.save();
                    init();
                }
            }).dimensions(left + 196, top + 38, 68, 20).build());

            for (int i = 0; i < Math.min(6, data.watchlist.size()); i++) {
                String name = data.watchlist.get(i);
                addDrawableChild(ButtonWidget.builder(Text.literal("Remove: " + name), b -> {
                    data.watchlist.remove(name);
                    data.save();
                    init();
                }).dimensions(left, top + 66 + i * 23, 264, 20).build());
            }
        } else if (tab == Tab.NOTES) {
            input = new TextFieldWidget(textRenderer, left, top + 38, 190, 20, Text.literal("Write a private note"));
            input.setMaxLength(160);
            addDrawableChild(input);
            addDrawableChild(ButtonWidget.builder(Text.literal("Save"), b -> {
                String note = input.getText().trim();
                if (!note.isEmpty()) {
                    data.notes.add(note);
                    data.save();
                    init();
                }
            }).dimensions(left + 196, top + 38, 68, 20).build());

            for (int i = 0; i < Math.min(6, data.notes.size()); i++) {
                final int index = data.notes.size() - 1 - i;
                String note = data.notes.get(index);
                String label = note.length() > 34 ? note.substring(0, 31) + "..." : note;
                addDrawableChild(ButtonWidget.builder(Text.literal("Delete: " + label), b -> {
                    data.notes.remove(index);
                    data.save();
                    init();
                }).dimensions(left, top + 66 + i * 23, 264, 20).build());
            }
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("Close"), b -> close())
                .dimensions(left + 204, this.height / 2 + 82, 60, 20).build());
    }

    private void changeTab(Tab next) {
        tab = next;
        init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        int left = this.width / 2 - 150;
        int top = this.height / 2 - 105;
        context.fill(left - 12, top - 14, left + 282, top + 220, 0xE8202028);
        context.drawCenteredTextWithShadow(textRenderer, title, this.width / 2, top - 7, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);

        if (tab == Tab.HOME) {
            context.drawTextWithShadow(textRenderer, "Your local staff toolkit", left, top + 65, 0xFFFFFF);
            context.drawTextWithShadow(textRenderer, "Players on watchlist: " + data.watchlist.size(), left, top + 88, 0xC8D8FF);
            context.drawTextWithShadow(textRenderer, "Private notes saved: " + data.notes.size(), left, top + 106, 0xC8D8FF);
            context.drawTextWithShadow(textRenderer, "Press K or type /staffpanel to open this panel.", left, top + 138, 0xAAAAAA);
            context.drawTextWithShadow(textRenderer, "Saved only on this computer; not sent to servers.", left, top + 156, 0xAAAAAA);
        } else if (tab == Tab.WATCHLIST) {
            context.drawTextWithShadow(textRenderer, "Players you want to keep track of:", left, top + 63, 0xFFFFFF);
            if (data.watchlist.isEmpty()) context.drawTextWithShadow(textRenderer, "Your watchlist is empty.", left, top + 88, 0xAAAAAA);
            if (data.watchlist.size() > 6) context.drawTextWithShadow(textRenderer, "Showing first 6 entries.", left, top + 210, 0xAAAAAA);
        } else {
            context.drawTextWithShadow(textRenderer, "Private notes stored on this computer:", left, top + 63, 0xFFFFFF);
            if (data.notes.isEmpty()) context.drawTextWithShadow(textRenderer, "No notes saved yet.", left, top + 88, 0xAAAAAA);
            if (data.notes.size() > 6) context.drawTextWithShadow(textRenderer, "Showing the 6 newest notes.", left, top + 210, 0xAAAAAA);
        }
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }
}
