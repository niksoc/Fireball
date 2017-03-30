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

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.logic.health.EngineDamageTypes;
import org.terasology.math.geom.Vector3f;

/**
 * Created by nikhil on 28/3/17.
 */
public class ProjectileActionComponent implements Component{
    /**
     * The direction of the projectile.
     */
    public Vector3f direction = null;

    /**
     * The distance travelled by the projectile.
     */
    public float distanceTravelled = 0;

    /**
     * The velocity of the projectile.
     */
    public int velocity = 7;

    /**
     * The max distance the projectile will fly.
     */
    public int maxDistance = 24;

    /**
     * The damage the projectile does
     */
    public int damageAmount = 3;

    /**
     * How many projectiles can be fired per second
     */
    public float projectilesPerSecond = 1.0f;

    public Prefab damageType = EngineDamageTypes.PHYSICAL.get();
}
