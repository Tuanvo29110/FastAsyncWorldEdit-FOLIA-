/*
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.sk89q.worldedit.extension.platform;

import com.fastasyncworldedit.core.extent.processor.PlacementStateProcessor;
import com.fastasyncworldedit.core.extent.processor.lighting.Relighter;
import com.fastasyncworldedit.core.extent.processor.lighting.RelighterFactory;
import com.fastasyncworldedit.core.queue.IBatchProcessor;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.io.ResourceLoader;
import com.sk89q.worldedit.world.DataFixer;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.Registries;
import org.enginehub.piston.CommandManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Represents a platform that WorldEdit has been implemented for.
 *
 * <p>It is strongly recommended that implementations extend from
 * {@link AbstractPlatform}.</p>
 */
public interface Platform extends Keyed {

    /**
     * Return the resource loader.
     *
     * @return The resource loader
     */
    ResourceLoader getResourceLoader();

    /**
     * Gets the registry holder.
     *
     * @return The registry holder
     */
    Registries getRegistries();

    /**
     * Gets the Minecraft data version being used by the platform.
     *
     * @return the data version
     */
    int getDataVersion();

    /**
     * Get a DataFixer capable of upgrading old data.
     *
     * @return a data fixer, or null if not supported by this platform
     */
    DataFixer getDataFixer();

    /**
     * Checks if a mob type is valid.
     *
     * @param type The mob type name to check
     * @return Whether the name is a valid mod type
     */
    boolean isValidMobType(String type);

    /**
     * Reload WorldEdit configuration.
     */
    void reload();

    /**
     * Schedules the given {@code task} to be invoked once every
     * {@code period} ticks after an initial delay of {@code delay} ticks.
     *
     * @param delay  Delay in server ticks before executing first repeat
     * @param period Period in server ticks of the task
     * @param task   Task to be executed
     * @return Task id number (-1 if scheduling failed)
     */
    int schedule(long delay, long period, Runnable task);

    /**
     * Get the watchdog service.
     *
     * @return the watchdog service, or {@code null} if none
     */
    @Nullable
    default Watchdog getWatchdog() {
        return null;
    }

    /**
     * Get a list of available or loaded worlds.
     *
     * @return a list of worlds
     */
    List<? extends World> getWorlds();

    /**
     * Create a duplicate of the given player.
     *
     * <p>The given player may have been provided by a different platform.</p>
     *
     * @param player the player to match
     * @return a matched player, otherwise null
     */
    @Nullable
    Player matchPlayer(Player player);

    /**
     * Create a duplicate of the given world.
     *
     * <p>The given world may have been provided by a different platform.</p>
     *
     * @param world the world to match
     * @return a matched world, otherwise null
     */
    @Nullable
    World matchWorld(World world);

    /**
     * Register the commands contained within the given command manager.
     *
     * <p>
     * This method should be ignored if the platform offers a command registration event.
     * </p>
     *
     * @param commandManager the command manager
     */
    void registerCommands(CommandManager commandManager);

    /**
     * Register game hooks.
     *
     * @deprecated Call {@link #setGameHooksEnabled(boolean)} with {@code true} instead
     */
    @Deprecated
    default void registerGameHooks() {
        setGameHooksEnabled(true);
    }

    /**
     * Set if the game hooks are enabled for this platform.
     */
    void setGameHooksEnabled(boolean enabled);

    /**
     * Get the configuration from this platform.
     *
     * @return the configuration
     */
    LocalConfiguration getConfiguration();

    /**
     * Get the version of WorldEdit that this platform provides.
     *
     * <p>This version should match WorldEdit releases because it may be
     * checked to match.</p>
     *
     * @return the version
     */
    String getVersion();

    /**
     * Get a friendly name of the platform.
     *
     * <p>The name can be anything (reasonable). An example name may be
     * "Bukkit" or "Forge".</p>
     *
     * @return the platform name
     */
    String getPlatformName();

    /**
     * Get the version of the platform, which can be anything.
     *
     * @return the platform version
     */
    String getPlatformVersion();

    /**
     * Get a map of advertised capabilities of this platform, where each key
     * in the given map is a supported capability and the respective value
     * indicates the preference for this platform for that given capability.
     *
     * @return a map of capabilities
     */
    Map<Capability, Preference> getCapabilities();

    /**
     * Get a set of {@link SideEffect}s supported by this platform.
     *
     * @return A set of supported side effects
     */
    Set<SideEffect> getSupportedSideEffects();

    /**
     * Get the number of ticks since the server started.
     * On some platforms this value may be an approximation based on the JVM run time.
     *
     * @return The number of ticks since the server started.
     */
    long getTickCount();

    //FAWE start

    /**
     * {@inheritDoc}
     *
     * @return an id
     */
    @NonAbstractForCompatibility(delegateName = "getPlatformName", delegateParams = {})
    @Override
    default String id() {
        return "legacy:" + getPlatformName().toLowerCase(Locale.ROOT).replaceAll("[^a-z_.-]", "_");
    }

    /**
     * Get the {@link RelighterFactory} that can be used to obtain
     * {@link Relighter}s.
     *
     * @return the relighter factory to be used.
     */
    @Nonnull
    RelighterFactory getRelighterFactory();

    /**
     * Get the default minimum Y value of worlds based on Minecraft version (inclusive).
     * @since 2.0.0
     */
    int versionMinY();

    /**
     * Get the default maximum Y value of worlds based on Minecraft version (inclusive).
     * @since 2.0.0
     */
    int versionMaxY();

    /**
     * Get a {@link IBatchProcessor} to be used in edit processing. Null if not required.
     * @since 2.1.0
     */
    @Nullable
    default IBatchProcessor getPlatformProcessor(boolean fastMode) {
        return null;
    }

    /**
     * Get a {@link IBatchProcessor} to be used in edit post-processing. Used for things such as tick-placed and tick fluids.
     * Null if not required.
     * @since 2.1.0
     */
    @Nullable
    default IBatchProcessor getPlatformPostProcessor(boolean fastMode) {
        return null;
    }

    /**
     * Returns an {@link PlacementStateProcessor} instance for processing placed blocks to "fix" them. Optional region to
     * prevent any changes outside of, as sometimes block neighbours will also be updated otherwise.
     *
     * @since 2.12.3
     */
    default PlacementStateProcessor getPlatformPlacementProcessor(Extent extent, BlockTypeMask mask, @Nullable Region region) {
        return null;
    }
    //FAWE end
}
