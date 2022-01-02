package com.aqupd.grizzlybear;

import com.aqupd.grizzlybear.entities.GrizzlyBearEntity;
import com.aqupd.grizzlybear.utils.AqConfig;
import com.aqupd.grizzlybear.utils.AqDebug;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.mixin.object.builder.SpawnRestrictionAccessor;
import net.minecraft.entity.*;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;

import java.util.Arrays;

import static com.aqupd.grizzlybear.utils.AqLogger.*;

public class Main implements ModInitializer {

	int weight = AqConfig.INSTANCE.getNumberProperty("spawn.weight");
	int mingroup = AqConfig.INSTANCE.getNumberProperty("spawn.min");
	int maxgroup = AqConfig.INSTANCE.getNumberProperty("spawn.max");

	String[] biomelist = AqConfig.INSTANCE.getStringProperty("spawn.biomes").split(",");

	public static final EntityType<GrizzlyBearEntity> GRIZZLYBEAR = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("aqupd", "grizzly_bear"),
			FabricEntityTypeBuilder.create(SpawnGroup.CREATURE,
					GrizzlyBearEntity::new).dimensions(EntityDimensions.changing(1.4f, 1.4f)).build()
	);

	public static final Identifier ENTITY_GRIZZLY_BEAR_AMBIENT = new Identifier("aqupd:grizzly_bear.ambient");
	public static SoundEvent GRIZZLY_BEAR_AMBIENT = new SoundEvent(ENTITY_GRIZZLY_BEAR_AMBIENT);
	public static final Identifier ENTITY_GRIZZLY_BEAR_AMBIENT_BABY = new Identifier("aqupd:grizzly_bear.ambient_baby");
	public static SoundEvent GRIZZLY_BEAR_AMBIENT_BABY = new SoundEvent(ENTITY_GRIZZLY_BEAR_AMBIENT_BABY);
	public static final Identifier ENTITY_GRIZZLY_BEAR_DEATH = new Identifier("aqupd:grizzly_bear.death");
	public static SoundEvent GRIZZLY_BEAR_DEATH = new SoundEvent(ENTITY_GRIZZLY_BEAR_DEATH);
	public static final Identifier ENTITY_GRIZZLY_BEAR_HURT = new Identifier("aqupd:grizzly_bear.hurt");
	public static SoundEvent GRIZZLY_BEAR_HURT = new SoundEvent(ENTITY_GRIZZLY_BEAR_HURT);
	public static final Identifier ENTITY_GRIZZLY_BEAR_STEP = new Identifier("aqupd:grizzly_bear.step");
	public static SoundEvent GRIZZLY_BEAR_STEP = new SoundEvent(ENTITY_GRIZZLY_BEAR_STEP);
	public static final Identifier ENTITY_GRIZZLY_BEAR_WARNING = new Identifier("aqupd:grizzly_bear.warning");
	public static SoundEvent GRIZZLY_BEAR_WARNING = new SoundEvent(ENTITY_GRIZZLY_BEAR_WARNING);

	public static final SpawnEggItem GRIZZLY_BEAR_SPAWN_EGG = new SpawnEggItem(GRIZZLYBEAR, 8545340, 4139806, new FabricItemSettings().group(ItemGroup.MISC).fireproof().maxCount(64));

	@Override
	public void onInitialize() {
		ServerWorldEvents.LOAD.register((server, world) -> AqDebug.INSTANCE.startDebug(AqConfig.INSTANCE.getBooleanProperty("debug")));

		Registry.register(Registry.SOUND_EVENT, Main.ENTITY_GRIZZLY_BEAR_AMBIENT, GRIZZLY_BEAR_AMBIENT);
		Registry.register(Registry.SOUND_EVENT, Main.ENTITY_GRIZZLY_BEAR_AMBIENT_BABY, GRIZZLY_BEAR_AMBIENT_BABY);
		Registry.register(Registry.SOUND_EVENT, Main.ENTITY_GRIZZLY_BEAR_DEATH, GRIZZLY_BEAR_DEATH);
		Registry.register(Registry.SOUND_EVENT, Main.ENTITY_GRIZZLY_BEAR_HURT, GRIZZLY_BEAR_HURT);
		Registry.register(Registry.SOUND_EVENT, Main.ENTITY_GRIZZLY_BEAR_STEP, GRIZZLY_BEAR_STEP);
		Registry.register(Registry.SOUND_EVENT, Main.ENTITY_GRIZZLY_BEAR_WARNING, GRIZZLY_BEAR_WARNING);

		Registry.register(Registry.ITEM, new Identifier("aqupd", "grizzly_bear_spawn_egg"), GRIZZLY_BEAR_SPAWN_EGG);
		FabricDefaultAttributeRegistry.register(GRIZZLYBEAR, com.aqupd.grizzlybear.entities.GrizzlyBearEntity.createGrizzlyBearAttributes());

		BiomeModifications.addSpawn(
				selection -> Arrays.stream(biomelist).anyMatch(x -> x.equals(selection.getBiome().getCategory().getName().toUpperCase())),
				SpawnGroup.CREATURE,
				GRIZZLYBEAR,
				weight, mingroup, maxgroup // weight/min group size/max group size
		);
		SpawnRestrictionAccessor.callRegister(GRIZZLYBEAR, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canMobSpawn);
		logInfo("Grizzly Bears mod is loaded!");
	}
}
