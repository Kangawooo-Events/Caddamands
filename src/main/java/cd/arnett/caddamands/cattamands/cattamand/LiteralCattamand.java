package cd.arnett.caddamands.cattamands.cattamand;

import cd.arnett.caddamands.cattamands.arguments.Cattarameter;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.lucko.spark.paper.lib.protobuf.ExperimentalApi;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class LiteralCattamand extends Cattamand {


    //region Properties

    /*=================================================================================================
                    -  Properties  -
    =================================================================================================*/

        String name;
        String permission;
        Predicate<CommandSourceStack> permissionPredicate;
        Command<CommandSourceStack> executes;
        List<? extends Cattarameter> arguments;
        List<? extends Cattamand> children;
        Cattamand redirect = null;
        List<String> aliases = List.of();

    //endregion


    //region Builder

    /*=================================================================================================
                    -  Builder  -
    =================================================================================================*/

    public static class Builder
    {
        String name;
        String permission = "";
        Predicate<CommandSourceStack> permissionPredicate = null;
        Command<CommandSourceStack> executes = null;
        List<? extends Cattarameter> arguments = List.of();
        List<? extends Cattamand> children = new ArrayList<>(2);
        Cattamand redirect = null;
        List<String> aliases = List.of();

        /**
         * Creates a Cattamand Builder with the name provided
         * @param name The new Name
         * @return builder, defaults to op check for permissions
         */
        public Builder(String name)
        {
            this.name = name;
        }

        /**
         * Sets the String permission required to run this command
         * @param permission The required String Permission
         * @return this
         */
        public Builder permission(String permission)
        {
            this.permission = permission;
            return this;
        }

        /**
         * Sets the permission predicate
         * @param permissionPredicate the test predicate (must be in
         *                      "boolean foo(CommandSourceStack context)"
         *                            format)
         * @return this
         */
        public Builder permissionPredicate(Predicate<CommandSourceStack> permissionPredicate)
        {
            this.permissionPredicate = permissionPredicate;
            return this;
        }

        /**
         * Sets the execution function for this command
         * @param executes Execution function (must be in
         *                 "int foo(CommandContext&ltCommandSourceStack&gt context) throws CommandSyntaxException"
         *                 format)
         * @return this
         */
        public Builder executes(Command<CommandSourceStack> executes)
        {
            this.executes = executes;
            return this;
        }

        /**
         * Sets the Argument nodes for this Cattamand
         * @param arguments List of Arguments
         * @return this
         */
        public Builder argument(List<? extends Cattarameter> arguments)
        {
            this.arguments = arguments;
            return this;
        }

        /**
         * Sets the child nodes of the command
         * @param children List of Child Cattamands
         * @return this
         */
        public Builder children(List<? extends Cattamand> children)
        {
            this.children = new ArrayList<>(children);
            return this;
        }

        /**
         * Sets the redirect (this has not been tested thoroughly and atm is mainly used for aliases)
         * @param redirect Cattamand to redirect to
         * @return this
         */
        public Builder redirect(Cattamand redirect)
        {
            this.redirect = redirect;
            return this;
        }

        /**
         * Sets the list of String aliases this command can be called by
         * @param aliases List of aliasees
         * @return this
         */
        public Builder aliases(List<String> aliases)
        {
            this.aliases = aliases;
            return this;
        }

        /**
         * Builds the builder into a Cattamand
         * @return built Cattamand, if Permission has not been set, defaults to an op check
         */
        public LiteralCattamand build()
        {
            return new LiteralCattamand(this);
        }
    }

    public LiteralCattamand(Builder builder)
    {
        name = builder.name;
        permission = builder.permission;
        permissionPredicate = builder.permissionPredicate;
        executes = builder.executes;
        arguments = builder.arguments;
        children = builder.children;
        redirect = builder.redirect;
        aliases = builder.aliases;
    }

    //endregion


    //region Constructors

    /*=================================================================================================
                    -  Constructors  -
    =================================================================================================*/

    public LiteralCattamand(String name)
    {
        this(name, "", List.of(), null, List.of());
    }

    public LiteralCattamand(String name, List<? extends Cattamand> children)
    {
        this(name, "", List.of(), null, children);
    }

    public LiteralCattamand(String name, final Command<CommandSourceStack> executes)
    {
        this(name, "", List.of(), executes, List.of());
    }

    //with perms
    public LiteralCattamand(String name, String permission, final Command<CommandSourceStack> executes)
    {
        this(name, permission, List.of(), executes, List.of());
    }

    //with children
    public LiteralCattamand(String name, String permission, List<? extends Cattamand> children)
    {
        this(name, permission, List.of(), null, children);
    }

    public LiteralCattamand(String name, final Command<CommandSourceStack> executes, List<? extends Cattamand> children)
    {
        this(name, "", List.of(), executes, children);
    }

    public LiteralCattamand(String name, String permission, final Command<CommandSourceStack> executes, List<? extends Cattamand> children)
    {
        this(name, permission, List.of(), executes, children);
    }

    //with predicate
    public LiteralCattamand(String name, Predicate<CommandSourceStack> permission)
    {
        this(name, permission, List.of(), null, List.of());
    }

    public LiteralCattamand(String name, Predicate<CommandSourceStack> permission, final List<? extends Cattarameter> arguments)
    {
        this(name, permission, arguments, null, List.of());
    }

    public LiteralCattamand(String name, Predicate<CommandSourceStack> permission, final Command<CommandSourceStack> executes)
    {
        this(name, permission, List.of(), executes, List.of());
    }

    public LiteralCattamand(String name, Predicate<CommandSourceStack> permission, final List<? extends Cattarameter> arguments, final Command<CommandSourceStack> executes)
    {
        this(name, permission, arguments, executes, List.of());
    }

    //with arguments
    public LiteralCattamand(String name, final List<? extends Cattarameter> arguments, final Command<CommandSourceStack> executes, List<? extends Cattamand> children)
    {
        this(name, "", arguments, executes, children);
    }

    public LiteralCattamand(String name, final List<? extends Cattarameter> arguments, final Command<CommandSourceStack> executes)
    {
        this(name, "", arguments, executes, List.of());
    }

    public LiteralCattamand(String name, String permission, final List<? extends Cattarameter> arguments, final Command<CommandSourceStack> executes)
    {
        this(name, permission, arguments, executes, List.of());
    }

    //with predicate
    public LiteralCattamand(String name, Predicate<CommandSourceStack> permission, final List<? extends Cattarameter> arguments, final Command<CommandSourceStack> executes, List<? extends Cattamand> children)
    {
        this.name = name;
        this.permissionPredicate = permission;
        this.executes = executes;
        this.arguments = arguments;
        this.children = children;
    }

    //with string permission
    public LiteralCattamand(String name, String permission, final List<? extends Cattarameter> arguments, final Command<CommandSourceStack> executes, List<? extends Cattamand> children)
    {
        this.name = name;
        this.permission = permission;
        this.executes = executes;
        this.arguments = arguments;
        this.children = children;
    }

    //endregion


    //region Execution

    /*=================================================================================================
                    -  Execution  -
    =================================================================================================*/

    /**
     * Runs the command execution
     * @param context Command Source Stack
     * @return success / fail (used by minecraft, see Command.SingleSuccess)
     * @throws CommandSyntaxException
     */
    @Override
    public int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return executes.run(context);
    }

    /**
     * @return Whether this has an executing command or not (in which case it is likely a branch/root of a command tree)
     */
    @Override
    public boolean doExecute()
    {
        return executes != null;
    }

    //endregion


    //region Setters

    /*=================================================================================================
                    -  Setters  -
    =================================================================================================*/

    /**
     * Sets the name of the Cattamand
     * @param name The new Name
     * @return this
     */
    public Cattamand setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the String permission required to run this command
     * @param permission The required String Permission
     * @return this
     */
    public Cattamand setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    /**
     * Sets the permission predicate
     * @param permissionPredicate the test predicate (must be in
     *                      "boolean foo(CommandSourceStack context)"
     *                            format)
     * @return this
     */
    public Cattamand setPermissionPredicate(Predicate<CommandSourceStack> permissionPredicate) {
        this.permissionPredicate = permissionPredicate;
        return this;
    }

    /**
     * Sets the execution function for this command
     * @param executes Execution function (must be in
     *                 "int foo(CommandContext&ltCommandSourceStack&gt context) throws CommandSyntaxException"
     *                 format)
     * @return this
     */
    public Cattamand setExecutes(Command<CommandSourceStack> executes) {
        this.executes = executes;
        return this;
    }

    /**
     * Sets the Argument nodes for this Cattamand
     * @param args List of Arguments
     * @return this
     */
    public Cattamand setArguments(List<? extends Cattarameter> args) {
        this.arguments = args;
        return this;
    }

    /**
     * Sets the child nodes of the command
     * @param children List of Child Cattamands
     * @return this
     */
    public Cattamand setChildren(List<? extends Cattamand> children) {
        this.children = children;
        return this;
    }

    /**
     * Sets the redirect (this has not been tested thoroughly and atm is mainly used for aliases)
     * @param redirect Cattamand to redirect to
     * @return this
     */
    @ExperimentalApi
    public Cattamand setRedirect(Cattamand redirect) {
        this.redirect = redirect;
        return this;
    }

    /**
     * Sets the list of String aliases this command can be called by
     * @param aliases List of aliasees
     * @return this
     */
    public Cattamand setAliases(List<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    //endregion


    //region Getters

    /*=================================================================================================
                    -  Getters  -
    =================================================================================================*/

    /**
     * @return List of the child Cattamands
     */
    @Override
    public List<? extends Cattamand> getChildren() {
        return children;
    }

    /**
     * @return Redirect Cattamand if one has been set, otherwise NULL
     */
    @Override
    public Cattamand getRedirect()
    {
        return redirect;
    }

    /**
     * @return List of arguments for the command in Cattarmaeter format
     */
    @Override
    public List<? extends Cattarameter> getArguments() {
        return arguments;
    }

    /**
     * @return Name of the Cattamand
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return Basic Permission string required by this command, defaults to an empty string (which does an op check)
     */
    @Override
    public String getPermission() {
        return permission;
    }

    /**
     * @return List of String Aliases which this command uses
     */
    @Override
    public List<String> getAliases() {
        return aliases;
    }

    //endregion


}
