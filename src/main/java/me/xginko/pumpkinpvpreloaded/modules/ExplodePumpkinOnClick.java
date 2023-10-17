package me.xginko.pumpkinpvpreloaded.modules;

import com.destroystokyo.paper.MaterialTags;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPConfig;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public class ExplodePumpkinOnClick implements PumpkinPVPModule, Listener {

    private final PumpkinPVPReloaded plugin;
    private final RegionScheduler regionScheduler;
    private final boolean triggerOnLeftClick, triggerOnRightClick;
    private final Map<EntityDamageEvent.DamageModifier, Double> damageModifier;
    private final Map<EntityDamageEvent.DamageModifier, ? extends Function<? super Double, Double>> modifierFunctions;

    protected ExplodePumpkinOnClick() {
        this.plugin = PumpkinPVPReloaded.getInstance();
        this.regionScheduler = plugin.getServer().getRegionScheduler();
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        this.triggerOnLeftClick = config.getBoolean("mechanics.explosion-triggers.left-click-pumpkin", true);
        this.triggerOnRightClick = config.getBoolean("mechanics.explosion-triggers.right-click-pumpkin", false);
        this.damageModifier = new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, Double.MAX_VALUE));
        this.modifierFunctions = new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, Functions.constant(-0.0)));
    }

    @Override
    public boolean shouldEnable() {
        return triggerOnLeftClick || triggerOnRightClick;
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockLeftClick(PlayerInteractEvent event) {
        final Action action = event.getAction();
        if (
                (triggerOnRightClick && action.isRightClick())
                || (triggerOnLeftClick && action.isLeftClick())
        ) {
            final Block clicked = event.getClickedBlock();
            if (clicked == null || !MaterialTags.PUMPKINS.isTagged(clicked.getType())) return;

            PrePumpkinExplodeEvent prePumpkinExplodeEvent = new PrePumpkinExplodeEvent(
                    clicked,
                    event.getPlayer(),
                    clicked.getLocation().toCenterLocation()
            );

            if (!prePumpkinExplodeEvent.callEvent()) return;

            final Location explodeLoc = prePumpkinExplodeEvent.getExplodeLocation();

            regionScheduler.run(plugin, explodeLoc, kaboom -> {
                prePumpkinExplodeEvent.getPumpkin().setType(Material.AIR);

                final Player exploder = prePumpkinExplodeEvent.getExploder();
                final float power = prePumpkinExplodeEvent.getExplodePower();
                final boolean fire = prePumpkinExplodeEvent.shouldSetFire();
                final boolean breakBlocks = prePumpkinExplodeEvent.shouldBreakBlocks();

                explodeLoc.getWorld().createExplosion(explodeLoc, power, fire, breakBlocks);

                final Collection<Player> likelyDamagedPlayers = explodeLoc.getNearbyPlayers(power, power, power);

                // Try to fill in spaces for correct death info
                for (Player likelyDamagedPlayer : likelyDamagedPlayers) {
                    final EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(
                            exploder,
                            likelyDamagedPlayer,
                            EntityDamageEvent.DamageCause.MAGIC,
                            damageModifier,
                            modifierFunctions,
                            true
                    );
                    damageEvent.callEvent();
                    likelyDamagedPlayer.setLastDamageCause(damageEvent);
                    likelyDamagedPlayer.setKiller(exploder);
                }

                new PostPumpkinExplodeEvent(exploder, explodeLoc, likelyDamagedPlayers, power, fire, breakBlocks).callEvent();
            });
        }
    }
}