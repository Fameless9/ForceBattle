package net.fameless.forcebattle.command;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.command.framework.CallerType;
import net.fameless.forcebattle.command.framework.Command;
import net.fameless.forcebattle.command.framework.CommandCaller;
import net.fameless.forcebattle.gui.LanguageGUI;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.StringUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LanguageCommand extends Command {

    private final LanguageGUI languageGUI;

    public LanguageCommand() {
        super(
                "language",
                List.of("lang"),
                CallerType.NONE,
                "/lang <identifier>",
                "forcebattle.lang",
                "Command to change the language of the plugin"
        );

        this.languageGUI = ForceBattle.get().getLanguageGUI();
    }

    @Override
    public void executeCommand(@NotNull CommandCaller caller, String @NotNull [] args) {
        if (args.length == 0) {
            if (!(caller instanceof BattlePlayer battlePlayer)) {
                caller.sendMessage(Caption.of(CallerType.CONSOLE.getErrorMessageKey()));
                return;
            }
            battlePlayer.openInventory(languageGUI.getLanguageGUI());
        } else {
            net.fameless.forcebattle.caption.Language newLanguage = net.fameless.forcebattle.caption.Language.ofIdentifier(args[0]);
            if (newLanguage != null && !newLanguage.equals(Caption.getCurrentLanguage())) {
                Caption.setCurrentLanguage(newLanguage);
                ForceBattle.broadcast(MiniMessage.miniMessage().deserialize(Caption.getCurrentLanguage().getUpdateMessage(), Caption.prefixTagResolver()));
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
