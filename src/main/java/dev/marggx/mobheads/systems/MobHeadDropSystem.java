package dev.marggx.mobheads.systems;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.marggx.mobheads.MobHeadConfig;
import dev.marggx.mobheads.MobHeadsPlugin;
import dev.marggx.mobheads.ValidHeads;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import org.joml.Vector3d;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MobHeadDropSystem extends DeathSystems.OnDeathSystem {

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(
            NPCEntity.getComponentType(),
            TransformComponent.getComponentType(),
            HeadRotation.getComponentType(),
            Query.not(Player.getComponentType())
        );
    }

    @Override
    public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        MobHeadConfig config = MobHeadsPlugin.get().getConfig().get();

        if (config.getDropChance() <= 0) return;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (random.nextDouble() * 100.0 >= config.getDropChance()) return;

        NPCEntity npcComponent = store.getComponent(ref, NPCEntity.getComponentType());
        if (npcComponent == null) return;

        String roleName = npcComponent.getRoleName();
        if (roleName == null || roleName.isEmpty()) return;

        String headItemId = "Head_" + roleName;
        if (!ValidHeads.HEADS.contains(headItemId)) return;
        if (Arrays.asList(config.getExcludedHeads()).contains(headItemId)) return;

        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (transformComponent == null) return;

        HeadRotation headRotationComponent = store.getComponent(ref, HeadRotation.getComponentType());
        if (headRotationComponent == null) return;

        Vector3d position = transformComponent.getPosition();
        Vector3d dropPosition = new Vector3d(position).add(0, 1, 0);
        Rotation3f headRotation = headRotationComponent.getRotation();

        ItemStack headStack = new ItemStack(headItemId, 1);
        Holder<EntityStore>[] drops = ItemComponent.generateItemDrops(store, List.of(headStack), dropPosition, new Rotation3f(headRotation));
        commandBuffer.addEntities(drops, AddReason.SPAWN);
    }
}
