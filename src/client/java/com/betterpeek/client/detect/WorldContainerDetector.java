package com.betterpeek.client.detect;

import com.betterpeek.client.render.PreviewSnapshot;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Looks at whatever the player's crosshair is pointed at and, if it's an
 * Inventory-backed block entity (chest, barrel, hopper, dropper, dispenser,
 * shulker box), returns a snapshot of its contents.
 *
 * <p>Caveat: on vanilla multiplayer servers, container contents are NOT
 * synced to the client until the player opens the container. This is a
 * client-only mod, so on those servers we return an empty snapshot. The
 * mod still works fully in single-player and on servers that sync
 * block-entity NBT (Spigot/Paper with sync options, most "lite" servers).
 */
public final class WorldContainerDetector {

    /** Max distance for the raycast; matches vanilla's interaction reach. */
    private static final double REACH = 6.0D;

    public PreviewSnapshot detect(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            return null;
        }

        HitResult hit = client.crosshairTarget;
        if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK) {
            return null;
        }

        BlockPos pos = blockHit.getBlockPos();
        World world = client.world;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof Inventory inventory)) {
            return null;
        }

        // A LootTable-protected container that hasn't been opened yet shows
        // up as an Inventory but with no items — skip it to avoid lying
        // to the player about an "empty" loot chest.
        if (blockEntity instanceof LootableContainerBlockEntity lootable
            && lootable.getLootTable() != null) {
            return null;
        }

        List<ItemStack> stacks = readStacks(inventory);
        if (stacks.isEmpty()) {
            return null;
        }

        int size = stacks.size();
        int columns = Math.min(size, 9);
        int rows = (size + columns - 1) / columns;

        Text title = blockEntity instanceof net.minecraft.util.Nameable nameable
            ? nameable.getDisplayName()
            : Text.translatable(world.getBlockState(pos).getBlock().getTranslationKey());

        return new PreviewSnapshot(title, columns, rows, stacks);
    }

    private static List<ItemStack> readStacks(Inventory inventory) {
        int size = inventory.size();
        List<ItemStack> out = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            out.add(inventory.getStack(i));
        }
        return out;
    }
}
