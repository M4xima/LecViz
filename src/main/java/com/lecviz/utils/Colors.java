package com.lecviz.utils;

import javafx.scene.paint.Color;

/**
 * 3Blue1Brown inspired color palette.
 * Dark background with vibrant accent colors for math/CS animations.
 */
public final class Colors {

    // Background
    public static final Color BACKGROUND = Color.web("#1C1C2E");
    public static final Color DARK_BG    = Color.web("#0F0F1A");

    // Primary accent colors (3B1B style)
    public static final Color BLUE       = Color.web("#58C4DD");
    public static final Color LIGHT_BLUE = Color.web("#9CDCEB");
    public static final Color TEAL       = Color.web("#5CD0B3");
    public static final Color GREEN      = Color.web("#83C167");
    public static final Color YELLOW     = Color.web("#FFFF00");
    public static final Color GOLD       = Color.web("#F4D345");
    public static final Color RED        = Color.web("#FC6255");
    public static final Color MAROON     = Color.web("#C55F73");
    public static final Color PINK       = Color.web("#E48D9C");
    public static final Color PURPLE     = Color.web("#9A72AC");
    public static final Color ORANGE     = Color.web("#FF862F");

    // Text colors
    public static final Color WHITE      = Color.web("#EEEEEE");
    public static final Color LIGHT_GRAY = Color.web("#BBBBBB");
    public static final Color GRAY       = Color.web("#888888");
    public static final Color DARK_GRAY  = Color.web("#444444");

    // Code syntax highlighting
    public static final Color CODE_BG       = Color.web("#282A36");
    public static final Color CODE_KEYWORD  = Color.web("#FF79C6");
    public static final Color CODE_STRING   = Color.web("#F1FA8C");
    public static final Color CODE_COMMENT  = Color.web("#6272A4");
    public static final Color CODE_TYPE     = Color.web("#8BE9FD");
    public static final Color CODE_NUMBER   = Color.web("#BD93F9");
    public static final Color CODE_FUNCTION = Color.web("#50FA7B");
    public static final Color CODE_DEFAULT  = Color.web("#F8F8F2");

    private Colors() {}

    public static Color withAlpha(Color c, double alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    public static Color interpolate(Color from, Color to, double t) {
        t = Math.max(0, Math.min(1, t));
        return new Color(
            from.getRed()   + (to.getRed()   - from.getRed())   * t,
            from.getGreen() + (to.getGreen() - from.getGreen()) * t,
            from.getBlue()  + (to.getBlue()  - from.getBlue())  * t,
            from.getOpacity() + (to.getOpacity() - from.getOpacity()) * t
        );
    }
}
