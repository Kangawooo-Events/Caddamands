package cd.arnett.caddamands.cattamands.arguments;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class ArgumentHelper {

    //region Resolving Helpers

    /*=================================================================================================
                    -  Resolving Helpers  -
    =================================================================================================*/

    /**
     *
     * Easily resolves a list of players from a provided "Players" argument
     * @param argName name of the arguemnt to resolve
     * @param ctx context of the command
     * @return List of players
     */
    public static List<Player> getPlayersFromArgs(String argName, CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException
    {
        return ctx.getArgument(argName, PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
    }

    /**
     *
     * Easily resolves a player from a provided "Player" argument
     * @param argName name of the arguemnt to resolve
     * @param ctx context of the command
     * @return Player
     */
    public static Player getPlayerFromArgs(String argName, CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException
    {
        return ctx.getArgument(argName, PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
    }

    /**
     *
     * Easily resolves a list of player profiles from a provided "PlayerProfile" argument
     * @param argName name of the arguemnt to resolve
     * @param ctx context of the command
     * @return List of Player Profiles
     */
    public static Collection<PlayerProfile> getPlayerProfilesFromArgs(String argName, CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException
    {
        return ctx.getArgument(argName, PlayerProfileListResolver.class).resolve(ctx.getSource());
    }

    //endregion


    //region Sender Helpers

    /*=================================================================================================
                    -  Sender Helpers  -
    =================================================================================================*/

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



    /**
     *
     * Gets the player who sent this command or throws a command syntax error and sends the error message if not sent from a
     * player
     * @param ctx context of the command
     * @return sending player
     */
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

    /**
     * Sends the sender a minimessage
     * @param message message to send sender
     * @throws CommandSyntaxException
     */
    public static void sendSenderMiniMessage(CommandContext<CommandSourceStack> ctx, String message) {
        CommandSender sender = ctx.getSource().getSender();
        sender.sendMessage(MiniMessage.miniMessage().deserialize(message));
    }

    //endregion

}
