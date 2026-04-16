package cd.arnett.caddamands.cattamands.cattamand;

import cd.arnett.caddamands.cattamands.arguments.Cattarameter;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class LiteralCattamand extends Cattamand {

    String name;
    String permission;
    Predicate<CommandSourceStack> permissionPredicate;
    Command<CommandSourceStack> executes;
    List<Cattarameter> arguments;
    List<? extends Cattamand> children;
    Cattamand redirect = null;
    List<String> aliases = List.of();

    public static class Builder
    {
        String name;
        String permission = "";
        Predicate<CommandSourceStack> permissionPredicate = null;
        Command<CommandSourceStack> executes = null;
        List<Cattarameter> arguments = List.of();
        List<Cattamand> children = new ArrayList<>(2);
        Cattamand redirect = null;
        List<String> aliases = List.of();

        public Builder(String name)
        {
            this.name = name;
        }

        public Builder permission(String permission)
        {
            this.permission = permission;
            return this;
        }

        public Builder permissionPredicate(Predicate<CommandSourceStack> permissionPredicate)
        {
            this.permissionPredicate = permissionPredicate;
            return this;
        }

        public Builder executes(Command<CommandSourceStack> executes)
        {
            this.executes = executes;
            return this;
        }

        public Builder argument(List<Cattarameter> arguments)
        {
            this.arguments = arguments;
            return this;
        }

        public Builder children(List<? extends Cattamand> children)
        {
            this.children = new ArrayList<>(children);
            return this;
        }

        public Builder redirect(Cattamand redirect)
        {
            this.redirect = redirect;
            return this;
        }

        public Builder aliases(List<String> aliases)
        {
            this.aliases = aliases;
            return this;
        }

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

    public LiteralCattamand(String name, Predicate<CommandSourceStack> permission, final List<Cattarameter> arguments)
    {
        this(name, permission, arguments, null, List.of());
    }

    public LiteralCattamand(String name, Predicate<CommandSourceStack> permission, final Command<CommandSourceStack> executes)
    {
        this(name, permission, List.of(), executes, List.of());
    }

    public LiteralCattamand(String name, Predicate<CommandSourceStack> permission, final List<Cattarameter> arguments, final Command<CommandSourceStack> executes)
    {
        this(name, permission, arguments, executes, List.of());
    }

    //with arguments
    public LiteralCattamand(String name, final List<Cattarameter> arguments, final Command<CommandSourceStack> executes, List<? extends Cattamand> children)
    {
        this(name, "", arguments, executes, children);
    }

    public LiteralCattamand(String name, final List<Cattarameter> arguments, final Command<CommandSourceStack> executes)
    {
        this(name, "", arguments, executes, List.of());
    }

    public LiteralCattamand(String name, String permission, final List<Cattarameter> arguments, final Command<CommandSourceStack> executes)
    {
        this(name, permission, arguments, executes, List.of());
    }

    //with predicate
    public LiteralCattamand(String name, Predicate<CommandSourceStack> permission, final List<Cattarameter> arguments, final Command<CommandSourceStack> executes, List<? extends Cattamand> children)
    {
        this.name = name;
        this.permissionPredicate = permission;
        this.executes = executes;
        this.arguments = arguments;
        this.children = children;
    }

    //with string permission
    public LiteralCattamand(String name, String permission, final List<Cattarameter> arguments, final Command<CommandSourceStack> executes, List<? extends Cattamand> children)
    {
        this.name = name;
        this.permission = permission;
        this.executes = executes;
        this.arguments = arguments;
        this.children = children;
    }

    public Cattamand setName(String name) {
        this.name = name;
        return this;
    }

    public Cattamand setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public Cattamand setPermissionPredicate(Predicate<CommandSourceStack> permissionPredicate) {
        this.permissionPredicate = permissionPredicate;
        return this;
    }

    public Cattamand setExecutes(Command<CommandSourceStack> executes) {
        this.executes = executes;
        return this;
    }

    public Cattamand setArgs(List<Cattarameter> args) {
        this.arguments = args;
        return this;
    }

    public Cattamand setChildren(List<Cattamand> children) {
        this.children = children;
        return this;
    }

    public Cattamand setRedirect(Cattamand redirect) {
        this.redirect = redirect;
        return this;
    }

    public Cattamand setAliases(List<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return executes.run(context);
    }

    @Override
    public boolean doExecute()
    {
        return executes != null;
    }

    @Override
    public List<? extends Cattamand> getChildren() {
        return children;
    }
    @Override
    public Cattamand getRedirect()
    {
        return redirect;
    }

    @Override
    public List<Cattarameter> getArguments() {
        return arguments;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public boolean hasPermission(CommandSourceStack css) {
        //first regular permission check
        if(!super.hasPermission(css))
            return false;

        //then predicate check
        if(permissionPredicate != null)
            return permissionPredicate.test(css);

        //there is no predicate to test (the user just wants a string permission check)
        else
            return true;
    }
}
