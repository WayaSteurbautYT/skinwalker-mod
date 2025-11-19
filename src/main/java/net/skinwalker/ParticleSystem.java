package net.skinwalker;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

/**
 * Particle System - Visual effects for transformations and events
 */
public class ParticleSystem {
    
    public static void init() {
        // Particle system is ready to use
    }
    
    /**
     * Spawn corruption particles around a player
     */
    public static void spawnCorruptionParticles(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        Vec3d pos = player.getPos();
        
        // Spawn purple/black particles
        for (int i = 0; i < 20; i++) {
            double x = pos.x + (Math.random() - 0.5) * 2;
            double y = pos.y + Math.random() * 2;
            double z = pos.z + (Math.random() - 0.5) * 2;
            
            world.spawnParticles(
                ParticleTypes.SMOKE,
                x, y, z,
                1, 0, 0, 0, 0.1
            );
            
            world.spawnParticles(
                ParticleTypes.LARGE_SMOKE,
                x, y, z,
                1, 0, 0, 0, 0.05
            );
        }
    }
    
    /**
     * Spawn transformation particles
     */
    public static void spawnTransformationParticles(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        Vec3d pos = player.getPos();
        
        // Portal-like effect
        for (int i = 0; i < 30; i++) {
            double angle = (i / 30.0) * Math.PI * 2;
            double radius = 1.5;
            double x = pos.x + Math.cos(angle) * radius;
            double y = pos.y + Math.random() * 2;
            double z = pos.z + Math.sin(angle) * radius;
            
            world.spawnParticles(
                ParticleTypes.PORTAL,
                x, y, z,
                1, 0, 0, 0, 0.1
            );
        }
    }
    
    /**
     * Spawn victory particles
     */
    public static void spawnVictoryParticles(ServerPlayerEntity player) {
        if (player == null) return;
        ServerWorld world = player.getServerWorld();
        Vec3d pos = player.getPos();
        
        for (int i = 0; i < 50; i++) {
            double x = pos.x + (Math.random() - 0.5) * 3;
            double y = pos.y + Math.random() * 3;
            double z = pos.z + (Math.random() - 0.5) * 3;
            
            world.spawnParticles(
                ParticleTypes.FIREWORK,
                x, y, z,
                1, 0, 0, 0, 0.1
            );
        }
    }
    
    /**
     * Spawn aura particles around skinwalker
     */
    public static void spawnSkinwalkerAura(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        Vec3d pos = player.getPos();
        
        // Subtle dark particles
        if (Math.random() < 0.1) { // Only spawn occasionally
            double x = pos.x + (Math.random() - 0.5) * 1.5;
            double y = pos.y + Math.random() * 2;
            double z = pos.z + (Math.random() - 0.5) * 1.5;
            
            world.spawnParticles(
                ParticleTypes.SMOKE,
                x, y, z,
                1, 0, 0, 0, 0.05
            );
        }
    }
}
