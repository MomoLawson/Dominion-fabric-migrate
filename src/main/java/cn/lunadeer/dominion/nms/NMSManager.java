package cn.lunadeer.dominion.nms;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.utils.XLogger;

/**
 * NMS Manager for Fabric - uses direct NMS access (no obfuscation in 26.1).
 */
public class NMSManager {
    private static NMSManager INSTANCE;
    private FakeEntityFactory fakeEntityFactory;
    private NMSPacketSender packetSender;

    private NMSManager() {}

    public static void initialize() {
        INSTANCE = new NMSManager();
        INSTANCE.loadImplementations();
    }

    public static NMSManager instance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("NMSManager has not been initialized.");
        }
        return INSTANCE;
    }

    public FakeEntityFactory getFakeEntityFactory() { return fakeEntityFactory; }
    public NMSPacketSender getPacketSender() { return packetSender; }

    private void loadImplementations() {
        fakeEntityFactory = new FabricFakeEntityFactory();
        packetSender = new FabricPacketSender();
        XLogger.info("Fabric NMS implementations loaded");
    }
}
