package org.arc.manager;

import org.arc.BaseSystem;
import org.arc.Entity;
import org.arc.annotations.SkipWire;
import org.arc.utils.Bag;
import org.arc.utils.BitVector;
import org.arc.utils.IntDeque;

/**
 * @author Arriety
 */
@SkipWire
public class EntityManager extends BaseSystem {

    private int nextId;
    private final Bag<Entity> entities;
    private final BitVector recycled = new BitVector();
    private final IntDeque limbo = new IntDeque();

    public EntityManager(int initialContainerSize) {
        entities = new Bag<>(initialContainerSize);
    }

    protected Entity createEntityInstance() {
        return obtain();
    }

    private Entity obtain() {
        if (limbo.isEmpty()) {
            return createEntity(nextId++);
        } else {
            int id = limbo.popFirst();
            recycled.unsafeClear(id);
            return entities.get(id);
        }
    }

    /**
     * Tạo một thực thể (Entity) mà không đăng ký nó vào thế giới (world).
     *
     * @param id ID sẽ được gán cho thực thể
     */
    private Entity createEntity(int id) {
        Entity e = new Entity(id, world);
        if (e.getId() >= entities.getCapacity()) {
            growEntityStores(); // Mở rộng bộ lưu trữ thực thể nếu vượt quá dung lượng
        }

        // Không được dùng set không an toàn, vì cần theo dõi ID cao nhất
        // để tăng tốc vòng lặp khi đồng bộ các đăng ký mới
        // trong ComponentManager#synchronize
        entities.set(e.getId(), e);

        return e; // Trả về entity vừa tạo
    }

    private void growEntityStores() {
        int newSize = 2 * entities.getCapacity();
        entities.ensureCapacity(newSize);
        ComponentManager cm = world.getComponentManager();
        cm.ensureCapacity(newSize);

        for (int i = 0, s = entityBitVectors.size(); s > i; i++) {
            entityBitVectors.get(i).ensureCapacity(newSize);
        }
    }

}
