package cd.arnett.caddamands.cattamands.cattamand;

import cd.arnett.caddamands.cattamands.arguments.Cattarameter;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.lucko.spark.paper.lib.protobuf.ExperimentalApi;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public abstract class Cattamand {

    //region Abstract Properties

    /*=================================================================================================
                    -  Abstract Properties  -
    =================================================================================================*/

        /**
         * Defines the execution of the command
         * @param context command context
         * @return success/fail (see Command.SingleSuccess)
         */
        public abstract int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException;

        /**
         * @return List of all the children for this cattamand
         */
        public abstract List<? extends Cattamand> getChildren();

        /**
         * @return List of Arguments this command requires
         */
        public abstract List<? extends Cattarameter> getArguments();

        /**
         * @return Name of the command
         */
        public abstract String getName();

        /**
         * @return String Permission required to use the command, see hasPermission for more detail
         */
        public abstract String getPermission();

        /**
         * @return List of String Aliases for this cattamand
         */
        public abstract List<String> getAliases();

    //endregion


    //region Optional Properties

    /*=================================================================================================
                    -  Optional Properties  -
    =================================================================================================*/

    /**
     * Helper to determine whether this command does an execution
     * @return Whether this command executes anything
     */
    public boolean doExecute()
    {
        return true;
    }

    /**
     * @return Optional Redirect (NOT TESTED)
     */
    @ExperimentalApi
    public Cattamand getRedirect()
    {
        return null;
    }

    /**
     * @return description of what the command does for documentation, which means optional :)
     */
    public String getDescription()
    {
        return "";
    }

    //endregion


    //region Permissions

    /*=================================================================================================
                    -  Permissions  -
    =================================================================================================*/


    /**
     * gets the permission predicate, defaults to FALSE result if not set/overridden
     * @return this
     */
    public Predicate<CommandSourceStack> getPermissionPredicate()
    {
        return (CommandSourceStack css) -> false;
    }


    /**
     * Does the user have permission to run this command?
     * First check the permission string and if not allowed
     * checks the permission predicate if applicable ()
     * @param css Command source stack
     * @return true / false
     */
    public boolean hasPermission(CommandSourceStack css)
    {
        //if permission is "op" that is a special case so we can check that here
        if(switch (getPermission().toLowerCase())
        {
            //op check
            case "op", "" -> css.getSender().isOp();
            //no permission needed
            case "none", "no", "n/a" -> true;
            //regular permission check
            default -> css.getSender().hasPermission(getPermission());
        })
        {
            return true;
        }

        //then predicate check to see if they meet that, default to false
        if(getPermissionPredicate() != null)
            return getPermissionPredicate().test(css);

        //there is no predicate to test (the user just wants a string permission check which already failed)
        else
            return false;
    }

    //endregion


    //region Building Brigadier Nodes

    /*=================================================================================================
                    -  Building Brigadier Nodes  -
    =================================================================================================*/


    /**
     * Builds the commmand for use in Brigadier API
     * (ex// Commands.literal("rootcmd").then(new LeadCommand().getCommand())...)
     * @return the cattamand to a brigader node
     */
    public final LiteralArgumentBuilder<CommandSourceStack> toArgBuilder()
    {
        // check permissions
        var rootCommand = Commands.literal(getName()).requires(this::hasPermission);

        //grab args once in case someone's overridden it to make there be some logic run here
        List<? extends Cattarameter> arguments = getArguments();

        //set the redirection then exit because it shouldn't have any args or children or executor
        if(getRedirect() != null)
        {
            rootCommand.redirect(getRedirect().toArgBuilder().build());
            return rootCommand;
        }

        //add the main arg/executor
        attachArgsExecution(rootCommand);

        return rootCommand;
    }


    /**
     * Attaches the arguments and Execution to a root Brigader command node,
     * Children are then attached to the LAST argument (or root node)
     * @param rootCommand Root command node
     */
    void attachArgsExecution(LiteralArgumentBuilder<CommandSourceStack> rootCommand)
    {
        List<? extends Cattarameter> arguments = getArguments();
        Command<CommandSourceStack> executor = doExecute()? this::execute : null;
        boolean rootExecutionApplied = false;
        boolean childrenApplied = false;

        //used to help build command right to left
        ArgumentBuilder<CommandSourceStack, ?> argSnake = null;

        //reverse loop through arguments
        for(int i = arguments.size() - 1; i >= 0; i--)
        {
            //grab the next argument
            var arg = Commands.argument(arguments.get(i).getName(), arguments.get(i).getType());

            //if there was a node after this then tail it to this
            if (argSnake != null)
            {
                arg.then(argSnake);
            }
            else
            {
                //this is the last argument in the tail so attach the children on here if present
                addChildrenToNode(arg);
                childrenApplied = true;
            }

            //fill suggestions
            Cattarameter currentArgument = arguments.get(i);
            if(currentArgument != null)
            {
                arg.suggests((context, builder) -> {

                    //create list with custom suggestions
                    currentArgument.getSuggestions(context).forEach((suggestion, hoverText) -> {
                        //adds the suggestion and the hover text associated with it
                        builder.suggest(suggestion, MessageComponentSerializer.message().serialize(
                                MiniMessage.miniMessage().deserialize(hoverText)
                        ));
                    });

                    if(currentArgument.doDefaultSuggestions())
                    {
                        //add default to list
                        currentArgument.getType().listSuggestions(context, builder).thenAccept(suggestions -> {
                            suggestions.getList().forEach(suggestion -> {
                                builder.suggest(suggestion.getText(), suggestion.getTooltip());
                            });
                        });
                    }

                    //return the default suggestions if we want or non if we only want what we have set
                    return builder.buildFuture();
                });
            }

            //fill execution if empty otherwise apply it's execution and save the commands execution for an earlier argument
            if(currentArgument.getExecutes() != null)
            {

                //apply the arguments execution
                arg.executes(currentArgument.getExecutes());
            }
            else if (!rootExecutionApplied)
            {
                //apply the root execution
                arg.executes(executor);
                rootExecutionApplied = true;
            }
            else
            {
                //bad syntax so throw the syntax at them and tell them to do better
                arg.executes(getSyntaxErrorMessage());
            }

            //if we are at the last argument then attach it to the root
            //otherwise just move the snake up the tree
            if(i == 0)
            {
                rootCommand.then(arg);
            }
            else
            {
                argSnake = arg;
            }
        }

        if(!rootExecutionApplied)
        {
            rootCommand.executes(executor);
        }
        else
        {
            rootCommand.executes(getSyntaxErrorMessage());
        }

        if(!childrenApplied)
        {
            //if there were no args tail, add the children to the root since it is the last in the chain
            addChildrenToNode(rootCommand);
        }
    }

    /**
     * Attaches the children of this Cattamand to a Brigader command node,
     * Children are attached AFTER Arguments
     * @param parent Parent command node
     */
    void addChildrenToNode(ArgumentBuilder<CommandSourceStack, ?> parent)
    {
        List<? extends Cattamand> children = getChildren();

        //go through each child and add it to the command
        for(int i = children.size() - 1; i >= 0; i--)
        {
            LiteralCommandNode<CommandSourceStack> node = children.get(i).toArgBuilder().build();

            //add that command to the tree under this one
            parent.then(node);

            //register any of the children's aliases
            for(String alias : children.get(i).getAliases())
            {
                //get the builder for the alias node
                ArgumentBuilder<CommandSourceStack, ?> aliasBuilder = Commands.literal(alias).redirect(node).requires(this::hasPermission);

                //if the alias has an executor attach that since sometimes this can cause an error
                //(which was fun having to figure out)
                if(node.getCommand() != null)
                    aliasBuilder.executes(node.getCommand());

                parent.then(aliasBuilder);
            }
        }
    }

    //endregion


    //region Command Registration

    /*=================================================================================================
                    -  Command Registration  -
    =================================================================================================*/

    /**
     * Registers ONLY this Cattamand as a root command with its children as branches/leaves, nothing above it in the tree
     * @param plugin the owning plugin of the command
     * @return The compiled command node (usually not important)
     */
    public LiteralCommandNode<CommandSourceStack> registerAsRoot(JavaPlugin plugin)
    {
        registerAsRoot(plugin, 0);
        return toArgBuilder().build();
    }

    /**
     * Registers ONLY this Cattamand as a root command with its children as branches/leaves, nothing above it in the tree
     * @param plugin the owning plugin of the command
     * @param priority the priority to register the command with
     */
    //registers ONLY this command, nothing above it in the tree
    public void registerAsRoot(JavaPlugin plugin, int priority)
    {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(cmd -> {
            registerAsRoot(plugin, cmd);
        }).priority(priority));
    }

    /**
     * Registers ONLY this Cattamand as a root command with its children as branches/leaves, nothing above it in the tree
     * @param plugin the owning plugin of the command
     * @param cmd the Registrar event to register the command with
     */
    public LiteralCommandNode<CommandSourceStack> registerAsRoot(JavaPlugin plugin, ReloadableRegistrarEvent<Commands> cmd)
    {
        plugin.getLogger().info("registering " + this.getName() + " command");

        var node = toArgBuilder().build();

        cmd.registrar().register(node);

        //register any of it's aliases
        getAliases().forEach(alias -> {
            //get the builder for the alias node
            LiteralArgumentBuilder<CommandSourceStack> aliasBuilder = Commands.literal(alias).redirect(node).requires(this::hasPermission);

            //if the alias has an executor attach that since sometimes this can cause an error
            //(which was fun having to figure out)
            if(node.getCommand() != null)
                aliasBuilder.executes(node.getCommand());

            cmd.registrar().register(aliasBuilder.build());
        });

        if(!Bukkit.getOnlinePlayers().isEmpty())
        {
            Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        }

        return node;
    }

    //endregion


    //region Syntax

    /*=================================================================================================
                    -  Syntax  -
    =================================================================================================*/

    /**
     * @return syntax of how to use the command (for documentation), auto generated
     */
    public String getSyntax()
    {
        StringBuilder out = new StringBuilder("/");

        out.append(getName()).append(" ");

        //go through each argument and add it to the command
        getArguments().forEach(argumentData -> {
            if(argumentData.getExecutes() != null)
            {
                //label it as executable
                out
                        .append('[')
                        .append(argumentData.getName())
                        .append(']')
                        .append(" ");
            }
            else
            {
                out.append(argumentData.getName()).append(" ");
            }
        });

        return out.toString();
    }

    /**
     * Returns an auto generated Syntax error message command execution
     * @return Command execution to send player error message
     */
    Command<CommandSourceStack> getSyntaxErrorMessage()
    {
        return context -> {//compile parameters
            StringBuilder builder = new StringBuilder("/... ");
            builder.append(getName()).append(" ");

            getArguments().forEach(argument -> {
                builder.append("<").append(argument.getName()).append("> ");
            });

            context.getSource().getSender().sendMessage(
                    MiniMessage.miniMessage().deserialize(
                            "<red><bold>Syntax Error: </bold>" + builder.toString() + "</red>"
                    )
            );

            return 1;
        };
    }

    //endregion
}
