package com.pg85.otg.settings.preset;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BedrockSettings {
    private final boolean ceilingBedrock;
    private final boolean flatBedrock;
    private final boolean bedrockDisabled;
}