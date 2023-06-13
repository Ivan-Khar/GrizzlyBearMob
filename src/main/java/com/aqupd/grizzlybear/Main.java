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
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;

import static com.aqupd.grizzlybear.utils.AqLogger.*;

public class Main implements ModInitializer {

	int weight = AqConfig.INSTANCE.getNumberProperty("spawn.weight");
	int mingroup = AqConfig.INSTANCE.getNumberProperty("spawn.min");
	int maxgroup = AqConfig.INSTANCE.getNumberProperty("spawn.max");

	String[] biomelist = AqConfig.INSTANCE.getStringProperty("spawn.biomes").split(",");

	public static final EntityType<GrizzlyBearEntity> GRIZZLYBEAR = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			new ResourceLocation("aqupd", "grizzly_bear"),
			FabricEntityTypeBuilder.create(MobCategory.CREATURE,
					GrizzlyBearEntity::new).dimensions(EntityDimensions.scalable(1.4f, 1.4f)).build()
	);

	public static final ResourceLocation ENTITY_GRIZZLY_BEAR_AMBIENT = new ResourceLocation("aqupd:grizzly_bear.ambient");
	public static SoundEvent GRIZZLY_BEAR_AMBIENT = SoundEvent.createVariableRangeEvent(ENTITY_GRIZZLY_BEAR_AMBIENT);
	public static final ResourceLocation ENTITY_GRIZZLY_BEAR_AMBIENT_BABY = new ResourceLocation("aqupd:grizzly_bear.ambient_baby");
	public static SoundEvent GRIZZLY_BEAR_AMBIENT_BABY = SoundEvent.createVariableRangeEvent(ENTITY_GRIZZLY_BEAR_AMBIENT_BABY);
	public static final ResourceLocation ENTITY_GRIZZLY_BEAR_DEATH = new ResourceLocation("aqupd:grizzly_bear.death");
	public static SoundEvent GRIZZLY_BEAR_DEATH = SoundEvent.createVariableRangeEvent(ENTITY_GRIZZLY_BEAR_DEATH);
	public static final ResourceLocation ENTITY_GRIZZLY_BEAR_HURT = new ResourceLocation("aqupd:grizzly_bear.hurt");
	public static SoundEvent GRIZZLY_BEAR_HURT = SoundEvent.createVariableRangeEvent(ENTITY_GRIZZLY_BEAR_HURT);
	public static final ResourceLocation ENTITY_GRIZZLY_BEAR_STEP = new ResourceLocation("aqupd:grizzly_bear.step");
	public static SoundEvent GRIZZLY_BEAR_STEP = SoundEvent.createVariableRangeEvent(ENTITY_GRIZZLY_BEAR_STEP);
	public static final ResourceLocation ENTITY_GRIZZLY_BEAR_WARNING = new ResourceLocation("aqupd:grizzly_bear.warning");
	public static SoundEvent GRIZZLY_BEAR_WARNING = SoundEvent.createVariableRangeEvent(ENTITY_GRIZZLY_BEAR_WARNING);

	public static final SpawnEggItem GRIZZLY_BEAR_SPAWN_EGG = new SpawnEggItem(GRIZZLYBEAR, 8545340, 4139806, new FabricItemSettings().stacksTo(64));

	@Override
	public void onInitialize() {
		ServerWorldEvents.LOAD.register((server, world) -> AqDebug.INSTANCE.startDebug(AqConfig.INSTANCE.getBooleanProperty("debug")));

		Registry.register(BuiltInRegistries.SOUND_EVENT, ENTITY_GRIZZLY_BEAR_AMBIENT, GRIZZLY_BEAR_AMBIENT);
		Registry.register(BuiltInRegistries.SOUND_EVENT, ENTITY_GRIZZLY_BEAR_AMBIENT_BABY, GRIZZLY_BEAR_AMBIENT_BABY);
		Registry.register(BuiltInRegistries.SOUND_EVENT, ENTITY_GRIZZLY_BEAR_DEATH, GRIZZLY_BEAR_DEATH);
		Registry.register(BuiltInRegistries.SOUND_EVENT, ENTITY_GRIZZLY_BEAR_HURT, GRIZZLY_BEAR_HURT);
		Registry.register(BuiltInRegistries.SOUND_EVENT, ENTITY_GRIZZLY_BEAR_STEP, GRIZZLY_BEAR_STEP);
		Registry.register(BuiltInRegistries.SOUND_EVENT, ENTITY_GRIZZLY_BEAR_WARNING, GRIZZLY_BEAR_WARNING);

		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("aqupd", "grizzly_bear_spawn_egg"), GRIZZLY_BEAR_SPAWN_EGG);
		FabricDefaultAttributeRegistry.register(GRIZZLYBEAR, com.aqupd.grizzlybear.entities.GrizzlyBearEntity.createGrizzlyBearAttributes());

		BiomeModifications.addSpawn(
				BiomeSelectors.includeByKey(Biomes.TAIGA),
				MobCategory.CREATURE,
				GRIZZLYBEAR,
				weight,
				mingroup,
				maxgroup
		);

		SpawnPlacements.register(GRIZZLYBEAR, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> entries.accept(GRIZZLY_BEAR_SPAWN_EGG));
		logInfo("Grizzly Bears mod is loaded!");
	}
}
