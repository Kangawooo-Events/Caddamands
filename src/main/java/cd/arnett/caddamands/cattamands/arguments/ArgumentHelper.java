package cd.arnett.caddamands.cattamands.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class ArgumentHelper {
    public static List<Player> getPlayersFromArgs(String argName, CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException
    {
        return ctx.getArgument(argName, PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
    }

    /**
     *
     * Gets the world the sender is in or throws a command syntax error and sends the error message if not sent from a
     * player or block
     *
     * @param ctx context of the command
     * @return world the sender is in
     */
    public static World getWorldOfSender(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();

        if((sender instanceof Player player))
        {
            return player.getWorld();
        }
        else if((sender instanceof BlockCommandSender block))
        {
            return block.getBlock().getWorld();
        }
        else
        {
            throw new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(
                    Component.text("Must be sent by a player or use the world parameter", NamedTextColor.RED)
            )).create();
        }
    }

    public static Player getPlayerSender(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();

        if((sender instanceof Player player))
        {
            return player;
        }
        else
        {
            throw new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(
                    Component.text("Must be sent by a player", NamedTextColor.RED)
            )).create();
        }
    }
}
