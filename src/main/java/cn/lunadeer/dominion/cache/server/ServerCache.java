package cn.lunadeer.dominion.cache.server;
public class ServerCache {
    private final DominionCache dominionCache = new DominionCache();
    private final MemberCache memberCache = new MemberCache();
    private final GroupCache groupCache = new GroupCache();
    public void load() { dominionCache.load(); memberCache.load(); groupCache.load(); }
    public DominionCache getDominionCache() { return dominionCache; }
    public MemberCache getMemberCache() { return memberCache; }
    public GroupCache getGroupCache() { return groupCache; }
}
