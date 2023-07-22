package net.diamonddev.simpletrims;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimMaterials;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static net.diamonddev.simpletrims.SimpleTrimDefinitionLoader.MaterialKeys.*;

public class SimpleTrimDefinitionLoader implements SimpleSynchronousResourceReloadListener {

    private static final String MATERIAL_FILEPATH = "simple_trim_material";
    private static final String PATTERN_FILEPATH = "simple_trim_patten";

    static class MaterialKeys {
        static final String
                KEY_ASSET_NAME = "asset_name",
                KEY_DESC = "description",
                KEY_DESC_COLOR = "color",
                KEY_DESC_TRANSLATIONKEY = "translate",
                KEY_INGREDIENT = "ingredient";
    }

    public static class MaterialBean {
        public static class DesciptionBean {
            @SerializedName(KEY_DESC_COLOR)
            public String matColorHexcode;

            @SerializedName(KEY_DESC_TRANSLATIONKEY)
            public String matNametranslationKey;
        }

        @SerializedName(KEY_ASSET_NAME)
        public String assetName = null;

        @SerializedName(KEY_DESC)
        public DesciptionBean desciption;

        @SerializedName(KEY_INGREDIENT)
        public String ingredient;

    }
    public static class MaterialBeanWrapper {
        private final MaterialBean bean;
        private final Identifier filepath;

        public MaterialBeanWrapper(MaterialBean bean, Identifier filepath) {
            this.bean = bean;
            this.filepath = filepath;
        }

        @Nullable private Item ingredient = null;
        public Item getIngredientAsItem() {
            if (ingredient == null) {
                ingredient = Registries.ITEM.get(new Identifier(bean.ingredient));
            }
            return ingredient;
        }
        public String getIngredientAsId() {
            return bean.ingredient;
        }
        public String getAssetName() {
            if (bean.assetName != null) {
                return bean.assetName;
            } else return isolateFileName(filepath);
        }
        public String getDescTranslationKey() {
            return bean.desciption.matNametranslationKey;
        }
        public int getDescColorCodeFromHex() {
            return HexFormat.fromHexDigits(bean.desciption.matColorHexcode, 1, bean.desciption.matColorHexcode.length()); // exclude the hash
        }
        public String getDescColorCodeAsHexString() {
            return bean.desciption.matColorHexcode;
        }
        public String getNamespace() {
            return filepath.getNamespace();
        }
        public Identifier getPathToPalette() {
            return new Identifier(filepath.getNamespace(), "trims/color_palettes/" + getAssetName());
        }
    }

    public static ArrayList<MaterialBeanWrapper> SIMPLE_TRIM_MATERIALS = new ArrayList<>();

    @Override
    public Identifier getFabricId() {
        return SimpleTrims.id("trim_material");
    }

    @Override
    public void reload(ResourceManager manager) { // could put template loading here too
        Gson gson = new Gson();

        // Clear Cache
        SIMPLE_TRIM_MATERIALS.clear();

        // Get Stream and consume beans
        for (Identifier id : manager.findResources(MATERIAL_FILEPATH, (id) -> id.getPath().endsWith(".json")).keySet()) {
            if (manager.getResource(id).isPresent()) {
                try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                    // Eat some beans
                    InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8); // open stream, utf8
                    MaterialBeanWrapper beanWrapper = new MaterialBeanWrapper(gson.fromJson(reader, MaterialBean.class), id);
                    SIMPLE_TRIM_MATERIALS.add(beanWrapper);
                } catch (Exception e) {
                    SimpleTrims.LOGGER.error("Error occurred reading trim at id [{}] - {}", id.toString(), e);
                }
            }
        }
    }


    public static void loopSimpleMaterials(Consumer<MaterialBeanWrapper> consumer) {
        for (MaterialBeanWrapper bean : SimpleTrimDefinitionLoader.SIMPLE_TRIM_MATERIALS) consumer.accept(bean);
    }

    public static void forEachMaterialIngredient(Consumer<Item> consumer) {
        for (MaterialBeanWrapper bean : SimpleTrimDefinitionLoader.SIMPLE_TRIM_MATERIALS) {
            consumer.accept(bean.getIngredientAsItem());
        }
    }

    public static String isolateFileName(Identifier resId) {
        String[] split = resId.getPath().split("/");
        return split[split.length-1].split("\\.")[0]; // isolates the filename ("namespace:path/to/file.json" -> "file")
    }

    public static Optional<Identifier> getTrimMatId(ItemStack stack) {
        if (stack.isIn(ItemTags.TRIMMABLE_ARMOR)) {
            NbtCompound nbtCompound = stack.getOrCreateSubNbt("Trim");
            if (nbtCompound.contains("material")) {
                return Optional.of(new Identifier(nbtCompound.getString("material")));
            }
        } return Optional.empty();
    }
}
