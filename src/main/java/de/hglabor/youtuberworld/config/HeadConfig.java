package de.hglabor.youtuberworld.config;

import de.hglabor.youtuberworld.util.ColorUtils;
import net.minecraft.block.*;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.EmptyBlockView;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

public final class HeadConfig {
    private static final Logger LOGGER = LogManager.getLogger("youtuber-world");
    private final static double COLOR_DISTANCE = 1600; //wert wie viel dings die farben sich ähneln
    private static final File HEAD_FOLDER = new File("mods/playerheads");
    private static final Map<String, List<List<Pair<Color, Integer>>>> HEADS = new HashMap<>();
    private static final List<Block> ALLOWED_BLOCKS = new ArrayList<>();

    private HeadConfig() {
    }

    public static void onInitialize() {
        if (!HEAD_FOLDER.exists()) {
            HEAD_FOLDER.mkdirs();
        }
        try {
            loadPngs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //#java so geil
        ALLOWED_BLOCKS.addAll(Registry.BLOCK
                .stream()
                .filter(block -> Block.isShapeFullCube(block.getDefaultState().getCollisionShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN)))
                .toList());
    }

    public static void loadPngs() throws Exception {
        File[] files = HEAD_FOLDER.listFiles();
        if (files == null) {
            throw new Exception("Keine Bilder?!");
        }

        for (File file : files) {
            if (!file.getName().endsWith(".png")) continue;

            LOGGER.info("Lade Bild: " + file.getName());

            var picture = ImageIO.read(file);

            if (picture.getWidth() != 16 && picture.getHeight() != 16) { //TODO force picture to be 16x16 or resize
                throw new Exception(file.getName() + " muss eine Größe von 16x16 haben");
            }

            HEADS.put(FilenameUtils.getBaseName(file.getName()), loopPicture(picture));
        }
    }

    public static List<List<Pair<Color, Integer>>> getRandomHead() {
        List<List<List<Pair<Color, Integer>>>> values = HEADS.values().stream().toList();
        return values.get(new Random().nextInt(values.size()));
    }

    public static Set<Integer> getKeys(List<List<Pair<Color, Integer>>> head) {
        var keys = new HashSet<Integer>();
        for (List<Pair<Color, Integer>> pairs : head) {
            for (Pair<Color, Integer> pair : pairs) {
                keys.add(pair.getRight());
            }
        }
        return keys;
    }

    public static List<Block> getBlocks(List<List<Pair<Color, Integer>>> head) {
        Set<Integer> keys = getKeys(head);
        var blocks = new HashSet<Block>();
        while (blocks.size() != keys.size()) {
            blocks.add(ALLOWED_BLOCKS.get(new Random().nextInt(ALLOWED_BLOCKS.size())));
        }
        return blocks.stream().toList();
    }

    private static List<List<Pair<Color, Integer>>> loopPicture(BufferedImage picture) {
        List<List<Pair<Color, Integer>>> map = new ArrayList<>();

        var index = 0;

        for (int x = 0; x < picture.getWidth(); x++) {
            map.add(new ArrayList<>());
            for (int z = 0; z < picture.getHeight(); z++) {
                List<Pair<Color, Integer>> row = map.get(x);
                Color color = new Color(picture.getRGB(x, z));
                Pair<Color, Integer> similarColor = ColorUtils.findSimilarColor(map, color, COLOR_DISTANCE);
                if (similarColor == null) {
                    row.add(new Pair<>(color, index));
                    index++;
                } else {
                    row.add(new Pair<>(color, similarColor.getRight()));
                }
            }
        }

        return map;
    }
}
