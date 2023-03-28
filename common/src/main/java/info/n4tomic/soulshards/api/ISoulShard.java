package info.n4tomic.soulshards.api;


import net.minecraft.world.item.ItemStack;

public interface ISoulShard {

    IBinding getBinding(ItemStack stack);
}
