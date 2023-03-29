package info.x2a.soulshards.item;

import info.x2a.soulshards.api.ISoulWeapon;
import info.x2a.soulshards.core.registry.RegistrarSoulShards;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.util.LazyLoadedValue;

public class ItemVileSword extends SwordItem implements ISoulWeapon {

    public static final Tier MATERIAL_VILE = new MaterialVile();

    public ItemVileSword() {
        super(MATERIAL_VILE, 3, -2.4F, new Properties());
    }

    @Override
    public int getSoulBonus(ItemStack stack, Player player, LivingEntity killedEntity) {
        return 1;
    }

    public static class MaterialVile implements Tier {

        private final LazyLoadedValue<Ingredient> ingredient;

        public MaterialVile() {
            this.ingredient = new LazyLoadedValue<>(() -> Ingredient.of(RegistrarSoulShards.CORRUPTED_INGOT.get()));
        }

        @Override
        public int getUses() {
            return Tiers.IRON.getUses();
        }

        @Override
        public float getSpeed() {
            return Tiers.IRON.getSpeed();
        }

        @Override
        public float getAttackDamageBonus() {
            return Tiers.IRON.getAttackDamageBonus();
        }

        @Override
        public int getLevel() {
            return Tiers.IRON.getLevel();
        }

        @Override
        public int getEnchantmentValue() {
            return Tiers.IRON.getEnchantmentValue();
        }

        @Override
        public Ingredient getRepairIngredient() {
            return ingredient.get();
        }

    }
}
