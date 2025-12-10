package instamine.commands;

import instamine.InstamineMod;
import instamine.config.ModSettings;
import instamine.config.ModSettings.FeatureToggle;
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
        MININGSPEED,
        ATTACKRANGE,
        CRAFTRANGE,
        MOVEMENTSPEED,
        PICKUPRANGE,
        DAMAGE,
        BUILDSPEED,
        OREMULT;

        public List<FeatureToggle> toFeatures() {
            if (this == ALL) {
                return Arrays.asList(FeatureToggle.values());
            }
            switch (this) {
                case MININGSPEED:
                    return Collections.singletonList(FeatureToggle.MINING);
                case ATTACKRANGE:
                    return Collections.singletonList(FeatureToggle.RANGE);
                case CRAFTRANGE:
                    return Collections.singletonList(FeatureToggle.CRAFT);
                case MOVEMENTSPEED:
                    return Collections.singletonList(FeatureToggle.MOVEMENT);
                case PICKUPRANGE:
                    return Collections.singletonList(FeatureToggle.PICKUP);
                case DAMAGE:
                    return Collections.singletonList(FeatureToggle.COMBAT);
                case BUILDSPEED:
                    return Collections.singletonList(FeatureToggle.BUILD);
                case OREMULT:
                    return Collections.singletonList(FeatureToggle.ORE);
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
        int intArg = (Integer) args[2];
        boolean valueProvided = intArg != Integer.MIN_VALUE;

        if (mode == Mode.STATUS) {
            if (valueProvided) {
                log.add("[Instamine] Numeric value ignored for status queries.");
            }
            handleStatus(target, log);
            return;
        }

        if (mode == Mode.SET) {
            if (!valueProvided) {
                log.add("[Instamine] Missing numeric value. Usage: /instamine <feature> set <value>");
                return;
            }

            switch (target) {
                case ATTACKRANGE: {
                    int applied = ModSettings.setExtendedRangeBlocks(intArg);
                    if (applied != intArg) {
                        log.add("[Instamine] Requested range clamped to " + applied + " blocks (max " + ModSettings.getMaxRangeBlocks() + ")");
                    }
                    String message = "Extended range set to " + ModSettings.formatRangeValue() + " (1 block = 32 px)";
                    log.add("[Instamine] " + message);
                    InstamineMod.broadcast(server, message);
                    break;
                }
                case CRAFTRANGE: {
                    int applied = ModSettings.setCraftRangeBlocks(intArg);
                    if (applied != intArg) {
                        log.add("[Instamine] Storage crafting range clamped to " + applied + " blocks (allowed " + ModSettings.getMinCraftRangeBlocks() + "-" + ModSettings.getMaxCraftRangeBlocks() + ")");
                    }
                    String message = "Storage crafting range set to " + ModSettings.formatCraftRangeValue();
                    log.add("[Instamine] " + message);
                    InstamineMod.broadcast(server, message);
                    break;
                }
                case MOVEMENTSPEED: {
                    int applied = ModSettings.setMovementSpeedPercent(intArg);
                    if (applied != intArg) {
                        log.add("[Instamine] Movement speed percent clamped to " + applied + "% (allowed " + ModSettings.getMinSpeedPercent() + "-" + ModSettings.getMaxSpeedPercent() + ")");
                    }
                    String message = "Movement speed multiplier set to " + ModSettings.formatMovementSpeedValue();
                    log.add("[Instamine] " + message);
                    InstamineMod.broadcast(server, message);
                    break;
                }
                case BUILDSPEED: {
                    int applied = ModSettings.setBuildSpeedPercent(intArg);
                    if (applied != intArg) {
                        log.add("[Instamine] Building speed percent clamped to " + applied + "% (allowed " + ModSettings.getMinBuildSpeedPercent() + "-" + ModSettings.getMaxBuildSpeedPercent() + ")");
                    }
                    String message = "Building speed multiplier set to " + ModSettings.formatBuildSpeedValue();
                    log.add("[Instamine] " + message);
                    InstamineMod.broadcast(server, message);
                    break;
                }
                case PICKUPRANGE: {
                    int applied = ModSettings.setPickupRangeBlocks(intArg);
                    if (applied != intArg) {
                        log.add("[Instamine] Pickup range clamped to " + applied + " blocks (allowed " + ModSettings.getMinPickupRangeBlocks() + "-" + ModSettings.getMaxPickupRangeBlocks() + ")");
                    }
                    String message = "Pickup range set to " + ModSettings.formatPickupRangeValue();
                    log.add("[Instamine] " + message);
                    InstamineMod.broadcast(server, message);
                    break;
                }
                case DAMAGE: {
                    int applied = ModSettings.setCombatDamagePercent(intArg);
                    if (applied != intArg) {
                        log.add("[Instamine] Combat damage percent clamped to " + applied + "% (allowed " + ModSettings.getMinCombatDamagePercent() + "-" + ModSettings.getMaxCombatDamagePercent() + ")");
                    }
                    String message = "Combat damage multiplier set to " + ModSettings.formatCombatDamageValue();
                    log.add("[Instamine] " + message);
                    InstamineMod.broadcast(server, message);
                    break;
                }
                case OREMULT: {
                    int applied = ModSettings.setOreMultiplier(intArg);
                    if (applied != intArg) {
                        log.add("[Instamine] Ore multiplier clamped to " + applied + "x (allowed " + ModSettings.getMinOreMultiplier() + "-" + ModSettings.getMaxOreMultiplier() + ")");
                    }
                    String message = "Ore drop multiplier set to " + ModSettings.formatOreMultiplierValue();
                    log.add("[Instamine] " + message);
                    InstamineMod.broadcast(server, message);
                    break;
                }
                case ALL:
                    log.add("[Instamine] 'set' mode is not available for the 'all' target.");
                    break;
                default:
                    log.add("[Instamine] 'set' mode is not supported for this feature.");
                    break;
            }
            return;
        }

        if (valueProvided) {
            switch (target) {
                case ATTACKRANGE:
                    log.add("[Instamine] Blocks value ignored. Use '/instamine attackrange set <blocks>' to change range.");
                    break;
                case CRAFTRANGE:
                    log.add("[Instamine] Blocks value ignored. Use '/instamine craftrange set <blocks>' to change storage crafting range.");
                    break;
                case MOVEMENTSPEED:
                    log.add("[Instamine] Percentage ignored. Use '/instamine movementspeed set <percent>' to change movement speed.");
                    break;
                case BUILDSPEED:
                    log.add("[Instamine] Percentage ignored. Use '/instamine buildspeed set <percent>' to change building speed.");
                    break;
                case PICKUPRANGE:
                    log.add("[Instamine] Blocks value ignored. Use '/instamine pickuprange set <blocks>' to change pickup range.");
                    break;
                case DAMAGE:
                    log.add("[Instamine] Percentage ignored. Use '/instamine damage set <percent>' to change combat damage.");
                    break;
                case OREMULT:
                    log.add("[Instamine] Multiplier value ignored. Use '/instamine oremult set <multiplier>' to change ore drops.");
                    break;
                default:
                    log.add("[Instamine] Numeric value ignored for this target.");
                    break;
            }
        }

        List<FeatureToggle> features = target.toFeatures();

        for (FeatureToggle feature : features) {
            boolean current = ModSettings.isFeatureEnabled(feature);
            boolean newValue = mode == Mode.ON ? true : mode == Mode.OFF ? false : !current;
            ModSettings.setFeatureEnabled(feature, newValue);
            String message = ModSettings.formatFeatureStateMessage(feature, newValue);
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
            log.add("§#8AADF4 - " + ModSettings.describeFeatureState(feature));
        }
    }
}
