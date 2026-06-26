package cn.lunadeer.dominion.utils;

/**
 * Version manager for Fabric - simplified since we target a single version.
 */
public class XVersionManager {
    public enum ImplementationVersion {
        v1_20_1, v1_21, v1_21_4, v1_21_6, v1_21_8, v1_21_9, v26;

        public int compareWith(ImplementationVersion other) {
            return this.ordinal() - other.ordinal();
        }

        public ImplementationVersion getPrevious() {
            int idx = ordinal() - 1;
            return idx >= 0 ? values()[idx] : null;
        }
    }

    public static ImplementationVersion VERSION = ImplementationVersion.v26;
}
