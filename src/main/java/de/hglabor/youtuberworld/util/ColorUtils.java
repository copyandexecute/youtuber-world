package de.hglabor.youtuberworld.util;

import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public final class ColorUtils {
    private ColorUtils() {
    }

    public static boolean isSimilar(Color first, Color second, double minDistance) {
        return getDistance(first, second) >= minDistance;
    }

    public static double getDistance(Color first, Color second) {
        return (first.getRed() - second.getRed()) * (first.getRed() - second.getRed())
                + (first.getGreen() - second.getGreen()) * (first.getGreen() - second.getGreen())
                + (first.getBlue() - second.getBlue()) * (first.getBlue() - second.getBlue());
    }

    @Nullable
    public static Pair<Color, Integer> findSimilarColor(List<List<Pair<Color, Integer>>> colorPalette, Color color, double colorDistance) {
        Pair<Color, Integer> currentPair = null;
        var currentSimilarityyy = colorDistance;

        for (List<Pair<Color, Integer>> pairs : colorPalette) {
            for (Pair<Color, Integer> pair : pairs) {
                var distance = ColorUtils.getDistance(color, pair.getLeft());
                if (distance <= currentSimilarityyy) {
                    currentPair = pair;
                    currentSimilarityyy = distance;
                }
            }
        }
        return currentPair;
    }
}
