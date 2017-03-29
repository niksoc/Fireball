/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.fireball;

import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.health.DoDamageEvent;
import org.terasology.logic.health.HealthComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.physics.CollisionGroup;
import org.terasology.physics.HitResult;
import org.terasology.physics.Physics;
import org.terasology.physics.StandardCollisionGroup;
import org.terasology.registry.In;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;

/**
 * Created by nikhil on 28/3/17.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class ProjectileAuthoritySystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    @In
    private WorldProvider worldProvider;

    @In
    private Physics physicsRenderer;

    @In
    private BlockEntityRegistry blockEntityRegistry;

    @In
    private EntityManager entityManager;

    @In
    private Time time;

    private CollisionGroup filter = StandardCollisionGroup.ALL;
    private float lastTime;

    @Override
    public void initialise() {
    }

    @Override
    public void shutdown() {
    }

    @ReceiveEvent
    public void onActivate(ActivateEvent event, EntityRef entity, ProjectileActionComponent projectileActionComponent) {

        if (time.getGameTime() > lastTime + 1.0f / projectileActionComponent.projectilesPerSecond) {
            //projectileActionComponent.position  = new Vector3f(event.getOrigin());
            projectileActionComponent.direction = new Vector3f(event.getDirection());

            entity.addOrSaveComponent(new LocationComponent(event.getOrigin()));

            entity.saveComponent(projectileActionComponent);
            Vector3f a = entity.getComponent(ProjectileActionComponent.class).direction;
            lastTime = time.getGameTime();
        }
    }

    /*
     * Updates the state of fired projectiles
     */
    @Override
    public void update(float delta) {
        for (EntityRef entity : entityManager.getEntitiesWith(ProjectileActionComponent.class)) {
            ProjectileActionComponent projectile = entity.getComponent(ProjectileActionComponent.class);
            if(projectile.direction == null) // not been fired
                continue;
            if(projectile.distanceTravelled >= projectile.maxDistance) {
                entity.destroy();
                continue;
            }
            HealthComponent health = entity.getComponent(HealthComponent.class);
            Vector3f position = entity.getComponent(LocationComponent.class).getWorldPosition();

            float displacement = delta * projectile.velocity;
            HitResult result;
            result = physicsRenderer.rayTrace(position, projectile.direction, displacement, filter);
            if(result.isHit()) {
                EntityRef blockEntity = result.getEntity();
                blockEntity.send(new DoDamageEvent(projectile.damageAmount, projectile.damageType));
                entity.destroy();
                continue;
            }

            Vector3f newPosition = position.add(projectile.direction.mul(displacement));

            projectile.distanceTravelled += displacement;
            entity.addOrSaveComponent(new LocationComponent(newPosition));
        }

    }
}
