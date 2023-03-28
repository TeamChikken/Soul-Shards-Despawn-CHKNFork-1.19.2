package info.x2a.soulshards.core.util;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class EnchantmentSoulStealer extends Enchantment {

    public EnchantmentSoulStealer() {
        super(Rarity.UNCOMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return (level - 1) * 11;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}
