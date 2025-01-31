package com.TNTStudios.ttaddons;

import com.TNTStudios.ttaddons.commands.HandcuffCommand;
import net.fabricmc.api.ModInitializer;

public class Ttaddons implements ModInitializer {

    @Override
    public void onInitialize() {
        HandcuffCommand.register();
    }
}
