package info.x2a.soulshards.item;

import info.x2a.soulshards.api.ISoulWeapon;
import info.x2a.soulshards.core.registry.RegistrarSoulShards;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ItemVileSword extends SwordItem implements ISoulWeapon {

    public static final Tier MATERIAL_VILE = new MaterialVile();

    public ItemVileSword() {
        super(MATERIAL_VILE, 3, -2.4F, new Properties());
    }

    @Override
    public int getSoulBonus(ItemStack stack, Player player, LivingEntity killedEntity) {
        return 1;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip,
                                TooltipFlag options) {
        tooltip.add(Component.translatable("tooltip.soulshards.vile_sword"));
    }

    public static class MaterialVile implements Tier {

        private final Supplier<Ingredient> ingredient;

        public MaterialVile() {
            this.ingredient = () -> Ingredient.of(RegistrarSoulShards.CORRUPTED_INGOT.get());
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
        public @NotNull Ingredient getRepairIngredient() {
            return ingredient.get();
        }

    }
}
