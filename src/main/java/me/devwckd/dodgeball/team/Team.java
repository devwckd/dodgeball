package me.devwckd.dodgeball.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Team {

    RED("§cRed Team", "§c"),
    BLUE("§9Blue Team", "§9");

    private final String displayName;
    private final String color;

}
