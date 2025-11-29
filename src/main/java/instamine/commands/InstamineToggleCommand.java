package instamine.commands;

import instamine.InstamineMod;
import instamine.InstamineMod.FeatureToggle;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.EnumParameterHandler;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class InstamineToggleCommand extends ModularChatCommand {

    private enum Target {
        ALL,
        MINING,
        RANGE,
        COMBAT,
        ORE;

        public List<FeatureToggle> toFeatures() {
            if (this == ALL) {
                return Arrays.asList(FeatureToggle.values());
            }
            switch (this) {
                case MINING:
                    return Collections.singletonList(FeatureToggle.MINING);
                case RANGE:
                    return Collections.singletonList(FeatureToggle.RANGE);
                case COMBAT:
                    return Collections.singletonList(FeatureToggle.COMBAT);
                case ORE:
                    return Collections.singletonList(FeatureToggle.ORE);
                default:
                    throw new IllegalArgumentException("Unhandled target: " + this);
            }
        }

        public String displayName() {
            switch (this) {
                case ALL:
                    return "All features";
                case MINING:
                    return FeatureToggle.MINING.getDisplayName();
                case RANGE:
                    return FeatureToggle.RANGE.getDisplayName();
                case COMBAT:
                    return FeatureToggle.COMBAT.getDisplayName();
                case ORE:
                    return FeatureToggle.ORE.getDisplayName();
                default:
                    throw new IllegalArgumentException("Unhandled target: " + this);
            }
        }
    }

    private enum Mode {
        ON,
        OFF,
        TOGGLE,
        STATUS,
        SET;

        public static Mode fromString(String value) {
            if (value == null || value.isEmpty()) {
                return TOGGLE;
            }
            switch (value.toLowerCase(Locale.ENGLISH)) {
                case "on":
                case "enable":
                case "enabled":
                    return ON;
                case "off":
                case "disable":
                case "disabled":
                    return OFF;
                case "set":
                case "value":
                case "blocks":
                    return SET;
                case "status":
                case "state":
                    return STATUS;
                case "toggle":
                default:
                    return TOGGLE;
            }
        }
    }

    public InstamineToggleCommand() {
        super(
            "instamine",
            "Toggle Instamine mod features",
            PermissionLevel.USER,
            false,
            new CmdParameter("feature", new EnumParameterHandler<>(Target.ALL, Target.values()), false),
            new CmdParameter("mode", new PresetStringParameterHandler("toggle", "on", "off", "status", "set"), true),
            new CmdParameter("value", new IntParameterHandler(Integer.MIN_VALUE), true)
        );
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog log) {
        Target target = (Target) args[0];
        Mode mode = Mode.fromString((String) args[1]);
        int rangeBlocksArg = (Integer) args[2];
        boolean valueProvided = rangeBlocksArg != Integer.MIN_VALUE;

        if (mode == Mode.STATUS) {
            if (valueProvided) {
                log.add("[Instamine] Blocks value ignored for status queries.");
            }
            handleStatus(target, log);
            return;
        }

        if (mode == Mode.SET) {
            if (target != Target.RANGE) {
                log.add("[Instamine] 'set' mode can only be used with the range feature.");
                return;
            }

            if (!valueProvided) {
                log.add("[Instamine] Missing blocks value. Usage: /instamine range set <blocks>");
                return;
            }

            int applied = InstamineMod.setExtendedRangeBlocks(rangeBlocksArg);
            if (applied != rangeBlocksArg) {
                log.add("[Instamine] Requested range clamped to " + applied + " blocks (max " + InstamineMod.getMaxRangeBlocks() + ")");
            }

            String message = "Extended range set to " + InstamineMod.formatRangeValue();
            log.add("[Instamine] " + message + " (1 block = 32 px)");
            InstamineMod.broadcast(server, message);
            return;
        }

        if (valueProvided) {
            if (target != Target.RANGE) {
                log.add("[Instamine] Blocks value is only applicable when adjusting the range feature.");
                return;
            }

            log.add("[Instamine] Blocks value ignored. Use '/instamine range set <blocks>' to change range.");
        }

        List<FeatureToggle> features = target.toFeatures();

        for (FeatureToggle feature : features) {
            boolean current = InstamineMod.isFeatureEnabled(feature);
            boolean newValue = mode == Mode.ON ? true : mode == Mode.OFF ? false : !current;
            InstamineMod.setFeatureEnabled(feature, newValue);
            String message = InstamineMod.formatFeatureStateMessage(feature, newValue);
            log.add("[Instamine] " + message);
            InstamineMod.broadcast(server, message);
        }
    }

    private void handleStatus(Target target, CommandLog log) {
        EnumSet<FeatureToggle> features = target == Target.ALL
            ? EnumSet.allOf(FeatureToggle.class)
            : EnumSet.copyOf(target.toFeatures());

        log.add("§#8AADF4[Instamine] Current feature states:");
        for (FeatureToggle feature : features) {
            log.add("§#8AADF4 - " + feature.describeWithState());
        }
    }
}
