package net.fameless.core.command;

import net.fameless.core.ForceBattle;
import net.fameless.core.caption.Caption;
import net.fameless.core.command.framework.CallerType;
import net.fameless.core.command.framework.Command;
import net.fameless.core.command.framework.CommandCaller;
import net.fameless.core.util.StringUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Language extends Command {

    public Language() {
        super(
                "language",
                List.of("lang"),
                CallerType.NONE,
                "/lang <identifier>",
                "forcebattle.lang",
                "Command to change the language of the plugin"
        );
    }

    @Override
    public void executeCommand(@NotNull CommandCaller caller, String @NotNull [] args) {
        if (args.length > 0) {
            net.fameless.core.caption.Language newLanguage = net.fameless.core.caption.Language.ofIdentifier(args[0]);
            if (newLanguage != null && !newLanguage.equals(Caption.getCurrentLanguage())) {
                Caption.setCurrentLanguage(newLanguage);
                ForceBattle.platform().broadcast(MiniMessage.miniMessage().deserialize(Caption.getCurrentLanguage().getUpdateMessage(), Caption.prefixTagResolver()));
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String @NotNull [] args) {
        List<String> languages = Arrays.stream(net.fameless.core.caption.Language.values()).map(net.fameless.core.caption.Language::getIdentifier).toList();
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], languages, new ArrayList<>());
        }
        return List.of();
    }

}
