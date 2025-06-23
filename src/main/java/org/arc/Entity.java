package org.arc;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>Lớp đại diện cho một thực thể (entity) trong ECS, nhận diện bằng một ID duy nhất.</p>
 * <p>Entity không chứa dữ liệu, mà chỉ làm "handle" để truy xuất component thông qua World.</p>
 * <p>Entity chỉ được tạo thông qua World/EntityManager, không được khởi tạo trực tiếp bằng new Object.</p>
 *
 * <p>Không nên giữ tham chiếu lâu dài đến Entity vì ID có thể được tái sử dụng.</p>
 *
 * @author Arriety
 */
public final class Entity {

    /**
     * The entities identifier in the world.
     */
    @Getter
    private final int id;

    private final World world;

    private Entity(int id, World world) {
        this.id = id;
        this.world = world;
    }

    @Override
    public String toString() {
        return "Entity[" + id + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        var entity = (Entity) o;

        return this.id == entity.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
