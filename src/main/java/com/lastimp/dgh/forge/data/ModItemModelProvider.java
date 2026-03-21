
package com.lastimp.dgh.forge.data;

import com.lastimp.dgh.common.entry.register.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (var item : ModItems.ITEMS_SET) {
            this.basicItem((Item) item.get());
        }
    }

    public ResourceLocation getResourceLocation(Item item){
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item));
    }

//    public void magicIngotModel(ResourceLocation item){
//        this.getBuilder(item.toString())
//                .parent(new ModelFile.UncheckedModelFile("item/generated"))
//                .texture("layer0",new ResourceLocation("item/iron_ingot"))
//                .override().predicate(new ResourceLocation(ExampleMod.MODID,"size"),16).model(new ModelFile.UncheckedModelFile("item/gold_ingot"))
//                .end();
//    }
}
