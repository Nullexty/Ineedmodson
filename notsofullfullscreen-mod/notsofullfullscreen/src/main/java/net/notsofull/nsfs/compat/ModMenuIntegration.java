package net.notsofull.nsfs.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.notsofull.nsfs.gui.NsfsConfigScreen;

/**
 * This class is only ever loaded by Fabric Loader if the "modmenu" entrypoint is
 * actually queried, which only happens if the Mod Menu mod is installed. It is safe
 * to reference Mod Menu's API classes here even though Mod Menu is not a hard
 * dependency of this mod.
 */
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return NsfsConfigScreen::new;
    }
}
