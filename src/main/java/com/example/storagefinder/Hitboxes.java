package com.example.storagefinder;

/** Hitboxes: powiększa hitbox jednostek przy celowaniu (patrz mixin/EntityMixin). */
public final class Hitboxes {
    private Hitboxes() {}

    public static float expand() {
        if (!StorageFinderConfig.xb("m.hitboxes", false) || !Krux.licensed()) return 0f;
        return (float) StorageFinderConfig.xd("hb.expand", 0.0);
    }
}
