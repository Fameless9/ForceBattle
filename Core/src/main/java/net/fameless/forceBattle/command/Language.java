package net.fameless.forceBattle.command;

import net.fameless.forceBattle.ForceBattle;
import net.fameless.forceBattle.caption.Caption;
import net.fameless.forceBattle.command.framework.CallerType;
import net.fameless.forceBattle.command.framework.Command;
import net.fameless.forceBattle.command.framework.CommandCaller;
import net.fameless.forceBattle.gui.LanguageGUI;
import net.fameless.forceBattle.player.BattlePlayer;
import net.fameless.forceBattle.util.StringUtil;
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
                "forcebattle.lang"
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
            net.fameless.forceBattle.caption.Language newLanguage = net.fameless.forceBattle.caption.Language.ofIdentifier(args[0]);
            if (newLanguage != null && !newLanguage.equals(Caption.getCurrentLanguage())) {
                Caption.setCurrentLanguage(newLanguage);
                ForceBattle.platform().broadcast(MiniMessage.miniMessage().deserialize(Caption.getCurrentLanguage().getUpdateMessage(), Caption.prefixTagResolver()));
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String @NotNull [] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], List.of("en", "zh_cn","zh_tw","de"), new ArrayList<>());
        }
        return List.of();
    }

}
