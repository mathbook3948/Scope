package dev.mathbook3948.scope.domain.guild;

public enum AuthorType {
    USER, BOT, SYSTEM, UNKNOWN;

    /**
     * 우선순위: SYSTEM &gt; BOT &gt; USER.
     */
    public static AuthorType from(boolean isBot, boolean isSystem) {
        if (isSystem) return SYSTEM;
        if (isBot) return BOT;
        return USER;
    }
}
