package com.aqupd.grizzlybear;

import com.aqupd.grizzlybear.entities.GrizzlyBearEntity;
import com.aqupd.grizzlybear.utils.AqConfig;
import com.aqupd.grizzlybear.utils.AqDebug;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.entity.mob.MobEntity;

import net.minecraft.world.biome.BiomeKeys;

import static com.aqupd.grizzlybear.utils.AqLogger.*;

public class Main implements ModInitializer {

	int weight = AqConfig.INSTANCE.getNumberProperty("spawn.weight");
	int mingroup = AqConfig.INSTANCE.getNumberProperty("spawn.min");
	int maxgroup = AqConfig.INSTANCE.getNumberProperty("spawn.max");

	String[] biomelist = AqConfig.INSTANCE.getStringProperty("spawn.biomes").split(",");

	public static final EntityType<GrizzlyBearEntity> GRIZZLYBEAR = Registry.register(
			Registries.ENTITY_TYPE,
			new Identifier("aqupd", "grizzly_bear"),
			FabricEntityTypeBuilder.create(SpawnGroup.CREATURE,
					GrizzlyBearEntity::new).dimensions(EntityDimensions.changing(1.4f, 1.4f)).build()
	);

	public static final Identifier ENTITY_GRIZZLY_BEAR_AMBIENT = new Identifier("aqupd:grizzly_bear.ambient");
	public static SoundEvent GRIZZLY_BEAR_AMBIENT = SoundEvent.of(ENTITY_GRIZZLY_BEAR_AMBIENT);
	public static final Identifier ENTITY_GRIZZLY_BEAR_AMBIENT_BABY = new Identifier("aqupd:grizzly_bear.ambient_baby");
	public static SoundEvent GRIZZLY_BEAR_AMBIENT_BABY = SoundEvent.of(ENTITY_GRIZZLY_BEAR_AMBIENT_BABY);
	public static final Identifier ENTITY_GRIZZLY_BEAR_DEATH = new Identifier("aqupd:grizzly_bear.death");
	public static SoundEvent GRIZZLY_BEAR_DEATH = SoundEvent.of(ENTITY_GRIZZLY_BEAR_DEATH);
	public static final Identifier ENTITY_GRIZZLY_BEAR_HURT = new Identifier("aqupd:grizzly_bear.hurt");
	public static SoundEvent GRIZZLY_BEAR_HURT = SoundEvent.of(ENTITY_GRIZZLY_BEAR_HURT);
	public static final Identifier ENTITY_GRIZZLY_BEAR_STEP = new Identifier("aqupd:grizzly_bear.step");
	public static SoundEvent GRIZZLY_BEAR_STEP = SoundEvent.of(ENTITY_GRIZZLY_BEAR_STEP);
	public static final Identifier ENTITY_GRIZZLY_BEAR_WARNING = new Identifier("aqupd:grizzly_bear.warning");
	public static SoundEvent GRIZZLY_BEAR_WARNING = SoundEvent.of(ENTITY_GRIZZLY_BEAR_WARNING);

	public static final SpawnEggItem GRIZZLY_BEAR_SPAWN_EGG = new SpawnEggItem(GRIZZLYBEAR, 8545340, 4139806, new FabricItemSettings().maxCount(64));

	@Override
	public void onInitialize() {
		ServerWorldEvents.LOAD.register((server, world) -> AqDebug.INSTANCE.startDebug(AqConfig.INSTANCE.getBooleanProperty("debug")));

		Registry.register(Registries.SOUND_EVENT, ENTITY_GRIZZLY_BEAR_AMBIENT, GRIZZLY_BEAR_AMBIENT);
		Registry.register(Registries.SOUND_EVENT, ENTITY_GRIZZLY_BEAR_AMBIENT_BABY, GRIZZLY_BEAR_AMBIENT_BABY);
		Registry.register(Registries.SOUND_EVENT, ENTITY_GRIZZLY_BEAR_DEATH, GRIZZLY_BEAR_DEATH);
		Registry.register(Registries.SOUND_EVENT, ENTITY_GRIZZLY_BEAR_HURT, GRIZZLY_BEAR_HURT);
		Registry.register(Registries.SOUND_EVENT, ENTITY_GRIZZLY_BEAR_STEP, GRIZZLY_BEAR_STEP);
		Registry.register(Registries.SOUND_EVENT, ENTITY_GRIZZLY_BEAR_WARNING, GRIZZLY_BEAR_WARNING);

		Registry.register(Registries.ITEM, new Identifier("aqupd", "grizzly_bear_spawn_egg"), GRIZZLY_BEAR_SPAWN_EGG);
		FabricDefaultAttributeRegistry.register(GRIZZLYBEAR, com.aqupd.grizzlybear.entities.GrizzlyBearEntity.createGrizzlyBearAttributes());

		BiomeModifications.addSpawn(
				BiomeSelectors.includeByKey(BiomeKeys.TAIGA),
				SpawnGroup.CREATURE,
				GRIZZLYBEAR,
				weight,
				mingroup,
				maxgroup
		);

		SpawnRestriction.register(GRIZZLYBEAR, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canMobSpawn);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> entries.add(GRIZZLY_BEAR_SPAWN_EGG));
		logInfo("Grizzly Bears mod is loaded!");
	}
}
