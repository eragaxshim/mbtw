package mbtw.mbtw.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/**
 * Based on Fabric API's ShapelessMatch in the implementation of their Recipe API v1.
 * This match is count-aware and compatible with Fabric's custom ingredients.
 * <a href="https://github.com/FabricMC/fabric/blob/5176f73dbbaa86a759e6525374682a1b2aa3cba6/fabric-recipe-api-v1/src/main/java/net/fabricmc/fabric/impl/recipe/ingredient/ShapelessMatch.java">GitHub source</a>
 */
public class ShapelessCountMatch {
    private final int[] match;
    private final BitSet bitSet;

    private ShapelessCountMatch(int size) {
        match = new int[size];
        bitSet = new BitSet(size * (size+1));
    }

    private boolean augment(int l) {
        // Below code directly from Fabric API, which is licensed under the Apache 2.0 License
        if (bitSet.get(l)) return false;
        bitSet.set(l);

        for (int r = 0; r < match.length; ++r) {
            if (bitSet.get(match.length + l * match.length + r)) {
                if (match[r] == -1 || augment(match[r])) {
                    match[r] = l;
                    return true;
                }
            }
        }

        return false;
    }

    public static List<ItemStack> combineStacks(List<ItemStack> stacks, boolean containsEmpty) {
        return stacks.stream().collect(ArrayList::new, (stackList, stack) -> {
            if (containsEmpty && stack.isEmpty()) return;

            for (ItemStack existingStack : stackList) {
                if (ItemStack.canCombine(existingStack, stack)) {
                    existingStack.increment(stack.getCount());
                    return;
                }
            }
            stackList.add(stack.copy());
        }, ArrayList::addAll);
    }

    public static int[] computeMatch(List<ItemStack> combinedStacks, List<Ingredient> ingredients) {
        // Below code directly from Fabric API, which is licensed under the Apache 2.0 License
        ShapelessCountMatch m = new ShapelessCountMatch(ingredients.size());

        // Build stack -> ingredient bipartite graph
        for (int i = 0; i < combinedStacks.size(); ++i) {
            ItemStack stack = combinedStacks.get(i);

            for (int j = 0; j < ingredients.size(); ++j) {
                if (ingredients.get(j).test(stack)) {
                    m.bitSet.set((i + 1) * m.match.length + j);
                }
            }
        }

        if (combinedStacks.size() != ingredients.size()) {
            return null;
        }

        // Init matches to -1 (no match)
        Arrays.fill(m.match, -1);

        // Try to find an augmenting path for each stack
        for (int i = 0; i < ingredients.size(); ++i) {
            if (!m.augment(i)) {
                return null;
            }

            m.bitSet.set(0, m.match.length, false);
        }

        return m.match;
    }

    public static boolean isMatch(List<ItemStack> stacks, List<Ingredient> ingredients) {
        // We combine all stacks
        List<ItemStack> combinedStacks = combineStacks(stacks, false);

        int[] match = computeMatch(combinedStacks, ingredients);
        // We know ingredients is never zero length
        return match != null;
    }
}
