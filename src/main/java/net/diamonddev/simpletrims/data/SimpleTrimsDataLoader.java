package net.diamonddev.simpletrims.data;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.diamonddev.simpletrims.SimpleTrims;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import static net.diamonddev.simpletrims.data.SimpleTrimsDataLoader.MaterialKeys.*;

public class SimpleTrimsDataLoader implements SimpleSynchronousResourceReloadListener {

    public static final String ENCODED_PALETTE_CONTAIN_STRING = "simpletrims_encoded_palette";
    public static final String NOT_A_TRANSLATION_KEY_LOL = "yeahBroThisIsntATranslationKey";
    public static final String REFERABLE_KEY_REGEX_PATTERN = "^[A-Za-z0-9]+\\.[A-Za-z0-9]+\\.simpletrims\\.referable\\.material$";

    private static final String MATERIAL_PALETTE_FILEPATH = "encodable_palettes";
    private static final String MATERIAL_FILEPATH = "simple_trim_material";
    private static final String PATTERN_FILEPATH = "simple_trim_patten";

    static class MaterialKeys {
        static final String
                KEY_ENCODED_PALETTE = "encoded_palette",
                KEY_ASSET_NAME = "asset_name",
                KEY_DESC = "description",
                KEY_DESC_COLOR = "color",
                KEY_DESC_TRANSLATIONKEY = "translate",
                KEY_TRANSLATIONS = "translations",
                KEY_TRANSLATIONS_LANG = "lang",
                KEY_TRANSLATIONS_STRING = "string",
                KEY_INGREDIENT = "ingredient";
    }

    public static class MaterialBean {
        public static class LangBean {
            @SerializedName(KEY_TRANSLATIONS_LANG)
            public String langCode;

            @SerializedName(KEY_TRANSLATIONS_STRING)
            public String translation;
        }

        public static class DesciptionBean {
            @SerializedName(KEY_DESC_COLOR)
            public String matColorHexcode;

            @SerializedName(KEY_DESC_TRANSLATIONKEY)
            public String matNameTranslationKey = NOT_A_TRANSLATION_KEY_LOL;

            @SerializedName(KEY_TRANSLATIONS)
            public ArrayList<LangBean> translations;
        }

        @SerializedName(KEY_ENCODED_PALETTE)
        public boolean encodedPalette = false;

        @SerializedName(KEY_ASSET_NAME)
        public String assetName = null;

        @SerializedName(KEY_DESC)
        public DesciptionBean desc;

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
            return bean.desc.matNameTranslationKey;
        }

        public String getReferrableTranslationKey() {
            return String.format("%s.%s.simpletrims.referable.material", getNamespace(), getAssetName());
        }
        public String getDescColorCodeAsHexString() {
            return bean.desc.matColorHexcode;
        }


        @Nullable private HashMap<String, String> translationHash = null;
        public HashMap<String, String> getTranslationHashmap() {
            if (translationHash == null) {
                translationHash = new HashMap<>();
                bean.desc.translations.forEach(langbean -> {
                    translationHash.put(langbean.langCode, langbean.translation);
                });
            }
            return translationHash;
        }

        public String getTranslation(String langcode) {
            return getTranslationHashmap().containsKey(langcode) ? getTranslationHashmap().get(langcode) : getTranslationHashmap().containsKey(Language.DEFAULT_LANGUAGE) ? getTranslationHashmap().get(Language.DEFAULT_LANGUAGE) : getReferrableTranslationKey();
        }

        public String getNamespace() {
            return filepath.getNamespace();
        }
        public Identifier getPathToPalette() {
            if (bean.encodedPalette) {
                return new Identifier(filepath.getNamespace(), ENCODED_PALETTE_CONTAIN_STRING + "/" + getNamespace() + "/" + getAssetName());
            } else return new Identifier(filepath.getNamespace(), "trims/color_palettes/" + getAssetName());
        }
    }

    public static ArrayList<MaterialBeanWrapper> SIMPLE_TRIM_MATERIALS = new ArrayList<>();
    public static ArrayList<PaletteEncoderDecoder.EncodedPalette> ENCODED_PALETTES = new ArrayList<>();

    @Override
    public Identifier getFabricId() {
        return SimpleTrims.id("data");
    }

    @Override
    public void reload(ResourceManager manager) { // could put template loading here too
        Gson gson = new Gson();

        // Clear Cache
        SIMPLE_TRIM_MATERIALS.clear();
        ENCODED_PALETTES.clear();

        // Get Streams and consume beans

        // Materials
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

        // Encoded Palettes
        for (Identifier id : manager.findResources(MATERIAL_PALETTE_FILEPATH, (id) -> id.getPath().endsWith(".png")).keySet()) {
            if (manager.getResource(id).isPresent()) {
                try (InputStream stream = manager.getResource(id).get().getInputStream()) {

                    ENCODED_PALETTES.add(PaletteEncoderDecoder.encode(id, stream));

                } catch (Exception e) {
                    SimpleTrims.LOGGER.error("Error occurred reading palette image at id [{}] - {}", id.toString(), e);
                }
            }
        }
    }

    public static String isolateFileName(Identifier resId) {
        String[] split = resId.getPath().split("/");
        return split[split.length-1].split("\\.")[0]; // isolates the filename ("namespace:path/to/file.json" -> "file")
    }

    public static Identifier convertEncodedPaletteLocToPalettedPermutationIdenfier(Identifier loc) {
        return new Identifier(loc.getNamespace(), ENCODED_PALETTE_CONTAIN_STRING + "/" + loc.getNamespace() + "/" + isolateFileName(loc));
    }
}
