package com.aqupd.brownbear;

import net.fabricmc.api.ModInitializer;
import com.aqupd.brownbear.entities.BrownBearEntity;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class Main implements ModInitializer {

	public static final EntityType<BrownBearEntity> BROWNBEAR = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("aqupd", "brown_bear"),
			FabricEntityTypeBuilder.create(SpawnGroup.CREATURE,
					BrownBearEntity::new).dimensions(EntityDimensions.changing(1.4f, 1.4f)).build()
	);

	public static final Identifier ENTITY_BROWN_BEAR_AMBIENT = new Identifier("aqupd:brown_bear.ambient");
	public static SoundEvent BROWN_BEAR_AMBIENT = new SoundEvent(ENTITY_BROWN_BEAR_AMBIENT);
	public static final Identifier ENTITY_BROWN_BEAR_AMBIENT_BABY = new Identifier("aqupd:brown_bear.ambient_baby");
	public static SoundEvent BROWN_BEAR_AMBIENT_BABY = new SoundEvent(ENTITY_BROWN_BEAR_AMBIENT_BABY);
	public static final Identifier ENTITY_BROWN_BEAR_DEATH = new Identifier("aqupd:brown_bear.death");
	public static SoundEvent BROWN_BEAR_DEATH = new SoundEvent(ENTITY_BROWN_BEAR_DEATH);
	public static final Identifier ENTITY_BROWN_BEAR_HURT = new Identifier("aqupd:brown_bear.hurt");
	public static SoundEvent BROWN_BEAR_HURT = new SoundEvent(ENTITY_BROWN_BEAR_HURT);
	public static final Identifier ENTITY_BROWN_BEAR_STEP = new Identifier("aqupd:brown_bear.step");
	public static SoundEvent BROWN_BEAR_STEP = new SoundEvent(ENTITY_BROWN_BEAR_STEP);
	public static final Identifier ENTITY_BROWN_BEAR_WARNING = new Identifier("aqupd:brown_bear.warning");
	public static SoundEvent BROWN_BEAR_WARNING = new SoundEvent(ENTITY_BROWN_BEAR_WARNING);

	public static final SpawnEggItem BROWN_BEAR_SPAWN_EGG = new SpawnEggItem(BROWNBEAR, 8545340, 4139806, new FabricItemSettings().group(ItemGroup.MISC).fireproof().maxCount(64));

	@Override
	public void onInitialize() {
		Registry.register(Registry.SOUND_EVENT, Main.ENTITY_BROWN_BEAR_AMBIENT, BROWN_BEAR_AMBIENT);
		Registry.register(Registry.SOUND_EVENT, Main.ENTITY_BROWN_BEAR_AMBIENT_BABY, BROWN_BEAR_AMBIENT_BABY);
		Registry.register(Registry.SOUND_EVENT, Main.ENTITY_BROWN_BEAR_DEATH, BROWN_BEAR_DEATH);
		Registry.register(Registry.SOUND_EVENT, Main.ENTITY_BROWN_BEAR_HURT, BROWN_BEAR_HURT);
		Registry.register(Registry.SOUND_EVENT, Main.ENTITY_BROWN_BEAR_STEP, BROWN_BEAR_STEP);
		Registry.register(Registry.SOUND_EVENT, Main.ENTITY_BROWN_BEAR_WARNING, BROWN_BEAR_WARNING);

		Registry.register(Registry.ITEM, new Identifier("aqupd", "brown_bear_spawn_egg"), BROWN_BEAR_SPAWN_EGG);
		FabricDefaultAttributeRegistry.register(BROWNBEAR, BrownBearEntity.createBrownBearAttributes());

		BiomeModifications.addSpawn(
				selection -> selection.getBiome().getCategory() == Biome.Category.TAIGA,
				SpawnGroup.CREATURE,
				BROWNBEAR,
				4, 1, 4 // weight/min group size/max group size
		);
		System.out.println("[Brown Bear Mod] Initialized");
	}
}
