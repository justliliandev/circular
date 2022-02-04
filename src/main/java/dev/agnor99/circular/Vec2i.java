package dev.agnor99.circular;

import net.minecraft.world.phys.Vec2;

public record Vec2i(int x, int y) {

    public Vec2i add(Vec2i other) {
        return this.add(other.x, other.y);
    }
    public Vec2i add(int x, int y) {
        return new Vec2i(this.x + x, this.y + y);
    }

    public Vec2 toFloat() {
        return new Vec2(x, y);
    }
}
