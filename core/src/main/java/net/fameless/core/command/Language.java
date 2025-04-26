package net.fameless.core.command;

import net.fameless.core.ForceBattle;
import net.fameless.core.caption.Caption;
import net.fameless.core.command.framework.CallerType;
import net.fameless.core.command.framework.Command;
import net.fameless.core.command.framework.CommandCaller;
import net.fameless.core.gui.LanguageGUI;
import net.fameless.core.player.BattlePlayer;
import net.fameless.core.util.StringUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Language extends Command {

    private final LanguageGUI<?> languageGUI;

    public Language() {
        super(
                "lang",
                List.of("language"),
                CallerType.NONE,
                "/lang <identifier>",
                "forcebattle.lang",
                "Command to change the language of the plugin"
        );

        this.languageGUI = ForceBattle.injector().getInstance(LanguageGUI.class);
    }

    @Override
    public void executeCommand(@NotNull CommandCaller caller, String @NotNull [] args) {
        if (args.length == 0) {
            if (!(caller instanceof BattlePlayer<?> battlePlayer)) {
                caller.sendMessage(Caption.of(CallerType.CONSOLE.getErrorMessageKey()));
                return;
            }
            battlePlayer.openInventory(languageGUI.getLanguageGUI());
        } else {
            net.fameless.core.caption.Language newLanguage = net.fameless.core.caption.Language.ofIdentifier(args[0]);
            if (newLanguage != null && !newLanguage.equals(Caption.getCurrentLanguage())) {
                Caption.setCurrentLanguage(newLanguage);
                ForceBattle.platform().broadcast(MiniMessage.miniMessage().deserialize(Caption.getCurrentLanguage().getUpdateMessage(), Caption.prefixTagResolver()));
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String @NotNull [] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], List.of("en", "zh_cn", "zh_tw", "de"), new ArrayList<>());
        }
        return List.of();
    }

}
